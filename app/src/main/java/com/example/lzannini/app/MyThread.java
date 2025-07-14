package com.example.lzannini.app;

import android.util.Log;

import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.RayCastCallback;
import com.google.fpl.liquidfun.Vec2;

public class MyThread extends Thread {
    public volatile int counter;
    private final GameWorld gw;
    private boolean running = true;
    private boolean paused = false;

    public MyThread(GameWorld gw) {
        this.gw = gw;
    }

    private void testRayCasting() {
        Log.i("MyThread", "Objects across the short middle line:");
        RayCastCallback listener = new RayCastCallback() {
            @Override
            public float reportFixture(Fixture f, Vec2 point, Vec2 normal, float fraction) {
                Log.i("MyThread", ((GameObject) f.getBody().getUserData()).name + " (" + fraction + ")");
                return 1;
            }
        };
        gw.world.rayCast(listener, -10, 0, 10, 0);
    }

    public synchronized void pauseThread() {
        paused = true;
    }

    public synchronized void resumeThread() {
        paused = false;
        notify(); // Riprende l'esecuzione del thread
    }

    public void stopThread() {
        running = false;
        interrupt(); // in caso stia dormendo
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Log.i("MyThread", "Thread interrupted during pause");
                        return;
                    }
                }
            }

            try {
                sleep(3000); // Simulazione logica periodica
                counter++;
                Log.i("MyThread", "counter: " + counter);
                testRayCasting();
            } catch (InterruptedException e) {
                Log.i("MyThread", "Thread interrupted during sleep");
                return;
            }
        }
    }
}
