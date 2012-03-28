package br.csoliveira.stockcontrol.model.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.Product;

public class DatabaseDelegate {

    /** Hold the Database's name */
    private static final String DB_NAME = "Stock_Control";

    /** Hold the Database's version */
    private static final int DB_VERSION = 1;

    /** Hold the Category table name */
    private static final String TABLE_CATEGORY = "Category";

    /** Hold the Product table name */
    private static final String TABLE_PRODUCT = "Product";

    /** Hold the script to delete the category table */
    private static final String DELETE_TABLE_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;

    /** Hold the script to delete product table */
    private static final String DELETE_TABLE_PRODUCT = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;

    /** Hold the script to create category table */
    private static final String CREATE_TABLE_CATEGORY = "create table " + TABLE_CATEGORY
            + "( _id integer primary key autoincrement," + "category text not null" + ");";

    /** Hold the script to create product table */
    private static final String CREATE_TABLE_PRODUCT = "create table " + TABLE_PRODUCT
            + "( _id integer primary key autoincrement," + "idCategoria integer not null,"
            + "product text not null," + "price text not null," + "quantity text not null" + ");";

    /** Hold an instance of SQLiteHelper */
    private SQLiteHelper mDatabaseHelper;

    /** Hold the data base while open */
    private SQLiteDatabase mDataBase;

    /** Static instance to connect to database */
    private static DatabaseDelegate mDatabaseDelegate;

    /** Dialog to be shown when there is a AsyncTask executing */
    private ProgressDialog mWaitDialog;

    /**
     * Get a instance of database
     * 
     * @param Context
     */
    public static DatabaseDelegate getInstance(Context applicationContext) {
        if (mDatabaseDelegate == null) {
            mDatabaseDelegate = new DatabaseDelegate(applicationContext);
        }
        return mDatabaseDelegate;
    }

    /**
     * Create a new database if it doesn't exist.
     * 
     * @param Context
     */
    private DatabaseDelegate(Context context) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new SQLiteHelper(context, DB_NAME, DB_VERSION, CREATE_TABLE_CATEGORY,
                    CREATE_TABLE_PRODUCT, DELETE_TABLE_CATEGORY, DELETE_TABLE_PRODUCT);

        }
    }

    public synchronized void insertCategory(Activity activity, Category category) {
        new InsertCategory(activity).execute(category);
    }

    /**
     * Insert a new category
     * 
     * @param category
     */
    public synchronized void insertCategory(Category category) {
        // Open Database
        mDataBase = mDatabaseHelper.getWritableDatabase();
        mDataBase.insert(TABLE_CATEGORY, null, categoryContentValues(category));
        // Close database
        closeDb();

    }

    /**
     * Close database
     */
    private void closeDb() {
        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
    }

    /**
     * Encapsulate a product inside a content value
     * 
     * @param product
     * @return contentValues
     */
    private ContentValues ProductContentValues(Product product) {
        ContentValues cv = new ContentValues();
        cv.put("product", product.getProductName());
        cv.put("idCategoria", product.getCategory().getIdCategory());
        cv.put("price", product.getPrice());
        cv.put("quantity", product.getQuantity());
        return cv;
    }

    /**
     * Encapsulate category inside a content value
     * 
     * @param category
     * @return contentValues
     */
    private ContentValues categoryContentValues(Category category) {
        ContentValues cv = new ContentValues();
        cv.put("category", category.getCategory());
        return cv;
    }

    private class InsertCategory extends AsyncTask<Category, Void, Void> {

        private Activity mActivity;

        public InsertCategory(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Category... params) {
            Category category = params[0];

            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();
            for (int i = 0; i < 200; i++) {
                mDataBase.insert(TABLE_CATEGORY, null, categoryContentValues(category));
            }
            // Close database
            closeDb();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideWaitDialog();
            super.onPostExecute(result);
        }
    }

    private void showWaitDialog(Activity mActivity) {
        mWaitDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.wait_text),
                true);
    }

    private void hideWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }
}
