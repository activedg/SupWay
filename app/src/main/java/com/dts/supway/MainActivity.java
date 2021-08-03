package com.dts.supway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import static android.speech.tts.TextToSpeech.ERROR;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    private TextToSpeech tts;
    private Handler handler;
    private SpeechRecognizer mRecognizer; // #2 Method
    private boolean startVoice = false;
    private TextView tvResult;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR){
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate(0.8f);
                    tts.setPitch(0.8f);
                }
            }
        });

        handler = new Handler();
        tvResult = findViewById(R.id.tv_result);
        checkBox = findViewById(R.id.cb_start);
        checkBox.setOnClickListener(v -> setStartOption());

        SharedPreferences sf = getSharedPreferences("sOption", MODE_PRIVATE);
        startVoice = sf.getBoolean("startVoice", false);
        checkBox.setChecked(startVoice);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (startVoice) { doVoice();}

        findViewById(R.id.sttButton).setOnClickListener(v -> doVoice());
        findViewById(R.id.sttImage).setOnClickListener(v -> extractVoice());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setStartOption(){
        if (checkBox.isChecked()){
            startVoice = true;
        } else {
            startVoice = false;
        }
    }

    private void doVoice(){
        VoiceTask voiceTask = new VoiceTask();
        voiceTask.execute();

    }

    private class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;

        @Override
        protected String doInBackground(String... strings) {
            try{
                getVoice();
            } catch(Exception e) {
            }
            return str;
        }
    }


    private void getVoice(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        startActivityForResult(intent, 2);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak("현재 위치한 역 이름을 말씀하세요.", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 500);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);

            if (str.contains("사당")){
                Toast.makeText(this, "사당역", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // #2 Method
    private void extractVoice(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            tts.speak("현재 위치한 역 이름을 말씀하세요.", TextToSpeech.QUEUE_FLUSH, null);
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {
            tts.speak("다시 말씀해주세요.", TextToSpeech.QUEUE_FLUSH, null);
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> results = bundle.getStringArrayList(key);
            String rs = results.get(0);
            Toast.makeText(getApplicationContext(), rs, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("sOption", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("startVoice", startVoice);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}