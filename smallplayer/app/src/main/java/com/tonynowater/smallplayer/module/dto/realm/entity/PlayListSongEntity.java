package com.tonynowater.smallplayer.module.dto.realm.entity;

import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.u2b.Playable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by tonynowater on 2017/5/31.
 */

public class PlayListSongEntity extends RealmObject implements Playable, EntityInterface {

    @PrimaryKey
    private int id;

    private int listId;

    private int position;

    @Required
    private String source;

    private String title;

    private String artist;

    private String albumArtUri;

    private int duration;

    private String isLocal;

    public PlayListSongEntity() {}

    public PlayListSongEntity(U2BVideoDTO.ItemsBean itemsBean) {
        setSource(itemsBean.getVideoId());
        setArtist(itemsBean.getSnippet().getDescription());
        setTitle(itemsBean.getSnippet().getTitle());
        setDuration((int) itemsBean.getVideoDuration());
        setAlbumArtUri(processThumbnails(itemsBean.getSnippet().getThumbnails()));
        setIsLocal(MetaDataCustomKeyDefine.ISNOTLOCAL);
    }

    public PlayListSongEntity(U2bPlayListVideoDTO.ItemsBean itemsBean) {
        setSource(itemsBean.getVideoId());
        setArtist(itemsBean.getSnippet().getDescription());
        setTitle(itemsBean.getSnippet().getTitle());
        setDuration((int) itemsBean.getVideoDuration());
        setAlbumArtUri(processThumbnails(itemsBean.getSnippet().getThumbnails()));
        setIsLocal(MetaDataCustomKeyDefine.ISNOTLOCAL);
    }

    private String processThumbnails(U2bPlayListVideoDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnails) {
        String sUrl = null;
        if (thumbnails != null && thumbnails.getHigh() != null) {
            sUrl = thumbnails.getHigh().getUrl();
        }
        return sUrl;
    }

    private String processThumbnails(U2BVideoDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnails) {
        String sUrl = null;
        if (thumbnails != null && thumbnails.getHigh() != null) {
            sUrl = thumbnails.getHigh().getUrl();
        }
        return sUrl;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
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

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    // TODO: 2017/8/20 METADATA_KEY_MEDIA_ID只能是字串，但Realm的ID是Long，先暫時這樣轉型處理
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

    @Override
    public PlayListSongEntity getPlayListSongEntity() {
        return this;
    }

    @Override
    public boolean isDeletedOrPrivatedVideo() {
        return false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return (getTitle() + getId()).hashCode();
    }
}
