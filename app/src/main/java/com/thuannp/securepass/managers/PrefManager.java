package com.thuannp.securepass.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.thuannp.securepass.utils.DefConstant;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(DefConstant.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(DefConstant.PREF_FIRST_RUN, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        boolean isFirstRun = pref.getBoolean(DefConstant.PREF_FIRST_RUN, false);
        return isFirstRun;
    }

    public boolean isPasswordRequired() {
        boolean isPasswordRequired = pref.getBoolean(DefConstant.PREF_KEY_REQUIRED, false);
        return isPasswordRequired;
    }

}
