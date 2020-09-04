package com.rebook.automart.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yemyatthu on 12/3/14.
 */
public class NetService {
  public static boolean isInternetAvailable(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) {
      return false;
    }
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null && ni.isAvailable() && ni.isConnected()) {
      return true;
    }
    return false;
  }
}
