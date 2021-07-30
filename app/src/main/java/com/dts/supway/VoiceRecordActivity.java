package com.dts.supway;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecordActivity extends SpeechRecognizerActivity {
    public static String EXTRA_KEY_RESULT_ARRAY = "result";
    public static String EXTRA_KEY_MARKED = "marked"; // 첫번째 값의 신뢰도가 현저하게 높은 경우 true. 아니면 false. Boolean
    public static String EXTRA_KEY_ERROR_CODE = "error_code"; // 에러가 발생했을 때 코드값. 코드값은 SpeechRecognizerClient를 참조. Integer
    public static String EXTRA_KEY_ERROR_MESSAGE = "error_msg"; // 에러 메시지. String


    @Override
    protected void onResourcesWillInitialize() {
        super.onResourcesWillInitialize();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_voice_record);

        // activity가 표시될 때의 transition 설정
        overridePendingTransition(R.anim.com_kakao_sdk_asr_grow_height_from_top, android.R.anim.fade_in);

        // isValidResourcesMappings()을 호출하면 리소스 및 View id 설정이 안된 것이 있는지 확인 가능
        boolean resourcePassed = isValidResourceMappings();

        if(!resourcePassed){
            setResult(RESULT_CANCELED);
            finish();
            return;
        }


    }

    @Override
    protected void onRecognitionSuccess(List<String> result, boolean marked) {
        Intent intent = new Intent()
                .putStringArrayListExtra(EXTRA_KEY_RESULT_ARRAY, new ArrayList<String>(result))
                .putExtra(EXTRA_KEY_MARKED, marked);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onRecognitionFailed(int errorCode, String errorMsg) {
        Intent intent = new Intent()
                .putExtra(EXTRA_KEY_ERROR_CODE, errorCode)
                .putExtra(EXTRA_KEY_ERROR_MESSAGE, errorMsg);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();

        // activity가 사라질 때 transition 효과 지정
        overridePendingTransition(android.R.anim.fade_out, R.anim.com_kakao_sdk_asr_shrink_height_from_bottom);
    }
}
