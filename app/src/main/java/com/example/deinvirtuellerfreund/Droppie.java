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
        ImageView dropsi=activity.findViewById(R.id.Dropsi_Image);
        switch (emotion) {
            case Anger:
                dropsi.setImageDrawable(activity.getResources().getDrawable(R.drawable.dropsi_angry));
                break;
            case Neutral:
                dropsi.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_normal));
                break;
            case Sadness:
                dropsi.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_sad));
                break;
            case Happiness:
                dropsi.setImageDrawable(activity.getResources().getDrawable(R.drawable.drobsi_smiling));
                break;
        }
    }

}
