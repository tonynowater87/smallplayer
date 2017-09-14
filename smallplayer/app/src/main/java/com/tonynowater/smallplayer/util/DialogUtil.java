package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayUserU2BListEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.service.EqualizerType;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.io.IOException;
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
     * @param baseMediaControlActivity
     */
    public static void showChangePlayListDialog(final BaseMediaControlActivity baseMediaControlActivity) {
        final RealmUtils realmUtils = new RealmUtils();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseMediaControlActivity);
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayListSortByPosition();
        int currentPlaylistPosition = realmUtils.queryCurrentPlayListPosition();
        builder.title(R.string.change_play_list_dialog_title);
        builder.items(playListEntities);
        builder.itemsCallbackSingleChoice(currentPlaylistPosition,
                new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                int playListId = playListEntities.get(i).getId();
                realmUtils.setCurrentPlayListID(playListId);
                baseMediaControlActivity.sendActionChangePlaylist(playListId);
                realmUtils.close();
                return true;
            }
        });
        builder.show();
    }

    /**
     * 顯示將歌曲List全部加到某個清單的對話框
     * @param baseMediaControlActivity
     * @param playableList 歌曲List
     * @param listName 歌單名稱
     */
    public static void showAddPlayableListDialog(final BaseMediaControlActivity baseMediaControlActivity, final List<Playable> playableList, final String listName) {
        final RealmUtils realmUtils = new RealmUtils();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseMediaControlActivity);
        final List<PlayListEntity> playListEntities = realmUtils.queryAllPlayListSortByPosition();
        final List<String> listItems = MiscellaneousUtil.getPlayListTitles(playListEntities);
        listItems.add(0, String.format(baseMediaControlActivity.getString(R.string.add_playlist_by_search), listName));
        builder.title(R.string.add_playable_list_dialog_title);
        builder.content(String.format(baseMediaControlActivity.getString(R.string.add_playable_list_dialog_content), String.valueOf(playableList.size())));
        builder.items(listItems);
        builder.itemsCallbackSingleChoice(-1,
                new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                int playlistId;
                if (position == 0) {
                    //自動新建歌單
                    playlistId = realmUtils.addNewPlayList(listName);
                } else {
                    //加入已有歌單
                    playlistId = playListEntities.get(position - 1).getId();
                }

                realmUtils.addSongsToPlayList(playlistId, playableList);
                realmUtils.close();
                return true;
            }
        });
        builder.show();
    }

    /**
     * 顯示切換等化器風格的Dialog
     * @param baseMediaControlActivity
     * @param equalizerType
     * @param transportControls
     */
    public static void showChangeEqualizerDialog(final BaseMediaControlActivity baseMediaControlActivity, EqualizerType equalizerType, final MediaControllerCompat.TransportControls transportControls) {
        if (equalizerType == null) {
            baseMediaControlActivity.showToast(baseMediaControlActivity.getString(R.string.equlizer_can_not_set_when_not_play_toast_msg));
            return;
        }

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

        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseMediaControlActivity);
        builder.title(R.string.change_equalizer_dialog_title);
        builder.items(names);
        builder.itemsCallbackSingleChoice(currentEqPosition, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PlayMusicService.BUNDLE_KEY_EQUALIZER_TYPE, types[position]);
                transportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_EQUALIZER_TYPE,bundle);
                baseMediaControlActivity.showToast(String.format(baseMediaControlActivity.getString(R.string.equlizer_set_finish_toast), names[position]));
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

    /**
     * 顯示「選擇使用者的Youtube歌單匯入」的對話框
     * @param baseActivity
     */
    public static void showImportUserPlayListDialog(final BaseActivity baseActivity) {
        final RealmUtils realmUtils = new RealmUtils();
        final List<PlayUserU2BListEntity> userU2BListEntities = realmUtils.queryUserU2BPlayList();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(baseActivity);
        builder.title(baseActivity.getString(R.string.choose_user_youtube_playlist));
        builder.items(userU2BListEntities);
        builder.dividerColor(ContextCompat.getColor(baseActivity, android.R.color.darker_gray));//只有標題的分隔線
        builder.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                final String playlistName = userU2BListEntities.get(i).toString();
                Log.d(TAG, "onSelection: " + playlistName);
                U2BApi.newInstance().queryU2BPlayListVideo(userU2BListEntities.get(i).getListId(), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        baseActivity.showToast(String.format(baseActivity.getString(R.string.import_user_youtube_playlist_error_detail_msg), e.toString()));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String sResponse = response.body().string();
                            Log.d(TAG, "onResponse body: " + sResponse);
                            processPlayListVideoList(playlistName, sResponse);
                        } else {
                            baseActivity.showToast(baseActivity.getString(R.string.import_user_youtube_playlist_error_detail_msg));
                        }
                    }
                });
            }

            private void processPlayListVideoList(final String playlistName, String sResponse) {
                StringBuilder sVideoIds;
                final U2bPlayListVideoDTO mU2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
                sVideoIds = MiscellaneousUtil.getVideoIdsForQueryDuration(mU2bPlayListVideoDTO.getItems());
                U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        baseActivity.showToast(String.format(baseActivity.getString(R.string.import_user_youtube_playlist_error_detail_msg), e.toString()));
                    }

                    // FIXME: 2017/6/14 搜尋到一半，在搜尋會 java.lang.NullPointerException: Attempt to invoke virtual method 'java.util.List com.tonynowater.smallplayer.module.dto.U2BVideoDTO.getItems()' on a null object reference
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String sResponse = response.body().string();
                            Log.d(TAG, "onResponse body: " + sResponse);
                            U2BVideoDurationDTO u2BVideoDurationDTO = new Gson().fromJson(sResponse, U2BVideoDurationDTO.class);
                            MiscellaneousUtil.processDuration(u2BVideoDurationDTO, mU2bPlayListVideoDTO.getItems());
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int playlistId = realmUtils.addNewPlayList(playlistName);
                                    realmUtils.addSongsToPlayList(playlistId, mU2bPlayListVideoDTO.getItems());
                                    baseActivity.showToast(String.format(baseActivity.getString(R.string.import_user_youtube_playlist_success_detail_msg), playlistName));
                                    realmUtils.close();
                                }
                            });
                        } else {
                            baseActivity.showToast(baseActivity.getString(R.string.import_user_youtube_playlist_error_detail_msg));
                        }
                    }
                });
            }
        });
        builder.show();
    }
}
