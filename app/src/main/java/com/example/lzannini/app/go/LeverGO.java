package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.example.lzannini.R;

public class LeverGO extends GameObject {

    private static final float width = 10f, height = 10f, density = 0.5f;
    private static float screen_semi_width, screen_semi_height;
    private static int INSTANCE_COUNT = 0;
    public boolean isPressed = false;
    public static boolean execAction = false;
    private final Canvas canvas;
    private final Paint paint = new Paint();

    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private Bitmap bitmap;
    private Bitmap bitmapOff, bitmapOn;

    public LeverGO(GameWorld gw, float x, float y) {
        super(gw);

        INSTANCE_COUNT++;
        this.canvas = new Canvas(gw.buffer);

        float boxWidth = 16 / gw.toPixelsXLength(1);
        float boxHeight = 16 / gw.toPixelsYLength(1);

        this.screen_semi_width = gw.toPixelsXLength(boxWidth) / 2;
        this.screen_semi_height = gw.toPixelsYLength(boxHeight) / 2;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.staticBody);

        this.body = gw.world.createBody(bdef);
        this.name = "Lever" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth / 2, boxHeight / 2);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        fixturedef.setFriction(0.1f);
        fixturedef.setRestitution(0.4f);
        fixturedef.setDensity(density);
        body.createFixture(fixturedef);

        int green = (int) (255 * Math.random());
        int color = Color.argb(200, 255, green, 0);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        fixturedef.delete();
        bdef.delete();
        box.delete();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmapOff = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.lever_off, o);
        bitmapOn = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.lever_on, o);
        bitmap = bitmapOff;

        src.set(0, 0, 16, 16);
    }


    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);
        dest.left = x - screen_semi_width * 2.5f;
        dest.bottom = y + screen_semi_height * 2.5f;
        dest.right = x + screen_semi_width * 2.5f;
        dest.top = y - screen_semi_height * 2.5f;
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }

    public void press() {
        if (!isPressed) {
            this.isPressed = true;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inScaled = false;
            this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.lever_on, o);
            execAction = true;
        }
    }
}