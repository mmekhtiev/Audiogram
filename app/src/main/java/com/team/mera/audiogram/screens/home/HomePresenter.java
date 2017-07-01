package com.team.mera.audiogram.screens.home;

import com.team.mera.audiogram.models.TrackDescription;

/**
 * Created by Denis on 01.07.2017.
 */

public interface HomePresenter {
    void play(TrackDescription trackDescription);
    void stop();
    void release();
}
