package com.example.projectMP3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//для теста стоит сделать тестовый файл медия который всегда будет доступен
public class UtilsTest {

    // получение пути файла для данных из raw директории
    public static String getCacheFile(String nameF, String path, Context context) throws IOException {
        File cacheFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + nameF+"."+path);
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.sample3);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException("Could not open file", e);
        }
        return cacheFile.getAbsolutePath();
    }

    public static final String SONG_TITLE = "sample3";
    public static final String SONG_ALBUM = "sample3";
    //добавляем тестовый муызкальный файл из assets в медиахранилище
    public static Uri insertTestAudio(Context instrumentationContext) throws Exception
    {

        if (!checkAudioFile(instrumentationContext, SONG_TITLE)) {
            String filename = "sample3";
            String extfilename = "wav";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.TITLE, SONG_TITLE);
            contentValues.put(MediaStore.MediaColumns.ALBUM, SONG_ALBUM);
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Audio.Media.DATA, getCacheFile(filename, extfilename, instrumentationContext));
            return instrumentationContext.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        return null;
    }

    //удалем тестовый муызкальный файл из медиахранилища
    public static void delAudio(Context instrumentationContext, Uri uri) throws Exception
    {
        // Удаляет строку (согласно URI)
        instrumentationContext.getContentResolver().delete(uri, null, null);
    }

    //выдает первый альбом(1) или наименование (0)
    public static String getFirstAudioData(Context instrumentationContext, int type)
    {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] Projection =  new String[1];
        if (type == 1) {
             Projection[0] = MediaStore.Audio.Media.ALBUM;
        }
        else
        {
            Projection[0] = MediaStore.Audio.Media.TITLE;
        }
        Cursor cursor = instrumentationContext.getContentResolver().query(uri, Projection,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                return cursor.getString(0);
            }
            // Закрываем Cursor (освобождаем память).
            cursor.close();
        }
        return null;
    }
    //удаляем удаленные файлы
    public static Boolean checkAudioFile(Context instrumentationContext, String Title)
    {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] Projection = {
                MediaStore.Audio.Media.TITLE
        };
        Cursor cursor = instrumentationContext.getContentResolver().query(uri, Projection,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String TITLE = cursor.getString(0);
                if (Title.equals(TITLE)) return true;
            }
            // Закрываем Cursor (освобождаем память).
            cursor.close();
        }
        return false;
    }

    //удаляем удаленные файлы
    public static void delDeletedAudioFile(Context instrumentationContext)
    {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] Projection = {
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = instrumentationContext.getContentResolver().query(uri, Projection,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String DATA = cursor.getString(0);
                File f = new File(DATA);
                if (!f.exists()) // проверяем есть ли старые файлы которых на самом деле нету
                {
                    try {
                        delAudio(instrumentationContext,Uri.fromFile(f));
                    }
                    catch (Exception e)
                    {}
                }
            }
            // Закрываем Cursor (освобождаем память).
            cursor.close();
        }
    }


}
