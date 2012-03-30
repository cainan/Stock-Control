package br.csoliveira.stockcontrol.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.adapter.CategoryListAdapter;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.database.DatabaseDelegate;
import br.csoliveira.stockcontrol.model.database.DatabaseInterface;
import br.csoliveira.stockcontrol.util.Constants;
import br.csoliveira.stockcontrol.util.Utils;

public class CategoryActivity extends Activity implements DatabaseInterface {

    /** Dialog id's */
    private static final int DIALOG_ADD_CATEGORY = 10;
    private static final int DIALOG_OPTIONS_CATEGORY = 11;
    private static final int DIALOG_EDIT_CATEGORY = 12;
    private static final int DIALOG_EMPTY_CATEGORY = 13;
    private static final int DIALOG_ERROR_CATEGORY = 14;

    /** Hold the add category button */
    private Button mAddBtn;

    /** Database delegate reference */
    private DatabaseDelegate mDatabaseDelegate;

    /** Hold category list adapter */
    private CategoryListAdapter mCategoryAdapter;

    /** Hold the category list */
    private ListView mCategoryList;

    /** Hold the empty message */
    private TextView mEmptyText;

    /** Hold the last selected category item */
    private Category mSelectedCategory;

    /** Hold the order by spinner */
    private Spinner mSpinner;

    /** Hold the order by Name */
    private String mOrderBy;

    /**
     * First method called when activity starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        mDatabaseDelegate = DatabaseDelegate.getInstance(getApplicationContext());
        mCategoryAdapter = new CategoryListAdapter(this);

        initView();

//        listCategory();

        updateView();
    }

    /**
     * Initialize the view
     */
    private void initView() {
        mAddBtn = (Button) findViewById(R.id.add_category_btn);
        if (mAddBtn != null) {
            mAddBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_ADD_CATEGORY);
                }
            });
        }

        mCategoryList = (ListView) findViewById(R.id.category_list);
        if (mCategoryList != null) {
            mCategoryList.setAdapter(mCategoryAdapter);
            mCategoryList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    mSelectedCategory = (Category) mCategoryAdapter.getItem(position);
                    if (mSelectedCategory != null) {
                        showDialog(DIALOG_OPTIONS_CATEGORY);
                    }
                }

            });
        }

        mSpinner = (Spinner) findViewById(R.id.order_by_spinner);
        if (mSpinner != null) {
            mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    Utils.doLog("selected: " + mSpinner.getItemAtPosition(position).toString());
                    mOrderBy = mSpinner.getItemAtPosition(position).toString();
                    listCategory(mOrderBy);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // Do nothing
                }

            });
        }

        mEmptyText = (TextView) findViewById(R.id.empty_list);
    }

    /**
     * Update the view
     */
    private void updateView() {
        boolean isCategoryEmpty = true;
        if (mCategoryAdapter.getCount() > 0) {
            isCategoryEmpty = false;
        }

        if (isCategoryEmpty) {
            mCategoryList.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mCategoryList.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    /**
     * Restore important infos when the activity is re-creating
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mSelectedCategory = (Category) savedInstanceState
                .getSerializable(Constants.EXTRA_SELECTED_CATEGORY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Save important infos when the activity is finishing
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.EXTRA_SELECTED_CATEGORY, mSelectedCategory);
        super.onSaveInstanceState(outState);
    }

    /**
     * Create dialogs
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);

        if (id == DIALOG_ADD_CATEGORY) {
            builder.setMessage(getString(R.string.category_name_text));

            LayoutInflater factory = LayoutInflater.from(this);
            final View textField = factory.inflate(R.layout.add_category_layout, null);
            final EditText editText = (EditText) textField.findViewById(R.id.category_name_field);
            builder.setView(textField);

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (editText != null) {
                        String text = editText.getText().toString();
                        if (text.trim().length() > 0) {
                            removeDialog(DIALOG_ADD_CATEGORY);
                            insertCategory(editText.getText().toString());
                        } else {
                            showDialog(DIALOG_EMPTY_CATEGORY);
                        }
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    removeDialog(DIALOG_ADD_CATEGORY);
                }
            });

            Dialog dialog = builder.create();
            return dialog;

        } else if (id == DIALOG_OPTIONS_CATEGORY) {
            builder.setMessage(getString(R.string.category_options));

            builder.setPositiveButton(R.string.edit_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_OPTIONS_CATEGORY);
                    showDialog(DIALOG_EDIT_CATEGORY);
                }
            });

            builder.setNegativeButton(R.string.remove_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_OPTIONS_CATEGORY);
                    removeCategory();
                }
            });
            Dialog dialog = builder.create();
            return dialog;
        } else if (id == DIALOG_EDIT_CATEGORY) {
            builder.setMessage(getString(R.string.category_name_text));

            LayoutInflater factory = LayoutInflater.from(this);
            final View textField = factory.inflate(R.layout.add_category_layout, null);
            final EditText editText = (EditText) textField.findViewById(R.id.category_name_field);
            if (editText != null) {
                editText.setText(mSelectedCategory.getCategory());
            }
            builder.setView(textField);

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (editText != null) {
                        String text = editText.getText().toString();
                        if (text.trim().length() > 0) {
                            removeDialog(DIALOG_EDIT_CATEGORY);
                            editCategory(editText.getText().toString());
                        } else {
                            showDialog(DIALOG_EMPTY_CATEGORY);
                        }
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    removeDialog(DIALOG_EDIT_CATEGORY);
                }
            });

            Dialog dialog = builder.create();
            return dialog;

        } else if (id == DIALOG_EMPTY_CATEGORY) {
            builder.setMessage(getString(R.string.category_name_invalid));

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_EMPTY_CATEGORY);
                }
            });

            Dialog dialog = builder.create();
            return dialog;
        } else if (id == DIALOG_ERROR_CATEGORY) {
            builder.setMessage(getString(R.string.error_text));

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_ERROR_CATEGORY);
                }
            });

            Dialog dialog = builder.create();
            return dialog;
        }

        return null;
    }

    /**
     * Call database to remove a category
     */
    private void removeCategory() {
        mDatabaseDelegate.removeCategory(this, mSelectedCategory.getIdCategory());
    }

    /**
     * Call database to inert a new category
     * 
     * @param categoryName
     */
    private void insertCategory(String categoryName) {
        Category category = new Category();
        category.setCategory(categoryName);
        mDatabaseDelegate.insertCategory(this, category);
    }

    /**
     * Call database to edit a category
     * 
     * @param categoryName
     */
    private void editCategory(String categoryName) {
        Category category = new Category();
        category.setCategory(categoryName);
        category.setIdCategory(mSelectedCategory.getIdCategory());
        mDatabaseDelegate.editCategory(this, category);
    }

    /**
     * Call database to list categories
     */
    private void listCategory(String orderBy) {
        mDatabaseDelegate.listCategory(this, orderBy);
    }

    /**
     * OnSucess callback
     */
    @Override
    public void onSuccess() {
        listCategory(mOrderBy);
    }

    /**
     * OnSucess callback
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(Object obj) {
        if (obj instanceof ArrayList<?>) {
            ArrayList<Category> categoryArray = (ArrayList<Category>) obj;
            mCategoryAdapter.updateCategory(categoryArray);
            updateView();
        }

    }

    /**
     * OnError callback
     */
    @Override
    public void onError() {
        showDialog(DIALOG_ERROR_CATEGORY);
    }

}
