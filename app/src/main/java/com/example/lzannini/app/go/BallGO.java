package com.example.lzannini.app.go;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.lzannini.app.GameObject;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.Vec2;

import com.example.lzannini.app.GameWorld;

public class BallGO extends GameObject {
    private static final float WIDTH = 2.5f;
    private static final float HEIGHT = 2.5f;
    private static final float DENSITY = 0.5f;
    private static final float FRICTION = 1f;
    private static final float RESTITUTION = 0.1f;
    private static int INSTANCE_COUNT = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();
    private boolean toDestroy = false;

    private final float screenSemiWidth, screenSemiHeight;

    public BallGO(GameWorld gw, float x, float y) {
        super(gw);
        INSTANCE_COUNT++;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.paint = new Paint();
        this.screenSemiWidth = gw.toPixelsXLength(WIDTH) / 2;
        this.screenSemiHeight = gw.toPixelsYLength(HEIGHT) / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);
        bodyDef.setLinearVelocity(new Vec2(5f, 5f));

        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "Ball" + INSTANCE_COUNT;
        this.body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(WIDTH / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(circleShape);
        fixtureDef.setFriction(FRICTION);
        fixtureDef.setRestitution(RESTITUTION);
        fixtureDef.setDensity(DENSITY);
        this.body.createFixture(fixtureDef);

        int color = Color.argb(255, 90, 77, 65);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        bodyDef.delete();
        circleShape.delete();
        fixtureDef.delete();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawCircle(x, y, this.screenSemiWidth, this.paint);
        this.canvas.restore();
    }

    public void setToDestroy() {
        this.toDestroy = true;
    }

    public boolean isToDestroy() {
        return toDestroy;
    }
}
