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
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

public class BalanceGO extends GameObject {
    private static final float BAR_WIDTH = 6.0f;
    private static final float BAR_HEIGHT = 0.3f;
    private static final float DENSITY = 1.0f;
    private Paint paint = new Paint();
    private boolean isVisible;
    private float screen_semi_width, screen_semi_height;
    private boolean isMovable;

    public BalanceGO(GameWorld gw, float x, float y) {
        super(gw);

        paint.setColor(Color.rgb(80, 40, 20));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        screen_semi_width = gw.toPixelsXLength(BAR_WIDTH) / 2;
        screen_semi_height = gw.toPixelsYLength(BAR_HEIGHT) / 2;

        // Pivot
        WallGO pivot = new WallGO(gw, x, x, y - 0.1f, y + 0.1f, false);
        gw.addGameObject(pivot);

        // Barra (sul pivot)
        BodyDef barDef = new BodyDef();
        barDef.setType(BodyType.dynamicBody);
        barDef.setPosition(x, y);
        this.body = gw.world.createBody(barDef);
        this.name = "SeesawBar";
        this.body.setUserData(this);
        isMovable = true;
        isVisible = true;

        PolygonShape barShape = new PolygonShape();
        barShape.setAsBox(BAR_WIDTH / 2, BAR_HEIGHT / 2);
        FixtureDef barFixture = new FixtureDef();
        barFixture.setShape(barShape);
        barFixture.setDensity(DENSITY);
        barFixture.setFriction(0.5f);
        barFixture.setRestitution(0.0f);
        this.body.createFixture(barFixture);

        barFixture.delete();
        barShape.delete();
        barDef.delete();

        // Revolute Joint per fissare la barra al pivot
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.setBodyA(this.body);
        revoluteJointDef.setBodyB(pivot.body);
        revoluteJointDef.setLocalAnchorA(0, 0);
        revoluteJointDef.setLocalAnchorB(0, 0);
        revoluteJointDef.setEnableMotor(false);
        revoluteJointDef.setMaxMotorTorque(10f);
        revoluteJointDef.setMotorSpeed(0f);
        gw.world.createJoint(revoluteJointDef);

        this.body.setLinearDamping(0.5f);
        this.body.setAngularDamping(0.5f);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (isVisible) {
            Canvas canvas = new Canvas(buffer);
            canvas.save();

            // Rotazione della barra intorno al pivot
            canvas.rotate((float) Math.toDegrees(angle), x, y);
            RectF barRect = new RectF(x - screen_semi_width, y - screen_semi_height, x + screen_semi_width, y + screen_semi_height);
            canvas.drawRect(barRect, paint);

            canvas.restore();
        }
    }

    public boolean getIsMovable() {
        return isMovable;
    }

    public void setIsMovable(boolean b) {
        isMovable = b;
    }

    public void setMovableOff() {
        isMovable = false;
        isVisible = false;
        body.setGravityScale(0);
        body.setLinearVelocity(new Vec2(0f, 0f));
    }
}
