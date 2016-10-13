package com.realjamapps.yamusicapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


class GenresViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.chkSelected) CheckBox chkSelected;

    GenresViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
