package com.tonynowater.smallplayer.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.service.notification.ChannelConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by tonynowater on 2017/6/18.
 */
public class FileHelper extends AsyncTask<Void, Integer, Boolean> {
    private static final String TAG = FileHelper.class.getSimpleName();
    private static final int BYTE_READ = 2048;
    private static final String ACTION_STOP_DOWNLOAD = "com.tonynowater.smallplayer.stop_download";
    private static final int REQUEST_CODE = 100;
    private final String mPath;
    private final PendingIntent mStopDownloadIntent;
    private PlayListSongEntity mPlayListSongEntity;
    private String mFileName;
    private String mFileSuffix;
    private String sMimeType;
    private Response mResponse;
    private OnFileHelperCallback mCallback;
    private int mId;
    private BroadcastReceiver broadcastReceiver;

    public interface OnFileHelperCallback {
        void onSuccess(String msg);
        void onFailure();
    }

    public FileHelper(PlayListSongEntity mPlayListSongEntity, Response mResponse, OnFileHelperCallback mCallback) {
        this.mPlayListSongEntity = mPlayListSongEntity;
        this.mFileSuffix = mResponse.body().contentType().subtype();
        this.sMimeType = mResponse.body().contentType().toString();
        this.mFileName = mPlayListSongEntity.getTitle() + "." + mFileSuffix;
        this.mResponse = mResponse;
        this.mCallback = mCallback;
        mPath = getFilePath();
        mId = mPlayListSongEntity.hashCode();
        mStopDownloadIntent = PendingIntent.getBroadcast(MyApplication.getContext(), REQUEST_CODE, new Intent(ACTION_STOP_DOWNLOAD).setPackage(MyApplication.getContext().getPackageName()), PendingIntent.FLAG_ONE_SHOT);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (TextUtils.equals(intent.getAction(), ACTION_STOP_DOWNLOAD)) {
                    Log.d(TAG, "onReceive: stop download");
                    unregister();
                    NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(mPlayListSongEntity.getId());
                    FileHelper.this.cancel(true);
                }
            }
        };
        MyApplication.getContext().registerReceiver(broadcastReceiver, new IntentFilter(ACTION_STOP_DOWNLOAD));
    }

    private void unregister() {
        try {
            MyApplication.getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 存Mp3的路徑
     */
    private String getFilePath() {
        return String.format("%s%s%s%s%s%s%s%s"
                , Environment.getExternalStorageDirectory().getAbsolutePath()
                , File.separator
                , "Android"
                , File.separator
                , "data"
                , File.separator
                , MyApplication.getContext().getPackageName()
                , File.separator);
    }

    /**
     * 儲存Mp3
     * @return
     */
    private boolean saveFile() {
        boolean bRet = true;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //外部儲存空間不可用
            bRet = false;
        } else {

            String filePath = mPath + mFileName;

            Log.d(TAG, "saveFile mPath : " + mPath);
            Log.d(TAG, "saveFile file : " + filePath);
            File file = new File(mPath);

            Log.d(TAG, "saveFile isDir : " + file.isDirectory());

            if (!file.isDirectory()) {
                Log.d(TAG, "saveFile createDir : " + filePath);
                file.mkdir();
            }

            file = new File(filePath);

            try {
                long startTime = System.currentTimeMillis();
                BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                BufferedSource bufferedSource = mResponse.body().source();
                handleWrites(bufferedSink, bufferedSource, mResponse.body().contentLength());
                MiscellaneousUtil.calcRunningTime("下載"+mFileName,startTime);
                bufferedSink.writeAll(bufferedSource);
                bufferedSink.flush();
                bufferedSink.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                bRet = false;
                Log.e(TAG, "saveFile : " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                bRet = false;
                Log.e(TAG, "saveFile : " + e.getMessage());
            } finally {
                if (!bRet) {
                    if (file.exists()) {
                        Log.e(TAG, "saveFile exist false delete file : " + mPath + mFileName);
                        file.delete();
                    }
                }
            }

        }

        Log.d(TAG, "saveFile save success : " + bRet);
        return bRet;
    }

    /**
     *
     * @param bufferedSink
     * @param source
     * @param contentLength
     */
    private void handleWrites(BufferedSink bufferedSink, BufferedSource source, long contentLength) throws IOException {
        long completeCount = 0;
        long readCount = 0;
        long limit = 10000;
        Log.d(TAG, "handleWrites contentLengh : " + contentLength);
        while (readCount != -1) {
            readCount = source.read(bufferedSink.buffer(), BYTE_READ);
            completeCount += readCount;

            //show every 50kb
            if (completeCount > limit) {
                limit += 50000;
                Log.d(TAG, "handleWrites total count : " + completeCount);
                publishProgress((int) (100 * completeCount/contentLength));
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d(TAG, "onProgressUpdate: " + values[0]);
        showProgressNotification(values[0]);
    }

    @Override
    protected Boolean doInBackground(Void[] objects) {
        return saveFile();
    }

    @Override
    protected void onPostExecute(Boolean bResult) {
        Log.d(TAG, "onPostExecute: " + bResult);
        dismissProgressNotification();
        unregister();
        if (bResult) {
            addFileToContentProvider();
            //addAlbumToContentProvider();
            MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mPath))));
            mCallback.onSuccess(String.format(MyApplication.getContext().getString(R.string.downloadMP3_success_msg), mFileName));
        } else {
            mCallback.onFailure();
        }
    }

    private void addFileToContentProvider() {
        ContentValues values = new ContentValues(8);
        values.put(MediaStore.Audio.Media._ID, mId);
        values.put(MediaStore.Audio.Media.ARTIST, mPlayListSongEntity.getArtist());
        values.put(MediaStore.Audio.Media.TITLE, mPlayListSongEntity.getTitle());
        values.put(MediaStore.Audio.Media.DURATION, mPlayListSongEntity.getDuration());
        values.put(MediaStore.Audio.Media.MIME_TYPE, sMimeType);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.DATA, mPath + mFileName);
        values.put(MediaStore.Audio.Media.ALBUM_ID, mId);
        MyApplication.getContext().getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
    }

    // FIXME: 2017/6/18 這邊加入Album 會 java.lang.UnsupportedOperationException: Invalid URI content://media/external/audio/albums
    private void addAlbumToContentProvider() {
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Albums._ID, mId);
        values.put(MediaStore.Audio.Albums.ALBUM_ART, mPlayListSongEntity.getAlbumArtUri());
        values.put(MediaStore.Audio.Albums.ALBUM_KEY, mId);
        values.put(MediaStore.Audio.Albums.ARTIST, mPlayListSongEntity.getArtist());
        MyApplication.getContext().getContentResolver().insert(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 關閉下載百分比的通知
     */
    private void dismissProgressNotification() {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mPlayListSongEntity.getId());
    }

    /**
     * 顯示下載百分比的通知
     * @param percent 百分比
     */
    private void showProgressNotification(int percent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext(), ChannelConstant.DEFAULT_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setContentTitle(MyApplication.getContext().getString(R.string.app_name) + " " + MyApplication.getContext().getString(R.string.downloadMP3_ing_msg))
                .setContentText(mFileName)
                .setAutoCancel(false)
                .setProgress(100, percent, false)
                .setOngoing(true)//設置下載的通知不可被滑掉。
                .addAction(android.R.drawable.ic_media_pause, MyApplication.getMyString(R.string.downloadMP3_cancel_msg), mStopDownloadIntent);

        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mPlayListSongEntity.getId(), builder.build());
    }
}
