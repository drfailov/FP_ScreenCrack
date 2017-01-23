package com.fsoft.screencrack;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * Created by Dr. Failov on 01.05.2014.
 */
public class CrackOverlayService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private WindowManager windowManager;
    BitmapView bitmapView;

    public class LocalBinder extends Binder {
        CrackOverlayService getService() {
            return CrackOverlayService.this;
        }
    }
    @Override public IBinder onBind(Intent intent) {
        // Not used
        return mBinder;
    }
    @Override public void onCreate() {
        super.onCreate();
        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            bitmapView = new BitmapView(this, MainActivity.crackImage);
            MainActivity.crackImage = null;

            WindowManager.LayoutParams params =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.FILL_PARENT,
                            WindowManager.LayoutParams.FILL_PARENT,
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                            PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.FILL;
            params.x = 0;
            params.y = 0;

            windowManager.addView(bitmapView, params);
        }
        catch (Exception | OutOfMemoryError e){
            Logger.log(e.toString());
        }
    }
    @Override public void onDestroy() {
        try {
            super.onDestroy();
            windowManager.removeView(bitmapView);
        }
        catch (Exception | OutOfMemoryError e){
            Logger.log(e.toString());
        }
    }
}