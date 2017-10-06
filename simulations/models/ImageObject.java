/* ImageObject is an object directly rendered by processing methods on p5Canvas. 
 * ImageObject is not belong to box2d, which means it will not be affected by physics engine
 * Only used to render some objects that has no collision with molecules and boundaries
 * 
 */
package simulations.models;

import org.jbox2d.common.Vec2;

import data.State;

import processing.core.PImage;
import simulations.P5Canvas;

public class ImageObject {
	
	private String name;
	private String picName;
	private PImage pImage;
	private final String parentUrl = new String("resources/compoundsPng50/");
	private P5Canvas p5Canvas;
	
	private float x;  //x-coordinate of the image
	private float y;  //y-coordinate of the image
	private float w;  //width to display the image
	private float h;  //height to display the image
	private float transX ;  //x-coordinate of translation
	private float transY;   //x-coordinate of translation
	private float velX;
	private float velY;
	private float acceX;
	private float acceY;
	
	private float angle; //The rotated angle
	
	private float transparency = 0;
	
	private Vec2 velocity = new Vec2();
	
	public enum Animation{
		moveStraight;
	}
	
	private Animation animation = Animation.moveStraight; 
	
	
	public ImageObject(float xv, float yv, float angle, String name, String picName,P5Canvas parent)
	{
		this.name = name;
		this.picName = picName;
		p5Canvas = parent;
		x= xv;
		y = yv;
		this.angle = p5Canvas.radians(angle);
		
		pImage = p5Canvas.loadImage( parentUrl + this.picName + ".png");
		w = pImage.width;
		h = pImage.height;
		transX = 0;
		transY = 0;
		velX = 0;
		velY = 0;
		acceX = 1f;
		acceY = 0;
		
		State.imageObjects.add(this);
		
	}
	
	public void setWidth(float wid)
	{
		w = wid;
	}
	
	public void setHeight(float hei)
	{
		h = hei;
	}
	
	public void setVelocity( Vec2 vel)
	{
		velocity.set(vel);
	}
	
	public void display()
	{
		//Update position
		x += velocity.x;
		y += velocity.y;
		
		
		//Draw image
		p5Canvas.pushMatrix();
		p5Canvas.translate(x, y);
		p5Canvas.rotate(-angle);
		
		//Add animation
		if(animation==Animation.moveStraight)
		{
//			if(transX>=2000 || transY>=2000)
//				return;
			velX +=acceX;
			velY +=acceY;
			//p5Canvas.translate(xVel,yVel);
			transX+=velX;
			transY+=velY;
		}
		
		//Add transparency
		p5Canvas.tint(255,(1-transparency)*255f);
		p5Canvas.image(pImage,transX,transY,w,h);
		
		p5Canvas.popMatrix();
	}
	
	public void setTransparency(float value)
	{
		if(value>=0 && value<=1)
		{
			transparency = value;
		}
	}
	
	public void setAnimation( Animation type)
	{
		animation = type;
	}
	

}
