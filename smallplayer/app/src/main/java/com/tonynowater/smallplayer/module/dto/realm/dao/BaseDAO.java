package com.tonynowater.smallplayer.module.dto.realm.dao;

import android.util.Log;

import com.tonynowater.smallplayer.module.dto.realm.entity.EntityInterface;

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
public abstract class BaseDAO<T extends RealmObject & EntityInterface> implements Closeable{
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
     * 插入一筆新資料，自動產生遞增ID
     * @param T
     * @return
     */
    public int insert(T T) {
        T.setId(getNextKey());
        inserOrUpdate(T);
        return T.getId();
    }

    /**
     * 更新一筆資料
     * @return ID : 更新成功回傳
     *         -1 : 更新失敗
     */
    public int update(T T) {
        T entity = getQuery().equalTo(COLUMN_ID, T.getId()).findFirst();
        if (entity != null) {
            inserOrUpdate(T);
            return T.getId();
        } else {
            return -1;
        }
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
     * 刪除一筆資料
     * @return true : 刪除成功
     *         false : 刪除失敗
     */
    public boolean delete(T T) {
        T entity = getQuery().equalTo(COLUMN_ID, T.getId()).findFirst();
        boolean bSuccess = false;
        if (entity != null) {
            try {
                realm.beginTransaction();
                entity.deleteFromRealm();
                bSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bSuccess) {
                    realm.commitTransaction();
                } else {
                    realm.cancelTransaction();
                }
            }
        }

        return false;
    }

    /**
     * 刪除這個表的所有資料
     */
    public void deleteAll() {
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
    }

    public RealmQuery<T> getQuery() {
        return realm.where(clazz);
    }

    /**
     * 查詢
     * @param dbQueryResult 回傳結果是否要Copy
     * @param queryCondition 比對的條件
     * @param params 要比對的欄位
     * @return
     */
    public List<T> query(DBQueryResult dbQueryResult, DBQueryCondition queryCondition, Map<String, Object> params) {
        RealmQuery<T> query = getQuery();
        for (String key : params.keySet()) {
            Object value = params.get(key);

            switch (queryCondition) {
                case EqualTo:
                    query = equalTo(query, key, value);
                    break;
                case LessThan:
                    query = lessThan(query, key, value);
                    break;
                case LessThanOrEqualTo:
                    query = lessThanOrEqualTo(query, key, value);
                    break;
                case GreaterThan:
                    query = greaterThan(query, key, value);
                    break;
                case GreaterThanOrEqualTo:
                    query = greaterThanOrEqualTo(query, key, value);
                    break;
            }
        }

        switch (dbQueryResult) {
            case Copy:
                return copyFromRealm(query.findAll());
            case NotCopy:
                return query.findAll();
            default:
                return query.findAll();
        }
    }

    private RealmQuery<T> greaterThanOrEqualTo(RealmQuery<T> query, String key, Object value) {
        if (value instanceof Integer) {
            query = query.greaterThanOrEqualTo(key, (Integer) value);
        } else if (value instanceof Long) {
            query = query.greaterThanOrEqualTo(key, (Long) value);
        } else if (value instanceof Float) {
            query = query.greaterThanOrEqualTo(key, (Float) value);
        } else if (value instanceof Double) {
            query = query.greaterThanOrEqualTo(key, (Double) value);
        } else if (value instanceof Date) {
            query = query.greaterThanOrEqualTo(key, (Date) value);
        }
        return query;
    }

    private RealmQuery<T> greaterThan(RealmQuery<T> query, String key, Object value) {
        if (value instanceof Integer) {
            query = query.greaterThan(key, (Integer) value);
        } else if (value instanceof Long) {
            query = query.greaterThan(key, (Long) value);
        } else if (value instanceof Float) {
            query = query.greaterThan(key, (Float) value);
        } else if (value instanceof Double) {
            query = query.greaterThan(key, (Double) value);
        } else if (value instanceof Date) {
            query = query.greaterThan(key, (Date) value);
        }
        return query;
    }

    private RealmQuery<T> lessThanOrEqualTo(RealmQuery<T> query, String key, Object value) {
        if (value instanceof Integer) {
            query = query.lessThanOrEqualTo(key, (Integer) value);
        } else if (value instanceof Long) {
            query = query.lessThanOrEqualTo(key, (Long) value);
        } else if (value instanceof Float) {
            query = query.lessThanOrEqualTo(key, (Float) value);
        } else if (value instanceof Double) {
            query = query.lessThanOrEqualTo(key, (Double) value);
        } else if (value instanceof Date) {
            query = query.lessThanOrEqualTo(key, (Date) value);
        }
        return query;
    }

    private RealmQuery<T> lessThan(RealmQuery<T> query, String key, Object value) {
        if (value instanceof Integer) {
            query = query.lessThan(key, (Integer) value);
        } else if (value instanceof Long) {
            query = query.lessThan(key, (Long) value);
        } else if (value instanceof Float) {
            query = query.lessThan(key, (Float) value);
        } else if (value instanceof Double) {
            query = query.lessThan(key, (Double) value);
        } else if (value instanceof Date) {
            query = query.lessThan(key, (Date) value);
        }
        return query;
    }

    private RealmQuery<T> equalTo(RealmQuery<T> query, String key, Object value) {
        if (value instanceof String) {
            query = query.equalTo(key, (String) value);
        } else if (value instanceof Integer) {
            query = query.equalTo(key, (Integer) value);
        } else if (value instanceof Long) {
            query = query.equalTo(key, (Long) value);
        } else if (value instanceof Float) {
            query = query.equalTo(key, (Float) value);
        } else if (value instanceof Double) {
            query = query.equalTo(key, (Double) value);
        } else if (value instanceof Byte) {
            query = query.equalTo(key, (Byte) value);
        } else if (value instanceof Short) {
            query = query.equalTo(key, (Short) value);
        } else if (value instanceof Boolean) {
            query = query.equalTo(key, (Boolean) value);
        } else if (value instanceof Date) {
            query = query.equalTo(key, (Date) value);
        }
        return query;
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
