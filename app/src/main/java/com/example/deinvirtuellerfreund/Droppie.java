package com.example.deinvirtuellerfreund;

import android.app.Activity;
import android.drm.DrmStore;
import android.media.Image;
import android.widget.ImageView;

public class Droppie {

    public Droppie(Activity activity) {
        this.activity=activity;
    }

    private Activity activity;

    public void changeEmotion(Emotion emotion){
        //ImageView dropsi=activity.findViewById(R.id.droppy_base);
        ImageView eyebrows = activity.findViewById(R.id.droppy_eyebrows);
        ImageView eyes = activity.findViewById(R.id.droppy_eyes);
        ImageView mouth = activity.findViewById(R.id.droppy_mouth);
        switch (emotion) {
            case Anger:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_angry_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_angry_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_angry_mouth));
                break;
            case Neutral:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_mouth));
                break;
            case Sadness:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_sad_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_sad_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_sad_mouth));
                break;
            case Happiness:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_lucky_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_lucky_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_lucky_mouth));
                break;
            case Talking:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_talking_mouth));
                break;
            case Poked:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_sad_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_neutral_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_talking_mouth));
                break;
            case Satisfied:
                eyebrows.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_lucky_eyebrows));
                eyes.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_lucky_eyes));
                mouth.setImageDrawable(activity.getResources().getDrawable(R.drawable.droppy_satisfied_mouth));
                break;
        }
    }

    public Emotion getEmotion(float[]array) {
        int i=0;
        float max=0;
        int maxInd=0;
        while(i<array.length) {
            if(array[i]>max) {
                max=array[i];
                maxInd=i;
            }
            i++;
        }
        System.out.println(Emotion.values()[maxInd]);
        return Emotion.values()[maxInd];
    }

}
