package edu.fsu.cs.mobile.planegame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //Member variables
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos, yAccel, yVel = 0.0f;
    private float xMax, yMax;
    private Bitmap plane;
    private SensorManager sensorManager;
    SensorEventListener2 seListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PlaneView planeView = new PlaneView(this);
        setContentView(planeView);

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        xMax = (float) size.x - 200;
        //yMax = (float) size.y - 200;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        seListener = new SensorEventListener2() {
            @Override
            public void onFlushCompleted(Sensor sensor) {}

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    xAccel = sensorEvent.values[0];
                    //yAccel = -sensorEvent.values[1];
                    updatePlane();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    protected void onStart(){
        super.onStart();
        sensorManager.registerListener(seListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop(){
        sensorManager.unregisterListener(seListener);
        super.onStop();
    }

    private void updatePlane() {
        float frameTime = 0.666f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        xPos -= xS;
        yPos -= yS;

        if(xPos > xMax){
            xPos = xMax;
        } else if(xPos < 0){
            xPos = 0;
        }

        if(yPos > yMax){
            yPos = yMax;
        } else if(yPos < 0){
            yPos = 0;
        }
    }

    private class PlaneView extends View {
        public PlaneView(Context context){
            super(context);
            Bitmap planeSrc = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
            final int dstWidth = 200;
            final int dstHeight = 200;
            plane = Bitmap.createScaledBitmap(planeSrc, dstWidth, dstHeight, true);
        }

        @Override
        protected void onDraw(Canvas canvas){
            canvas.drawBitmap(plane, xPos, yPos, null);
            invalidate();
        }
    }
}
