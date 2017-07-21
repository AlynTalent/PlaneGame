package edu.fsu.cs.mobile.planegame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //Member variables
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos;
    private float xMax;
    private Bitmap plane, cloud;
    private SensorManager sensorManager;
    SensorEventListener2 seListener;

    int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PlaneView planeView = new PlaneView(this);
        setContentView(planeView);

        Point size = new Point();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        xMax = (float) size.x - 200;
        yPos = size.y - 450;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        seListener = new SensorEventListener2() {
            @Override
            public void onFlushCompleted(Sensor sensor) {}

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    xAccel = sensorEvent.values[0];
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

        float xS = (xVel / 2) * frameTime;

        xPos -= xS;

        if(xPos > xMax){
            xPos = xMax;
        } else if(xPos < 0){
            xPos = 0;
        }

    }

    private class PlaneView extends View {
        public PlaneView(Context context){
            super(context);
            Bitmap planeSrc = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
            Bitmap cloudSrc = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
            final int dstWidth = 200;
            final int dstHeight = 200;
            plane = Bitmap.createScaledBitmap(planeSrc, dstWidth, dstHeight, true);
            cloud = Bitmap.createScaledBitmap(cloudSrc, 300, 200, true);
        }

        @Override
        protected void onDraw(Canvas canvas){
            Paint paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);

            int Sky = Color.rgb(135, 206, 250);
            canvas.drawColor(Sky);
            canvas.drawText("Score: ", 700, 200, paint);
            canvas.drawBitmap(cloud, 100, 100, null);
            canvas.drawBitmap(cloud, 500, 500, null);
            canvas.drawBitmap(plane, xPos, yPos, null);

            invalidate();
        }
    }
}
