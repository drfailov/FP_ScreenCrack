package com.fsoft.screencrack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * Created by Dr. Failov on 20.09.2014.
 */
public class Data {
    static Activity activity;
    static SharedPreferences prefs;
    static Store store;
    static Tools tools;
    static HashMap<String, Object> cache;
    static HashMap<String, Timer> timers;


    static String backgroundColorInt(){return "int*-16770560*background_color";}
    public static String saveFileprefixString() {
        return "string*ScreenCrack*save_fileprefix";
    }

    static void init(Activity _act){
        activity = _act;
        prefs = activity.getPreferences(Context.MODE_PRIVATE);
        store = new Store(activity);
        tools = new Tools(activity);
        cache = new HashMap<>();
        timers = new HashMap<>();
    }
    static boolean save(Object data, String tag){
        boolean ok = true;
        try{
            if(!tag.contains("*")){
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(tag, (String)data);
                edit.commit();
            }
            else {
                String[] arg = tag.split("\\*");
                String type = arg[0];
                String def = arg[1];
                String name = arg[2];
                //put into cache
                cache.put(name, data);
                //schedule timer
                if(timers.containsKey(name))
                    timers.get(name).cancel();
                Timer timer = new Timer();
                timers.put(name, timer);
                timer.schedule(new MyTimerTask(data, name, def, type), 2000);
            }
        }catch (Exception e){
            ok = false;
        }
        return ok;
    }


    static class MyTimerTask extends TimerTask {
        Object data;
        String type;
        String def;
        String name;

        protected MyTimerTask(Object _data, String _name, String _def, String _type) {
            super();
            data = _data;
            type = _type;
            def = _def;
            name = _name;
        }

        @Override
        public void run() {
            try {
                Logger.log("Write " + name + ": " + data);
                SharedPreferences.Editor edit = prefs.edit();
                if (type.equals("int"))
                    edit.putInt(name, (Integer) data);
                else if (type.equals("float"))
                    edit.putFloat(name, (Float) data);
                else if (type.equals("boolean"))
                    edit.putBoolean(name, (Boolean) data);
                else if (type.equals("string"))
                    edit.putString(name, (String) data);
                edit.commit();
            }
            catch (Exception | OutOfMemoryError e){
                Log.d(Logger.TAG, "Error while (delayed) WRITE: " + e);
                if(tools!= null)
                    tools.getStackTrace(e);
            }
        }
    }
    static Object get(String tag){
        try {
            if (!tag.contains("*"))
                return prefs.getString(tag, "");
            else {
                String[] arg = tag.split("\\*");
                String type = arg[0];
                String def = arg[1];
                String name = arg[2];

                if(cache.containsKey(name)) {
                    Object result = cache.get(name);
                    return result;
                }
                if (type.equals("int")) {
                    int result = prefs.getInt(name, Integer.parseInt(def));
                    cache.put(name, result);
                    return result;
                }
                else if (type.equals("float")) {
                    float result = prefs.getFloat(name, Float.parseFloat(def));
                    cache.put(name, result);
                    return result;
                }
                else if (type.equals("boolean")) {
                    boolean result = prefs.getBoolean(arg[2], Boolean.parseBoolean(arg[1]));
                    cache.put(name, result);
                    return result;
                }
                else if (type.equals("string")) {
                    String result = prefs.getString(arg[2], arg[1]);
                    cache.put(name, result);
                    return result;
                }
                else
                    return "";
            }
        }catch (Exception | OutOfMemoryError e){
            Log.d(Logger.TAG, "Error while GET: " + e);
            if(tools!= null)
                tools.getStackTrace(e);
            return "error";
        }
    }
    static boolean clear(){
        boolean ok = true;
        try {
            SharedPreferences.Editor edit = prefs.edit();
            edit.clear();
            edit.commit();
        }catch (Exception e){
            Logger.log("Clear settings: " + e.toString());
            ok = false;
        }
        return ok;
    }
    static public boolean isTutor(String subject, int limit){
        try {
            String tag = "Tutor_" + subject;
            int current_times = prefs.getInt(tag, 0);
            if(current_times < limit) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt(tag, current_times+1);
                edit.commit();
                return true;
            }
            else
                return false;
        }catch (Exception e){
            Logger.log("Где-то в GlobalData.isTutor("+subject+") произошла ошибка ",e.toString() + "\nStackTrace: \n" + tools.getStackTrace(e), false);
        }
        return false;
    }
    static public void setTutor(String subject, int value){
        //используется для сброса значения показов
        try {
            String tag = "Tutor_" + subject;
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(tag, value);
            edit.commit();
        }catch (Exception e){
            Logger.log("Где-то в GlobalData.isTutor("+subject+") произошла ошибка ",e.toString() + "\nStackTrace: \n" + tools.getStackTrace(e), false);
        }
    }
    static boolean fuckIt(){
        boolean ok = false;
        try{
            activity = null;
            prefs = null;
            store = null;
            tools = null;
        }catch (Exception e){
            ok = false;
        }
        return  ok;
    }
}

