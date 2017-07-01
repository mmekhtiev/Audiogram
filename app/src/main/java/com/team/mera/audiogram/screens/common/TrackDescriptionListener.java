package com.team.mera.audiogram.screens.common;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;

/**
 * Created by u01 on 01.07.2017.
 */

public interface TrackDescriptionListener {
    void onUpdate(TrackDescription description, int position);
    ArrayList<TrackDescription> getDescriptions();
}
