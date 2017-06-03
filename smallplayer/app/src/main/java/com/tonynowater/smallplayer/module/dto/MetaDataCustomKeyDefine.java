package com.tonynowater.smallplayer.module.dto;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

/**
 * Created by tonynowater on 2017/5/28.
 */
public class MetaDataCustomKeyDefine {
    public static final String CUSTOM_METADATA_KEY_SOURCE = "CUSTOM_METADATA_KEY_SOURCE";
    public static final String CUSTOM_METADATA_KEY_IS_LOCAL = "CUSTOM_METADATA_KEY_IS_LOCAL";
    public static final String ISLOCAL = "1";
    public static final String ISNOTLOCAL = "0";

    /**
     * 檢查是否為本地音樂
     */
    public static boolean isLocal(@NonNull MediaMetadataCompat mediaMetadataCompat) {
        return TextUtils.equals(mediaMetadataCompat.getString(CUSTOM_METADATA_KEY_IS_LOCAL), ISLOCAL);
    }
}
