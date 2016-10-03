package com.example.hackupc.usingservices;

/**
 * Created by ericgarciaribera on 20/02/16.
 */

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class HelloService extends Service implements AccelerometerListener{

    private static final String TAG = "HelloService";

    private boolean isRunning  = false;

    public MediaPlayer mp;

    double angle0 = 0;
    boolean started = true;
    int detect = 0;
    int delay = 0;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;

        Toast.makeText(getBaseContext(), "Service started, listening...",
                Toast.LENGTH_SHORT).show();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }

        mp = MediaPlayer.create(this, R.raw.sound);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true && isRunning) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.i(TAG, "Service running");
                    }
                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
        AccelerometerManager.stopListening();

        mp.stop();
        detect =  delay = 0;
        Toast.makeText(getBaseContext(), "Service stoped!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        ++delay;
        //Log.d(TAG, "onSensorEvent(" + x + ", " + y + ", " + z + ")");
        // If some values are exactly zero, then likely the sensor is not powered up yet.
        // ignore these events to avoid false horizontal positives.
        if (x == 0.0 || y == 0.0 || z == 0.0) return;
        // magnitude of the acceleration vector projected onto XY plane
        final double xy = Math.sqrt(x*x + y*y);
        // compute the vertical angle
        double angle = Math.atan2(xy, z);
        // convert to degrees
        angle = angle * 180.0 / Math.PI;
        final int orientation = (angle >  50.0 ? 1 : 2);
        //Log.d(TAG, "angle: " + angle + " orientation: " + orientation);
        if (started) {
            angle0 = angle;
            started = false;
        }
        if ((angle0) < (angle-5) || angle0 > (angle+5) ) {
            //mp.start();
            Log.d(TAG, "angle: " + angle + " orientation: " + orientation);
            ++detect;
            delay = 0;
        }
        if (detect >= 10) {
            mp.start();
        }
        if (delay >= 100) detect = delay = 0;
    }


    @Override
    public void onShake(float force) {
        Log.i(TAG, "SHAKE DETECTED!!!!");

        //mp.start();
    }
}
