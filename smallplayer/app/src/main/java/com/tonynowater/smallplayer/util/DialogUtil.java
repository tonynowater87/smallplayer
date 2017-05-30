package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class DialogUtil {
    public static void showAddPlayListDialog(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.add_play_list_dialog_title);
        builder.input(context.getString(R.string.add_play_list_dialog_hint), "", false, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                RealmUtils.addNewPlayList(charSequence.toString());
            }
        });
        builder.show();
    }
}
