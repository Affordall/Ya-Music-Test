package com.realjamapps.yamusicapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.models.Genres;

import java.util.ArrayList;


public class GenresListViewAdapter extends RecyclerView.Adapter<GenresViewHolder> {

    private ArrayList<Genres> gList;

    public GenresListViewAdapter(ArrayList<Genres> genres) {
        this.gList = genres;

    }

    public void refresh(ArrayList<Genres> listData) {
        this.gList = listData;
        System.gc();
        notifyDataSetChanged();
    }

    @Override
    public GenresViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filter_genre, null);

        return new GenresViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final GenresViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tvName.setText(gList.get(position).getName());

        viewHolder.chkSelected.setChecked(gList.get(position).isSelected());

        viewHolder.chkSelected.setTag(gList.get(position));

        viewHolder.chkSelected.setOnClickListener(v -> {
            CheckBox cb = (CheckBox) v;
            Genres contact = (Genres) cb.getTag();
            contact.setSelected(cb.isChecked());
            gList.get(pos).setSelected(cb.isChecked());
        });

    }

    @Override
    public int getItemCount() {
        return gList.size();
    }

    public ArrayList<Genres> getGenresList() {
        return gList;
    }
}