class Store{
    //Этот класс создан для хранение общеполезной информации
    Activity activity=null;
    int displayHeight = 0;
    int displayWidth = 0;
    int DPI = 0;

    Store (Activity _act){
        activity = _act;
        //достать размер дисплея
        DisplayMetrics dm = new DisplayMetrics();                             	//получить размер дисплея
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        DPI = dm.densityDpi;
    }
}

class Tools{
    //Этот класс создан для выполнения общеполезных прикладных задач
    Activity activity=null;
    HashMap<Integer, String> resourceCache;

    Tools(Activity _act){
        activity = _act;
        resourceCache = new HashMap<>();
    }
    String getStackTrace(Throwable aThrowable) {
        try{
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            aThrowable.printStackTrace(printWriter);
            return result.toString();
        }catch (Exception e){
            Logger.log("getStackTrace : " + e.toString());
        }
        return "(Error)";
    }
    String readFromFile(int fileResource) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(fileResource)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            } finally {
                br.close();
            }
        }catch (Exception e){
            return  e.toString();
        }
    }
    String readFromFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
    String getResource(int id) {
        try{
            if(resourceCache.containsKey(id)){
                return resourceCache.get(id);
            }
            else{
                String result = activity.getResources().getString(id);
                resourceCache.put(id, result);
                return result;
            }
        }catch (Exception e){
            Logger.log("GlobalData.getResource", "Something goes wrong.\n"+e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }
        return " (Error) ";
    }    //будем использовать эту хрень потому что эту лесенку не хочется строить везде
    int getGridColor(int color){
        return getGridColor(color, 0.1f);
    }
    int getGridColor(int color, float difference){
        try{
            //calculate gridColor
            float [] hsv=new float[3];                        //0 hue      1 saturaton    2 value
            final float threshold = 0.6f;
            Color.colorToHSV(color, hsv);
            //value
            if(hsv[2] > threshold) {
                hsv[2] -= difference;
                if(hsv[2] < 0)
                    hsv[2] = 0;
            }
            else{
                hsv[2] += difference;
                if(hsv[2] > 1)
                    hsv[2] = 1;
            }
            return Color.HSVToColor(hsv);
        }catch (Exception e){
            Logger.log("GlobalData.getGridColor", "Exception: " + e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }catch (OutOfMemoryError e) {
            Logger.log("GlobalData.getGridColor", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }
        return Color.BLACK;
    }
    Bitmap decodeFile(String path, int required_w, int required_h) {
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // Calculate inSampleSize
            final int height = options.outHeight;
            final int width = options.outWidth;
            if (height > required_h || width > required_w) {
                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / required_h);
                final int widthRatio = Math.round((float) width /  required_w);
                // Choose the smallest ratio as inSampleSize value, this will guarantee a final image with both dimensions larger than or equal to the requested height and width.
                options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            options.inJustDecodeBounds = false;
            // Decode bitmap with inSampleSize set
            Bitmap result=null;
            try {
                result=BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e){
                Logger.log("GlobalData.decodeFile", getStackTrace(e), false);
            }
            return result;
        }catch (Exception e){
            Logger.log("Где-то в GlobalData.decodeFile произошла ошибка ",e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }catch (OutOfMemoryError e) {
            Logger.log("Где-то в GlobalData.decodeFile Недостаточно памяти ", e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }
        return null;
    }   //декодирование файла с оптимизацией до определенного размера
    Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        try{
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);
            // "RECREATE" THE NEW BITMAP
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        }catch (Exception e){
            Logger.log("Где-то в GlobalData.getResizedBitmap произошла ошибка ",e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }catch (OutOfMemoryError e) {
            Logger.log("Где-то в GlobalData.getResizedBitmap Недостаточно памяти ", e.toString() + "\nStackTrace: \n" + getStackTrace(e), false);
        }
        return null;
    }
}