package com.example.voice;

import com.example.voice.mfcc.MFCC;

import java.util.ArrayList;
import java.util.Arrays;

public class Preprocessor {


    public ArrayList<float[][]> cutAndPreprocess(short[]signal, int n_mfcc) {
        ArrayList<float[][]>mels=new ArrayList<>();
        float sr=16000;
        float mean_signal_length=16000; // 16kHz*1sec=16kHz
        while(true) {
            float s_len=signal.length;
            if (s_len < mean_signal_length) {
                float pad_len = mean_signal_length - s_len;
                float pad_rem = pad_len % 2;
                pad_len = (int) ((double) pad_len / 2.0);
                short[] insertBefore = new short[(int) pad_len];
                short[] insertAfter = new short[(int) (pad_len + pad_rem)];
                short[]cur = combine(insertBefore, signal);
                cur = combine(signal, insertAfter);
                mels.add(preprocessAudioFile(cur,n_mfcc));
                break;
            } else {
                short[]cur=Arrays.copyOfRange(signal, (int) 0, (int) mean_signal_length);
                mels.add(preprocessAudioFile(cur,n_mfcc));
                signal=Arrays.copyOfRange(signal,(int)mean_signal_length,(int)signal.length);
            }
        }
        return mels;
    }
    // Preprocess audio file
    //
    //    Preprocess audio file
    //
    //    :param filepath: path to .wav-file
    //    :param n_mfcc: Number of mfcc features to take for each frame (e.g., for RNN)
    //    :param raw_audio: Whether to return raw audio signal (e.g., for Conv1D neural network)
    //    :param spectogram: Whether to return a melspectogram (e.g., for Conv2D neural network)
    //
    //    :return feature vector from mfcc
    //
    // DEFAULT: raw_audio=false, spectogram=false
    public float[][] preprocessAudioFile(short[]signal,int n_mfcc) {
        float sr=16000;
        // SIGNAL: Array(?)
        // # step 2
        //    signal, sr = librosa.load(path=filepath, sr=16000)
        //    s_len = len(signal)
        //
        //    # empirically calculated mean length for the given data set (2 seconds)
        //    mean_signal_length = 32000 # 16kHz * 2 seconds = 32kHz
        //
        float s_len=signal.length;
        // Empirically calculated mean length for the given data set (2 seconds)
        float mean_signal_length=16000; // 16kHz*1sec=16kHz
        //    # pad/slice the signals to have same size if lesser than required
        //    if s_len < mean_signal_length:
        //        pad_len = mean_signal_length - s_len
        //        pad_rem = pad_len % 2
        //        pad_len //= 2
        //        signal = np.pad(signal, (pad_len, pad_len + pad_rem),
        //                        'constant', constant_values=0)
        if(s_len<mean_signal_length) {
            float pad_len=mean_signal_length-s_len;
            float pad_rem=pad_len% 2;
            pad_len=(int)((double)pad_len/2.0);
            short[]insertBefore=new short[(int)pad_len];
            short[]insertAfter=new short[(int)(pad_len+pad_rem)];
            signal=combine(insertBefore,signal);
            signal=combine(signal,insertAfter);
        } else {
            //    else:
            //        pad_len = s_len - mean_signal_length
            //        pad_len //= 2
            //        signal = signal[pad_len:pad_len + mean_signal_length]
            float pad_len=s_len-mean_signal_length;
            signal=Arrays.copyOfRange(signal,(int)pad_len,(int)pad_len+(int)mean_signal_length);
        }
        //
        //    # return raw audio signal
        //    if raw_audio:
        //      return signal
        //if(raw_audio==true) {
            //return signal;
        //}
        //    # parameters for STFT
        //    n_fft = 512
        //    hop_length = 256
        //    frame_length = n_fft / sr
        //    frame_stride = hop_length / sr
        //
        //    # amount mel bands
        //    n_mels = 40
        float n_fft=512;
        float hop_length=256;
        float frame_length=n_fft/sr;
        float frame_stride=hop_length/sr;
        float n_mels=40;

        // TODO: fMin und fMax rausfinden
        MFCC mfcc=new MFCC((int)n_mfcc,(int)n_mels,0.0f,(int)n_fft,(int)hop_length,(int)sr,sr/2.0f);
        //    # compute mel scale spectogram
        //    if spectogram:
        //      S = librosa.feature.melspectrogram(y=signal,
        //                                         sr=sr,
        //                                         n_fft=n_fft,
        //                                         hop_length=hop_length,
        //                                         n_mels=n_mels)
        //      S = librosa.power_to_db(S)
        //      return S
        //if(spectogram==true) {
        //    double[][] S = mfcc.melSpectrogram(signal);
        //    S = mfcc.powerToDb(S);
        //    return S;
        //}
        //    # step 3
        //    # compute MFCCs
        //    mfccs = speechpy.feature.mfcc(signal=signal,
        //                                  sampling_frequency=sr,
        //                                  frame_length=frame_length,
        //                                  frame_stride=frame_stride,
        //                                  fft_length=n_fft,
        //                                  num_filters=n_mels,
        //                                  num_cepstral=n_mfcc)
        //    return mfccs

        float[][]res=mfcc.output(shortToDouble(signal));
        return res;
    }


    private double[]shortToDouble(short[]shortInput) {
        double[] doubleInputBuffer = new double[shortInput.length];
        // We need to play in float values between -1.0 and 1.0, so divide the
        // signed 16-bit inputs.
        for (int i = 0; i < shortInput.length; ++i) {
            doubleInputBuffer[i] = shortInput[i] / 32767.0;
        }
        return doubleInputBuffer;
    }


    // Combines two Arrays a and b
    // First array : [1, 2, 3, 4]
    // Second array : [5, 6, 7, 8]
    // Combined array : [1, 2, 3, 4, 5, 6, 7, 8]
    //
    //Read more: https://javarevisited.blogspot.com/2013/02/combine-integer-and-string-array-java-example-tutorial.html#ixzz5q68tR6o5
    //
    public static short[] combine(short[] a, short[] b){
        int length = a.length + b.length;
        short[] result = new short[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }





}
