package br.csoliveira.stockcontrol.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.adapter.ProductListAdapter;
import br.csoliveira.stockcontrol.model.Product;
import br.csoliveira.stockcontrol.model.database.DatabaseDelegate;
import br.csoliveira.stockcontrol.model.database.DatabaseInterface;
import br.csoliveira.stockcontrol.util.Utils;

public class ProductActivity extends Activity implements DatabaseInterface {

    /** Request code to add new product */
    private static final int REQUEST_CODE_ADD = 10;
    private static final int REQUEST_CODE_EDIT = 11;

    /** Hold the add product button */
    private Button mAddBtn;

    /** Hold the product list */
    private ListView mProductList;

    /** Hold category list adapter */
    private ProductListAdapter mProductAdapter;

    /** Database delegate reference */
    private DatabaseDelegate mDatabaseDelegate;

    /** Hold the empty message */
    private TextView mEmptyText;

    /** Hold the order by spinner */
    private Spinner mSpinner;

    /** Hold the last selected product item */
    private Product mSelectedProduct;

    /** Hold the order by Name */
    private String mOrderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view);

        mDatabaseDelegate = DatabaseDelegate.getInstance(this);
        mProductAdapter = new ProductListAdapter(this);

        initView();
    }

    /**
     * Initialize the view
     */
    private void initView() {
        mAddBtn = (Button) findViewById(R.id.add_product_btn);
        if (mAddBtn != null) {
            mAddBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductActivity.this, ProductFieldsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD);
                }
            });
        }

        mProductList = (ListView) findViewById(R.id.product_list);
        if (mProductList != null) {
            mProductList.setAdapter(mProductAdapter);
            mProductList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    mSelectedProduct = (Product) mProductAdapter.getItem(position);
                    if (mSelectedProduct != null) {
                        // showDialog(DIALOG_OPTIONS_CATEGORY);
                    }
                }

            });
        }

        mSpinner = (Spinner) findViewById(R.id.order_by_spinner);
        if (mSpinner != null) {
            mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    mOrderBy = mSpinner.getItemAtPosition(position).toString();
                    listProduct(mOrderBy);
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
        boolean isProductEmpty = true;
        if (mProductAdapter.getCount() > 0) {
            isProductEmpty = false;
        }

        if (isProductEmpty) {
            mProductList.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mProductList.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void listProduct(String mOrderBy2) {
        mDatabaseDelegate.listProduct(this, null);
    }

    @Override
    public void onSuccess() {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(Object obj) {
        if (obj instanceof ArrayList<?>) {
            ArrayList<Product> productArray = (ArrayList<Product>) obj;
            mProductAdapter.updateProduct(productArray);
            updateView();
        }
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

}
