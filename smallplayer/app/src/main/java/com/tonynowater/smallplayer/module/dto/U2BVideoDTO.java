package com.tonynowater.smallplayer.module.dto;

import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;

import java.util.List;

/**
 * Created by tonyliao on 2017/5/1.
 */
public class U2BVideoDTO {
    private static final String TAG = U2BVideoDTO.class.getSimpleName();
//    {
//        "kind": "youtube#searchListResponse",
//            "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/NFTVKzDKaU_PcJd2pR2QpKF0yCE\"",
//            "nextPageToken": "CAEQAA",
//            "regionCode": "TW",
//            "pageInfo": {
//        "totalResults": 61119,
//                "resultsPerPage": 1
//    },
//        "items": [{
//        "kind": "youtube#searchResult",
//                "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/_JzludqTFDnERPo2GiCT_27xK2c\"",
//                "id": {
//            "kind": "youtube#video",
//                    "videoId": "n0FOgqJv6Vs"
//        },
//        "snippet": {
//            "publishedAt": "2011-04-11T12:06:12.000Z",
//                    "channelId": "UCYIHp2e2z6uTT3j9JWsRbPQ",
//                    "title": "王力宏 Leehom Wang - 你不知道的事",
//                    "description": "Music video by Leehom Wang performing NI BU ZHI DAO DE SHI. © 2010 Sony Music Entertainment Taiwan Ltd.",
//                    "thumbnails": {
//                "default": {
//                    "url": "https://i.ytimg.com/vi/n0FOgqJv6Vs/default.jpg",
//                            "width": 120,
//                            "height": 90
//                },
//                "medium": {
//                    "url": "https://i.ytimg.com/vi/n0FOgqJv6Vs/mqdefault.jpg",
//                            "width": 320,
//                            "height": 180
//                },
//                "high": {
//                    "url": "https://i.ytimg.com/vi/n0FOgqJv6Vs/hqdefault.jpg",
//                            "width": 480,
//                            "height": 360
//                }
//            },
//            "channelTitle": "leehomwangVEVO",
//                    "liveBroadcastContent": "none"
//        }
//    }]
//    }

    private String kind;
    private String etag;
    private String nextPageToken;
    private String regionCode;
    private PageInfoBean pageInfo;
    private List<ItemsBean> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class PageInfoBean {
        /**
         * totalResults : 434244
         * resultsPerPage : 25
         */

        private int totalResults;
        private int resultsPerPage;

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getResultsPerPage() {
            return resultsPerPage;
        }

        public void setResultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
    }

    public static class ItemsBean implements Playable {
        /**
         * kind : youtube#searchResult
         * etag : "m2yskBQFythfE4irbTIeOgYYfBU/t1_TdQR_I1p8lifuPbrQOyZqFoo"
         * id : {"kind":"youtube#video","videoId":"osnuIq1eLTk"}
         * snippet : {"publishedAt":"2013-11-10T01:58:05.000Z","channelId":"UCM6Zft-lFfWD2PmmOflgYPw","title":"5566歌曲大串燒","description":"歌曲播放順序名稱為以下： 1.我難過2愛情漫遊3.無所謂4.一光年5.神話6.哇沙米7.Without your love 8.跟他拼9.Easy come easy go 10.綻放11.For you 12. One...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/hqdefault.jpg","width":480,"height":360}},"channelTitle":"s1990413","liveBroadcastContent":"none"}
         */
        private String kind;
        private String etag;
        private IdBean id;
        private SnippetBean snippet;
        private int durationToMilionSecond = -1;
        private String url;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public IdBean getId() {
            return id;
        }

        public void setId(IdBean id) {
            this.id = id;
        }

        public SnippetBean getSnippet() {
            return snippet;
        }

        public void setSnippet(SnippetBean snippet) {
            this.snippet = snippet;
        }

        public void setVideoDuration(int durationToMilionSecond) {
            this.durationToMilionSecond = durationToMilionSecond;
        }

        public long getVideoDuration() {
            return durationToMilionSecond;
        }

        @Override
        public PlayListSongEntity getPlayListSongEntity() {
            PlayListSongEntity playListSongEntity = new PlayListSongEntity();
            playListSongEntity.setSource(getId().getVideoId());
            playListSongEntity.setArtist(getSnippet().getTitle());
            playListSongEntity.setTitle(getSnippet().getTitle());
            playListSongEntity.setDuration((int) getVideoDuration());
            playListSongEntity.setAlbumArtUri(getArtUrl());
            playListSongEntity.setIsLocal(MetaDataCustomKeyDefine.ISNOTLOCAL);
            return playListSongEntity;
        }

