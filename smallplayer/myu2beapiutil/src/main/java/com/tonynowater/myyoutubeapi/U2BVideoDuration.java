package com.tonynowater.myyoutubeapi;

import java.util.List;

/**
 * Created by tonyliao on 2017/5/17.
 */

public class U2BVideoDuration {

    /**
     * kind : youtube#videoListResponse
     * etag : "m2yskBQFythfE4irbTIeOgYYfBU/tpCRV8TYaCXVwIteKoaXZaweIUc"
     * pageInfo : {"totalResults":1,"resultsPerPage":1}
     * items : [{"kind":"youtube#video","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/sJGPWRHSYslAaUPLZuRgZdJvT6A\"","id":"XwAVMCbINig","contentDetails":{"duration":"PT5H23M36S","dimension":"2d","definition":"hd","caption":"false","licensedContent":true,"projection":"rectangular"}}]
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
         * totalResults : 1
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

    public static class ItemsBean {
        /**
         * kind : youtube#video
         * etag : "m2yskBQFythfE4irbTIeOgYYfBU/sJGPWRHSYslAaUPLZuRgZdJvT6A"
         * id : XwAVMCbINig
         * contentDetails : {"duration":"PT5H23M36S","dimension":"2d","definition":"hd","caption":"false","licensedContent":true,"projection":"rectangular"}
         */

        private String kind;
        private String etag;
        private String id;
        private ContentDetailsBean contentDetails;

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

        public ContentDetailsBean getContentDetails() {
            return contentDetails;
        }

        public void setContentDetails(ContentDetailsBean contentDetails) {
            this.contentDetails = contentDetails;
        }

        public static class ContentDetailsBean {
            /**
             * duration : PT5H23M36S
             * dimension : 2d
             * definition : hd
             * caption : false
             * licensedContent : true
             * projection : rectangular
             */

            private String duration;
            private String dimension;
            private String definition;
            private String caption;
            private boolean licensedContent;
            private String projection;

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getDimension() {
                return dimension;
            }

            public void setDimension(String dimension) {
                this.dimension = dimension;
            }

            public String getDefinition() {
                return definition;
            }

            public void setDefinition(String definition) {
                this.definition = definition;
            }

            public String getCaption() {
                return caption;
            }

            public void setCaption(String caption) {
                this.caption = caption;
            }

            public boolean isLicensedContent() {
                return licensedContent;
            }

            public void setLicensedContent(boolean licensedContent) {
                this.licensedContent = licensedContent;
            }

            public String getProjection() {
                return projection;
            }

            public void setProjection(String projection) {
                this.projection = projection;
            }
        }
    }
}
