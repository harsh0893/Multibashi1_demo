package com.harsh.sainih.multibashi1.utils;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final int BUFFER_SIZE = 4096;

    /**
     * method which downloads audios and stores them in the internal storage
     *
     * @param url
     * @param context
     * @return status of the download
     * @throws IOException
     */

    public static Boolean downloadAudioFromHttpUrl(String url, Context context) throws IOException {
        InputStream in;
        Uri builtUri = Uri.parse(url).buildUpon()
                .build();
        URL builturl = new URL(builtUri.toString());
        HttpURLConnection connection = (HttpURLConnection) builturl.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.addRequestProperty("Accept", "audio/aac,audio/mp3");
        try {
            int responseStatusCode = connection.getResponseCode();
            if (responseStatusCode != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            String fileName = Uri.parse(url).getLastPathSegment();
            String[] filelist = context.fileList();
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].equals(fileName)) {
                    in.close();
                    return true;
                }

            }
            OutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = in.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }


            os.close();
            in.close();
        } catch (Exception e) {


            e.printStackTrace();
            return false;

        } finally {
            connection.disconnect();
        }
        return true;
    }
}
