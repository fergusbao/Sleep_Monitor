package edu.njit.junyi.sleep_monitor_v1.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.njit.junyi.sleep_monitor_v1.model.SoundModel;

/**
 * Created by junyi on 3/22/18.
 */

public class JSONHelper {
    private static String fileName;
    private static final String TAG = "JSONHelper";
    private final static String path =
            Environment.getExternalStorageDirectory() +  File.separator + "Sleep Monitor" + File.separator;

    public static void createFolder() {
        // Create folder
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    public static void createFile(String file_name) {
        // Create file
        File file = new File(path + File.separator + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exportToJSON(String fileName, DataItem dataItem) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(dataItem);
        Log.i(TAG, "exportToJSON: " + jsonString);

        FileOutputStream fileOutputStream = null;
        File file = new File(path, fileName);

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static DataItem importFromJSON(String fileName) {

        FileReader reader = null;

        try {
            File file = new File(path, fileName);
            reader = new FileReader(file);
            Gson gson = new Gson();
            DataItem dataItem = gson.fromJson(reader, DataItem.class);
            return dataItem;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    static class DataItems {
        List<DataItem> dataItems;

        public List<DataItem> getDataItems() {
            return dataItems;
        }

        public void setDataItems(List<DataItem> dataItems) {
            this.dataItems = dataItems;
        }
    }

    public static Long getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        return Long.parseLong(simpleDateFormat.format(date));
    }

    public static boolean exportToListJSON(List<String> list) {
        String fileName1 = "list.json";
        MenuList menuList = new MenuList();
        menuList.setList(list);
        Gson gson = new Gson();
        String jsonString = gson.toJson(menuList);
        Log.i(TAG, "exportToJSON: " + jsonString);

        FileOutputStream fileOutputStream = null;
        File file = new File(path, fileName1);

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static List<String> importFromListJSON() {
        String fileName = "list.json";
        FileReader reader = null;

        try {
            File file = new File(path, fileName);
            reader = new FileReader(file);
            Gson gson = new Gson();
            MenuList menuList = gson.fromJson(reader, MenuList.class);
            return menuList.getList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean fileExists(String fileName) {

        try {
            File file = new File(path, fileName);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
}
