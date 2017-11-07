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
import android.widget.ImageView;
import android.widget.TextView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutU2bsuggestionAdapterListItemBinding;

/**
 * Created by tonynowater on 2017/6/6.
 */

public class  CustomSearchAdapter extends CursorAdapter {
    private static final String TAG = CustomSearchAdapter.class.getSimpleName();
    public static final String COLUMN_SUGGESTION = "suggestion";
    private LayoutU2bsuggestionAdapterListItemBinding mBinding;
    private int mIconId = R.drawable.ic_search_white_36dp;

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
        viewHolder.ivIcon = mBinding.ivLayoutU2bsuggestionAdapterListIcon;
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
            viewHolder.tvLeft.setTextAppearance(context, android.R.style.TextAppearance_Material_Small);
            viewHolder.ivIcon.setVisibility(View.GONE);
            viewHolder.tvRight.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvLeft.setTextAppearance(context, android.R.style.TextAppearance_Material_Medium);
            viewHolder.ivIcon.setVisibility(View.VISIBLE);
            viewHolder.ivIcon.setImageDrawable(context.getDrawable(mIconId));
            viewHolder.tvRight.setVisibility(View.GONE);
        }

        viewHolder.tvLeft.setText(suggestion);
    }

    public static class ViewHolder {
        ImageView ivIcon;
        TextView tvLeft;
        TextView tvRight;
    }

    public void setImageIcon(int iconId) {
        mIconId = iconId;
    }
}
