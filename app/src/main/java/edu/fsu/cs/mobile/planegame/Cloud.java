package edu.fsu.cs.mobile.planegame;

import java.util.Random;

/**
 * Created by sam12 on 7/23/2017.
 */

public class Cloud {
    int x, y, type;
    int yspeed;
    Random xRand;

    Cloud(){
        xRand = new Random();
        x = -100 + xRand.nextInt(900);
        y = -300;
        //yspeed = 5;
    }

    public void setType(int type) {
        this.type = type;
        switch (type){
            case 1:
                yspeed = 5;
                break;
            case 2:
                yspeed = 3;
                break;
        }
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
            x = -100 + (50 * xRand.nextInt(20));
            //yspeed = 3 + xRand.nextInt(5);
        }
    }

}
