package com.example.servicesample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }//onCreate

    public void onPlayButtonClick(View view) {
        //サービスクラスにインテントを飛ばして再生を促す
        Intent intent
                = new Intent(MainActivity.this,
                SoundManageService.class);
        startService(intent);
        findViewById(R.id.btPlay).setEnabled(false);
        findViewById(R.id.btStop).setEnabled(true);
    }//onPlayButtonClick

    public void onStopButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this,
                SoundManageService.class);
        stopService(intent);
        findViewById(R.id.btPlay).setEnabled(true);
        findViewById(R.id.btStop).setEnabled(false);
    }//onStopButtonClick

}//MainActivity