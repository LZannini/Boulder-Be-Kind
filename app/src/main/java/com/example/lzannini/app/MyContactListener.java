package com.example.lzannini.app;

import android.util.Log;

import com.badlogic.androidgames.framework.Sound;
import com.example.lzannini.app.go.BallGO;
import com.example.lzannini.app.go.BoxGO;
import com.example.lzannini.app.go.Button2GO;
import com.example.lzannini.app.go.ButtonGO;
import com.example.lzannini.app.go.CageGO;
import com.example.lzannini.app.go.CannonBallGO;
import com.example.lzannini.app.go.EnclosureGO;
import com.example.lzannini.app.go.PortalGO;
import com.example.lzannini.app.go.SmallBallGO;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by mfaella on 01/03/16.
 */
public class MyContactListener extends ContactListener {

    private Collection<Collision> cache = new HashSet<>();

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /** Warning: this method runs inside world.step
     *  Hence, it cannot change the physical world.
     */

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA(),
                fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        GameObject a = (GameObject) userdataA,
                b = (GameObject) userdataB;

        cache.add(new Collision(a, b));

        if ((a instanceof BallGO && b instanceof CannonBallGO) || (a instanceof CannonBallGO && b instanceof BallGO)) {
            CannonBallGO cannonball = (a instanceof CannonBallGO) ? (CannonBallGO) a : (CannonBallGO) b;
            cannonball.setToDestroy();
        }

        if ((a instanceof EnclosureGO && b instanceof CannonBallGO) || (a instanceof CannonBallGO && b instanceof EnclosureGO)) {
            CannonBallGO cannonball = (a instanceof CannonBallGO) ? (CannonBallGO) a : (CannonBallGO) b;
            cannonball.setToDestroy();
        }

        if ((a instanceof BallGO && b instanceof CageGO) || (a instanceof CageGO && b instanceof BallGO)) {
            BallGO ball = (a instanceof BallGO) ? (BallGO) a : (BallGO) b;
            CageGO cage = (a instanceof CageGO) ? (CageGO) a : (CageGO) b;
            ball.setToDestroy();
            Level.gameOver = true;
        }

        if ((a instanceof BallGO && b instanceof Button2GO) || (a instanceof Button2GO && b instanceof BallGO)) {
            Button2GO button = (a instanceof Button2GO) ? (Button2GO) a : (Button2GO) b;
            button.push();
        }

        if ((a instanceof SmallBallGO && b instanceof Button2GO) || (a instanceof Button2GO && b instanceof SmallBallGO)) {
            Button2GO button = (a instanceof Button2GO) ? (Button2GO) a : (Button2GO) b;
            button.push();
            if (!BoxGO.explode)
                BoxGO.explode = true;
        }

        if ((a instanceof BallGO && b instanceof PortalGO) || (a instanceof PortalGO && b instanceof BallGO)) {
            PortalGO portal = (a instanceof PortalGO) ? (PortalGO) a : (PortalGO) b;
            BallGO ball = (a instanceof BallGO) ? (BallGO) a : (BallGO) b;
            if (!portal.getIsUp()) {
                ball.setToDestroy();
                PortalGO.warp_flag = true;
            }

        }

        if ((a instanceof ButtonGO && b instanceof BallGO) || (a instanceof BallGO && b instanceof ButtonGO)) {
            ButtonGO button = (a instanceof ButtonGO) ? (ButtonGO) a : (ButtonGO) b;
            BallGO ball = (a instanceof BallGO) ? (BallGO) a : (BallGO) b;
            button.push();
            ball.setToDestroy();
            Level.puzzleCompleted = true;
        }
        Sound sound = CollisionSounds.getSound(a.getClass(), b.getClass());
        if (sound!=null)
            sound.play(0.7f);
        Log.d("MyContactListener", "contact bwt " + a.name + " and " + b.name);
    }
}

