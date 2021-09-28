package com.thuannp.securepass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE_PASS = "com.thuannp.SecurePass.EXTRA_TYPE_PASS";
    final String PREFS_NAME = "appEssentials";
    String TYPE_PASS_1 = "PIN";
    String TYPE_PASS_2 = "PASSWORD";
    String PREF = "HASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }
}