package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

public class TriangleBaseGO extends GameObject {
    private Paint paint = new Paint();
    private static int INSTANCE_COUNT = 0;
    private float xStart, xEnd, yStart, yEnd;
    private float screen_xStart, screen_xEnd, screen_yStart, screen_yEnd;

    public TriangleBaseGO(GameWorld gw, float xStart, float xEnd, float yStart, float yEnd) {
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
        this.name = "TriangleBase" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape triangleShape = new PolygonShape();

        triangleShape.setAsTriangle(
                2 * ((xStart + xEnd) / 2) - xStart, yStart,
                2 * ((xStart + xEnd) / 2) - xEnd, yStart,
                2 * ((xStart + xEnd) / 2) - xStart, yEnd
        );
        body.createFixture(triangleShape, 0);

        bdef.delete();
        triangleShape.delete();
    }

    public void mirrorHorizontally() {
        float centerX = (xStart + xEnd) / 2;
        float newXStart = 2 * centerX - xStart;
        float newXEnd = 2 * centerX - xEnd;

        xStart = newXStart;
        xEnd = newXEnd;

        this.screen_xStart = gw.toPixelsX(xStart);
        this.screen_xEnd = gw.toPixelsX(xEnd);
    }

    public void mirrorVertically() {
        float centerY = (yStart + yEnd) / 2;
        float newYStart = 2 * centerY - yStart;
        float newYEnd = 2 * centerY - yEnd;

        yStart = newYStart;
        yEnd = newYEnd;

        this.screen_yStart = gw.toPixelsY(yStart);
        this.screen_yEnd = gw.toPixelsY(yEnd);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        paint.setARGB(255, 30, 30, 30);
        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = new Canvas(buffer);
        Path path = new Path();

        path.moveTo(screen_xStart, screen_yStart);
        path.lineTo(screen_xEnd, screen_yStart);
        path.lineTo(screen_xStart, screen_yEnd);
        path.close();

        canvas.drawPath(path, paint);
    }
}
