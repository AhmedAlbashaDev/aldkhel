package com.aldkhel.aldkhel.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aldkhel.aldkhel.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DbHelper  extends SQLiteOpenHelper {


    private static final String TAG = "DbHelper";
    private static final String DB_NAME = "markat.db";
    private static final int VERSION = 1;

    public final static class ProductContract {
        static final String TABLE_PRODUCTS = "products";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_CITY = "city";
        public static final String COL_CONTACT = "contact_phone";
        public static final String COL_DETAILS = "details";
        public static final String COL_STATUS = "status";
        public static final String COL_IMAGE = "image";
        public static final String COL_MARK = "mark";
        public static final String COL_USEDATE = "use_date";
        public static final String COL_PRICE = "price";
        public static final String COL_CATEGORYID = "category_id";
        public static final String COL_OFFER = "offer";
        public static final String COL_CHECKED = "checked";
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ProductContract.TABLE_PRODUCTS + " ("
            + ProductContract.COL_ID + " INTEGER,"
            + ProductContract.COL_NAME + " TEXT,"
            + ProductContract.COL_CITY + " TEXT,"
            + ProductContract.COL_CONTACT + " INTEGER,"
            + ProductContract.COL_DETAILS + " TEXT,"
            + ProductContract.COL_STATUS + " TEXT,"
            + ProductContract.COL_IMAGE + " TEXT,"
            + ProductContract.COL_MARK + " TEXT,"
            + ProductContract.COL_USEDATE + " TEXT,"
            + ProductContract.COL_PRICE + " INTEGER,"
            + ProductContract.COL_CATEGORYID + " TEXT,"
            + ProductContract.COL_OFFER + " TEXT,"
            + ProductContract.COL_CHECKED + " TEXT)");
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


        ContentValues cv = new ContentValues();
        cv.put(ProductContract.COL_ID,product.getId());
        cv.put(ProductContract.COL_NAME,product.getName());
        cv.put(ProductContract.COL_CITY,product.getCity());
        cv.put(ProductContract.COL_CONTACT,product.getContactPhone());
        cv.put(ProductContract.COL_DETAILS,product.getDetails());
        cv.put(ProductContract.COL_STATUS,product.getStatus());
        cv.put(ProductContract.COL_IMAGE,product.getImageUrl());
        cv.put(ProductContract.COL_MARK,product.getMark());
        cv.put(ProductContract.COL_USEDATE,product.getUseDate());
        cv.put(ProductContract.COL_PRICE,product.getPrice());
        cv.put(ProductContract.COL_CATEGORYID,product.getMark());
        cv.put(ProductContract.COL_OFFER,product.getMark());
        cv.put(ProductContract.COL_CHECKED,product.getMark());
        // insert new value
        return getWritableDatabase().insert(
                ProductContract.TABLE_PRODUCTS,
                null,
                cv);
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
                        ProductContract.COL_CITY,
                        ProductContract.COL_CONTACT,
                        ProductContract.COL_DETAILS,
                        ProductContract.COL_STATUS,
                        ProductContract.COL_IMAGE,
                        ProductContract.COL_MARK,
                        ProductContract.COL_USEDATE,
                        ProductContract.COL_PRICE,
                        ProductContract.COL_CATEGORYID,
                        ProductContract.COL_OFFER,
                        ProductContract.COL_CHECKED,
                },
                null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            Product product;

            while (cursor.moveToNext()) {
                product = new Product();
                product.setId(cursor.getLong(cursor.getColumnIndex(ProductContract.COL_ID)));
                product.setName(cursor.getString(cursor.getColumnIndex(ProductContract.COL_NAME)));
//                product.setCategory(cursor.getString(cursor.getColumnIndex(ProductContract.COL_CATEGORYID)));
                product.setContactPhone(cursor.getString(cursor.getColumnIndex(ProductContract.COL_CONTACT)));
                product.setCity(cursor.getString(cursor.getColumnIndex(ProductContract.COL_CITY)));
                product.setMark(cursor.getString(cursor.getColumnIndex(ProductContract.COL_MARK)));
                product.setOffer(cursor.getString(cursor.getColumnIndex(ProductContract.COL_OFFER)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex(ProductContract.COL_PRICE)));
                product.setImageUrl(cursor.getString(cursor.getColumnIndex(ProductContract.COL_IMAGE)));
                product.setDetails(cursor.getString(cursor.getColumnIndex(ProductContract.COL_DETAILS)));
                product.setStatus(cursor.getString(cursor.getColumnIndex(ProductContract.COL_STATUS)));
                product.setUseDate(cursor.getString(cursor.getColumnIndex(ProductContract.COL_USEDATE)));
                product.setChecked(false);
                products.add(product);
            }
            cursor.close();
        }
        return products;
    }

}
