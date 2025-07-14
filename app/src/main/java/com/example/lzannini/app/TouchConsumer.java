package com.example.lzannini.app;
import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.example.lzannini.app.activity.MenuActivity;
import com.example.lzannini.app.go.BalanceGO;
import com.example.lzannini.app.go.BallGO;
import com.example.lzannini.app.go.BoxGO;
import com.example.lzannini.app.go.BridgeGO;
import com.example.lzannini.app.go.CageGO;
import com.example.lzannini.app.go.CannonBallGO;
import com.example.lzannini.app.go.HammerGO;
import com.example.lzannini.app.go.LeverGO;
import com.example.lzannini.app.go.PulleyPlatformGO;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.MouseJointDef;
import com.google.fpl.liquidfun.QueryCallback;

/**
 * Takes care of user interaction: pulls objects using a Mouse Joint.
 */
public class TouchConsumer {

    // keep track of what we are dragging
    private MouseJoint mouseJoint;
    private int activePointerID;
    private Fixture touchedFixture;

    private GameWorld gw;
    private QueryCallback touchQueryCallback = new TouchQueryCallback();

    // physical units, semi-side of a square around the touch point
    private final static float POINTER_SIZE = 0.5f;

    private class TouchQueryCallback extends QueryCallback
    {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    /**
        scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    */
    public TouchConsumer(GameWorld gw) {
        this.gw = gw;
    }

    public void consumeTouchEvent(Input.TouchEvent event)
    {
        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                consumeTouchDown(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                consumeTouchUp(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                consumeTouchMove(event);
                break;
        }
    }

    private void consumeTouchDown(Input.TouchEvent event) {
        int pointerId = event.pointer;

        if (mouseJoint != null) return;

        float x = gw.toMetersX(event.x), y = gw.toMetersY(event.y);

        Log.d("MultiTouchHandler", "touch down at " + x + ", " + y);

        touchedFixture = null;
        gw.world.queryAABB(touchQueryCallback, x - POINTER_SIZE, y - POINTER_SIZE, x + POINTER_SIZE, y + POINTER_SIZE);
        if (touchedFixture != null) {
            // From fixture to GO
            Body touchedBody = touchedFixture.getBody();
            Object userData = touchedBody.getUserData();


            if (userData != null) {
                if (userData instanceof LeverGO) {
                    LeverGO lever = (LeverGO) userData;
                    lever.press();
                }

                GameObject touchedGO = (GameObject) userData;
                activePointerID = pointerId;
                Log.d("MultiTouchHandler", "touched game object " + touchedGO.name);

                if (touchedGO instanceof HammerGO) {
                    Log.d("MultiTouchHandler", "Destroying Hammer");
                    if (MenuActivity.isSoundEnabled)
                        ((HammerGO) touchedGO).playHammerSound();
                    gw.world.destroyBody(touchedBody);
                    gw.objects.remove(touchedGO);
                    Level.isHammerTaken = true;
                    return;
                }

                if (touchedGO instanceof PulleyPlatformGO) {
                    if (MenuActivity.isSoundEnabled)
                        ((PulleyPlatformGO) touchedGO).playPulleySound();
                }

                if (touchedGO instanceof CageGO && Level.isHammerTaken) {
                    Log.d("MultiTouchHandler", "Breaking Cage");
                    CageGO cage = (CageGO) touchedGO;
                    cage.breakCage();
                    Level.levelCompleted = true;
                    return;
                }

                if (touchedGO instanceof BoxGO && !((BoxGO) touchedGO).getIsMovable()) {
                    if (mouseJoint != null && touchedFixture.getBody() == mouseJoint.getBodyB()) {
                        Log.d("TouchConsumer", "Releasing MouseJoint for non-movable BoxGO");
                        gw.world.destroyJoint(mouseJoint);
                        mouseJoint = null;
                        activePointerID = 0;
                    }
                    return;
                }

                if (touchedGO instanceof BallGO || touchedGO instanceof CannonBallGO)
                    return;

                if (touchedGO instanceof BridgeGO && !((BridgeGO) touchedGO).getIsActive())
                    return;

                //if (touchedGO instanceof BalanceGO)
                //    return;

                setupMouseJoint(x, y, touchedBody);
            }
        }
    }

    // Set up a mouse joint between the touched GameObject and the touch coordinates (x,y)
    private void setupMouseJoint(float x, float y, Body touchedBody) {
        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.setBodyA(touchedBody); // irrelevant but necessary
        mouseJointDef.setBodyB(touchedBody);
        mouseJointDef.setMaxForce(500 * touchedBody.getMass());
        mouseJointDef.setTarget(x, y);
        mouseJoint = gw.world.createMouseJoint(mouseJointDef);
    }

    private void consumeTouchUp(Input.TouchEvent event) {
        if (mouseJoint != null && event.pointer == activePointerID) {
            Log.d("MultiTouchHandler", "Releasing joint");
            gw.world.destroyJoint(mouseJoint);
            mouseJoint = null;
            activePointerID = 0;
        }
    }

    private void consumeTouchMove(Input.TouchEvent event) {
        float x = gw.toMetersX(event.x), y = gw.toMetersY(event.y);
        if (mouseJoint != null && event.pointer == activePointerID) {
            Body body = mouseJoint.getBodyB();
            Object userData = body.getUserData();

            if (userData instanceof GameObject) {
                if (userData instanceof BoxGO) {
                    BoxGO draggedGO = (BoxGO) userData;

                    if (!draggedGO.getIsMovable()) {
                        Log.d("TouchConsumer", "Dragged object is no longer movable, releasing joint.");
                        gw.world.destroyJoint(mouseJoint);
                        mouseJoint = null;
                        activePointerID = 0;
                        return;
                    }
                }
            }

            Log.d("MultiTouchHandler", "active pointer moved to " + x + ", " + y);
            mouseJoint.setTarget(x, y);
        }
    }

}
