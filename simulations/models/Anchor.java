package simulations.models;

import java.util.ArrayList;


import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import data.State;

import simulations.P5Canvas;
import simulations.PBox2D;

/**
 * @author Qin Li
 * Class Anchor is used to define anchors, a fixed point to which one or more molecules connect with a Distancejoint
 */
public class Anchor {
	
	private float x;
	private float y;
	private float worldX;
	private float worldY;
	private PBox2D box2d;
	private P5Canvas p5Canvas;
	private BodyDef bd;
	public Body body;
	public ArrayList<DistanceJointWrap> compoundJoint;
	
	public Anchor(float xp, float yp,PBox2D box2d_,P5Canvas parent_)
	{
		this.x = xp;
		this.y = yp;
		box2d = box2d_;
		p5Canvas = parent_;
		float angle = 0;
		createBody(x, y,angle);
		compoundJoint = new ArrayList<DistanceJointWrap>();
	
	}
	
	private void createBody(float xp, float yp, float angle)
	{
		bd = new BodyDef();
		bd.type = BodyType.STATIC;
       bd.position.set(box2d.coordPixelsToWorld(new Vec2(x, y)));
       bd.angle = angle;
		// This infinitive loop fix nullPointerException because
		// box2d.createBody(bd) may create a null body
		body = box2d.createBody(bd);
		while (body == null) {
			body = box2d.createBody(bd);
		}
		body.setAngularVelocity(0);
		body.setUserData(this);
	}
	
	public void destroy() {
		box2d.destroyBody(body);
		body.m_world = null;
	}
	
	//Get position in world coordinates
	public Vec2 getPosition() {
		return body.getPosition();
	}
	
	public void setPosition(float xp, float yp)
	{

		if (p5Canvas.isDragging() ) {
			float xx = worldX + PBox2D.scalarPixelsToWorld(xp);
			float yy = worldY - PBox2D.scalarPixelsToWorld(yp);
			Vec2 v = new Vec2(xx, yy);
			body.setTransform(v, body.getAngle());
			body.setAngularVelocity(0);
		} else {
			worldX = body.getPosition().x;
			worldY = body.getPosition().y;
		}
	}
	
	//Move anchors with a specified vector
	public void move(float xVec, float yVec)
	{
		Vec2 move = box2d.vectorPixelsToWorld(new Vec2(xVec,yVec));
		Vec2 pos = new Vec2(body.getPosition());
		pos.addLocal(move);
		body.setTransform(pos, body.getAngle());
	}

}
