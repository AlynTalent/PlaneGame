package edu.fsu.cs.mobile.planegame;

import java.util.Random;

/**
 * Created by sam12 on 7/24/2017.
 */

public class Enemy {
    int x, y;
    int yspeed;
    Random xRand;
    int range = 100;

    Enemy(){
        xRand = new Random();
        x = 50 * xRand.nextInt(20);
        y = -300;
        yspeed = 6;
    }

    private double distance(int xa, int ya, int xb, int yb)
    {
        int x1 = xa;
        int x2 = xb;
        int xSqr = (x2 - x1) * (x2 - x1);
        int y1 = ya;
        int y2 = yb;
        int ySqr = (y2 - y1) * (y2 - y1);
        double distance = Math.sqrt(xSqr + ySqr);

        return distance;
    }

    public void detectCollision(){
        if(distance(x, y, (int) MainActivity.xPos, (int) MainActivity.yPos) < range){
            MainActivity.doDamage();
        }
    }

    public void speedUp(){
        yspeed = yspeed * 2;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(){
        y += yspeed;

        if(y >= 1550){
            y = -200;
            x = 50 * xRand.nextInt(20);
            MainActivity.updateScore();
        }
        detectCollision();
    }

}
