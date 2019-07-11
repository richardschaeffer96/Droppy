package com.example.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.R;

import java.util.Random;

//Minigame Essen
public class FoodScreen {

    private TextView number;
    private ImageView apple;
    private ImageView rottenApple;
    private ImageView buttonOne;
    private ImageView buttonTwo;
    private ImageView buttonThree;
    private ImageView backButton;
    private Droppie droppie;
    private boolean isRottenApple;
    private Activity activity;
    private int points;


    @SuppressLint("SetTextI18n")
    public FoodScreen(final Activity activity, int points) {
        this.activity = activity;
        this.activity.setContentView(R.layout.food_game);
        this.points = points;
        number = this.activity.findViewById(R.id.number);
        number.setText(Integer.toString(points));
        apple = this.activity.findViewById(R.id.apple);
        rottenApple = this.activity.findViewById(R.id.rotten_apple);
        buttonOne = this.activity.findViewById(R.id.button_1);
        buttonTwo = this.activity.findViewById(R.id.button_2);
        buttonThree = this.activity.findViewById(R.id.button_3);
        backButton = this.activity.findViewById(R.id.button_back);
        droppie = new Droppie(activity);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setContentView(R.layout.activity_main);
            }
        });

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApple();
            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApple();
            }
        });

        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApple();
            }
        });

        apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedApple();
            }
        });

        rottenApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedApple();
            }
        });
    }

    private void showApple() {
        buttonOne.setVisibility(View.INVISIBLE);
        buttonTwo.setVisibility(View.INVISIBLE);
        buttonThree.setVisibility(View.INVISIBLE);
        if (isRottenApple()) {
            isRottenApple = true;
            rottenApple.setVisibility(View.VISIBLE);
        } else {
            isRottenApple = false;
            apple.setVisibility(View.VISIBLE);
        }
    }

    private void feedApple() {
        if (isRottenApple) {
            this.points--;
            droppie.changeEmotion(Emotion.Sadness);
            activity.setContentView(R.layout.minigame_cheer_dropsy);
            new CheerDropsyFeedScreen(activity, points);


//            infoText = findViewById(R.id.info_text);
//            infoHeadline = findViewById(R.id.info_headline);

//            rottenApple.setVisibility(View.INVISIBLE);
            //          droppie.changeEmotion(Emotion.Sadness);
            /*    infoText.setText("Du hast Droppy einen vergammelten Apfel gegeben :( \n Stecke Droppy mit deinem Lachen an, um sein Vertrauen zurück zu gewinnen.");*/

        } else {
            points++;
            String level = Integer.toString(points);
            number.setText(level);
            droppie.changeEmotion(Emotion.Happiness);
            apple.setVisibility(View.INVISIBLE);
            buttonOne.setVisibility(View.VISIBLE);
            buttonTwo.setVisibility(View.VISIBLE);
            buttonThree.setVisibility(View.VISIBLE);
        }
    }

    private boolean isRottenApple() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        return randomNum % 2 != 0;
    }
}
