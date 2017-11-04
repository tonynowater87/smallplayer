package com.tonynowater.smallplayer.fragment.u2bsearch;

import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;

/**
 * Created by tony10532 on 2017/5/19.
 */
public enum EnumU2BSearchType {
   VIDEO(U2BApiDefine.U2B_API_VIDEO_URL)
   , PLAYLIST(U2BApiDefine.U2B_API_PLAYLIST_URL)
   , PLAYLISTVIDEO(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL)
   , USER_LIST(U2BApiDefine.U2B_USER_PLAYLIST_QUERY_URL)
   , USER_CHANNELS(U2BApiDefine.U2B_API_USER_FAVORITE_VIDEO_URL);

   String url;
   EnumU2BSearchType(String url) {
      this.url = url;
   }

   public String getUrl() {
      return url;
   }
}
