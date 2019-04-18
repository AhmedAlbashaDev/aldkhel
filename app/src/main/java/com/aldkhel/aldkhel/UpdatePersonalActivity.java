package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aldkhel.aldkhel.utils.Consts;
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

        final EditText etFirstName = findViewById(R.id.etFirstName);
        final EditText etSecondName = findViewById(R.id.etSecondName);
        final EditText etPhone = findViewById(R.id.etPhone);
        final EditText etEmail = findViewById(R.id.etEmail);

        try {


            JSONObject json = new JSONObject(getIntent().getStringExtra("json"));
            etFirstName.setText(json.getString("firstname"));
            etSecondName.setText(json.getString("lastname"));
            etPhone.setText(json.getString("telephone"));
            etEmail.setText(json.getString("email"));

            id = json.getLong("customer_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        findViewById(R.id.bUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = etFirstName.getText().toString();
                sname = etSecondName.getText().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();

                update();

            }
        });

    }

    private void update() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.post(Consts.API_URL + "write/update_personal.php")
                .setPriority(Priority.HIGH)
                .addBodyParameter("firstname", fname)
                .addBodyParameter("lastname", sname)
                .addBodyParameter("email", email)
                .addBodyParameter("telephone", phone)
                .addBodyParameter("customer_id", id+"")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getLong("data") > 0) {
                                Toast.makeText(UpdatePersonalActivity.this, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(UpdatePersonalActivity.this, "لم تتم العملية بنجاح", Toast.LENGTH_SHORT).show();
                            }

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
