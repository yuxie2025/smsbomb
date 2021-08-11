package com.yuxie.smsbomb;

import android.content.Context;
import android.os.Environment;

import com.baselib.baseapp.BaseApplication;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/7/13.
 */

public class App extends BaseApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initData();
        Utils.init(this);
    }

    private void initData() {
        //腾讯Bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "c4c304173f", false);
    }

    public static Context getContext() {
        return mContext;
    }


}
