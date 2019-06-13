package com.example.deinvirtuellerfreund;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voice.Preprocessor;
import com.example.voice.RecordHelper;
import com.example.voice.TFLiteClassifier;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RecordHelper recordHelper;
    private Activity activity;

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
        level_number = findViewById(R.id.level_number);
        dropsi = findViewById(R.id.Dropsi_Image);
        info_overlay = new Dialog(this);
        talk = findViewById(R.id.Button_Micro);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weather_icon = findViewById(R.id.weather_icon);
        weather_icon.setTypeface(weatherFont);

        activity = this;
        recordHelper=new RecordHelper(activity);


        taskLoadUp(city);

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
                        Preprocessor prep=new Preprocessor();
                        float[][]mels=prep.preprocessAudioFile(signal,39);
                        TFLiteClassifier tflite=new TFLiteClassifier(activity);
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

    public void info(View v){
        info_overlay.setContentView(R.layout.info_overlay);
        info_overlay.show();

    }

    public void close(View v){
        info_overlay.hide();
    }

    //public void talk(View v){ }

    public void feed(View v){
        if(level_bar.getProgress()!=100){
            Integer add = 10 + level_bar.getProgress();
            level_bar.setProgress(add);
            change_dropsi("happy");
        } else {
            Integer level = Integer.parseInt(level_number.getText().toString());
            level = level +1;
            level_number.setText(level.toString());
            level_bar.setProgress(0);
            change_dropsi("normal");
        }
    }

    //possible parameters: "normal", "sad", "happy"
    public void change_dropsi(String mood){
        if(mood=="happy"){
            dropsi.setImageDrawable(getResources().getDrawable(R.drawable.drobsi_smiling));
        } else if (mood=="sad"){
            dropsi.setImageDrawable(getResources().getDrawable(R.drawable.drobsi_sad));
        } else if (mood=="normal"){
            dropsi.setImageDrawable(getResources().getDrawable(R.drawable.drobsi_normal));
        }
    }

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


}
