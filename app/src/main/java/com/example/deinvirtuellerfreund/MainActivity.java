package com.example.deinvirtuellerfreund;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Dialog info_overlay;
    ProgressBar level_bar;
    TextView level_number;
    ImageView dropsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level_bar = findViewById(R.id.level_bar);
        level_number = findViewById(R.id.level_number);
        dropsi = findViewById(R.id.Dropsi_Image);
        info_overlay = new Dialog(this);

    }

    public void info(View v){
        info_overlay.setContentView(R.layout.info_overlay);
        info_overlay.show();

    }

    public void close(View v){
        info_overlay.hide();
    }

    public void talk(View v){

    }

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
}
