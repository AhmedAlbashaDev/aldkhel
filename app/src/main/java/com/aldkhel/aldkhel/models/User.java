package com.aldkhel.aldkhel.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private int newsletter;
    private long addressId;

    public User() {}

    private User(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        telephone = in.readString();
        newsletter = in.readInt();
        addressId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(telephone);
        dest.writeInt(newsletter);
        dest.writeLong(addressId);
    }
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(int newsletter) {
        this.newsletter = newsletter;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("customer_id", getId());
        json.put("firstname", getFirstName());
        json.put("lastname", getLastName());
        json.put("email", getEmail());
        json.put("telephone", getTelephone());
        json.put("newsletter", getNewsletter());
        json.put("address_id", getAddressId());
        return json;
    }

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        user.setId(json.getLong("customer_id"));
        user.setFirstName(json.getString("firstname"));
        user.setLastName(json.getString("lastname"));
        user.setEmail(json.getString("email"));
        user.setTelephone(json.getString("telephone"));
        user.setNewsletter(json.getInt("newsletter"));
        user.setAddressId(json.getLong("address_id"));
        return user;
    }

}
