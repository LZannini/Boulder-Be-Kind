package com.example.lzannini.app;
import android.content.Context;

import com.example.lzannini.app.go.BalanceGO;
import com.example.lzannini.app.go.BallGO;
import com.example.lzannini.app.go.BaseGO;
import com.example.lzannini.app.go.BoxGO;
import com.example.lzannini.app.go.BridgeGO;
import com.example.lzannini.app.go.Button2GO;
import com.example.lzannini.app.go.ButtonGO;
import com.example.lzannini.app.go.CageGO;
import com.example.lzannini.app.go.CannonGO;
import com.example.lzannini.app.go.EnclosureGO;
import com.example.lzannini.app.go.GuiGO;
import com.example.lzannini.app.go.InclinedFloorGO;
import com.example.lzannini.app.go.LeverGO;
import com.example.lzannini.app.go.PortalGO;
import com.example.lzannini.app.go.PulleyPlatformGO;
import com.example.lzannini.app.go.SmallBallGO;
import com.example.lzannini.app.go.TriangleBaseGO;
import com.example.lzannini.app.go.WallGO;

public class Level {

    public static boolean isStarted = false;
    private static Box physicalSize;
    private Context context;
    public static boolean puzzleCompleted = false;
    public static boolean isHammerTaken = false;
    public static boolean levelCompleted = false;
    public static boolean gameOver = false;
    public static boolean countdownOver = false;

    public Level(GameWorld gw) { }

    public void initLevel(GameWorld gw, int level) {
        gw.addGameObject(new EnclosureGO(gw, -11, 11, -16, 16));
        isHammerTaken = false;
        addGUI(gw);
    }

    public static void setupLevel1(GameWorld gw) {
        gw.addGameObject(new BallGO(gw, -9f, -15));
        gw.addGameObject(new WallGO(gw, -3.5f, 3.5f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, -3.5f, 3.5f, 13f, 15f));
        gw.addGameObject(new InclinedFloorGO(gw, -10f, -1f, -10f, -8f));
        TriangleBaseGO triangleBase = new TriangleBaseGO(gw, 5f, 10f, -0.8325f, -3f);
        triangleBase.mirrorHorizontally();
        gw.addGameObject(triangleBase);
        gw.addGameObject(new CageGO(gw, 0, 10.5f));
        gw.addGameObject(new LeverGO(gw, -8.5f, -3f));
        gw.addGameObject(new CannonGO(gw, -7.5f, 1f));
        gw.addGameObject(new WallGO(gw, -9.5f, -7.5f, -2f, -2f, false));
        gw.addGameObject(new ButtonGO(gw, 6.5f, 7.5f));
        gw.addGameObject(new WallGO(gw, 4.25f, 8.75f, 8f, 8f, false));
        gw.addGameObject(new BaseGO(gw, 4.25f, 8.75f, 8f, 15f));
    }

    public static void setupLevel2(GameWorld gw) {
        gw.destroyCage();

        gw.addGameObject(new CageGO(gw, 0, 10.5f));
        gw.addGameObject(new WallGO(gw, -3.5f, 3.5f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, -3.5f, 3.5f, 13f, 15f));
        gw.addGameObject(new ButtonGO(gw, 6.75f, 12.5f));
        gw.addGameObject(new WallGO(gw, 4.5f, 9f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, 4.5f, 9f, 13f, 15f));
        gw.addGameObject(new BridgeGO(gw, -9.25f, -4.25f, 6.35f));
        gw.addGameObject(new WallGO(gw, -8f, -5.5f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, -8f, -5.5f, 13f, 15f));

        gw.addGameObject(new WallGO(gw, -9.675f, 9.675f, -12.75f, -12.75f, false));
        gw.addGameObject(new WallGO(gw, -9.5f, -9.5f, -12.75f, 6f, false));
        gw.addGameObject(new WallGO(gw, 9.5f, 9.5f, -12.75f, 6f, false));
        gw.addGameObject(new WallGO(gw, -9.675f, -2.5f, 6f, 6f, false));

        TriangleBaseGO base = new TriangleBaseGO(gw, -2.55f, -2f, 5.7825f, 6.2f);
        base.mirrorVertically();
        gw.addGameObject(base);

        TriangleBaseGO base2 = new TriangleBaseGO(gw, 2f, 2.55f, 5.7825f, 6.2f);
        base2.mirrorVertically();
        base2.mirrorHorizontally();
        gw.addGameObject(base2);

        gw.addGameObject(new WallGO(gw, 2.5f, 5f, 6f, 6f, false));
        gw.addGameObject(new WallGO(gw, 5f, 8.5f, 6f, 6f, true));
        gw.addGameObject(new WallGO(gw, 8.5f, 9.675f, 6f, 6f, false));
        gw.addGameObject(new BallGO(gw, -8f, -10.7f));

        gw.addGameObject(new InclinedFloorGO(gw, -9.5f, -2.5f, -9.5f, -8.5f));
        gw.addGameObject(new WallGO(gw, -2.6f, 2.5f, -8.5f, -8.5f, true));
        gw.addGameObject(new WallGO(gw, 7.8f, 9.5f, -8f, -8f, false));
        gw.addGameObject(new LeverGO(gw, 8.35f, -9.15f));

        gw.addGameObject(new WallGO(gw, -2.5f, -2.5f, -4.5f, 1.9f, true));
        gw.addGameObject(new InclinedFloorGO(gw, 2.5f, 8f, -4.5f, -8f));
        gw.addGameObject(new WallGO(gw, -6.5f,-2.45f, 1.7f, 1.7f,false));
        gw.addGameObject(new Button2GO(gw, -5f, 1.35f));
        gw.addGameObject(new InclinedFloorGO(gw, -9.5f, -5.5f, 4f, 6f));
    }

