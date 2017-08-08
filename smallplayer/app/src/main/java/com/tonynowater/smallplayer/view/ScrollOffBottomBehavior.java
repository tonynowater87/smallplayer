package com.tonynowater.smallplayer.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 給FloatingActionButton能在CoordinatorLayout裡上下滑動隱藏
 *
 * Created by tonynowater on 2017/8/8.
 */
public class ScrollOffBottomBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final String TAG = ScrollOffBottomBehavior.class.getSimpleName();
    private int mViewHeight;
    private ObjectAnimator mAnimatorTransition;
    private ObjectAnimator mAnimatorRotation;
    private ObjectAnimator mAnimatorScaleX;
    private ObjectAnimator mAnimatorScaleY;
    private AnimatorSet mAnimatorSet;

    public ScrollOffBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        mViewHeight = child.getHeight() + ((CoordinatorLayout.LayoutParams)child.getLayoutParams()).bottomMargin;
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (mAnimatorSet == null || !mAnimatorSet.isRunning()) {
            int totalScroll = dyConsumed + dyUnconsumed;
            int targetTraslation = totalScroll > 0 ? mViewHeight : 0;
            int duration = totalScroll > 0 ? 500 : 250;
            float scale = 1 - targetTraslation / 100;
            if (scale > 1) {
                scale = 1;
            }
            if (scale < 0) {
                scale = 0;
            }

            Log.d(TAG, "onNestedScroll : " + targetTraslation);
            //設定浮動按鈕屬性動畫
            mAnimatorSet = new AnimatorSet();
            mAnimatorTransition = ObjectAnimator.ofFloat(child, "translationY", targetTraslation);
            mAnimatorTransition.setDuration(duration);
            mAnimatorRotation = ObjectAnimator.ofFloat(child, "rotation", targetTraslation);
            mAnimatorRotation.setDuration(duration);
            mAnimatorScaleX = ObjectAnimator.ofFloat(child, "scaleX", scale);
            mAnimatorScaleX.setDuration(duration);
            mAnimatorScaleY = ObjectAnimator.ofFloat(child, "scaleY", scale);
            mAnimatorScaleY.setDuration(duration);
            mAnimatorSet.play(mAnimatorTransition)
                    .with(mAnimatorRotation)
                    .with(mAnimatorScaleX)
                    .with(mAnimatorScaleY);
            mAnimatorSet.start();
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
