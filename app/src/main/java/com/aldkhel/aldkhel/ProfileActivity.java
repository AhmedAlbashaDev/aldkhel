package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.User;
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

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private AppCompatCheckBox cbNews;

    private User user;
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
        setContentView(R.layout.activity_profile);

        session = Utils.getSessionId(this);

        cbNews = findViewById(R.id.cbNews);
        cbNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int news = cbNews.isChecked() ? 1 : 0;

                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.show();

                AndroidNetworking.put(Consts.API_URL + "rest/account/newsletter&subscribe=" + news)
                        .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                        .addHeaders(Consts.API_SESSION_KEY, session)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d(TAG, response.toString());

                                try {

                                    if (response.getInt("success") != 1) {
                                        Toast.makeText(ProfileActivity.this, "لم تتم العملية بنجاح", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (news == 1) {
                                        Toast.makeText(ProfileActivity.this, "تمت عملية الاشتراك", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "تم الغاء الاشتراك", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            @Override
                            public void onError(ANError error) {
                                progressDialog.dismiss();
                                error.printStackTrace();
                                Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }


    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.bAddress) {
            Intent intent = new Intent(this, UpdateAddressActivity.class);
            intent.putExtra("address", user.getAddressId());
            startActivity(intent);
        }

        if (id == R.id.bAccount) {
            Intent intent = new Intent(this, UpdatePersonalActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }

        if (id == R.id.bOrders) {
            Intent intent = new Intent(this, OrdersHistoryActivity.class);
            startActivity(intent);
        }

        if (id == R.id.bLogout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("هل تريد تسجيل الخروج !");
            builder.setPositiveButton("تاكيد", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    doLogout();
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

        if (id == R.id.bChangePassword) {
            changePasswordDialog();
        }

    }

    void changePasswordDialog() {

        View v = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final EditText etPassword = v.findViewById(R.id.etPassword);
        final EditText etConfPassword = v.findViewById(R.id.etConfPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setTitle("تغير كلمة المرور");
        builder.setPositiveButton("تاكيد", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String password = etPassword.getText().toString();
                final String confPassword = etConfPassword.getText().toString();

                if (password.isEmpty() || confPassword.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "الرجاء ملئ الحقول الفارغة", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confPassword)) {
                    Toast.makeText(ProfileActivity.this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();

                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.show();

                JSONObject json = new JSONObject();
                try {
                    json.put("password", password);
                    json.put("confirm", confPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.put(Consts.API_URL + "rest/account/password")
                        .setPriority(Priority.HIGH)
                        .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                        .addHeaders(Consts.API_SESSION_KEY, session)
                        .addJSONObjectBody(json)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                Log.d(TAG, response.toString());

                                try {

                                    if (response.getInt("success") != 1) {
                                        Toast.makeText(ProfileActivity.this, "لم تتم العملية بنجاح", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

//                                    JSONObject data = response.getJSONObject("data");

                                    Toast.makeText(ProfileActivity.this, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            @Override
                            public void onError(ANError error) {
                                progressDialog.dismiss();
                                error.printStackTrace();
                                Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void fetchData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/account/account")
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
                                Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject data = response.getJSONObject("data");

                            user = User.fromJson(data);

                            cbNews.setChecked(user.getNewsletter() == 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void doLogout() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        new DbHelper(this).deleteAllProducts();

        AndroidNetworking.post(Consts.API_URL + "rest/logout/logout")
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
                                Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this)
                                    .edit()
                                    .clear()
                                    .apply();

                            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(ProfileActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
