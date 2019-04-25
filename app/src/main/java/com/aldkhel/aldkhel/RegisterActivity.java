package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
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

    private String session;

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

        session = getIntent().getStringExtra("session");

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

        findViewById(R.id.tvAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("الشروط والاحكام");
                builder.setMessage(getString(R.string.terms));
                builder.setCancelable(false);
                builder.setPositiveButton("مواقق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cbAgree.setChecked(true);
                    }
                });
                builder.show();
            }
        });

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


        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchZones(countries.get(spCountry.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fetchCountries();

    }

    private void doRegister() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        JSONObject json = new JSONObject();
        try {
            json.put("firstname", fname);
            json.put("lastname", sname);
            json.put("email", email);
            json.put("telephone", phone);
            json.put("company", company);
            json.put("company_id", company);
            json.put("address_1", address);
            json.put("address_2", address2);
            json.put("city", city);
            json.put("postcode", mail);
            json.put("country_id", countryId+"");
            json.put("zone_id", zoneId+"");
            json.put("password", password);
            json.put("confirm", confPassword);
            json.put("newsletter", news+"");
            json.put("agree", "1");
            json.put("tax_id", "1");
            json.put("fax", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Consts.API_URL + "rest/register/register")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addJSONObjectBody(json)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(RegisterActivity.this, "تم تسجيل الدخول بنجاح مرحبا بك", Toast.LENGTH_SHORT).show();

//                            JSONObject data = response.getJSONObject("data");

//                            Utils.saveUser(RegisterActivity.this, User.fromJson(data));
                            Utils.saveSession(RegisterActivity.this, session);
                            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                            finish();

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

        AndroidNetworking.get(Consts.API_URL + "feed/rest_api/countries")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray data = response.getJSONArray("data");


                            JSONObject json;
                            for (int i=0;i<data.length();i++) {
                                json = data.getJSONObject(i);
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

    private void fetchZones(long countryId) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        zones = new HashMap<>();

        AndroidNetworking.get(Consts.API_URL + "feed/rest_api/countries&id=" + countryId)
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray data = response.getJSONObject("data").getJSONArray("zone");

                            JSONObject json;
                            for (int i=0;i<data.length();i++) {
                                json = data.getJSONObject(i);
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


    private void requestSessionId() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "feed/rest_api/session")
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(RegisterActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject data = response.getJSONObject("data");
                            session = data.getString("session");
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
