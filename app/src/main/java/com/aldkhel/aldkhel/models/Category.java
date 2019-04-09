package com.aldkhel.aldkhel.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aldkhel.aldkhel.utils.Consts;

import org.json.JSONException;
import org.json.JSONObject;

public class Category implements Parcelable {

    private long id;
    private String name;
    private String image;

    public Category() {}

    private Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
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

    public String getImage() {
        return Consts.BASE_IMAGE + image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Category fromJson(JSONObject json) throws JSONException {
        Category category = new Category();
        category.setId(json.getLong("category_id"));
        category.setName(json.getString("name"));
        category.setImage(json.getString("image"));
        return category;
    }

}
