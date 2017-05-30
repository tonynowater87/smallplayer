package com.tonynowater.smallplayer.module.dto;

import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;
import com.tonynowater.smallplayer.u2b.Playable;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/21.
 */
public class U2bPlayListVideoDTO {
    private static final String TAG = U2bPlayListVideoDTO.class.getSimpleName();
    /**
     * kind : youtube#playlistItemListResponse
     * etag : "m2yskBQFythfE4irbTIeOgYYfBU/ogt0jI-0Jd-n2JF1CPuASjKgM3Q"
     * nextPageToken : CAEQAA
     * pageInfo : {"totalResults":23,"resultsPerPage":1}
     * items : [{"kind":"youtube#playlistItem","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/b1Shpo8koxznnj7QFapNrFSG0Ng\"","id":"UExLV2ItRG9hMnF1dUlmR2xBNVBjYUt6eGVfQXpwRmcxUy41NkI0NEY2RDEwNTU3Q0M2","snippet":{"publishedAt":"2014-04-07T09:16:18.000Z","channelId":"UCQgXsy4fM0_naqY7xOkPvMQ","title":"動力火車 Power Station [ 不甘心不放手 Wouldn't Let Go ] Official Music Video","description":"不甘心不放手\n作詞：吳梵  作曲：吳梵  編曲：洪敬堯\n\n不再執著於昨天的癡狂\n我的心像是台北的街頭\n不知該往哪兒走\n\n妳的心像閃爍的霓虹\n叫人迷戀卻也迷惑\n我沒有把握\n\n誰在慫恿夜的脆弱　撫平的傷又隱隱作痛\n是誰說過不再回頭　還是讓妳淹沒了我\n（將我淹沒）\n\n想放棄卻不能甘心放手　留妳在夢中卻苦痛了我\n等著傷心不如學會承受　反正妳不會是我的\n想放棄卻不能甘心放手　留妳在夢中卻苦痛了我\n等妳想起不如先忘記妳　反正離開妳的人是我","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/sddefault.jpg","width":640,"height":480}},"channelTitle":"呂如涵","playlistId":"PLKWb-Doa2quuIfGlA5PcaKzxe_AzpFg1S","position":0,"resourceId":{"kind":"youtube#video","videoId":"N-y8c994jsk"}}}]
     */

