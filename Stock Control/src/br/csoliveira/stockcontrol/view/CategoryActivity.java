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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.adapter.CategoryListAdapter;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.database.DatabaseDelegate;
import br.csoliveira.stockcontrol.model.database.DatabaseInterface;
import br.csoliveira.stockcontrol.util.Constants;
import br.csoliveira.stockcontrol.util.Utils;

public class CategoryActivity extends Activity implements DatabaseInterface {

    private static final int DIALOG_ADD_CATEGORY = 10;
    private static final int DIALOG_OPTIONS_CATEGORY = 11;

    private Button mAddBtn;

    private DatabaseDelegate mDatabaseDelegate;

    private CategoryListAdapter mCategoryAdapter;

    private ListView mCategoryList;

    private Category mSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        mDatabaseDelegate = DatabaseDelegate.getInstance(getApplicationContext());
        mCategoryAdapter = new CategoryListAdapter(this);

        initView();

        listCategory();
    }

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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mSelectedCategory = (Category) savedInstanceState
                .getSerializable(Constants.EXTRA_SELECTED_CATEGORY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.EXTRA_SELECTED_CATEGORY, mSelectedCategory);
        super.onSaveInstanceState(outState);
    }

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
                    dismissDialog(DIALOG_ADD_CATEGORY);
                    if (editText != null) {
                        saveCategory(editText.getText().toString());
                        editText.setText("");
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismissDialog(DIALOG_ADD_CATEGORY);
                    if (editText != null) {
                        editText.setText("");
                    }
                }
            });

            Dialog dialog = builder.create();
            return dialog;

        } else if (id == DIALOG_OPTIONS_CATEGORY) {
            builder.setMessage(getString(R.string.category_options));

            builder.setPositiveButton(R.string.edit_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });

            builder.setNegativeButton(R.string.remove_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog(DIALOG_OPTIONS_CATEGORY);
                    removeCategory();
                }
            });
            Dialog dialog = builder.create();
            return dialog;
        }

        return null;
    }

    private void removeCategory() {
        mDatabaseDelegate.removeCategory(this, mSelectedCategory.getIdCategory());
    }

    private void saveCategory(String name) {
        Category category = new Category();
        category.setCategory(name);
        mDatabaseDelegate.insertCategory(this, category);
    }

    private void listCategory() {
        mDatabaseDelegate.listCategory(this);
    }

    @Override
    public void onSuccess() {
        listCategory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(Object obj) {
        if (obj instanceof ArrayList<?>) {
            ArrayList<Category> categoryArray = (ArrayList<Category>) obj;
            mCategoryAdapter.updateCategory(categoryArray);
        }

    }

    @Override
    public void onError() {
        // TODO error code
    }

}
