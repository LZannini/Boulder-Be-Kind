package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class BridgeGO extends GameObject {
    private static final float THICKNESS = 0.3f;
    private static final float DENSITY = 0.8f;
    private static int INSTANCE_COUNT = 0;
    private final Paint paint = new Paint();
    private static float screen_width, screen_height;
    private boolean isActive = false;

    public BridgeGO(GameWorld gw, float xStart, float xEnd, float yStart) {
        super(gw);
        INSTANCE_COUNT++;

        float width = xEnd - xStart;
        float height = THICKNESS;

        screen_width = gw.toPixelsXLength(width) / 2;
        screen_height = gw.toPixelsYLength(height) / 2;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(xStart + width / 2, yStart);
        bdef.setType(BodyType.dynamicBody);
        this.body = gw.world.createBody(bdef);
        body.setSleepingAllowed(false);
        body.setFixedRotation(true);

        this.name = "Bridge" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape bridgeShape = new PolygonShape();
        bridgeShape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(bridgeShape);
        fixtureDef.setDensity(DENSITY);
        fixtureDef.setFriction(0.5f);
        fixtureDef.setRestitution(0.2f);
        body.createFixture(fixtureDef);

        body.setGravityScale(0);
        body.setLinearVelocity(new Vec2(0f, 0f));

        paint.setColor(Color.rgb(80, 40, 20));
        paint.setStyle(Paint.Style.FILL);

        fixtureDef.delete();
        bridgeShape.delete();
        bdef.delete();
    }

    public void setMovableOn() {
        if (!isActive) {
            isActive = true;
            body.setGravityScale(1);
        }
    }

    public void setMovableOff() {
            isActive = false;
            body.setGravityScale(0);
            body.setLinearVelocity(new Vec2(0f, 0f));
        paint.setColor(Color.TRANSPARENT);
    }



    public boolean getIsActive() {
        return isActive;
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (paint.getColor() != Color.TRANSPARENT) {
            Canvas canvas = new Canvas(buffer);
            canvas.save();
            RectF rect = new RectF(x - screen_width, y - screen_height, x + screen_width, y + screen_height);
            canvas.drawRect(rect, paint);
            canvas.restore();
        }
    }
}
