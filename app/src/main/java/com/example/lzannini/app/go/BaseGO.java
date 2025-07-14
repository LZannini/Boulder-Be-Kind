package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

public class BaseGO extends GameObject {
    private Paint paint = new Paint();
    private static int INSTANCE_COUNT = 0;
    private float xStart, xEnd, yStart, yEnd;
    private float screen_xStart, screen_xEnd, screen_yStart, screen_yEnd;

    public BaseGO(GameWorld gw, float xStart, float xEnd, float yStart, float yEnd) {
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
        this.name = "Base" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape baseShape = new PolygonShape();
        baseShape.setAsBox((xEnd - xStart) / 2, (yEnd - yStart) / 2, xStart + (xEnd - xStart) / 2, yStart + (yEnd - yStart) / 2, 0);
        body.createFixture(baseShape, 0);

        bdef.delete();
        baseShape.delete();
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        paint.setARGB(255, 30, 30, 30);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(buffer);
        canvas.drawRect(screen_xStart, screen_yStart, screen_xEnd, screen_yEnd, paint);
    }
}
