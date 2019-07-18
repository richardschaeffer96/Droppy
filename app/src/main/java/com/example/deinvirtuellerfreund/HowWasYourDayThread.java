package com.example.deinvirtuellerfreund;

import android.app.Activity;

import com.example.voice.Preprocessor;
import com.example.voice.TFLiteClassifier;

import java.util.ArrayList;

public class HowWasYourDayThread implements Runnable {

    public HowWasYourDayThread(Activity activity, short[]signal) {
        this.activity=activity;
        this.signal=signal;
        droppie=new Droppie(activity);
        start();
    }

    private Activity activity;
    private short[]signal;
    private Thread clockThread;
    private Droppie droppie;



    // Starte einen Thread
    public void start() {
        if (clockThread == null) {
            clockThread = new Thread(this, "Clock");
            clockThread.start();
        }
    }

    // Das hier ist der Thread
    @Override
    public void run() {
        Thread myThread = Thread.currentThread();
        while (clockThread == myThread) {
            Preprocessor prep=new Preprocessor();
            ArrayList<float[][]> seconds_as_mels=prep.cutAndPreprocess(signal,39);
            TFLiteClassifier tflite=new TFLiteClassifier(activity);
            final Emotion emotion=tflite.recognizeEmotion(seconds_as_mels);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (emotion) {
                        case Happiness:
                            droppie.changeEmotion(Emotion.Happiness);
                            ((MainActivity)activity).saySentence(((MainActivity)activity).w_positiv,null);
                            break;
                        case Sadness:
                            droppie.changeEmotion(Emotion.Sadness);
                            ((MainActivity)activity).saySentence(((MainActivity)activity).w_negativ,((MainActivity)activity).w_joke_question);
                            break;
                        case Neutral:
                            droppie.changeEmotion(Emotion.Neutral);
                            ((MainActivity)activity).saySentence(((MainActivity)activity).w_joke_question,null);
                            break;
                        case Anger:
                            droppie.changeEmotion(Emotion.Anger);
                            ((MainActivity)activity).saySentence(((MainActivity)activity).w_negativ,null);
                            break;
                    }
                }
            });
            break;
        }
    }

}