        /**
         * @return 畫面要顯示的Art圖
         */
        private String getArtUrl() {
            String sArtUrl;
            if (getSnippet().getThumbnails() == null) {
                Log.e(TAG, "thumbnails null ");
                return null;
            }

            try {
                sArtUrl = getSnippet().getThumbnails().getHigh().getUrl();
                if (TextUtils.isEmpty(sArtUrl)) {
                    sArtUrl = getSnippet().getThumbnails().getDefaultX().getUrl();
                }
            } catch (Exception e) {
                Log.e(TAG, "getStandard() null ");
                sArtUrl = getSnippet().getThumbnails().getDefaultX().getUrl();
            }
            return sArtUrl;
        }

        public static class IdBean {
            /**
             * kind : youtube#video
             * videoId : osnuIq1eLTk
             */

            private String kind;
            private String videoId;

            public String getKind() {
                return kind;
            }

            public void setKind(String kind) {
                this.kind = kind;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }
        }

        public static class SnippetBean {
            /**
             * publishedAt : 2013-11-10T01:58:05.000Z
             * channelId : UCM6Zft-lFfWD2PmmOflgYPw
             * title : 5566歌曲大串燒
             * description : 歌曲播放順序名稱為以下： 1.我難過2愛情漫遊3.無所謂4.一光年5.神話6.哇沙米7.Without your love 8.跟他拼9.Easy come easy go 10.綻放11.For you 12. One...
             * thumbnails : {"default":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/hqdefault.jpg","width":480,"height":360}}
             * channelTitle : s1990413
             * liveBroadcastContent : none
             */

            private String publishedAt;
            private String channelId;
            private String title;
            private String description;
            private ThumbnailsBean thumbnails;
            private String channelTitle;
            private String liveBroadcastContent;

            public String getPublishedAt() {
                return publishedAt;
            }

            public void setPublishedAt(String publishedAt) {
                this.publishedAt = publishedAt;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
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

            public ThumbnailsBean getThumbnails() {
                return thumbnails;
            }

            public void setThumbnails(ThumbnailsBean thumbnails) {
                this.thumbnails = thumbnails;
            }

            public String getChannelTitle() {
                return channelTitle;
            }

            public void setChannelTitle(String channelTitle) {
                this.channelTitle = channelTitle;
            }

            public String getLiveBroadcastContent() {
                return liveBroadcastContent;
            }

            public void setLiveBroadcastContent(String liveBroadcastContent) {
                this.liveBroadcastContent = liveBroadcastContent;
            }

            public static class ThumbnailsBean {
                /**
                 * default : {"url":"https://i.ytimg.com/vi/osnuIq1eLTk/default.jpg","width":120,"height":90}
                 * medium : {"url":"https://i.ytimg.com/vi/osnuIq1eLTk/mqdefault.jpg","width":320,"height":180}
                 * high : {"url":"https://i.ytimg.com/vi/osnuIq1eLTk/hqdefault.jpg","width":480,"height":360}
                 */

                @SerializedName("default")
                private DefaultBean defaultX;
                private MediumBean medium;
                private HighBean high;

                public DefaultBean getDefaultX() {
                    return defaultX;
                }

                public void setDefaultX(DefaultBean defaultX) {
                    this.defaultX = defaultX;
                }

                public MediumBean getMedium() {
                    return medium;
                }

                public void setMedium(MediumBean medium) {
                    this.medium = medium;
                }

                public HighBean getHigh() {
                    return high;
                }

                public void setHigh(HighBean high) {
                    this.high = high;
                }

                public static class DefaultBean {
                    /**
                     * url : https://i.ytimg.com/vi/osnuIq1eLTk/default.jpg
                     * width : 120
                     * height : 90
                     */

                    private String url;
                    private int width;
                    private int height;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }
                }

                public static class MediumBean {
                    /**
                     * url : https://i.ytimg.com/vi/osnuIq1eLTk/mqdefault.jpg
                     * width : 320
                     * height : 180
                     */

                    private String url;
                    private int width;
                    private int height;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }
                }

                public static class HighBean {
                    /**
                     * url : https://i.ytimg.com/vi/osnuIq1eLTk/hqdefault.jpg
                     * width : 480
                     * height : 360
                     */

                    private String url;
                    private int width;
                    private int height;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }
                }
            }
        }
    }
}
