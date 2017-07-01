package com.team.mera.audiogram.screens.addrecord;

import com.team.mera.audiogram.MicrophoneWriter;

public class RecordPresenterImpl implements RecordPresenter {

    private RecordView mView;

    private MicrophoneWriter mMicrophoneWriter;

    public RecordPresenterImpl(RecordView view) {
        mView = view;
        mMicrophoneWriter = new MicrophoneWriter();
    }

    @Override
    public void startRecord() {
        mMicrophoneWriter.start();
    }

    @Override
    public void stopRecord() {
        mMicrophoneWriter.stop();
    }
}
