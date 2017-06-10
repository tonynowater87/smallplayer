package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class DialogUtil {

    /**
     * 顯示單欄輸入的Dialog
     * @param context
     * @param callback
     */
    public static void showInputDialog(Context context, String title, String hint, MaterialDialog.InputCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.input(hint, "", false, callback);
        builder.show();
    }

    /**
     * 顯示單欄輸入的Dialog
     * @param context
     * @param callback
     */
    public static void showInputDialog(Context context, String title, String hint, String prefill, MaterialDialog.InputCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.input(hint, prefill, false, callback);
        builder.show();
    }

    /**
     * 顯示播放及加入歌單的Dialog
     * @param context
     * @param callback
     */
    public static void showActionDialog(Context context, String title, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.items(R.array.action_list);
        builder.itemsCallback(callback);
        builder.show();
    }

    /**
     * 顯示加入歌曲至歌單的Dialog
     * @param context
     * @param playable
     */
    public static void showSelectPlaylistDialog(Context context, final Playable playable, final MediaControllerCompat.TransportControls transportControls) {
        final RealmUtils realmUtils = new RealmUtils();
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayList();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(playable.getPlayListSongEntity().getTitle());
        builder.items(playListEntities);
        builder.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                int playlistID = playListEntities.get(i).getId();
                realmUtils.addSongToPlayList(playlistID, playable.getPlayListSongEntity());
                realmUtils.close();
                Bundle bundle = new Bundle();
                bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playlistID);
                transportControls.sendCustomAction(PlayMusicService.ACTION_ADD_SONG_TO_PLAYLIST, bundle);
            }
        });
        builder.show();
    }

    /**
     * 顯示切換歌單的Dialog
     * @param baseActivity
     */
    public static void showChangePlayListDialog(final BaseActivity baseActivity) {
        final RealmUtils realmUtils = new RealmUtils();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseActivity);
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayListSortByPosition();
        int currentPlaylistPosition = realmUtils.queryCurrentPlayListPosition();
        builder.title(R.string.change_play_list_dialog_title);
        builder.items(playListEntities);
        builder.itemsCallbackSingleChoice(currentPlaylistPosition, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                int playListId = playListEntities.get(i).getId();
                realmUtils.setCurrentPlayListID(playListId);
                baseActivity.sendMetaDataToService(playListId);
                realmUtils.close();
                return true;
            }
        });
        builder.show();
    }

    /**
     * 顯示確定/取消對話框
     * @param context
     * @param title
     * @param singleButtonCallback
     */
    public static void showYesNoDialog(Context context, String title, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.positiveText(context.getString(android.R.string.ok));
        builder.negativeText(context.getString(android.R.string.cancel));
        builder.onPositive(singleButtonCallback);
        builder.onNegative(singleButtonCallback);
        builder.show();
    }

    /**
     * 顯示確定/取消對話框
     * @param context
     * @param title
     * @param singleButtonCallback
     */
    public static void showYesNoDialog(Context context, String title, MaterialDialog.SingleButtonCallback singleButtonCallback, DialogInterface.OnDismissListener dismissListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.positiveText(context.getString(android.R.string.ok));
        builder.negativeText(context.getString(android.R.string.cancel));
        builder.onPositive(singleButtonCallback);
        builder.onNegative(singleButtonCallback);
        builder.dismissListener(dismissListener);
        builder.show();
    }

    /**
     * 顯示警示訊息對話框
     * @param context
     * @param title
     * @param msg
     */
    public static void showMessageDialog(Context context, String title, String msg) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(msg);
        builder.positiveText(context.getString(android.R.string.ok));
        builder.show();
    }

    /**
     * 顯示警示訊息對話框
     * @param context
     * @param title
     * @param msg
     * @param callback
     */
    public static void showMessageDialog(Context context, String title, String msg, MaterialDialog.SingleButtonCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(msg);
        builder.positiveText(context.getString(android.R.string.ok));
        builder.onPositive(callback);
        builder.show();
    }
}
