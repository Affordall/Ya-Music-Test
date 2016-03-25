package com.realjamapps.yamusicapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realjamapps.yamusicapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.realjamapps.yamusicapp.models.Performer;

import java.util.ArrayList;

public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter.ViewHolder> {

    private ArrayList<Performer> mItems;
    OnItemClickListener mItemClickListener;
    private Context mContext;

    public MainGridAdapter(Context context, ArrayList<Performer> listData) {
        mItems = new ArrayList<>();
        this.mContext = context;
        this.mItems = listData;
    }

    protected void refresh(ArrayList<Performer> listData) {
        this.mItems = listData;
        System.gc();
        notifyDataSetChanged();
    }

    protected void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) { //final here

        final Performer item = mItems.get(position); //final here

       /* Picasso.with(mContext)
                .load(item.getmImageUrl())
                .error(R.color.gray_overlay)
                .placeholder(R.color.gray_overlay)
                .networkPolicy(NetworkPolicy.OFFLINE)
                        //.into(holder.imgThumbnail);

                .into(holder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(item.getmImageUrl())
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


       /* holder.tv_item_name.setText(item.getmItemName());
        String srt = item.getmNewPrice() + " \u20BD";
        holder.tv_new_price.setText(srt);
        holder.tv_new_price.setTypeface(null, Typeface.BOLD);*/
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
        public LinearLayout placeHolder;
        public LinearLayout placeNameHolder;
        public RelativeLayout placePriceHolder;

        public ImageView imgThumbnail;
        public TextView tv_item_name;
        public TextView tv_old_price, tv_new_price;

        public ViewHolder(View itemView) {
            super(itemView);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            tv_item_name = (TextView) itemView.findViewById(R.id.tv_item_name);

            placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.iv_image_url);

            placePriceHolder = (RelativeLayout) itemView.findViewById(R.id.placePriceHolder);
            //tv_old_price = (TextView) itemView.findViewById(R.id.tv_old_price);
            tv_new_price = (TextView) itemView.findViewById(R.id.tv_new_price);

            placeHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                Performer item = mItems.get(getAdapterPosition());
                //mItemClickListener.onItemClick(itemView, item.getmIdFromAPI());
            }
        }
    }



}
