package edu.fsu.cs.mobile.planegame;

import java.util.Random;

/**
 * Created by sam12 on 7/23/2017.
 */

public class Cloud {
    int x, y;
    int yspeed;
    Random xRand;

    Cloud(){
        xRand = new Random();
        x = xRand.nextInt(800);
        y = -300;
        yspeed = 3 + xRand.nextInt(5);
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
            x = 50 * xRand.nextInt(18);
            yspeed = 3 + xRand.nextInt(5);
        }
    }

}
