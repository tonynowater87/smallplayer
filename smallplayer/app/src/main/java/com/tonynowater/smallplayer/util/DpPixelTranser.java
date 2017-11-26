package com.tonynowater.smallplayer.util;

import android.content.res.Resources;

/**
 * Created by tonyliao on 2017/11/20.
 */
public class DpPixelTranser {
    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
