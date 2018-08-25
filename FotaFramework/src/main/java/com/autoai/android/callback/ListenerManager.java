package com.autoai.android.callback;

import android.os.Bundle;
import android.os.SystemClock;

import java.util.Hashtable;

/**
 * Created by wangyanchao on 2018/8/24.
 */

public class ListenerManager {
//    private static final String TAG = "ListenerManager";

    private volatile static ListenerManager SINGLETANCE;

    private Hashtable<String, InnerListener> mCallBacks;

    private ListenerManager() {
        this.mCallBacks = new Hashtable<String, InnerListener>();
    }

    public static ListenerManager getInstance() {
        if (SINGLETANCE == null) {
            synchronized (ListenerManager.class) {
                if (SINGLETANCE == null) {
                    SINGLETANCE = new ListenerManager();
                }
            }
        }
        return SINGLETANCE;
    }

    public synchronized String addListener(InnerListener listener) {
        if (mCallBacks != null) {
            String key = String.valueOf(SystemClock.currentThreadTimeMillis());
            mCallBacks.put(key, listener);
            return key;
        }
        return null;
    }

    public synchronized void removeAllListener() {
        mCallBacks.clear();
    }

    public synchronized void sendCall(String action, Bundle bundle) {
        if (mCallBacks != null && mCallBacks.size() > 0) {
            for (String key : mCallBacks.keySet()) {
                InnerListener listener = mCallBacks.get(key);
                if (listener != null) {
                    listener.onReceive(action, bundle);
                }
            }
        }
    }

}
