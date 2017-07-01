package com.team.mera.audiogram.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.TrackDescriptionListener;
import com.team.mera.audiogram.utils.NotificationUtils;
import com.team.mera.audiogram.widgets.pinchview.PinchLayout;
import com.team.mera.audiogram.widgets.pinchview.PinchListener;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends ArrayAdapter<Track> implements PinchListener, TrackDescriptionListener {
    private List<Track> mTrackList;
    private ArrayList<TrackDescription> mTrackDescriptions;
    private int mActivePosition;

    public TrackAdapter(@NonNull Context context, List<Track> list) {
        super(context, 0, list);
        mTrackList = list;
        mTrackDescriptions = new ArrayList<>();

        for (Track track : list) {
            addDescription(track);
        }
    }

    public void add(Track track) {
        mTrackList.add(track);
        addDescription(track);
        notifyDataSetChanged();
    }

    public void zoomIn() {
        if (PinchLayout.mParentScaleX < 3) {
            PinchLayout.mParentScaleX++;

            notifyDataSetChanged();
        } else {
            NotificationUtils.showToast(getContext(), "Max zoom");
        }
    }

    public void zoomOut() {
        if (PinchLayout.mParentScaleX > 1) {
            PinchLayout.mParentScaleX--;

            notifyDataSetChanged();
        } else {
            NotificationUtils.showToast(getContext(), "Min zoom");
        }
    }

    private void addDescription(Track track) {
        TrackDescription description = new TrackDescription();
        description.setPath(track.getPath());
        description.setStartTime(0);
        description.setScaleDuration(1);
        description.setScaleVolume(1);
        description.setIsRepeated(false);

        mTrackDescriptions.add(description);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = new TrackLayout(getContext(), this, position);

            ListView.LayoutParams params = new ListView.LayoutParams(
                    ListView.LayoutParams.MATCH_PARENT,
                    ListView.LayoutParams.WRAP_CONTENT);

            convertView.setLayoutParams(params);
        }

        ((TrackLayout) convertView).setTrack(mTrackList.get(position));
        ((TrackLayout) convertView).setActive(position == mActivePosition);

        return convertView;
    }

    @Override
    public void onActive(int position) {
        mActivePosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onUpdate(TrackDescription description, int position) {
        if (description != null) {
            mTrackDescriptions.set(position, description);
        }
    }

    @Override
    public ArrayList<TrackDescription> getDescriptions() {
        return mTrackDescriptions;
    }

    @Override
    public List<Track> getTracks() {
        for (int i = 0; i < mTrackDescriptions.size(); i++) {
            mTrackList.get(i).setTrackDescription(mTrackDescriptions.get(i));
        }
        return mTrackList;
    }
}
