package com.realjamapps.yamusicapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;
import java.util.List;

public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter.ViewHolder> {

    private ArrayList<Performer> mItems;
    OnItemClickListener mItemClickListener;
    private Context mContext;

    public MainGridAdapter(Context context, ArrayList<Performer> listData) {
        mItems = new ArrayList<>();
        this.mContext = context;
        this.mItems = listData;
    }

    public void refresh(ArrayList<Performer> listData) {
        this.mItems = listData;
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Performer performer = mItems.get(position);

        Uri uri = Uri.parse(performer.getmCoverSmall());

        holder.draweeView.setImageURI(uri);

        holder.tv_performer_name.setText(performer.getmName());
        holder.tv_performer_name.setTypeface(null, Typeface.BOLD);

        List genres = performer.getmGenres();

        String genresString = TextUtils.join(", ", genres);
        holder.tv_performer_genres.setText(genresString);

        String tracksAlbumsString = (String.valueOf(performer.getmAlbums())+ " "
                + mContext.getString(R.string.albums) + ", "
                + String.valueOf(performer.getmTracks()) + " "
                + mContext.getString(R.string.tracks));
        holder.tv_performer_tracks_and_albums.setText(tracksAlbumsString);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout placeMainHolder;
        public TextView tv_performer_name, tv_performer_genres,
                tv_performer_tracks_and_albums;

        SimpleDraweeView draweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            placeMainHolder = (RelativeLayout) itemView.findViewById(R.id.placeMainHolder);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.iv_image_url);
            tv_performer_name = (TextView) itemView.findViewById(R.id.tv_performer_name);
            tv_performer_genres = (TextView) itemView.findViewById(R.id.tv_performer_genres);
            tv_performer_tracks_and_albums = (TextView) itemView.findViewById(R.id.tv_performer_tracks_and_albums);

            placeMainHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                Performer item = mItems.get(getAdapterPosition());
                mItemClickListener.onItemClick(itemView, item.getmId());
            }
        }
    }



}
