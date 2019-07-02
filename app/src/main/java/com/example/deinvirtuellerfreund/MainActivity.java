package com.example.deinvirtuellerfreund;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera.CameraPreview;
import com.example.camera.GraphicFaceTracker;
import com.example.camera.GraphicOverlay;
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
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.util.EncodingUtils;
public class MainActivity extends AppCompatActivity {

    private RecordHelper recordHelper;
    public static Activity activity;

    private CameraPreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private CameraSource mCameraSource = null;

    private static final String TAG = "VideoFaceDetection";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int RC_HANDLE_GMS = 2;

    Dialog info_overlay;
    ProgressBar level_bar;
    TextView level_number;
    ImageView dropsi;
    ImageView talk;
    Typeface weatherFont;
    TextView weather_icon;
    String city = "80331";
    String OPEN_WEATHER_MAP_API = "2edbde5bf49570bc94fde0a1051a5737";
    /* Please Put your API KEY here */
    // my api String OPEN_WEATHER_MAP_API = "36682bdc6277f8859ee8ae12f272ea9a";
    /* Please Put your API KEY here */
    Boolean check_button = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        level_bar = findViewById(R.id.level_bar);
        if(fileIsExists("levelbar.txt")){

            level_bar.setProgress(levelbarauslesen("levelbar.txt"));

        }
        level_number = findViewById(R.id.level_number);
        if(fileIsExists("levelnumber.txt")){

            level_number.setText(levelnumberauslesen("levelnumber.txt"));

        }
        dropsi = findViewById(R.id.Dropsi_Image);
        info_overlay = new Dialog(this);
        talk = findViewById(R.id.Button_Micro);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weather_icon = findViewById(R.id.weather_icon);
        weather_icon.setTypeface(weatherFont);

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

        talk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (recordHelper.getRecording() == false) {
                    recordHelper.startRecording();
                    talk.setImageDrawable(getResources().getDrawable(R.drawable.button_micro_clicked));
                } else {
                    recordHelper.stopRecording();
                    talk.setImageDrawable(getResources().getDrawable(R.drawable.button_micro));
                    try {
                        short[] signal = recordHelper.transformToWavData();
                        Preprocessor prep = new Preprocessor();
                        float[][] mels = prep.preprocessAudioFile(signal, 39);
                        TFLiteClassifier tflite = new TFLiteClassifier(activity);
                        tflite.recognize(mels);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });



        /*
        talk.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                recordHelper=new RecordHelper(activity);
                if(event.getAction()==MotionEvent.ACTION_DOWN&&check_button.equals(false)){
                    check_button = true;
                    recordHelper.startRecording();
                }else if (event.getAction()==MotionEvent.ACTION_UP){
                    recordHelper.stopRecording();
                    try {
                        short[]signal=recordHelper.transformToWavData();
                        Preprocessor prep=new Preprocessor();
                        float[][]mels=prep.preprocessAudioFile(signal,39);
                        TFLiteClassifier tflite=new TFLiteClassifier(activity);
                        tflite.recognize(mels);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    check_button=false;
                }
                return false;
            }

        });

        */

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
        Integer Ergebnis;
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

        return Ergebnis= Integer.parseInt(fileContent);
    }
    public Integer levelnumberauslesen(String filename){
        String fileContent="";
        Integer Ergebnis;
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

        /* TO-DO Turn Camera Sound OFF!
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        if (info.canDisableShutterSound) {
            mCamera.enableShutterSound(false);
        }
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

    }

    public void close(View v){
        info_overlay.hide();
    }

    //public void talk(View v){ }

    public void feed(View v){
        Droppie droppie=new Droppie(this);
        if(level_bar.getProgress()!=100){
            Integer add = 10 + level_bar.getProgress();
            level_bar.setProgress(add);
            droppie.changeEmotion(Emotion.Happiness);
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


    }}




    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
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
            System.out.println(xml);

            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                System.out.println(xml);
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
