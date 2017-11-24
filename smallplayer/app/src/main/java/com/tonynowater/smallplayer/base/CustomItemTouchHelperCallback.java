package com.tonynowater.smallplayer.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.util.Logger;

/**
 * Created by tonynowater on 2017/6/1.
 */

public class CustomItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter itemTouchHelperAdapter;

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
        itemTouchHelperAdapter.onDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Logger.getInstance().i("dX", dX + "");

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        String drawTxt = "刪除";
        float centerY = viewHolder.itemView.getTop() + (viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2;
        float textWidth = paint.measureText(drawTxt);
        if (dX > 0 && isCurrentlyActive) {
            //向右滑
            ColorDrawable bg = new ColorDrawable(ContextCompat.getColor(recyclerView.getContext(), R.color.colorAccent));
            bg.setBounds(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), (int) dX, viewHolder.itemView.getBottom());
            bg.draw(c);
            if (dX < textWidth) {
                c.drawText(drawTxt, viewHolder.itemView.getLeft(), centerY, paint);
            } else {
                c.drawText(drawTxt, viewHolder.itemView.getLeft(), centerY, paint);
            }

        } else if (dX < 0 && isCurrentlyActive) {
            //向左滑
            ColorDrawable bg = new ColorDrawable(ContextCompat.getColor(recyclerView.getContext(), R.color.colorFourth));
            bg.setBounds(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
            bg.draw(c);

            if (Math.abs(dX) < textWidth) {
                c.drawText(drawTxt, viewHolder.itemView.getRight() + dX, centerY, paint);
            } else {
                c.drawText(drawTxt, viewHolder.itemView.getRight() - textWidth, centerY, paint);
            }
        }
    }

}
