package com.example.camera_test2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.lang.annotation.Target;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressDialog mDialog;
//    进度弹窗
//    identify mDialog
    VideoView videoView;
//    ImageView btnPlayPause;
    ImageButton btnplaypause;
    String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    @Override
//    https://www.youtube.com/watch?v=aqz-KE-bpKQ big buck bunny
//    https://zhidao.baidu.com/question/1732880394851155707.html useful file

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView)findViewById(R.id.videoView); //get the ID from activity_main.xml
        btnplaypause = (ImageButton)findViewById(R.id.btn_play_pause);
        btnplaypause.setOnClickListener(this); //创建点击事件
//        only need to setup this
    }

    @Override
    public void onClick(View v) //点击事件
    {
        mDialog = new ProgressDialog(MainActivity.this);
//      error only use this, the call is not inside the context
        mDialog.setMessage("Please wait you dumb bitch"); //内容
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show(); //全部显示出来

        try //对代码进行包裹
        {
//            可能会出现异常的语句
            if(!videoView.isPlaying())
            {
                Uri uri = Uri.parse(videoURL); //调web浏览器
                videoView.setVideoURI(uri); //播放视频
//              监听事件，网络流媒体播放结束监听

//              处理播放结束后的操作
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        btnplaypause.setImageResource(R.drawable.ic_play); //现实图片
                    }
                });
            }
            else
            {
                videoView.pause();;
                btnplaypause.setImageResource(R.drawable.ic_play);
                btnplaypause.setImageResource(R.drawable.ic_pause);
            }

        }

        catch (Exception ex)
        {
            //出现异常
        }
        videoView.requestFocus();
//        设置是否获得焦点。若有requestFocus()被调用时，后者优先处理
//        用于处理准备结束后的操作
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mDialog.dismiss();
                mp.setLooping(true);
                videoView.start();
            }
        });

    }
}
