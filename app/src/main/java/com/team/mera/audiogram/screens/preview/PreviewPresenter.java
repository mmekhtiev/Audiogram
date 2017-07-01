package com.team.mera.audiogram.screens.preview;

import com.team.mera.audiogram.models.TrackDescription;

/**
 * Created by User on 02.07.2017.
 */

public interface PreviewPresenter {
    void play(TrackDescription trackDescription);
    void stop();
    void release();
}
