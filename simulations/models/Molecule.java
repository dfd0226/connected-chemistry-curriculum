package simulations.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import processing.core.*;
import simulations.P5Canvas;
import simulations.PBox2D;


import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;

import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.PrismaticJoint;

import data.DBinterface;
import data.State;

import Util.ColorCollection;
import Util.Constants;
import Util.SVGReader;
import static data.State.*;

public class Molecule {
	
	public enum mState {Solid,Liquid,Gas;

	public static int valueOf(mState s) {
		return 0;
	}}
	// We need to keep track of a Body and a width and height
	public Body body;
	mState state;
	private ArrayList<Fixture> fixtures;
	private BodyDef bd;
	private PBox2D box2d;
	private P5Canvas p5Canvas;
	private PShape pShape = new PShape();
	private float pShapeW = 0f;
	private float pShapeH = 0f;
	public float[][] circles;
	public ArrayList<String> elementNames;
	public ArrayList<Integer> elementCharges;
	private String name;
	public float fric;
	public float res;
	private float shapeScale = 1;    //The scale parameter that changes the shape size
	private float displayScale = 1;  //The scale parameter that changes the display size

	private float xTmp;   //Temporary x to save x position while dragging, in world coordinates
	private float yTmp;   //Temporary x to save x position while dragging, in world coordinates
	private float minSize;
	private float maxSize;
	public boolean polarity;
	public boolean isHidden = false;
	public float freezingTem;
	public float boilingTem;
	private boolean enableAutoState = true;
	public float mass = 0;
	public float enthalpy [] = new float [3];
	public float entropy [] = new float [3];

	public Vec2 force = new Vec2(0, 0);
	public Vec2[] loc = new Vec2[20];
	public Vec2[] locWorld = new Vec2[20];
	public float[] gap = new float[20]; // Distance from a molecule`s top left
										// corner to its center
	public float[] a1 = new float[20];

	public float[] sumForceX;
	public float[] sumForceY;
	public float[] sumForceWaterX;
	public float[] sumForceWaterY;

	public float chargeRate = 1;
	public static float clRadius = 28f;
	public static float oRadius = 18.495f; // Oxygen Radius. This depends on SVG
											// file

	public int compoundJ = -1; // Index of molecule to which this molecule is
								// connecting
								// Only be used in Unit2
	public int otherJ = -1;
	public int CaOtherJ = -1;

	public ArrayList<DistanceJointWrap> compoundJoint = null; // Reference of
																// joints of
																// this molecule
	// public ArrayList<Molecule> compoundJointPair = null; //Reference of
	// molecules to which this molecule is connecting
	public PrismaticJoint compoundJoints2 = null; // is Used for Unit 2 set 7
	public DistanceJoint otherJoints = null;
	
	//private ElectronList electronList = null;
	
	//Neighbors information of this molecule, that is used to find react pairs
	public ArrayList<Molecule> neighbors = null;

	public float ionDis = 0; // Use to compute dissolve
	private boolean reactive = true; // Molecule can only react if this flag is
										// true
	//private boolean restitutionDampEnable = false;
	private int tableIndex = -1;
	private float ratioKE = 1;  //ratio that used to tune molecule speed with a given energy
							//Does not change with temperature
	
	private float transparency = 0.0f;  //Transparency of molecule, 1.0 means totally transparent
	private boolean displayPng = false; //If use png pictures
	private PImage pngSource = null;
	private String pngSourceName = new String();
	private String parentName = new String();

