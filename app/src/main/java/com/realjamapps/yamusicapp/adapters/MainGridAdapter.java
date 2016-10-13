package com.realjamapps.yamusicapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;
import java.util.List;

public class MainGridAdapter extends RecyclerView.Adapter<MainViewHolder> implements RecyclerView.OnItemTouchListener {

    private ArrayList<Performer> mItems;
    private final OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public MainGridAdapter(Context context, ArrayList<Performer> listData, OnItemClickListener listener) {
        mItems = new ArrayList<>();
        this.mContext = context;
        this.mItems = listData;
        this.mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    public Performer getArticle(int i) {
        return mItems.get(i);
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
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {

        final Performer performer = mItems.get(position);

        Uri uri = Uri.parse(performer.getmCoverSmall());

        holder.draweeView.setImageURI(uri);

        holder.tvPerformerName.setText(performer.getmName());
        holder.tvPerformerName.setTypeface(null, Typeface.BOLD);

        List genres = performer.getmGenres();

        String genresString = TextUtils.join(", ", genres);
        holder.tvPerformerGenres.setText(genresString);

        int albumsCount = performer.getmAlbums();
        int tracksCount = performer.getmTracks();
        String quantityAlbums = mContext.getResources().getQuantityString(R.plurals.plurals_albums, albumsCount);
        String quantityTracks = mContext.getResources().getQuantityString(R.plurals.plurals_tracks, tracksCount);

        String tracksAlbumsString = (String.valueOf(albumsCount)+ " "
                + quantityAlbums + ", "
                + String.valueOf(tracksCount) + " "
                + quantityTracks);

        holder.tvPerformerTracksAlbums.setText(tracksAlbumsString);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mItemClickListener = mItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
}
