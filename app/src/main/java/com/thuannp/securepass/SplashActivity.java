package com.thuannp.securepass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SplashActivity extends AppCompatActivity {
    MasterKey masterKey = null;
    SharedPreferences sharedPreferences = null;

    // Gradient on status bar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color_splash));
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences UIPref = getSharedPreferences(DefConstant.PREF_NAME, MODE_PRIVATE);
        if (UIPref.getBoolean(DefConstant.PREF_DARK, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_splash);
        TextView password_manager = findViewById(R.id.password_manager);
        password_manager.setText(R.string.app_name);
        new Handler().postDelayed(() -> {
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
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            final boolean askPasswordLaunchState = sharedPreferences.getBoolean(DefConstant.PREF_KEY_REQUIRED, false);
            final boolean firstRun = sharedPreferences.getBoolean(DefConstant.PREF_FIRST_RUN, true);
            if (firstRun) {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            } else {
                if (askPasswordLaunchState) {
                    startActivity(new Intent(SplashActivity.this, MLockActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.recommended_master_password, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }, DefConstant.SPLASH_TIME_OUT);
    }

}