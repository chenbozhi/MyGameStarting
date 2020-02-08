package com.cbzgame.mygamestarting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.SurfaceView;

import android.graphics.Paint;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class MovementView extends SurfaceView implements SurfaceHolder.Callback{

    private int width;
    private int height;
    private Paint circlePaint;
    UpdateThread updateThread;
    Ball[] balls;
    private final int anotherBalls = 10;

    private class Ball
    {
        private int xPos;
        private int yPos;
        private int xVel;
        private int yVel;
        private int circleRadius;
        private int color;
    }

    public MovementView(Context context) {
        super(context);
        getHolder().addCallback(this);
        circlePaint = new Paint();
        circlePaint.setColor(Color.GREEN);
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE); //画布涂白

        for(int i = 0; i < anotherBalls; i++)
        {
            circlePaint.setColor(balls[i].color);
            canvas.drawCircle(balls[i].xPos, balls[i].yPos,balls[i].circleRadius, circlePaint);
        }
    }
    protected void updatePhysics()
    {
        for(int i = 0; i < anotherBalls; i++)
        {
            balls[i].xPos += balls[i].xVel;
            balls[i].yPos += balls[i].yVel;
            if(balls[i].yPos - balls[i].circleRadius < 0 || balls[i].yPos + balls[i].circleRadius > height)
            {
                if(balls[i].yPos - balls[i].circleRadius < 0)
                {
                    balls[i].yPos = balls[i].circleRadius;
                }
                else
                {
                    balls[i].yPos = height - balls[i].circleRadius;
                }
                balls[i].yVel *= -1;
            }
            if(balls[i].xPos - balls[i].circleRadius < 0 || balls[i].xPos + balls[i].circleRadius > width)
            {
                if(balls[i].xPos - balls[i].circleRadius < 0)
                {
                    balls[i].xPos = balls[i].circleRadius;
                }
                else
                {
                    balls[i].xPos = width - balls[i].circleRadius;
                }
                balls[i].xVel *= -1;
            }
        }
        attackBall();
    }

    private void attackBall()  //小球之间的碰撞
    {
        for(int i = 0; i < anotherBalls; i++)
        {
            for(int j = 0; j < anotherBalls; j++)
            {
                if(i != j)
                {
                    if((Math.sqrt(Math.pow((double)(balls[i].xPos - balls[j].xPos) , 2) + Math.pow((double)(balls[i].yPos - balls[j].yPos) , 2))) <= balls[i].circleRadius + balls[j].circleRadius)
                    {
                        int x = balls[i].xVel;
                        int y = balls[i].yVel;
                        balls[i].xVel = balls[j].xVel;
                        balls[i].yVel = balls[j].yVel;
                        balls[j].xVel = x;
                        balls[j].yVel = y;
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void surfaceCreated(SurfaceHolder holder)
    {
        Rect surfaceFrame = holder.getSurfaceFrame();
        width = surfaceFrame.width();
        height = surfaceFrame.height();

        balls = new Ball[anotherBalls];
        Random random = new Random();
        for(int i = 0; i < anotherBalls; i++)
        {
            balls[i] = new Ball();
            balls[i].circleRadius = random.nextInt(40) + 40;
            balls[i].xPos = random.nextInt(width - balls[i].circleRadius) + balls[i].circleRadius;
            balls[i].yPos = random.nextInt(height - balls[i].circleRadius) + balls[i].circleRadius;
            int direction = random.nextInt(2);
            balls[i].xVel = (random.nextInt(15) + 3) * (direction == 0 ? 1 : -1);
            balls[i].yVel = (random.nextInt(15) + 3) * (direction == 0 ? 1 : -1);
            balls[i].color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }

        updateThread = new UpdateThread(this);
        updateThread.setRuing(true);
        updateThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        updateThread.setRuing(false);
        while (retry)
        {
            try
            {
                updateThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    //没用上
    //获取16进制颜色字符串
    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }
    */
}
