package com.tonynowater.smallplayer.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log

import com.squareup.okhttp.Response
import com.tonynowater.smallplayer.MyApplication
import com.tonynowater.smallplayer.R
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity
import com.tonynowater.smallplayer.service.notification.ChannelConstant

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

import okio.BufferedSink
import okio.BufferedSource
import okio.Okio

/**
 * Created by tonynowater on 2017/6/18.
 */
class FileHelper(private val mPlayListSongEntity: PlayListSongEntity, private val mResponse: Response, private val mCallback: OnFileHelperCallback) : AsyncTask<Void, Int, Boolean>() {
    private val mPath: String
    private val mStopDownloadIntent: PendingIntent
    private val mFileName: String
    private val mFileSuffix: String
    private val mMimeType: String
    private val mId: Int
    private val mBroadcastReceiver: BroadcastReceiver

    /**
     * @return 存Mp3的路徑
     */
    private val mFilePath: String
        get() = MyApplication.getContext().getExternalFilesDir(null).absolutePath + File.separator

    interface OnFileHelperCallback {
        fun onSuccess(msg: String)
        fun onFailure()
    }

    init {
        this.mFileSuffix = mResponse.body().contentType().subtype()
        this.mMimeType = mResponse.body().contentType().toString()
        this.mFileName = mPlayListSongEntity.title + "." + mFileSuffix
        mPath = mFilePath
        mId = mPlayListSongEntity.hashCode()
        mStopDownloadIntent = PendingIntent.getBroadcast(MyApplication.getContext(), REQUEST_CODE, Intent(ACTION_STOP_DOWNLOAD).setPackage(MyApplication.getContext().packageName), PendingIntent.FLAG_ONE_SHOT)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (TextUtils.equals(intent.action, ACTION_STOP_DOWNLOAD)) {
                    Logger.getInstance().d(TAG, "onReceive: stop download")
                    unregister()
                    dismissProgressNotification()
                    this@FileHelper.cancel(true)
                }
            }
        }
        MyApplication.getContext().registerReceiver(mBroadcastReceiver, IntentFilter(ACTION_STOP_DOWNLOAD))
    }

    private fun unregister() {
        try {
            MyApplication.getContext().unregisterReceiver(mBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 儲存Mp3
     * @return
     */
    private fun saveFile(): Boolean {
        var bRet = true
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            //外部儲存空間不可用
            bRet = false
        } else {

            val filePath = mPath + mFileName

            Logger.getInstance().d(TAG, "saveFile mPath : $mPath")
            Logger.getInstance().d(TAG, "saveFile file : $filePath")
            var file = File(mPath)

            Logger.getInstance().d(TAG, "saveFile isDir : " + file.isDirectory)

            if (!file.isDirectory) {
                Logger.getInstance().d(TAG, "saveFile createDir : $filePath")
                file.mkdir()
            }

            file = File(filePath)

            try {
                val startTime = System.currentTimeMillis()
                val bufferedSink = Okio.buffer(Okio.sink(file))
                val bufferedSource = mResponse.body().source()
                handleWrites(bufferedSink, bufferedSource, mResponse.body().contentLength())
                MiscellaneousUtil.calcRunningTime("下載$mFileName", startTime)
                bufferedSink.writeAll(bufferedSource)
                bufferedSink.flush()
                bufferedSink.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                bRet = false
                Log.e(TAG, "saveFile : " + e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                bRet = false
                Log.e(TAG, "saveFile : " + e.message)
            } finally {
                if (!bRet) {
                    if (file.exists()) {
                        Log.e(TAG, "saveFile exist false delete file : $mPath$mFileName")
                        file.delete()
                    }
                }
            }

        }

        Logger.getInstance().d(TAG, "saveFile save success : $bRet")
        return bRet
    }

    /**
     *
     * @param bufferedSink
     * @param source
     * @param contentLength
     */
    @Throws(IOException::class)
    private fun handleWrites(bufferedSink: BufferedSink, source: BufferedSource, contentLength: Long) {
        var completeCount: Long = 0
        var readCount: Long = 0
        var limit: Long = 50000
        Logger.getInstance().d(TAG, "handleWrites contentLength : $contentLength")
        while (readCount != -1L) {
            readCount = source.read(bufferedSink.buffer(), BYTE_READ.toLong())
            completeCount += readCount

            //show every 50kb
            if (completeCount > limit) {
                limit += 50000
                Logger.getInstance().d(TAG, "handleWrites total count : $completeCount")
                publishProgress((100 * completeCount / contentLength).toInt())
            }
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        Logger.getInstance().d(TAG, "onProgressUpdate: " + values[0])
        showProgressNotification(values[0]!!)
    }

    override fun doInBackground(objects: Array<Void>): Boolean? {
        return saveFile()
    }

    override fun onPostExecute(bResult: Boolean?) {
        Logger.getInstance().d(TAG, "onPostExecute: " + bResult!!)
        dismissProgressNotification()
        unregister()
        if (bResult) {
            addFileToContentProvider()
            //addAlbumToContentProvider();
            MyApplication.getContext().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(mPath))))
            mCallback.onSuccess(String.format(MyApplication.getContext().getString(R.string.downloadMP3_success_msg), mFileName))
        } else {
            mCallback.onFailure()
        }
    }

    private fun addFileToContentProvider() {
        val values = ContentValues(8)
        values.put(MediaStore.Audio.Media._ID, mId)
        values.put(MediaStore.Audio.Media.ARTIST, mPlayListSongEntity.artist)
        values.put(MediaStore.Audio.Media.TITLE, mPlayListSongEntity.title)
        values.put(MediaStore.Audio.Media.DURATION, mPlayListSongEntity.duration)
        values.put(MediaStore.Audio.Media.MIME_TYPE, mMimeType)
        values.put(MediaStore.Audio.Media.IS_MUSIC, true)
        values.put(MediaStore.Audio.Media.DATA, mPath + mFileName)
        values.put(MediaStore.Audio.Media.ALBUM_ID, mId)
        MyApplication.getContext().contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
    }

    // FIXME: 2017/6/18 這邊加入Album 會 java.lang.UnsupportedOperationException: Invalid URI content://media/external/audio/albums
    private fun addAlbumToContentProvider() {
        val values = ContentValues(4)
        values.put(MediaStore.Audio.Albums._ID, mId)
        values.put(MediaStore.Audio.Albums.ALBUM_ART, mPlayListSongEntity.albumArtUri)
        values.put(MediaStore.Audio.Albums.ALBUM_KEY, mId)
        values.put(MediaStore.Audio.Albums.ARTIST, mPlayListSongEntity.artist)
        MyApplication.getContext().contentResolver.insert(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, values)
    }

    /**
     * 關閉下載百分比的通知
     */
    private fun dismissProgressNotification() {
        MyApplication.getNotificationManager().cancel(mPlayListSongEntity.id)
    }

    /**
     * 顯示下載百分比的通知
     * @param percent 百分比
     */
    private fun showProgressNotification(percent: Int) {
        val builder = NotificationCompat.Builder(MyApplication.getContext(), ChannelConstant.DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setContentTitle(MyApplication.getContext().getString(R.string.app_name) + " " + MyApplication.getContext().getString(R.string.downloadMP3_ing_msg))
                .setContentText(mFileName)
                .setAutoCancel(false)
                .setProgress(100, percent, false)
                .setOngoing(true)//設置下載的通知不可被滑掉。
                .addAction(android.R.drawable.ic_media_pause, MyApplication.getMyString(R.string.downloadMP3_cancel_msg), mStopDownloadIntent)

        MyApplication.getNotificationManager().notify(mPlayListSongEntity.id, builder.build())
    }

    companion object {
        private val TAG = FileHelper::class.java.simpleName
        private const val BYTE_READ = 2048
        private const val ACTION_STOP_DOWNLOAD = "com.tonynowater.smallplayer.stop_download"
        private const val REQUEST_CODE = 100
    }
}
