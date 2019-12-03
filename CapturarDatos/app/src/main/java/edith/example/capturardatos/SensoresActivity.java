package edith.example.capturardatos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensoresActivity extends AppCompatActivity implements SensorEventListener {
    final int TEMP = 0;
    final int HUMI = 1;
    final int PRES = 2;
    final int LIGH = 3;
    //los datos de los sensores se guardan en las sig. variables
    float milibarsOfPressure;
    float porcentHumidity;
    float luxLigth;
    float termTemperature;
    float[][] datosSensores = new float[4][1];
    private TextView tvDatosSensores;
    private SensorManager sensorManager;
    //temperatura, humedad, presión, proximidad, iluminación
    private Sensor senTemperature, senHumidity, senPressure, senLight;

    //Clico de vida
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        tvDatosSensores = findViewById(R.id.tvDatosSensores);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            senPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            senHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            senLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            senTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

    }


    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); //es 'this' porque la clase es un listener
    }

    @Override protected void onResume() {
        super.onResume();
        //Registrar listener por sensor
        if (senPressure != null) { sensorManager.registerListener(this, senPressure, SensorManager.SENSOR_DELAY_NORMAL); }
        if (senHumidity != null) { sensorManager.registerListener(this, senHumidity, SensorManager.SENSOR_DELAY_NORMAL); }
        if (senLight != null) { sensorManager.registerListener(this, senLight, SensorManager.SENSOR_DELAY_NORMAL); }
        if (senTemperature != null) { sensorManager.registerListener(this, senTemperature, SensorManager.SENSOR_DELAY_NORMAL); }
    }

    //SensorEventListener
    @Override public void onAccuracyChanged(Sensor sensor, int i) {
        //do something if accuaracy change
    }

    @Override public void onSensorChanged(SensorEvent event) {
        String sCade = "";

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) { // °C (temperatura ambiente)
            termTemperature = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {// lx (iluminación)
            luxLigth = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) { // hPa o mbar (presión de aire ambiental)
            milibarsOfPressure = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {// % (humedad relativa ambiental)
            porcentHumidity = event.values[0];
        }

        datosSensores[TEMP][0] = termTemperature;
        datosSensores[LIGH][0] = luxLigth;
        datosSensores[PRES][0] = milibarsOfPressure;
        datosSensores[HUMI][0] = porcentHumidity;

        sCade = sCade + "TEMPERATURA: " + datosSensores[TEMP][0] + " °C\n\n";
        sCade = sCade + "LUZ: " + datosSensores[LIGH][0] + " lx\n\n";
        sCade = sCade + "PRESIÓN: " + datosSensores[PRES][0] + " mbar\n\n";
        sCade = sCade + "HUMEDAD: " + datosSensores[HUMI][0] + "%";
        tvDatosSensores.setText(sCade);
    }

}