    private String kind;
    private String etag;
    private String nextPageToken;
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
         * totalResults : 23
         * resultsPerPage : 1
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
         * kind : youtube#playlistItem
         * etag : "m2yskBQFythfE4irbTIeOgYYfBU/b1Shpo8koxznnj7QFapNrFSG0Ng"
         * id : UExLV2ItRG9hMnF1dUlmR2xBNVBjYUt6eGVfQXpwRmcxUy41NkI0NEY2RDEwNTU3Q0M2
         * snippet : {"publishedAt":"2014-04-07T09:16:18.000Z","channelId":"UCQgXsy4fM0_naqY7xOkPvMQ","title":"動力火車 Power Station [ 不甘心不放手 Wouldn't Let Go ] Official Music Video","description":"不甘心不放手\n作詞：吳梵  作曲：吳梵  編曲：洪敬堯\n\n不再執著於昨天的癡狂\n我的心像是台北的街頭\n不知該往哪兒走\n\n妳的心像閃爍的霓虹\n叫人迷戀卻也迷惑\n我沒有把握\n\n誰在慫恿夜的脆弱　撫平的傷又隱隱作痛\n是誰說過不再回頭　還是讓妳淹沒了我\n（將我淹沒）\n\n想放棄卻不能甘心放手　留妳在夢中卻苦痛了我\n等著傷心不如學會承受　反正妳不會是我的\n想放棄卻不能甘心放手　留妳在夢中卻苦痛了我\n等妳想起不如先忘記妳　反正離開妳的人是我","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/sddefault.jpg","width":640,"height":480}},"channelTitle":"呂如涵","playlistId":"PLKWb-Doa2quuIfGlA5PcaKzxe_AzpFg1S","position":0,"resourceId":{"kind":"youtube#video","videoId":"N-y8c994jsk"}}
         */

        private String kind;
        private String etag;
        private String id;
        private SnippetBean snippet;
        private String url;
        private int durationToMilionSecond = -1;

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

        //TODO
        public MediaMetadataCompat getMediaMetadata() {

            String sArtUrl = "";
            try {
                sArtUrl = getSnippet().getThumbnails().getStandard().getUrl();// TODO: 2017/5/22 若查回沒URL會當機
            } catch (Exception e) {
                Log.e(TAG, "getMediaMetadata: null url");
            }
            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, getId())
                    .putString("VIDEO_ID", getSnippet().getResourceId().getVideoId())// TODO: 2017/5/21
                    .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE, getUrl())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getSnippet().getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, getSnippet().getDescription())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getVideoDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, getSnippet().getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, sArtUrl)
                    .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL, MetaDataCustomKeyDefine.ISNOTLOCAL)
                    .build();
        }

        @Override
        public PlayListSongDTO getPlayListSongDTO() {
            String sArtUrl = "";
            try {
                sArtUrl = getSnippet().getThumbnails().getHigh().getUrl();// TODO: 2017/5/22 若查回沒URL會當機
            } catch (Exception e) {
                Log.e(TAG, "getMediaMetadata: null url");
            }
            PlayListSongDTO playListSongDTO = new PlayListSongDTO();
            playListSongDTO.setId((int) getVideoDuration());
            playListSongDTO.setSource(getUrl());
            playListSongDTO.setArtist(getSnippet().getTitle());
            playListSongDTO.setTitle(getSnippet().getTitle());
            playListSongDTO.setDuration((int) getVideoDuration());
            playListSongDTO.setAlbumArtUri(sArtUrl);
            playListSongDTO.setIsLocal(MetaDataCustomKeyDefine.ISNOTLOCAL);
            return playListSongDTO;
        }

        public void setDataSource(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setVideoDuration(int durationToMilionSecond) {
            this.durationToMilionSecond = durationToMilionSecond;
        }

        public long getVideoDuration() {
            return durationToMilionSecond;
        }

        public static class SnippetBean {
            /**
             * publishedAt : 2014-04-07T09:16:18.000Z
             * channelId : UCQgXsy4fM0_naqY7xOkPvMQ
             * title : 動力火車 Power Station [ 不甘心不放手 Wouldn't Let Go ] Official Music Video
             * description : 不甘心不放手
             作詞：吳梵  作曲：吳梵  編曲：洪敬堯

             不再執著於昨天的癡狂
             我的心像是台北的街頭
             不知該往哪兒走

             妳的心像閃爍的霓虹
             叫人迷戀卻也迷惑
             我沒有把握

             誰在慫恿夜的脆弱　撫平的傷又隱隱作痛
             是誰說過不再回頭　還是讓妳淹沒了我
             （將我淹沒）

             想放棄卻不能甘心放手　留妳在夢中卻苦痛了我
             等著傷心不如學會承受　反正妳不會是我的
             想放棄卻不能甘心放手　留妳在夢中卻苦痛了我
             等妳想起不如先忘記妳　反正離開妳的人是我
             * thumbnails : {"default":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/hqdefault.jpg","width":480,"height":360},"standard":{"url":"https://i.ytimg.com/vi/N-y8c994jsk/sddefault.jpg","width":640,"height":480}}
             * channelTitle : 呂如涵
             * playlistId : PLKWb-Doa2quuIfGlA5PcaKzxe_AzpFg1S
             * position : 0
             * resourceId : {"kind":"youtube#video","videoId":"N-y8c994jsk"}
             */

            private String publishedAt;
            private String channelId;
            private String title;
            private String description;
            private ThumbnailsBean thumbnails;
            private String channelTitle;
            private String playlistId;
            private int position;
            private ResourceIdBean resourceId;

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

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            public ResourceIdBean getResourceId() {
                return resourceId;
            }

            public void setResourceId(ResourceIdBean resourceId) {
                this.resourceId = resourceId;
            }

            public static class ThumbnailsBean {
                /**
                 * default : {"url":"https://i.ytimg.com/vi/N-y8c994jsk/default.jpg","width":120,"height":90}
                 * medium : {"url":"https://i.ytimg.com/vi/N-y8c994jsk/mqdefault.jpg","width":320,"height":180}
                 * high : {"url":"https://i.ytimg.com/vi/N-y8c994jsk/hqdefault.jpg","width":480,"height":360}
                 * standard : {"url":"https://i.ytimg.com/vi/N-y8c994jsk/sddefault.jpg","width":640,"height":480}
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
                     * url : https://i.ytimg.com/vi/N-y8c994jsk/default.jpg
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
                     * url : https://i.ytimg.com/vi/N-y8c994jsk/mqdefault.jpg
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
                     * url : https://i.ytimg.com/vi/N-y8c994jsk/hqdefault.jpg
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
                     * url : https://i.ytimg.com/vi/N-y8c994jsk/sddefault.jpg
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

            public static class ResourceIdBean {
                /**
                 * kind : youtube#video
                 * videoId : N-y8c994jsk
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
        }
    }
}
