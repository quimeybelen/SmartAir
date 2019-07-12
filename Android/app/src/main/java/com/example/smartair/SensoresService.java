package com.example.smartair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;


public class SensoresService extends Service implements SensorEventListener {

    /***Constantes***/
    private static final int PROXIMITY = 1;
    private static final int NO_PROXIMITY = 0;
    private static final int SHAKED = 1;
    private static final int LIGHT = 1;
    private static final int DO_ACTION_LIGHT = 0;
    private static final int NO_LIGHT = 0;
    private static final int MIN_SHAKE = 18;
    private static final float SHAKE_FLOAT = 0.9f;
    private static final String NO_ACCELEROMETER_SENSOR_MSG = "Acelerometro no disponible";
    private static final String NO_LIGHT_SENSOR_MSG = "Sensor de luz no disponible"; 
    private static final String NO_PROXIMITY_SENSOR_MSG = "Sensor de proximidad no disponible";           

    //Managers
    private SensorManager proximitySensorManager;
    private SensorManager shakeSensorManager;
    private SensorManager lightSensorManager;

    //Sensor de luz
    private static final int SENSOR_SENSITIVITY_LIGHT =1;

    //Shake
    private float acelVal = SensorManager.GRAVITY_EARTH;;
    private float acelLast = SensorManager.GRAVITY_EARTH;;
    private float shake = 0.00f;

    //Sensor de proximidad
    private static final int SENSOR_SENSITIVITY_PROXIMITY = 0;

    /***Variables de chequeo de estados***/
    public static int proxCheck = 0;
    public static int luzCheck = 0;
    public static int shakeCheck = 0;
    public static int accionLuz = 0;
    public static int accionProx = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Sensor de proximidad
        proximitySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Sensor shake
        shakeSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Sensor shake
        lightSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Sensor proximitySensor = proximitySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor shakeSensor = shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor lightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (shakeSensor != null) {
            shakeSensorManager.registerListener(this, shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, NO_ACCELEROMETER_SENSOR_MSG, Toast.LENGTH_LONG).show();
        }

        if (proximitySensor != null) {
            proximitySensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, NO_PROXIMITY_SENSOR_MSG, Toast.LENGTH_LONG).show();
        }

        if (lightSensor != null) {
            lightSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, NO_LIGHT_SENSOR_MSG, Toast.LENGTH_LONG).show();
        }

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Sensor de proximidad
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY_PROXIMITY && event.values[0] <= SENSOR_SENSITIVITY_PROXIMITY)
            {
                proxCheck = PROXIMITY;
            }
            else
            {
                proxCheck = NO_PROXIMITY;
            }
        }

        //Shake
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acelLast=acelVal;
            acelVal= (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal-acelLast;
            shake = shake * SHAKE_FLOAT + delta;

            if(shake > MIN_SHAKE) {
                shakeCheck = SHAKED;
            }
        }

        //Sensor de luz
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] >= -SENSOR_SENSITIVITY_LIGHT && event.values[0] <= SENSOR_SENSITIVITY_LIGHT) {
                luzCheck = LIGHT;
            }
            else
            {
                luzCheck = NO_LIGHT;
                accionLuz = DO_ACTION_LIGHT;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
