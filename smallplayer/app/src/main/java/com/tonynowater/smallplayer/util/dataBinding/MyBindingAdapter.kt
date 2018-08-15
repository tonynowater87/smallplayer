package com.tonynowater.smallplayer.util.dataBinding

import android.databinding.BindingAdapter
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.tonynowater.smallplayer.R

/**
 * Created by tonyliao on 2018/8/15.
 */
object MyBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = "imageUrlHandler")
    fun setImage(imageView: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.context).load(url).into(imageView)
        } else {
            Glide.with(imageView.context).load(R.drawable.ic_default_art).into(imageView)
        }
    }
}