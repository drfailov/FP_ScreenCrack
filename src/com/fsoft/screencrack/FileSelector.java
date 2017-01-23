package com.fsoft.screencrack;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

/**
 *
 * Created by Dr. Failov on 20.09.2014.
 */
public class FileSelector extends FragmentActivity {
    static ImageGridFragment.OnSelect initAction = null;
    static String[] initPaths = null;
    static String[] initNames = null;

    ViewPager pager;
    Context context = this;
    String[] paths = null;
    String[] names = null;
    ImageGridFragment.OnSelect onSelect = null;
    int backgroundColor = Color.rgb(39,50,56);

    @Override public void onCreate(Bundle bundle)  {
        try{
            super.onCreate(bundle);
            if(initAction == null)
                Logger.log("There is no action received! Did you forgot to fill static fields before calling???");
            onSelect = initAction;
            initAction = null;
            paths = initPaths;
            initPaths = null;
            names = initNames;
            initNames = null;

            Logger.log("sDraw.onCreate", "Инициализация: FileSelector...", false);
            requestWindowFeature(Window.FEATURE_NO_TITLE);  //убрать панель уведомлений
            setContentView(R.layout.view_pager_top_titlestrip);
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setBackgroundColor(backgroundColor);
            PagerAdapter pagerAdapter = new FileSelectorPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);
        }catch (Exception | OutOfMemoryError e) {
            Logger.log("FileSelector.onCreate", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }
    }
    @Override protected void onSaveInstanceState(Bundle outState) {
        if(paths != null && names != null && onSelect != null) {
            initPaths = paths;
            initAction = onSelect;
            initNames = names;
        }
        super.onSaveInstanceState(outState);
    }
    @Override protected void onResume(){
        super.onResume();
    }
    @Override protected void onDestroy(){
        pager = null;
        context = null;
        paths = null;
        names = null;
        onSelect = null;
        super.onDestroy();
        System.gc();
    }

    private class FileSelectorPagerAdapter extends FragmentPagerAdapter {
        ImageGridFragment[] fragments = null;

        public FileSelectorPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ImageGridFragment[paths.length];
        }
        @Override public CharSequence getPageTitle(int position) {
            if(names == null)
                return " error ";
            if(position < 0 || position >= names.length)
                return " error ";
            return names[position];
        }
        @Override public Fragment getItem(int position) {
            if(paths == null)
                return new Fragment();
            if(position < 0 || position >= paths.length)
                return new Fragment();
            if(fragments[position] == null)
                fragments[position] = new ImageGridFragment(context, paths[position], onSelect);
            return fragments[position];
        }
        @Override public int getCount() {
            return paths.length;
        }
    }
    private class DialogLoading{
        ProgressDialog dialog_loading;
        Context context = null;
        String text = "";

        DialogLoading(Context in_context){
            text =  "Загрузка...";
            context = in_context;
            initDialog();
        }

        DialogLoading(Context in_context, String in_text){
            text = in_text;
            context = in_context;
            initDialog();
        }

        void setText(String in_text){
            if(in_text != null)
                text = in_text;
            dialog_loading.setMessage(text);
        }

        void initDialog(){
            dialog_loading=new ProgressDialog(context);
            dialog_loading.setCancelable(false);
            dialog_loading.setMessage(text);
        }

        void show(){
            dialog_loading.show();
        }
        void hide(){
            dialog_loading.dismiss();
        }
    }
}
