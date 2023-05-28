package com.hbgtx.hola.utils;

import android.content.Context;
import android.widget.Toast;

import com.hbgtx.hola.activities.MainActivity;

public class Util {

    public static boolean isValidUserId(String userId) {
        return userId != null && userId.length() >= 3;
    }

    public static boolean isValidMessage(String message) {
        return message.trim().length() > 0;
    }

    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
