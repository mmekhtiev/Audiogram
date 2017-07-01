package com.team.mera.audiogram.screens.addrecord;

import com.team.mera.audiogram.audioprocessing.MicrophoneWriter;

public class RecordPresenterImpl implements RecordPresenter, MicrophoneWriter.WriteListener {

    private RecordView mView;

    private MicrophoneWriter mMicrophoneWriter;

    public RecordPresenterImpl(RecordView view) {
        mView = view;
        mMicrophoneWriter = new MicrophoneWriter(this);
    }

    @Override
    public void startRecord() {
        mMicrophoneWriter.start();
    }

    @Override
    public void stopRecord() {
        mMicrophoneWriter.stop();
    }

    @Override
    public void release() {
        mView = null;
        mMicrophoneWriter.release();
    }

    @Override
    public void onWavWrited(String path) {
        if (mView != null) {
            mView.onFileWrited(path);
        }
    }
}
