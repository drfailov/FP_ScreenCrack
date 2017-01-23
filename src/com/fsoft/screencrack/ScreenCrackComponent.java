package com.fsoft.screencrack;

import android.graphics.Bitmap;

/**
 * Родительский класс для таких подклассов как генератор трещин, деформаций, пятен, и т.д.
 * В рамках данного класса будут описаны базовые механизмы связанные с интерфейсами
 * Created by Dr. Failov on 21.09.2014.
 */
public class ScreenCrackComponent {
    public interface ShowTextInterface{
        void showText(String text);
    }
    public interface ShowImageInterface{
        void showImage(Bitmap image);
    }

    private ShowImageInterface showImageInterface = null;
    private ShowTextInterface showTextInterface = null;

    protected void showText(String text){
        if(showTextInterface != null)
            showTextInterface.showText(text);
    }
    protected  void showImage(Bitmap bitmap){
        if(showImageInterface != null)
            showImageInterface.showImage(bitmap);
    }

    public void setShowImageInterface(ShowImageInterface showImageInterface) {
        this.showImageInterface = showImageInterface;
    }

    public void setShowTextInterface(ShowTextInterface showTextInterface) {
        this.showTextInterface = showTextInterface;
    }
}
