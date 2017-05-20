package com.tonynowater.smallplayer.u2b;

import android.support.v4.media.MediaMetadataCompat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonyliao on 2017/5/1.
 */

public class U2BVideoDTO {
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    /**
     * kind : youtube#searchListResponse
     * etag : "m2yskBQFythfE4irbTIeOgYYfBU/aMtXHLU4A4_KtHTySjAgoPqFYQQ"
     * nextPageToken : CBkQAA
     * regionCode : TW
     * pageInfo : {"totalResults":434244,"resultsPerPage":25}
     * items : [{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/t1_TdQR_I1p8lifuPbrQOyZqFoo\"","id":{"kind":"youtube#video","videoId":"osnuIq1eLTk"},"snippet":{"publishedAt":"2013-11-10T01:58:05.000Z","channelId":"UCM6Zft-lFfWD2PmmOflgYPw","title":"5566歌曲大串燒","description":"歌曲播放順序名稱為以下： 1.我難過2愛情漫遊3.無所謂4.一光年5.神話6.哇沙米7.Without your love 8.跟他拼9.Easy come easy go 10.綻放11.For you 12. One...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/osnuIq1eLTk/hqdefault.jpg","width":480,"height":360}},"channelTitle":"s1990413","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/WRueShNYbLSHrJxj7v6f9bPNdpI\"","id":{"kind":"youtube#video","videoId":"T0LfHEwEXXw"},"snippet":{"publishedAt":"2013-04-26T13:02:56.000Z","channelId":"UCB0AkF6JddEg5dqLkk1-qtg","title":"5566 - 我難過 MV [HQ]","description":"5566 - 我難過MV [HQ]","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/T0LfHEwEXXw/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/T0LfHEwEXXw/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/T0LfHEwEXXw/hqdefault.jpg","width":480,"height":360}},"channelTitle":"5566 频道","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/LddqlvwF1GKTQXrrHbPCCLh2TNA\"","id":{"kind":"youtube#video","videoId":"pEA7EwkLmnQ"},"snippet":{"publishedAt":"2016-12-31T16:02:28.000Z","channelId":"UCIU8ha-NHmLjtUwU7dFiXUA","title":"2017花young臺中跨年晚會 5566《傳說+無所謂》｜三立新聞網SETN.com","description":"按讚【三立新聞FB】重大訊息搶先看： http://www.facebook.com/setnews ➲ 加入【三立新聞LINE】給你更多新聞： http://goo.gl/Te1ifA ➲ 下載【三立新聞網APP】...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/pEA7EwkLmnQ/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/pEA7EwkLmnQ/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/pEA7EwkLmnQ/hqdefault.jpg","width":480,"height":360}},"channelTitle":"SETN三立新聞網","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/bDdK8wxQNnMnxUv05C5kNLr9fbI\"","id":{"kind":"youtube#video","videoId":"rBBQzO411bA"},"snippet":{"publishedAt":"2013-03-17T02:15:27.000Z","channelId":"UCB0AkF6JddEg5dqLkk1-qtg","title":"5566 - 存在 MV [HQ]","description":"5566 - 存在MV [HQ]","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/rBBQzO411bA/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/rBBQzO411bA/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/rBBQzO411bA/hqdefault.jpg","width":480,"height":360}},"channelTitle":"5566 频道","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/O-bHtos1kej4K2_xVNi6LD6tSag\"","id":{"kind":"youtube#video","videoId":"tA5UHpMQmqY"},"snippet":{"publishedAt":"2016-10-08T11:34:38.000Z","channelId":"UCIU8ha-NHmLjtUwU7dFiXUA","title":"金鐘／超懷念！5566合體大唱神曲我難過｜三立新聞網SETN.com","description":"按讚【三立新聞FB】重大訊息搶先看： http://www.facebook.com/setnews ➲ 加入【三立新聞LINE】給你更多新聞： http://goo.gl/Te1ifA ➲ 下載【三立新聞網APP】...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/tA5UHpMQmqY/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/tA5UHpMQmqY/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/tA5UHpMQmqY/hqdefault.jpg","width":480,"height":360}},"channelTitle":"SETN三立新聞網","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/VxyNHCBrg4suHIzWqNxdipyeMv4\"","id":{"kind":"youtube#video","videoId":"eHN8hFbW_sc"},"snippet":{"publishedAt":"2009-08-07T18:39:36.000Z","channelId":"UCIjQrZU4v44RADTzeuxYxYA","title":"5566【MVP情人】無所謂 MV","description":"無所謂.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/eHN8hFbW_sc/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/eHN8hFbW_sc/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/eHN8hFbW_sc/hqdefault.jpg","width":480,"height":360}},"channelTitle":"hsieh0220","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/duUnvtZLQ17SPlu962I-er4WTVk\"","id":{"kind":"youtube#video","videoId":"5aZuRshDKFg"},"snippet":{"publishedAt":"2016-12-31T16:19:36.000Z","channelId":"UCIU8ha-NHmLjtUwU7dFiXUA","title":"2017花young臺中跨年晚會 5566《我難過》｜三立新聞網SETN.com","description":"按讚【三立新聞FB】重大訊息搶先看： http://www.facebook.com/setnews ➲ 加入【三立新聞LINE】給你更多新聞： http://goo.gl/Te1ifA ➲ 下載【三立新聞網APP】...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/5aZuRshDKFg/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/5aZuRshDKFg/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/5aZuRshDKFg/hqdefault.jpg","width":480,"height":360}},"channelTitle":"SETN三立新聞網","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/pHpQUKM7be2rRwC4lJlv79F4PQA\"","id":{"kind":"youtube#video","videoId":"dksNqalhviA"},"snippet":{"publishedAt":"2016-12-31T13:09:40.000Z","channelId":"UCO3bfz4KY6zGT5IOaFJuxpA","title":"跨年看這! 5566.玖壹壹台中拚場│中視新聞 20161231","description":"台中跨年演唱會，將在洲際棒球場及麗寶樂園，雙主場舉行，堅強卡司輪番上陣，包括經典天團5566，將獨家演出獻給台中，與民眾一起倒數，接著再...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/dksNqalhviA/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/dksNqalhviA/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/dksNqalhviA/hqdefault.jpg","width":480,"height":360}},"channelTitle":"中視新聞","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/hOCaqrawJZiePrE-zxzTGT13s98\"","id":{"kind":"youtube#video","videoId":"1Q54KOQB6vg"},"snippet":{"publishedAt":"2016-12-31T17:08:39.000Z","channelId":"UCokOmKCQAugpV1t6lombFFA","title":"【2017花YOUNG台中】 5566天團台中獨家合體  全場瘋狂","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/1Q54KOQB6vg/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/1Q54KOQB6vg/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/1Q54KOQB6vg/hqdefault.jpg","width":480,"height":360}},"channelTitle":"快點TV","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/KkTfdQkWuSr-Ju-hKs02641vTgk\"","id":{"kind":"youtube#video","videoId":"jQfrLCN-D0o"},"snippet":{"publishedAt":"2014-03-23T19:42:28.000Z","channelId":"UCwtbuh8E0o_McoTXAJ6YjGA","title":"5566-抒情+慢歌精選2002~2008","description":"音樂是自己慢慢接的參照最棒冠軍精選56快歌精選-http://ppt.cc/tfYBt 1.因為愛2.煎熬3.綻放4.boyfriend 5.回到我身邊6.One more try 7.守候8.一光年9.我難過...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/jQfrLCN-D0o/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/jQfrLCN-D0o/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/jQfrLCN-D0o/hqdefault.jpg","width":480,"height":360}},"channelTitle":"abow2022","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/p-FF9lArgGqbJg-kD45SmoWWVGs\"","id":{"kind":"youtube#video","videoId":"zmRQBmOJZmA"},"snippet":{"publishedAt":"2016-10-28T08:52:29.000Z","channelId":"UCQZvLuSQCLi0wvlffExowPg","title":"王少偉坦承私下與5566不聯絡 在意更真實的自己","description":"本土天團5566今年終於破天荒在金鐘獎合體， 成為頒獎典禮一大亮點， 雖然五人最終聚在一起， 然而合體完卻又各分東西， 孫協志、王仁甫和許孟哲...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/zmRQBmOJZmA/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/zmRQBmOJZmA/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/zmRQBmOJZmA/hqdefault.jpg","width":480,"height":360}},"channelTitle":"wikienter","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/ClZNK_QEOb1WZ3F5GQS3Kghv4w8\"","id":{"kind":"youtube#video","videoId":"8ZTKoiEVoxs"},"snippet":{"publishedAt":"2009-08-07T18:01:09.000Z","channelId":"UCIjQrZU4v44RADTzeuxYxYA","title":"5566【3rd】好久不見 MV","description":"好久不見.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/8ZTKoiEVoxs/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/8ZTKoiEVoxs/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/8ZTKoiEVoxs/hqdefault.jpg","width":480,"height":360}},"channelTitle":"hsieh0220","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/JeK1ovDDlUEvasmuhT1sjZc6vLc\"","id":{"kind":"youtube#video","videoId":"X476C8DZz3Q"},"snippet":{"publishedAt":"2013-02-24T03:32:11.000Z","channelId":"UCB0AkF6JddEg5dqLkk1-qtg","title":"5566 - 傳說 MV [HQ]","description":"5566 - 傳說MV [HQ] MV feat. 柯家恩.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/X476C8DZz3Q/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/X476C8DZz3Q/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/X476C8DZz3Q/hqdefault.jpg","width":480,"height":360}},"channelTitle":"5566 频道","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/QHIAmwDRmHm9PPa_Ctoff65diOk\"","id":{"kind":"youtube#video","videoId":"eLLDhxzWjAc"},"snippet":{"publishedAt":"2015-02-15T12:47:37.000Z","channelId":"UCBa9CX_5ogXNE-iu_NDO82Q","title":"5566 as F4","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/eLLDhxzWjAc/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/eLLDhxzWjAc/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/eLLDhxzWjAc/hqdefault.jpg","width":480,"height":360}},"channelTitle":"Audrey Sia","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/MMfP3hF74-w4EjijoySGKUtVhtg\"","id":{"kind":"youtube#video","videoId":"bU4PgZid5I0"},"snippet":{"publishedAt":"2017-04-27T09:30:01.000Z","channelId":"UC-H3UK70OAX6lfaMJJYWmSw","title":"5566 我難過 歌詞","description":"別忘了訂閱我的【實驗挑戰】頻道《實大》▷ https://goo.gl/IuUqfv 我的Facebook粉專▷https://goo.gl/I3D5PO 在Instagram追蹤我▷https://goo.gl/56C3uF (fb/IG  實大...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/bU4PgZid5I0/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/bU4PgZid5I0/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/bU4PgZid5I0/hqdefault.jpg","width":480,"height":360}},"channelTitle":"Star lyric","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/RlS7Az1T4SP6fJkMzZESS_RUuTg\"","id":{"kind":"youtube#video","videoId":"GHil-Vwh4tI"},"snippet":{"publishedAt":"2007-12-16T04:40:17.000Z","channelId":"UCIbDspSDT5b2kn-emmqQ1GA","title":"5566+周傳雄~哈薩雅琪MV","description":"哈薩雅琪MTV.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/GHil-Vwh4tI/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/GHil-Vwh4tI/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/GHil-Vwh4tI/hqdefault.jpg","width":480,"height":360}},"channelTitle":"Belial1128","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/sm50cTVzElCe53ZG0gSVnqZtL9U\"","id":{"kind":"youtube#video","videoId":"Vpfitm3Vvy4"},"snippet":{"publishedAt":"2017-03-08T14:27:14.000Z","channelId":"UCPCxPvGdAmxnsRP-j5vyDQg","title":"百萬大歌星20090125～5566","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/Vpfitm3Vvy4/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/Vpfitm3Vvy4/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/Vpfitm3Vvy4/hqdefault.jpg","width":480,"height":360}},"channelTitle":"陳宥靜","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/o1cQ0HJ3mFPfxGZdZW8HqU63iyk\"","id":{"kind":"youtube#video","videoId":"57Bg-FVM6Yk"},"snippet":{"publishedAt":"2015-09-02T20:01:28.000Z","channelId":"UCwtbuh8E0o_McoTXAJ6YjGA","title":"5566-動感+快歌精選2002~2008","description":"拖了一年多終於生出了56的快歌精選抒情慢歌版在此-http://ppt.cc/fDXyW 音樂都是自己慢慢接的~ 1.C'est si bon 2.好久不見3.最後一秒4.無所謂5.Easy come...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/57Bg-FVM6Yk/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/57Bg-FVM6Yk/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/57Bg-FVM6Yk/hqdefault.jpg","width":480,"height":360}},"channelTitle":"abow2022","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/Ot9pxeTkcEB4D-IOZ-29xUSZwAQ\"","id":{"kind":"youtube#video","videoId":"2qICuz5TTgY"},"snippet":{"publishedAt":"2016-10-15T14:35:13.000Z","channelId":"UCVgjSPvkkVgswcw9i64VTsw","title":"5566-放手一搏 KTV","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/2qICuz5TTgY/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/2qICuz5TTgY/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/2qICuz5TTgY/hqdefault.jpg","width":480,"height":360}},"channelTitle":"吳振煌","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/x7Gs53H-siq75fTtbbZYNLxADHM\"","id":{"kind":"youtube#video","videoId":"WodoH13QndI"},"snippet":{"publishedAt":"2017-04-22T16:00:03.000Z","channelId":"UC0mQThvR6AogBSdGPDcP5kg","title":"飢餓遊戲／台南市／5566 孫協志 王仁甫 許孟哲／EP11完整版20170101","description":"來賓：陳為民、Terry、楊雅筑、何嘉文、大飛、黃沐妍16:46 第一關／達標賽：以競標方式取得挑戰資格，任務達標可獲得早餐充飢。 》任務一：2次...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/WodoH13QndI/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/WodoH13QndI/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/WodoH13QndI/hqdefault.jpg","width":480,"height":360}},"channelTitle":"【中視 CTV】官方頻道","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/uD5eSxpVlDoRi7hl-8T18A7M4qM\"","id":{"kind":"youtube#video","videoId":"x1m7HXjLmVw"},"snippet":{"publishedAt":"2013-07-01T13:07:21.000Z","channelId":"UCI0vf1NLklWuabm_hJ3_gnA","title":"KTV)5566 守候","description":"","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/x1m7HXjLmVw/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/x1m7HXjLmVw/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/x1m7HXjLmVw/hqdefault.jpg","width":480,"height":360}},"channelTitle":"patty 呂","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/RDH0GNo9sIRenvnhrCxq0jkXAog\"","id":{"kind":"youtube#video","videoId":"_hel2XE6_6k"},"snippet":{"publishedAt":"2016-12-30T23:57:46.000Z","channelId":"UCO3bfz4KY6zGT5IOaFJuxpA","title":"台中跨年雙主場 5566倒數.玖壹壹壓軸│中視新聞20161231","description":"迎接2017，全台瘋跨年。在台中更在麗寶樂園，和洲際棒球場，有雙主場。兩邊卡司互尬，不但請來911壓軸，還找來5566再次合體陪倒數，要重現經典畫...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/_hel2XE6_6k/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/_hel2XE6_6k/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/_hel2XE6_6k/hqdefault.jpg","width":480,"height":360}},"channelTitle":"中視新聞","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/By2SENiaIg-GVpvoKuPn9iL16N0\"","id":{"kind":"youtube#video","videoId":"SbvpXWi3DtI"},"snippet":{"publishedAt":"2016-10-27T11:56:07.000Z","channelId":"UCG5m2Qo9Lkj6O24FqODi46w","title":"我愛黑澀會 20050923 5566","description":"純分享無營利版權歸電視台所有.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/SbvpXWi3DtI/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/SbvpXWi3DtI/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/SbvpXWi3DtI/hqdefault.jpg","width":480,"height":360}},"channelTitle":"utlmutlm","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/1kOrU4A21k7iBi1ShL7p11jJoHc\"","id":{"kind":"youtube#video","videoId":"q4IaAgRuhHU"},"snippet":{"publishedAt":"2013-05-08T12:05:15.000Z","channelId":"UCteaDDEN52v-fXhLZKAhwug","title":"5566 imitating Cyndi Wang 5566模仿王心凌《爱你》","description":"SAM IS TOO CUTE and jason and zax are hilarious ^^ also tony as sam wang wijdeiuwjgu.","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/q4IaAgRuhHU/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/q4IaAgRuhHU/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/q4IaAgRuhHU/hqdefault.jpg","width":480,"height":360}},"channelTitle":"HanFanyVal","liveBroadcastContent":"none"}},{"kind":"youtube#searchResult","etag":"\"m2yskBQFythfE4irbTIeOgYYfBU/0HpWZOMUzyIbb6SN2UhrvCSb6Yk\"","id":{"kind":"youtube#video","videoId":"er8LUi7Cxh0"},"snippet":{"publishedAt":"2016-11-03T05:01:22.000Z","channelId":"UCeF6JP-sxqJnznCAQo_K66g","title":"噓！星聞 x 5566史上最噴飯快問快答","description":"噓編掛保證，沒看到噴飯噓編吃皮鞋~(來啊繼續彼此傷害啊) 從超噴飯到超18禁，5566不要害我們被檢舉嘿~ 更多精彩影片請鎖定【噓！星聞】粉絲團www.f...","thumbnails":{"default":{"url":"https://i.ytimg.com/vi/er8LUi7Cxh0/default.jpg","width":120,"height":90},"medium":{"url":"https://i.ytimg.com/vi/er8LUi7Cxh0/mqdefault.jpg","width":320,"height":180},"high":{"url":"https://i.ytimg.com/vi/er8LUi7Cxh0/hqdefault.jpg","width":480,"height":360}},"channelTitle":"噓!星聞","liveBroadcastContent":"none"}}]
     */

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
        private int durationToMilionSecond;
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

        //TODO
        public MediaMetadataCompat getMediaMetadata() {
            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, getId().getVideoId())
                    .putString("VIDEO_ID", getId().getVideoId())
                    .putString(CUSTOM_METADATA_TRACK_SOURCE, getUrl())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getSnippet().getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, getSnippet().getDescription())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, getVideoDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, getSnippet().getTitle())
                    .build();
        }

        public void setDataSource(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
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
