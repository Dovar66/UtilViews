package com.dovar.utilviews.timertextview;

import java.util.Timer;

/**
 * 单例获取计时器
 */
public class MyTimer extends Timer {

    private static MyTimer timer;

    private MyTimer() {
    }

    public static MyTimer getInstance(){
        if (timer==null){
            synchronized (MyTimer.class){
                if (timer==null){
                    timer=new MyTimer();
                }
            }
        }
        return timer;
    }
}
