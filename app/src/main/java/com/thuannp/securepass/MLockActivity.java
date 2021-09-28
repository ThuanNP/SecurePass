package com.thuannp.securepass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MLockActivity extends AppCompatActivity {

    TextView mLock_tv_greet, mLock_tv_pp;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    MasterKey masterKey = null;
    SharedPreferences sharedPreferences = null;

    // Gradient on status bar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_mlock);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPromptInit();
        }

        mLock_tv_greet = findViewById(R.id.master_lock_l_tv_greet);
        mLock_tv_pp = findViewById(R.id.master_lock_l_tv_greet);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView = findViewById(R.id.pin_lock_view);

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

        //First time then change Text
        if (sharedPreferences.getBoolean(DefConstant.PREF_FIRST_RUN, true)
                || sharedPreferences.getString(DefConstant.PREF_HASH, "").isEmpty()  ) {
            // Do first run stuff here then set 'first run time' as false
            // using the following line to edit/commit prefs
            mLock_tv_greet.setText(R.string.master_lock_st_create_password);
            //Setting BIO_AUTH button to GONE
            findViewById(R.id.biometric_login).setVisibility(View.GONE);

        }
        PinLockListener mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                final boolean askPasswordLaunchState = sharedPreferences.getBoolean(DefConstant.PREF_KEY_REQUIRED, false);
                if (askPasswordLaunchState) {
                    String hashPIN = sharedPreferences.getString(DefConstant.PREF_HASH, "");
                    if (hashPIN.isEmpty()) {
                        sharedPreferences.edit().putString(DefConstant.PREF_HASH, pin).apply();
                        Toast.makeText(getApplicationContext(), "Master password created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    } else if (hashPIN.equals(pin)){
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    } else {
                        mPinLockView.resetPinLockView();
                        Toast.makeText(getApplicationContext(), "Login unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.recommended_master_password, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        };

        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void biometricPromptInit() {
        //Create a thread pool with a single thread
        Executor executor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = MLockActivity.this;
        //Start listening for authentication events
        final BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(MLockActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        //Create the BiometricPrompt instance//
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                //Add some text to the dialog//
                .setTitle(getString(R.string.biometric_prompt_title))
                .setDescription(getString(R.string.biometric_prompt_description))
                .setNegativeButtonText(getString(R.string.biometric_prompt_button_cancel))
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        Button biometricLoginButton = findViewById(R.id.biometric_login);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
}