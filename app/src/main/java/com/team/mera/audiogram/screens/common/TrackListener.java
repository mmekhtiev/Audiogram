package com.team.mera.audiogram.screens.common;

import com.team.mera.audiogram.models.Track;

public interface TrackListener {
    boolean onProgress(double progress);
    void onSuccess(Track track);
}
