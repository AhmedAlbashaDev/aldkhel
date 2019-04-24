package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.User;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UpdatePersonalActivity extends AppCompatActivity {

    private static final String TAG = "UpdatePersonalActivity";

    private String fname;
    private String sname;
    private String phone;
    private String email;
    private long id;

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
        setContentView(R.layout.activity_update_personal);

        session = Utils.getSessionId(this);

        final EditText etFirstName = findViewById(R.id.etFirstName);
        final EditText etSecondName = findViewById(R.id.etSecondName);
        final EditText etPhone = findViewById(R.id.etPhone);
        final EditText etEmail = findViewById(R.id.etEmail);


        user = Utils.loadUser(this);
        etFirstName.setText(user.getFirstName());
        etSecondName.setText(user.getLastName());
        etPhone.setText(user.getTelephone());
        etEmail.setText(user.getEmail());

        id = user.getId();


        findViewById(R.id.bUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = etFirstName.getText().toString();
                sname = etSecondName.getText().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();

                user.setFirstName(fname);
                user.setLastName(sname);
                user.setTelephone(phone);
                user.setEmail(email);

                update();

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
            json = user.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(Consts.API_URL + "rest/account/account")
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
                                Toast.makeText(UpdatePersonalActivity.this, "لم تتم العملية بنجاح", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Utils.saveUser(UpdatePersonalActivity.this, user);
                            Toast.makeText(UpdatePersonalActivity.this, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();
                            finish();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(UpdatePersonalActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
