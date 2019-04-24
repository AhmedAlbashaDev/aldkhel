package com.aldkhel.aldkhel.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.aldkhel.aldkhel.utils.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Product implements Parcelable {

    private long id;
    private String name;
    private double offer;
    private double price;
    private int quantity;
    private String details;
    private String imageUrl;
    private int viewed;
    private String stockStatus;

    public Product() {}


    protected Product(Parcel in) {
        id = in.readLong();
        name = in.readString();
        offer = in.readDouble();
        price = in.readDouble();
        quantity = in.readInt();
        details = in.readString();
        imageUrl = in.readString();
        viewed = in.readInt();
        stockStatus = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeDouble(offer);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(details);
        dest.writeString(imageUrl);
        dest.writeInt(viewed);
        dest.writeString(stockStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImageUrl() {
        return  imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.ProductContract.COL_ID, getId());
        cv.put(DbHelper.ProductContract.COL_NAME, getName());
        cv.put(DbHelper.ProductContract.COL_DETAILS, getDetails());
        cv.put(DbHelper.ProductContract.COL_IMAGE, getImageUrl());
        cv.put(DbHelper.ProductContract.COL_PRICE, getPrice());
        cv.put(DbHelper.ProductContract.COL_QUANTITY, getQuantity());
        return cv;
    }

    public static Product fromCursor(Cursor cursor) {

        Product product = new Product();
        product.setId(cursor.getLong(cursor.getColumnIndex(DbHelper.ProductContract.COL_ID)));
        product.setName(cursor.getString(cursor.getColumnIndex(DbHelper.ProductContract.COL_NAME)));
//                product.setCategory(cursor.getString(cursor.getColumnIndex(DbHelper.ProductContract.COL_CATEGORYID)));
        product.setOffer(cursor.getDouble(cursor.getColumnIndex(DbHelper.ProductContract.COL_OFFER)));
        product.setPrice(cursor.getDouble(cursor.getColumnIndex(DbHelper.ProductContract.COL_PRICE)));
        product.setImageUrl(cursor.getString(cursor.getColumnIndex(DbHelper.ProductContract.COL_IMAGE)));
        product.setDetails(cursor.getString(cursor.getColumnIndex(DbHelper.ProductContract.COL_DETAILS)));
        product.setQuantity(cursor.getInt(cursor.getColumnIndex(DbHelper.ProductContract.COL_QUANTITY)));
         return product;
    }

    public static Product fromJson(JSONObject json) throws JSONException {
        Product product = new Product();
        product.setId(json.getLong("product_id"));
        product.setName(json.getString("name"));
        product.setDetails(json.getString("description"));
        if (json.has("thumb")) {
            product.setImageUrl(json.getString("thumb"));
        } else {
            product.setImageUrl(json.getString("image"));
        }
        product.setPrice(json.getDouble("price"));
        product.setStockStatus(json.getString("stock_status"));
//        product.setOffer(json.getDouble("offer"));
        return product;
    }

}
