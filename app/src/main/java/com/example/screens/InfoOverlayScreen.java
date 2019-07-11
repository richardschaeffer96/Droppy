package com.example.screens;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.R;


public class InfoOverlayScreen {

    private TextView headline;
    private TextView content;
    private Button close;
    private Activity activity;
    private String nextView;

    public InfoOverlayScreen(final Activity activity, String nextScreen, String headline, String content) {
        this.nextView = nextScreen;
        this.activity = activity;
        this.headline = this.activity.findViewById(R.id.info_headline);
        this.content = this.activity.findViewById(R.id.info_text);
        close = this.activity.findViewById(R.id.button_close);

        this.headline.setText(headline);
        this.content.setText(content);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (nextView) {
                    case "chear":
                        activity.setContentView(R.layout.minigame_cheer_dropsy);
                        new CheerDropsyScreen(activity);
                        break;
                    case "food":
                        activity.setContentView(R.layout.minigame_instruction_cheer_dropsy);
                        new FoodScreen(activity, 0);
                        break;
                    case "main":
                        activity.setContentView(R.layout.activity_main);
                        break;
                }
            }
        });
    }
}
