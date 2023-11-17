package com.example.speedometerapp.model;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class MyTextToSpeech {
    private TextToSpeech tts;
    TextToSpeech.OnInitListener initListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status==TextToSpeech.SUCCESS){
                tts.setLanguage(Locale.US);
            }
        }
    };

    public MyTextToSpeech(Context context){
        tts = new TextToSpeech(context, initListener);
    }

    public void speak(String message){
        tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
    }
}
