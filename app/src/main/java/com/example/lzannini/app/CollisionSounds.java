package com.example.lzannini.app;

import android.util.SparseArray;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Sound;
import com.example.lzannini.app.activity.MenuActivity;
import com.example.lzannini.app.go.BallGO;
import com.example.lzannini.app.go.ButtonGO;
import com.example.lzannini.app.go.CageGO;
import com.example.lzannini.app.go.CannonBallGO;
import com.example.lzannini.app.go.PortalGO;
import com.example.lzannini.app.go.PulleyPlatformGO;

/**
 * Created by mfaella on 04/03/16.
 */
public class CollisionSounds {
    private static Sound dumbSound;
    private static Sound ballCrashSound;
    private static Sound portalSound;

    private static SparseArray<Sound> map;

    private static int myHash(Class<?> a, Class<?> b)
    {
        return a.hashCode() ^ b.hashCode();
    }

    public static void init(Audio audio)
    {
        dumbSound = audio.newSound("balls_bump.wav");
        ballCrashSound = audio.newSound("glass_break_gameOver.wav");
        portalSound = audio.newSound("portal.wav");

        map = new SparseArray<>();
        map.put(myHash(BallGO.class, CannonBallGO.class), dumbSound);
        map.put(myHash(BallGO.class, ButtonGO.class), dumbSound);
        map.put(myHash(BallGO.class, PulleyPlatformGO.class), dumbSound);
        map.put(myHash(BallGO.class, CageGO.class), ballCrashSound);
        map.put(myHash(BallGO.class, PortalGO.class), portalSound);
    }

    public static Sound getSound(Class<?> a, Class<?> b) {
        if (!MenuActivity.isSoundEnabled) {
            return null;
        }
        int hash = myHash(a, b);
        return map.get(hash);
    }
}
