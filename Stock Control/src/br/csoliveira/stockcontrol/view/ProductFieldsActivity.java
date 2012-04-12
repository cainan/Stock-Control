package br.csoliveira.stockcontrol.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.Product;
import br.csoliveira.stockcontrol.model.database.DatabaseDelegate;
import br.csoliveira.stockcontrol.model.database.DatabaseInterface;

public class ProductFieldsActivity extends Activity implements DatabaseInterface {

    /** Dialog id's */
    private static final int DIALOG_ERROR = 1;
    private static final int DIALOG_FIELD_EMPY = 2;

    /** */
    DatabaseDelegate mDatabaseDelegate;

    /** */
    EditText mNameField;

    /** */
    EditText mPriceField;

    /** */
    EditText mQuantityField;

    /** */
    Button mAddButton;

    /** */
    Spinner mSpinner;

    /** */
    ArrayList<Category> mCategoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_fields);

        mDatabaseDelegate = DatabaseDelegate.getInstance(this);

        initView();

        initCategoriesAvailable();

    }

    private void initView() {
        mNameField = (EditText) findViewById(R.id.product_name_field);
        mPriceField = (EditText) findViewById(R.id.product_price_field);
        mQuantityField = (EditText) findViewById(R.id.product_quantity_field);
        mSpinner = (Spinner) findViewById(R.id.product_category_spinner);

        mAddButton = (Button) findViewById(R.id.product_add_btn);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    saveProduct();
                }

            });
        }
    }

    private void initCategoriesAvailable() {
        mDatabaseDelegate.listCategory(this, null);
    }

    private void updateSpinner() {
        String[] arrayString = new String[mCategoryArray.size()];

        for (int i = 0; i < mCategoryArray.size(); i++) {
            arrayString[i] = mCategoryArray.get(i).getCategoryName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayString);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    protected void saveProduct() {
        if (mNameField != null && mPriceField != null && mQuantityField != null) {
            String name = mNameField.getText().toString();
            String price = mPriceField.getText().toString();
            String quantity = mQuantityField.getText().toString();

            if (name.trim().length() < 1 || price.trim().length() < 1
                    || quantity.trim().length() < 1) {
                showDialog(DIALOG_FIELD_EMPY);
            } else {
                Product product = new Product();
                Category category = mCategoryArray.get(mSpinner.getSelectedItemPosition());

                product.setProductName(mNameField.getText().toString());
                product.setPrice(mPriceField.getText().toString());
                product.setQuantity(Integer.parseInt(mQuantityField.getText().toString()));
                product.setCategory(category);

                mDatabaseDelegate.insertProduct(this, product);
            }

        } else {
            showDialog(DIALOG_FIELD_EMPY);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);

        if (id == DIALOG_ERROR) {
            builder.setMessage(getString(R.string.error_text));

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_ERROR);
                }
            });

            Dialog dialog = builder.create();
            return dialog;
        } else if (id == DIALOG_FIELD_EMPY) {
            builder.setMessage(getString(R.string.empty_field_text));

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(DIALOG_FIELD_EMPY);
                }
            });

            Dialog dialog = builder.create();
            return dialog;
        }
        return null;
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, getString(R.string.success_text), Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(Object obj) {
        if (obj instanceof ArrayList<?>) {
            mCategoryArray = (ArrayList<Category>) obj;
            updateSpinner();
        }
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

}
