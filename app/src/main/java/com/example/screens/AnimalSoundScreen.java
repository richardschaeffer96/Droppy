package com.example.screens;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.MainActivity;
import com.example.deinvirtuellerfreund.R;
import com.example.voice.Preprocessor;
import com.example.voice.RecordHelper;
import com.example.voice.TFLiteClassifier;

import java.io.IOException;
import java.util.Random;

public class AnimalSoundScreen implements Runnable {

    public AnimalSoundScreen(Activity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.minigame_animals);
        tvCurAn=activity.findViewById(R.id.cur_animal);
        tvWantedAn=activity.findViewById(R.id.wanted_animal);
        wantedAnImage=activity.findViewById(R.id.wanted_animal_pic);
        tvSecs = activity.findViewById(R.id.seconds_left_animals);
        tvSecs.setText(secsLeft + " s");
        time = System.currentTimeMillis();
        recordHelper=new RecordHelper(activity);
        initStartCondition();
        start();
    }

    private TextView tv_progress;
    private Activity activity;
    private static TextView tvSecs;
    private double time;
    private double dTime;
    private int secsLeft=25;
    private Thread clockThread = null;
    private RecordHelper recordHelper;
    private Random r=new Random();
    private TextView tvCurAn;
    private TextView tvWantedAn;
    private ImageView wantedAnImage;
    private Drawable drawable;
    private int change=0;


    private String[] animals ={"Katze","Hund","Schwein","Schaf", "Neutral", "Stille"};
    private String modelFile="tiergeräusche_3_seks_claudia.lite";
    private int startInd=0;



    // Initialisiere, ob Droppie zu Beginn WÜTEND oder TRAURIG ist
    private void initStartCondition() {
        tv_progress=activity.findViewById(R.id.number_animals);
        tv_progress.setText(0);
        int ind=r.nextInt(animals.length);
        tvWantedAn.setText(animals[ind]);
        if(animals[ind]=="Katze"){
            wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.cat));
        }else if(animals[ind]=="Hund"){
            wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.dog));
        }else if(animals[ind]=="Schwein"){
            wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.pig));
        }else{
            wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.sheep));
        }
    }

    // Nimmt einen beim Sprechen auf
    private void startRecording() {
        if(recordHelper.getRecording()==false){
            System.out.println("START RECORDING");
            recordHelper.startRecording();
        }
    }

    // Nach Aufnahme wird Emotion ausgewertet
    // Je nach Startsetting (wütend oder traurig) erhält man für REDEN oder LACHEN Punkte
    private void evaluateEmotion(int ind) {
        String an= animals[ind];
        tvCurAn.setText(an);
        if(tvWantedAn.getText().equals(tvCurAn.getText())) {
            System.out.println(tvWantedAn.getText()+"=="+tvCurAn.getText());
            int cur=Integer.parseInt(tv_progress.getText().toString())+1;
            tv_progress.setText(cur);
            int rand = r.nextInt(4);
            tvWantedAn.setText(animals[rand]);

            if(animals[rand]=="Katze"){
                wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.cat));
            }else if(animals[rand]=="Hund"){
                wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.dog));
            }else if(animals[rand]=="Schwein"){
                wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.pig));
            }else{
                wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.sheep));
            }
        }
    }

    // Beende Aufnahme, starte Aufbereitung der Daten und Beginne Auswertung
    private void stopRecording() {
        if(recordHelper.getRecording()==true) {
            System.out.println("STOP RECORDING");
            recordHelper.stopRecording();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        short[] signal = new short[0];
                        try {
                            signal = recordHelper.transformToWavData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Preprocessor prep = new Preprocessor();
                        float[][] mels = prep.preprocessAudioFile(signal, 39);
                        TFLiteClassifier tflite = new TFLiteClassifier(activity);
                        int ind=tflite.recognize(mels,modelFile, animals.length);
                        evaluateEmotion(ind);
                    }
                }
            });
        }
    }

    // Starte einen Thread
    public void start() {
        if (clockThread == null) {
            clockThread = new Thread(this, "Clock");
            clockThread.start();
        }
    }

    // Das hier ist der Thread
    // Es wird je Sekunde eine neue Aufnahme gestartet und ausgewertet
    // Die Aufnahmen sind also alle 1 Sekunde lang
    // Wird eine Aufnahme gestoppt, wird sofort die nächste gestartet
    // Außerdem läuft die Zeit ab. Pro Sekunde wird auch oben die Zeitanzeige aktualisiert
    @Override
    public void run() {
        Thread myThread = Thread.currentThread();
        while (clockThread == myThread) {
            startRecording();
            double curTime = System.currentTimeMillis();
            dTime += curTime - time;
            time = curTime;
            if (dTime > 1000f) {
                synchronized (this) {
                    change++;
                    dTime = 0;
                    secsLeft--;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvSecs.setText(secsLeft + " s");
                        }
                    });
                    if (secsLeft <= 0) {
                        stopRecording();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(Integer.parseInt(tv_progress.getText().toString())>=3) {
                                    activity.setContentView(R.layout.won_screen);
                                    ((MainActivity)activity).changeLevel(10);
                                } else {
                                    activity.setContentView(R.layout.gameover_screen);
                                }
                            }
                        });
                        break;
                    }
                    stopRecording();
                    if(change>5) {
                        change=0;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int ind=r.nextInt(4);
                                if(animals[ind]=="Katze"){
                                    wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.cat));
                                }else if(animals[ind]=="Hund"){
                                    wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.dog));
                                }else if(animals[ind]=="Schwein"){
                                    wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.pig));
                                }else{
                                    wantedAnImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.sheep));
                                }
                            }
                        });
                    }
                }
            }
        }
    }

}
