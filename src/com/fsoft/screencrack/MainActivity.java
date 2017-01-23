package com.fsoft.screencrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 * Created by Dr. Failov on 01.05.2014.
 */
public class MainActivity extends Activity {
    public static Bitmap crackImage = null;

    MainActivity context = this;
    String filesDir;
    //PREVIEW
    LinearLayout llPREVIEW;
    ImageView previewImage;
    TextView previewText;
    Button generateButton;
    Button openButton;
    Button saveButton;
    //FULLSCREEN
    LinearLayout llFULLSCREEN;
    Button startButton;
    Button stopButton;
    //CRACKTYPE
    RadioButton crackTypeFall;//Деформации как правило из угла
    RadioButton crackTypePoint;//Деформации из одной точки
    RadioButton crackTypeTwain;//Деформации от края до края
    //DISPLAYTYPE
    RadioButton displayTypeIndestructible;//деформаций изображение не будет
    RadioButton displayTypeTFT;//деформированные части изображения белые, возможны полосы
    RadioButton displayTypeTFT1;//деформированные части изображения полосы
    RadioButton displayTypeIPS;//Деформированные части изображения черные, возможны полосы
    RadioButton displayTypeAMOLED;//Деформированные части изображения - тупо черный.
    //CONTROLLERPOSITION
    RadioButton chipPositionTop;//контроллер дисплея сверху
    RadioButton chipPositionBottom;//
    //CRACKPOSITION
    LinearLayout llCRACKPOSITION;
    RadioButton crackPositionTop;//эпицентр трещины сверху
    RadioButton crackPositionBottom;//Эпицентр трещины внизу
    //OPTIONAL
    LinearLayout llOPTIONAL;
    CheckBox showOptionalCheckbox;
    //CRACK LONG
    SeekBar crackLength;//длина элементарного отрезка
    TextView crackLengthText;
    //CRACK STRONGNESS
    SeekBar crackStrongness;//толщина трещие
    TextView crackStrongnessText;
    //CRACK TORTUOSITY
    SeekBar crackTortuosity;//степень изогнутости
    TextView crackTortuosityText;
    //CRACK COUNT
    SeekBar crackCount;//каоличество создаваемых
    TextView crackCountText;
    //SPOT SIZE
    SeekBar spotSize;  //размер пятен
    TextView spotSizeText;
    //CONNECTIONS DURABILITY
    SeekBar connectionsDurability;  //Прочность соединений
    TextView connectionsDurabilityText;
    //CHECKBOXES
    CheckBox allowIntersectionsCheckbox;
    CheckBox crackNeighbourhoodCheckbox;
    CheckBox drawSpotsCheckbox;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        Logger.init(this);
        Data.init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_window);
        filesDir = Environment.getExternalStorageDirectory() + "/Pictures/ScreenCrack/";
        if (!loadIUElements()){
            Logger.messageBox("Не удалось загрузить все компоненты программы. Программа не может продолжить работу.");
            return;
        }
        if (!configureUIElements()){
            Logger.messageBox("Не удалось настроить все элементы интерфейса. Возможно, что-то не будет работать.");
        }
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_MENU){
            AlertDialog.Builder dialog= new AlertDialog.Builder(this);
            ScrollView scroll=new ScrollView(this);
            TextView text=new TextView(this);
            text.setText(readFromFile(R.raw.changelog));
            text.setScrollContainer(true);
            scroll.addView(text);
            dialog.setView(scroll);
            dialog.setTitle("История изменений");
            dialog.show();
        }
        return super.dispatchKeyEvent(event);
    }
    String readFromFile(int fileResource) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(fileResource)));
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
    boolean loadIUElements(){
        try{
            //PREVIEW
            llPREVIEW = (LinearLayout)findViewById(R.id.llPREVIEW);
            previewImage = (ImageView)findViewById(R.id.previewView);
            previewText = (TextView)findViewById(R.id.textPreviewNotReady);
            generateButton = (Button)findViewById(R.id.buttonGenerate);
            openButton = (Button)findViewById(R.id.buttonOpen);
            saveButton = (Button)findViewById(R.id.buttonSave);
            //FULLSCREEN
            llFULLSCREEN = (LinearLayout)findViewById(R.id.llFULLSCREEN);
            startButton = (Button)findViewById(R.id.startServiceButton);
            stopButton = (Button)findViewById(R.id.stopServiceButton);
            //CRACKTYPE
            crackTypePoint = (RadioButton)findViewById(R.id.radioButtonCrackPoint);
            crackTypeFall = (RadioButton)findViewById(R.id.radioButtonCrackFall);
            crackTypeTwain = (RadioButton)findViewById(R.id.radioButtonCrackTwice);
            //DISPLAYTYPE
            displayTypeIndestructible = (RadioButton)findViewById(R.id.radioButtonIndestructible);
            displayTypeIPS = (RadioButton)findViewById(R.id.radioButtonIPS);
            displayTypeTFT1 = (RadioButton)findViewById(R.id.radioButtonTFT1);
            displayTypeTFT = (RadioButton)findViewById(R.id.radioButtonTFT);
            displayTypeAMOLED = (RadioButton)findViewById(R.id.radioButtonOLED);
            //CONTROLLERPOSITION
            chipPositionBottom = (RadioButton)findViewById(R.id.radioButtonChipBottom);
            chipPositionTop = (RadioButton)findViewById(R.id.radioButtonChipTop);
            //CRACKPOSITION
            llCRACKPOSITION = (LinearLayout)findViewById(R.id.llCRACKPOSITION);
            crackPositionBottom = (RadioButton)findViewById(R.id.radioButtonCrackBottom);
            crackPositionTop = (RadioButton)findViewById(R.id.radioButtonCrackTop);
            //OPTIONAL
            llOPTIONAL = (LinearLayout)findViewById(R.id.llOPTIONAL);
            showOptionalCheckbox = (CheckBox)findViewById(R.id.checkBoxOptional);
            //CRACK LONG
            crackLength = (SeekBar)findViewById(R.id.seekBarCrackLenght);
            crackLengthText = (TextView)findViewById(R.id.textViewCrackLength);
            //CRACK STRONGNESS
            crackStrongness = (SeekBar)findViewById(R.id.seekBarCrackWidth);
            crackStrongnessText = (TextView)findViewById(R.id.textViewCrackWidth);
            //CRACK TORTUOSITY
            crackTortuosity = (SeekBar)findViewById(R.id.seekBarCrackTortuosity);
            crackTortuosityText = (TextView)findViewById(R.id.textViewCrackTortuosity);
            //CRACK COUNT
            crackCount = (SeekBar)findViewById(R.id.seekBarCrackCount);
            crackCountText = (TextView)findViewById(R.id.textViewCrackCount);
            //SPOT SIZE
            spotSize = (SeekBar)findViewById(R.id.seekBarSpotSize);
            spotSizeText = (TextView)findViewById(R.id.textViewSpotSize);
            //CONNECTIONS DURABILITY
            connectionsDurability = (SeekBar)findViewById(R.id.seekBarConnectionDurability);
            connectionsDurabilityText = (TextView)findViewById(R.id.textViewConnectionDurability);
            //CHECKBOXES
            crackNeighbourhoodCheckbox = (CheckBox)findViewById(R.id.checkBoxCrackNeighbours);
            allowIntersectionsCheckbox = (CheckBox)findViewById(R.id.checkBoxAllowIntersections);
            drawSpotsCheckbox = (CheckBox)findViewById(R.id.checkBoxDrawSpots);
            return true;
        }catch (Exception e){
            Logger.log("Error while getting elements: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }
    boolean configureUIElements(){
        try {
            //------------------------configure optionals
            showOptionalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        llOPTIONAL.setVisibility(View.VISIBLE);
                    else
                        llOPTIONAL.setVisibility(View.GONE);
                }
            });
            showOptionalCheckbox.setChecked(false);
            llOPTIONAL.setVisibility(View.GONE);
            //Configure SeekBar's
            crackLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    crackLengthText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            crackStrongness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    crackStrongnessText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            crackTortuosity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    crackTortuosityText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            crackCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    crackCountText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            spotSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    spotSizeText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            connectionsDurability.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    connectionsDurabilityText.setText(" " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //ConfigureButtons
            generateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startGenerator();
                }
            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveFile();
                }
            });
            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile();
                }
            });
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startService();
                }
            });
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopService();
                }
            });

            crackTypePoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectCrackPoint();
                }
            });
            crackTypeFall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectCrackFall();
                }
            });
            crackTypeFall.performClick();
            crackTypeTwain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectCrackTwice();
                }
            });

            displayTypeIPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectIPS();
                }
            });
            displayTypeTFT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectTFT();
                }
            });
            displayTypeTFT1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectTFT1();
                }
            });
            displayTypeAMOLED.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOLED();
                }
            });
            displayTypeIndestructible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectTouchscreen();
                }
            });

            return true;
        }catch (Exception e){
            Logger.log("Error while configuring: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }
    Bitmap toVisualize = null;
    void visualizeBitmap(Bitmap in){
        toVisualize = in;
        if(toVisualize != null) {
            context.previewImage.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    context.previewImage.setImageBitmap(toVisualize);
                    context.previewImage.postInvalidate();
                }
            });
        }
    }

    void stopService(){
        try {
            Logger.log("Остановка сервиса...");
            stopService(new Intent(context, CrackOverlayService.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void startService(){
        if(MainActivity.crackImage == null){
            Logger.messageBox("Перед запуском полноэкранного режима сгенерируйте узор.");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if(MainActivity.crackImage == null){
                        Logger.log("Ссылка пуста. Ничего не делать.");
                        return;
                    }
                    stopService();
                    Logger.log("Запуск сервиса...");
                    context.startService(new Intent(context, CrackOverlayService.class));
                    context.previewText.setVisibility(View.VISIBLE);
                    context.previewImage.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    context.llPREVIEW.postInvalidateDelayed(100);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Нет", null);
        builder.setMessage("Внимание! Сейчас будет запущена эмуляция разбирого экрана и " +
                "Вы не будете видеть часть изображения на экране! Не пугайтесь.   " +
                "Запомните расположение программы, чтобы в случае необходимости ВСЛЕПУЮ открыть её и нажать кнопку \"Остановить\". " +
                "В крайнем случае перезагрузите телефон - это закроет программу. \n" +
                "Вы готовы?");
        builder.show();
    }
    void startGenerator(){
        try {
            stopService();
            Logger.log("Запуск генератора...");
            GenerateTask generateTask = new GenerateTask(context);
            generateTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void saveFile(){
        if(crackImage == null){
            Logger.messageBox("Сначала сгенерируйте узор.");
            return;
        }
        if(!checkFolder(new File(filesDir))){
            Logger.messageBox("Не удалось создать папку: " + filesDir);
            return;
        }

        for (int i = 0; true; i++) {
            String next = filesDir + i + ".png";
            File cur = new File(next);
            if(!cur.exists()){
                try {
                    FileOutputStream fos = new FileOutputStream(cur);
                    crackImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Logger.messageBox("Файл сохранен здесь: " + cur);
                }
                catch (Exception e){
                    Logger.messageBox("Файл сохранить не удалось: " + cur + "     " + e.toString());
                }
                break;
            }
        }
    }
    boolean checkFolder(File path){
        //если надо - создать папку
        boolean exist = path.isDirectory();
        if(!exist) {
            Logger.log("Draw.checkFolder", "Создание папки " + path + "...", false);
            exist = path.mkdirs();
            //если папку созать не удалось
            if(!exist){
                Logger.log("Draw.checkFolder", "Создать папку не удалось: \n " + path, false);
            }
        }
        return exist;
    }
    void openFile(){
        FileSelector.initAction = new ImageGridFragment.OnSelect() {
            @Override
            public void select(String path) {
                crackImage = BitmapFactory.decodeFile(path);
                visualizeBitmap(crackImage);
            }
        };
        FileSelector.initNames = new String[]{"Сохраненные файлы"};
        FileSelector.initPaths = new String[]{filesDir};
        startActivity(new Intent(context, FileSelector.class));
    }
    void selectCrackPoint(){
        crackLength.setProgress(5);
        crackStrongness.setProgress(15);
        crackCount.setProgress(99);
        crackTortuosity.setProgress(15);
        allowIntersectionsCheckbox.setChecked(false);
        crackNeighbourhoodCheckbox.setChecked(true);
        llCRACKPOSITION.setVisibility(View.VISIBLE);
    }
    void selectCrackFall(){
        crackLength.setProgress(9);
        crackStrongness.setProgress(12);
        crackCount.setProgress(14);
        crackTortuosity.setProgress(20);
        allowIntersectionsCheckbox.setChecked(false);
        crackNeighbourhoodCheckbox.setChecked(true);
        llCRACKPOSITION.setVisibility(View.VISIBLE);
    }
    void selectCrackTwice(){
        crackLength.setProgress(10);
        crackStrongness.setProgress(88);
        crackCount.setProgress(100);
        crackTortuosity.setProgress(6);
        allowIntersectionsCheckbox.setChecked(false);
        crackNeighbourhoodCheckbox.setChecked(true);
        llCRACKPOSITION.setVisibility(View.GONE);
    }
    void selectIPS(){
        spotSize.setProgress(Data.store.DPI/6);
        drawSpotsCheckbox.setChecked(true);
    }
    void selectTFT(){
        spotSize.setProgress(Data.store.DPI/4);
        drawSpotsCheckbox.setChecked(true);
    }
    void selectTFT1(){
        spotSize.setProgress(Data.store.DPI/4);
        drawSpotsCheckbox.setChecked(true);
    }
    void selectOLED(){
        drawSpotsCheckbox.setChecked(false);
    }
    void selectTouchscreen(){
        drawSpotsCheckbox.setChecked(false);
    }
}

class GenerateTask extends AsyncTask<String, String, String> {
    MainActivity context;
    Random random;
    Bitmap bitmapCrackMap;
    Bitmap bitmapCrackImage;
    Bitmap bitmap;
    Paint paint;
    Canvas canvas;
    int width=0, height=0, DPI = 0;
    int crackColor = Color.GRAY;
    String dialogText = "Генерация узора";
    String textToShow = "";
    boolean cont = true;
    int crackStrongness;
    int crackLength;
    int crackTortuosity;
    int crackCount;
    int spotSize;
    int connectionDurability;
    boolean allowIntersection;
    boolean crackNeighbours;
    boolean drawSpots;

    public GenerateTask(MainActivity c) {
        super();
        context = c;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = windowManager.getDefaultDisplay().getWidth();
        height = windowManager.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DPI = displayMetrics.densityDpi;

        random = new Random();
        random.setSeed(System.currentTimeMillis());
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCrackImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCrackMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        paint = new Paint();
        paint.setAlpha(255);
        canvas = new Canvas(bitmap);
    }
    @Override protected void onPreExecute() {
        super.onPreExecute();
        makeGenerating();
        crackStrongness = context.crackStrongness.getProgress();
        crackLength = context.crackLength.getProgress();
        crackTortuosity = context.crackTortuosity.getProgress();
        crackCount = context.crackCount.getProgress();
        spotSize = context.spotSize.getProgress();
        connectionDurability = context.connectionsDurability.getProgress();
        allowIntersection = context.allowIntersectionsCheckbox.isChecked();
        crackNeighbours = context.crackNeighbourhoodCheckbox.isChecked();
        drawSpots = context.drawSpotsCheckbox.isChecked();
    }
    @Override protected String doInBackground(String... params) {
        try{
            //-----------------------------------------ГЕНЕРАЦИЯ ТРЕЩИН
            dialogText = "Генерация трещин ";
            if(context.crackTypePoint.isChecked()){
                int cx = getRandom(width);
                int cy = getRandom(height/2);
                if(context.crackPositionBottom.isChecked())
                    cy += height/2;

                for(int i=0; i<crackCount && cont; i++) {
                    float degres = getRandom(360f);
                    setText(dialogText + "(" + i + "/" + crackCount + ")");
                    context.visualizeBitmap(bitmapCrackImage);
                    float crackFund = DPI/20;
                    makeCrack(cx + getRandom(crackFund), cy + getRandom(crackFund), degres, crackStrongness + 40, crackTortuosity, crackLength, !allowIntersection, crackNeighbours);
                }
            }
            else if(context.crackTypeFall.isChecked()){
                float degressFund = 150;
                boolean left = getRandom(2) == 0;
                int cx = left ? 1 : width-1;
                int cy = getRandom(height/2);
                if(context.crackPositionBottom.isChecked())
                    cy += height/2;
                int heightFund = DPI/2;
                float degres = left ? 90-degressFund/2 : -90-(degressFund/2);

                for(int i=0; i<crackCount && cont; i++) {
                    int yc = cy + getRandom(heightFund*2)-heightFund;
                    setText(dialogText + "(" + i + "/" + crackCount + ")");
                    context.visualizeBitmap(bitmapCrackImage);
                    makeCrack(cx, yc, (float)Math.toRadians( degres+getRandom(degressFund)), crackStrongness + 40, crackTortuosity, crackLength, !allowIntersection, crackNeighbours);
                }
            }
            else if(context.crackTypeTwain.isChecked()){
                int cx = 1;
                float degressFund = 50;
                float degres = 90-degressFund/2;
                int cy = (height/2);
                int heightFund = DPI/8;

                for(int i=0; i<crackCount/2 && cont; i++) {
                    int yc = cy + getRandom(heightFund*2)-heightFund;
                    setText(dialogText + "(" + i + "/" + crackCount + ")");
                    context.visualizeBitmap(bitmapCrackImage);
                    makeCrack(cx, yc, (float)Math.toRadians(degres+getRandom(degressFund)), crackStrongness + 40, crackTortuosity, crackLength, !allowIntersection, crackNeighbours);
                }

                cx = width-1;
                degres = -90-(degressFund/2);

                for(int i=0; i<crackCount/2; i++) {
                    int yc = cy + getRandom(heightFund*2)-heightFund;
                    setText(dialogText + "(" + (i + crackCount / 2) + "/" + crackCount + ")");
                    context.visualizeBitmap(bitmapCrackImage);
                    makeCrack(cx, yc, (float)Math.toRadians(degres+getRandom(degressFund)), crackStrongness + 40, crackTortuosity, crackLength, !allowIntersection, crackNeighbours);
                }
            }

            //-----------------------------------------ГЕНЕРАЦИЯ ПОЛОС
            dialogText = "Деформация изображения ";
            if(!context.displayTypeIndestructible.isChecked() && cont){
                int type = 0;
                if(context.displayTypeAMOLED.isChecked())
                    type = TYPE_AMOLED;
                else if(context.displayTypeIPS.isChecked())
                    type = TYPE_IPS;
                else if(context.displayTypeTFT.isChecked())
                    type = TYPE_TFT;
                else if(context.displayTypeTFT1.isChecked())
                    type = TYPE_TFT1;

                int direction = 0;
                if(context.chipPositionBottom.isChecked())
                    direction = DIRECTION_UP;
                else if(context.chipPositionTop.isChecked())
                    direction = DIRECTION_DOWN;

                makeDeformations(direction, type);
            }
            //-----------------------------------------ГЕНЕРАЦИЯ ПЯТЕН
            dialogText = "Генерация пятен... ";
            if(drawSpots && cont)
                makeSpots(spotSize);

            //-----------------------------------------ПЕРЕНОС ХОЛСТА
            if(cont) {
                dialogText = "Перенос холста трещин";
                setText(dialogText + "...");
                paint.setAlpha(128);
                canvas.drawBitmap(bitmapCrackImage, 0, 0, paint);

                MainActivity.crackImage = bitmap;
            }
            return "ok";
        } catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }
    @Override protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Logger.log("Ответ генератора: " + s);
        //dialogLoading.cancel();
        makeReady();
        if(s.equals("ok")) {
            if(MainActivity.crackImage != null) {
                context.llFULLSCREEN.setVisibility(View.VISIBLE);
                context.previewImage.setImageBitmap(MainActivity.crackImage);
            }
        }
        else
            Logger.show("Ответ генератора: " + s);
        Logger.log("StartServiceTask завершается...");
    }
    void cancel(){
        makeCancelling();
        cont = false;
    }
    void makeGenerating(){
        context.previewText.setVisibility(View.VISIBLE);
        setText("Запуск...");
        context.generateButton.setText("Отмена");
        context.generateButton.setEnabled(true);
        context.generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        context.startButton.setEnabled(false);
        context.stopButton.setEnabled(false);
    }
    void makeCancelling(){
        context.previewText.setVisibility(View.VISIBLE);
        setText("Отменяется...");
        context.generateButton.setText("Отменяется...");
        context.generateButton.setEnabled(false);
        context.startButton.setEnabled(false);
        context.stopButton.setEnabled(false);

    }
    void makeReady(){
        context.previewText.setVisibility(View.VISIBLE);
        setText("  ");
        context.generateButton.setText("Генерировать");
        context.generateButton.setEnabled(true);
        context.generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startGenerator();
            }
        });
        context.startButton.setEnabled(true);
        context.stopButton.setEnabled(true);
    }
    void setText(String text){
        textToShow = text;
        context.previewText.getHandler().post(new Runnable() {
            @Override
            public void run() {
                context.previewText.setText(textToShow);
            }
        });
    }

    /*
    * cx 0...width
    * cy 0...height
    * degrees 0...360
    * stoniness 0...255
    * length 0...5
    * */
    void makeCrack(float cx, float cy, float degrees, float stoniness, float tortuosity, float length, boolean avoidIntersections, boolean crackNeighbours){
        Paint maskPaint = new Paint();
        maskPaint.setStrokeWidth(2f);
        Paint imagePaint = new Paint();
        imagePaint.setColor(crackColor);
        imagePaint.setAntiAlias(true);
        Canvas imageCanvas = new Canvas(bitmapCrackImage);
        Canvas maskCanvas = new Canvas(bitmapCrackMap);
        Bitmap tmpCrackBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas tmpMapCanvas = new Canvas(tmpCrackBitmap);
        float startX = cx;
        float startY = cy;
        float lineSize = 1+length;
        float degressMaxStep = 0.01f * tortuosity;
        boolean cont = true;
        int crackNeighbourMinDistance = 20;
        int crackNeighbourhoodCounter = crackNeighbourMinDistance;

        while(stoniness > 0 && cx < width && cx > 0 && cy > 0 && cy < height && cont){
            float endX = cx + (lineSize * (float)Math.sin(degrees));
            float endY = cy + (lineSize * (float)Math.cos(degrees));

            //проверка на пересечения
            if(avoidIntersections) {
                float distance = (float) Math.sqrt(Math.pow(cx - startX, 2) + Math.pow(cy - startY, 2));
                if (distance > DPI / 6) {
                    float steps = 10 + length;
                    float dx = endX - cx;
                    float dy = endY - cy;
                    for (float fx = cx; Math.abs(fx - cx) < Math.abs(dx) && cont; fx += dx / steps) {
                        for (float fy = cy; Math.abs(fy - cy) < Math.abs(dy) && cont; fy += dy / steps) {
                            if (fx >= width || fx < 0 || fy < 0 || fy >= height) {//вышли за рамки дисплея
                                endX = fx;
                                endY = fy;
                                cont = false;
                                break;
                            }
                            int color = bitmapCrackMap.getPixel((int) fx, (int) fy);
                            int transparency = Color.alpha(color);
                            if (transparency > 200) {//нашли пересекающую трещину
                                endX = fx;
                                endY = fy;
                                cont = false;
                            }
                            if (dy / steps < 0.01f)//если шаг СЛИШКОМ мал
                                break;
                        }
                        if (dx / steps < 0.01f)
                            break;
                    }
                }
            }

            //формирование трещинок к близлежащим деформациям
            if(crackNeighbours && crackNeighbourhoodCounter <=0){
                float maxDistance = DPI/15;
                boolean found = false;
                for(float neighbourRadius = 2; neighbourRadius < maxDistance && !found; neighbourRadius++){
                    for(float neighbourDegrees = 0; neighbourDegrees < 360 && !found; neighbourDegrees++){
                        float neighbourX = cx + (neighbourRadius * (float)Math.sin(neighbourDegrees));
                        float neighbourY = cy + (neighbourRadius * (float)Math.cos(neighbourDegrees));
                        if(neighbourX > 0 && neighbourX < width && neighbourY > 0 && neighbourY < height) {
                            int color = bitmapCrackMap.getPixel((int) neighbourX, (int) neighbourY);
                            int alpha = Color.alpha(color);
                            if (alpha > 200) {
                                found = true;
                                crackNeighbourhoodCounter = crackNeighbourMinDistance;
                                //draw onto image
                                imagePaint.setStrokeWidth((stoniness / 255) * 2);
                                imageCanvas.drawLine(cx, cy, neighbourX, neighbourY, imagePaint);
                                //draw onto map
                                maskPaint.setColor(Color.rgb((int) stoniness, (int) stoniness, (int) stoniness));
                                tmpMapCanvas.drawLine(cx, cy, neighbourX, neighbourY, maskPaint);
                            }
                        }
                    }
                }
            }
            else if(crackNeighbourhoodCounter > 0)
                crackNeighbourhoodCounter--;

            //draw onto image
            imagePaint.setStrokeWidth((stoniness / 255) * 2);
            imageCanvas.drawLine(cx, cy, endX, endY, imagePaint);

            //draw onto map
            maskPaint.setColor(Color.rgb((int)stoniness, (int)stoniness, (int)stoniness));
            tmpMapCanvas.drawLine(cx, cy, endX, endY, maskPaint);

            cx = endX;
            cy = endY;
            degrees += getRandom(degressMaxStep) * (getRandom(3)-1);
            stoniness -= getRandom(1.0f);
        }
        maskCanvas.drawBitmap(tmpCrackBitmap, 0, 0, maskPaint);
    }

    /*
    * direction DIRECTION_UP   DIRECTION_DOWN
    * type TYPE_AMOLED   TYPE_IPS    TYPE_TFT
    * */
    final int DIRECTION_UP = 0;
    final int DIRECTION_DOWN = 1;
    final int TYPE_AMOLED = 0;
    final int TYPE_IPS = 1;
    final int TYPE_TFT = 2;
    final int TYPE_TFT1 = 3;
    void makeDeformations(int direction, int type){
        int xStart = 0;
        int yStart = 0;
        int xStop = 0;
        int yStop = 0;
        int dx = 0;
        int dy = 0;
        Paint paint = new Paint();
        if(direction == DIRECTION_DOWN){
            xStart = 0;
            yStart = 0;
            xStop = width;
            yStop = height;
            dx = 1;
            dy = 1;
        }
        else if(direction == DIRECTION_UP){
            xStart = 0;
            yStart = height-1;
            xStop = width;
            yStop = -1;
            dx = 1;
            dy = -1;
        }

        int cnt = 0;
        for(int x = xStart; x!=xStop && cont; x+=dx){
            if(cnt == 15) {
                setText(dialogText + " (" + x + "/" + xStop + ")");
                context.visualizeBitmap(bitmap);
                cnt = 0;
            }
            cnt++;
            int color = Color.TRANSPARENT;
            boolean skip = false;
            for(int y = yStart; y!=yStop && cont; y+=dy){
                int cur = bitmapCrackMap.getPixel(x,y);
                int alpha = Color.alpha(cur);
                if(alpha > 200 && !skip){
                    //do
                    switch (type){
                        case TYPE_TFT:
                            color = deformTFT(color, cur);
                            break;
                        case TYPE_AMOLED:
                            color = deformAMOLED(color, cur);
                            break;
                        case TYPE_IPS:
                            color = deformIPS(color, cur);
                            break;
                        case TYPE_TFT1:
                            color = deformTFT1(color, cur);
                            break;
                    }
                    skip = true;
                }
                else
                    skip = false;
                paint.setColor(color);
                canvas.drawPoint(x,y, paint);
            }
        }
    }

    int deformTFT(int imageColor, int crackColor){
        int crackIntensity = Color.red(crackColor);//0...255
        int alpha = Color.alpha(imageColor);
        int colorRed = Math.min(alpha, Color.red(imageColor));
        int colorGreen = Math.min(alpha, Color.green(imageColor));
        int colorBlue = Math.min(alpha, Color.blue(imageColor));
        int threshold = connectionDurability;
        int random = getRandom(128) + crackIntensity;
        if(random > threshold){//если обрыв есть точно
            int selectedColor = getRandom(3);
            switch (selectedColor){
                case 0:
                    colorRed = 255;
                    break;
                case 1:
                    colorGreen = 255;
                    break;
                case 2:
                    colorBlue = 255;
                    break;
            }
        }
        return Color.argb(Math.max(Math.max(colorRed, colorGreen), colorBlue), colorRed, colorGreen, colorBlue);
    }
    int deformTFT1(int imageColor, int crackColor) {
        int crackIntensity = Color.red(crackColor);//0...255
        int random = getRandom(50) + crackIntensity;
        int threshold = connectionDurability;
        if (random > threshold) {//если обрыв есть точно
            return Color.rgb(getRandom(255), getRandom(255), getRandom(255));
        }
        return imageColor;
    }
    int deformIPS(int imageColor, int crackColor) {
        int crackIntensity = Color.red(crackColor);//0...255
        int random = getRandom(40) + crackIntensity;
        int threshold = connectionDurability;
        if (random > threshold) {//если обрыв есть точно
            int rand  = getRandom(6);
            if(Color.red(imageColor) == Color.green(imageColor) && Color.green(imageColor) == Color.blue(imageColor) && Color.alpha(imageColor) > 200)
                rand = 1;
            if(rand == 0){//цветной
                int colorRed = 0;
                int colorGreen = 0;
                int colorBlue = 0;
                int selectedColor = getRandom(7);
                switch (selectedColor) {
                    case 0:
                        colorRed = 255;
                        break;
                    case 1:
                        colorGreen = 255;
                        break;
                    case 2:
                        colorBlue = 255;
                        break;
                    case 3:
                        colorRed = 255;
                        colorGreen = 255;
                        break;
                    case 4:
                        colorBlue = 255;
                        colorRed = 255;
                        break;
                    case 5:
                        colorBlue = 255;
                        colorGreen = 255;
                        break;
                    case 6:
                        colorBlue = 255;
                        colorRed = 255;
                        colorGreen = 255;
                        break;
                }
                return Color.rgb(colorRed, colorGreen, colorBlue);
            }
            else {//черній
                int gr = getRandom(10);
                return Color.rgb(gr,gr,gr);
            }
        }
        return imageColor;
    }
    int deformAMOLED(int imageColor, int crackColor){
        int crackIntensity = Color.red(crackColor);//0...255
        int threshold = connectionDurability;
        int random = getRandom(128) + crackIntensity;
        if(random > threshold){//если обрыв есть точно
            return Color.BLACK;
        }
        else
            return imageColor;
    }

    void makeSpots(int spotSize){
        Paint spotsPaint  = new Paint();
        float max = 0;
        float maxRadius = spotSize;
        int power = 2;

        setText("Поиск максимума...");
        for(int x = 0; x< width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmapCrackMap.getPixel(x,y);
                int alpha = Color.alpha(pixel);
                if(alpha > 200) {
                    int stronginess = Color.red(pixel);
                    if(stronginess > max)
                        max = stronginess;
                }
            }
        }
        float divider = (float)Math.pow(max/2, power)/maxRadius;
        for(int x = 0; x< width && cont; x++){
            for(int y=0; y<height && cont; y++){
                int pixel = bitmapCrackMap.getPixel(x,y);
                int alpha = Color.alpha(pixel);
                if(alpha > 200){
                    int stronginess = Color.red(pixel);
                    stronginess -= max/2;
                    if(stronginess < 0)
                        stronginess = 0;
                    float size = (float)Math.pow(stronginess, power)/divider;
                    if(size > 0) {
                        canvas.drawCircle(x, y, size, spotsPaint);
                        //рисовать пятнфшки
                        if(getRandom(8) == 0) {
                            float px = (getRandom(2) == 0 ? -1 : 1) * (getRandom(DPI / 20) + size) + x;
                            float py = (getRandom(2) == 0 ? -1 : 1) * (getRandom(DPI / 20) + size) + y;
                            float psize = getRandom(3) + 1;
                            canvas.drawCircle(px, py, psize, spotsPaint);
                        }
                    }
                }
            }
            setText("Рисование пятен (" + x + "/" + width + ")");
            context.visualizeBitmap(bitmap);
        }
    }

    float getRandom(float max){
        if(max == 0)
            return 0;
        float r = random.nextInt((int)(max * 100f));
        return r/100f;
    }
    int getRandom(int max){
        return random.nextInt(max);
    }

}

class DialogLoading{
    private int backgroundColor = Color.rgb(39,50,56);
    private Context _context;
    private Dialog menu_dialog;
    private LinearLayout _linearLayout;
    private TextView _textView;
    private Button _button;
    private WaitingCircle _waWaitingCircle;
    private DialogInterface.OnClickListener _cancelListener;
    private String textToShow = "";
    private Handler handler;

    public DialogLoading(Context in_context, String buttonText, DialogInterface.OnClickListener cancelListener, String message){
        _context = in_context;
        _cancelListener = cancelListener;
        handler = new Handler();
        WindowManager windowManager = (WindowManager) in_context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int DPI = displayMetrics.densityDpi;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        lp.setMargins(2,20,2,20);
        _linearLayout = new LinearLayout(in_context);
        _linearLayout.setBackgroundColor(backgroundColor);
        _waWaitingCircle = new WaitingCircle(_context);
        _waWaitingCircle.setLayoutParams(new ViewGroup.LayoutParams(DPI / 2, DPI / 2));
        _linearLayout.addView(_waWaitingCircle);
        _textView = new TextView(in_context);
        _textView.setText(message);
        _textView.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams textLP = new LinearLayout.LayoutParams((int)(new Paint().measureText(message)*3+DPI/5), ViewGroup.LayoutParams.FILL_PARENT);
        textLP.setMargins(2, 20, 2, 20);
        _textView.setLayoutParams(textLP);
        _textView.setPadding(10, 0, 10, 0);
        _textView.setGravity(Gravity.CENTER);
        _linearLayout.addView(_textView);
        if(buttonText != null && cancelListener != null){
            _button = new AnimatedButton(in_context);
            _button.setText("   " + buttonText + "   " );
            _button.setLayoutParams(lp);
            _button.setOnClickListener(getCancelButtonListener());
            _linearLayout.addView(_button);
        }

        menu_dialog = new Dialog(in_context);
        menu_dialog.setCanceledOnTouchOutside(true);
        menu_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
        wLayoutParams.width = WindowManager.LayoutParams.FILL_PARENT;
        wLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.DIM_AMOUNT_CHANGED;// | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wLayoutParams.horizontalMargin = 0f;
        wLayoutParams.verticalMargin = 0f;
        wLayoutParams.dimAmount = 0.0f;
        //wLayoutParams.windowAnimations = R.style.DialogAnimation;
        wLayoutParams.gravity = Gravity.BOTTOM;// | Gravity.FILL_HORIZONTAL;
        menu_dialog.getWindow().setAttributes(wLayoutParams);
        menu_dialog.setContentView(_linearLayout);
        //menu_dialog.setOnKeyListener(getOnKeyListener());
        menu_dialog.setCancelable(false);
    }
    public void show(){
        //dialog_loading.show();
        menu_dialog.show();
    }
    public void cancel(){
        //dialog_loading.dismiss();
        menu_dialog.cancel();
    }
    public void setText(String text)
    {
        textToShow = text;
        handler.post(new Runnable() {
            @Override
            public void run() {
                _textView.setText(textToShow);
            }
        });
    }
    private View.OnClickListener getCancelButtonListener(){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_dialog.cancel();
                _cancelListener.onClick(null, 0);
            }
        };
    }
