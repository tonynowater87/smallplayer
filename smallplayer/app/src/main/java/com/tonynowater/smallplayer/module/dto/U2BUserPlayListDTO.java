package com.tonynowater.smallplayer.module.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonynowtaer on 2017/8/27.
 */
public class U2BUserPlayListDTO {

//    {
//        "kind": "youtube#playlistListResponse",
//            "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/rCVPoPj-rXm4YlW_al8-yS2tsAk\"",
//            "pageInfo": {
//        "totalResults": 13,
//                "resultsPerPage": 25
//    },
//        "items": [
//        {
//            "kind": "youtube#playlist",
//                "etag": "\"m2yskBQFythfE4irbTIeOgYYfBU/sZpPxYajHWhxjXJ3ho-Qll87Gag\"",
//                "id": "PLnGsZNJO89LJhcUR9Ly66PI3X7Zh-Rqcq",
//                "snippet": {
//            "publishedAt": "2017-05-07T08:58:43.000Z",
//                    "channelId": "UCWXPvBBrvOZi2KZ-9YBEagA",
//                    "title": "英文歌單",
//                    "description": "",
//                    "thumbnails": {
//                "default": {
//                    "url": "https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg",
//                            "width": 120,
//                            "height": 90
//                },
//                "medium": {
//                    "url": "https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg",
//                            "width": 320,
//                            "height": 180
//                },
//                "high": {
//                    "url": "https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg",
//                            "width": 480,
//                            "height": 360
//                },
//                "standard": {
//                    "url": "https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg",
//                            "width": 640,
//                            "height": 480
//                }
//            },
//            "channelTitle": "liao tony",
//                    "localized": {
//                "title": "英文歌單",
//                        "description": ""
//            }
//        }
//        }
//    ]
//    }

    /**
     * kind : youtube#playlistListResponse
     * etag : "m2yskBQFythfE4irbTIeOgYYfBU/rCVPoPj-rXm4YlW_al8-yS2tsAk"
     * pageInfo : {"totalResults":13,"resultsPerPage":25}
     * items : [{"kind":"youtube#playlist","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/sZpPxYajHWhxjXJ3ho-Qll87Gag\"","id":"PLnGsZNJO89LJhcUR9Ly66PI3X7Zh-Rqcq","snippet":{"publishedAt":"2017-05-07T08:58:43.000Z","channelId":"UCWXPvBBrvOZi2KZ-9YBEagA","title":"英文歌單","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg","width":640,"height":480}},"channelTitle":"liao tony","localized":{"title":"英文歌單","description":""}}}]
     */

    private String kind;
    private String etag;
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
         * totalResults : 13
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

    public static class ItemsBean {
        /**
         * kind : youtube#playlist
         * etag : "m2yskBQFythfE4irbTIeOgYYfBU/sZpPxYajHWhxjXJ3ho-Qll87Gag"
         * id : PLnGsZNJO89LJhcUR9Ly66PI3X7Zh-Rqcq
         * snippet : {"publishedAt":"2017-05-07T08:58:43.000Z","channelId":"UCWXPvBBrvOZi2KZ-9YBEagA","title":"英文歌單","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg","width":640,"height":480}},"channelTitle":"liao tony","localized":{"title":"英文歌單","description":""}}
         */

        private String kind;
        private String etag;
        private String id;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public SnippetBean getSnippet() {
            return snippet;
        }

        public void setSnippet(SnippetBean snippet) {
            this.snippet = snippet;
        }

        public static class SnippetBean {
            /**
             * publishedAt : 2017-05-07T08:58:43.000Z
             * channelId : UCWXPvBBrvOZi2KZ-9YBEagA
             * title : 英文歌單
             * description :
             * thumbnails : {"default":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg","width":640,"height":480}}
             * channelTitle : liao tony
             * localized : {"title":"英文歌單","description":""}
             */

            private String publishedAt;
            private String channelId;
            private String title;
            private String description;
            private ThumbnailsBean thumbnails;
            private String channelTitle;
            private LocalizedBean localized;

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

            public LocalizedBean getLocalized() {
                return localized;
            }

            public void setLocalized(LocalizedBean localized) {
                this.localized = localized;
            }

            public static class ThumbnailsBean {
                /**
                 * default : {"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg","width":120,"height":90}
                 * medium : {"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg","width":320,"height":180}
                 * high : {"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg","width":480,"height":360}
                 * standard : {"url":"https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg","width":640,"height":480}
                 */

                @SerializedName("default")
                private DefaultBean defaultX;
                private MediumBean medium;
                private HighBean high;
                private StandardBean standard;

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

                public StandardBean getStandard() {
                    return standard;
                }

                public void setStandard(StandardBean standard) {
                    this.standard = standard;
                }

                public static class DefaultBean {
                    /**
                     * url : https://i.ytimg.com/vi/7Qp5vcuMIlk/default.jpg
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
                     * url : https://i.ytimg.com/vi/7Qp5vcuMIlk/mqdefault.jpg
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
                     * url : https://i.ytimg.com/vi/7Qp5vcuMIlk/hqdefault.jpg
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

                public static class StandardBean {
                    /**
                     * url : https://i.ytimg.com/vi/7Qp5vcuMIlk/sddefault.jpg
                     * width : 640
                     * height : 480
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

            public static class LocalizedBean {
                /**
                 * title : 英文歌單
                 * description :
                 */

                private String title;
                private String description;

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
            }
        }
    }
}
