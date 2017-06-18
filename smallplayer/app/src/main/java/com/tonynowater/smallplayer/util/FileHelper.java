package com.tonynowater.smallplayer.util;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

// TODO: 2017/6/18 下載MP3還需要另外做通知顯示下載中的狀態
/**
 * Created by tonynowater on 2017/6/18.
 */
public class FileHelper extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = FileHelper.class.getSimpleName();
    private static final int BYTE_READ = 1024 * 8;
    private final String mPath;
    private final String mp3suffix = ".mp3";
    private PlayListSongEntity mPlayListSongEntity;
    private String mFileName;
    private InputStream mInputStream;
    private OnFileHelperCallback mCallback;
    private int mId;

    public interface OnFileHelperCallback {
        void onSuccess(String msg);
        void onFailure();
    }

    public FileHelper(PlayListSongEntity mPlayListSongEntity, InputStream mInputStream, OnFileHelperCallback mCallback) {
        this.mPlayListSongEntity = mPlayListSongEntity;
        this.mFileName = mPlayListSongEntity.getTitle() + mp3suffix;
        this.mInputStream = mInputStream;
        this.mCallback = mCallback;
        mPath = getFilePath();
        mId = (mPlayListSongEntity.getTitle() + mPlayListSongEntity.getId()).hashCode();
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
    public boolean saveFile() {
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

            if (file.exists()) {
                Log.d(TAG, "saveFile exist : " + filePath);
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.TAIWAN);
                file = new File(mPath + filePath + calendar.getTime().toString());
            }

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[BYTE_READ];
                int available = mInputStream.available();
                Log.d(TAG, "saveFile available : " + available);
                long startTime = System.currentTimeMillis();
                int sum = 0;
                int len;
                while ((len = mInputStream.read(bytes)) != -1) {
                    sum += bytes.length;
                    if (available != 0) {
                        Log.d(TAG, "saveFile : " + ((sum / available) * 100));
                    }

                    Log.d(TAG, "saveFile len : " + len);
                    fileOutputStream.write(bytes, 0, len);
                }

                fileOutputStream.flush();

                Log.d(TAG, "saveFile finish in : " + (System.currentTimeMillis() - startTime));
            } catch (FileNotFoundException e) {
                bRet = false;
                e.printStackTrace();
                Log.e(TAG, "saveFile: " + e.getMessage());
            } catch (IOException e) {
                bRet = false;
                e.printStackTrace();
                Log.e(TAG, "saveFile: " + e.getMessage());
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Log.d(TAG, "saveFile save success : " + bRet);
        return bRet;
    }

    @Override
    protected Boolean doInBackground(Void[] objects) {
        return saveFile();
    }

    @Override
    protected void onPostExecute(Boolean bResult) {
        Log.d(TAG, "onPostExecute: " + bResult);
        if (bResult) {
            addFileToContentProvider();
            //addAlbumToContentProvider();
            MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ mPath + mFileName)));
            mCallback.onSuccess(String.format(MyApplication.getContext().getString(R.string.downloadMP3_success_msg), mFileName));
        } else {
            mCallback.onFailure();
        }
    }

    private void addFileToContentProvider() {
        ContentValues values = new ContentValues(8);
        values.put(MediaStore.Audio.Media._ID, mPlayListSongEntity.getId());
        values.put(MediaStore.Audio.Media.ALBUM_ID, mId);
        values.put(MediaStore.Audio.Media.ARTIST, mPlayListSongEntity.getArtist());
        values.put(MediaStore.Audio.Media.TITLE, mPlayListSongEntity.getTitle());
        values.put(MediaStore.Audio.Media.DURATION, mPlayListSongEntity.getDuration());
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        values.put(MediaStore.Audio.Media.DATA, mPath + mFileName);
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
}
