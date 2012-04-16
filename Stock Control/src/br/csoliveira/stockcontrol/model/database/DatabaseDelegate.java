package br.csoliveira.stockcontrol.model.database;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.Product;
import br.csoliveira.stockcontrol.util.Utils;

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
            + "( _id integer primary key autoincrement," + "idCategory int not null,"
            + "product text not null," + "price text not null," + "quantity text not null,"
            + "FOREIGN KEY(" + "idCategory" + ") REFERENCES " + TABLE_CATEGORY + "(" + " _id "
            + "));";

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

    /**
     * Call the asyncTask to insert a new category
     * 
     * @param activity
     * @param category
     */
    public synchronized void insertCategory(Activity activity, Category category) {
        new InsertCategory(activity).execute(category);
    }

    /**
     * Call asyncTask to remove a category
     * 
     * @param activity
     * @param idCategory
     */
    public void removeCategory(Activity activity, int idCategory) {
        new RemoveCategory(activity).execute(idCategory);
    }

    /**
     * Call asyncTask to edit a category
     * 
     * @param activity
     * @param category
     */
    public void editCategory(Activity activity, Category category) {
        new EditCategory(activity).execute(category);
    }

    /**
     * Call the asyncTask to list categories
     * 
     * @param activity
     * @param orderBy
     */
    public synchronized void listCategory(Activity activity, String orderBy) {
        String sqlOrderBy = generateOrderByCategory(activity, orderBy);
        new ListCategory(activity).execute(sqlOrderBy);
    }

    /**
     * Create a string used in orderBy parameter of a query in database
     * 
     * @param activity
     * @param orderBy
     * @return
     */
    private String generateOrderByCategory(Activity activity, String orderBy) {

        int selected = -1;

        if (orderBy != null) {
            String[] array = activity.getResources().getStringArray(R.array.order_by_array);
            for (int i = 0; i < array.length; i++) {
                if (orderBy.equals(array[i])) {
                    selected = i;
                }
            }
        }

        String sqlSentece;

        switch (selected) {
        case 0:
            sqlSentece = "category " + "ASC";
            break;
        case 1:
            sqlSentece = "category " + "DESC";
            break;
        case 2:
            sqlSentece = "_id " + "ASC";
            break;
        case 3:
            sqlSentece = "_id " + "DESC";
            break;
        default:
            sqlSentece = null;
            break;
        }

        return sqlSentece;
    }

    /**
     * Call the asyncTask to insert a new product
     * 
     * @param activity
     * @param category
     */
    public synchronized void insertProduct(Activity activity, Product product) {
        new InsertProduct(activity).execute(product);
    }

    /**
     * Call asyncTask to remove a product
     * 
     * @param activity
     * @param idProduct
     */
    public void removeProduct(Activity activity, int idProduct) {
        new RemoveProduct(activity).execute(idProduct);
    }

    /**
     * Call asyncTask to edit a product
     * 
     * @param activity
     * @param category
     */
    public void editProduct(Activity activity, Product category) {
        // new EditCategory(activity).execute(category);
    }

    /**
     * Call the asyncTask to list products
     * 
     * @param activity
     * @param orderBy
     */
    public synchronized void listProduct(Activity activity, String orderBy) {
        String sqlOrderBy = generateOrderByCategory(activity, orderBy);
        new ListProduct(activity).execute(sqlOrderBy);
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
    private ContentValues productContentValues(Product product) {
        ContentValues cv = new ContentValues();
        cv.put("product", product.getProductName());
        cv.put("idCategory", product.getCategory().getIdCategory());
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
        cv.put("category", category.getCategoryName());
        return cv;
    }

    /**
     * Insert a new category into a database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class InsertCategory extends AsyncTask<Category, Void, Boolean> {

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
        protected Boolean doInBackground(Category... params) {
            Category category = params[0];

            boolean sucess = false;
            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.insert(TABLE_CATEGORY, null, categoryContentValues(category)) > 0) {
                sucess = true;
            }

            // Close database
            closeDb();

            return sucess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * Edit a category in database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class EditCategory extends AsyncTask<Category, Void, Boolean> {

        private Activity mActivity;

        public EditCategory(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Category... params) {
            Category category = params[0];
            boolean sucess = false;
            int id = category.getIdCategory();
            ContentValues cv = categoryContentValues(category);

            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.update(TABLE_CATEGORY, cv, "_id =" + id, null) > 0) {
                sucess = true;
            }

            // Close database
            closeDb();

            return sucess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * List the categories in the database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class ListCategory extends AsyncTask<String, Void, ArrayList<Category>> {

        private Activity mActivity;

        public ListCategory(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Category> doInBackground(String... params) {
            String orderBy = params[0];
            ArrayList<Category> categoryArray = new ArrayList<Category>();
            Category category;

            String[] allColumns = { "_id", "category" };

            mDataBase = mDatabaseHelper.getWritableDatabase();

            Cursor cursor = mDataBase.query(TABLE_CATEGORY, allColumns, null, null, null, null,
                    orderBy);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); i++) {
                    category = new Category();
                    category.setIdCategory(cursor.getInt(0));
                    category.setCategoryName(cursor.getString(1));

                    categoryArray.add(category);
                    cursor.moveToNext();
                }
            }

            closeDb();

            return categoryArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categoryArray) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, true, categoryArray);
            super.onPostExecute(categoryArray);
        }

    }

    /**
     * Remove a category from database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class RemoveCategory extends AsyncTask<Integer, Void, Boolean> {

        private Activity mActivity;

        public RemoveCategory(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            int id = params[0];
            boolean success = false;
            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.delete(TABLE_CATEGORY, "_id =" + id, null) > 0) {
                success = true;
            }

            // Close database
            closeDb();

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * Insert a new product into a database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class InsertProduct extends AsyncTask<Product, Void, Boolean> {

        private Activity mActivity;

        public InsertProduct(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Product... params) {
            Product product = params[0];

            boolean sucess = false;
            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.insert(TABLE_PRODUCT, null, productContentValues(product)) > 0) {
                sucess = true;
            }

            // Close database
            closeDb();

            return sucess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * Edit a product in database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class EditProduct extends AsyncTask<Product, Void, Boolean> {

        private Activity mActivity;

        public EditProduct(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Product... params) {
            Product product = params[0];
            boolean sucess = false;
            int id = product.getIdProduct();
            ContentValues cv = productContentValues(product);

            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.update(TABLE_PRODUCT, cv, "_id =" + id, null) > 0) {
                sucess = true;
            }

            // Close database
            closeDb();

            return sucess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * List the products in the database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class ListProduct extends AsyncTask<String, Void, ArrayList<Product>> {

        private Activity mActivity;

        public ListProduct(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            String orderBy = params[0];
            ArrayList<Product> productArray = new ArrayList<Product>();
            Product product;

            String[] allColumns = { "_id", "idCategory", "product", "price", "quantity" };

            mDataBase = mDatabaseHelper.getWritableDatabase();

            Cursor cursor = mDataBase.query(TABLE_PRODUCT, allColumns, null, null, null, null,
                    orderBy);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                for (int i = 0; i < cursor.getCount(); i++) {
                    product = new Product();
                    product.setIdProduct(cursor.getInt(0));
                    product.getCategory().setIdCategory(cursor.getInt(1));
                    product.setProductName(cursor.getString(2));
                    product.setPrice(cursor.getString(3));
                    product.setQuantity(cursor.getInt(4));

                    productArray.add(product);
                    cursor.moveToNext();
                }
            }

            closeDb();

            return productArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productArray) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, true, productArray);
            super.onPostExecute(productArray);
        }

    }

    /**
     * Remove a product from database
     * 
     * @author vntcaol
     * @version 1.0
     * @created 29/03/2012
     */
    private class RemoveProduct extends AsyncTask<Integer, Void, Boolean> {

        private Activity mActivity;

        public RemoveProduct(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            showWaitDialog(mActivity);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            int id = params[0];
            boolean success = false;
            // Open Database
            mDataBase = mDatabaseHelper.getWritableDatabase();

            if (mDataBase.delete(TABLE_PRODUCT, "_id =" + id, null) > 0) {
                success = true;
            }

            // Close database
            closeDb();

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideWaitDialog(mActivity);
            notifyActivity((DatabaseInterface) mActivity, success);
            super.onPostExecute(success);
        }
    }

    /**
     * Show a progress dialog to the user
     * 
     * @param activity
     */
    private void showWaitDialog(Activity activity) {
        Utils.lockScreenRotation(activity);
        mWaitDialog = ProgressDialog.show(activity, "", activity.getString(R.string.wait_text),
                true);
    }

    /**
     * Hide the progress dialog
     * 
     * @param activity
     */
    private void hideWaitDialog(Activity activity) {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            Utils.unlockScreenRotation(activity);
        }
    }

    /**
     * Notify the activity that the database actions has finished
     * 
     * @param listener
     * @param success
     */
    private void notifyActivity(DatabaseInterface listener, boolean success) {
        if (success) {
            listener.onSuccess();
        } else {
            listener.onError();
        }
    }

    /**
     * Notify the activity that the database actions has finished
     * 
     * @param listener
     * @param success
     * @param categoryArray
     */
    private void notifyActivity(DatabaseInterface listener, boolean success, ArrayList<?> array) {
        listener.onSuccess(array);
    }
}
