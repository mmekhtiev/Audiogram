package com.team.mera.audiogram.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    // Create file from input stream
    public static File getFile(Context context, InputStream input) {
        File file = new File(context.getCacheDir(), "cacheFileAppeal.srl");
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    private String[] getAudioList(Context context) {
        final Cursor mCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        String[] mAudioPath = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                mAudioPath[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Log.d(TAG, "getAudioList: path: " + mAudioPath[i]);
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return songs;
    }
}
