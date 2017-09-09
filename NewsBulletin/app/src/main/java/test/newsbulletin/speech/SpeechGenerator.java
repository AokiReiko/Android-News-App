package test.newsbulletin.voice;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.setting.TtsSettings;
import com.iflytek.sunflower.FlowerCollector;

import test.newsbulletin.R;

/**
 * Created by pushi on 2017/9/9.
 */

public class SpeechGenerator {

    public SpeechGenerator(String _str, Activity _activity)
    {
        activity = _activity;
        str = _str;
        mTts = SpeechSynthesizer.createSynthesizer(activity, mTtsInitListener);

        // 云端发音人名称列表
        Resources res = activity.getResources();
        mCloudVoicersEntries = res.getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = res.getStringArray(R.array.voicer_cloud_values);

        mSharedPreferences = activity.getSharedPreferences(TtsSettings.PREFER_NAME, 0);
        mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

    }
    public boolean start() // 是否合成成功
    {
        FlowerCollector.onEvent(activity, "tts_play");
        setParam();
        int code = mTts.startSpeaking(str, mTtsListener);
        if (code != ErrorCode.SUCCESS)
            return false;
        return true;
    }
    public void end()
    {
        mTts.stopSpeaking();
    }
    public void pause()
    {
        mTts.pauseSpeaking();
    }
    public void resume()
    {
        mTts.resumeSpeaking();
    }

    String str;
    Activity activity;
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";

    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    // 云端/本地单选按钮
    private RadioGroup mRadioGroup;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private Toast mToast;
    private SharedPreferences mSharedPreferences;

    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {

            if (code != ErrorCode.SUCCESS) {

            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private SynthesizerListener mTtsListener = new SynthesizerListener(){
        @Override
        public void onSpeakBegin() {}

        @Override
        public void onSpeakPaused() {}

        @Override
        public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {}

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}

        @Override
        public void onCompleted(SpeechError error) {}

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
}
