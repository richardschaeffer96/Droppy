package com.example.deinvirtuellerfreund;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera.CameraPreview;
import com.example.camera.GraphicFaceTracker;
import com.example.camera.GraphicOverlay;
import com.example.screens.AnimalSoundScreen;
import com.example.screens.CheerScreen;
import com.example.screens.FoodScreen;
import com.example.screens.InfoOverlayScreen;
import com.example.voice.Preprocessor;
import com.example.voice.RecordHelper;
import com.example.voice.TFLiteClassifier;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private RecordHelper recordHelper;
    public static Activity activity;

    private CameraPreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private CameraSource mCameraSource = null;

    private static final String TAG = "VideoFaceDetection";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int RC_HANDLE_GMS = 2;

    private String nextScreen;

    MediaPlayer player;
    Dialog info_overlay;
    TextView info_header;
    TextView info_text;
    public static ProgressBar level_bar;
    public static Boolean inMinigame;
    TextView level_number;
    TextView level_header;
    Field[] fields;
    Boolean new_joke = true;
    ArrayList<String> jokes = new ArrayList<String>();
    Integer jokecount = 0;
    public static Integer points = 0;
    public static Integer jokeProgress = 100;

    //BODY OF DROPPY
    ImageView droppy;
    ImageView eyebrows;
    ImageView eyes;
    ImageView mouth;
    ImageView collisionbox;

    ImageView talk;
    ImageView eat;
    ImageView info;
    Typeface weatherFont;
    TextView weather_icon;
    String city = "80331";
    String OPEN_WEATHER_MAP_API = "2edbde5bf49570bc94fde0a1051a5737";
    /* Please Put your API KEY here */
    // my api String OPEN_WEATHER_MAP_API = "36682bdc6277f8859ee8ae12f272ea9a";
    /* Please Put your API KEY here */
    Boolean check_button = false;

    Animation animWobble;
    Animation animEyes;
    Animation animEyebrows;
    Animation animMouth;
    Animation animTalking;
    Droppie droppie;

    private static float DownX = 0;
    private static float DownY = 0;
    private static float moveX = 0;
    private static float moveY = 0;
    private static long currentMS = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_INTERNET = 1;
    private static final int REQUEST_RECORD_AUDIO = 1;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 1;
    static Camera camera = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_INTERNET);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_NETWORK_STATE);
        }

        level_header = findViewById(R.id.level_header);

        level_bar = findViewById(R.id.level_bar);
        if(fileIsExists("levelbar.txt")){

            level_bar.setProgress(levelbarauslesen("levelbar.txt"));

        }
        level_number = findViewById(R.id.level_number);
        if(fileIsExists("levelnumber.txt")){

            level_number.setText(levelnumberauslesen("levelnumber.txt"));

        }

        inMinigame=false;

        droppie=new Droppie(this);

        droppy = findViewById(R.id.droppy_base);
        eyebrows = findViewById(R.id.droppy_eyebrows);
        eyes = findViewById(R.id.droppy_eyes);
        mouth = findViewById(R.id.droppy_mouth);
        collisionbox = findViewById(R.id.collisionbox);

        listRaw();

        collisionbox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DownX = event.getX();//float DownX
                        DownY = event.getY();//float DownY
                        moveX = 0;
                        moveY = 0;
                        currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
                        break;
                    case MotionEvent.ACTION_MOVE:
                        droppie.changeEmotion(Emotion.Satisfied);
                        moveX += Math.abs(event.getX() - DownX);//X轴距离
                        moveY += Math.abs(event.getY() - DownY);//y轴距离
                        DownX = event.getX();
                        DownY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                        //判断是否继续传递信号
                        if(moveTime>200&&(moveX>20||moveY>20)){
                            droppie.changeEmotion(Emotion.Happiness);
                            return true; //不再执行后面的事件，在这句前可写要执行的触摸相关代码。点击事件是发生在触摸弹起后
                        } else {
                            droppie.changeEmotion(Emotion.Poked);
                            }
                        break;
                }
                return true;//继续执行后面的代码
            }
        });

        info_overlay = new Dialog(this);

        info_overlay.setContentView(R.layout.info_overlay);

        info_header = info_overlay.findViewById(R.id.info_headline);
        info_text = info_overlay.findViewById(R.id.info_text);

        talk = findViewById(R.id.Button_Micro);
        eat = findViewById(R.id.Button_Eat);
        info = findViewById(R.id.Button_Info);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weather_icon = findViewById(R.id.weather_icon);
        weather_icon.setTypeface(weatherFont);

        animWobble = AnimationUtils.loadAnimation(this, R.anim.wobbleanimation);
        animWobble.setAnimationListener(this);
        droppy.startAnimation(animWobble);

        animEyes = AnimationUtils.loadAnimation(this, R.anim.eyesanimation);
        animEyes.setAnimationListener(this);
        eyes.startAnimation(animEyes);

        animEyebrows = AnimationUtils.loadAnimation(this, R.anim.eyebrowsanimation);
        animEyebrows.setAnimationListener(this);
        eyebrows.startAnimation(animEyebrows);

        animMouth = AnimationUtils.loadAnimation(this, R.anim.mouthanimation);
        animMouth.setAnimationListener(this);
        mouth.startAnimation(animMouth);

        animTalking = AnimationUtils.loadAnimation(this, R.anim.talkinganimation);
        animTalking.setAnimationListener(this);

        activity = this;
        recordHelper = new RecordHelper(activity);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);


        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        //} else {

        createCameraSource();
        //}

        //taskLoadUp(city);
        talk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recordHelper.getRecording()==false){
                    recordHelper.startRecording();
                    talk.setImageDrawable(getResources().getDrawable(R.drawable.button_micro_clicked));
                } else {
                    recordHelper.stopRecording();
                    talk.setImageDrawable(getResources().getDrawable(R.drawable.button_micro));
                    try {
                        short[]signal=recordHelper.transformToWavData();
                        new HowWasYourDayThread(activity,signal);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public boolean fileIsExists(String filename) {
        try {
            String AbsolutePath = getFilesDir().getAbsolutePath();
            File f = new File(AbsolutePath + "/" + filename);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public Integer levelbarauslesen(String filename){
        String fileContent="";

        try {
            FileInputStream fis;
            fis = openFileInput(filename);
            byte[] buffer = new byte[2];
            fis.read(buffer);

            fileContent = EncodingUtils.getString(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Integer.parseInt(fileContent);
    }
    public String levelnumberauslesen(String filename){
        String fileContent="";

        try {
            FileInputStream fis;
            fis = openFileInput(filename);
            byte[] buffer = new byte[1];
            fis.read(buffer);

            fileContent = EncodingUtils.getString(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileContent;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK) {
            createCameraSource();
        }
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        //camera sound off
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        /*camera sound on
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
         */

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }



    public void info(View v){
        info_overlay.setContentView(R.layout.info_overlay);
        info_overlay.show();
        nextScreen = "info";

    }

    public void close(View v){
        info_overlay.hide();

        switch (nextScreen) {
            case "info":
                break;
            case "cheer":
                activity.setContentView(R.layout.minigame_cheer);
                new CheerScreen(activity);
                break;
            case "food":
                activity.setContentView(R.layout.food_game);
                new FoodScreen(activity, 0);
                break;
            case "animal":
                activity.setContentView(R.layout.minigame_animals);
                new AnimalSoundScreen(activity);
                break;
            case "main":
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                break;
        }
    }

    //public void talk(View v){ }

    public void feed(View v){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, eat);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String gameTitle = (String) item.getTitle();
                switch (gameTitle) {
                    case "Witze":
                        nextScreen="joke";
                        info_header.setText("getResources().getString(R.string.joke_headline)");
                        info_text.setText("getResources().getString(R.string.joke_content)");
                        break;
                    case "Essen":
                        nextScreen="food";
                        info_header.setText(getResources().getString(R.string.food_headline));
                        info_text.setText(getResources().getString(R.string.food_content));
                        break;
                    case "Aufmuntern":
                        nextScreen="cheer";
                        info_header.setText(getResources().getString(R.string.cheer_headline));
                        info_text.setText(getResources().getString(R.string.cheer_content));
                        break;
                    case "Tiergeräusche":
                        nextScreen="animal";
                        info_header.setText(getResources().getString(R.string.animal_headline));
                        info_text.setText(getResources().getString(R.string.animal_content));
                        break;
                }

                info_overlay.show();

          //      Toast.makeText(MainActivity.this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popupMenu.show();
    }


    public void backToMain(View v){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }


    public void changeLevel(Integer score) {

        System.out.println("PROGRESS IST: " + level_bar.getProgress());

        if(level_bar.getProgress()<100){
            Integer add = score + level_bar.getProgress();
            level_bar.setProgress(add);
            try {
                FileOutputStream fos = openFileOutput("levelbar.txt",
                        Context.MODE_PRIVATE);
                String inputFileContext = add.toString();
                fos.write(inputFileContext.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fileIsExists("levelbar.txt")){
                System.out.println("levelbar datein existiert" );
            }
        } else {
            System.out.println("!!!!!!! LEVEL UP !!!!!!!!!");
            Integer level = Integer.parseInt(level_number.getText().toString());
            level = level +1;
            level_number.setText(level.toString());
            level_bar.setProgress(0);
            droppie.changeEmotion(Emotion.Neutral);
            try {
                FileOutputStream fos = openFileOutput("levelnumber.txt",
                        Context.MODE_PRIVATE);
                String inputFileContext = level.toString();
                fos.write(inputFileContext.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fileIsExists("levelnumber.txt")){
                System.out.println("levelnumber datein existiert" );
            }
        }
    }

    public void preJokeChallenge(){
        for(int i=0; i<fields.length; i++){
            jokes.add(fields[i].getName());
        }

        Collections.shuffle(jokes);


        GraphicOverlay.delay_active=false;

        inMinigame=true;

        jokeChallenge();
    }



    public void tellJoke(View v){

        inMinigame=false;

        for(int i=0; i<fields.length; i++){
            jokes.add(fields[i].getName());
        }

        Collections.shuffle(jokes);
        GraphicOverlay.delay_active=false;

        String jokeString = jokes.get(0);

        Uri uri = Uri.parse("android.resource://com.example.deinvirtuellerfreund/raw/" + jokeString);

        droppie.changeEmotion(Emotion.Talking);
        mouth.startAnimation(animTalking);

        player = MediaPlayer.create(this, uri);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayer();
                GraphicOverlay.delay_active=true;
                if(points>=3){
                    mouth.startAnimation(animMouth);
                    droppie.changeEmotion(Emotion.Happiness);
                    points=0;
                }else{
                    mouth.startAnimation(animMouth);
                    droppie.changeEmotion(Emotion.Sadness);
                    points=0;
                }
            }
        });

        player.start();

        }

    public void jokeChallenge() {

        int max_jokes = 1;

        if(jokeProgress<=0){
            jokecount=max_jokes;
        }

        setJokeView();

        if(jokecount<max_jokes){

            if (player == null) {

                String jokeString = jokes.get(0);
                jokes.remove(0);

                Uri uri = Uri.parse("android.resource://com.example.deinvirtuellerfreund/raw/" + jokeString);

                droppie.changeEmotion(Emotion.Talking);
                mouth.startAnimation(animTalking);

                player = MediaPlayer.create(this, uri);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayer();
                        mouth.startAnimation(animMouth);
                        droppie.changeEmotion(Emotion.Happiness);

                        synchronized (this) {
                            try {
                                this.wait(2000);
                                jokecount++;
                                jokeChallenge();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                player.start();

            }


        }

        if(jokecount==max_jokes){
            GraphicOverlay.delay_active=true;
            inMinigame=false;
            deleteJokeView(jokeProgress);
        }

    }

    public void setJokeView(){
        level_header.setText("Durchhaltevermögen");
        level_number.setVisibility(View.INVISIBLE);
        weather_icon.setVisibility(View.INVISIBLE);
        talk.setVisibility(View.INVISIBLE);
        eat.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);
        level_bar.setProgress(jokeProgress);
    }

    public void deleteJokeView(Integer progress){

        jokecount=0;

        if(progress>80){
            points=50;
        }else if(progress>50&&progress<80){
            points=15;
        }else if(progress<50&&progress>30){
            points=10;
        }else if(progress<30){
            points=0;
        }

        jokeProgress = 100;

        level_header.setText("Level: ");
        level_number.setVisibility(View.VISIBLE);
        weather_icon.setVisibility(View.VISIBLE);
        talk.setVisibility(View.VISIBLE);
        eat.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        level_bar.setProgress(levelbarauslesen("levelbar.txt"));
        if(points==0){

        }else{
            changeLevel(points);

        }
    }

    public void listRaw(){
        fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            Log.i("Raw Asset: ", fields[count].getName());
        }
    }



    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStop() {
        super.onStop();
        stopPlayer();
    }

    public void foodGame() {
        String headline = getResources().getString(R.string.food_headline);
        String content = getResources().getString(R.string.food_content);
        setContentView(R.layout.info_overlay);
        new InfoOverlayScreen(this, "food", headline, content);
    }

    public void cheerGame() {
        String headline = getResources().getString(R.string.cheer_headline);
        String content = getResources().getString(R.string.cheer_content);
        setContentView(R.layout.info_overlay);
        new InfoOverlayScreen(this, "cheer", headline, content);
    }

    public void animalGame() {
        String headline = getResources().getString(R.string.animal_headline);
        String content = getResources().getString(R.string.animal_content);
        setContentView(R.layout.info_overlay);
        new InfoOverlayScreen(this, "animal", headline, content);
    }



    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
       // droppy.startAnimation(animWobble);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                if (xml==null){

                } else {
                    JSONObject json = new JSONObject(xml);
                    if (json != null) {
                        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                        JSONObject main = json.getJSONObject("main");
                        DateFormat df = DateFormat.getDateTimeInstance();

                        //cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                        //detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                        //currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°");
                        //humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                        //pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                        //updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                        weather_icon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                                json.getJSONObject("sys").getLong("sunrise") * 1000,
                                json.getJSONObject("sys").getLong("sunset") * 1000)));

                        //loader.setVisibility(View.GONE);

                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }


        }



    }

    public void update_weather(View v){
        taskLoadUp(city);
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

}