	/******************************************************************
	 * FUNCTION : Molecule() DESCRIPTION : Molecule Constructor
	 * 
	 * INPUTS : x (float), y (float), compoundName_ (String), box2d_ (PBox2D),
	 * parent_ (P5Canvas), angle (float) OUTPUTS: None
	 *******************************************************************/
	public Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float angle)
	{
		this(x,y,compoundName_,box2d_,parent_,angle,compoundName_);
	}
	public Molecule(float x, float y, String compoundName_, PBox2D box2d_,
			P5Canvas parent_, float angle,String svgFileName) {
		p5Canvas = parent_;
		box2d = box2d_;
		name = compoundName_;
		compoundJoint = new ArrayList<DistanceJointWrap>();
		neighbors = new ArrayList<Molecule>();
		// compoundJointPair = new ArrayList<Molecule>();
		fixtures = new ArrayList<Fixture>();

		String path = "resources/compoundsSvg/" + svgFileName + ".svg";
		try{
		pShape = p5Canvas.loadShape(path);
		pShapeW = pShape.width;
		pShapeH = pShape.height;
		}
		catch(Exception ex)
		{
			pShape =null;
			pShapeW = 0;
			pShapeH=0;
		}
		
		minSize = Math.min(pShapeW, pShapeH);
		setMaxSize(Math.max(pShapeW, pShapeH));
		polarity = p5Canvas.db.getCompoundPolarity(compoundName_);

		circles = SVGReader.getSVG(path);
		if(name.equals("Silver-Carbonate") )
		{
			elementNames = new ArrayList<String> ();
			elementNames.add("Silver");
			elementNames.add("Silver");
			elementNames.add("Carbonate");
		}
		else if(name.equals("Silver-Hydroxide"))
		{
			elementNames = new ArrayList<String> ();
			elementNames.add("Silver");
			elementNames.add("Hydroxide");
		}
		else
		elementNames = SVGReader.getNames();
		elementCharges = new ArrayList<Integer>();

		int numElement = elementNames.size();
		for (int i = 0; i < numElement; i++) {
			int charge = DBinterface.getElementCharge(elementNames.get(i));
			elementCharges.add(charge);
			if (elementNames.get(i).equals("Chloride"))
				elementNames.set(i, new String("Chlorine")) ;
			else if(elementNames.get(i).equals("Bromide"))
				elementNames.set(i, new String("Bromine")) ;
			mass+= DBinterface.getElementMass(elementNames.get(i))/1000;
		}
		sumForceX = new float[numElement];
		sumForceY = new float[numElement];
		sumForceWaterX = new float[numElement];
		sumForceWaterY = new float[numElement];
		freezingTem = DBinterface.getCompoundFreezingPointCelsius(name);
		boilingTem = DBinterface.getCompoundBoilingPointCelsius(name);
		
		setEnthalpy();
		setEntropy();
		
		// Identify specific situation
		if ((name.equals("Sodium-Ion") || name.equals("Potassium-Ion"))
				&& (p5Canvas.getUnit() == 2 && p5Canvas.getSet() != 7)) {
			circles[0][0] = 28;
		} else if (name.equals("Calcium-Ion")) {
			circles[0][0] = 28;
		}

		// Set up gap: distance from a molecule`s top left corner to its center
		for (int i = 0; i < numElement; i++) {
			float xx = circles[i][1] - pShapeW / 2;
			float yy = -(circles[i][2] - pShapeH / 2);
			gap[i] = (float) Math.sqrt(xx * xx + yy * yy);
			gap[i] = PBox2D.scalarPixelsToWorld(gap[i]);
			if (xx != 0)
				a1[i] = (float) (Math.atan(yy / xx));
			if (xx < 0)
				a1[i] += Math.PI;
		}

		setPropertyByHeat(true);
		createBody(x, y, angle);
		//electronList = new ElectronList(this);
	}

	/******************************************************************
	 * FUNCTION : destroy() DESCRIPTION : Molecule Destroy function, return all
	 * other molecules to which this molecule is connecting
	 * 
	 * INPUTS : None OUTPUTS: ArrayList<Molecule>
	 *******************************************************************/
	public ArrayList<DistanceJointWrap> destroy() {
		ArrayList<DistanceJointWrap> res = null;
		// if(!this.compoundJointPair.isEmpty())
		// res = this.destoryAllJointsPair();
		if (!this.compoundJoint.isEmpty())
			res = this.destroyAllJoints();
		this.killBody();
		State.molecules.remove(this);
		return res;
	}
	


	/******************************************************************
	 * FUNCTION : destroyAllJoints() DESCRIPTION : Destroy all joints that are
	 * connecting to this molecule
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public ArrayList<DistanceJointWrap> destroyAllJoints() {

		Molecule pair = null;
		DistanceJointWrap dj = null;
		Anchor anchor = null;

		// Replicate references of all DistanceJoint to which this molecule is
		// connecting to
		ArrayList<DistanceJointWrap> djList = new ArrayList<DistanceJointWrap>();
		for (int m = 0; m < this.compoundJoint.size(); m++) {
			dj = compoundJoint.get(m);

			djList.add(new DistanceJointWrap(dj, false));
		}

		// Find other molecules to which this molecules is connecting to and
		// remove the distanceJoint reference in them
		if (this.compoundJoint != null && compoundJoint.size() > 0) {
			for (int i = 0; i < compoundJoint.size(); i++) {
				// Get the other molecule of this pair
				dj = compoundJoint.get(i);
				if (dj.getBodyA().getUserData() instanceof Molecule) {
					pair = (Molecule) dj.getBodyA().getUserData();
					if (pair == this) // Find the other one
					{
						if (dj.getBodyB().getUserData() instanceof Molecule) {

							pair = (Molecule) dj.getBodyB().getUserData();
							// Delete this joint reference in other Molecule
							// objects
							if (pair.compoundJoint.contains(dj))
								pair.compoundJoint.remove(dj);
						} else if (dj.getBodyB().getUserData() instanceof Anchor) {
							anchor = (Anchor) dj.getBodyB().getUserData();
							// Delete this joint reference in other Anchors
							if (anchor.compoundJoint.contains(dj))
								anchor.compoundJoint.remove(dj);
						}
					} else {
						// Delete this joint reference in other Molecule objects
						if (pair.compoundJoint.contains(dj))
							pair.compoundJoint.remove(dj);
					}

				} else if (dj.getBodyA().getUserData() instanceof Anchor) {
					anchor = (Anchor) dj.getBodyA().getUserData();
					// Delete this joint reference in other Anchors
					if (anchor.compoundJoint.contains(dj))
						anchor.compoundJoint.remove(dj);
				}

			}

		}

		// Delete this joint from world
		for (int k = 0; k < this.compoundJoint.size(); k++) {
			dj = compoundJoint.get(k);
			// PBox2D.world.destroyJoint(dj);
			dj.destroy();

		}
		compoundJoint.clear();

		/*
		 * //Remove this body from joints in djList for( int k =
		 * 0;k<djList.size();k++) {
		 * 
		 * dj = djList.get(k); if( dj.getBodyA()==this.body) dj.m_bodyA =null;
		 * else dj.m_bodyB = null;
		 * 
		 * }
		 */

		return djList;

	}

	/******************************************************************
	 * FUNCTION : SetPropertyByHeat() DESCRIPTION : Set restitution, friction
	 * and charge rate regarding to temperature
	 * 
	 * INPUTS : isIntial (boolean) OUTPUTS: None
	 *******************************************************************/
	public void setPropertyByHeat(boolean isInitial) {
		float temp = p5Canvas.temp;
		/*
		//Set up restituion
		res = (temp - freezingTem) / (boilingTem - freezingTem);
		if (res > 0.05)
			res = 1f;
		else //solid case
			res = 0.05f;

		if (temp <= freezingTem)
			fric = 0.6f;
		else
			fric = 0;
		*/
		
		updateState();
		//Solid case
		if( isSolid())
		{
			res = 0.5f;
			fric = 0.6f;
			setGravityScale(1.0f);
		}
		//Liquid case
		else if (isLiquid())
		{
			/*
			if(restitutionDampEnable)
			{
				res = 0.7f + (temp - freezingTem) / (boilingTem - freezingTem);
				res = (res>=1)?1:res;
			}
			else
			*/
				res = 1.0f;
			fric = 0f;
			setGravityScale(1.0f);
		}
		//Gas case
		else 
		{
			res=1.0f;
			fric = 0f;
			this.setGravityScale(0.0f);
		}
		
		if((p5Canvas.getUnit()==1||p5Canvas.getUnit()==2))
		{
		if (name.equals("Water"))
		{
			res =1.0f;
			chargeRate = 0.95f;
		}
		else if (name.equals("Sodium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.55f;
			if(temp>150)
				res = 0.0f;
			
		} else if (name.equals("Chlorine-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.f;
		} else if (name.equals("Calcium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.9f;
		} else if (name.equals("Silicon-Dioxide")) {
			chargeRate = 0.98f;
			fric = 1;
			res = 0.3f;
			shapeScale = 1.2f;
		} else if (name.equals("Glycerol")) {
			chargeRate = 0.9f;
			shapeScale = 1.1f;
		} else if (name.equals("Acetic-Acid")) {
			chargeRate = 0.85f;
			shapeScale = 1.1f;
		} else if (name.equals("Bicarbonate")) {
			chargeRate = 0.88f;
			fric = 1;
			res = 0.0f;
			shapeScale = 1.1f;
		} else if (name.equals("Potassium-Ion")) {
			chargeRate = 0.93f;
			fric = 1;
			res = 0.55f;
		}
		
		}

		if (!isInitial) {   //If this is not initialization
			setRestitution(res);
			setFriction(fric);
			if (name.equals("Water"))
			{   
				if(temp < 100)
					shapeScale = 1 + (100 - temp) / 300f;
				else
					shapeScale = 1f;
					//Enlarge molecule shape size to make them not close to each other
					setRadius(1.25f);
			}
		}
		else  //If this is initialization
		{

		}
		
	}

	/******************************************************************
	 * FUNCTION : createBody() DESCRIPTION : Create body and shape for molecules
	 * 
	 * INPUTS : x (float), y (float), angle (float) OUTPUTS: None
	 *******************************************************************/
	public void createBody(float x, float y, float angle) {

		// Mannually set up density
		float mul = setMul();

		// Define the body and make it from the shape
		bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(x, y)));
		bd.angle = angle;
	
		// This infinitive loop fix nullPointerException because
		// box2d.createBody(bd) may create a null body
		body = box2d.createBody(bd);
		while (body == null) {
			body = box2d.createBody(bd);
		}

		FixtureDef fd = new FixtureDef();
		for (int i = 0; i < circles.length; i++) {
			// Define a circle
			CircleShape circleShape = new CircleShape();

			// Offset its "local position" (relative to 0,0)
			Vec2 offset = new Vec2(circles[i][1] - pShapeW / 2, circles[i][2]
					- pShapeH / 2);
			circleShape.m_p.set(box2d.vectorPixelsToWorld(offset));
			circleShape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0])
					* shapeScale;

			float m = 1;
			String element = null;
			if (elementNames != null && i < elementNames.size()) {
				if (elementNames.get(i).equals("Chloride"))
					element = new String("Chlorine");
				else if(elementNames.get(i).equals("Bromide"))
					element = new String("Bromine");
				else
					element = new String(elementNames.get(i));
				m = DBinterface.getElementMass(element);
			}
			float d = m / (circles[i][0] * circles[i][0] * circles[i][0]);
			fd.filter.categoryBits = Constants.MOLECULE_ID; //All the molecules that enable collision is 2
			fd.filter.maskBits = Constants.MOLECULE_ID+Constants.BOUNDARY_ID+Constants.MOLE_NOTBOUND_ID;  //All the objects that should not be collided is 4
			fd.shape = circleShape;
			fd.density = d * mul;
			fd.friction = fric;
			fd.restitution = res; // Restitution is bounciness
			// if( p5Canvas.temp < this.freezingTem)
			// fd.restitution = 1.0f;
			// Attach shapes!
			Fixture fixture = body.createFixture(fd);
			fixtures.add(fixture);
		}

		// Give it some initial random velocity
		body.setLinearVelocity(new Vec2(p5Canvas.random(-1, 1), p5Canvas
				.random(-1, 1)));
		body.setAngularVelocity(0);
		body.setUserData(this);
		//Set mass property to the data got from database
		//It has been proved to not work
		
		MassData data = new MassData();
		body.getMassData(data);
		float factor = mass / data.mass;
		data.mass*= factor;
		data.I*=factor;
		body.setMassData(data);
		
		//body.resetMassData();
		
	}	
	

	public static Vec2 getShapeSize(String compoundName_, P5Canvas parent_) {
		String path = "resources/compoundsSvg/" + compoundName_ + ".svg";
		if ((compoundName_.equals("Sodium-Ion") || compoundName_
				.equals("Potassium-Ion"))
				&& (parent_.getUnit() == 2 && parent_.getSet() != 7)) {
			path = "resources/compoundsSvg/" + "Chlorine-Ion" + ".svg";
		}

		PShape pShape = parent_.loadShape(path);
		float pShapeW = pShape.width;
		float pShapeH = pShape.height;
		float[][] circles = SVGReader.getSVG(path);
		if (compoundName_.equals("Sodium-Chloride")) {
			pShapeW = circles[1][0] * 4;
			pShapeH = circles[1][0] * 2;
		}
		return new Vec2(pShapeW, pShapeH);
	}
	
	//Get size of current molecule, return as Vec2 with x representing width and y representing height
	public Vec2 getShapeSize()
	{
		return new Vec2(pShapeW,pShapeH);
	}

	public int getNumElement() {
		//return circles.length;
		return elementNames.size();
	}

	public String getName() {
		return name;
	}

	public float getBodyMass() {
		return body.getMass();
	}
	public float getMoleculeMass()
	{
		return mass;
	}
	//Get position in box2d world coordinates
	public Vec2 getPosition() {
		return body.getPosition();
	}
	//Get position in p5Canvas coordinates
	public Vec2 getPositionInPixel()
	{
		return box2d.coordWorldToPixels(getPosition());
	}
	
	public void setPosition(Vec2 pos,float angle)
	{
		body.setTransform(pos, angle);
	}
	
	public void setPositionInPixel(Vec2 posInPixel)
	{
		Vec2 posInWorld = box2d.coordPixelsToWorld(posInPixel);
		body.setTransform(posInWorld, body.getAngle());
	}
	public float getAngle()
	{
		return body.getAngle();
	}
	
	public void setAngle(float angle)
	{
		body.setTransform(getPosition(), angle);
	}

	public void setRestitution(float r) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			if (s == null)
				continue;
			s.setRestitution(r);
			s = s.getNext();
		}
	}

	public void setFriction(float r) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			s.setFriction(r);
			s = s.getNext();

		}
	}

	public Vec2 getElementLocation(int e) {
		Vec2 pos = body.getPosition();
		float a2 = body.getAngle();
		Vec2 v = new Vec2((float) Math.cos(a1[e] + a2), (float) Math.sin(a1[e]
				+ a2));
		if(this.getName().equals("Silver-Carbonate")&&e>=2) //Set Ag2CO3 element location at the center of CO3
			return pos.add(v.mul(gap[5]));
		return pos.add(v.mul(gap[e]));
	}

	public void addForce(Vec2 f) {
		Vec2 pos = body.getPosition();
		body.applyForce(f, pos);
	}

	/* Add a force to a certain position */
	public void addForce(Vec2 f, int e) {
		force = f;
		Vec2 l = getElementLocation(e);
		loc[e] = l;
		body.applyForce(force, l);
	}
	
	// Add linear impulse to molecule
	public void applyLinearImpulse(Vec2 impulse, Vec2 point)
	{
		body.applyLinearImpulse(impulse, point);
	}

	public void setRadius(float scale) {
		Fixture s = body.getFixtureList();
		for (int i = 0; i < circles.length; i++) {
			s.m_shape.m_radius = PBox2D.scalarPixelsToWorld(circles[i][0])
					* scale;
			s = s.getNext();
		}
	}
	
	
	//Move molecule with a specified vector
	public void move(float xVec, float yVec)
	{
		Vec2 move = box2d.vectorPixelsToWorld(new Vec2(xVec,yVec));
		Vec2 pos = new Vec2(body.getPosition());
		pos.addLocal(move);
		body.setTransform(pos, body.getAngle());
	}

	public void display() {
		// float yyy = (2+body.getPosition().y)/90;
		// if (yyy<0) yyy=0;
		// if (P5Canvas.temp<100)
		// body.applyForce(new Vec2(0,-yyy), body.getPosition());

//		//Update molecule positions
//		if (p5Canvas.isDragging() ) {
//			float xx = xTmp + PBox2D.scalarPixelsToWorld(p5Canvas.xDrag);
//			float yy = yTmp - PBox2D.scalarPixelsToWorld(p5Canvas.yDrag);
//			Vec2 v = new Vec2(xx, yy);
//			body.setTransform(v, body.getAngle());
//			body.setAngularVelocity(0);
//		}
//		else 
		{
			xTmp = body.getPosition().x;
			yTmp = body.getPosition().y;
		}
		/************************** Boundary Check **************************/

		boundaryCheck();

		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();

		/********************* Draw Bodies *******************/
		drawBody(pos, a);

		hideMolecule();

		if (name.equals("Calcium-Ion")) {
			p5Canvas.stroke(Color.BLUE.getRGB());
		}
		//Draw electrons
		//electronList.display();
		
		p5Canvas.popMatrix();
		// End drawing

		displayForces();

		displayJoints(pos );
		
		
		
		//Draw element center for testing
		/*
		for(int e=0;e<this.elementNames.size();e++)
		{
			int size = 5;
			p5Canvas.fill(204, 102, 0);
			Vec2 loc = new Vec2(PBox2D.vectorWorldToPixels(getElementLocation(e)));
			p5Canvas.ellipse(loc.x,p5Canvas.h * 0.77f+loc.y,size,size);
		}
		*/

	}
	
	private void drawBody(Vec2 pos, float a)
	{
		p5Canvas.pushMatrix();
		p5Canvas.translate(pos.x, pos.y);
		p5Canvas.rotate(-a);
		if(!displayPng)  //If not using png file
		{
			p5Canvas.shape(pShape, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH);
			
			//Apply transparency
			p5Canvas.stroke(ColorCollection.getColorSimBackgroundInt(),transparency*255f);
			p5Canvas.strokeWeight(2.0f);
			p5Canvas.fill(ColorCollection.getColorSimBackgroundInt(), transparency*255f); //Background color
			for (int i = 0; i < circles.length; i++) {
				p5Canvas.ellipse(circles[i][1] - pShapeW / 2, circles[i][2]
						- pShapeH / 2, circles[i][0] * 2, circles[i][0] * 2);
			}
			
		}
		else  //If using png file
		{
			p5Canvas.tint(255, (1-transparency)*255f);  // Apply transparency without changing color
			p5Canvas.image(pngSource, pShapeW / -2, pShapeH / -2, pShapeW, pShapeH);
		}
	}
	
	private void boundaryCheck()
	{
		if(fixtures.size()>0 && fixtures.get(0).getFilterData()!=null )
		{
			
			int catergory = fixtures.get(0).getFilterData().categoryBits;
			if(((catergory & Constants.MOLE_NOTBOUND_ID)==0) && catergory!=Constants.NOCOLLIDER)
				
		{
		/* If molecules go out of boundary, reset their position */
		/* Top boundary check, top boundary has max y value */
		if (body.getPosition().y + PBox2D.scalarPixelsToWorld(this.minSize / 2) > p5Canvas.boundaries.getTopBoundary().body
				.getPosition().y) {
			Vec2 v = new Vec2(body.getPosition().x,
					p5Canvas.boundaries.getTopBoundary().body.getPosition().y
							- PBox2D.scalarPixelsToWorld(getMaxSize() / 2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Bottom boundary check, bot boundary has min y value */
		else if (body.getPosition().y
				- PBox2D.scalarPixelsToWorld(this.minSize/ 2) < (p5Canvas.boundaries.getBotBoundary().body
					.getPosition().y - p5Canvas.boundaries.getBotBoundary().h)) {
			Vec2 v = new Vec2(body.getPosition().x,
					p5Canvas.boundaries.getBotBoundary().body.getPosition().y
							+ PBox2D.scalarPixelsToWorld(getMaxSize() / 2));
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Left boundary check, left boundary has min x value */
		if (body.getPosition().x - PBox2D.scalarPixelsToWorld(this.minSize / 2) < p5Canvas.boundaries.getLeftBoundary().body
				.getPosition().x) {
			Vec2 v = new Vec2(p5Canvas.boundaries.getLeftBoundary().body.getPosition().x
					+ PBox2D.scalarPixelsToWorld(getMaxSize() / 2),
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		/* Right boundary check, right boundary has max x value */
		else if (body.getPosition().x
				+ PBox2D.scalarPixelsToWorld(this.minSize / 2) > p5Canvas.boundaries.getRightBoundar().body
					.getPosition().x) {
			Vec2 v = new Vec2(p5Canvas.boundaries.getRightBoundar().body.getPosition().x
					- PBox2D.scalarPixelsToWorld(getMaxSize() / 2),
					body.getPosition().y);
			if (body != null && v != null)
				body.setTransform(v, body.getAngle());
		}
		}
		}
	}
	
	private void hideMolecule()
	{
		
		/*
		 * If molecules are selected or deselected in tableview, render or hide
		 * them
		 */
		if (! p5Canvas.getTableView().selectedRowsIsEmpty()) {

			String [] selectedMoleculesString = p5Canvas.getTableView().getSelectedMolecule();
			if(selectedMoleculesString!=null)
			{
				if(selectedMoleculesString.length>0)
				{
					
					boolean contains = false;
					if(this.tableIndex == -1 )
					{
						
						for(String moleName:selectedMoleculesString)
						{
							if(name.equals(moleName))
							{
								contains = true;
								break;
							}
						}
					}
					else //If we are using tableIndex to connect molecule with table index
					{
						int [] selectedRows = p5Canvas.getTableView().getSelectedRows();
						for(int index:selectedRows)
						{
							if(this.tableIndex==index)
							{
								contains = true;
								break;
							}
						}
					}
					if(!contains) //If selected molecules names do not contain this name
					{
						p5Canvas.noStroke();
						p5Canvas.fill(ColorCollection.getColorSimBackgroundInt(), 240); //Background color
						for (int i = 0; i < circles.length; i++) {
							p5Canvas.ellipse(circles[i][1] - pShapeW / 2, circles[i][2]
									- pShapeH / 2, circles[i][0] * 2, circles[i][0] * 2);
						}
					}
				
					
			}
			}

			
		}
		/* If hide checkbox is selected, hide them */
		else if (p5Canvas.isHidingEnabled && !isHidden) {
			p5Canvas.noStroke();
			p5Canvas.fill(ColorCollection.getColorSimBackgroundInt(), 240); //Background color
			for (int i = 0; i < circles.length; i++) {
				p5Canvas.ellipse(circles[i][1] - pShapeW / 2, circles[i][2]
						- pShapeH / 2, circles[i][0] * 2, circles[i][0] * 2);
			}
		}
	}
	
	private void displayForces()
	{
		/* Check if it is displaying forces */
		if (p5Canvas.isDisplayForces ) {
			int numElement = elementNames.size();
			for (int i = 0; i < numElement; i++) {
				if (loc[i] == null)
					continue;
				if(!(sumForceWaterX[i]==0&&sumForceWaterY[i]==0&&sumForceX[i]==0&&sumForceY[i]==0 ))
				{
				p5Canvas.stroke(Color.BLUE.getRGB());
				p5Canvas.line(
						PBox2D.scalarWorldToPixels(loc[i].x),
						p5Canvas.height - PBox2D.scalarWorldToPixels(loc[i].y),
						PBox2D.scalarWorldToPixels(loc[i].x)
								+ PBox2D.scalarWorldToPixels(sumForceWaterX[i]
										+ sumForceX[i]),
						p5Canvas.height
								- PBox2D.scalarWorldToPixels(loc[i].y)
								- PBox2D.scalarWorldToPixels(sumForceWaterY[i]
										+ sumForceY[i]));
				}
			}
			//this.clearForce();
		}
	}
	
	private void displayJoints(Vec2 pos)
	{
		/* Check if it is displaying joints */
		if (p5Canvas.isDisplayJoints) {
			// For Unit 1 and Unit 2
			if (p5Canvas.getUnit() == 1
					|| p5Canvas.getUnit() == 2) {
				if (compoundJ >= 0) {
					Vec2 pos2 = box2d.getBodyPixelCoord(molecules
							.get(compoundJ).body);
					p5Canvas.stroke(Color.BLACK.getRGB());
					p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
				}

				if (otherJ >= 0) {
					Vec2 pos2 = box2d
							.getBodyPixelCoord(molecules.get(otherJ).body);
					p5Canvas.stroke(Color.RED.getRGB());
					p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
				}
			} else {
				if (compoundJoint.size() != 0) {
					Vec2 pos2 = new Vec2();
					Body theOtherBody = null;
					for (int i = 0; i < compoundJoint.size(); i++) {
						if (compoundJoint.get(i).getBodyA().getUserData() == this)
							theOtherBody = compoundJoint.get(i).getBodyB();
						else
							theOtherBody = compoundJoint.get(i).getBodyA();

						pos2.set(box2d.getBodyPixelCoord(theOtherBody));
						p5Canvas.stroke(Color.BLACK.getRGB());
						p5Canvas.line(pos.x, pos.y, pos2.x, pos2.y);
					}
				}

			}
		}
	}

	// This function removes the particle from the box2d world
	public void killBody() {
		if(body!=null)
		box2d.destroyBody(body);
		body.m_world = null;
	}
	
	public void clearForce(){
		
		for (int e = 0; e < this.getNumElement(); e++) {
			this.sumForceX[e]=0;
			this.sumForceY[e]=0;
			
			
			this.sumForceWaterX[e]=0;
			this.sumForceWaterY[e]=0;
		
		}
	}

	/******************************************************************
	 * FUNCTION : setMul() DESCRIPTION : Mannually set up density for different
	 * elements
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/

	private float setMul() {
		float mul = 1.0f;
		if (name.equals("Pentane"))
			mul = 0.004f;
		else if (name.equals("Bromine"))
			mul = 0.0000001f;
		else if (name.equals("Mercury"))
			mul = 0.3f;
		else if (name.equals("Hydrogen-Peroxide"))
			mul = 0.8f;
		else if (name.equals("Sodium-Chloride"))
			if(p5Canvas.getUnit()==1 ||p5Canvas.getUnit()==2)
			mul = 1.0f;
			else
				mul =4.0f;
		else if (name.equals("Sodium-Ion"))
			mul = 0.011f / 0.006448616f;
		else if (name.equals("Chlorine-Ion"))
			mul = 4.0f;
		else if (name.equals("Glycerol"))
			mul = 2.0f;
		else if (name.equals("Silicon-Dioxide"))
			mul = 1.f;
		else if (name.equals("Calcium-Ion"))
			mul = 1.6f;
		else if (name.equals("Bicarbonate"))
			mul = 1.50f;
		else if (name.equals("Potassium-Ion"))
			mul = 1.1f;
		else if (name.equals("Chlorine"))
			if(p5Canvas.getUnit()==1 ||p5Canvas.getUnit()==2)
			mul = 0.04f;
			else
				mul =0.4f;
		else if (name.equals("Sodium"))
			mul = 1.0f;
		else if (name.equals("Hydrogen-Ion"))
			mul =6.0f;
		else if (name.equals("Lithium-Ion"))
			mul =4f;
		else if (name.equals("Hydrogen-Sulfide"))
			mul =3f;
		else if (name.equals("Hydrogen"))
			mul = 15.0f;
		else if (name.equals("Chloride"))
			mul = 1.5f;
		else if (name.equals("Ammonium"))
			mul = 2.5f;
		else if (name.equals("Helium"))
			mul = 4.0f;
		
		//mul = p5Canvas.getMoleculeDensity(getName());
		return mul;
		
		
	}
	
	//Set scale that gravity will apply on body
	//Range from 0.0 to 1.0
	public void setGravityScale(float gs)
	{
		if(gs>=1.0f)
			gs = 1.0f;
		else if ( gs<=0.0f)
			gs = 0.0f;
		if(body!=null)
		{
			//body.m_linearDamping=1.0f;
			body.m_gravityScale = gs;
		}
	}

	/**
	 * @return the maxSize
	 */
	public float getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            the maxSize to set
	 */
	public void setMaxSize(float maxSize) {
		this.maxSize = maxSize;
	}

	public void setReactive(boolean flag) {
		this.reactive = flag;
	}

	public boolean getReactive() {
		return this.reactive;
	}

	public boolean isSolid() {
		return (state == mState.Solid);
	}
	public boolean isLiquid(){
		return (state ==mState.Liquid);
	}
	public boolean isGas(){
		return (state ==mState.Gas);
	}
	public Vec2 getLinearVelocity()
	{
		return body.getLinearVelocity();
	}
	public float getLinearVelocityScalar()
	{
		float scalar = 0;
		Vec2 vector = body.getLinearVelocity();
		scalar = vector.x*vector.x + vector.y*vector.y;
		scalar = (float) Math.sqrt(scalar);
		return scalar;
	}
	
	public float getAngularVelocityScalar()
	{
		return body.getAngularVelocity();
	}
	/*
	public void setKineticEnergy(float ke)
	{
		Random rand = new Random(System.nanoTime());
		double velocityScalar = Math.sqrt(ke*2/body.getMass());
		double velocityX = (rand.nextFloat()-0.5)*velocityScalar;
		double velocityY =0;
		boolean direction = rand.nextBoolean();
		double directionV = (direction?1:-1);
		velocityY = directionV * Math.sqrt(velocityScalar*velocityScalar-velocityX*velocityX);
		this.setLinearVelocity(new Vec2((float)velocityX,(float)velocityY));
		
	}*/
	
	//Multiply molecules` kinetic energy by ratio r
	public void constrainKineticEnergy(float r)
	{
		float ratio = (float)Math.sqrt(r);
		Vec2 velocity = this.getLinearVelocity();
		velocity.mulLocal(ratio);
		
		float angularVelocity = body.getAngularVelocity();
		angularVelocity*=ratio;
		body.setAngularVelocity(angularVelocity);
		
	}
	
	public void setKineticEnergy(float energy)
	{
		float ratio = getKineticEnergy()/energy;
		constrainKineticEnergy(ratio);
	}
	
	//Multiply molecules` kinetic energy by ratio r
	public void shakeMolecule(float r)
	{
//		this.updateState();
//		if(state==mState.Solid)
		{
			float ratio = r;
			float angularVelocity = body.getAngularVelocity();
			angularVelocity*=ratio;
			body.setAngularVelocity(angularVelocity);
		}
		
	}
	
	public float getKineticEnergy()
	{
		float eRotational= 0.5f* body.getInertia()*body.getAngularVelocity()* body.getAngularVelocity();
		float eTransitional= 0.5f * body.getMass()* (Vec2.dot(body.getLinearVelocity(),body.getLinearVelocity()));
		return (eRotational+eTransitional)*ratioKE;
	}
	public void setLinearVelocity(Vec2 vec)
	{
		body.setLinearVelocity(vec);
	}
	public void setTableIndex(int index)
	{
		this.tableIndex = index;
	}
	public int getTableIndex()
	{
		return this.tableIndex;
	}
	private void setEnthalpy()
	{
		enthalpy[0] = DBinterface.getEntalpy(name, "solid");
		enthalpy[1] = DBinterface.getEntalpy(name, "liquid");
		enthalpy[2] = DBinterface.getEntalpy(name, "gas");
	}
	
	private void setEntropy()
	{
		entropy[0] = DBinterface.getEntropy(name, "solid");
		entropy[1] = DBinterface.getEntropy(name, "liquid");
		entropy[2] = DBinterface.getEntropy(name, "gas");
	}
	public float getEnthalpy()
	{
		float enthalpyValue = 0;
		//Check that which state is this molecule in
		updateState();
		switch (state)
		{
		case Solid:
			enthalpyValue = enthalpy[0];
			break;
		case Liquid:
			enthalpyValue = enthalpy[1];
			break;
		case Gas:
			enthalpyValue = enthalpy[2];
			break;
		}
			return enthalpyValue;
	}
	
	
	//Get enthalpy by state
	public float getEnthalpy(String s)
	{
		float enthalpyValue = 0;
		if(s.equals("solid"))
			enthalpyValue = enthalpy[0];
		else if(s.equals("liquid"))

			enthalpyValue = enthalpy[1];
		else if(s.equals("gas"))

			enthalpyValue = enthalpy[2];
		return enthalpyValue;
	}
	
	//Get entropy of this system	
	public float getEntropy()
	{
		float entropyValue = 0;
		//Check that which state is this molecule in
		updateState();
		switch (state)
		{
		case Solid:
			entropyValue = entropy[0];
			break;
		case Liquid:
			entropyValue = entropy[1];
			break;
		case Gas:
			entropyValue = entropy[2];
			break;
		}
			return entropyValue;
	}
	
	//Get entropy by state
	public float getEntropy(String s)
	{
		float entropyValue = 0;
		if(s.equals("solid"))
			entropyValue = entropy[0];
		else if(s.equals("liquid"))

			entropyValue = entropy[1];
		else if(s.equals("gas"))

			entropyValue = entropy[2];
		return entropyValue;
	}
	
	//Update molecule state based on temperature
	private void updateState()
	{
		//Only if enableAutoState is true
		if(enableAutoState)
		{
			float temp = p5Canvas.temp;
			if(temp<this.freezingTem)
				state = mState.Solid;
			else if (temp>=this.boilingTem)
				state = mState.Gas;
			else
				state = mState.Liquid;
		}
	}
	//Update molecule state manually
	public void setState(mState st)
	{
		//Only if enableAutoState is false
		if(!enableAutoState)
		{
			state = st;
		}
	}
	
	public mState getState()
	{
		return state;
	}
	//Check if molecule contains mouse pressed point
	  public boolean contains(float x, float y) {
		  Vec2 p5CanvasPoint = new Vec2(x,y);
		  boolean inside = false;
		    Vec2 worldPoint = box2d.coordPixelsToWorld(p5CanvasPoint);
		    Fixture s = body.getFixtureList();
		    while(s!=null)
		    {
		      inside = s.testPoint(worldPoint);
		      if(inside)
		    	  return inside;
		      s=s.getNext();
		    }
		    
		    return inside;
		  }
	  /*
	  public void setRestitutionDamp(boolean b)
	  {
		  this.restitutionDampEnable = b;
	  }
	  */
	  public void setRatioKE(float v)
	  {
		  if( v>=0)
			  ratioKE=v;
	  }
	  
	  public void setEnableAutoStateChange(boolean flag)
	  {
		  this.enableAutoState = flag;
	  }
	  public boolean getEnableAutoStateChange()
	  {
		  return this.enableAutoState;
	  }
	  
	  public void setBoillingPoint(float temp	)
	  {
		  this.boilingTem = temp;
	  }
	  public void setFreezingPoint(float temp)
	  {
		  this.freezingTem = temp;
	  }
	  
	  public P5Canvas getP5Canvas()
	  {
		  return p5Canvas;
	  }
	  
	  public void setTransparent(float t)
	  {
		  transparency = t;
	  }
	  
	  //Decide which object this one collides with
	  //The first parameter is the category of this one
	  //The second parameter is the mask
	  public void setFixtureCatergory(int cate,int mask)
	  {
		  Filter filter = new Filter();
		filter.categoryBits = cate;
		  filter.maskBits = mask;
		  for(int i=0;i<fixtures.size();i++)
		  {
			  fixtures.get(i).setFilterData(filter);
		  }
	  }
	  
	  public void setImage(String compoundName)
	  {
		  if(compoundName!=null)
		  {
			  pngSourceName = new String(compoundName);
			  pngSource = p5Canvas.loadImage("resources/compoundsPng50/" + pngSourceName + ".png");
		  if(pngSource!=null)
			  displayPng = true;
		  }
	  }
	  
	  public String getImageName()
	  {
		  return pngSourceName;
	  }
	  
	  public void setParentName(String str)
	  {
		  parentName = new String(str);
	  }
	  
	  public String getParentName()
	  {
		  return parentName;
	  }
	  

}
