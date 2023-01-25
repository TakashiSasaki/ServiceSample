package com.example.servicesample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;


// 音の再生だけならUIの操作は必要ないので、
//UIスレッドとは独立しtサービス単体で再生できる。
//通知もUIスレッドとは独立して表示させることができる。
//マニフェストで android:process 属性による指定をしなければ
//サービスはアプリと同じプロセスで実行される。
//サービスを別のプロセスとして分離することもできる。

public class SoundManageService extends Service {

    private MediaPlayer _player;

    //public SoundManageService() {
    //}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _player = new MediaPlayer();
    }//onCreate

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mediaFileUriStr =
                "android.resource://" + getPackageName() + "/"
                        + R.raw.mountain_stream;
        try {
            _player.setDataSource(mediaFileUriStr);
            _player.setOnPreparedListener
                    (new PlayerPreparedListener());
            _player.setOnCompletionListener
                    (new PlayerCompletionListener());
            _player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ServiceSample", "メディアプレーヤー準備時の例外発生", e);
        }//try
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }//onStartCommand

    @Override
    public void onDestroy() {
        if (_player.isPlaying()) {
            _player.stop();
        }//if
        _player.release();
        _player = null;
        super.onDestroy();
    }//onDestroy

    //このメソッドは static にできる
    //なぜか考えてみよう。
    private class PlayerPreparedListener
            implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }//onPrepared
    }//PlayerPreparedListener class

    private class PlayerCompletionListener
            implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopSelf();
        }//onCompletion
    }//PlayerCompletionListener

}//SoundManageService class

