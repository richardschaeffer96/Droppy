package com.example.screens;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera.GraphicOverlay;
import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class JokeChallenge implements Runnable{

    public JokeChallenge(Activity activity, ImageView mouth) {
        this.activity = activity;
        activity.setContentView(R.layout.minigame_cheer);
        this.mouth = mouth;

        animTalking = AnimationUtils.loadAnimation(activity, R.anim.talkinganimation);
        animMouth = AnimationUtils.loadAnimation(activity, R.anim.mouthanimation);

        progressBar=activity.findViewById(R.id.emotion_bar);
        progressBar.setProgress(current_progress);
        header=activity.findViewById(R.id.emotion_header);
        header.setText("Durchhaltevermögen");
        droppie=new Droppie(activity);

        start();
        listRaw();
        preJokeChallenge();
    }

    private TextView header;
    private ProgressBar progressBar;
    private Activity activity;
    private Thread clockThread = null;
    private Droppie droppie;

    public static int current_progress = 100;

    Animation animMouth;
    Animation animTalking;

    ImageView mouth;

    Field[] fields;
    ArrayList<String> jokes = new ArrayList<String>();
    Integer jokecount = 0;

    MediaPlayer player;

    public void preJokeChallenge(){
        for(int i=0; i<fields.length; i++){
            jokes.add(fields[i].getName());
        }

        Collections.shuffle(jokes);

        GraphicOverlay.delay_active=false;

        jokeChallenge();
    }

    public void jokeChallenge() {

        int max_jokes = 3;

        if(jokecount==max_jokes){
            GraphicOverlay.delay_active=true;
            System.out.println("Du hast " + progressBar.getProgress() + " Punkte erhalten!");
        }

        if(jokecount<max_jokes){
            if (player == null) {

                String jokeString = jokes.get(0);
                jokes.remove(0);

                System.out.println("Witz: " + jokeString);

                Uri uri = Uri.parse("android.resource://com.example.deinvirtuellerfreund/raw/" + jokeString);

                droppie.changeEmotion(Emotion.Talking);
                mouth.startAnimation(animTalking);

                player = MediaPlayer.create(activity, uri);
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
            Toast.makeText(activity, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    // Starte einen Thread
    public void start() {
        if (clockThread == null) {
            clockThread = new Thread(this, "Clock");
            clockThread.start();
        }
    }

    @Override
    public void run() {
        Thread myThread = Thread.currentThread();
        while (clockThread == myThread) {
            progressBar.setProgress(current_progress);
        }
    }

}
