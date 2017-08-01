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
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //Member variables
    static int startScreen = 0;
    static boolean gameOver = false;
    static MenuItem score;
    static MenuItem lives;
    Menu menu;
    static float xPos, xAccel, xVel = 0.0f;
    static float yPos;
    private float xMax;
    private static Bitmap plane;
    private static Bitmap damagedPlane;
    private static Bitmap explosion;
    private Bitmap cloud1;
    private Bitmap cloud2;
    private Bitmap enemy;
    private SensorManager sensorManager;
    SensorEventListener2 seListener;
    ArrayList<Cloud> cloudArray = new ArrayList<Cloud>();
    ArrayList<Enemy> enemyArray = new ArrayList<Enemy>();
    int clouds, enemies, totalenemies = 0;
    static int livesRem = 3;
    int move = 0, maxEnemy, level;
    static int vulnerable = 0;
    static int points = 0;
    Bitmap cloudSrc;
    Bitmap enemySrc;
    static Bitmap planeSrc;
    static Bitmap planeSrc2;
    static Bitmap explosionSrc;
    static Context c;
    Button restartButton;
    boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final PlaneView planeView = new PlaneView(this);
        setContentView(planeView);
        clouds = 10;
        enemies = 10;
        maxEnemy = 3;
        level = 1;
        c = getApplicationContext();
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
                startScreen = 1;
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
            }
        });

        planeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (pause == false)
                    pause = true;
                else
                    pause = false;
                return MainActivity.super.onTouchEvent(event);
            }
        });

        restartButton = new Button(c);
        restartButton.setText("Restart Game");
        addContentView(restartButton, params);
        restartButton.setVisibility(View.GONE);
        restartButton.setClickable(false);
        enemyArray.clear();

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScreen = 1;
                restartButton.setVisibility(View.GONE);
                restartButton.setClickable(false);
                gameOver = false;
                enemies = 10;
                totalenemies = 0;
                livesRem = 3;
                lives.setTitle("Lives: " + livesRem);
                points = 0;
                score.setTitle("Score: " + points);
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

    public static void updateLives(){
        if(livesRem == 0){
            gameOver = true;
            startScreen = 0;
        }else{
            --livesRem;
            lives.setTitle("Lives: " + livesRem);
            Toast t = Toast.makeText(c, "-1 Life", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
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

    public void tick() {
        Random temp = new Random();
        if(temp.nextInt(250) <= 1){
            addClouds();
        }

        if(startScreen == 1) {
            switch (level){
                default:
                    if(points / 1000 == level){
                        maxEnemy++;
                        level++;
                        Toast t = Toast.makeText(c, "Level " + level, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        t.show();
                    }
            }

            if (temp.nextInt(200) < 1) {
                if (totalenemies < maxEnemy) {
                    addEnemies();
                }
            }
        }

        if(pause == false) {
            moveClouds();
            moveEnemies();
            updatePlane();
        }
    }

    public static void doDamage(){
        if(vulnerable == 0){
            updateLives();
            vulnerable = 1;
            //give 3 seconds before being vulnerable again
            new CountDownTimer(3000,100){
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    vulnerable = 0;
                }
            }.start();
        }
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

            planeSrc = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
            planeSrc2 = BitmapFactory.decodeResource(getResources(), R.drawable.damaged_plane);
            //explosionSrc = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
            final int dstWidth = 200;
            final int dstHeight = 200;
            cloudSrc = BitmapFactory.decodeResource(getResources(), R.drawable.white_cloud);
            enemySrc = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_fighter);
            cloud1 = Bitmap.createScaledBitmap(cloudSrc, 325, 200, false);
            cloud2 = Bitmap.createScaledBitmap(cloudSrc, 175, 100, false);
            plane = Bitmap.createScaledBitmap(planeSrc, dstWidth, dstHeight, true);
            damagedPlane = Bitmap.createScaledBitmap(planeSrc2, dstWidth, dstHeight, true);
            //explosion = Bitmap.createScaledBitmap(explosionSrc, dstWidth, dstHeight, true);
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

            if(startScreen == 1) {
                for (int i = 0; (i < enemyArray.size() && enemyArray.size() != 0); ++i) {
                    canvas.drawBitmap(enemy, enemyArray.get(i).getX(), enemyArray.get(i).getY(), paint);
                }
            }else if(gameOver == true){
                canvas.drawText("Game Over", 350, 350, paint);
                enemyArray.clear();
                restartButton.setClickable(true);
                restartButton.setVisibility(VISIBLE);
            }else{
                canvas.drawText("FIGHTING FALCON", 350, 350, paint);
            }

            if(pause == true)
                canvas.drawText("PAUSE", 350, 350, paint);


            //will only draw a plane when gameOver is not true
            if (!gameOver) {
                if (vulnerable == 1)
                    canvas.drawBitmap(damagedPlane, xPos, yPos, null);
                else
                    canvas.drawBitmap(plane, xPos, yPos, null);
            }
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
