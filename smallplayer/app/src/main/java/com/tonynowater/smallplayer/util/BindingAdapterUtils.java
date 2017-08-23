package com.tonynowater.smallplayer.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by tony10532 on 2017/8/23.
 */
public class BindingAdapterUtils {

    /**
     * 讓DataBinding能夠設定R檔資料到ImageView
     * @param imageView
     * @param resource
     */
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
