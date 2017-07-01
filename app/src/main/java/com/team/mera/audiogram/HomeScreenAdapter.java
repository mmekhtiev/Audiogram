package com.team.mera.audiogram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.List;

/**
 * Created by Denis on 01.07.2017.
 */

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {

    private List<TrackDescription> mTracks;
    private Context mContext;
    private HomeItemsListener mListener;

    public interface HomeItemsListener {
        void onShareClicked(String path);
        void onPlayClicked(TrackDescription track);
        void onStopClicked();
    }

    public HomeScreenAdapter(List<TrackDescription> tracks, HomeItemsListener listener) {
        mTracks = tracks;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TrackDescription track = mTracks.get(position);
        holder.mNameTV.setText(track.getName());
        holder.mPathTV.setText(track.getPath());

        holder.mShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShareClicked(track.getPath());
            }
        });

        holder.mPlayImage.setImageResource(track.isPlaying() ? R.drawable.ic_stop_black_24dp : R.drawable.ic_play_circle_outline_black_24dp);

        holder.mPlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!track.isPlaying()) {
                    mListener.onPlayClicked(track);
                } else {
                    mListener.onStopClicked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTV;
        private TextView mPathTV;
        private ImageView mShareImage;
        private ImageView mPlayImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mPathTV = (TextView) itemView.findViewById(R.id.path);
            mShareImage = (ImageView) itemView.findViewById(R.id.share);
            mPlayImage = (ImageView) itemView.findViewById(R.id.play);
        }
    }
}
