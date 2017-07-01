package com.team.mera.audiogram.audioprocessing;

import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Denis on 01.07.2017.
 */

public class AudioProcessor {

    public static boolean transcodeTracks(List<Track> tracks) {

        Track result = new Track();
        result.setTrackDescription(tracks.get(0).getTrackDescription());
        result.setBytes(tracks.get(0).getBytes());

        for (int i = 1; i < tracks.size(); i++) {
            byte[] resData = overlayAudio(result, tracks.get(i), 48000, tracks.get(i).getTrackDescription().getStartTime());
            result.setTrackDescription(tracks.get(i).getTrackDescription());
            result.setBytes(resData);
        }

        return AudioUtils.saveSamplesToWAV(result.getBytes(), 48000, "overlaid_");
    }

    private static byte[] joinAudio(Track track1, Track track2, int sampleRate) {
        byte[] data1 = track1.getBytes();
        byte[] data2 = track2.getBytes();

        byte[] resData = new byte[data1.length + data2.length];

        System.arraycopy(data1, 0, resData, 0, data1.length);
        System.arraycopy(data2, 0, resData, data1.length, data2.length);

        return resData;
    }

    private static byte[] overlayAudio(Track track1, Track track2, int sampleRate, float startTimeInSec) {

        byte[] data1 = track1.getBytes();
        byte[] data2 = track2.getBytes();

        //data1 = LinearInterpolation.interpolate(sampleRate, (int) (track1.getScaleDuration() * sampleRate), data1);
        //data2 = LinearInterpolation.interpolate(sampleRate, (int) (track2.getScaleDuration() * sampleRate), data2);

        int bitsPerSample = 16;
        int channels = 1;
        int byteRate = sampleRate * channels * bitsPerSample / 8;

        int startIndex = (int) (startTimeInSec * byteRate);

        int resultLength = startIndex + data2.length;
        resultLength = resultLength < data1.length ? data1.length : resultLength;

        byte[] resData = new byte[resultLength];

        System.arraycopy(data1, 0, resData, 0, data1.length);

        for (int i = startIndex; i < data2.length; i++) {
            resData[i] = (byte) (resData[i] + data2[i - startIndex]);
        }

        return resData;
    }

}
