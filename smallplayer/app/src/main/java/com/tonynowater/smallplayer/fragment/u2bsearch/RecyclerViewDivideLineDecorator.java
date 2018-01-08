package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tonynowater.smallplayer.R;

/**
 * 畫RecyclerView的分隔線
 * Created by tonyliao on 2017/4/30.
 */
public class RecyclerViewDivideLineDecorator extends RecyclerView.ItemDecoration {

    private Drawable mDividerDrawable;

    public RecyclerViewDivideLineDecorator(Context context) {
        mDividerDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorFourth));
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
    }

    /**
     * All decorations are drawn before drawing the items.
     * In case you want to draw decorations after drawing the view, override onDrawOver() method instead of onDraw().
     */
    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        View child;
        RecyclerView.LayoutParams params;
        int iDividerLeft = parent.getPaddingLeft();
        int iDividerRight = parent.getWidth() - parent.getPaddingRight();
        int childcount = parent.getChildCount();
        for (int i = 0; i < childcount; i++) {
            child = parent.getChildAt(i);
            if (i == 0) {
                mDividerDrawable.setBounds(iDividerLeft, child.getTop(), iDividerRight, child.getTop() + mDividerDrawable.getIntrinsicHeight());
                mDividerDrawable.draw(canvas);
            }
            params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int iDividerTop = child.getBottom() + params.bottomMargin;
            int iDividerBottom = iDividerTop + mDividerDrawable.getIntrinsicHeight();
            mDividerDrawable.setBounds(iDividerLeft, iDividerTop, iDividerRight, iDividerBottom);
            mDividerDrawable.draw(canvas);
        }
    }
}
