package com.team.mera.audiogram.screens.composition;

import com.team.mera.audiogram.audioprocessing.AudioProcessor;
import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;
import java.util.List;

public class ComposerPresenterImpl implements CompositionPresenter {

    private CompositionView mView;

    public ComposerPresenterImpl(CompositionView view) {
        mView = view;
    }

    @Override
    public void compose(List<Track> list) {
        if (AudioProcessor.transcodeTracks(list)) {
            mView.onSuccess();
        } else {
            mView.onError();
        }
    }
}
