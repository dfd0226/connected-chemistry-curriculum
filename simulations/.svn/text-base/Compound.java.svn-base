package view;

import processing.core.*;
import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

public class Compound {
	PApplet parent;
	// We need to keep track of a Body and a radius
	Body body;
	float r;
	PBox2D box2d;
	int col;
	String file;

	Compound(float x, float y, String compoundName, PBox2D box2d_, PApplet parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		r = 25;
		file = compoundName;
		// This function puts the particle in the Box2d world
		makeBody(x,y,r);
		body.setUserData(this);

		col = parent.color(175);
	}

	// This function removes the particle from the box2d world
	void killBody() {
		box2d.destroyBody(body);
	}

	// Change color when hit
	void change(double tEMPMOD) {
		if(tEMPMOD > 1) {
			col = parent.color(255,0,0);
		}
		else if(tEMPMOD < 1) {
			col = parent.color(0,0,255);
		}
		else {
		}
		body.setLinearVelocity(body.getLinearVelocity().mul((float)tEMPMOD));
	}

	// Is the particle ready for deletion?
	boolean done() {
		// Let's find the screen position of the particle
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Is it off the bottom of the screen?
		if (pos.y > parent.height+r*2) {
			killBody();
			return true;
		}
		return false;
	}

	// 
	void display() {
		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();
		parent.pushMatrix();
		parent.translate(pos.x,pos.y);
		parent.rotate(a);
		parent.fill(col);
		parent.stroke(0);
		parent.strokeWeight(1);
		parent.ellipse(0,0,r*2,r*2);
		// Let's add a line so we can see the rotation
		parent.line(0,0,r,0);
		parent.popMatrix();
	}

	// Here's our function that adds the particle to the Box2D world
	void makeBody(float x, float y, float r) {
		// Define a body
		BodyDef bd = new BodyDef();
		// Set its position
		bd.position = box2d.coordPixelsToWorld(x,y);
		body = box2d.world.createBody(bd);

		// Make the body's shape a circle
		CircleDef cd = new CircleDef();
		cd.radius = box2d.scalarPixelsToWorld(r);
		cd.density = 1.0f;
		cd.friction = 0.0f;
		cd.restitution = 1.0f; // Restitution is bounciness
		body.createShape(cd);

		// Always do this at the end
		body.setMassFromShapes();

		// Give it a random initial velocity (and angular velocity)
		body.setLinearVelocity(new Vec2(parent.random(-10f,10f),parent.random(5f,10f)));
		//body.setAngularVelocity(random(-10,10));
	}
}
