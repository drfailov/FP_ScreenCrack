package com.fsoft.screencrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Dr. Failov on 20.09.2014.
 */
public class ImageGridFragment extends Fragment {
    interface OnSelect{
        void select(String path);
    }

    Context context;
    String path;
    String[] files;
    GridView gridView;
    OnSelect onSelect;
    int imageBackgroundColor = Color.rgb(57, 66, 73);
    //status
    boolean pathValid = false; //folder is correct and exist
    boolean pathIsEmpty = true;  //folder contains at least one file
    boolean running = true;   // is program ready to execute commands. Uses for prevent multiple times run OnClick

    public ImageGridFragment(){
        super();
    }
    public ImageGridFragment(Context in_context, String in_path, OnSelect _onS){
        super();
        context = in_context;
        path = in_path;
        onSelect = _onS;
    }
    @Override public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        try{
            if(bundle != null){
                path = bundle.getString("path");
                context = getActivity();
            }
            //scanFiles
            scanFiles(path);
        }catch (Exception e) {
            Logger.log("ImageGridFragment("+path+").onCreate", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }catch (OutOfMemoryError e) {
            Logger.log("ImageGridFragment("+path+").onCreate", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try{
            if(!pathValid)
                return getBigMessage("Папка не создана");
            if(pathIsEmpty)
                return getBigMessage("Папка пуста");
            gridView = new GridView(context);
//            gridView.setRecyclerListener(new AbsListView.RecyclerListener() {
//                @Override
//                public void onMovedToScrapHeap(View view) {
//                    try {
//                        ((ImageBlock) view).freeUp();
//                    } catch (Exception e) {
//                        Logger.log("setRecyclerListener", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
//                    }
//                }
//            });
//            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int state) {
//                    if (state == SCROLL_STATE_IDLE)
//                        loadVisible();
//                }
//
//                @Override
//                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
//                }
//            });
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Logger.log("adapterView = " + adapterView + "; view = " + view + "; i = " + i + "; l = " + l);
//                }
//            });
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            gridView.setNumColumns(display.getWidth()/Data.store.DPI < 5 ? display.getWidth()/Data.store.DPI + 1 : display.getWidth()/Data.store.DPI);

            buildGrid(gridView);
        }catch (Exception e) {
            Logger.log("ImageGridFragment("+path+").onCreateView", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }catch (OutOfMemoryError e) {
            Logger.log("ImageGridFragment("+path+").onCreateView", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }
        return gridView;
    }
    @Override public void onResume(){
        super.onResume();
        //runRefreshWaiter();
        Logger.log("ImageGridFragment.onResume", "Finished", false);
    }
    @Override public void onSaveInstanceState(Bundle outState){
        outState.putString("path", path);
    }
    @Override public void onPause() {
        if(gridView != null)
            for (int i = 0; i < gridView.getChildCount(); i++) {
                try{
                    ((ImageBlock)gridView.getChildAt(i)).freeUp();
                }catch (Exception e){}
            }
        super.onPause();
    }
    @Override public void onDestroy() {
        gridView = null;
        files = null;
        super.onDestroy();
    }

    //applied
    void scanFiles(String in_path){
        try{
            File dir = new File(in_path);
            pathValid = dir.isDirectory();  //check is it directory
            if(pathValid){
                String [] filenames = dir.list(new FilenameFilter() {   //get all the files
                                                   @Override public boolean accept(File file, String s) {
                                                       return s.endsWith(".jpg") || s.endsWith(".png");
                                                   }
                                               }
                );
                pathIsEmpty = filenames.length <= 0;      //check is empty

                for(int i=0;i<filenames.length;i++)               //sort
                    for(int j=1;j<filenames.length;j++)
                        if(filenames[j].compareToIgnoreCase(filenames[j-1])>0) {
                            String tmp = filenames[j-1];
                            filenames[j-1] = filenames[j];
                            filenames[j] = tmp;
                        }

                files = new String[filenames.length];           //copy to other massive with adding folder adress
                for (int i=0; i<filenames.length; i++)
                    files[i] = in_path + File.separator + filenames[i];
            }
        }catch (Exception e) {
            Logger.log("ImageGridFragment("+path+").scanFiles", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }catch (OutOfMemoryError e) {
            Logger.log("ImageGridFragment("+path+").scanFiles", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }
    }
    void buildGrid(GridView in_gridView){
        try{
            if(pathValid && !pathIsEmpty){
                ViewAdapter viewAdapter;
                viewAdapter = new ViewAdapter(context);
                in_gridView.setAdapter(viewAdapter);
                //add number of files
                //На самом деле это костыль. Стоит он тут по той причине, что нажание на первый в списке
                // почему-то не всегда срабатывает. На этот элемент наживать не нужно, поэтому он решает проблему.
                TextBlock textView = new TextBlock(context, "Всего: " + files.length);
                textView.setPadding(Data.store.DPI/30);
                viewAdapter.addView(textView);
                //add files
                for (String file:files){
                    //Logger.log("File: "+file);
                    ImageBlock imageBlock;
                    imageBlock = new ImageBlock(context, file);
                    imageBlock.setTag(file);
                    imageBlock.setOnClickListener(getOnClickListener());
                    imageBlock.setOnLongClickListener(getOnLongListener());
                    imageBlock.setPadding(Data.store.DPI/30);
                    viewAdapter.addView(imageBlock);
                }
            }
        }catch (Exception e) {
            Logger.log("ImageGridFragment("+path+").buildGrid", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }catch (OutOfMemoryError e) {
            Logger.log("ImageGridFragment("+path+").buildGrid", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
        }
    }
    View getBigMessage(String in_text) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setGravity(Gravity.CENTER);
        TextView textView = new TextView(context);
        textView.setText(in_text);
        textView.setPadding(50, 50, 50, 50);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        relativeLayout.addView(textView);
        return relativeLayout;
    }
    View.OnClickListener getOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(running){
                        running = false;
                        //claen memory
                        for(int i = 0; i < gridView.getChildCount(); i++) {
                            try{
                                ImageBlock block = (ImageBlock)gridView.getChildAt(i);
                                block.freeUp();
                            }catch (Exception e){
                                Logger.log("OnClickListener", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                            }
                        }
                        //run
                        String path = (String)view.getTag();
                        onSelect.select(path);
                        ((Activity)context).finish();
                    }
                }catch (Exception e) {
                    Logger.log("ImageGridFragment("+path+").OnClickListener", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }catch (OutOfMemoryError e) {
                    Logger.log("ImageGridFragment("+path+").OnClickListener", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }
            }
        } ;
    }
    View.OnLongClickListener getOnLongListener(){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try{
                    if(running){
                        running = false;
                        //vibrate
                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(50);
                        //showMessage
                        String in_path = (String)view.getTag();
                        Logger.log("ImageGridFragment.getOnLongListener", "Deleting file: "+in_path, false);
                        DeleteConfirmation deleteConfirmation = new DeleteConfirmation(in_path, view);
                        deleteConfirmation.show();
                        running = true;
                    }
                }catch (Exception e) {
                    Logger.log("ImageGridFragment("+path+").OnLongClickListener", "Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }catch (OutOfMemoryError e) {
                    Logger.log("ImageGridFragment("+path+").OnLongClickListener", "OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }
                return true;
            }
        };
    }

    class ImageBlock extends View{
        final  int DOWNSCALE = 2;
        protected int padding = 5;

        protected String file;
        private Bitmap bitmap = null;
        protected Paint backgroundPaint;
        protected Matrix matrix;
        private RectF backgroundRect;

        protected int sqareSize = 0;
        private int imageSise;
        private int imageOffset;

        private boolean loading = false;
        private boolean loaded = false;
        private Loader loader = null;
        private String fileDate = "";
        private Paint fileDatePaint = null;

        ImageBlock(Context in_context, String in_file){
            super(in_context);
            file = in_file;
            backgroundPaint = new Paint();
            backgroundPaint.setColor(imageBackgroundColor);
            //backgroundPaint.setFilterBitmap(true);
        }

        @Override protected void onSizeChanged(int in_width, int in_height, int in_OldWidth, int in_OldHeight){
            super.onSizeChanged(in_width, in_height, in_OldWidth, in_OldHeight);
            setMinimumHeight(in_width);
            sqareSize = in_width;
            imageSise = sqareSize - padding*4;
            imageOffset = padding*2;
            backgroundRect = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);
            matrix = new Matrix();
            matrix.preScale(DOWNSCALE, DOWNSCALE);
            matrix.postTranslate(imageOffset, imageOffset);
            //fileDate
            fileDate = new File(file).getName();
            String filePrefix = (String)Data.get(Data.saveFileprefixString());
            int cropBeginning = filePrefix.length() + 1;
            int cropEnd = 7;//-56.png
            if(fileDate.length() > cropBeginning + cropEnd)
                fileDate = fileDate.substring(cropBeginning, fileDate.length() - cropEnd);
            fileDatePaint = new Paint();
            fileDatePaint.setTextSize(in_width/11);
            fileDatePaint.setColor(Color.WHITE);
        }
        @Override protected void onDraw(Canvas canvas){
            canvas.drawRoundRect(backgroundRect, padding*2, padding*2, backgroundPaint);
            if(bitmap != null){
                canvas.drawBitmap(bitmap, matrix, backgroundPaint);
            }else{
                loadImage();
            }
            canvas.drawText(fileDate, padding*2, padding*2+fileDatePaint.getTextSize(), fileDatePaint);
        }

        void setColor(int c){
            backgroundPaint.setColor(c);
        }
        void freeUp(){
            if(!loading){
                bitmap = null;
                loaded = false;
                System.gc();
            }
        }
        void setPadding(int in_padding){
            padding = in_padding;
            imageSise = sqareSize - padding*4;
            imageOffset = padding*2;
            backgroundRect = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);
            matrix = new Matrix();
            matrix.preScale(DOWNSCALE, DOWNSCALE);
            matrix.postTranslate(imageOffset, imageOffset);
        }
        public void loadImage(){
            if(!loaded && !loading){
                if(new File(file).exists()) {
                    loader = new Loader();
                    loader.execute();
                }
            }
        }

        class Loader extends AsyncTask<Void, Void, Void> {
            @Override public void onPreExecute() {
                loading = true;
            }
            @Override public Void doInBackground(Void... v) {
                try {
                    //get image size
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file, options);
                    int fileHeight = options.outHeight;
                    int fileWidth = options.outWidth;

                    //caclulate needed image size for loading
                    int loadHeight = 0;
                    int loadWidth = 0;
                    if(fileHeight > fileWidth){     //portrait
                        loadWidth = imageSise;
                        loadHeight = loadWidth * fileHeight/fileWidth;
                    } else {                        //landscape
                        loadHeight = imageSise;
                        loadWidth = loadHeight * fileWidth/fileHeight;
                    }
                    loadHeight /= DOWNSCALE;    //уменьшить размер загружаемого изображения (оптимизания)
                    loadWidth /= DOWNSCALE;

                    //garbage collect
                    System.gc();

                    //load image
                    Bitmap tmp = bitmap=Data.tools.decodeFile(file, loadWidth, loadHeight);

                    //crop image
                    if(tmp != null) {
                        int cropSize = Math.min(tmp.getHeight(), tmp.getWidth()) - 1;
                        bitmap = Bitmap.createBitmap(tmp, 0, 0, cropSize, cropSize);
                    }
                    System.gc();
                }catch (Exception e){
                    Logger.log("ImageGridFragment.Loader.doInBackground", "File: " + file + ",\n Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }  catch (OutOfMemoryError e) {
                    Logger.log("ImageGridFragment.Loader.doInBackground", "File: " + file + ",\n OutOfMemoryError: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                }
                return null;
            }
            @Override public void onPostExecute(Void v) {
                loading = false;
                loaded = true;
                invalidate();
            }
        }
    }
    class TextBlock extends ImageBlock{
        int textPadding = 0;
        TextBlock(Context in_context, String in_file){
            super(in_context, in_file);
            textPadding = Data.store.DPI / 10;
        }
        @Override protected void onDraw(Canvas canvas){
            backgroundPaint.setColor(imageBackgroundColor);
            canvas.drawRoundRect(new RectF(padding, padding, getWidth() - padding, getHeight() - padding), padding*2, padding*2, backgroundPaint);
            backgroundPaint.setColor(Color.GRAY);
            canvas.drawText(file, textPadding, sqareSize / 2  + backgroundPaint.getTextSize()*.3f, backgroundPaint);
        }
        @Override void freeUp(){}
        @Override public void loadImage(){}
        @Override protected void onSizeChanged(int in_width, int in_height, int in_OldWidth, int in_OldHeight){
            super.onSizeChanged(in_width, in_height, in_OldWidth, in_OldHeight);
            adjustTextSize();
        }

        void adjustTextSize() {
            backgroundPaint.setTextSize(100);
            while(backgroundPaint.measureText(file) > sqareSize-textPadding*2)
                backgroundPaint.setTextSize(backgroundPaint.getTextSize() - 1);
        }
    }
    class DeleteConfirmation extends AlertDialog {
        String file;
        View view;
        DeleteConfirmation(String in_file, View in_view){
            super(context);
            file = in_file;
            view = in_view;
        }

        @Override
        public void show() {
            setButton("Подтвердить", getAccceptListener());
            setButton2("Отменить", getNullListener());
            setTitle("Подтвердите удаление");
            setMessage("Вы действительно хотите удалить файл \n\"" + file + "\"?");
            super.show();
        }

        DialogInterface.OnClickListener getAccceptListener(){
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try{
                        //TEST PATHS

                        String transparentFileName = new File(file).getName();
                        String transparentFilePath = new File(file).getParent();
                        String exportedFilePath = new File(transparentFilePath).getParent();
                        String transparentFileString = transparentFilePath + File.separator + transparentFileName;
                        String exportedFileString = exportedFilePath + File.separator + transparentFileName;
                        File transparentFile = new File(transparentFileString);
                        File exportedFile = new File(exportedFileString);

                        Logger.log("transparentFileName = " + transparentFileName);
                        Logger.log("transparentFilePath = " + transparentFilePath);
                        Logger.log("exportedFilePath = " + exportedFilePath);
                        Logger.log("transparentFile = " + transparentFile);
                        Logger.log("exportedFile = " + exportedFile);

                        //try to delete transparent file
                        if(transparentFile.exists()){
                            if(transparentFile.delete())
                                Logger.log("Success deleted: " + transparentFile);
                            else
                                Logger.log("Failed to delete: " + transparentFile);
                        }
                        else
                            Logger.log("File doesn't exist: " + transparentFile);

                        //try to delete exported file
                        if(exportedFile.exists()){
                            if(exportedFile.delete())
                                Logger.log("Success deleted: " + exportedFile);
                            else
                                Logger.log("Failed to delete: " + exportedFile);
                        }
                        else
                            Logger.log("File doesn't exist: " + exportedFile);


                        //deactivate view of deleted file
                        ImageBlock imageBlock = (ImageBlock)view;//views.get(file);
                        imageBlock.freeUp();
                        imageBlock.setColor(Color.BLACK);
                        imageBlock.setOnLongClickListener(null);
                        imageBlock.setOnClickListener(null);
                        imageBlock.invalidate();
                    }catch (Exception e){
                        Logger.log("ImageGridFragment.DeleteConfirmation.DialogInterface.OnClickListener", "File: " + file + ",\n Exception: " + e.toString() + "\nStackTrace: \n" + (Data.tools == null ? e.toString() : Data.tools.getStackTrace(e)), false);
                    }
                }
            };
        }

        DialogInterface.OnClickListener getNullListener(){
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            };
        }
    }
}

