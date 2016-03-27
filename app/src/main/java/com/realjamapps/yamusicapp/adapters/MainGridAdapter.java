package com.realjamapps.yamusicapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.realjamapps.yamusicapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
        //System.gc();
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Performer performer = mItems.get(position);

        Uri uri = Uri.parse(performer.getmCoverBig());

        holder.draweeView.setImageURI(uri);

        /*Picasso.with(mContext)
                .load(performer.getmCoverBig())
                .error(R.color.gray_overlay)
                .placeholder(R.color.gray_overlay)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(performer.getmCoverBig())
                                .error(R.color.gray_overlay)
                                .placeholder(R.color.gray_overlay)
                                .into(holder.imgThumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });*/

        holder.tv_performer_name.setText(performer.getmName());
        holder.tv_performer_name.setTypeface(null, Typeface.BOLD);

        List genres = performer.getmGenres();

        String genresString = TextUtils.join(", ", genres);
        holder.tv_performer_genres.setText(genresString);

        String tracksAlbumsString = (String.valueOf(performer.getmAlbums())+ " albums, " + String.valueOf(performer.getmTracks()) + " tracks");
        holder.tv_performer_tracks_and_albums.setText(tracksAlbumsString);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
        //return 0;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public LinearLayout placeMainHolder;
        public LinearLayout placeInfoHolder;
        RelativeLayout placeMainHolder;
        public ImageView imgThumbnail;
        public TextView tv_performer_name, tv_performer_genres,
                tv_performer_tracks_and_albums;

        SimpleDraweeView draweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            //placeMainHolder = (LinearLayout) itemView.findViewById(R.id.placeMainHolder);
            placeMainHolder = (RelativeLayout) itemView.findViewById(R.id.placeMainHolder);

            imgThumbnail = (ImageView) itemView.findViewById(R.id.iv_image_url);

            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.iv_image_url);

            placeInfoHolder = (LinearLayout) itemView.findViewById(R.id.placeInfoHolder);

            tv_performer_name = (TextView) itemView.findViewById(R.id.tv_performer_name);
            tv_performer_genres = (TextView) itemView.findViewById(R.id.tv_performer_genres);
            tv_performer_tracks_and_albums = (TextView) itemView.findViewById(R.id.tv_performer_tracks_and_albums);

            placeMainHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                Performer item = mItems.get(getAdapterPosition());
                mItemClickListener.onItemClick(itemView, (int)item.getmId());
            }
        }
    }



}