//    private DialogInterface.OnKeyListener getOnKeyListener(){
//        return new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
//                    Dialog.OnClickListener = getCancelButtonListener();
//                    getCancelButtonListener().onClick(null);
//                    return true;
//                }
//                return false;
//            }
//        };
//    }

    private class WaitingCircle extends View{
        Context wc_context;
        Timer _timer;
        android.os.Handler _handler;
        int _width;
        int _height;
        int _desiredSize;
        int strokeSize = 10;
        int color = Color.WHITE;
        RectF circleRect;
        int degress = 0; //0...360
        int size = 0; //0...360
        Paint _paint = new Paint();

        public WaitingCircle(Context _wc_context){
            super(_wc_context);
            wc_context = _wc_context;
            _paint.setAntiAlias(true);
            _paint.setStyle(Paint.Style.STROKE);
            _paint.setColor(color);
            _paint.setStrokeWidth(strokeSize);
            _desiredSize = _context.getResources().getDisplayMetrics().densityDpi/2;
            _width = _height = _desiredSize;
        }
        @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            WindowManager windowManager = (WindowManager) wc_context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            height = width = displayMetrics.densityDpi / 2;
//                if(getLayoutParams() != null) {
//                    height = Math.min(height, getLayoutParams().height);
//                    width = Math.min(height, getLayoutParams().width);
//
//                }else{
//                    height = width = Math.min(height, width);
//                }

            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
            );
        }
        @Override protected void onSizeChanged (int w, int h, int oldw, int oldh){
            super.onSizeChanged(w,h,oldw, oldh);
            _width = w;
            _height = h;
            strokeSize = w / 15;
            circleRect = new RectF(strokeSize*2, strokeSize*2, getWidth()-strokeSize*2, getHeight()-strokeSize*2);
        }
        @Override protected void onDraw(Canvas canvas){
            canvas.drawColor(backgroundColor);
            canvas.drawArc(circleRect, degress, size, false, _paint);
        }
        @Override protected void onAttachedToWindow (){
            super.onAttachedToWindow();
            _handler = new Handler();
            _timer = new Timer();
            _timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    step();
                    postInvalidate();
                }
            }, 0, 20);
        }
        @Override protected void onDetachedFromWindow (){
            super.onDetachedFromWindow();
            _timer.cancel();
        }

        boolean addSize = true;
        int sizeSpeed = 3;
        int moveSpeed = 5;
        void step(){
            if(addSize) {
                degress = addDegress(degress, moveSpeed - sizeSpeed);
                size += sizeSpeed;
                if(size > 300)
                    addSize = !addSize;
            }
            else{
                degress = addDegress(degress, moveSpeed + sizeSpeed);
                size -= sizeSpeed;
                if(size < 10)
                    addSize = !addSize;
            }
        }
        int addDegress(int orig, int add){
            int result = orig + add;
            while(result > 360){
                result -= 360;
            }
            return result;
        }
        int subDegress(int orig, int sub){
            int result = orig + sub;
            while(result < 0){
                result += 360;
            }
            return result;
        }
    }
}

