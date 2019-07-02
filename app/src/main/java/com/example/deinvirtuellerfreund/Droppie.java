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


    //possible parameters: "normal", "sad", "happy"
    public void changeEmotion(Emotion emotion){
        ImageView dropsi=activity.findViewById(R.id.droppy_base);
        switch (emotion) {
            case Anger:
                //droppy.setImageDrawable(activity.getResources().getDrawable(R.drawable.dropsi_angry));
                break;
            case Neutral:
                //droppy.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_normal));
                break;
            case Sadness:
                //droppy.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_sad));
                break;
            case Happiness:
                //droppy.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_smiling));
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
