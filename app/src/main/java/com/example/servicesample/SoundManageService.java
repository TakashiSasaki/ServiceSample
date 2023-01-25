package com.example.servicesample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SoundManageService extends Service {
    public SoundManageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}