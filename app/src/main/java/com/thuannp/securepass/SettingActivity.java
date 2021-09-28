package com.thuannp.securepass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.thuannp.securepass.room.CredDB;
import com.thuannp.securepass.ui.password.PasswordViewModel;
import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ir.androidexception.roomdatabasebackupandrestore.Backup;
import ir.androidexception.roomdatabasebackupandrestore.OnWorkFinishListener;
import ir.androidexception.roomdatabasebackupandrestore.Restore;


public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences = null;
    SharedPreferences UIPref;
    String TYPE_PASS_1 = "PIN";
    String TYPE_PASS_2 = "PASSWORD";
    String NO_DATA = "NO DATA";
    MasterKey masterKey = null;
    TextView change_password, delete_data, about_app;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

        change_password = findViewById(R.id.change_master_password);
        delete_data = findViewById(R.id.delete_all_data);
        about_app = findViewById(R.id.about_app);
        progressBar = findViewById(R.id.progress_bar);
        final SwitchMaterial askPasswordLaunchSwitch = findViewById(R.id.ask_password_launch);
        final SwitchMaterial secureCoreModeSwitch = findViewById(R.id.secure_core_mode);
        final SwitchMaterial darkThemeSwitch = findViewById(R.id.ask_dark_theme);

        final boolean secureCodeModeState = sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false);
        secureCoreModeSwitch.setChecked(secureCodeModeState);
        //Set password requirement
        final boolean askPasswordLaunchState = sharedPreferences.getBoolean(DefConstant.PREF_KEY_REQUIRED, false);
        askPasswordLaunchSwitch.setChecked(askPasswordLaunchState);
        if (askPasswordLaunchState) {
            change_password.setVisibility(View.VISIBLE);
        } else {
            change_password.setVisibility(View.GONE);
        }
        //Set theme mode
        UIPref = getSharedPreferences(DefConstant.PREF_NAME, MODE_PRIVATE);
        boolean onDarkTheme = UIPref.getBoolean(DefConstant.PREF_DARK, false);
        darkThemeSwitch.setChecked(onDarkTheme);

        final SharedPreferences.Editor UIEditor = UIPref.edit();
        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            UIEditor.putBoolean(DefConstant.PREF_DARK, isChecked).apply();
            if (isChecked) {
                // Enable Dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        askPasswordLaunchSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            askPassword(isChecked);
        });
        secureCoreModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            secureCodeMode(isChecked);
        });
    }

    private void secureCodeMode(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, state).apply();
        if (state) {
            //to do False
            //remove copy to clipboard and screenshot ability
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Success. Restart app to apply changes", Toast.LENGTH_LONG).show();
        } else {
            //to do true
            //set copy to clipboard and screenshot ability
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Secure code mode is inactive", Toast.LENGTH_LONG).show();
        }
    }

    private void askPassword(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DefConstant.PREF_KEY_REQUIRED, state).apply();

        if (state) {
            //to do False
            Toast.makeText(getApplicationContext(), "Password: ON", Toast.LENGTH_SHORT).show();
            change_password.setVisibility(View.VISIBLE);
        } else {
            //to do true
            Toast.makeText(getApplicationContext(), "Password: OFF", Toast.LENGTH_SHORT).show();
            change_password.setVisibility(View.GONE);
        }
    }

//    public void changePassword(View view) {
//        TextView PIN = findViewById(R.id.change_master_password_option_1);
//        //TODO Change to password disabled for now
//        //TextView Password = findViewById(R.id.change_master_password_option_2);
//        PIN.setVisibility(View.VISIBLE);
//        //Password.setVisibility(View.VISIBLE);
//    }

    public void changePasswordToPIN(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        intent.putExtra(ChangePasswordActivity.EXTRA_TYPE_PASS, TYPE_PASS_1);
        startActivity(intent);
    }

    public void changePasswordToPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        intent.putExtra(ChangePasswordActivity.EXTRA_TYPE_PASS, TYPE_PASS_2);
        startActivity(intent);
    }

    public void export_data(View view) {
        check_storage_perms(this);
        new Backup.Init()
                .database(CredDB.getInstance(this))
                .path("storage/emulated/0/")
                .fileName("securepass_backup.txt")
                .secretKey("FOEj6pr7CSaYlbJhE4N8aeQpbzEZJXH5") //optional https://randomkeygen.com
                .onWorkFinishListener((success, message) -> {
                    // do anything
                    if (message.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Restart app to sync your credentials", Toast.LENGTH_SHORT).show();
                    }
                })
                .execute();
    }

    public void restore_data(View view) {
        check_storage_perms(this);
        // Restore
        new Restore.Init()
                .database(CredDB.getInstance(this))
                .backupFilePath("storage/emulated/0/securepass_backup.txt")
                .secretKey("FOEj6pr7CSaYlbJhE4N8aeQpbzEZJXH5") // if your backup file is encrypted, this parameter is required
                .onWorkFinishListener((success, message) -> {
                    // do anything
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                })
                .execute();
    }

    public void delete_data(View view) {
        //AlertDialog START
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Delete Everything");
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Are you sure, You want to delete everything?");
        alertDialogBuilder.setCancelable(false);
        //Positive button
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PasswordViewModel passwordViewModel = new PasswordViewModel(getApplication());
                progressBar.setVisibility(View.VISIBLE);
                passwordViewModel.deleteAllNotes();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NO_DATA, false).apply();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //Negative button
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void show_about(View view) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), R.string.permission_required, Toast.LENGTH_LONG).show();
        }
    }

    public boolean check_storage_perms(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }
}