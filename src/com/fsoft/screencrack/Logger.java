package com.fsoft.screencrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 02.05.13
 * Time: 23:14
 */
public class Logger {
    static String TAG="sCracker";
    static Toast correntToast;
    static Context context;

    static String logFileName = null;
    static File logFile = null;
    static FileWriter logWriter = null;
    static boolean fileError = false;

    static void init(Context _context){
        context = _context;
    }
    static void show(String text){
        if(correntToast != null)
            correntToast.cancel();
        correntToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        correntToast.show();
    }
    static void log(String text){
        Log.d(TAG, text);
    }
    static void log(String from, String text, boolean display) {
        try{
            Log.d(TAG, from + " : " + text);
            if(display)
                show(text);
//            if(GlobalData.debug){
//                show(text);
//                writeToFile(text);
//            }
        }catch (Exception e){
            Log.e(TAG, "Error while LOG!!!");
            e.printStackTrace();
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }
    static void messageBox(String text){
        messageBox(text, null);
    }
    static void messageBox(String text, DialogInterface.OnClickListener listener){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Уведомление");
            builder.setMessage(text);
            builder.setPositiveButton("OK", listener);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {                   //предотвратить срабатывание клавиш громкости
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    return keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP;
                }
            });
            builder.setCancelable(false);
            builder.show();
        }catch (Exception e){
            Log.e(TAG, "Error while messageBox!!!");
            e.printStackTrace();
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }
    static void writeToFile(String text){
        if(fileError)
            return;
        if(logFileName == null)
            logFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sDraw.log";
        if(logFile == null)
            logFile = new File(logFileName);
        try{
            if(logWriter == null){
                logWriter = new FileWriter(logFile, true);
                Log.d(TAG, "Открыт файл для записи логов: " + logFileName);
            }
            Calendar calendar = Calendar.getInstance();
            logWriter.write(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "." + calendar.get(Calendar.MILLISECOND) + " - " + text + "\n");
        }catch (Exception e){
            fileError = true;
            Logger.show("Ошибка записи в файл лога");
            Log.d(TAG, "Logger.writeToFile : " + "File = " + logFileName + "Exception: " + e.toString());
        }
    }
    static void closeFile(){
        try{
            if(logWriter != null)    {
                logWriter.close();
                Log.d(TAG, "Закрыт файл для записи логов: " + logFileName);
            }
        }catch (Exception e){
            Logger.log("Logger.closeFile", "File = "+logFileName+"Exception: "+e.toString(), false);
            fileError = true;
        }
    }
    static void fuckIt(){
        closeFile();
        if(correntToast != null)
            correntToast.cancel();
    }
}
