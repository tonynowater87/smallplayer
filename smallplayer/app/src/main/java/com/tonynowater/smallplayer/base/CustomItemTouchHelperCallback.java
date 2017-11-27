package com.tonynowater.smallplayer.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.util.DpPixelTranser;
import com.tonynowater.smallplayer.util.Logger;

// TODO: 2017/11/26 二段式刪除畫面沒出來，點擊也無效... 
/**
 * RecyclerView 滑動刪除，拖曳處理
 * Created by tonynowater on 2017/6/1.
 */
public class CustomItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private static final int TOUCH_BUTTON_AREA = DpPixelTranser.dpToPx(100);
    private static final int TXT_PADDING = DpPixelTranser.dpToPx(10);
    private static final int TXT_SIZE = DpPixelTranser.dpToPx(20);
    private static final int BUTTON_BACKGROUND_COLOR = ContextCompat.getColor(MyApplication.getContext(), R.color.colorPrimary);
    private static final String DEL_TXT = "刪除";

    private final ItemTouchHelperAdapter itemTouchHelperAdapter;
    private RecyclerView.ViewHolder currentItemViewHolder;
    private ButtonsState mEButtonsState = ButtonsState.GONE;
    private float mDx = 0;
    private boolean mIsNeedCheckToDismiss = true;
    private boolean mIsTouching = false;
    private Rect mRectTouchArea;

    public CustomItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) {
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemTouchHelperAdapter.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mIsNeedCheckToDismiss) {
            return;
        }
        itemTouchHelperAdapter.onDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (mIsTouching) {
            mIsTouching = mEButtonsState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Logger.getInstance().i("dX", dX + "");
        if (mIsNeedCheckToDismiss) {
            if (mEButtonsState != ButtonsState.GONE) {
                if (mEButtonsState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, TOUCH_BUTTON_AREA);
                else if (mEButtonsState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -TOUCH_BUTTON_AREA);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        } else {
            mDx = dX;
            if (dX > 0) {
                //左往右滑
                mEButtonsState = ButtonsState.LEFT_VISIBLE;
                currentItemViewHolder = viewHolder;
            } else if (dX < 0) {
                //右往左滑
                mEButtonsState = ButtonsState.RIGHT_VISIBLE;
                currentItemViewHolder = viewHolder;
            } else {
                mEButtonsState = ButtonsState.GONE;
                currentItemViewHolder = null;
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDx = dX;
                if (dX < 0) {
                    mEButtonsState = ButtonsState.RIGHT_VISIBLE;
                    currentItemViewHolder = viewHolder;
                }
                else if (dX > 0) {
                    mEButtonsState = ButtonsState.LEFT_VISIBLE;
                    currentItemViewHolder = viewHolder;
                }

                mIsTouching = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (mIsTouching) {
                    if (mEButtonsState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void setTouchDownListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CustomItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);//重刷更新畫面
                    setItemsClickable(recyclerView, true);
                    mIsTouching = false;
                    if (mRectTouchArea != null && mRectTouchArea.contains((int)event.getX(), (int)event.getY())) {
                        itemTouchHelperAdapter.onDismiss(viewHolder.getAdapterPosition());
                    }

                    mEButtonsState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

        /**
         * 使用ItemDecorator的Canvas去畫刪除的畫面
         * @param canvas
         */
    public void onDraw(Canvas canvas) {
        if (currentItemViewHolder != null) {
            drawButtons(canvas, currentItemViewHolder);
        }
    }

    /**
     * 畫刪除按鈕及文字
     * @param c
     * @param viewHolder
     */
    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        Logger.getInstance().i("drawBtn", "");
        View itemView = viewHolder.itemView;
        mRectTouchArea = null;
        switch (mEButtonsState) {
            case GONE:
                mRectTouchArea = new Rect();
                break;
            case LEFT_VISIBLE:
                mRectTouchArea = new Rect(itemView.getLeft(), itemView.getTop(), (int) mDx, itemView.getBottom());
                break;
            case RIGHT_VISIBLE:
                mRectTouchArea = new Rect((itemView.getRight() + (int) mDx), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                break;
        }
        Paint paint = new Paint();
        paint.setColor(BUTTON_BACKGROUND_COLOR);
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(mRectTouchArea, paint);
        drawTxt(c, itemView);
    }

    private void drawTxt(Canvas c, View itemView) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(TXT_SIZE);
        paint.setTextAlign(Paint.Align.CENTER);
        float textWidth = paint.measureText(DEL_TXT);
        float txtHalfHeight = (paint.descent() + paint.ascent()) / 2;
        float centerY = itemView.getTop() + (itemView.getBottom() - itemView.getTop()) / 2;
        switch (mEButtonsState) {
            case GONE:
                break;
            case LEFT_VISIBLE:
                c.drawText(DEL_TXT, itemView.getLeft() + textWidth + TXT_PADDING, centerY - txtHalfHeight, paint);
                break;
            case RIGHT_VISIBLE:
                c.drawText(DEL_TXT, itemView.getRight() - textWidth - TXT_PADDING, centerY - txtHalfHeight, paint);
                break;
        }
    }
}

enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}
