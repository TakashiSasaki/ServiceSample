package com.example.servicesample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;


// 音の再生だけならUIの操作は必要ないので、
// UIスレッドとは独立しtサービス単体で再生できる。
// 通知もUIスレッドとは独立して表示させることができる。
// マニフェストで android:process 属性による指定をしなければ
// サービスはアプリと同じプロセスで実行される。
// サービスを別のプロセスとして分離することもできる。

public class SoundManageService extends Service {

    private MediaPlayer _player;

    //通知チャネルIDを定数として持っておく。
    //自然言語非依存なのでstring.xmlに書かない。
    private static final String CHANNEL_ID
            = "soundmanagerservice_notification_channel";

    //public SoundManageService() {
    //}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        _player = new MediaPlayer();

        //通知チャネルを作成する。
        //第一引数は識別子、第二識別子はユーザーが見る文字列

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

    }//onCreate

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mediaFileUriStr =
                "android.resource://" + getPackageName() + "/"
                        + R.raw.mountain_stream;
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);
        try {
            //
            _player.setDataSource
                    (SoundManageService.this,
                            mediaFileUri);
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
            //停止ボタン押下時にここは実行されている
            //ちゃんと再生は止まる。
            _player.stop();
        }//if
        _player.release();
        _player = null;
        //super.onDestroy();
    }//onDestroy

    //このクラスはstaticにできる。
    private static class PlayerPreparedListener
            implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            mp.start();
        }//onPrepared
    }//PlayerPreparedListener class

    //PlayerCompletionListener は MediaPlayer でメディアの再生が終わったことを検知するリスナ。
    //停止ボタンを押したときに onCompletion が呼び出されるわけではない
    private class PlayerCompletionListener
            implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {


            //再生が終了したことを通知する 教科書 p316
            //ビルダーを使ってNotificationオブジェクトを作成する。
            //いわゆるデザインパターンの一つ。
            NotificationCompat.Builder b =
                    new NotificationCompat.Builder(
                            SoundManageService.this, CHANNEL_ID
                    );

            //以下もメソッドチェインで書き直すと見やすくなる
            //一時的にしか使わずスコープの小さな変数は短い名前が良い
            b.setSmallIcon(android.R.drawable.ic_dialog_info);
            b.setContentTitle(
                    getString(R.string.msg_notification_title_finish)
            );
            b.setContentTitle(
                    getString(R.string.msg_notification_text_finish)
            );
            Notification n = b.build();

            //メソッドチェインで書くと見やすい
            //androidxのパッケージで統一したいのでCompatを使う
            NotificationManagerCompat
                    .from(SoundManageService.this)
                    .notify(100, n);
            //nもb.build()で代替したほうが見やすい

            //再生を停止する
            //ここはMediaPlayer.OnCompletionListenerインターフェイスの中
            //なのでMediaPlayer.OnCompletionListener#stopSelf
            //決してSoundManagerService#stopSelfではないことに注意。
            //クラス内クラスを使うとスコープが分かりづらいね。
            stopSelf();

        }//onCompletion
    }//PlayerCompletionListener

}//SoundManageService class

