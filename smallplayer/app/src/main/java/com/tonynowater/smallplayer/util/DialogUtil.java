package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.u2b.Playable;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class DialogUtil {
    public static void showAddPlayListDialog(Context context, MaterialDialog.InputCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.add_play_list_dialog_title);
        builder.input(context.getString(R.string.add_play_list_dialog_hint), "", false, callback);
        builder.show();
    }

    public static void showActionDialog(Context context, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.items(R.array.action_list);
        builder.itemsCallback(callback);
        builder.show();
    }

    public static void showSelectPlaylistDialog(Context context, final Playable playable) {
        final RealmUtils realmUtils = new RealmUtils();
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayList();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.items(playListEntities);
        builder.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                realmUtils.addSongToPlayList(playListEntities.get(i).getId(), playable.getPlayListSongEntity());
                realmUtils.close();
            }
        });
        builder.show();
    }
}
