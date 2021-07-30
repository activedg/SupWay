package com.dts.supway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.security.MessageDigest;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener, SpeechRecognizeListener {
    private TextToSpeechClient ttsClient;
    private SpeechRecognizerClient sttClient;

    /* SpeechRecognizer recognizer;
    TextToSpeech tts; */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAppKeyHash();

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        // Kakao 음성인식 Api 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(this);

        findViewById(R.id.sttButton).setOnClickListener(v -> getVoice());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getVoice(){
        Intent intent = new Intent(getApplicationContext(), VoiceRecordActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(VoiceRecordActivity.EXTRA_KEY_RESULT_ARRAY);
            final StringBuilder builder = new StringBuilder();

            for (String result: results){
                builder.append(result);
                builder.append("\n");
            }

            new AlertDialog.Builder(this)
                    .setMessage(builder.toString())
                    .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                    .show();
        } else if (resultCode == RESULT_CANCELED) {
            // 음성인식 등의 오류가 아닌 Activity가 취소 된 경우
            if (data == null) return;

            int errorCode = data.getIntExtra(VoiceRecordActivity.EXTRA_KEY_ERROR_CODE, -1);
            String errorMsg = data.getStringExtra(VoiceRecordActivity.EXTRA_KEY_ERROR_MESSAGE);

            if (errorCode != -1 && !TextUtils.isEmpty(errorMsg)) {
                new AlertDialog.Builder(this)
                        .setMessage(errorMsg)
                        .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
        Toast.makeText(this, "해당 권한이 승인되었습니다.", Toast.LENGTH_LONG);
    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }

    /*
    public class VoiceTask extends AsyncTask<String, Integer, String> {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);
            if (str.contains("사당")){

            }
        }
    } */

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }

    @Override
    public void onPartialResult(String partialResult) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onAudioLevel(float audioLevel) {

    }

    @Override
    public void onFinished() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 카카오 음성 서비스 종료
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }
}