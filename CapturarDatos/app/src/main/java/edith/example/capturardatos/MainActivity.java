package edith.example.capturardatos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //temperatura, humedad, presión, iluminación
    private SensorManager sensorManager;
    private Sensor pressure, proximity, humidity,light, temperature;
    TextView txtVwPresión;
    //los datos del sensor se guardan en las sig. variables
    float milibarsOfPressure;
    float cmOfProximity;
    float porcentHumidity;
    float luxLigth;
    float termTemperature;

    //---------------------------------------------
    //ciclo de vida
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        humidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        System.out.println("iam jea");

    }


    @Override
    protected void onPause() {
        super.onPause();
        //Be sure to unregister the sensor when the acivity pauses.
        sensorManager.unregisterListener(this); //es 'this' porque la clase Main es un listener

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register a listener for the sensor
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //-------------------------------------------------------
    //SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do something if accuaracy change
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            milibarsOfPressure = event.values[0];
            Log.i("Baro", " Pressure " + milibarsOfPressure);
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            cmOfProximity = event.values[0];
            Log.i("Prox", " Proximity " + cmOfProximity);
        }

        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {//porcentaje %
            porcentHumidity = event.values[0];
            Log.i("higro", " Humidity " + porcentHumidity);
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {//lx
            luxLigth = event.values[0];
            Log.i("vatiho", " Light " + luxLigth);
        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) { // grados centigrados°C
            termTemperature = event.values[0];
            Log.i("termo", " Temperature " + termTemperature);
        }
    }

}

