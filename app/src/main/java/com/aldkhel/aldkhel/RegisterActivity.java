package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aldkhel.aldkhel.utils.Consts;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Spinner spCountry;
    private Spinner spZone;

    private Map<String, Integer> countries;
    private Map<String, Integer> zones;

    private String fname;
    private String sname;
    private String phone;
    private String email;
    private String company;
    private String address;
    private String address2;
    private String mail;
    private String password;
    private String confPassword;
    private int countryId;
    private int zoneId;
    private String city;
    private int news;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Consts.APP_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_register);

        findViewById(R.id.bLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etFirstName = findViewById(R.id.etFirstName);
        final EditText etSecondName = findViewById(R.id.etSecondName);
        final EditText etPhone = findViewById(R.id.etPhone);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etCompany = findViewById(R.id.etCompany);
        final EditText etCity = findViewById(R.id.etCity);
        final EditText etAddress = findViewById(R.id.etAddress);
        final EditText etAddress2 = findViewById(R.id.etAddress2);
        final EditText etMail = findViewById(R.id.etMail);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etConfPassword = findViewById(R.id.etConfPassword);
        spCountry = findViewById(R.id.spCountry);
        spZone = findViewById(R.id.spZone);
        final AppCompatCheckBox cbNews = findViewById(R.id.cbNews);
        final AppCompatCheckBox cbAgree = findViewById(R.id.cbAgree);

        final Button bRegister = findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbNews.isChecked()) news = 1;

                fname = etFirstName.getText().toString();
                sname = etSecondName.getText().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();
                company = etCompany.getText().toString();
                address = etAddress.getText().toString();
                address2 = etAddress2.getText().toString();
                mail = etMail.getText().toString();
                city = etCity.getText().toString();
                password = etPassword.getText().toString();
                confPassword = etConfPassword.getText().toString();

                countryId = countries.get(spCountry.getSelectedItem().toString());
                zoneId = zones.get(spZone.getSelectedItem().toString());

                if (!password.equals(confPassword)) {
                    Toast.makeText(RegisterActivity.this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!cbAgree.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "الرجاء الموافقة على الشروط والاحكام", Toast.LENGTH_SHORT).show();
                    return;
                }

                doRegister();
            }
        });


        fetchCountries();
        fetchZones();

    }

    private void doRegister() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.post(Consts.API_URL + "write/register.php")
                .setPriority(Priority.HIGH)
                .addBodyParameter("firstname", fname)
                .addBodyParameter("lastname", sname)
                .addBodyParameter("email", email)
                .addBodyParameter("telephone", phone)
                .addBodyParameter("company", company)
                .addBodyParameter("address_1", address)
                .addBodyParameter("address_2", address2)
                .addBodyParameter("city", city)
                .addBodyParameter("postcode", mail)
                .addBodyParameter("country_id", countryId+"")
                .addBodyParameter("zone_id", zoneId+"")
                .addBodyParameter("password", password)
                .addBodyParameter("newsletter", news+"")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            long id = response.getLong("data");
                            if (id > 0) {

                                PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this)
                                        .edit()
                                        .putLong("id", id)
                                        .apply();

                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCountries() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        countries = new HashMap<>();

        AndroidNetworking.get(Consts.API_URL + "show/countries.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            JSONObject json;
                            for (int i=0;i<response.length();i++) {
                                json = response.getJSONObject(i);
                                countries.put(json.getString("name"), json.getInt("country_id"));
                            }

                            spCountry.setAdapter(
                                    new ArrayAdapter<>(
                                            RegisterActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            new ArrayList<>(countries.keySet())
                                    )
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchZones() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        zones = new HashMap<>();

        AndroidNetworking.get(Consts.API_URL + "show/zones.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            JSONObject json;
                            for (int i=0;i<response.length();i++) {
                                json = response.getJSONObject(i);
                                zones.put(json.getString("name"), json.getInt("zone_id"));
                            }

                            spZone.setAdapter(
                                    new ArrayAdapter<>(
                                            RegisterActivity.this,
                                            android.R.layout.simple_list_item_1,
                                            new ArrayList<>(zones.keySet())
                                    )
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
