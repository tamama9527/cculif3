package com.loveplusplus.update;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class UpdateChecker {


    public static void checkForDialog(Context context) {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                new CheckUpdateTask(context, Constants.TYPE_DIALOG, true).execute();
            }
        } else {
            Log.e(Constants.TAG, "The arg context is null");
        }
    }


    public static void checkForNotification(Context context) {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                new CheckUpdateTask(context, Constants.TYPE_NOTIFICATION, false).execute();
            }
        } else {
            Log.e(Constants.TAG, "The arg context is null");
        }

    }


}
