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
        tvSecs.setText(""+secsLeft);
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
    private int secsLeft=20;
    private Thread clockThread = null;
    private RecordHelper recordHelper;
    private Random r=new Random();
    private Droppie droppie;

    private String[]startCondition={"wütend","traurig"};
    private String[]emotions={"lachen","stille","reden","husten","gähnen"};
    private String modelFile="lachen_stille_reden_alle.lite";
    private int startInd=0;



    // Initialisiere, ob Droppie zu Beginn WÜTEND oder TRAURIG ist
    private void initStartCondition() {
        progressBar=activity.findViewById(R.id.emotion_bar);
        progressBar.setMax(5);
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
            recordHelper.startRecording();
        }
    }


    // Nach Aufnahme wird Emotion ausgewertet
    // Je nach Startsetting (wütend oder traurig) erhält man für REDEN oder LACHEN Punkte
    private void evaluateEmotion(int ind) {
        String em=emotions[ind];
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
            if(cur==3) {
                droppie.changeEmotion(Emotion.Happiness);
                ((MainActivity)activity).saySentence(((MainActivity)activity).w_streicheln,null);
            } else if(cur==5) {
                secsLeft=0;

            }
            progressBar.setProgress(cur);
        }
    }

    // Beende Aufnahme, starte Aufbereitung der Daten und Beginne Auswertung
    private void stopRecording() {
        if(recordHelper.getRecording()==true) {
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

    // starte thread
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
            synchronized (this) {
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
                                tvSecs.setText("" + Math.max(secsLeft,0));
                            }
                        });
                        if (secsLeft <= 0) {
                            stopRecording();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressBar.getProgress() >= 5) {
                                        activity.setContentView(R.layout.won_screen);
                                        ((MainActivity) activity).changeLevel(5);
                                        ((MainActivity) activity).saySentence(((MainActivity) activity).w_gewonnen, null);
                                    } else {
                                        activity.setContentView(R.layout.gameover_screen);
                                        ((MainActivity) activity).saySentence(((MainActivity) activity).w_gameover, null);
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

}