    public static void setupLevel3(GameWorld gw) {
        gw.destroyCage();

        gw.addGameObject(new CageGO(gw, -4, 10.5f));
        gw.addGameObject(new WallGO(gw, -0.5f, -7.5f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, -0.5f, -7.5f, 13f, 15f));
        gw.addGameObject(new ButtonGO(gw, 7.75f, 12.5f));
        gw.addGameObject(new WallGO(gw, 5.5f, 10f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, 5.5f, 10f, 13f, 15f));

        gw.addGameObject(new WallGO(gw, 5.5f, 5.5f, 5.5f, 15f, false));
        gw.addGameObject(new InclinedFloorGO(gw, -0.75f, 5.5f, 13f, 5.6f));
        gw.addGameObject(new BaseGO(gw, -0.5f, 5.4f, 13f, 15f));
        TriangleBaseGO triangleBase = new TriangleBaseGO(gw, -0.5f, 5.4f, 13f, 5.5f);
        triangleBase.mirrorHorizontally();
        gw.addGameObject(triangleBase);

        gw.addGameObject(new InclinedFloorGO(gw, -3.5f, 10f, -8.75f, -9.25f));
        gw.addGameObject(new WallGO(gw, -3.5f, -3.5f, -8.95f, -4.5f, false));
        gw.addGameObject(new BallGO(gw, 8.7f, -10.75f));
        gw.addGameObject(new BalanceGO(gw, -6.85f, -8.75f));
        gw.addGameObject(new InclinedFloorGO(gw, -10f, 0.25f, -2f, 0f));
        gw.addGameObject(new PulleyPlatformGO(gw, 7.75f, 2f, 2));
    }

    public static void setupLevel4(GameWorld gw) {
        gw.destroyCage();

        gw.addGameObject(new BallGO(gw, -9f, -11.5f));
        gw.addGameObject(new InclinedFloorGO(gw, -10f, 4f, -9.25f, -10f));
        gw.addGameObject(new PortalGO(gw, 7f, -8f, false));

        gw.addGameObject(new SmallBallGO(gw, -8f, -6.5f));

        gw.addGameObject(new WallGO(gw, -8.5f, 0, -6, -6, false));
        gw.addGameObject(new WallGO(gw, -8.5f, -8.5f, -6.2f, 3.55f, false));
        gw.addGameObject(new InclinedFloorGO(gw, -8.5f, -3.7f, 3.35f, 4f));
        gw.addGameObject(new WallGO(gw, 0, 0, -6.2f, 0.5f, false));
        gw.addGameObject(new BoxGO(gw, -1.75f,-1.1f));
        gw.addGameObject(new WallGO(gw, -3.3f, -0.2f, 0.5f, 0.5f, true));
        gw.addGameObject(new WallGO(gw, -0.2f, 3.5f, 0.5f, 0.5f, false));
        gw.addGameObject(new WallGO(gw, 3.5f, 3.5f, -1f, 0.5f, false));
        gw.addGameObject(new Button2GO(gw, 1.75f, 0.15f));
        gw.addGameObject(new WallGO(gw, 3.5f, 3.5f, 0.3f, 2.65f, false));
        gw.addGameObject(new PortalGO(gw, -6.35f, -4.75f, true));

        gw.addGameObject(new WallGO(gw, -3.5f, -3.5f, 3.8f,5.5f, true));
        gw.addGameObject(new WallGO(gw, 0f, 4.75f, 5.5f, 5.5f, true));
        gw.addGameObject(new WallGO(gw, 0f, 0f, 4.5f, 7.25f, false));
        gw.addGameObject(new WallGO(gw, -1f, 0.2f, 7.25f, 7.25f, false));

        gw.addGameObject(new CageGO(gw, -4, 10.5f));
        gw.addGameObject(new WallGO(gw, -0.5f, -7.5f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, -0.5f, -7.5f, 13f, 15f));
        gw.addGameObject(new ButtonGO(gw, 7.75f, 12.5f));
        gw.addGameObject(new WallGO(gw, 5.5f, 10f, 13f, 13f, false));
        gw.addGameObject(new BaseGO(gw, 5.5f, 10f, 13f, 15f));


        gw.addGameObject(new WallGO(gw, 5.31f, 5.31f, 2.5f, 9.2f, true));
        gw.addGameObject(new BaseGO(gw, -0.5f, 5.5f, 13f, 15f));
        TriangleBaseGO triangleBase = new TriangleBaseGO(gw, -0.75f, 5.5f, 13f, 9f);
        triangleBase.mirrorHorizontally();
        gw.addGameObject(triangleBase);

        gw.addGameObject(new LeverGO(gw, 6.5f, 1.7f));
        gw.addGameObject(new WallGO(gw, 5.5f, 7.5f, 2.7f, 2.7f, false));
    }

    public static void setupGameOver(GameWorld gw) {
        GuiGO gui = new GuiGO(gw);
        gui.setLevelCounter(gw.getLevel());
        gw.addGameObject(gui);
    }

    public void addGUI(GameWorld gw) {
        GuiGO gui = new GuiGO(gw);
        gui.setLevelCounter(gw.getLevel());
        gw.addGameObject(gui);
    }
}
