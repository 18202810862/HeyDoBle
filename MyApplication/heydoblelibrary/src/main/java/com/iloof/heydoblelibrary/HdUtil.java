package com.iloof.heydoblelibrary;

import android.content.Context;
import android.content.Intent;

import java.util.Map;
import java.util.Set;

/**
 * Created by chentao on 2017/12/11.
 */

public class HdUtil {

    /**
     * 发送广播
     * @param context
     * @param what intent action
     * @param params intent params
     */
    public static void broadcast(Context context, String what,
                                 Map<String, String> params) {
        Intent intent = new Intent(what);
        if (params != null) {
            Set<String> keys = params.keySet();
            for (String string : keys) {
                intent.putExtra(string, params.get(string));
            }
        }
        context.sendBroadcast(intent);
    }
}
