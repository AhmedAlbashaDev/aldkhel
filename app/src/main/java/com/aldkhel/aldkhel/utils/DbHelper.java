package com.aldkhel.aldkhel.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aldkhel.aldkhel.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DbHelper  extends SQLiteOpenHelper {


    private static final String TAG = "DbHelper";
    private static final String DB_NAME = "aldkel.db";
    private static final int VERSION = 1;

    public final static class ProductContract {
        static final String TABLE_PRODUCTS = "products";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_DETAILS = "details";
        public static final String COL_IMAGE = "image";
        public static final String COL_PRICE = "price";
        public static final String COL_CATEGORYID = "category_id";
        public static final String COL_OFFER = "offer";
        public static final String COL_QUANTITY = "quantity";
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ProductContract.TABLE_PRODUCTS + " ("
            + ProductContract.COL_ID + " INTEGER,"
            + ProductContract.COL_NAME + " TEXT,"
            + ProductContract.COL_DETAILS + " TEXT,"
            + ProductContract.COL_IMAGE + " TEXT,"
            + ProductContract.COL_PRICE + " INTEGER,"
            + ProductContract.COL_QUANTITY + " INTEGER,"
            + ProductContract.COL_CATEGORYID + " TEXT,"
            + ProductContract.COL_OFFER + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductContract.TABLE_PRODUCTS);
        onCreate(db);
    }

    public long addProduct(Product product) {
        Cursor cursor = getReadableDatabase().query(ProductContract.TABLE_PRODUCTS,
                new String[] {ProductContract.COL_ID},
                ProductContract.COL_ID + " = ?",
                 new String[] {product.getId() + ""},
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            return 1;
        }

        // insert new value
        return getWritableDatabase().insert(
                ProductContract.TABLE_PRODUCTS,
                null,
                product.toContentValues());
    }

    public long deleteAllProducts() {
        return getWritableDatabase().delete(
                ProductContract.TABLE_PRODUCTS,
                null,
                null);
    }

    public int deleteProduct(long productId) {
        return getWritableDatabase().delete(ProductContract.TABLE_PRODUCTS,
                ProductContract.COL_ID + " = ?",
                new String[] {productId + ""});
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(ProductContract.TABLE_PRODUCTS,
                new String[] {ProductContract.COL_ID,
                        ProductContract.COL_NAME,
                        ProductContract.COL_DETAILS,
                        ProductContract.COL_IMAGE,
                        ProductContract.COL_PRICE,
                        ProductContract.COL_CATEGORYID,
                        ProductContract.COL_OFFER,
                        ProductContract.COL_QUANTITY,
                },
                null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                products.add(Product.fromCursor(cursor));
            }
            cursor.close();
        }
        return products;
    }

}
