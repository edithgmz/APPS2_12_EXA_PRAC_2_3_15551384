package edith.example.capturardatos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class SensoresActivity extends AppCompatActivity implements SensorEventListener, Button.OnClickListener {
    private final int TEMP = 0;
    private final int HUMI = 1;
    //los datos de los sensores se guardan en las sig. variables
    private float porcentHumidity;
    private float termTemperature;
    private float[][] datosSensores = new float[4][1];
    private TextView txtVwDatosSensores, txtVwDatosCaptura;
    private SensorManager sensorManager;
    private Sensor senTemperature, senHumidity; //temperatura, humedad
    private Calendar diaTomado = null;
    private String horaCaptura = "";
    MySQLAPIconnection mySQLAPIconnection; //API

    //Ciclo de vida
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        txtVwDatosSensores = findViewById(R.id.txtVwDatosSensores);
        txtVwDatosCaptura = findViewById(R.id.txtVwDatosCaptura);
        Button btnDatosCaptura = findViewById(R.id.btnDatosCaptura);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            senHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            senTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        btnDatosCaptura.setOnClickListener(this);

        new Thread(new Runnable() { // se usa un hilo con while(true) para actuar como un listener de datoDelDia
            @Override
            public void run() {
                int nDatos=0;
                boolean datoDelDia = false;
                while(true){//no ortodoxo, me duele verlo :'(
                    if (!datoDelDia){//si no tiene dato del dia lo toma
                        nDatos++;
                        datoDelDia = nDatos == 31; //si se han mandado 31 datos no se mete al condicional
                        diaTomado = Calendar.getInstance(); //define que el dia que tomo el dato es hoy
                        int hora = diaTomado.get(Calendar.HOUR_OF_DAY);
                        int minutos = diaTomado.get(Calendar.MINUTE);
                        int segundos = diaTomado.get(Calendar.SECOND);
                        int milis = diaTomado.get(Calendar.MILLISECOND);
                        int diaMes = diaTomado.get(Calendar.DAY_OF_MONTH);
                        int mes = diaTomado.get(Calendar.MONTH);
                        int year =diaTomado.get(Calendar.YEAR); //en inglés para no usar ñ o ni
                        obtenerHora(hora, minutos, segundos, milis);
                        obtenerFecha(diaMes,mes,year);

                        mandarDatos(); //API
                        try {
                            Thread.sleep(950);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //if()
                    }
                }
            }
        }).start();
    }

    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); //es 'this' porque la clase es un listener
    }

    @Override protected void onResume() {
        super.onResume();
        //Registrar listener por sensor
        if (senHumidity != null) { sensorManager.registerListener(this, senHumidity, SensorManager.SENSOR_DELAY_NORMAL); }
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
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {// % (humedad relativa ambiental)
            porcentHumidity = event.values[0];
        }

        datosSensores[TEMP][0] = termTemperature;
        datosSensores[HUMI][0] = porcentHumidity;

        sCade += "Temperatura: " + datosSensores[TEMP][0] + " °C\n\n";
        sCade += "Humedad: " + datosSensores[HUMI][0] + "%";
        txtVwDatosSensores.setText(sCade);
    }

    String obtenerHora(int hora, int minutos, int segundos, int milis){
        //Definir la hora de la captura
        if (hora <= 9 && minutos <= 9 && segundos <= 9 && milis <= 9) { //01:01:01:01
            horaCaptura = "0" + hora + ":0" + minutos + ":0" + segundos + ":0" + milis;
        } else if (hora > 9 && minutos <= 9 && segundos <= 9 && milis <= 9) { //10:01:01:01
            horaCaptura = hora + ":0" + minutos + ":0" + segundos + ":0" + milis;
        } else if (hora > 9 && minutos <= 9 && segundos <= 9) { //10:01:01:10
            horaCaptura = hora + ":0" + minutos + ":0" + segundos + ":" + milis;
        } else if (hora > 9 && minutos <= 9 && milis <= 9) { //10:01:10:01
            horaCaptura = hora + ":0" + minutos + ":" + segundos + ":0" + milis;
        } else if (hora > 9 && segundos <= 9 && milis <= 9) { //10:10:01:01
            horaCaptura = hora + ":" + minutos + ":0" + segundos + ":0" + milis;
        } else if (hora <= 9 && segundos <= 9 && milis <= 9) { //01:10:01:01
            horaCaptura = "0" + hora + ":" + minutos + ":0" + segundos + ":0" + milis;
        } else if (hora <= 9 && minutos <= 9 && milis <= 9) { //01:01:10:01
            horaCaptura = "0" + hora + ":0" + minutos + ":" + segundos + ":0" + milis;
        } else if (hora <= 9 && minutos <= 9 && segundos <= 9) { //01:01:01:10
            horaCaptura = "0" + hora + ":0" + minutos + ":0" + segundos + ":" + milis;
        } else if (hora <= 9 && milis <= 9) { //01:10:10:01
            horaCaptura = "0" + hora + ":" + minutos + ":" + segundos + ":0" + milis;
        } else if (hora <= 9 && segundos <= 9) { //01:10:01:10
            horaCaptura = "0" + hora + ":" + minutos + ":0" + segundos + ":" + milis;
        } else if (hora <= 9 && minutos <= 9) { //01:01:10:10
            horaCaptura = "0" + hora + ":0" + minutos + ":" + segundos + ":" + milis;
        } else if (hora <= 9) { //01:10:10:10
            horaCaptura = "0" + hora + ":" + minutos + ":" + segundos + ":" + milis;
        } else if (minutos <= 9) { //10:01:10:10
            horaCaptura = hora + ":0" + minutos + ":" + segundos + ":" + milis;
        } else if (segundos <= 9) { //10:10:01:10
            horaCaptura = hora + ":" + minutos + ":0" + segundos + ":" + milis;
        } else if (milis <= 9) { //10:10:10:01
            horaCaptura = hora + ":" + minutos + ":" + segundos + ":0" + milis;
        } else { //10:10:10:10
            horaCaptura = hora + ":" + minutos + ":" + segundos + ":" + milis;
        }

        return horaCaptura;
    }

    String obtenerFecha(int diaMes, int mes, int year){
        String sDia=diaMes+"",sMes=mes+"", sYear=year+"";
        if (diaMes <=9 )
            sDia="0" + sDia;
        if (mes <= 9)
            sMes = "0" + sMes;
        if (year <= 9)
            sYear = "0" +sYear;

        return sDia+"-"+sMes+"-"+sYear;
    }

    void mandarDatos(){
        //API
        mySQLAPIconnection = new MySQLAPIconnection();
        mySQLAPIconnection.execute();
    }

    @Override public void onClick(View v) {
        if(v.getId() == R.id.btnDatosCaptura){
            diaTomado = Calendar.getInstance();
            int hora = diaTomado.get(Calendar.HOUR_OF_DAY);
            int minutos = diaTomado.get(Calendar.MINUTE);
            int segundos = diaTomado.get(Calendar.SECOND);
            int milis = diaTomado.get(Calendar.MILLISECOND);
            int diaMes = diaTomado.get(Calendar.DAY_OF_MONTH);
            int mes = diaTomado.get(Calendar.MONTH);
            int year =diaTomado.get(Calendar.YEAR); //en inglés para no usar ñ o ni

            mandarDatos();

            String sDatos = txtVwDatosCaptura.getText().toString() + "\n\n";
            sDatos += "Temperatura: " + datosSensores[TEMP][0] + " °C\n" + "Humedad: " + datosSensores[HUMI][0] + "%\n" + "Hora Captura: " +
                      obtenerHora(hora, minutos, segundos, milis)+", " + obtenerFecha(diaMes,mes,year);
            txtVwDatosCaptura.setText(sDatos);
        }
    }

    class MySQLAPIconnection extends AsyncTask<Void, Void, String> {
        private final String url = "http://192.168.1.83:3000/Tasks";

        @Override protected String doInBackground(Void... voids) {
            String sResu = null;

            try {
                URL ruta = new URL(url);
                HttpURLConnection httpCon = (HttpURLConnection) ruta.openConnection();
                //Definir tipo de solicitud
                httpCon.setRequestMethod("POST");
                httpCon.setDoInput(true); //Entrada
                httpCon.setDoOutput(true); //Resultado
                httpCon.setRequestProperty("Content-Type", "application/json;charset=utf-8"); //Tipo
                httpCon.connect();
                //Objeto con los datos a enviar
                JSONObject jsonObject = new JSONObject();
                if(termTemperature != 0){
                    jsonObject.put("Temperatura", termTemperature);
                    jsonObject.put("Humedad", porcentHumidity);
                    jsonObject.put("Hora", horaCaptura);
                }
                //Enviar datos
                DataOutputStream escribir = new DataOutputStream(httpCon.getOutputStream());
                escribir.write(jsonObject.toString().getBytes());
                escribir.flush();
                escribir.close();
                //Leer resultado
                InputStream inputStream = httpCon.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder resu = new StringBuilder();
                while ((sResu = br.readLine()) != null) {
                    resu.append(sResu);
                    resu.append("\n");
                }
                br.close();
                sResu = resu.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return sResu;
        }
    }

}
