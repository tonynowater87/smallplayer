package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.service.EqualizerType;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class DialogUtil {
    private static final String TAG = DialogUtil.class.getSimpleName();
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
     * 顯示Action的Dialog
     * @param context
     * @param title 對話框標題
     * @param actionList 對話框的動作列表
     * @param callback
     */
    public static void showActionDialog(Context context, String title,int actionList, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.items(actionList);
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
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayListSortByPosition();
        final List<PlayListSongEntity> songEntities = getSongInListCheckedList(playable, realmUtils);
        Integer[] checkList = getCheckedList(playListEntities, songEntities);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(playable.getPlayListSongEntity().getTitle());
        builder.items(playListEntities);
        builder.itemsCallbackMultiChoice(checkList, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {

                //先把要加入的歌曲全刪
                for (int i = 0; i < songEntities.size(); i++) {
                    realmUtils.deleteSongFromPlayList(songEntities.get(i));
                }

                //再把此次新增的歌曲加到各歌單裡
                PlayListEntity playListEntity;
                for (int i = 0; i < integers.length; i++) {
                    playListEntity = playListEntities.get(integers[i]);
                    Log.d(TAG, "onSelection : " + playListEntity.getPlayListName());
                    realmUtils.addSongToPlayList(playListEntity.getId(), playable.getPlayListSongEntity());
                    if (playListEntity.getId() == realmUtils.queryCurrentPlayListID()) {
                        //若有加入目前播放歌單，則更新目前播放歌單
                        Bundle bundle = new Bundle();
                        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playListEntity.getId());
                        transportControls.sendCustomAction(PlayMusicService.ACTION_ADD_SONG_TO_PLAYLIST, bundle);
                    }
                }

                // TODO: 2017/7/9 若在這邊刪除歌曲，要即時更新歌單歌曲


                realmUtils.close();
                return false;
            }
        });
        builder.positiveText(context.getString(android.R.string.ok));
        builder.positiveColor(context.getResources().getColor(R.color.colorAccent));
        builder.negativeText(context.getString(android.R.string.cancel));
        builder.show();
    }

    /**
     * @param playListEntities
     * @param playListSongEntities
     * @return 要加入的歌曲在哪幾個歌單的array
     */
    private static Integer[] getCheckedList(List<PlayListEntity> playListEntities, List<PlayListSongEntity> playListSongEntities) {
        List<Integer> checkedlist = new ArrayList<>();

        for (int i = 0; i < playListEntities.size(); i++) {
            for (int j = 0; j < playListSongEntities.size(); j++) {
                if (playListEntities.get(i).getId() == playListSongEntities.get(j).getListId()) {
                    checkedlist.add(i);
                    break;
                }
            }
        }

        return checkedlist.toArray(new Integer[checkedlist.size()]);
    }

    /**
     * @param playable
     * @param realmUtils
     * @return 要加入的歌的所有Entity，可知道歌曲已加在哪些歌單裡了
     */
    private static List<PlayListSongEntity> getSongInListCheckedList(Playable playable, RealmUtils realmUtils) {
        PlayListSongEntity playListSongEntity = playable.getPlayListSongEntity();
        List<PlayListSongEntity> allPlayListSong = realmUtils.queryAllPlayListSong();
        List<PlayListSongEntity> addSongInList = new ArrayList<>();
        for (int i = 0; i < allPlayListSong.size(); i++) {
            if (TextUtils.equals(playListSongEntity.getTitle(), allPlayListSong.get(i).getTitle())) {
                addSongInList.add(allPlayListSong.get(i));
            }
        }

        return addSongInList;
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
                baseActivity.sendActionChangePlaylist(playListId);
                realmUtils.close();
                return true;
            }
        });
        builder.show();
    }

    /**
     * 顯示切換等化器風格的Dialog
     * @param baseActivity
     * @param equalizerType
     * @param transportControls
     */
    public static void showChangeEqualizerDialog(final BaseActivity baseActivity, EqualizerType equalizerType, final MediaControllerCompat.TransportControls transportControls) {
        final EqualizerType[] types = EqualizerType.values();
        final String[] names = new String[types.length];
        int currentEqPosition = 0;
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getValue();
            if (equalizerType == types[i]) {
                currentEqPosition = i;
            }
        }

        if (equalizerType == null) {
            currentEqPosition = -1;
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseActivity);
        builder.title(R.string.change_equalizer_dialog_title);
        builder.items(names);
        builder.itemsCallbackSingleChoice(currentEqPosition, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PlayMusicService.BUNDLE_KEY_EQUALIZER_TYPE, types[position]);
                transportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_EQUALIZER_TYPE,bundle);
                Toast.makeText(baseActivity.getApplicationContext(), String.format(baseActivity.getString(R.string.equlizer_set_finish_toast), names[position]), Toast.LENGTH_SHORT).show();
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
