package com.team.mera.audiogram.utils;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoundsManager {

    private SoundPool mSoundPool;
    private List<Integer> mSampleIds = new ArrayList<>();
    private List<Integer> mStreamIds = new ArrayList<>();

    private static int MAX_STREAMS_PER_POOL = 15;

    private List<TrackDescription> mTrackList = new ArrayList<TrackDescription>();
    private HashMap<Integer, TrackDescription> mTrackDescriptionMap = new HashMap<>();

    public SoundsManager() {
        mSoundPool = createSoundPool();
    }

    public void loadTracks(List<TrackDescription> tracks) {
        Log.d("MyLog", "loadTracks: " + tracks);
        stop();
        mTrackList.addAll(tracks);
    }

    public void loadTrack(TrackDescription track) {
        mTrackList.add(track);
    }

    public void playAll() {
        int size = mTrackList.size();
        for (int i = 0; i < size; i++) {
            int sampleId = mSoundPool.load(mTrackList.get(i).getPath(), 1);
            if (sampleId > 0) {
                mTrackDescriptionMap.put(sampleId, mTrackList.get(i));
                mSampleIds.add(sampleId);
            }
        }
    }

    public void stop() {
        for (int i = 0; i < mStreamIds.size(); i++) {
            mSoundPool.stop(mStreamIds.get(i));
        }
        mSoundPool.release();
        mSoundPool = createSoundPool();
        mTrackList.clear();
        mTrackDescriptionMap.clear();
        mLoadTrackCount = 0;
    }

    private int mLoadTrackCount = 0;

    private SoundPool createSoundPool() {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS_PER_POOL)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS_PER_POOL, android.media.AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoadTrackCount++;
                if (mLoadTrackCount == mTrackDescriptionMap.size()) {
                    for (Integer id : mSampleIds) {
                        TrackDescription track = mTrackDescriptionMap.get(id);
                        if (track != null) {
                            playTrack(track, id);
                        }
                    }
                }
            }
        });
        return soundPool;
    }


    private void playTrack(final TrackDescription track, final int sampleId) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int streamId = mSoundPool.play(
                        sampleId,
                        track.getScaleVolume(),
                        track.getScaleVolume(),
                        1,
                        track.getIsRepeated(),
                        track.getScaleDuration());
                mStreamIds.add(streamId);
            }
        }, (int) track.getStartTime() * 1000);
    }
}
