package com.tonynowater.smallplayer.module.dto.realm;

import android.util.Log;

import com.tonynowater.smallplayer.util.Logger;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Realm版本變更處理
 *
 * Created by tonynowater on 2017/8/27.
 */
public class Migration implements RealmMigration {
    private static final String TAG = Migration.class.getSimpleName();
    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        Logger.getInstance().d(TAG, "migrate old version : " + oldVersion);
        Logger.getInstance().d(TAG, "migrate new version : " + newVersion);
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        /********************************************************
           Version 0 => 1 新增暫存使用者的Youtube播放清單U2BUserPlayListDTO
         ********************************************************/
        if (oldVersion == 0) {
            if (false == schema.contains("PlayUserU2BListEntity")) {
                RealmObjectSchema personSchema = schema.create("PlayUserU2BListEntity");
                // Combine 'firstName' and 'lastName' in a new field called 'fullName'
                personSchema
                        .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                        .addField("title", String.class, FieldAttribute.REQUIRED)
                        .addField("listId", String.class, FieldAttribute.REQUIRED);
                oldVersion++;
            }
        }
    }
}
