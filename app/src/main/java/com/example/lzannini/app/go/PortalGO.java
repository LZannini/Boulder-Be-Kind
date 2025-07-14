package com.example.lzannini.app.go;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.lzannini.app.GameObject;
import com.example.lzannini.app.GameWorld;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.example.lzannini.R;

public class PortalGO extends GameObject {

    private static final float width = 28f, height = 14f, density = 0.5f;
    private static float screen_semi_width, screen_semi_height;
    private static int INSTANCE_COUNT = 0;

    private final boolean isUp;
    private final Canvas canvas;
    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private Bitmap bitmap;
    public static boolean warp_flag = false;

    public PortalGO(GameWorld gw, float x, float y, boolean isUp) {
        super(gw);

        INSTANCE_COUNT++;
        this.isUp = isUp;
        this.canvas = new Canvas(gw.buffer);

        float boxWidth = width / gw.toPixelsXLength(1);
        float boxHeight = height / gw.toPixelsYLength(1);

        this.screen_semi_width = gw.toPixelsXLength(boxWidth) / 2;
        this.screen_semi_height = gw.toPixelsYLength(boxHeight) / 2;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.staticBody);

        this.body = gw.world.createBody(bdef);
        this.name = "Portal" + INSTANCE_COUNT;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth / 2, boxHeight / 2);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        fixturedef.setFriction(0.1f);
        fixturedef.setRestitution(0.4f);
        fixturedef.setDensity(density);
        body.createFixture(fixturedef);

        fixturedef.delete();
        bdef.delete();
        box.delete();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;

        if (isUp) {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.portal_up, o);
        } else {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.portal_down, o);
        }

        src.set(0, 0, 16, 8);
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

    public boolean getIsUp() {
        return isUp;
    }

}
