package com.thuannp.securepass;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;
import com.thuannp.securepass.adapters.RecyclerViewAdapter;
import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ir.androidexception.roomdatabasebackupandrestore.AESUtils;

public class ModifyActivity extends AppCompatActivity {
    public static final String TAG = "MODIFY";
    EditText newPassword;
    EditText emailText, oldPassword;
    String provName, email, passwd, decPass;
    CheckBox show_change_password, show_password;
    MasterKey masterKey = null;
    Button changePasswordButton, updateBtn, deleteBtn, cancelBtn, saveBtn;
    SharedPreferences sharedPreferences = null;
    LinearLayout newPasswordLayout;
    RecyclerViewAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        emailText = findViewById(R.id.modify_email);
        oldPassword = findViewById(R.id.modify_old_password);
        show_password = findViewById(R.id.show_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        newPassword = findViewById(R.id.modify_new_password);
        show_change_password = findViewById(R.id.modify_show_password);
        updateBtn = findViewById(R.id.modify_update);
        deleteBtn = findViewById(R.id.modify_delete);
        cancelBtn = findViewById(R.id.modify_cancel);
        saveBtn = findViewById(R.id.modify_save);
//        updateBtn.setEnabled(false);
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
        show_data();
        show_password.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                oldPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else {
                oldPassword.setInputType(129);
            }
        });

        show_change_password.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else {
                newPassword.setInputType(129);
            }
        });
        changePasswordButton.setOnClickListener(v -> changePassword());
        updateBtn.setOnClickListener(v -> changeEmail());
        cancelBtn.setOnClickListener(v -> changeCancel());
        saveBtn.setOnClickListener(v -> modify_data());
        deleteBtn.setOnClickListener(v -> delete_data());
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.modify_update && updateBtn.isEnabled()) {
//            modify_data();
//        } else if (v.getId() == R.id.modify_delete) {
//            delete_data();
//        } else if (v.getId() == R.id.change_password_button) {
//            changePassword();
//        }
//    }

    private void show_data() {
        String sha = sharedPreferences.getString(DefConstant.PREF_HASH, "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);
        //DECRYPT
        Intent intent = getIntent();
        provName = intent.getStringExtra(DefConstant.EXTRA_PROVIDER_NAME);
        email = intent.getStringExtra(DefConstant.EXTRA_EMAIL);
        passwd = intent.getStringExtra(DefConstant.EXTRA_ENCRYPT);
        try {
            String decEmail = AESUtils.decrypt(email, HASH);
            decPass = AESUtils.decrypt(passwd, HASH);

            emailText.setText(decEmail);
            oldPassword.setText(decPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modify_data() {
        String text_old_password, text_new_password;
        text_old_password = oldPassword.getText().toString();
        text_new_password = newPassword.getText().toString();
        String sha = sharedPreferences.getString(DefConstant.PREF_HASH, "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);
        if (text_old_password.trim().isEmpty()) {
            oldPassword.setError("Email Required");
            oldPassword.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(DefConstant.EXTRA_PROVIDER_NAME, provName);
        if (findViewById(R.id.layout_confirm_password).getVisibility() == View.VISIBLE) {
            if (text_new_password.trim().isEmpty()) {
                newPassword.setError("Required");
                newPassword.requestFocus();
                return;
            }
            if (text_new_password.trim().equals(text_old_password.trim().isEmpty())) {
                newPassword.setError("Confirm Password is not match");
                newPassword.requestFocus();
                return;
            }
        }
        try {
            String encPass = AESUtils.encrypt(text_old_password, HASH);
            intent.putExtra(DefConstant.EXTRA_ENCRYPT, encPass);
            String encMail = AESUtils.encrypt(emailText.getText().toString().trim(), HASH);
            intent.putExtra(DefConstant.EXTRA_EMAIL, encMail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id = getIntent().getIntExtra(DefConstant.EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(DefConstant.EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void delete_data() {
        Intent intent = new Intent();
        intent.putExtra(DefConstant.EXTRA_DELETE, true);
        intent.putExtra(DefConstant.EXTRA_EMAIL, email);
        intent.putExtra(DefConstant.EXTRA_ENCRYPT, passwd);
        int id = getIntent().getIntExtra(DefConstant.EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(DefConstant.EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void changeEmail() {
        updateBtn.setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        emailText.setEnabled(true);
    }

    private void changePassword() {
        updateBtn.setVisibility(View.GONE);
//        findViewById(R.id.show_password).setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.GONE);
        oldPassword.setEnabled(true);
        newPassword.setVisibility(View.VISIBLE);
        oldPassword.setText("");
        newPassword.setText("");
        newPasswordLayout = findViewById(R.id.layout_confirm_password);
        newPasswordLayout.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        show_change_password.setVisibility(View.VISIBLE);
    }

    private void changeCancel() {
        updateBtn.setVisibility(View.VISIBLE);
//        findViewById(R.id.show_password).setVisibility(View.VISIBLE);
        changePasswordButton.setVisibility(View.VISIBLE);
        deleteBtn.setVisibility(View.VISIBLE);
        oldPassword.setEnabled(false);
        emailText.setEnabled(false);
        newPassword.setVisibility(View.GONE);
        newPasswordLayout = findViewById(R.id.layout_confirm_password);
        newPasswordLayout.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        show_change_password.setVisibility(View.GONE);

        show_data();

    }

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

    public void copy_email(View view) {
        if (sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();
        } else {
            TextView textView = findViewById(R.id.modify_email);
            final String gn_email = textView.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email", gn_email);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Email Copied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copy_password(View view) {
        if (sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();
        } else {
            TextView textView = findViewById(R.id.modify_old_password);
            final String gn_password = textView.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", gn_password);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Password Copied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}