package com.captor.points.gtnaozuka.util.operations;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;

import com.captor.points.gtnaozuka.entity.DataItem;
import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DisplayToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileOperations {

    public static String DATA_PATH;
    public static String PHOTOS_PATH;
    public static String SENT_PATH;

    public static String CACHE_PATH;
    public static final String THUMBS_CACHE_FOLDER = "thumbs";
    public static final String IMAGES_CACHE_FOLDER = "images";

    public static void definePaths(String packageName) {
        String subPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "Android" + File.separator + "data" +
                File.separator + packageName;

        String filesSubPath = subPath + File.separator + "files";
        DATA_PATH = filesSubPath + File.separator + "data";
        PHOTOS_PATH = filesSubPath + File.separator + "photos";
        SENT_PATH = filesSubPath + File.separator + "sent";
        CACHE_PATH = subPath + File.separator + "cache";
    }

    public static void delete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }

    public static File storeFile(Activity activity, String path, String content, String type) {
        if (!isWritable(activity))
            return null;

        String filename = getFilename(activity, type, ".dat");

        File f = null;
        try {
            File fDir = new File(path);
            fDir.mkdirs();

            f = new File(fDir, filename);
            if (!f.exists())
                f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static File storePhoto(Activity activity, String path, Bitmap snapshot) {
        if (!isWritable(activity))
            return null;

        String filename = getFilename(activity, activity.getResources().getString(R.string.photo), ".png");

        File f = null;
        try {
            File fDir = new File(path);
            fDir.mkdirs();

            f = new File(fDir, filename);
            if (!f.exists())
                f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            snapshot.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static String getFilename(Context context, String type, String extension) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", LanguageOperations.getCurrentLocale(context));
        Date d = new Date();
        return type + "_" + sdf.format(d) + extension;
    }

    public static String[] listAllFiles(Activity activity, final String path) {
        if (!isReadable(activity))
            return null;

        File filePath = new File(path);

        String[] files = filePath.list();
        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = new File(path + File.separator + o1.toString());
                File f2 = new File(path + File.separator + o2.toString());
                if (f1.lastModified() > f2.lastModified()) {
                    return -1;
                } else if (f1.lastModified() < f2.lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });
        return files;
    }

    public static List<DataItem> splitAttrs(String[] filenames) {
        List<DataItem> data = new ArrayList<>();
        for (String filename : filenames) {
            String[] strings = filename.substring(0, filename.lastIndexOf('.')).split("_");
            DataItem di = new DataItem(strings[0], strings[1]);
            data.add(di);
        }
        return data;
    }

    public static ArrayList<String> read(String folder, String filename) {
        String filePath = folder + File.separator + filename;

        ArrayList<String> content = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static File editFile(String filename, String content) {
        File f = null;
        try {
            f = new File(FileOperations.DATA_PATH + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static boolean isWritable(Activity activity) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            new Handler().post(new DisplayToast(activity.getApplicationContext(), activity.getResources().getString(R.string.unauthorized_access)));
            return false;
        }
        return true;
    }

    private static boolean isReadable(Activity activity) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            new Handler().post(new DisplayToast(activity.getApplicationContext(), activity.getResources().getString(R.string.unauthorized_access)));
            return false;
        }
        return true;
    }
}
