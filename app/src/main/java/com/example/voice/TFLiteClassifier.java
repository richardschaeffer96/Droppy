package com.example.voice;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.widget.Button;
import android.widget.TextView;

import com.example.deinvirtuellerfreund.Droppie;
import com.example.deinvirtuellerfreund.Emotion;
import com.example.deinvirtuellerfreund.R;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteClassifier {

    private Interpreter tflite;

    private String[]emotions={"angry","happy","neutral","sad"};


    public TFLiteClassifier(Activity activity) {
        this.activity=activity;
    }

    private Activity activity;


    public void recognize(float[][] mels) {

        String modelFile="model_emodb.lite";
        try {
            tflite = new Interpreter(loadModelFile(activity, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        float[][][] inp=new float[1][][];
        inp[0]=mels;
        float[][] out=new float[1][4];
        tflite.run(inp,out);

        for (int i=0; i<out.length;i++){
            for(int j=0; j<out[i].length;j++){

            }
        }
        Droppie droppie=new Droppie(activity);
        droppie.changeEmotion(getEmotion(out[0]));
    }

    private Emotion getEmotion(float[]array) {
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
