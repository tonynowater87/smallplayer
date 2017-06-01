package com.tonynowater.smallplayer.base;

/**
 * Created by tonynowater on 2017/6/1.
 */
public interface ItemTouchHelperAdapter {
    /**
     * 刪除項目
     * @param position
     */
    void onDismiss(int position);

    /**
     * 移動項目
     */
    void onMove(int from, int to);
}
