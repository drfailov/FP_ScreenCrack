package com.fsoft.screencrack;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 *
 * Created by Dr. Failov on 20.09.2014.
 */
public class ViewAdapter implements ListAdapter {
    private int size = 0;
    private ArrayList<View> cache = null;
    Context context = null;

    ViewAdapter(Context c){
        cache = new ArrayList<View>();
        size = 0;
        context = c;
    }

    public  View  getDelimiter(){
        View delimiter=null;
        try{
            delimiter=new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
        }catch (Exception e){
            Logger.log("Где-то в ViewAdapter.getDelimiter произошла ошибка ",e.toString()+"\n", true);
        }catch (OutOfMemoryError e) {
            Logger.log("Где-то в ViewAdapter.getDelimiter Недостаточно памяти: ", e.toString(), true);
        }
        return delimiter;
    }

    public void addDelimiter(){
        cache.add(getDelimiter());
        size ++;
    }

    public void addView(View v){
        try{
            cache.add(v);
            size ++;
        }catch (Exception e){
            Logger.log("Где-то в ViewAdapter.addView произошла ошибка ",e.toString()+"\n", true);
        }catch (OutOfMemoryError e) {
            Logger.log("Где-то в ViewAdapter.addView Недостаточно памяти: ", e.toString(), true);
        }
    }

    private View getView(int pos){
        if(pos < size)
            return cache.get(pos);
        else if(context != null)
            return new View(context);
        else
            return null;
    }

    public void clear(){
        cache.clear();
        size = 0;
    }

    public void removeView(android.view.View child){
        cache.remove(child);
        size --;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return getView(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return size == 0 ? 1 : size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
