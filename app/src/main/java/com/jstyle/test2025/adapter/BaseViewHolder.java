package com.jstyle.test2025.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2018/6/27.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.mView=itemView;
    }

    public View getView() {
        return mView;
    }
}
