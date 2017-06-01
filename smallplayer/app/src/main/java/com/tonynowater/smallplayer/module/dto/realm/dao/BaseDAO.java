package com.tonynowater.smallplayer.module.dto.realm.dao;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by tonynowater on 2017/5/31.
 */
public abstract class BaseDAO<T extends RealmObject> implements Closeable{
    private static final String TAG = BaseDAO.class.getSimpleName();
    public static final int DEFAULT_ID = 0;

    public static final String COLUMN_ID = "id";

    protected Realm realm;
    protected Class<T> clazz;

    public BaseDAO(Class<T> clazz) {
        this.realm = Realm.getDefaultInstance();
        this.clazz = clazz;
    }

    /**
     * @return 查所有項目
     */
    public List<T> queryAll() {
        return realm.where(clazz).findAll();
    }

    /**
     * 插入一筆或更新一筆資料
     *
     * @param item
     */
    protected void inserOrUpdate(T item) {
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    /**
     * 刪除這個表的所有資料
     */
    public void deleteAll() {
        realm.beginTransaction();
        realm.delete(clazz);
    }

    public RealmQuery<T> getQuery() {
        return realm.where(clazz);
    }

    /**
     * 查詢加參數
     * @param params
     * @return
     */
    public List<T> queryForCopy(Map<String, Object> params) {
        RealmQuery<T> query = getQuery();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                query = query.equalTo(key, (String) value);
                continue;
            } else if (value instanceof Integer) {
                query = query.equalTo(key, (Integer) value);
                continue;
            } else if (value instanceof Long) {
                query = query.equalTo(key, (Long) value);
                continue;
            } else if (value instanceof Float) {
                query = query.equalTo(key, (Float) value);
                continue;
            } else if (value instanceof Double) {
                query = query.equalTo(key, (Double) value);
                continue;
            } else if (value instanceof Byte) {
                query = query.equalTo(key, (Byte) value);
                continue;
            } else if (value instanceof Short) {
                query = query.equalTo(key, (Short) value);
                continue;
            } else if (value instanceof Boolean) {
                query = query.equalTo(key, (Boolean) value);
                continue;
            } else if (value instanceof Date) {
                query = query.equalTo(key, (Date) value);
                continue;
            }
        }

        return copyFromRealm(query.findAll());
    }

    /**
     * 查詢加參數
     * @param params
     * @return
     */
    public List<T> queryNotCopy(Map<String, Object> params) {
        RealmQuery<T> query = getQuery();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                query = query.equalTo(key, (String) value);
                continue;
            } else if (value instanceof Integer) {
                query = query.equalTo(key, (Integer) value);
                continue;
            } else if (value instanceof Long) {
                query = query.equalTo(key, (Long) value);
                continue;
            } else if (value instanceof Float) {
                query = query.equalTo(key, (Float) value);
                continue;
            } else if (value instanceof Double) {
                query = query.equalTo(key, (Double) value);
                continue;
            } else if (value instanceof Byte) {
                query = query.equalTo(key, (Byte) value);
                continue;
            } else if (value instanceof Short) {
                query = query.equalTo(key, (Short) value);
                continue;
            } else if (value instanceof Boolean) {
                query = query.equalTo(key, (Boolean) value);
                continue;
            } else if (value instanceof Date) {
                query = query.equalTo(key, (Date) value);
                continue;
            }
        }

        return query.findAll();
    }

    /**
     * @param realmResults
     * @return 查詢的結果複制一份
     */
    public List<T> copyFromRealm(RealmResults<T> realmResults){
        return realm.copyFromRealm(realmResults);
    }

    public T copyFromReal(T realmObject) {
        return realm.copyFromRealm(realmObject);
    }

    /**
     * 使用完畢要呼叫釋放資料放memory leak
     */
    @Override
    public void close() throws IOException {
        try {
            realm.close();
        } catch (Exception e) {
            throw e;
        } finally {
            realm = null;
        }
    }

    /**
     * @return 自動遞增的ID
     */
    protected int getNextKey() {

        if (realm.where(clazz).max(COLUMN_ID) == null) {
            Log.d(TAG, "getNextKey: null" );
            return DEFAULT_ID;
        } else {
            int id = realm.where(clazz).max(COLUMN_ID).intValue() + 1;
            Log.d(TAG, "getNextKey:" + id );
            return id;
        }
    }
}
