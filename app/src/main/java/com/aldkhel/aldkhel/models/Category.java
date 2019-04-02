package com.aldkhel.aldkhel.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {

    private long id;
    private String name;

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

    @Override
    public String toString() {
        return name;
    }

    public static Category fromJson(JSONObject json) throws JSONException {
        Category category = new Category();
        category.setId(json.getLong("category_id"));
        category.setName(json.getString("name"));
        return category;
    }

}
