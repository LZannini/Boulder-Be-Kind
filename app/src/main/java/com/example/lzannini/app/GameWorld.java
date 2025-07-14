package com.example.lzannini.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.example.lzannini.R;
import com.example.lzannini.app.activity.GameActivity;
import com.example.lzannini.app.activity.MenuActivity;
import com.example.lzannini.app.go.BalanceGO;
import com.example.lzannini.app.go.BallGO;
import com.example.lzannini.app.go.BoxGO;
import com.example.lzannini.app.go.BridgeGO;
import com.example.lzannini.app.go.CageGO;
import com.example.lzannini.app.go.CannonBallGO;
import com.example.lzannini.app.go.ExplosionGO;
import com.example.lzannini.app.go.HammerGO;
import com.example.lzannini.app.go.LeverGO;
import com.example.lzannini.app.go.PortalGO;
import com.example.lzannini.app.go.PulleyPlatformGO;
import com.example.lzannini.app.go.WallGO;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The game objects and the viewport.
 *
 * Created by mfaella on 27/02/16.
 */
public class GameWorld {
    // Rendering
    final static int bufferWidth = 400, bufferHeight = 600;
    public Bitmap buffer;
    private Canvas canvas;
    List<GameObject> objects;
    public World world;
    final Box physicalSize, screenSize, currentView;
    private MyContactListener contactListener;
    private TouchConsumer touchConsumer;
    private Bitmap bitmap;
    private Bitmap background;

    private boolean boxExploded = false;

    private TouchHandler touchHandler;
    private static int level = 1;
    // Particles
    public ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    private static final float TIME_STEP = 1 / 50f; // 50 fps
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;
    private final GameActivity activity;
    private float boxX, boxY;

    public GameWorld(Box physicalSize, Box screenSize, GameActivity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 9.81f);  // gravity vector
        this.boxX = this.boxY = 0;

        this.currentView = physicalSize;

        // The particle system
        ParticleSystemDef psysdef = new ParticleSystemDef();
        this.particleSystem = world.createParticleSystem(psysdef);
        particleSystem.setRadius(PARTICLE_RADIUS);
        particleSystem.setMaxParticleCount(MAXPARTICLECOUNT);
        psysdef.delete();

        // stored to prevent GC
        contactListener = new MyContactListener();
        world.setContactListener(contactListener);

        background = BitmapFactory.decodeResource(activity.getResources(), R.drawable.background_game);

        touchConsumer = new TouchConsumer(this);

