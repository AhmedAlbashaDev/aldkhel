package com.aldkhel.aldkhel.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Category implements Parcelable {

    private long id;
    private String name;
    private String image;
    private String jsonString;
    private List<Category> subCategories;

    public Category() {}

    private Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readString();
        jsonString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(jsonString);
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
        return  image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Category fromJson(JSONObject json) throws JSONException {
        Category category = new Category();
        if (json.has("id")) {
            category.setId(json.getLong("id"));
        } else {
            category.setId(json.getLong("category_id"));
        }
        category.setName(json.getString("name"));
        category.setImage(json.getString("image"));
        return category;
    }

}
