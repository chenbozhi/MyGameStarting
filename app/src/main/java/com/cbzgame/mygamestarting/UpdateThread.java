package com.cbzgame.mygamestarting;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class UpdateThread extends Thread {
    private long time;
    private final int fps = 20;
    private boolean toRun = false;
    private MovementView movementView;
    private SurfaceHolder surfaceHolder;

    public UpdateThread(MovementView movementView) {
        this.movementView = movementView;
        surfaceHolder = this.movementView.getHolder();
    }

    public void setRuing(boolean run)
    {
        toRun = true;
    }
    @SuppressLint("WrongCall")


    public void run()
    {
        Canvas canvas;
        while (toRun)
        {
            long cTime = System.currentTimeMillis();
            if(cTime - time <= 1000 / fps)
            {
                canvas = null;
                try{
                    canvas = surfaceHolder.lockCanvas(null);
                    movementView.updatePhysics();
                    movementView.onDraw(canvas);
                }finally {
                    if(canvas != null)
                    {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
            time = cTime;
        }
    }
}
