package com.example.screens;

import android.app.Activity;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.MainActivity;
import com.example.deinvirtuellerfreund.R;
import com.example.voice.Preprocessor;
import com.example.voice.RecordHelper;
import com.example.voice.TFLiteClassifier;

import java.io.IOException;
import java.util.Random;

public class CheerScreen implements Runnable {

    public CheerScreen(Activity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.minigame_cheer);
        tvSecs = activity.findViewById(R.id.seconds_left);
        tvSecs.setText(secsLeft + " s");
        time = System.currentTimeMillis();
        recordHelper=new RecordHelper(activity);
        initStartCondition();
        start();
    }

    private ProgressBar progressBar;
    private Activity activity;
    private static TextView tvSecs;
    private double time;
    private double dTime;
    private int secsLeft=15;
    private Thread clockThread = null;
    private RecordHelper recordHelper;
    private Random r=new Random();
    private Droppie droppie;

    private String[]startCondition={"wütend","traurig"};
    private String[]emotions={"lachen","stille","reden","klopfen","gähnen","husten"};
    private String modelFile="lachen_stille_reden.lite";
    private int startInd=0;



    // Initialisiere, ob Droppie zu Beginn WÜTEND oder TRAURIG ist
    private void initStartCondition() {
        progressBar=activity.findViewById(R.id.emotion_bar);
        progressBar.setMax(10);
        progressBar.setProgress(1);
        droppie=new Droppie(activity);
        startInd=r.nextInt(2);
        if(startInd==0) {
            droppie.changeEmotion(Emotion.Anger);
        } else {
            droppie.changeEmotion(Emotion.Sadness);
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
        String em=emotions[ind];
        System.out.println(em);
        boolean success=false;
        if(startInd==0) {
            // WÜTEND: BEKOMMT PUNKT FÜR REDEN
            if(em=="reden") {
                success=true;
            }
        } else {
            // TRAURIG: BEKOMMT PUNKT FÜR LACHEN
            if(em=="lachen") {
                success=true;
            }
        }
        if(success==true) {
            int cur=progressBar.getProgress()+1;
            if(cur==5) {
                droppie.changeEmotion(Emotion.Neutral);
            } else if(cur==7) {
                droppie.changeEmotion(Emotion.Happiness);
            }
            progressBar.setProgress(cur);
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
                        int ind=tflite.recognize(mels,modelFile,emotions.length);
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
                                if(progressBar.getProgress()>=5) {
                                    activity.setContentView(R.layout.won_screen);
                                } else {
                                    activity.setContentView(R.layout.gameover_screen);

                                }
                            }
                        });
                        break;
                    }
                    stopRecording();
                }
            }
        }

    }

}
