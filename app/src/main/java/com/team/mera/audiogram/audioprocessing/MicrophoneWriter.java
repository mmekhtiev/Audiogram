package com.team.mera.audiogram.audioprocessing;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.team.mera.audiogram.audioprocessing.AudioUtils.makeDirs;
import static com.team.mera.audiogram.audioprocessing.AudioUtils.saveFileToWAV;

/**
 * Created by Denis on 30.06.2017.
 */

public class MicrophoneWriter {

    private static final String TAG = MicrophoneWriter.class.getSimpleName();

    private AudioRecord mAudioRecorder;
    private int mBuffSize;

    private static int[] mSampleRates = new int[] {48000, 44100, 22050, 11025, 8000};

    private Thread mRecordingThread = null;
    private int mSampleRate;

    public MicrophoneWriter() {
        mBuffSize = AudioRecord.getMinBufferSize(getValidSampleRates(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecorder = findAudioRecord();
        mSampleRate = getValidSampleRates();
    }

    public void start() {
        mAudioRecorder.startRecording();

        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    public void stop() {
        if (mRecordingThread != null) {
            mRecordingThread.interrupt();
        }
        if (mAudioRecorder != null) {
            mAudioRecorder.stop();
        }
    }

    private void writeAudioDataToFile() {
        File file = makeDirs("pcm", "", "tmp");

        short sData[] = new short[mBuffSize / 2];

        FileOutputStream os;
        try {
            os = new FileOutputStream(file.getPath());

            while (!mRecordingThread.isInterrupted()) {
                // gets the voice output from microphone to byte format

                mAudioRecorder.read(sData, 0, mBuffSize / 2);

                try {
                    // // writes the data to file from buffer
                    // // stores the voice buffer
                    byte bData[] = short2byte(sData);
                    os.write(bData, 0, mBuffSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            os.close();

            saveFileToWAV(file, mSampleRate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private int getValidSampleRates() {
        for (int rate : new int[] {44100, 22050, 11025, 16000, 8000}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                return rate;
            }
        }
        return 8000;
    }

    private AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO}) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);

                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, mBuffSize);

                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                            Log.d(TAG, "Return recorder");
                            return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }
}
