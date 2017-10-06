package view;

import processing.core.*;
import pbox2d.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.*;

public class Boundary {

	private PApplet parent;
	// A boundary is a simple rectangle with x,y,width,and height
	float x;
	float y;
	float w;
	float h;
	int myColor;
	// But we also have to make a body for box2d to know about it
	Body b;
	PBox2D box2d;

	Boundary(float x_,float y_, float w_, float h_, int color_, PBox2D box2d_, PApplet parent_) {
		this.parent = parent_;
		this.box2d = box2d_;
		x = x_;
		y = y_;
		w = w_;
		h = h_;
		myColor = color_;

		// Figure out the box2d coordinates
		float box2dW = box2d.scalarPixelsToWorld(w/2);
		float box2dH = box2d.scalarPixelsToWorld(h/2);
		Vec2 center = new Vec2(x,y);

		// Define the polygon
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(box2dW, box2dH);
		sd.density = 0;    // No density means it won't move!
		sd.friction = 0.3f;

		// Create the body
		BodyDef bd = new BodyDef();
		bd.position.set(box2d.coordPixelsToWorld(center));
		b = box2d.createBody(bd);
		b.createShape(sd);
		b.setUserData(this);
	}

	void changeColor(int newColor) {
		myColor = newColor;
	}
	// Draw the boundary, if it were at an angle we'd have to do something fancier
	void display() {
		parent.fill(myColor);
		parent.stroke(myColor);
		parent.rectMode(PConstants.CENTER);
		parent.rect(x,y,w,h);
	}

}
