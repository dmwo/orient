package com.example.camera_test2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.annotation.Target;
import java.text.BreakIterator;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

    //public BreakIterator connectionStatus;
    ProgressDialog mDialog;
//    进度弹窗
    //identify mDialog
    VideoView videoView;
    ImageView btnPlayPause;
    ImageButton btnplaypause;
    String videoURL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    TextView xValue;
    TextView yValue;
    TextView zValue;
    //gyroscope data

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer, mGyro; //define a sensor

    @Override
//    https://www.youtube.com/watch?v=aqz-KE-bpKQ big buck bunny
//    https://zhidao.baidu.com/question/1732880394851155707.html useful file

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Initializing Sensor Service"); //used for debug
        videoView = (VideoView)findViewById(R.id.videoView); //get the ID from activity_main.xml
        btnplaypause = (ImageButton)findViewById(R.id.btn_play_pause);
        btnplaypause.setOnClickListener(this); //创建点击事件


        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);

//        only need to setup this
//        gyroscope data
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //define the type
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
//        boolean registerListener(SensorEventListener listener,Sensor sensor,int rateUs)
//　　    上面方法参数的意义：listener：传感器的监听器、sensor：待监听的传感器、rateUs：传感器的采样率。
        Log.d(TAG, "onCreate: Registered accelerometer listener");
//        registerListener用来注册要监听的sensor
//        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//       mVibrator.vibrate(500);
//        如让手机持续振动500毫秒
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) //当传感器精度发生变化时回调
    {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)  //当传感器感应的值发生变化时回调。
    {
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == sensor.TYPE_GYROSCOPE)
        {
            Log.d(TAG, "onSensorChanged X: "+ sensorEvent.values[0] + " Y: "+ sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
//            xValue.setText("X-axis: "+ sensorEvent.values[0] +" m/s\u00B2  ");
//            yValue.setText("Y-axis: " + sensorEvent.values[1] + " m/s\u00B2   ");
//            zValue.setText("Z-axis: " + sensorEvent.values[2] + " m/s\u00B2   ");
            xValue.setText("X-axis: "+ sensorEvent.values[0] +" rad/s ");
            yValue.setText("Y-axis: " + sensorEvent.values[1] + " rad/s  ");
            zValue.setText("Z-axis: " + sensorEvent.values[2] + " rad/s   ");

        }

    }

    @Override
    public void onClick(View v) //点击事件
    {
        mDialog = new ProgressDialog(MainActivity.this);
//      error only use this, the call is not inside the context
        mDialog.setMessage("Please wait for a little bit"); //内容
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
//                        Log.i("Mission", "onCompletion: ");
                        btnplaypause.setImageResource(R.drawable.ic_play); //现实图片 结束播放后的动作
//                        finish();
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
                btnplaypause.setImageResource(R.drawable.ic_pause);
//                btnplaypause.setImageResource(R.drawable.ic_pause);
            }
        });

    }
}