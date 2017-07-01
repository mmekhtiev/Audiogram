package com.team.mera.audiogram.audioprocessing;

import com.team.mera.audiogram.models.TrackDescription;

import java.io.File;
import java.io.IOException;

/**
 * Created by Denis on 01.07.2017.
 */

public class AudioProcessor {

    public static void joinAudio(String url1, String url2, int sampleRate) {
        File file1 = new File(url1);
        File file2 = new File(url2);

        try {
            byte[] data1 = AudioUtils.getBytesFromFile(file1);
            byte[] data2 = AudioUtils.getBytesFromFile(file2);

            byte[] resData = new byte[data1.length + data2.length];

            System.arraycopy(data1, 0, resData, 0, data1.length);
            System.arraycopy(data2, 0, resData, data1.length, data2.length);

            AudioUtils.saveSamplesToWAV(resData, sampleRate, "joined_");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void overlayAudio(TrackDescription track1, TrackDescription track2, int sampleRate, float startTimeInSec) {
        File file1 = new File(track1.getPath());
        File file2 = new File(track2.getPath());

        try {
            byte[] data1 = AudioUtils.getBytesFromFile(file1);
            byte[] data2 = AudioUtils.getBytesFromFile(file2);

            data1 = LinearInterpolation.interpolate(sampleRate, (int) (track1.getScaleDuration() * sampleRate), data1);
            data2 = LinearInterpolation.interpolate(sampleRate, (int) (track2.getScaleDuration() * sampleRate), data2);

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

            AudioUtils.saveSamplesToWAV(resData, sampleRate, "overlaid_");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
