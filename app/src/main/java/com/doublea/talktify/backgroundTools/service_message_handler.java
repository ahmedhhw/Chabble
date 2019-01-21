package com.doublea.talktify.backgroundTools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class service_message_handler extends Service {
    public service_message_handler() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
