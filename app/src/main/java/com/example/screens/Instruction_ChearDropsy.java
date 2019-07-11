package com.example.screens;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.example.deinvirtuellerfreund.MainActivity;
import com.example.deinvirtuellerfreund.R;

public class Instruction_ChearDropsy {

    public Instruction_ChearDropsy(final Activity activity) {
        System.out.println("INSTRUCTION CHEER DROPSY");
        this.activity=activity;
        Button startDroppie=activity.findViewById(R.id.Button_Start_Cheer_Dropsy);
        startDroppie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCheerDropsy(view);
            }
        });

    }

    private Activity activity;


    private void startCheerDropsy(View v) {
        System.out.println("PUSH BUTTON START CHEER DROPSY");
        activity.setContentView(R.layout.activity_main);
        activity.setContentView(R.layout.minigame_cheer_dropsy);
        new CheerDropsyScreen(activity);


    }


}
