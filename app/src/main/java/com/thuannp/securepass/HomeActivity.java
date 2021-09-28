package com.thuannp.securepass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.thuannp.securepass.utils.DefConstant;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String COLLECTION = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-";

    AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;
    MasterKey masterKey = null;
    boolean secureCodeModeState;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getIntent().setAction("1");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        ImageButton imageButton = view.findViewById(R.id.refresh);
        final TextView textView1 = view.findViewById(R.id.generate_password);
        builder = new AlertDialog.Builder(this);
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
        secureCodeModeState = sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false);
        if (secureCodeModeState) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_password,
                R.id.nav_social,
                R.id.nav_wifi).setOpenableLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //Initial random password gen
        String Password = generatePassword();
        textView1.setText(Password);

        imageButton.setOnClickListener(v -> nav_refresh());
    }

    private void nav_refresh() {
        TextView textView = findViewById(R.id.generate_password);
        String Password = generatePassword();
        textView.setText(Password);
    }

    private String generatePassword() {
        //Creating Random object
        Random random = new Random();
        //Limiting the length of the generated password between 8 to 14
        int limit = (int) (Math.random() * 14 + 8);
        StringBuilder password = new StringBuilder();
        for (int itr = 0; itr < limit; itr++) {
            password.append(COLLECTION.charAt(random.nextInt(COLLECTION.length())));
        }
        return password.toString();
    }

    public void copy(View view) {
        if (sharedPreferences.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false)) {
            ImageButton copyImage = findViewById(R.id.copy);
            copyImage.setEnabled(false);
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();
        } else {
            TextView textView = findViewById(R.id.generate_password);
            final String gn_password = textView.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", gn_password);
            if (clip != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                break;
            case R.id.action_help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.action_logout:
                startActivity(new Intent(getApplicationContext(), MLockActivity.class));
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}