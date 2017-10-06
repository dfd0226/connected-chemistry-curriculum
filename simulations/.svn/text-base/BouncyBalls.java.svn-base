package view;

import java.util.ArrayList;

import pbox2d.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import p5.Area;

public class BouncyBalls extends Area {
	private static final long serialVersionUID = 4297088161926072300L;

	// A reference to our box2d world
	PBox2D box2d;

	// An ArrayList of particles that will fall on the surface
	ArrayList<Compound> particles;
	// A list we'll use to track fixed objects
	ArrayList<Boundary> boundaries;

	private double TEMP_MOD = 0;

	public void setup() {
		size(400,300);
		//smooth();

		// Initialize box2d physics and create the world
		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0.0f,0.0f);
		// Turn on collision listening!
		box2d.listenForCollisions();

		// Create the empty list
		particles = new ArrayList<Compound>();
		boundaries = new ArrayList<Boundary>();

		// Add a bunch of fixed boundaries
		boundaries.add(new Boundary(width/2,height,width,10, color(0,0,0), box2d, this));
		boundaries.add(new Boundary(width/2,0,width,10, color(0,0,0), box2d, this));
		boundaries.add(new Boundary(0,height/2,10,height, color(0,0,0), box2d, this));
		boundaries.add(new Boundary(width,height/2,10,height, color(0,0,0), box2d, this));

	}

	public void draw() {
		if(TEMP_MOD > 1) {
			boundaries.get(0).changeColor(color(255,0,0));
		}
		else if(TEMP_MOD < 1) {
			boundaries.get(0).changeColor(color(0,0,255));
		}
		else {
			boundaries.get(0).changeColor(color(0,0,0));
		}
		background(255);

		// We must always step through time!
		box2d.step();


		// Look at all particles
		for (int i = particles.size()-1; i >= 0; i--) {
			Compound p = particles.get(i);
			p.display();
			// Particles that leave the screen, we delete them
			// (note they have to be deleted from both the box2d world and our list
			if (p.done()) {
				particles.remove(i);
			}
		}
		// Display all the boundaries
		for (Boundary wall: boundaries) {
			wall.display();
		}
	}
	
	/*
	 * Function to create compounds from outside the PApplet
	 */
	public void addCompound( float x, float y, String compoundName) {
		particles.add(new Compound(x,y,compoundName, box2d, this));
	}
	
	public void mousePressed() {
		particles.add(new Compound(mouseX,mouseY,"Default", box2d, this));
	}
	// Collision event functions!
	public void addContact(ContactPoint cp) {
		// Get both shapes
		Shape s1 = cp.shape1;
		Shape s2 = cp.shape2;
		// Get both bodies
		Body b1 = s1.getBody();
		Body b2 = s2.getBody();
		// Get our objects that reference these bodies
		Object o1 = b1.getUserData();
		Object o2 = b2.getUserData();

		// What class are they?  Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		// If object 1 is a Box, then object 2 must be a particle
		// Note we are ignoring particle on particle collisions
		if (c1.contains("Boundary") && ((Boundary)o1).equals(boundaries.get(0))) {
			Compound p = (Compound) o2;
			p.change(TEMP_MOD);
		} 
		// If object 2 is a Box, then object 1 must be a particle
		else if (c2.contains("Boundary")&& ((Boundary)o2).equals(boundaries.get(0))) {
			Compound p = (Compound) o1;
			p.change(TEMP_MOD);
		}
	}


	// Contacts continue to collide - i.e. resting on each other
	public void persistContact(ContactPoint cp) {
	}

	// Objects stop touching each other
	public void removeContact(ContactPoint cp) {
	}

	// Contact point is resolved into an add, persist etc
	public void resultContact(ContactResult cr) {
	}

	public void setBotTemp(int value) {
		TEMP_MOD = (double)((double)value/128.0);
	}
}
