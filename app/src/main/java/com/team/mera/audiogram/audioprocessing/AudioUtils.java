package com.team.mera.audiogram.audioprocessing;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by Denis on 30.06.2017.
 */

public class AudioUtils {

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, Math.min(bytes.length - offset, 512 * 1024))) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static void saveFileToWAV(File fileToConvert, long sampleRate) {
        try {
            long subChunk1Size = 16;
            int bitsPerSample = 16;
            int format = 1;
            long channels = 1;
            long byteRate = sampleRate * channels * bitsPerSample / 8;
            int blockAlign = (int) (channels * bitsPerSample / 8);

            byte[] clipData = AudioUtils.getBytesFromFile(fileToConvert);

            long dataSize = clipData.length;
            long chunk2Size =  dataSize * channels * bitsPerSample / 8;
            long chunkSize = 36 + chunk2Size;

            File file = makeDirs("wav", "", "samples");

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSamplesToWAV(byte[] clipData, long sampleRate, String prefix) {
        try {
            long subChunk1Size = 16;
            int bitsPerSample = 16;
            int format = 1;
            long channels = 1;
            long byteRate = sampleRate * channels * bitsPerSample / 8;
            int blockAlign = (int) (channels * bitsPerSample / 8);

            long dataSize = clipData.length;
            long chunk2Size =  dataSize * channels * bitsPerSample / 8;
            long chunkSize = 36 + chunk2Size;

            File file = makeDirs("wav", prefix, "results");

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File makeDirs(String extension, String prefix, String folder) {
        String externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Audiogram/" + folder;
        File externalStorageFile = new File(externalStorage);

        if (!externalStorageFile.exists()) {
            externalStorageFile.mkdirs();
        }

        File file = new File(externalStorageFile.getPath() + "/Sample_" + prefix + Calendar.getInstance().getTime().getTime() + "." + extension);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
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
