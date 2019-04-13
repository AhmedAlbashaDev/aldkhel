package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aldkhel.aldkhel.utils.Consts;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

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
    private String country;
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
        final EditText etAddress = findViewById(R.id.etAddress);
        final EditText etAddress2 = findViewById(R.id.etAddress2);
        final EditText etMail = findViewById(R.id.etMail);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etConfPassword = findViewById(R.id.etConfPassword);
        final Spinner spCountry = findViewById(R.id.spCountry);
        final Spinner spCity = findViewById(R.id.spCity);
        final AppCompatCheckBox cbNews = findViewById(R.id.cbNews);
        final AppCompatCheckBox cbAgree = findViewById(R.id.cbAgree);

        final Button bRegister = findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = etFirstName.getText().toString();
                sname = etSecondName.getText().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();
                company = etCompany.getText().toString();
                address = etAddress.getText().toString();
                address2 = etAddress2.getText().toString();
                mail = etMail.getText().toString();
                city = spCity.getSelectedItem().toString();
                country = spCountry.getSelectedItem().toString();
                password = etPassword.getText().toString();
                confPassword = etConfPassword.getText().toString();

                if (!password.equals(confPassword)) {
                    Toast.makeText(RegisterActivity.this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show();
                    return;
                }

                doRegister();
            }
        });

    }

    private void doRegister() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.dismiss();

        AndroidNetworking.post(Consts.API_URL + "write/login.php")
                .setPriority(Priority.HIGH)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

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
