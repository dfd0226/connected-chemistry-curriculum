package simulations.models;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import Util.ColorCollection;
import Util.Constants;

import processing.core.PShape;

import simulations.P5Canvas;
import simulations.PBox2D;

public class Boundary {

	private P5Canvas p5Canvas;
	// But we also have to make a body for box2d to know about it
	public Body body;
	PBox2D box2d;
	Boundaries boundaries;
	private float x;
	private float y;
	public float w;
	public float h;
	//private Vec2 positionOrigin;
	//private float box2dW;
	//private float box2dH;
	//private float boundaryWidth;
	private int id =-1;
	//private int volumeSliderValue;
	//private int volumeSliderDefaultValue;
	private float yOriginal =0; //Original y of body when .d
	private float yTop = 0;
	//public static boolean isTransformed =false; //Increase or Decrease in Volume
	private PShape poleShape = new PShape();    //The pole that on top of base
	private PShape baseShape = new PShape();    //piston base
	private PShape weightShape = new PShape();	//Weight on base
	private Fixture fixture = null;
	public float defaultFriction=0.0f;
	
	
	public Boundary(int _id, float xv, float yv, float wv, float hv, Boundaries parent) {
		id = _id;
		boundaries = parent;
		p5Canvas = boundaries.getP5Canvas();
		box2d = p5Canvas.getBox2d();
		x=xv;
		y=yv;
		w = wv;
		h = hv;
		// Figure out the box2d coordinates
		float box2dW = box2d.scalarPixelsToWorld(w/2);
		float box2dH = box2d.scalarPixelsToWorld(h/2);
		
		// Define the polygon
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(box2dW, box2dH);
		
		// Create the body
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
        bd.position.set(box2d.coordPixelsToWorld(new Vec2(x,y)));
		body = box2d.createBody(bd);
		while (body ==null){ 
			body = box2d.createBody(bd);
		}	
		
		FixtureDef fd = new FixtureDef();
        fd.shape = polygonShape;
    	fd.density = 0f;    // No density means it won't move!
        fd.friction = 1.0f;
    	fd.restitution =1f;
    	fd.filter.categoryBits = Constants.BOUNDARY_ID; //All the molecules that enable collision is 2
		fd.filter.maskBits = Constants.MOLECULE_ID+Constants.NOTMOLE_BOUND_ID;  //All the objects that shou
        fixture = body.createFixture(fd);
        
	//	body.setMassFromShapes();
		body.setUserData(this);
		//positionOrigin = new Vec2(body.getPosition());
		yOriginal = body.getPosition().y;
		yTop= yOriginal;
		//isTransformed =true;
		if(id==boundaries.TOP)
		{
			String basePath = "resources/compoundsSvg/base.svg";
			String weightPath = "resources/compoundsSvg/weight-with-base.svg";
			String polePath = "resources/compoundsSvg/piston.svg";
			baseShape =  p5Canvas.loadShape(basePath);
			weightShape = p5Canvas.loadShape(weightPath);
			poleShape = p5Canvas.loadShape(polePath);
		}
	}
	
	//Reset position of boundary, input in pixel coordinates
	public void resetPosition(float xv,float yv)
	{
		//TODO: set boundary position
		Vec2 vec = box2d.coordPixelsToWorld(new Vec2(xv,yv));
		body.setTransform(vec, body.getAngle());
	}
	
	//Move boundary, input is move vector in pixel coordinates
	public void move(float xv,float yv)
	{
		Vec2 move = box2d.vectorPixelsToWorld(new Vec2(xv,yv));
		Vec2 pos = new Vec2(body.getPosition());
		pos.addLocal(move);
		body.setTransform(pos, body.getAngle());
		
		this.yTop+=move.y;
	}
	
	//Designed to move top boundary, x value will never be changed while volume is changing
	public void setY(float dif)
	{
		Vec2 pos = new Vec2(body.getPosition());
		Vec2 vec = new Vec2();
		vec.x = pos.x;
		vec.y = this.yTop + box2d.scalarPixelsToWorld(dif);
		if(!body.isAwake())
			body.setAwake(true);
		body.setTransform(vec, body.getAngle());
	}
	
	//Reset yTop to yOriginal, only be called during reset
	public void resetY(float dif)
	{
		yTop = yOriginal;
		setY(dif);
	}
	
	public float getId(){
		return id;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
		/*
	public void set(int v){
		
			volumeSliderValue = v;
			difVolume = (volumeSliderValue-volumeSliderDefaultValue)*p5Canvas.multiplierVolume;
			if( difVolume!=0)
				isTransformed =true;
			else 
				isTransformed = false;
	}
	*/
	
	public void display() {
		float a = body.getAngle();
		
		//Start to draw boundaries
		Vec2 pos = box2d.getBodyPixelCoord(body);
		
		/*
		//Transform top boundary to right position before draw it
		if (id==2)
	    {
			if(isTransformed && p5Canvas.isSimStarted)
		    {
				Vec2 v = new Vec2(body.getPosition().x, yOriginal + 
						box2d.scalarPixelsToWorld(difVolume));
//				boolean tmp = p5Canvas.isEnable;
//				p5Canvas.isEnable = false;
				body.setTransform(v, body.getAngle());
//				p5Canvas.isEnable = tmp;
				isTransformed =false;
				
		    }
		}	*/
		/***************************** Draw Boundary ***************************/
		p5Canvas.pushMatrix();
		p5Canvas.translate(pos.x, pos.y);
		p5Canvas.rotate(-a);
		float pShapeW =w;
		float pShapeH =h;

		//Render top boundary with a real image 
		if(id == boundaries.TOP) 
		{	
			float width = baseShape.getWidth();
			float height = baseShape.getHeight();
			float ratio = pShapeW/width;
			width *=ratio;
			height*=ratio;
			//For Unit 4 Sim 4 Set 2, we use weight top boundary image instead of base image
//			if(p5Canvas.isSimSelected(4,4,2)||p5Canvas.isSimSelected(7, 1, 1)||p5Canvas.isSimSelected(7, 3,1))
			if(boundaries.hasWeight())
				p5Canvas.shape(weightShape, pShapeW/-2, pShapeH/-2-(height-pShapeH),width,height);
			else
			{
				float poleHeight = poleShape.getHeight();
				p5Canvas.shape(poleShape,pShapeW/-2, pShapeH/-1.5f-(poleHeight-pShapeH),width,poleHeight);
				p5Canvas.shape(baseShape, pShapeW/-2, pShapeH/-2-(height-pShapeH),width,height);
			}
		}
		
		//Color
		if(id == boundaries.TOP)
			p5Canvas.noFill();
			//p5Canvas.fill(p5Canvas.boundaryColor);
		else if (id == boundaries.BOT)
			p5Canvas.fill(p5Canvas.heatRGB);
		else
		{
			p5Canvas.fill(ColorCollection.getColorSimBoundaryInt());
		}
		p5Canvas.noStroke();
		p5Canvas.rect(pShapeW/-2 , pShapeH/-2 , pShapeW , pShapeH);	
		
		p5Canvas.popMatrix();
		/****************************** End Drawing *******************************/
	 	
	}
	
	public float getOriginalY()
	{
		return this.yOriginal;
	}
		
	public void destroy() {
		if(this.body!=null)
			box2d.destroyBody(body);
		body.m_world =null;
	}
	
	public void setFriction(float friction)
	{
		fixture.setFriction(friction);
	}
	
	public float getFriction()
	{
		return fixture.getFriction();
	}
	
	public Vec2 getLinearVelocity()
	{
		return body.getLinearVelocity();
	}
}
