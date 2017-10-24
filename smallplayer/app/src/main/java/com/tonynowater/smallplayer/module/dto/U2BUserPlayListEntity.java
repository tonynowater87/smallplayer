package com.tonynowater.smallplayer.module.dto;

/**
 * Created by tonynowater on 2017/10/24.
 */
public class U2BUserPlayListEntity {
    private String playlistId;
    private String title;
    private String description;
    private String channelTitle;
    private String artUrl;

    public U2BUserPlayListEntity(U2BUserPlayListDTO.ItemsBean itemsBean) {
        setPlaylistId(itemsBean.getId());
        setTitle(itemsBean.getSnippet().getTitle());
        setDescription(itemsBean.getSnippet().getDescription());
        setChannelTitle(itemsBean.getSnippet().getChannelTitle());
        setArtUrl(processThumbnails(itemsBean.getSnippet().getThumbnails()));
    }

    public U2BUserPlayListEntity(U2BPlayListDTO.ItemsBean itemsBean) {
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

    private String processThumbnails(U2BUserPlayListDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnails) {
        String sUrl = null;
        if (thumbnails != null && thumbnails.getHigh() != null) {
            sUrl = thumbnails.getHigh().getUrl();
        }
        return sUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
