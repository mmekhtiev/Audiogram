package com.team.mera.audiogram.screens.preview;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.team.mera.audiogram.models.TrackDescription;

/**
 * Created by User on 02.07.2017.
 */

public class PreviewPresenterImpl implements PreviewPresenter, MediaPlayer.OnCompletionListener {

    private PreviewView mView;

    private MediaPlayer mPlayer;

    private TrackDescription mCurrentTrackDescription;

    public PreviewPresenterImpl(PreviewView view) {
        mView = view;
    }

    @Override
    public void play(TrackDescription trackDescription) {
        try {
            if (mPlayer != null) {
                stop();
            }
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setDataSource(trackDescription.getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            if (mView != null) {
                mView.onPlayingCompleted(trackDescription);
            }
        }

        mCurrentTrackDescription = trackDescription;
    }

    @Override
    public void stop() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mView != null) {
            mView.onPlayingCompleted(mCurrentTrackDescription);
        }
    }

    @Override
    public void release() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mView = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
    }
}