        this.objects = new ArrayList<>();
        this.canvas = new Canvas(buffer);
    }

    public void resetGame() {
        Level.countdownOver = false;
        Level.isStarted = false;
        Level.puzzleCompleted = false;
        Level.isHammerTaken = false;
        Level.levelCompleted = false;
        Level.gameOver = false;
        level = 1;
    }

    public Bitmap getBitmapBuffer() {
        return this.buffer;
    }

    public synchronized GameObject addGameObject(GameObject obj)
    {
        objects.add(obj);
        return obj;
    }

    public World getWorld() {
        return this.world;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public synchronized void addParticleGroup(GameObject obj)
    {
        objects.add(obj);
    }

    // To distance sounds from each other
    private long timeOfLastSound = 0;

    public synchronized void update(float elapsedTime) {
        if (!Level.isStarted) {}
        else {
        // advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

        // Handle collisions
        handleCollisions(contactListener.getCollisions());

        if (Level.puzzleCompleted) {
            if (level == 3)
                addGameObject(new HammerGO(this, 7.75f, -5));
            else if (level == 4)
                addGameObject(new HammerGO(this, -1.5f, -8.25f));
            else
                addGameObject(new HammerGO(this, 0, -3));
            Level.puzzleCompleted = false;
        }

        if (Level.gameOver) {
            level = -1;
            activity.runOnUiThread(() -> activity.pauseButton.setVisibility(android.view.View.INVISIBLE));
            destroyAll();
            Level.setupGameOver(this);
            Level.gameOver = false;
        }

        if (Level.levelCompleted) {
            activity.runOnUiThread(() -> activity.pauseButton.setVisibility(View.INVISIBLE));
            Level.levelCompleted = false;
            Level.isStarted = false;
            level++;
            Iterator<GameObject> iterator = objects.iterator();
            while (iterator.hasNext()) {
                GameObject go = iterator.next();
                if (!(go instanceof CageGO)) {
                    world.destroyBody(go.body);
                    iterator.remove();      }
            }
            setupNextLevel();
        }

        if (Level.isStarted && Level.countdownOver) {
            if (level == 1)
                Level.setupLevel1(this);
            if (level == 2)
                Level.setupLevel2(this);
            else if (level == 3)
                Level.setupLevel3(this);
            else if (level == 4)
                Level.setupLevel4(this);
            activity.runOnUiThread(() -> {
                activity.pauseButton.setVisibility(View.VISIBLE);
                activity.pauseButton.requestLayout();
                activity.pauseButton.invalidate();
            });

            Level.countdownOver = false;
        }

        if (BoxGO.explode) {
            if (level == 4) {
                destroyBox();
            }
            BoxGO.explode = false;
        }

        if (PortalGO.warp_flag) {
            if (level == 4)
                addGameObject(new BallGO(this, -6.35f, -2f));
            Iterator<GameObject> iterator = objects.iterator();
            while (iterator.hasNext()) {
                GameObject go = iterator.next();
                if ((go instanceof BoxGO && ((BoxGO) go).getIsMovable()))
                    ((BoxGO) go).setIsMovable(false);
            }
            PortalGO.warp_flag = false;
        }

        if (LeverGO.execAction) {
            if (level == 1) {
                addGameObject(new CannonBallGO(this, -6, 0));
                addGameObject(new ExplosionGO(this, -6.5f, 0.5f));
            }
            else if (level == 2) {
                addGameObject(new ExplosionGO(this, 6.75f, 6f));
                addGameObject(new ExplosionGO(this, 0f, -8.5f));
                addGameObject(new ExplosionGO(this, -2.5f, -1.25f));
                destroyWall();
            }
            else if (level == 4) {
                addGameObject(new ExplosionGO(this, 5.31f, 5.7f));
                addGameObject(new ExplosionGO(this, 2.375f, 5.5f));
                addGameObject(new ExplosionGO(this, -3.5f, 4.65f));
                addGameObject(new ExplosionGO(this, -1.75f, 0f));
                destroyWall();
                destroyBox();
            }
            LeverGO.execAction = false;
        }

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents())
            touchConsumer.consumeTouchEvent(event);

            Iterator<GameObject> iterator = objects.iterator();
            while (iterator.hasNext()) {
                GameObject obj = iterator.next();

                if (obj instanceof ExplosionGO) {
                    ExplosionGO explosion = (ExplosionGO) obj;
                    long elapsed = SystemClock.uptimeMillis() - explosion.getStartTime();

                    if (elapsed > 1000) {
                        iterator.remove();
                        world.destroyBody(obj.body);
                    } else
                        explosion.decreaseAlpha(5);
                }

                if (obj instanceof CannonBallGO && ((CannonBallGO) obj).isToDestroy()) {
                    Log.d("GameWorld", "Destroying CannonBall");
                    world.destroyBody(obj.body);
                    iterator.remove();
                }

                if (obj instanceof BallGO && ((BallGO) obj).isToDestroy()) {
                    Log.d("GameWorld", "Destroying Ball");
                    world.destroyBody(obj.body);
                    iterator.remove();
                }

            }
        }
    }

    public static int getLevel() {
        return level;
    }

    public synchronized void render()
    {
        canvas.drawBitmap(background, null, new Rect(0, 0, bufferWidth, bufferHeight), null);
        for (GameObject obj: objects)
            obj.draw(buffer);
    }

    public synchronized void setupNextLevel() {
        Level gameLevel = new Level(this);
        if (level == 1) gameLevel.initLevel(this, 1);
        else if (level == 2) gameLevel.initLevel(this, 2);
        else if (level == 3) gameLevel.initLevel(this, 3);
        else if (level == 3) gameLevel.initLevel(this, 4);
        else gameLevel.initLevel(this, -1);
    }

    private void handleCollisions(Collection<Collision> collisions) {
        for (Collision event: collisions) {
            Sound sound = CollisionSounds.getSound(event.a.getClass(), event.b.getClass());
            if (sound!=null) {
                long currentTime = System.nanoTime();
                if (currentTime - timeOfLastSound > 500_000_000) {
                    timeOfLastSound = currentTime;
                    sound.play(0.7f);
                }
            }
        }

    }

    // Conversions between screen coordinates and physical coordinates

    public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/screenSize.width); }
    public float toMetersY(float y) { return currentView.ymin + y * (currentView.height/screenSize.height); }

    public float toPixelsX(float x) { return (x-currentView.xmin)/currentView.width*bufferWidth; }
    public float toPixelsY(float y) { return (y-currentView.ymin)/currentView.height*bufferHeight; }

    public float toPixelsXLength(float x)
    {
        return x/currentView.width*bufferWidth;
    }
    public float toPixelsYLength(float y)
    {
        return y/currentView.height*bufferHeight;
    }

    public synchronized void setGravity(float x, float y)
    {
        world.setGravity(x, y);
    }

    @Override
    public void finalize()
    {
        world.delete();
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }

    public GameActivity getActivity() {
        return this.activity;
    }

    public void destroyWall() {
        Iterator<GameObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof WallGO && ((WallGO) obj).isToDestroy()) {
                Log.d("GameWorld", "Destroying Wall");
                world.destroyBody(obj.body);
                iterator.remove();
            }
        }
    }

    public void destroyBox() {
        if (boxExploded)
            return;
        Iterator<GameObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof BoxGO) {
                Vec2 position = obj.body.getPosition();
                this.boxX = position.getX();
                this.boxY = position.getY();
                Log.d("GameWorld", "Destroying Box");
                world.destroyBody(obj.body);
                iterator.remove();
            }
        }
        addGameObject(new ExplosionGO(this, this.boxX, this.boxY));
        BoxGO.explode = false;
        boxExploded = true;
    }

    public void destroyCage() {
        Iterator<GameObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof CageGO) {
                Log.d("GameWorld", "Destroying Cage");
                world.destroyBody(obj.body);
                iterator.remove();
            }
        }
    }

    public void destroyAll() {
        Iterator<GameObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof BridgeGO && Level.gameOver) {
                ((BridgeGO) obj).setMovableOff();
                continue;
            }
            if (obj instanceof BalanceGO && Level.gameOver) {
                ((BalanceGO) obj).setMovableOff();
                continue;
            }
            if (obj instanceof BoxGO && Level.gameOver) {
                ((BoxGO) obj).setMovableOff();
                ((BoxGO) obj).setVisibleOff();
                continue;
            }
            if (obj instanceof PulleyPlatformGO && Level.gameOver) {
                ((PulleyPlatformGO) obj).setMovableOff();
                continue;
            }
            Log.d("GameWorld", "Destroying All");
            world.destroyBody(obj.body);
            iterator.remove();
        }
    }

    public void activateBridge() {
        Iterator<GameObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (obj instanceof BridgeGO)
                ((BridgeGO) obj).setMovableOn();
        }
    }

    public void goToMenu() {
        Intent intent = new Intent(activity, MenuActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
