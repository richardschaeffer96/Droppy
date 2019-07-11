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
                    case "cheer":
                        activity.setContentView(R.layout.minigame_cheer);
                        new CheerScreen(activity);
                        break;
                    case "food":
                        activity.setContentView(R.layout.food_game);
                        new FoodScreen(activity, 0);
                        break;
                    case "animal":
                        activity.setContentView(R.layout.minigame_animals);
                        new AnimalSoundScreen(activity);
                    case "main":
                        activity.setContentView(R.layout.activity_main);
                        activity.recreate();
                        break;
                }
            }
        });
    }
}
