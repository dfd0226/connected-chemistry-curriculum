/**
 * 
 */
package simulations.models;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import simulations.PBox2D;

/**
 * @author Qin Li
 * Class DistanceJointWrap is a wrapper of Pbox2D DistanceJoint class
 * Added copy constructor and virtual DistanceJoint on the basis of DistanceJoint class
 */
public class DistanceJointWrap {
	
	private DistanceJoint dj;
	private Body bodyA;
	private Body bodyB;
	private float length;
	private float frequency;   //Frequency decides bounciness, the larger the bouncier
	private float dampingRatio; 
	private boolean isActive;
	

	public DistanceJointWrap(Body b1,Body b2, float len, float fre, float dam)
	{
		Initialization(b1,b2,len,fre,dam);
		isActive = true;
	}
	public DistanceJointWrap( DistanceJointDef djf)
	{
		Initialization(djf.bodyA, djf.bodyB, djf.length, djf.frequencyHz, djf.dampingRatio);
		isActive = true;
	}
	
	private void Initialization(Body bodyA2, Body bodyB2, float length2,
			float frequencyHz, float dampingRatio2) {
		// TODO Auto-generated method stub
		DistanceJointDef djd = new DistanceJointDef();
		bodyA = bodyA2;
		djd.bodyA = bodyA;
		
		bodyB = bodyB2;
		djd.bodyB = bodyB;
		
		length = length2;
		djd.length = length;
		
		frequency = frequencyHz;
		djd.frequencyHz = frequency;
		
		dampingRatio = dampingRatio2;
		djd.dampingRatio = dampingRatio;

		dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		isActive = true;
	}
	public DistanceJointWrap( DistanceJointWrap djw , boolean active)
	{
		
		DistanceJointDef djd = new DistanceJointDef();
		
		bodyA = djw.getBodyA();
		djd.bodyA = bodyA;
		
		bodyB = djw.getBodyB();
		djd.bodyB = bodyB;
		
		length = djw.getLength();
		djd.length = length;
		
		frequency = djw.getFrequency();
		djd.frequencyHz = frequency;
		
		dampingRatio = djw.getDampingRatio();
		djd.dampingRatio = dampingRatio;
		
		if(active)
		{
			isActive = true;
			dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		}
		else
		{
			isActive = false;
		}
	}
	
	
	public Body getBodyA()
	{
		return bodyA;
	}
	
	public Body getBodyB()
	{
		return bodyB;
	}
	
	public float getLength()
	{
		return length;
	}
	
	
	public void getReactionForce(float inv_dt, Vec2 argOut) {
		dj.getReactionForce(inv_dt, argOut);
	}
	
	public float getFrequency()
	{
		return frequency;
	}
	public float getDampingRatio()
	{
		return dampingRatio;
	}
	public boolean IsActive()
	{
		return isActive;
	}
	public void destroy()
	{
		PBox2D.world.destroyJoint(dj);
	}

}
