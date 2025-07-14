package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.example.lzannini.app.Level;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

public class GuiGO extends GameObject {

    private final Paint paint;
    private static int levelCounter = 0;
    private long startTime;
    private boolean countdownActive = true;
    private boolean countdownFinished = false;
    private boolean showCompletionMessage = false;

    public GuiGO(GameWorld gw) {
        super(gw);
        this.paint = new Paint();

        Typeface typeface = Typeface.createFromAsset(gw.getActivity().getAssets(), "fonts/PressStart2P.ttf");
        this.paint.setTypeface(typeface);

        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setUserData(this);

        bodyDef.delete();

        this.paint.setColor(Color.BLACK);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paint.setTextSize(35);
        this.paint.setTextAlign(Paint.Align.CENTER);

        this.startTime = System.currentTimeMillis();
    }

    public void setLevelCounter(int level) {
        levelCounter = level;
        startTime = System.currentTimeMillis();
        countdownActive = true;
        countdownFinished = false;
        showCompletionMessage = (level > 1);
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        Canvas canvas = new Canvas(buf);

        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);

        float textX = buf.getWidth() / 2f;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;

        if (levelCounter == 5) {
            if (elapsed < 3) {
                this.paint.setTextSize(16f);
                this.paint.setColor(Color.YELLOW);
                float textY = buf.getHeight() / 3f;
                canvas.drawText("All levels completed.", textX, textY, paint);
                canvas.drawText("You won!", textX, textY + 25, paint);
            } else {
                gw.goToMenu();
            }
        } else if (levelCounter == -1) {
            if (elapsed < 3) {
                this.paint.setTextSize(16);
                this.paint.setColor(Color.rgb(215, 0, 0));
                float textY = buf.getHeight() / 3f;
                canvas.drawText("Mark is dead! Game Over", textX, textY, paint);
            } else {
                gw.goToMenu();
            }
        } else {
            showCompletionMessage = false;
            int countdown = 3 - (int) (elapsed - 2);
            if (countdown > 0) {
                this.paint.setTextSize(16f);
                float textY = buf.getHeight() / 3f;
                canvas.drawText("Level " + levelCounter + " is starting in " + countdown, textX, textY, paint);
                Level.countdownOver = true;
            } else {
                Level.isStarted = true;
                countdownActive = false;
                this.paint.setTextSize(35);
                float textY = 40;
                canvas.drawText("Level " + levelCounter, textX, textY, paint);

                if (Level.isHammerTaken) {
                    this.paint.setTextSize(18);
                    this.paint.setColor(Color.YELLOW);
                    float hammerTextY = textY + 31.5f;
                    canvas.drawText("You took the hammer.", textX, hammerTextY, paint);
                    canvas.drawText("Break the cage!", textX, hammerTextY + 25, paint);
                    this.paint.setColor(Color.BLACK);
                }
            }
        }
        canvas.restore();
    }
}
