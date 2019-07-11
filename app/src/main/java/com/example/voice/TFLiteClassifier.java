package com.example.voice;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.MainActivity;
import com.example.screens.JokeChallenge;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class TFLiteClassifier {

    private Interpreter tflite;


    public TFLiteClassifier(Activity activity) {
        this.activity=activity;
    }

    private Activity activity;

    // Recognizing
    public int recognize(float[][] mels,String modelFile,int outputSize) {
        try {
            tflite = new Interpreter(loadModelFile(activity, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        float[][][] inp=new float[1][][];
        inp[0]=mels;
        float[][] out=new float[1][outputSize];
        tflite.run(inp,out);
        return getMaxIndex(out[0]);
    }


    // Recognizing Images
    public void recognizeImage(Bitmap scaledBMP) {
        System.out.println("START IMAGE RECOGNITION");
        String modelFile="fer2013_model.lite";
        try {
            tflite = new Interpreter(loadModelFile(activity, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int height=scaledBMP.getHeight();
        int width=scaledBMP.getWidth();
        float[][][][] inp=new float[1][height][width][3];
        int y=0;
        while(y<height) {
            int x=0;
            while(x<width) {
                int col=scaledBMP.getPixel(x,y);
                inp[0][y][x][0]=(float) Color.red(col)/255f;
                inp[0][y][x][1]=(float)Color.green(col)/255f;
                inp[0][y][x][2]=(float)Color.blue(col)/255f;
                x++;
            }
            y++;
        }
        //
        float[][] out=new float[1][4];
        tflite.run(inp,out);

        Droppie droppie=new Droppie(activity);
        droppie.changeEmotion(droppie.getEmotion(out[0]));

    }


    // Recognizing Images
    public void checkIfLaughing(Bitmap scaledBMP) {
        System.out.println("START IMAGE RECOGNITION");
        String modelFile="fer2013_model.lite";
        try {
            tflite = new Interpreter(loadModelFile(activity, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int height=scaledBMP.getHeight();
        int width=scaledBMP.getWidth();
        float[][][][] inp=new float[1][height][width][3];
        int y=0;
        while(y<height) {
            int x=0;
            while(x<width) {
                int col=scaledBMP.getPixel(x,y);
                inp[0][y][x][0]=(float) Color.red(col)/255f;
                inp[0][y][x][1]=(float)Color.green(col)/255f;
                inp[0][y][x][2]=(float)Color.blue(col)/255f;
                x++;
            }
            y++;
        }
        //
        float[][] out=new float[1][4];
        tflite.run(inp,out);

        int ind=getMaxIndex(out[0]);
        if(Emotion.values()[ind]==Emotion.Happiness) {

            if(MainActivity.inMinigame==true){
                MainActivity.jokeProgress -= 10;
                MainActivity.level_bar.setProgress(MainActivity.jokeProgress);
                System.out.println("!!!!!! Jokeprogress ist: " + MainActivity.jokeProgress);
            }else{
                MainActivity.points+=1;
            }

            System.out.println("MOMENTANE PUNTKE SIND: " + MainActivity.points);
            System.out.println("IN MINIGAME IST: " + MainActivity.inMinigame);

        }
    }


    private int getMaxIndex(float[]array) {
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
        return maxInd;
    }

    private Emotion getEmotion(float[]array) {
        int maxInd=getMaxIndex(array);
        return Emotion.values()[maxInd];
    }


    private String arrayToString(float[]array) {
        String str="{";
        int i=0;
        while(i<array.length) {
            str+=i+":"+(int)(array[i]*100)+"%, ";
            i++;
        }
        str=str.substring(0,str.length()-2)+"}";

        return str;
    }

    public void recognizeEmotion(ArrayList<float[][]> seconds) {
        String[]emotions={"happy","sad","angry","neutral","klopfen","husten","stille"};
        String model_name="emotionen_ENLARGED.lite";
        HashMap<Emotion,Integer> emo=new HashMap<>();
        emo.put(Emotion.Anger,0);
        emo.put(Emotion.Happiness,0);
        emo.put(Emotion.Neutral,0);
        emo.put(Emotion.Sadness,0);
        int i=0;
        while(i<seconds.size()) {
            String em = emotions[recognize(seconds.get(i),model_name,emotions.length)];
            switch (em) {
                case "happy":
                    emo.put(Emotion.Happiness, emo.get(Emotion.Happiness) + 1);
                    break;
                case "neutral":
                    emo.put(Emotion.Neutral, emo.get(Emotion.Neutral) + 1);
                    break;
                case "sad":
                    emo.put(Emotion.Sadness, emo.get(Emotion.Sadness) + 1);
                    break;
                case "angry":
                    emo.put(Emotion.Anger, emo.get(Emotion.Anger) + 1);
                    break;
            }
            i++;
        }
        System.out.println("Happiness: "+emo.get(Emotion.Happiness)+", Angry: "+emo.get(Emotion.Anger)+
                ", Neutral: "+emo.get(Emotion.Neutral)+", Sad: "+emo.get(Emotion.Sadness));
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}
