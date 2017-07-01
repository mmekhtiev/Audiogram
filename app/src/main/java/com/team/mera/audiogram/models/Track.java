package com.team.mera.audiogram.models;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.team.mera.audiogram.screens.common.TrackListener;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Get from https://github.com/google/ringdroid
 */
public class Track {
    private static final int SAMPLES_PER_FRAME = 1024;

    private TrackListener mTrackListener = null;

    private float[] mHeights;
    private int mColor;
    private String mPath;
    private int mDuration;
    private byte[] mBytes;
    private TrackDescription mTrackDescription;

    // A Track object should only be created using the static methods create()
    private Track() {
    }

    // Create and return a Track object using the file path
    public static Track create(String path, TrackListener trackListener) throws IOException {
        Track track = new Track();
        track.setTrackListener(trackListener);
        track.readFile(path);
        return track;
    }

    public float[] getHeights() {
        return mHeights;
    }

    public String getPath() {
        return mPath;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getDuration() {
        return mDuration;
    }

    private void setTrackListener(TrackListener trackListener) {
        mTrackListener = trackListener;
    }

    private void readFile(String path) throws IOException {
        mPath = path;

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(path);

        int size = (int) new File(path).length();
        int numTracks = extractor.getTrackCount();

        MediaFormat format = null;

        // Find and select the first audio track present in the file
        for (int i = 0; i < numTracks; i++) {
            format = extractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                extractor.selectTrack(i);
                break;
            }

            if (i == numTracks - 1) {
                throw new IOException("No audio track found");
            }
        }

        int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        mDuration = (int) format.getLong(MediaFormat.KEY_DURATION);

        MediaCodec codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
        codec.configure(format, null, null, 0);
        codec.start();

        int sampleSize;
        int totalSize = 0;
        int decodedSamplesSize = 0;  // size of the output buffer containing decoded samples.

        byte[] decodedSamples = null;

        ByteBuffer[] inputBuffers = codec.getInputBuffers();
        ByteBuffer[] outputBuffers = codec.getOutputBuffers();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        long sampleTime;

        boolean isDone = false;

        // Set the size of the decoded samples buffer to 1MB (~6sec of a stereo stream at 44.1kHz).
        // For longer streams, the buffer size will be increased later on, calculating a rough
        // estimate of the total size needed to store all the samples in order to resize the buffer
        // only once.
        ByteBuffer decodedBytes = ByteBuffer.allocate(1<<20);
        Boolean firstSampleData = true;
        while (true) {
            // read data from file and feed it to the decoder input buffers.
            int inputBufferIndex = codec.dequeueInputBuffer(100);
            if (!isDone && inputBufferIndex >= 0) {
                sampleSize = extractor.readSampleData(inputBuffers[inputBufferIndex], 0);
                if (firstSampleData
                        && format.getString(MediaFormat.KEY_MIME).equals("audio/mp4a-latm")
                        && sampleSize == 2) {
                    // For some reasons on some devices (e.g. the Samsung S3) you should not
                    // provide the first two bytes of an AAC stream, otherwise the MediaCodec will
                    // crash. These two bytes do not contain music data but basic info on the
                    // stream (e.g. channel configuration and sampling frequency), and skipping them
                    // seems OK with other devices (MediaCodec has already been configured and
                    // already knows these parameters).
                    extractor.advance();
                    totalSize += sampleSize;
                } else if (sampleSize < 0) {
                    // All samples have been read.
                    codec.queueInputBuffer(inputBufferIndex, 0, 0, -1, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isDone = true;
                } else {
                    sampleTime = extractor.getSampleTime();
                    codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, sampleTime, 0);
                    extractor.advance();
                    totalSize += sampleSize;
                    if (mTrackListener != null) {
                        if (!mTrackListener.onProgress((float)(totalSize) / size)) {
                            // We are asked to stop reading the file. Returning immediately. The
                            // Track object is invalid and should NOT be used afterward!
                            extractor.release();
                            extractor = null;
                            codec.stop();
                            codec.release();
                            codec = null;
                            return;
                        }
                    }
                }
                firstSampleData = false;
            }

            // Get decoded stream from the decoder output buffers.
            int outputBufferIndex = codec.dequeueOutputBuffer(info, 100);
            if (outputBufferIndex >= 0 && info.size > 0) {
                if (decodedSamplesSize < info.size) {
                    decodedSamplesSize = info.size;
                    decodedSamples = new byte[decodedSamplesSize];
                }
                outputBuffers[outputBufferIndex].get(decodedSamples, 0, info.size);
                outputBuffers[outputBufferIndex].clear();
                // Check if buffer is big enough. Resize it if it's too small.
                if (decodedBytes.remaining() < info.size) {
                    int position = decodedBytes.position();
                    int newSize = (int)((position * (1.0 * size / totalSize)) * 1.2);
                    ByteBuffer newDecodedBytes = ByteBuffer.allocate(newSize);
                    decodedBytes.rewind();
                    newDecodedBytes.put(decodedBytes);
                    decodedBytes = newDecodedBytes;
                    decodedBytes.position(position);
                }
                decodedBytes.put(decodedSamples, 0, info.size);
                codec.releaseOutputBuffer(outputBufferIndex, false);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = codec.getOutputBuffers();
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // Subsequent data will conform to new format.
                // We could check that codec.getOutputFormat(), which is the new output format,
                // is what we expect.
            }

            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                // We got all the decoded data from the decoder. Stop here.
                break;
            }
        }

        int numSamples = decodedBytes.position() / (channels * 2);  // One sample = 2 bytes.
        decodedBytes.rewind();
        decodedBytes.order(ByteOrder.LITTLE_ENDIAN);

        ShortBuffer decodedBuffer = decodedBytes.asShortBuffer();

        extractor.release();
        extractor = null;
        codec.stop();
        codec.release();
        codec = null;

        int numFrames = numSamples / SAMPLES_PER_FRAME;
        if (numSamples % SAMPLES_PER_FRAME != 0){
            numFrames++;
        }

        mHeights = new float[numFrames];

        int gain, value;
        for (int i = 0; i < numFrames; i++){
            gain = -1;

            for(int j = 0; j < SAMPLES_PER_FRAME; j++) {
                value = 0;

                for (int k = 0; k < channels; k++) {
                    if (decodedBuffer.remaining() > 0) {
                        value += Math.abs(decodedBuffer.get());
                    }
                }

                value /= channels;
                if (gain < value) {
                    gain = value;
                }
            }

            mHeights[i] = gain / 65025f;
        }

        decodedBuffer.position(0);

        //short[] samples = decodedBytes.array();

        mBytes = decodedBytes.array();
        //for (int i = 0; i < samples.length; i++) {
        //    mBytes[i] = (byte) samples[i];
        //}

        decodedBuffer.clear();
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public TrackDescription getTrackDescription() {
        return mTrackDescription;
    }

    public void setTrackDescription(TrackDescription trackDescription) {
        mTrackDescription = trackDescription;
    }
}
