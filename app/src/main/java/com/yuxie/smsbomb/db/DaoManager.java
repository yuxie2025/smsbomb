package com.yuxie.smsbomb.db;


import com.baselib.baseapp.BaseApplication;
import com.yuxie.smsbomb.greendao.DaoMaster;
import com.yuxie.smsbomb.greendao.DaoSession;

/**
 * Created by luo on 2018/3/1.
 */

public class DaoManager {
    private static DaoManager mInstance;
    private DaoSession mDaoSession;
    private DaoMaster mDaoMaster;

    private DaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(BaseApplication.getAppContext(), "my_db", null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        this.mDaoMaster = mDaoMaster;
    }

    public static DaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new DaoManager();
        }
        return mInstance;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }
}
