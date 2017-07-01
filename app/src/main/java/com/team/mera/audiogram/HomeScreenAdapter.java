package com.team.mera.audiogram;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.List;

/**
 * Created by Denis on 01.07.2017.
 */

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {

    private List<TrackDescription> mTracks;

    public HomeScreenAdapter(List<TrackDescription> tracks) {
        mTracks = tracks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrackDescription track = mTracks.get(position);
        holder.mNameTV.setText(track.getName());
        holder.mPathTV.setText(track.getPath());
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTV;
        private TextView mPathTV;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mPathTV = (TextView) itemView.findViewById(R.id.path);
        }
    }
}
