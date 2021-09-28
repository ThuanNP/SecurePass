package com.thuannp.securepass;

import static com.thuannp.securepass.utils.DefConstant.providersEmail;
import static com.thuannp.securepass.utils.DefConstant.providersSocial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;
import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ir.androidexception.roomdatabasebackupandrestore.AESUtils;

public class AddActivity extends AppCompatActivity{

    MasterKey masterKey = null;
    String providerNameString, passwordFromCOPY;
    Button add_button;
    Spinner providerName;
    TextView prov_tv;
    private EditText email, password;
    String provider;
    SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        providerName = findViewById(R.id.provider_name);
        email = findViewById(R.id.add_email);
        password = findViewById(R.id.add_password);
        CheckBox checkBox = findViewById(R.id.add_show_password);
        add_button = findViewById(R.id.add_record);
        prov_tv = findViewById(R.id.prov_tv);

        // Encrypted SharedPrefs
        try {
            //x.security
            masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            //init sharedPef
            sharedPreferences = EncryptedSharedPreferences.create(
                    getApplicationContext(),
                    DefConstant.PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else {
                password.setInputType(129);
            }
        });
        add_button.setOnClickListener(v -> save_data());
    }

    @Override
    protected void onStart() {
        super.onStart();
        provider = getIntent().getStringExtra(DefConstant.EXTRA_PROVIDER);
        if (provider == null) {
            provider = "mail";
        }
        passwordFromCOPY = getIntent().getStringExtra(DefConstant.PASSWORD);
        assert provider != null;
        switch (provider) {
            case "social":
                email.setHint("Email");
                password.setText(passwordFromCOPY);
                ArrayAdapter arrayAdapterSocial = new ArrayAdapter(this, android.R.layout.simple_spinner_item, providersSocial);
                arrayAdapterSocial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                providerName.setAdapter(arrayAdapterSocial);
                providerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        providerNameString = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "wifi":
                prov_tv.setVisibility(View.GONE);
                providerName.setVisibility(View.GONE);
                email.setHint("Network name");
                break;
            default:
                email.setHint("Username/Email");
                password.setText(passwordFromCOPY);
                ArrayAdapter arrayAdapterEmail = new ArrayAdapter(this, android.R.layout.simple_spinner_item, providersEmail);
                arrayAdapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                providerName.setAdapter(arrayAdapterEmail);
                providerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        providerNameString = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
        }
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.add_record) {
//            save_data();
//        }
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void save_data() {
        String text_email, text_password;
        text_email = email.getText().toString();
        text_password = password.getText().toString();
        String sha = sharedPreferences.getString("HASH", "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);

        // Check validated
        if (text_email.trim().isEmpty()) {
            email.setError("Required");
            email.requestFocus();
            return;
        }
        if (text_password.trim().isEmpty()) {
            password.setError("Required");
            password.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(DefConstant.EXTRA_PROVIDER_NAME, providerNameString);
        // AES UTILS ENC and DEC
        try {
            String encEmail = AESUtils.encrypt(text_email, HASH);
            String encPass = AESUtils.encrypt(text_password, HASH);
            intent.putExtra(DefConstant.EXTRA_EMAIL, encEmail);
            intent.putExtra(DefConstant.EXTRA_ENCRYPT, encPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}