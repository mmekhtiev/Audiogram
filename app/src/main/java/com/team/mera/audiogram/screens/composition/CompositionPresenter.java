package com.team.mera.audiogram.screens.composition;

import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;
import java.util.List;

public interface CompositionPresenter {
    void compose(List<Track> list);
}
