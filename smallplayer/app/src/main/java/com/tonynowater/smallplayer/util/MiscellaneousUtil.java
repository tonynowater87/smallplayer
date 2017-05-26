package com.tonynowater.smallplayer.util;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/27.
 */

public class MiscellaneousUtil {
    private MiscellaneousUtil() {
    }

    public static boolean isListOK(List<?> list) {
        return list != null && list.size() > 0;
    }
}
