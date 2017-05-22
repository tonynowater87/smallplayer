package com.tonynowater.smallplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.tonynowater.smallplayer.service.ResourceHelper;

/**
 * Created by tonyliao on 2017/5/19.
 */
public class ProgressView extends View {

    private Paint mPaint = null;
    private int mProgress = 0;
    private int mMax = 100;

    public ProgressView(Context context) {
        super(context);
        initial();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initial();
    }

    private void initial() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_orange_light));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = 0;
        float top = getTop();
        final int right = (int)((float)getWidth() * mProgress / mMax);
        float bottom = getBottom();
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }

    public int getmProgress() {
        return this.mProgress;
    }

    public void setmMax(int mMax) {
        this.mMax = mMax;
        invalidate();
    }

    public int getmMax() {
        return this.mMax;
    }
}
