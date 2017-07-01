package com.team.mera.audiogram;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import static com.team.mera.audiogram.GetBytesFromFile.getBytesFromFile;

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
        mRecordingThread.interrupt();
        mAudioRecorder.stop();
    }

    private void writeAudioDataToFile() {
        File file = makeDirs("pcm");

        short sData[] = new short[mBuffSize / 2];
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveWAV(file, mSampleRate);
    }

    private File makeDirs(String extension) {
        String externalStorage = Environment.getExternalStorageDirectory().getPath() + "/Audiogram";
        File externalStorageFile = new File(externalStorage);

        if (!externalStorageFile.exists()) {
            externalStorageFile.mkdirs();
        }

        File file = new File(externalStorageFile.getPath() + "/Sample_" + Calendar.getInstance().getTime().getTime() + "." + extension);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
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
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
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

    private void saveWAV(File fileToConvert, long sampleRate) {
        try {
            long subChunk1Size = 16;
            int bitsPerSample = 16;
            int format = 1;
            long channels = 1;
            long byteRate = sampleRate * channels * bitsPerSample / 8;
            int blockAlign = (int) (channels * bitsPerSample / 8);

            byte[] clipData = getBytesFromFile(fileToConvert);

            long dataSize = clipData.length;
            long chunk2Size =  dataSize * channels * bitsPerSample / 8;
            long chunkSize = 36 + chunk2Size;

            File file = makeDirs("wav");

            OutputStream os;
            os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream outFile = new DataOutputStream(bos);

            outFile.writeBytes("RIFF");                                 // 00 - RIFF
            outFile.write(intToByteArray((int)chunkSize), 0, 4);      // 04 - how big is the rest of this file?
            outFile.writeBytes("WAVE");                                 // 08 - WAVE
            outFile.writeBytes("fmt ");                                 // 12 - fmt
            outFile.write(intToByteArray((int)subChunk1Size), 0, 4);  // 16 - size of this chunk
            outFile.write(shortToByteArray((short)format), 0, 2);     // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
            outFile.write(shortToByteArray((short)channels), 0, 2);   // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
            outFile.write(intToByteArray((int)sampleRate), 0, 4);     // 24 - samples per second (numbers per second)
            outFile.write(intToByteArray((int)byteRate), 0, 4);       // 28 - bytes per second
            outFile.write(shortToByteArray((short)blockAlign), 0, 2); // 32 - # of bytes in one sample, for all channels
            outFile.write(shortToByteArray((short)bitsPerSample), 0, 2);  // 34 - how many bits in a sample(number)?  usually 16 or 24
            outFile.writeBytes("data");                                 // 36 - data
            outFile.write(intToByteArray((int)dataSize), 0, 4);       // 40 - how big is this data chunk
            outFile.write(clipData);                                    // 44 - the actual data itself - just a long string of numbers

            outFile.flush();
            outFile.close();

            fileToConvert.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] intToByteArray(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    private static byte[] shortToByteArray(short data) {
        return new byte[]{(byte)(data & 0xff),(byte)((data >>> 8) & 0xff)};
    }
}
