package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.module.dto.realm.PlayListDTO;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.u2b.Playable;

import java.util.List;

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

    public static void showActionDialog(Context context, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.items(R.array.action_list);
        builder.itemsCallback(callback);
        builder.show();
    }

    public static void showSelectPlaylistDialog(Context context, final Playable playable) {
        final List<PlayListDTO> playListDTOList = RealmUtils.queryAllPlayList();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.items(playListDTOList);
        builder.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                RealmUtils.addSongToPlayList(playListDTOList.get(i).getId(), playable.getPlayListSongDTO());
            }
        });
        builder.show();
    }
}
