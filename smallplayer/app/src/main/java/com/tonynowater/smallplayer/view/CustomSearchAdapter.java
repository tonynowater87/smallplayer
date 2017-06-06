package com.tonynowater.smallplayer.view;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutU2bsuggestionAdapterListItemBinding;

/**
 * Created by tonynowater on 2017/6/6.
 */

public class CustomSearchAdapter extends CursorAdapter {
    private static final String TAG = CustomSearchAdapter.class.getSimpleName();
    public static final String COLUMN_SUGGESTION = "suggestion";
    private LayoutU2bsuggestionAdapterListItemBinding mBinding;

    public CustomSearchAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public CustomSearchAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_u2bsuggestion_adapter_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvLeft = mBinding.tvLayoutU2bsuggestionAdapterListItem;
        viewHolder.tvRight = mBinding.tvRightLayoutU2bsuggestionAdapterListItem;
        mBinding.getRoot().setTag(viewHolder);
        return mBinding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (view == null || cursor == null) {
            Log.d(TAG, "bindView: null");
            return;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String suggestion = cursor.getString(cursor.getColumnIndex(COLUMN_SUGGESTION));
        if (TextUtils.equals(context.getString(R.string.search_bar_hint), suggestion)) {
            viewHolder.tvRight.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvRight.setVisibility(View.GONE);
        }

        viewHolder.tvLeft.setText(suggestion);
    }

    public static class ViewHolder {
        TextView tvLeft;
        TextView tvRight;
    }
}
