package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.PrismaticJointDef;
import com.google.fpl.liquidfun.Vec2;

import java.io.IOException;

public class PulleyPlatformGO extends GameObject {
    private static final float PLATFORM_WIDTH = 3.0f;
    private static final float PLATFORM_HEIGHT = 0.6f;
    private static final float DENSITY = 1.0f;

    private Paint paint = new Paint();
    private float screen_semi_width, screen_semi_height;
    private float topY;
    private float anchorX;

    private boolean isVisible;
    private boolean isMovable;

    public PulleyPlatformGO(GameWorld gw, float x, float y, float ropeLength) {
        super(gw);
        this.anchorX = x;
        this.topY = y;

        paint.setColor(Color.rgb(80, 40, 20));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        screen_semi_width = gw.toPixelsXLength(PLATFORM_WIDTH) / 2;
        screen_semi_height = gw.toPixelsYLength(PLATFORM_HEIGHT) / 2;

        isVisible = true;
        isMovable = true;

        // Piattaforma
        BodyDef platformDef = new BodyDef();
        platformDef.setType(BodyType.dynamicBody);
        platformDef.setPosition(x, y + ropeLength);
        this.body = gw.world.createBody(platformDef);
        this.name = "PulleyPlatform";
        this.body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(PLATFORM_WIDTH / 2, PLATFORM_HEIGHT / 2);

        FixtureDef fixture = new FixtureDef();
        fixture.setShape(shape);
        fixture.setDensity(DENSITY);
        fixture.setFriction(0.5f);
        fixture.setRestitution(0.1f);
        this.body.createFixture(fixture);

        // Punto di ancoraggio
        BodyDef anchorDef = new BodyDef();
        anchorDef.setType(BodyType.staticBody);
        anchorDef.setPosition(x, y);
        Body anchor = gw.world.createBody(anchorDef);

        // Joint Prismatico per spostare la piattaforma verticalmente verso il punto di ancoraggio
        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.setBodyA(anchor);
        jointDef.setBodyB(this.body);
        jointDef.setLocalAnchorA(0, 0);
        jointDef.setLocalAnchorB(0, 0);
        jointDef.setLocalAxisA(0, 1);
        jointDef.setReferenceAngle(0f);
        jointDef.setEnableLimit(true);
        jointDef.setLowerTranslation(0f);
        jointDef.setUpperTranslation(ropeLength);
        gw.world.createJoint(jointDef);

        shape.delete();
        fixture.delete();
        platformDef.delete();
        anchorDef.delete();
        jointDef.delete();
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (!isVisible) return;

        Canvas canvas = new Canvas(buffer);
        canvas.save();

        float anchorScreenX = gw.toPixelsX(anchorX);
        float anchorScreenY = gw.toPixelsY(topY);
        canvas.drawLine(anchorScreenX, anchorScreenY, x, y, getLinePaint());

        canvas.rotate((float) Math.toDegrees(angle), x, y);
        RectF rect = new RectF(x - screen_semi_width, y - screen_semi_height, x + screen_semi_width, y + screen_semi_height);
        canvas.drawRect(rect, paint);

        canvas.restore();
    }

    private Paint getLinePaint() {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStrokeWidth(4);
        return linePaint;
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

    public void playPulleySound() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(gw.getActivity().getAssets().openFd("clank.wav").getFileDescriptor(),
                    gw.getActivity().getAssets().openFd("clank.wav").getStartOffset(),
                    gw.getActivity().getAssets().openFd("clank.wav").getLength());

            mediaPlayer.setOnCompletionListener(mp -> mp.release());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
