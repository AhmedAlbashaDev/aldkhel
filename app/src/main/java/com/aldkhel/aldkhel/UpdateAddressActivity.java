package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class UpdateAddressActivity extends AppCompatActivity {

    private static final String TAG = "UpdateAddressActivity";

    private Spinner spCountry;
    private Spinner spZone;
    private EditText etCompany;
    private EditText etCity;
    private EditText etAddress;
    private EditText etAddress2;
    private EditText etMail;

    private Map<String, Integer> countries;
    private Map<String, Integer> zones;

    private String session;
    private String company;
    private String address;
    private String address2;
    private String mail;
    private int countryId;
    private int zoneId;
    private String city;

    private long addressId;


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
        setContentView(R.layout.activity_update_address);

        session = Utils.getSessionId(this);

        addressId = getIntent().getLongExtra("address", 0);

        etCompany = findViewById(R.id.etCompany);
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etAddress2 = findViewById(R.id.etAddress2);
        etMail = findViewById(R.id.etMail);
        spCountry = findViewById(R.id.spCountry);
        spZone = findViewById(R.id.spZone);

        final Button bUpdate = findViewById(R.id.bUpdate);
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                company = etCompany.getText().toString();
                address = etAddress.getText().toString();
                address2 = etAddress2.getText().toString();
                mail = etMail.getText().toString();
                city = etCity.getText().toString();

                countryId = countries.get(spCountry.getSelectedItem().toString());
                zoneId = zones.get(spZone.getSelectedItem().toString());

                update();
            }
        });

        findViewById(R.id.bDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAddressActivity.this);
                builder.setMessage("هل تريد اكمال العملية !");
                builder.setPositiveButton("تاكيد", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteAddressData();
                    }
                });
                builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
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


        getAddressData();

        fetchCountries();
    }

    private void getAddressData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/account/address&id=" + addressId)
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject data = response.getJSONObject("data");

//                            spCountry;
//                            spZone;

                            etCompany.setText(data.getString("company"));
                            etCity.setText(data.getString("city"));
                            etAddress.setText(data.getString("address_1"));
                            etAddress2.setText(data.getString("address_2"));
                            etMail.setText(data.getString("postcode"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAddressData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.delete(Consts.API_URL + "rest/account/address&id=" + addressId)
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(UpdateAddressActivity.this, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void update() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        JSONObject json = new JSONObject();
        try {
            json.put("company", company);
            json.put("address_1", address);
            json.put("address_2", address2);
            json.put("city", city);
            json.put("postcode", mail);
            json.put("country_id", countryId+"");
            json.put("zone_id", zoneId+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.put(Consts.API_URL + "rest/account/address&id=" + addressId)
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addJSONObjectBody(json)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(UpdateAddressActivity.this, "تم العملية بنجاح", Toast.LENGTH_SHORT).show();

                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
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
                                            UpdateAddressActivity.this,
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
                        Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
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
                                            UpdateAddressActivity.this,
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
                        Toast.makeText(UpdateAddressActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
