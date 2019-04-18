package com.meivaldi.youlanda.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    public int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "YoulandaLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_IS_INITIALIZED = "isInitialized";
    public static final String KEY_STARTER = "modal";
    public static final String KEY_FINAL = "modal_akhir";
    public static final String KEY_TRANSACTION = "transaksi";
    public static final String KEY_PRODUCT = "produk";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putBoolean(KEY_IS_INITIALIZED, false);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setInitialized(boolean value) {
        editor.putBoolean(KEY_IS_INITIALIZED, value);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setStarter(int modal) {
        editor.putInt(KEY_STARTER, modal);
        editor.putInt(KEY_FINAL, 0);
        editor.putInt(KEY_TRANSACTION, 0);
        editor.putInt(KEY_PRODUCT, 0);
        editor.commit();
    }
}
