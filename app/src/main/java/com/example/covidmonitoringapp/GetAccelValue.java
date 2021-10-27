package com.example.covidmonitoringapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class GetAccelValue extends Service implements SensorEventListener {

    private SensorManager accelManage;
    private Sensor senseAccel;
    private static final String TAG = "AccelService";
    public ArrayList<Float> zValues;

    @Override
    public void onCreate() {
        super.onCreate();
        zValues = new ArrayList<Float>();
        Toast.makeText(getApplicationContext(), "service created", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = sensorEvent.values[2];
            Log.d(TAG, "accel z value: " + z);
            zValues.add(z);
//            calculateRPeak(z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public GetAccelValue() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void calculateRPeak(float zValue) {

    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
        accelManage.unregisterListener(this,senseAccel);
        Log.d("z values",zValues.toString());
        // call peak detection algorithm
        AccelPeakDetectorThread accelPeakDetectorThread = new AccelPeakDetectorThread(getApplicationContext(),zValues);
        accelPeakDetectorThread.start();
        Toast.makeText(getApplicationContext(), "service destroyed", Toast.LENGTH_LONG).show();
    }
}

class AccelPeakDetectorThread extends Thread {
    Context context;
    ArrayList<Float> accelerometerData;

    AccelPeakDetectorThread(Context context,ArrayList<Float> accelerometerData) {
        this.context = context;
        this.accelerometerData = accelerometerData;
    }

    @Override
    public void run() {
        ArrayList<Float> newAccelerometerData = new ArrayList<Float>();
        for(int i=0;i<accelerometerData.size();i+=10)
        {
            newAccelerometerData.add(accelerometerData.get(i));
        }
        int totalPeaks = calculatePeakValues(newAccelerometerData);
        double respiratoryRate = (4*totalPeaks)/3;
        sendMessageToActivity(respiratoryRate);

    }

    public int calculatePeakValues(ArrayList<Float> timeSeriesData)
    {
        int peaks = 0, totalPeaks = 0;
        int timeSeriesDataSize = timeSeriesData.size();
        for(int i=1; i<timeSeriesDataSize-1; i++) // iterating from second to last-1 element
        {
            if(timeSeriesData.get(i-1) < timeSeriesData.get(i))
            {
                if(timeSeriesData.get(i + 1) < timeSeriesData.get(i))
                {
                    peaks+=1;
                }
            } else {
                if(timeSeriesData.get(i+1) > timeSeriesData.get(i))
                {
                    peaks+=1;
                }
            }
        }
        totalPeaks = peaks / 2;
//        Toast.makeText(MainActivity.getApplicationContext(), String.valueOf(totalPeaks), Toast.LENGTH_SHORT).show();
        Log.d("totalPeaks",String.valueOf(totalPeaks));
        return totalPeaks;
    }

    void sendMessageToActivity(double respiratoryRate) {
        Intent intent = new Intent("respiratoryRateUpdater");
        Log.d("respiratory rate value",String.valueOf(respiratoryRate));
        intent.putExtra("RespiratoryRate", respiratoryRate);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}