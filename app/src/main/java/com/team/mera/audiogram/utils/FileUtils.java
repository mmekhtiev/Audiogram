package com.team.mera.audiogram.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

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
}
