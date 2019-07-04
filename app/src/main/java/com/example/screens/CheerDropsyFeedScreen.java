package com.example.screens;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.R;
import com.example.voice.Preprocessor;
import com.example.voice.RecordHelper;
import com.example.voice.TFLiteClassifier;

import java.io.IOException;
import java.util.Random;

public class CheerDropsyFeedScreen implements Runnable {

    public CheerDropsyFeedScreen(Activity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.minigame_cheer_dropsy);
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
    private int secsLeft=10;
    private Thread clockThread = null;
    private RecordHelper recordHelper;
    private Random r=new Random();
    private Droppie droppie;

    private String[]startCondition={"wütend","traurig"};
    private String[]emotions={"lachen","stille","reden"};
    private String modelFile="lachen_stille_reden.lite";
    private int startInd=0;



    // Initialisiere, ob Droppie zu Beginn WÜTEND oder TRAURIG ist
    private void initStartCondition() {
        progressBar=activity.findViewById(R.id.emotion_bar);
        progressBar.setMax(6);
        progressBar.setProgress(1);
        droppie=new Droppie(activity);
        droppie.changeEmotion(Emotion.Sadness);
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
        boolean success= em.equals("lachen");

        if(success) {
            int cur=progressBar.getProgress()+1;
            if(cur==3) {
                droppie.changeEmotion(Emotion.Neutral);
            } else if(cur==5) {
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
                        int ind=tflite.recognize(mels,modelFile);
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
                                activity.setContentView(R.layout.activity_food);
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
