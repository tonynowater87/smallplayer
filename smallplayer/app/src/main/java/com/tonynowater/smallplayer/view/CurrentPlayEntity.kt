package com.tonynowater.smallplayer.view

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine
import com.tonynowater.smallplayer.util.TimeUtil

/**
 * Created by tonyliao on 2018/8/11.
 */
data class CurrentPlayEntity(val mediaId: String
                             , val id: String
                             , val title: String
                             , val artist: String
                             , val duration: String
                             , val artUri: String?
                             , val isLocal: String
                             , val source: String) {

    companion object {
        @JvmStatic
        private fun transferMediaItemToEntity(mediaItem: MediaBrowserCompat.MediaItem): CurrentPlayEntity {
            val mediaId = mediaItem.mediaId!!
            val extras = mediaItem.description.extras
            val id = extras?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)!!
            val title = extras.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            val artist = extras.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            val duration = TimeUtil.formatSongDuration(extras.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt())
            val artUri = extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
            val isLocal = extras.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL)
            val source = extras.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE)
            return CurrentPlayEntity(mediaId, id, title, artist, duration, artUri, isLocal, source)
        }

        @JvmStatic
        fun transferListMediaItemListToEntityArray(items: List<MediaBrowserCompat.MediaItem>): List<CurrentPlayEntity> {
            val entities: MutableList<CurrentPlayEntity> = mutableListOf()

            for (i in 0 until items.size) {
                entities.add(i, transferMediaItemToEntity(items[i]))
            }

            return entities
        }
    }
}