package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

public class InclinedFloorGO extends GameObject {
    private static final float WIDTH = 0.2f;
    private Paint paint = new Paint();
    private float xStart, xEnd, yStart, yEnd;
    private static int INSTANCE_COUNT = 0;
    private float screen_xStart, screen_xEnd, screen_yStart, screen_yEnd;

    public InclinedFloorGO(GameWorld gw, float xStart, float xEnd, float yStart, float yEnd) {
        super(gw);
        INSTANCE_COUNT++;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.screen_xStart = gw.toPixelsX(xStart);
        this.screen_xEnd = gw.toPixelsX(xEnd);
        this.screen_yStart = gw.toPixelsY(yStart);
        this.screen_yEnd = gw.toPixelsY(yEnd);

        BodyDef bdef = new BodyDef();
        this.body = gw.world.createBody(bdef);
        this.name = "InclinedFloor" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox((xEnd - xStart) / 2, WIDTH, xStart + (xEnd - xStart) / 2, (yStart + yEnd) / 2,
                (float) Math.atan2(yEnd - yStart, xEnd - xStart));
        body.createFixture(floorShape, 0);

        bdef.delete();
        floorShape.delete();
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        paint.setARGB(255, 0, 0, 0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        Canvas canvas = new Canvas(buffer);
        canvas.drawLine(screen_xStart, screen_yStart, screen_xEnd, screen_yEnd, paint);
    }
}