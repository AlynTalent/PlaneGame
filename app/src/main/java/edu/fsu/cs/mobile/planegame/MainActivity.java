package edu.fsu.cs.mobile.planegame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //Member variables
    boolean startScreen = true;
    static MenuItem score;
    MenuItem lives;
    Menu menu;
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos;
    private float xMax;
    private Bitmap plane, cloud1, cloud2, enemy;
    private SensorManager sensorManager;
    SensorEventListener2 seListener;
    ArrayList<Cloud> cloudArray = new ArrayList<Cloud>();
    ArrayList<Enemy> enemyArray = new ArrayList<Enemy>();
    int clouds, enemies, totalenemies = 0;
    int livesRem = 3;
    int move = 0;
    static int points = 0;
    Bitmap cloudSrc, enemySrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final PlaneView planeView = new PlaneView(this);
        setContentView(planeView);
        clouds = 10;
        enemies = 10;
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        //setContentView(R.layout.activity_main);
        setContentView(planeView);

        //create start button dynamically
        final Button startButton = new Button(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 0;
        params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        startButton.setText("Start Game");
        addContentView(startButton, params);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScreen = false;
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
            }
        });

       /* Button HighScoresButton = (Button) findViewById(R.id.HighScoreButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show fragemtn with high scores
            }
        });
*/
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
                    //updatePlane();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        score = menu.findItem(R.id.score);
        score.setTitle("Score: " + points);

        lives = menu.findItem(R.id.lives);
        lives.setTitle("Lives: " + livesRem);

        this.menu = menu;
        return true;
    }

    public static void updateScore(){
        points += 50;
        score.setTitle("Score: " + points);
    }

    @Override
    protected void onStart(){
        super.onStart();
        sensorManager.registerListener(seListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    protected void onStop(){
        sensorManager.unregisterListener(seListener);
        super.onStop();
    }

    private void updatePlane() {
        float frameTime = 0.666f;
        //xVel += (xAccel * frameTime);
        float xS = 0;
        //float xS = (xVel / 2) * frameTime;

        //xPos -= xS;
        if(xAccel > -2 && xAccel < 2){
            xVel = 0;
            move = 0;
        }else if(xAccel > 2){
            if(move == 0 || move == -1){
                xVel = 2;
                move = 1;
            }

            xVel += xAccel * frameTime;
            xS = (xVel / 2) * frameTime;
        }else if(xAccel < -2){
            if (move == 0 || move == 1){
                xVel = -2;
                move = -1;
            }

            xVel += xAccel * frameTime;
            xS = (xVel / 2) * frameTime;
        }

        xPos -= xS;

        if(xPos > xMax){
            xPos = xMax;
        } else if(xPos < 0){
            xPos = 0;
        }

    }

    public void tick(){
        Random temp = new Random();
        if(temp.nextInt(250) <= 1){
            addClouds();
        }

        if(startScreen == false) {
            if (temp.nextInt(200) < 1) {
                if (totalenemies < 3) {
                    addEnemies();
                }
            }
        }

        moveClouds();
        moveEnemies();
        updatePlane();
    }

    public void moveEnemies(){
        Enemy tempEnemy = new Enemy();

        for (int i = 0; i < enemyArray.size(); ++i){
            tempEnemy = enemyArray.get(i);
            tempEnemy.move();
        }
    }

    public void moveClouds(){
        Cloud tempCloud = new Cloud();

        for (int i = 0; i < cloudArray.size(); ++i){
            tempCloud = cloudArray.get(i);
            tempCloud.move();
        }
    }

    public void addClouds(){
        if (clouds != 0){
            cloudArray.add(new Cloud());
            --clouds;
        }
    }

    public void addEnemies(){
        if(enemies != 0){
            enemyArray.add(new Enemy());
            --enemies;
            ++totalenemies;
        }
    }

    public class PlaneView extends View {
        Paint emptyPaint;

        public PlaneView(Context context){
            super(context);
            emptyPaint = new Paint();
            Bitmap planeSrc = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
            final int dstWidth = 200;
            final int dstHeight = 200;
            cloudSrc = BitmapFactory.decodeResource(getResources(), R.drawable.white_cloud);
            enemySrc = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_fighter);
            cloud1 = Bitmap.createScaledBitmap(cloudSrc, 325, 200, false);
            cloud2 = Bitmap.createScaledBitmap(cloudSrc, 175, 100, false);
            plane = Bitmap.createScaledBitmap(planeSrc, dstWidth, dstHeight, true);
            enemy = Bitmap.createScaledBitmap(enemySrc, 175, 200, false);
            enemy = RotateBitmap(enemy, 180);
        }

        @Override
        protected void onDraw(Canvas canvas){
            tick();
            Paint paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

            int Sky = Color.rgb(135, 206, 250);
            canvas.drawColor(Sky);

            for(int i = 0; (i < cloudArray.size() && cloudArray.size() != 0); ++i){
                if(i > 4 ) {
                    cloudArray.get(i).setType(1);
                    canvas.drawBitmap(cloud1, cloudArray.get(i).getX(), cloudArray.get(i).getY(), emptyPaint);
                } else{
                    cloudArray.get(i).setType(2);
                    canvas.drawBitmap(cloud2, cloudArray.get(i).getX(), cloudArray.get(i).getY(), emptyPaint);
                }
            }

            if(startScreen == false) {
                for (int i = 0; (i < enemyArray.size() && enemyArray.size() != 0); ++i) {
                    canvas.drawBitmap(enemy, enemyArray.get(i).getX(), enemyArray.get(i).getY(), paint);
                }
            }else{
                canvas.drawText("FIGHTING FALCON", 350, 300, paint);
            }
            canvas.drawBitmap(plane, xPos, yPos, null);


            invalidate();
        }

        public void speedUp(){
            for(int i = 0; (i < cloudArray.size() && cloudArray.size() != 0); ++i){
                cloudArray.get(i).speedUp();
            }
        }

        public Bitmap RotateBitmap(Bitmap source, float angle)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
    }
}
