package com.example.lzannini.app;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;

/**
 *
 * Created by mfaella on 27/02/16.
 */
public class MyRevoluteJoint
{
    Joint joint;

    public MyRevoluteJoint(GameWorld gw, Body a, Body b)
    {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(-1f, -1f);
        jointDef.setLocalAnchorB(-1f, -1f);
        // add friction
        jointDef.setEnableMotor(true);
        jointDef.setMotorSpeed(1.5f);
        jointDef.setMaxMotorTorque(80f);
        joint = gw.world.createJoint(jointDef);

        jointDef.delete();
    }
}
