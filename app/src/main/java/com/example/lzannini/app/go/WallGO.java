package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

public class WallGO extends GameObject {
    private static final float THICKNESS = 0.2f;
    private Paint paint = new Paint();
    private static int INSTANCE_COUNT = 0;
    private float xStart, xEnd, yStart, yEnd;
    private boolean isToDestroy;
    private float screen_xStart, screen_xEnd, screen_yStart, screen_yEnd;

    public WallGO(GameWorld gw, float xStart, float xEnd, float yStart, float yEnd, boolean isToDestroy) {
        super(gw);
        INSTANCE_COUNT++;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.isToDestroy = isToDestroy;

        this.screen_xStart = gw.toPixelsX(xStart);
        this.screen_xEnd = gw.toPixelsX(xEnd);
        this.screen_yStart = gw.toPixelsY(yStart);
        this.screen_yEnd = gw.toPixelsY(yEnd);

        BodyDef bdef = new BodyDef();
        bdef.setType(com.google.fpl.liquidfun.BodyType.staticBody);
        float centerX = (xStart + xEnd) / 2;
        float centerY = (yStart + yEnd) / 2;
        bdef.setPosition(centerX, centerY);
        this.body = gw.world.createBody(bdef);

        this.name = "Wall" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape wallShape = new PolygonShape();
        if (xStart == xEnd) {
            wallShape.setAsBox(THICKNESS, (yEnd - yStart) / 2);
        } else {
            wallShape.setAsBox((xEnd - xStart) / 2, THICKNESS);
        }
        body.createFixture(wallShape, 0);

        bdef.delete();
        wallShape.delete();
    }


    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        Canvas canvas = new Canvas(buffer);

        if (isToDestroy) {
            int segments = 30;
            float dx = (screen_xEnd - screen_xStart) / segments;
            float dy = (screen_yEnd - screen_yStart) / segments;

            for (int i = 0; i < segments; i++) {
                float startX = screen_xStart + i * dx;
                float startY = screen_yStart + i * dy;
                float endX = screen_xStart + (i + 1) * dx;
                float endY = screen_yStart + (i + 1) * dy;

                if (i % 6 == 5) {
                    paint.setARGB(255, 255, 0, 0);
                } else {
                    paint.setARGB(255, 0, 0, 0);
                }
                paint.setStrokeWidth(8);
                canvas.drawLine(startX, startY, endX, endY, paint);
            }
        } else {
            paint.setARGB(255, 0, 0, 0);
            paint.setStrokeWidth(8);
            canvas.drawLine(screen_xStart, screen_yStart, screen_xEnd, screen_yEnd, paint);
        }
    }

    public boolean isToDestroy() {
        return isToDestroy;
    }
}
