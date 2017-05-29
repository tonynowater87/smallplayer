package com.tonynowater.smallplayer.module.dto;

import com.google.gson.annotations.SerializedName;
import com.tonynowater.smallplayer.u2b.Playable;

import java.util.List;

/**
 * Created by tony10532 on 2017/5/20.
 */

public class U2BPlayListDTO {

//    {
//        "kind": "youtube#searchListResponse",
//            "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/4-j96RuGgkPxDpZiP-Dh3maqsNM\"",
//            "nextPageToken": "CAEQAA",
//            "regionCode": "TW",
//            "pageInfo": {
//        "totalResults": 4150,
//                "resultsPerPage": 1
//    },
//        "items": [{
//        "kind": "youtube#searchResult",
//                "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/HGBAkIYbW3LFqqoP91n9MCf_DgE\"",
//                "id": {
//            "kind": "youtube#playlist",
//                    "playlistId": "PLjk5xY9_wwMdFnfq0sL-jB5ybGNiA8GaB"
//        },
//        "snippet": {
//            "publishedAt": "2016-11-13T08:01:00.000Z",
//                    "channelId": "UCcoDqdwNB2bkxb50UXivPAg",
//                    "title": "王力宏 你不知道的事",
//                    "description": "",
//                    "thumbnails": {
//                "default": {
//                    "url": "https://i.ytimg.com/vi/AURMxl7cp90/default.jpg",
//                            "width": 120,
//                            "height": 90
//                },
//                "medium": {
//                    "url": "https://i.ytimg.com/vi/AURMxl7cp90/mqdefault.jpg",
//                            "width": 320,
//                            "height": 180
//                },
//                "high": {
//                    "url": "https://i.ytimg.com/vi/AURMxl7cp90/hqdefault.jpg",
//                            "width": 480,
//                            "height": 360
//                }
//            },
//            "channelTitle": "Chi Ku",
//                    "liveBroadcastContent": "none"
//        }
//    }]
//    }

    private String kind;
    private String etag;
    private String nextPageToken;
    private String prevPageToken;
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

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
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
         * totalResults : 2585
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
         * etag : "m2yskBQFythfE4irbTIeOgYYfBU/hSMlrNuOGzVDP7Eg34Ipb8f5R8s"
         * id : {"kind":"youtube#playlist","playlistId":"PLGzlPWAy4QFYR5SaH8JC1V5ikzKlO34KY"}
         * snippet : {"publishedAt":"2016-09-13T08:45:24.000Z","channelId":"UC6EyRk-xAMeETIGl1vwsLBg","title":"阿杜","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/hqdefault.jpg","width":480,"height":360}},"channelTitle":"張偉唐","liveBroadcastContent":"none"}
         */

        private String kind;
        private String etag;
        private IdBean id;
        private SnippetBean snippet;

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

        public static class IdBean {
            /**
             * kind : youtube#playlist
             * playlistId : PLGzlPWAy4QFYR5SaH8JC1V5ikzKlO34KY
             */

            private String kind;
            private String playlistId;

            public String getKind() {
                return kind;
            }

            public void setKind(String kind) {
                this.kind = kind;
            }

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
            }
        }

        public static class SnippetBean {
            /**
             * publishedAt : 2016-09-13T08:45:24.000Z
             * channelId : UC6EyRk-xAMeETIGl1vwsLBg
             * title : 阿杜
             * description :
             * thumbnails : {"default":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/hqdefault.jpg","width":480,"height":360}}
             * channelTitle : 張偉唐
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
                 * default : {"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/default.jpg","width":120,"height":90}
                 * medium : {"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/mqdefault.jpg","width":320,"height":180}
                 * high : {"url":"https://i.ytimg.com/vi/Bxyoesk3DVs/hqdefault.jpg","width":480,"height":360}
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
                     * url : https://i.ytimg.com/vi/Bxyoesk3DVs/default.jpg
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
                     * url : https://i.ytimg.com/vi/Bxyoesk3DVs/mqdefault.jpg
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
                     * url : https://i.ytimg.com/vi/Bxyoesk3DVs/hqdefault.jpg
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
