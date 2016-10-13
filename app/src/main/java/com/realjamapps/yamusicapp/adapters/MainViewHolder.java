package com.realjamapps.yamusicapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


class MainViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_performer_name) TextView tvPerformerName;
    @BindView(R.id.tv_performer_genres) TextView tvPerformerGenres;
    @BindView(R.id.tv_performer_tracks_and_albums) TextView tvPerformerTracksAlbums;
    @BindView(R.id.iv_image_url) SimpleDraweeView draweeView;

    MainViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        itemView.setClickable(true);
    }
}
