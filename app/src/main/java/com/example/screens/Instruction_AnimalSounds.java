package com.example.screens;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.example.deinvirtuellerfreund.R;

public class Instruction_AnimalSounds {

    public Instruction_AnimalSounds(final Activity activity) {
        System.out.println("INSTRUCTION ANIMAL SOUNDS");
        this.activity=activity;
        Button startDroppie=activity.findViewById(R.id.Button_Start_Animal_Sounds);
        startDroppie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimalSounds(view);
            }
        });

    }

    private Activity activity;


    private void startAnimalSounds(View v) {
        System.out.println("PUSH BUTTON START ANIMAL SOUNDS");
        activity.setContentView(R.layout.minigame_animals);
        new AnimalSoundScreen(activity);


    }


}
