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

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //Member variables
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos;
    private float xMax;
    private Bitmap plane, cloud1, cloud2;
    private SensorManager sensorManager;
    SensorEventListener2 seListener;
    ArrayList<Cloud> cloudArray = new ArrayList<Cloud>();
    int bkgdobj;
    int height, width;
    Bitmap cloudSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PlaneView planeView = new PlaneView(this);
        setContentView(planeView);
        bkgdobj = 10;
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

    public void tick(){
        Random temp = new Random();

        if(temp.nextInt(100) <= 2){
            addClouds();
        }
        moveClouds();
    }

    public void moveClouds(){
        Cloud tempCloud = new Cloud();

        for (int i = 0; i < cloudArray.size(); ++i){
            tempCloud = cloudArray.get(i);
            tempCloud.move();
        }
    }

    public void addClouds(){
        if (bkgdobj != 0){
            cloudArray.add(new Cloud());
            --bkgdobj;
        }
    }

    public class PlaneView extends View {
        Paint emptyPaint;
        ArrayList<Bitmap> clouds = new ArrayList<Bitmap>();

        public PlaneView(Context context){
            super(context);
            emptyPaint = new Paint();
            Bitmap planeSrc = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
            final int dstWidth = 200;
            final int dstHeight = 200;
            cloudSrc = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
            cloud1 = Bitmap.createScaledBitmap(cloudSrc, 325, 200, false);
            cloud2 = Bitmap.createScaledBitmap(cloudSrc, 175, 100, false);
            plane = Bitmap.createScaledBitmap(planeSrc, dstWidth, dstHeight, true);
        }

        @Override
        protected void onDraw(Canvas canvas){
            tick();
            Paint paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);

            int Sky = Color.rgb(135, 206, 250);
            canvas.drawColor(Sky);
            canvas.drawText("Score: ", 700, 200, paint);

            for(int i = 0; (i < cloudArray.size() && cloudArray.size() != 0); ++i){
                if(i % 2 == 0) {
                    canvas.drawBitmap(cloud1, cloudArray.get(i).getX(), cloudArray.get(i).getY(), emptyPaint);
                } else{
                    canvas.drawBitmap(cloud2, cloudArray.get(i).getX(), cloudArray.get(i).getY(), emptyPaint);
                }
            }

            canvas.drawBitmap(plane, xPos, yPos, null);


            invalidate();
        }
    }
}
