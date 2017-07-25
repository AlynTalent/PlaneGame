package edu.fsu.cs.mobile.planegame;

import java.util.Random;

/**
 * Created by sam12 on 7/24/2017.
 */

public class Enemy {
    int x, y;
    int yspeed;
    Random xRand;

    Enemy(){
        xRand = new Random();
        x = 50 * xRand.nextInt(20);
        y = -300;
        yspeed = 6;
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
    }

}
