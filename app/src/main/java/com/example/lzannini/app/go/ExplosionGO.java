package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.content.res.AssetManager;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.example.lzannini.R;
import com.example.lzannini.app.activity.MenuActivity;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

import java.io.IOException;

public class ExplosionGO extends GameObject {
    private static final float size = 2.5f;
    private static float screen_semi_size;
    private final Canvas canvas;
    private final Paint paint = new Paint();
    private final Bitmap explosionBitmap;
    private long startTime;
    private boolean isFading;
    private float alpha;

    public ExplosionGO(GameWorld gw, float x, float y) {
        super(gw);
        this.canvas = new Canvas(gw.buffer);
        this.screen_semi_size = gw.toPixelsXLength(size) / 2;
        this.startTime = SystemClock.uptimeMillis();
        this.isFading = false;
        this.alpha = 255;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.staticBody);
        this.body = gw.world.createBody(bdef);
        body.setUserData(this);
        bdef.delete();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        explosionBitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.explosion, o);

        if (MenuActivity.isSoundEnabled)
            playExplosionSound(gw.getActivity().getAssets());
    }

    public long getStartTime() {
        return startTime;
    }

    public void decreaseAlpha(float amount) {
        alpha -= amount;
        if (alpha < 0) {
            alpha = 0;
        }
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);
        paint.setAlpha((int) alpha);
        Rect src = new Rect(0, 0, explosionBitmap.getWidth(), explosionBitmap.getHeight());
        RectF dest = new RectF(x - screen_semi_size, y - screen_semi_size, x + screen_semi_size, y + screen_semi_size);
        canvas.drawBitmap(explosionBitmap, src, dest, paint);
        canvas.restore();
    }

    private void playExplosionSound(AssetManager assetManager) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(assetManager.openFd("explosion.wav").getFileDescriptor(),
                    assetManager.openFd("explosion.wav").getStartOffset(),
                    assetManager.openFd("explosion.wav").getLength());

            mediaPlayer.setOnCompletionListener(mp -> mp.release());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
