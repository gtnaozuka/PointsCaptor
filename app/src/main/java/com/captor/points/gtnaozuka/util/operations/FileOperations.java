package com.captor.points.gtnaozuka.util.operations;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;

import com.captor.points.gtnaozuka.pointscaptor.R;
import com.captor.points.gtnaozuka.util.DisplayToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileOperations {

    public static String FILES_PATH;
    public static String SENT_PATH;
    public static final String THUMBS_CACHE_DIR = "thumbs";
    public static final String IMAGES_CACHE_DIR = "images";

    public static void definePaths(String packageName) {
        String subPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "Android" + File.separator + "data" +
                File.separator + packageName + File.separator;
        FILES_PATH = subPath + "files";
        SENT_PATH = subPath + "sent";
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

    public static File storeFile(Activity activity, String path, String content) {
        if (!isWritable(activity))
            return null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
        Date d = new Date();
        String filename = "PC_" + df.format(d) + ".dat";

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

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
        Date d = new Date();
        String filename = "GM_" + df.format(d) + ".png";

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

    public static String[] listAllFiles(Activity activity, String path, String extension) {
        if (!isReadable(activity))
            return null;

        File filePath = new File(path);

        ArrayList<String> fileList = new ArrayList<>();
        for (String filename : filePath.list()) {
            if (getFileExtension(filename).equals(extension))
                fileList.add(filename);
        }
        String[] files = new String[fileList.size()];
        files = fileList.toArray(files);

        return DataOperations.reverse(files);
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

    public static String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0)
            return filename.substring(filename.lastIndexOf(".") + 1);
        else
            return "";
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