class AnimatedButton extends Button{
    int backgroudColor = Color.rgb(67,86,92);
    Paint borderPaint = new Paint();
    int circleOpacityMax = 160;
    int circleDelay = 20;

    int circleX = -1;
    int circleY = -1;
    int circleSize = 0;
    int circleOpacity = circleOpacityMax;
    Timer circleTimer = null;
    Paint circlePaint = new Paint();

    public AnimatedButton(Context context) {
        super(context);
        setTextColor(Color.WHITE);
        setBackgroundColor(Color.TRANSPARENT);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(circleTimer != null)
            circleTimer.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(backgroudColor);
        canvas.drawRect(1,1,getWidth()-1, getHeight()-1, borderPaint);
        circlePaint.setColor(Color.argb(circleOpacity, 255, 255, 255));
        canvas.drawCircle(circleX, circleY, circleSize, circlePaint);
        super.onDraw(canvas);
    }

    private void beginHighlight(int x, int y){
        if(circleTimer != null){
            circleTimer.cancel();
        }

        circleX = x;
        circleY = y;
        circleSize = 10;
        circleOpacity = circleOpacityMax;
        circleTimer = new Timer();
        circleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                circleSize *= 1.5;
                circleOpacity -= 8;
                if (circleOpacity <= 0)
                    circleTimer.cancel();
                postInvalidate();
            }
        }, circleDelay, circleDelay);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if(action == MotionEvent.ACTION_DOWN){                                         //залить черным
            beginHighlight((int)event.getX(), (int)event.getY());
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(event.getX() < 0  || event.getY() < 0 || event.getX() > getWidth() || event.getY() > getHeight()) {//если вышли за пределы элемента
            }
        }
        else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){ //отпустили палец
        }
        return super.onTouchEvent(event);
    }

    Handler clickHandler = new Handler();
    @Override
    public boolean performClick() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                clickHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        click();
                    }
                });
            }
        }, 300);
        return true;
    }
    void click(){
        super.performClick();
    }
}
