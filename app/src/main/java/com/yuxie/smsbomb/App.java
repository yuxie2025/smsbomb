package com.yuxie.smsbomb;

import android.content.Context;
import android.os.Environment;

import com.baselib.baseapp.BaseApplication;

/**
 * Created by Administrator on 2017/7/13.
 */

public class App extends BaseApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }


}
