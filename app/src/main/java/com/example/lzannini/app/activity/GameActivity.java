package com.example.lzannini.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.example.lzannini.R;
import com.example.lzannini.app.AndroidFastRenderView;
import com.example.lzannini.app.Box;
import com.example.lzannini.app.CollisionSounds;
import com.example.lzannini.app.GameWorld;
import com.example.lzannini.app.MyThread;

import java.nio.ByteOrder;

public class GameActivity extends Activity {

    private GameWorld gw;
    private MyThread t;
    private AndroidFastRenderView renderView;
    private Audio audio;
    private Music backgroundMusic;
    private MultiTouchHandler touch;
    private boolean isPaused = false;
    public static String TAG;
    public Button pauseButton;


    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");
        TAG = getString(R.string.app_name);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_game);
        FrameLayout layout = findViewById(R.id.main_container);

        setupAudio();
        setupGameWorld();

        // Creazione e inserimento della renderView nel layout
        this.renderView = new AndroidFastRenderView(this, this.gw);
        layout.addView(this.renderView, 0);

        // Gestione del touch
        this.touch = new MultiTouchHandler(this.renderView, 1, 1);
        this.gw.setTouchHandler(touch);

        // Start del thread di gioco
        this.t = new MyThread(this.gw);
        this.t.start();

        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setVisibility(View.INVISIBLE);
        pauseButton.setOnClickListener(v -> togglePause(pauseButton));


        Log.i(TAG, "onCreate complete, Endianness = " + (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "Big Endian" : "Little Endian"));
    }

    private void setupGameWorld() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX);
        Box screenSize = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        this.gw = new GameWorld(physicalSize, screenSize, this);
        this.gw.setupNextLevel();
    }

    private void setupAudio() {
        audio = new AndroidAudio(this);
        CollisionSounds.init(audio);
        backgroundMusic = audio.newMusic("soundtrack.mp3");
        if (MenuActivity.isSoundEnabled)
            backgroundMusic.play();
        else
            backgroundMusic.stop();
    }

    private void togglePause(Button pauseButton) {
        if (isPaused) {
            renderView.resume();
            t.resumeThread();
            backgroundMusic.play();
            isPaused = false;
            pauseButton.setTextSize(18);
            pauseButton.setText("❚❚");
        } else {
            renderView.pause();
            t.pauseThread();
            backgroundMusic.pause();
            isPaused = true;
            pauseButton.setTextSize(26);
            pauseButton.setText("▶");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gw.resetGame();
        renderView.pause();
        backgroundMusic.pause();

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(getString(R.string.important_info), t.counter);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
        gw.resetGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gw.resetGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gw.resetGame();
        renderView.resume();
        if (MenuActivity.isSoundEnabled) backgroundMusic.play();

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt(getString(R.string.important_info), -1);
        t.counter = counter;
    }
}
