package com.team.mera.audiogram.screens.common;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;

public interface FragmentListener {
    void open(BaseFragment fragment, boolean useBackStack);
    void back();

    void setDescriptions(ArrayList<TrackDescription> descriptions);
    ArrayList<TrackDescription> getDescriptions();
}
