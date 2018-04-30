package com.yuxie.smsbomb.db;


import com.yuxie.smsbomb.greendao.SmsApiDao;

/**
 * Created by luo on 2018/3/1.
 */

public class EntityManager {

    private static EntityManager entityManager;

    public SmsApiDao getSmsApiDao() {
        return DaoManager.getInstance().getSession().getSmsApiDao();
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
}
