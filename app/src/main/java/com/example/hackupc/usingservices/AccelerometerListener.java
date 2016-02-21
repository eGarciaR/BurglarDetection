package com.example.hackupc.usingservices;

/**
 * Created by ericgarciaribera on 20/02/16.
 */
public interface AccelerometerListener {
    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);
}
