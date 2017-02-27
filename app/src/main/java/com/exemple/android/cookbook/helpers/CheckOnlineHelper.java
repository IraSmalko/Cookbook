package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckOnlineHelper {

    private Context mContext;

    public CheckOnlineHelper(Context context) {
        mContext = context;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
