package com.garage.myapplication.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Chanatipp24 on 3/7/2560.
 */

public class UserHelper {
    Context context;
    SharedPreferences sharedPerfs;
    SharedPreferences.Editor editor;

    // Prefs Keys
    static String perfsName = "com.garage.login.UserHelper";
    static int perfsMode = 0;

    public UserHelper(Context context) {
        this.context = context;
        this.sharedPerfs = this.context.getSharedPreferences(perfsName, perfsMode);
        this.editor = sharedPerfs.edit();
    }

    public void createSession(String sMemberID) {

        editor.putBoolean("LoginStatus", true);
        editor.putString("MemberID", sMemberID);

        editor.commit();
    }

    public void deleteSession() {
        editor.clear();
        editor.commit();
    }

    public boolean getLoginStatus() {
        return sharedPerfs.getBoolean("LoginStatus", false);
    }

    public String getMemberID() {
        return sharedPerfs.getString("MemberID", null);
    }
}
