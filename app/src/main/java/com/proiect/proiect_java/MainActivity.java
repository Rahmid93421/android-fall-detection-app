package com.proiect.proiect_java;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.math.BigDecimal;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.proiect.proiect_java.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor sensor;
    ThreadClass threadObj = new ThreadClass();

    GraphView graph;
    private LineGraphSeries<DataPoint> mSeries1;
    int pointsInd = 0;
    private static final double ALPHA = 0.1f;
    private float[] staticmAccelVal = new float[3];
    LayoutInflater inflater;
    View popupView;
    PopupWindow popupWindow;
    private boolean booleanShowPopUp = false;
    private FusedLocationProviderClient fusedLocationClient;
    double longitude;
    double latitude;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public MainActivity() {

    }

    // private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textView.setText("Hello from code!");
        threadObj.start();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, 20000);

        graph = (GraphView) findViewById(R.id.idGraphView);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadObj.state = 3;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mSeries1 = new LineGraphSeries<>();
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(1024);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        //getLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    // Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                    // .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Get location
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        }
    }

    public float[] lpf(float[] input, float[] output) {
        if (output == null) {
            return input;
        }
        for (int i = 0; i < input.length; i++) {
            output[i] = (float) (output[i] + ALPHA * (input[i] - output[i]));
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            staticmAccelVal = lpf(event.values.clone(), staticmAccelVal);
            // staticmAccelVal = event.values.clone();

            threadObj.mAccelVal = (Math.sqrt(Math.pow(staticmAccelVal[0], 2) + Math.pow(staticmAccelVal[1], 2) + Math.pow(staticmAccelVal[2], 2)));
            threadObj.mAccelVal = BigDecimal.valueOf(threadObj.mAccelVal)
                    .setScale(2, 0)
                    .doubleValue();

            if (threadObj.state == 4 && booleanShowPopUp == false) {
                inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.popup_window, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

                Button btn1 = popupView.findViewById(R.id.button2);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        threadObj.state = 0;
                        booleanShowPopUp = false;
                    }
                });
                binding.textView.setText("Fall detected!!!!");
                // setContentView(R.layout.photo_activity);

                threadObj.latitude = latitude;
                threadObj.longitude = longitude;
                booleanShowPopUp = true;

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    threadObj.latitude = location.getLatitude();
                                    threadObj.longitude = location.getLongitude();
                                } else {
                                    System.out.println("Not working..");
                                }
                            }
                        });
            } else if(threadObj.state == 5) {
                popupWindow.dismiss();
                booleanShowPopUp = false;
            } else {
                binding.textView.setText("" + threadObj.mAccelVal);
            }

            binding.button.setText("" + threadObj.state);
            binding.textView3.setText("min: " + threadObj.min);
            binding.textView2.setText("max: " + threadObj.max);

            mSeries1.appendData(new DataPoint(pointsInd++, threadObj.mAccelVal), true, 1024);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}