package com.realjamapps.yamusicapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.realjamapps.yamusicapp.R;
import com.realjamapps.yamusicapp.models.Genres;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GenresListViewAdapter extends RecyclerView.Adapter<GenresListViewAdapter.ViewHolder> {

    private ArrayList<Genres> gList;

    public GenresListViewAdapter(ArrayList<Genres> students) {
        this.gList = students;

    }

    public void refresh(ArrayList<Genres> listData) {
        this.gList = listData;
        System.gc();
        notifyDataSetChanged();
    }

    @Override
    public GenresListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filter_genre, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tvName.setText(gList.get(position).getName());

        viewHolder.chkSelected.setChecked(gList.get(position).isSelected());

        viewHolder.chkSelected.setTag(gList.get(position));

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Genres contact = (Genres) cb.getTag();

                contact.setSelected(cb.isChecked());
                gList.get(pos).setSelected(cb.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return gList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvName) TextView tvName;
        @Bind(R.id.chkSelected) CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }
    }

    public ArrayList<Genres> getGenresList() {
        return gList;
    }
}
