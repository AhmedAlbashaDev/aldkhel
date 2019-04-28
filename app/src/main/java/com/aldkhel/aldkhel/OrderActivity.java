package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.DbHelper;
import com.aldkhel.aldkhel.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

    private TextView tvPaymentAddress;
    private TextView tvShippingAddress;
    private AppCompatCheckBox cbAgree;

    private String session;
    private JSONObject jsonPaymentAddress;

    private DbHelper dbHelper;

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
        setContentView(R.layout.activity_order);

        session = Utils.getSessionId(this);
        dbHelper = new DbHelper(this);

        tvPaymentAddress = findViewById(R.id.tvPaymentAddress);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);

        cbAgree = findViewById(R.id.cbAgree);
        findViewById(R.id.tvAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                builder.setTitle("سياسة الاستبدال والاسترجاع");
                builder.setMessage(getString(R.string.return_policy));
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

        getPaymentAddress();

        findViewById(R.id.bSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPaymentAddress();
//                postShippingAddress();
//                postPaymentMethod();
//                postShippingMethod();

            }
        });

    }


    private void getPaymentAddress() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/payment_address/paymentaddress")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            jsonPaymentAddress = response.getJSONArray("addresses").getJSONObject(0);

                            String address = String.format("%s, %s, %s, %s, %s, %s, %s",
                                    jsonPaymentAddress.getString("firstname"),
                                    jsonPaymentAddress.getString("lastname"),
                                    jsonPaymentAddress.getString("company"),
                                    jsonPaymentAddress.getString("address_1"),
                                    jsonPaymentAddress.getString("city"),
                                    jsonPaymentAddress.getString("country"),
                                    jsonPaymentAddress.getString("zone")
                            );

                            tvPaymentAddress.setText(address);
                            tvShippingAddress.setText(address);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void postPaymentAddress() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.post(Consts.API_URL + "rest/payment_address/paymentaddress")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addJSONObjectBody(jsonPaymentAddress)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            postShippingAddress();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void postShippingAddress() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.post(Consts.API_URL + "rest/shipping_address/shippingaddress")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addJSONObjectBody(jsonPaymentAddress)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            postPaymentMethod();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void postConfirm() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/confirm/confirm")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            putConfirm();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void putConfirm() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.put(Consts.API_URL + "rest/confirm/confirm")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            dbHelper.deleteAllProducts();
                            Toast.makeText(OrderActivity.this, "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void postPaymentMethod() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        JSONObject json = new JSONObject();
        try {
            json.put("payment_method", "cod");
            json.put("agree", "1");
            json.put("comment", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Consts.API_URL + "rest/payment_method/payments")
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
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }


                            postShippingMethod();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void postShippingMethod() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        JSONObject json = new JSONObject();
        try {
            json.put("shipping_method", "flat.flat");
            json.put("comment", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Consts.API_URL + "rest/shipping_method/shippingmethods")
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
                                Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            postConfirm();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
