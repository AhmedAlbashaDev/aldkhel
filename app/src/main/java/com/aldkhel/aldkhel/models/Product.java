package com.aldkhel.aldkhel.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aldkhel.aldkhel.utils.Consts;

import org.json.JSONException;
import org.json.JSONObject;

public class Product implements Parcelable {

    private long id;
    private String name;
    private String city;
    private String status;
    private double offer;
    private String mark;
    private double price;
    private String details;
    private String useDate;
    private String imageUrl;
    private String contactPhone;
    private boolean checked;

    public Product() {}


    protected Product(Parcel in) {
        id = in.readLong();
        name = in.readString();
        city = in.readString();
        status = in.readString();
        offer = in.readDouble();
        mark = in.readString();
        price = in.readDouble();
        details = in.readString();
        useDate = in.readString();
        imageUrl = in.readString();
        contactPhone = in.readString();
        checked = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(status);
        dest.writeDouble(offer);
        dest.writeString(mark);
        dest.writeDouble(price);
        dest.writeString(details);
        dest.writeString(useDate);
        dest.writeString(imageUrl);
        dest.writeString(contactPhone);
        dest.writeInt(checked ? 1 : 0);
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getImageUrl() {
        return Consts.BASE_IMAGE + imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static Product fromJson(JSONObject json) throws JSONException {
        Product product = new Product();
        product.setId(json.getLong("id"));
        product.setName(json.getString("name"));
        product.setCity(json.getString("city"));
        product.setContactPhone(json.getString("contact_phone"));
        product.setDetails(json.getString("details"));
        product.setStatus(json.getString("status"));
        product.setImageUrl(json.getString("image"));
        product.setMark(json.getString("mark"));
        product.setUseDate(json.getString("use_date"));
        product.setPrice(json.getDouble("price"));
        product.setOffer(json.getDouble("offer"));
        product.setChecked(json.getInt("checked") == 1);
        return product;
    }

}
