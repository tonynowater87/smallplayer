package com.tonynowater.smallplayer.service.notification;


import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;

/**
 * Created by tonynowater on 2018/1/16.
 */

public class ChannelConstant {
    public static final String DEFAULT_CHANNEL_GROUP_ID = "com.tonynowater.smallplayer.channel.group.default";
    public static final String DEFAULT_CHANNEL_GROUP_NAME = MyApplication.getMyString(R.string.channel_group_default_name);
    public static final String PLAYER_CHANNEL_ID = "com.tonynowater.smallplayer.channel.default";
    public static final String PLAYER_CHANNEL_NAME = MyApplication.getMyString(R.string.channel_player_name);
    public static final String DOWNLOAD_CHANNEL_ID = "com.tonynowater.smallplayer.channel.download";
    public static final String DOWNLOAD_CHANNEL_NAME = MyApplication.getMyString(R.string.channel_download_name);
}
