package com.fsoft.screencrack;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by Dr. Failov on 01.05.2014.
 */
public class BitmapView extends View {
    Context context;
    Bitmap bitmap;
    Paint paint;

    public BitmapView(Context context, Bitmap bitmap) {
        super(context);
        try {
            this.context = context;
            this.bitmap = Bitmap.createBitmap(bitmap);
            paint = new Paint();
        }
        catch (Exception | OutOfMemoryError e){
            Logger.log(e.toString());
        }
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        catch (Exception | OutOfMemoryError e){
            Logger.log(e.toString());
        }
    }
}
