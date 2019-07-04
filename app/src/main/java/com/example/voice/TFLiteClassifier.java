package com.example.voice;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteClassifier {

    private Interpreter tflite;


    public TFLiteClassifier(Activity activity) {
        this.activity=activity;
    }

    private Activity activity;

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



    // Recognizing
    public int recognize(float[][] mels,String modelFile) {
        try {
            tflite = new Interpreter(loadModelFile(activity, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        float[][][] inp=new float[1][][];
        inp[0]=mels;
        float[][] out=new float[1][3];
        tflite.run(inp,out);

        for (int i=0; i<out.length;i++){
            for(int j=0; j<out[i].length;j++){

            }
        }
        return getMaxIndex(out[0]);
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

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}
