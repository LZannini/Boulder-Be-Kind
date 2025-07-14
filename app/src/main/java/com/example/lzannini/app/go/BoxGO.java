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
import com.google.fpl.liquidfun.Vec2;

public class BoxGO extends GameObject {
    private static final float width = 2.75f, height = 2.75f;
    private static int instances = 0;
    private boolean isMovable;
    private boolean isVisible;
    private final Canvas canvas;
    private final float screen_semi_width, screen_semi_height;
    public static boolean explode = false;

    public BoxGO(GameWorld gw, float x, float y) {
        super(gw);

        instances++;

        this.canvas = new Canvas(gw.buffer);
        this.screen_semi_width = gw.toPixelsXLength(width) / 2;
        this.screen_semi_height = gw.toPixelsYLength(height) / 2;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gw.world.createBody(bdef);
        body.setSleepingAllowed(true);
        this.name = "Box" + instances;
        body.setUserData(this);
        isMovable = true;
        isVisible = true;

        PolygonShape box = new PolygonShape();
        box.setAsBox(width / 2, height / 2);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        fixturedef.setFriction(1f);
        fixturedef.setRestitution(0.2f);
        body.createFixture(fixturedef);

        fixturedef.delete();
        bdef.delete();
        box.delete();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.box, o);
        src.set(0, 0, 16, 16);
    }

    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private Bitmap bitmap;

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (isVisible) {
            canvas.save();
            dest.left = x - screen_semi_width;
            dest.bottom = y + screen_semi_height;
            dest.right = x + screen_semi_width;
            dest.top = y - screen_semi_height;
            canvas.drawBitmap(bitmap, src, dest, null);
            canvas.restore();
        }
    }

    public boolean getIsMovable() {
        return isMovable;
    }

    public void setIsMovable(boolean b) {
        isMovable = b;
    }

    public void setVisibleOff() {
        isVisible = false;
    }

    public void setMovableOff() {
        isMovable = false;
        body.setGravityScale(1f);
    }
}
