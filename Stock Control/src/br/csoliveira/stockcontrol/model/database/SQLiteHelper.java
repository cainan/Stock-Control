package br.csoliveira.stockcontrol.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.csoliveira.stockcontrol.util.Utils;

public class SQLiteHelper extends SQLiteOpenHelper {
    private String mCreateTableCategory;
    private String mCreateTableProduct;
    private String mDeleteTableCategory;
    private String mDeleteTableProduct;

    /**
     * Class' Constructor
     * 
     * @param ctx
     * @param databaseName
     * @param version
     * @param createTableCategory
     * @param scriptDelete
     */
    public SQLiteHelper(Context ctx, String databaseName, int version, String createTableCategory,
            String createTableProduct, String deleteTableCategory, String deleteTableProduct) {
        super(ctx, databaseName, null, version);
        Utils.doLog("SQLiteHelper constructor");
        this.mCreateTableCategory = createTableCategory;
        this.mCreateTableProduct = createTableProduct;
        this.mDeleteTableCategory = deleteTableCategory;
        this.mDeleteTableProduct = deleteTableProduct;
    }

    /**
     * Create the Database
     */
    public void onCreate(SQLiteDatabase db) {
        Utils.doLog("creating DataBase");
        db.execSQL(mCreateTableCategory);
        db.execSQL(mCreateTableProduct);
    }

    /**
     * Upgrade the database if necessary
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Utils.doLog("upgrading DataBase");
        db.execSQL(mDeleteTableCategory);
        db.execSQL(mDeleteTableProduct);
        onCreate(db);
    }
}