package br.com.civico.mais.saude.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Jônatas Rodrigues on 30/11/2016.
 */
public class InternalStorage {

    private InternalStorage() {}

    public static void writeObject(Context context, String key, Object object) throws IOException {
        deleteCache(context,key);
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }

    public static void deleteCache(Context context, String key) {
        try {
            File file =context.getFileStreamPath(key);
            file.delete();
            if(file.exists()) {
                file.getCanonicalFile().delete();
                if (file.exists()) {
                    context.deleteFile(file.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static long getFreeSpace(){
        StatFs stats = new StatFs("/data");
        long availableBlocks = stats.getAvailableBlocksLong();
        long blockSizeInBytes = stats.getBlockSizeLong();
        return availableBlocks * blockSizeInBytes;
    }

    public static boolean hasCache(Context context, String key) {
        File file =context.getFileStreamPath(key);
        return file.exists();
    }
}
