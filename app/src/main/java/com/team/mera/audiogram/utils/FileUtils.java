package com.team.mera.audiogram.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;

import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.home.HomeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

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

    public static ArrayList<TrackDescription> getAudioList(Context context) {
        final Cursor mCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        ArrayList<TrackDescription> songsList = new ArrayList<>();

        songsList.addAll(HomeFragment.getAudioList());

        if (mCursor.moveToFirst()) {
            do {
                TrackDescription song = new TrackDescription();
                song.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setColor(generateRandomColor());
                songsList.add(song);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return songsList;
    }

    private static int generateRandomColor() {
        Random rand = new Random();
        return Color.argb(255, rand.nextInt(150) + 50, rand.nextInt(150) + 50, rand.nextInt(150) + 50);
    }
}
