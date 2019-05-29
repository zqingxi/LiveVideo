package com.lexiang.livevideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TXLivePlayer mLivePlayer;
    TXCloudVideoView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String sdkVersion = TXLiveBase.getSDKVersionStr();
        Log.d("liteavsdk", "liteeav sdk version is : " + sdkVersion);
        //mPlayerView 即 step1 中添加的界面 view
        mView = (TXCloudVideoView) findViewById(R.id.video_view);
        //创建 player 对象
        mLivePlayer = new TXLivePlayer(this);

        //关联 player 对象与界面 view
        mLivePlayer.setPlayerView(mView);
        // 设置填充模式
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // 设置画面渲染方向
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        //流畅模式
        mPlayConfig.setAutoAdjustCacheTime(false);
        mPlayConfig.setMinAutoAdjustCacheTime(5);
        mPlayConfig.setMaxAutoAdjustCacheTime(10);
        mLivePlayer.enableHardwareDecode(false);
        mLivePlayer.setConfig(mPlayConfig);
        String flvUrl = "http://video.oaksh.cn/live/test.flv";

        mLivePlayer.setPlayListener(new ITXLivePlayListener(){
            @Override
            public void onPlayEvent(int event, Bundle param) {
                if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
                    int width = param.getInt(TXLiveConstants.EVT_PARAM1, 0);
                    int height = param.getInt(TXLiveConstants.EVT_PARAM2, 0);
                    Log.d("onPlayEvent", "width : " + width + " ; height : " + height);
                    if (width != 0 && height != 0) {
                        byte[] buf = new byte[width * height * 3 / 2];
                        Log.d("get buf", "buf.length : " + buf.length);
                        mLivePlayer.addVideoRawData(buf);
                    }
                }
            }
            @Override
            public void onNetStatus(Bundle var1) {

            }
        });


        TXLivePlayer.ITXVideoRawDataListener rawDataListener = new TXLivePlayer.ITXVideoRawDataListener() {
            @Override
            public void onVideoRawDataAvailable(byte[] buf, int width, int height, int timestamp) {
                Log.d("TAG", "onVideoRawDataAvailable : "  + Arrays.toString(buf) +
                        "; width = " + width + "; height = " + height + "; timestamp = " + timestamp);
                mLivePlayer.addVideoRawData(buf);
            }
        };
        mLivePlayer.setVideoRawDataListener(rawDataListener);

        // 推荐FLV
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mView.onDestroy();
    }
}
