package com.example.deinvirtuellerfreund;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.screens.CheerDropsyFeedScreen;
import com.example.screens.CheerDropsyScreen;

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
    Dialog info_overlay;
    Dialog cheerScreen;
    boolean isRottenApple;
    private Activity activity;

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

        info_overlay = new Dialog(this);
        cheerScreen = new Dialog(this);

}

/*

    private void showInfo() {
        info_overlay.setContentView(R.layout.info_overlay);
        info_overlay.show();
        TextView infoHeadline = (TextView) info_overlay.findViewById(R.id.info_headline);
        TextView infoText = info_overlay.findViewById(R.id.info_text);
        infoHeadline.setText("Füttere Dropsy!");
        infoText.setText("Gib Dropsy einen Apfel um eure Freundschaft weiter zu stärken! Aber Vorsicht, zwei der" +
                "drei Äpfel sind vergammelt. Wenn du Dropsy keinen guten Apfel gibst, musst du ihn" +
                "mit deinem Lachen anstecken, um sein Vertrauen zurück zu gewinnen!");
    }
*/

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
            if (num > 0) {
                String numb = Integer.toString(num - 1);
                number.setText(numb);
            }

            info_overlay.setContentView(R.layout.info_overlay);
            info_overlay.show();
            TextView infoHeadline = (TextView) info_overlay.findViewById(R.id.info_headline);
            TextView infoText = info_overlay.findViewById(R.id.info_text);
            infoHeadline.setText("Der Apfel war vergammelt :(");
            infoText.setText("Versuche Droppys Vertrauen zurückzugewinnen, indem du ihn mit deinem Lachen ansteckst!");

//            infoText = findViewById(R.id.info_text);
//            infoHeadline = findViewById(R.id.info_headline);

   /*         rottenApple.setVisibility(View.INVISIBLE);
            droppie.changeEmotion(Emotion.Sadness);
            infoText.setText("Du hast Droppy einen vergammelten Apfel gegeben :( \n Stecke Droppy mit deinem Lachen an, um sein Vertrauen zurück zu gewinnen.");*/

        } else {
            String numb = Integer.toString(num + 1);
            number.setText(numb);
            droppie.changeEmotion(Emotion.Happiness);
            apple.setVisibility(View.INVISIBLE);
            buttonOne.setVisibility(View.VISIBLE);
            buttonTwo.setVisibility(View.VISIBLE);
            buttonThree.setVisibility(View.VISIBLE);
        }
    }

    public void close(View v) {
        info_overlay.hide();
        cheerScreen.setContentView(R.layout.minigame_cheer_dropsy);
        cheerScreen.show();
        new CheerDropsyScreen(this);

    }

    private boolean isRottenApple() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        return randomNum % 2 != 0;
    }
}
