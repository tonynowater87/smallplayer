package com.tonynowater.smallplayer.module.dto.realm;

import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 播放清單裡的歌曲
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListSongDTO extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String source;

    private String title;

    private String artist;

    private String albumArtUri;

    private int duration;

    private String isLocal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String local) {
        isLocal = local;
    }

    public MediaMetadataCompat getMediaMetadata() {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(getId()))
                .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE, getSource())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getDuration())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getAlbumArtUri())
                .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL, getIsLocal())
                .build();
    }
}
