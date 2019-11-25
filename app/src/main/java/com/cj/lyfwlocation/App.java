package com.cj.lyfwlocation;

import android.app.Application;
import android.content.Context;

/**
 * Author:chris - jason
 * Date:2019-11-20.
 * Package:com.cj.lyfwlocation
 */
public class App extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getmContext() {
        return mContext;
    }

}
