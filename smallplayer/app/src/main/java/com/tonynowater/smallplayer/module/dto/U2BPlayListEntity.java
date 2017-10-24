package com.tonynowater.smallplayer.module.dto;

/**
 * 畫面端要顯示的U2BPlayListEntity，由U2BUserPlayListDTO.ItemsBean轉換而成
 * Created by tonynowater on 2017/10/23.
 */
public class U2BPlayListEntity {
    private String playlistId;
    private String title;
    private String description;
    private String channelTitle;
    private String artUrl;

    public U2BPlayListEntity(U2BPlayListDTO.ItemsBean itemsBean) {
        setPlaylistId(itemsBean.id.playlistId);
        setTitle(itemsBean.snippet.title);
        setDescription(itemsBean.snippet.description);
        setChannelTitle(itemsBean.snippet.channelTitle);
        setArtUrl(processThumbnails(itemsBean.snippet.thumbnails));
    }

    private String processThumbnails(U2BPlayListDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnails) {
        String sUrl = null;
        if (thumbnails != null && thumbnails.high != null) {
            sUrl = thumbnails.high.url;
        }
        return sUrl;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }

    public String getChannelTitle() {
        return String.format("來自 %s",channelTitle);
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }
}
