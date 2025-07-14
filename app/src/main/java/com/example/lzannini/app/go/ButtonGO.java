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
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.example.lzannini.R;

public class ButtonGO extends GameObject {

    private static final float width = 3.33f, height = 1, density = 0.5f;
    private static float screen_semi_width, screen_semi_height;
    private static int INSTANCE_COUNT = 0;
    private boolean isPushed;
    private final Canvas canvas;
    private final Paint paint = new Paint();

    public ButtonGO(GameWorld gw, float x, float y)
    {
        super(gw);

        INSTANCE_COUNT++;

        this.canvas = new Canvas(gw.buffer);
        this.screen_semi_width = gw.toPixelsXLength(width)/2;
        this.screen_semi_height = gw.toPixelsYLength(height)/2;
        this.isPushed = false;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.staticBody);

        this.body = gw.world.createBody(bdef);
        this.name = "Button" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(width / 2, height / 2);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        fixturedef.setFriction(0.1f);
        fixturedef.setRestitution(0.4f);
        fixturedef.setDensity(density);
        body.createFixture(fixturedef);

        int green = (int)(255*Math.random());
        int color = Color.argb(200, 255, green, 0);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        fixturedef.delete();
        bdef.delete();
        box.delete();

        Fixture f = body.getFixtureList();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.button, o);
        bitmapOff = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.button_pressed, o);

        src.set(0, 0, 10, 3);
    }

    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private Bitmap bitmap, bitmapOff;

    public void push() {
        if (!isPushed) {
            isPushed = true;
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inScaled = false;
            this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.button_pressed, o);
        }
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }
}
