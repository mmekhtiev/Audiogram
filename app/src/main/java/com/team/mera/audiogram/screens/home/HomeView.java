package com.team.mera.audiogram.screens.home;

import com.team.mera.audiogram.models.TrackDescription;

/**
 * Created by Denis on 01.07.2017.
 */

public interface HomeView {
    void onPlayingCompleted(TrackDescription trackDescription);
}
