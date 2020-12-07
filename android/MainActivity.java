package com.example.pointer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor gyroSensor;
    float x, z; // nilai sensor
    String ip = "192.168.2.28"; // alamat server (IP local)
    String urlLeftclk = String.format("http://%s:3001/api/leftclk", ip); // path untuk request klik kiri
    String urlRightclk = String.format("http://%s:3001/api/Rightclk", ip); // path untuk request klik kanan
    String urlMove = String.format("http://%s:3001/api/move", ip); // path untuk request gerakkan mouse


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        final Button button = findViewById(R.id.move_pointer);
        MyOnTouchListener l = new MyOnTouchListener();
        button.setOnTouchListener(l);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor gyroSensor, int acc) {
        }
        public void onSensorChanged(SensorEvent event) {
            // ambil hasil pembacaan sensor untuk gyro sumbu x dan sumbu z (kec. sudut)
            x = (float) (event.values[0]);
            z = (float) (event.values[2]);
        }
    };

    public void leftClick(View view) {
        // fungsi request click kiri
        AndroidNetworking.post(urlLeftclk)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


    public void rightClick(View view) {
        // fungsi request click kanan
        AndroidNetworking.post(urlRightclk)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    public void movePointer(int x_offset, int y_offset) {
        // fungsi request gerakkan pointer

        // susun body request (JSON)
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("x", x_offset);
            jsonObject.put("y", y_offset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(urlMove)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


    // listen apabila tombol gerak ditekan atau diangkat
    Handler exHandler = new Handler();
    public void schedulePeriodicMethod() {
        exHandler.post(execution);
    }
    public void stopPeriodicMethod() {
        exHandler.removeCallbacks(execution);
    }
    int broadcastmode = 0;
    class MyOnTouchListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN: // kasus tombol ditekan
                    broadcastmode = 1;
                    schedulePeriodicMethod();
                    break;

                case MotionEvent.ACTION_UP: // kasus tombol diangkat
                    broadcastmode = 0;
                    stopPeriodicMethod();
                    break;
            }
            return true;
        }
    }

    // jika tombol gerak sedang ditekan, maka dipanggil
    private Runnable execution = new Runnable() {
        @Override
        public void run() {
            if (broadcastmode ==0) return;
            int sensitivity = 150; // konstanta untuk kuantisasi, semakin besar maka semakin besar lompatan pointer
            int x_value = (int) (-1*z*sensitivity); // dikali -1 untuk efek mirror
            int y_value = (int) (-1*x*sensitivity);
            movePointer(x_value, y_value); // panggil fungsi untuk mengirim request gerak

            exHandler.postDelayed(execution, 100); // tunggu 100 milisekon sebelum rekursi
        }
    };
}