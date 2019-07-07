package com.example.deinvirtuellerfreund;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FoodActivity extends AppCompatActivity {

    TextView number;
    ImageView apple;
    ImageView rottenApple;
    ImageView buttonOne;
    ImageView buttonTwo;
    ImageView buttonThree;
    Droppie droppie = new Droppie(this);
    boolean isRottenApple;

    //BODY OF DROPPY
    ImageView droppy;
    ImageView eyebrows;
    ImageView eyes;
    ImageView mouth;
    ImageView collisionbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        number = findViewById(R.id.number);
        apple = findViewById(R.id.apple);
        rottenApple = findViewById(R.id.rotten_apple);
        buttonOne = findViewById(R.id.button_1);
        buttonTwo = findViewById(R.id.button_2);
        buttonThree = findViewById(R.id.button_3);

        droppy = findViewById(R.id.droppy_base);
        eyebrows = findViewById(R.id.droppy_eyebrows);
        eyes = findViewById(R.id.droppy_eyes);
        mouth = findViewById(R.id.droppy_mouth);
        collisionbox = findViewById(R.id.collisionbox);

    }

    public void goBack(View v) {
        finish();
    }

    public void showApple(View view) {
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

    public void feedApple(View view) {
        int num = Integer.parseInt((String) number.getText());
        if (isRottenApple) {
            if (num > 0){
                number.setText(num - 1);
            }
            rottenApple.setVisibility(View.INVISIBLE);
            droppie.changeEmotion(Emotion.Sadness);
        } else {
            number.setText(num + 1);
            droppie.changeEmotion(Emotion.Happiness);
        }
    }

    private boolean isRottenApple() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        return randomNum % 2 != 0;
    }
}
