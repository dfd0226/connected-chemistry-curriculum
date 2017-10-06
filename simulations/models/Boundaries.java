package simulations.models;

import org.jbox2d.common.Vec2;

import simulations.P5Canvas;
import simulations.PBox2D;

public class Boundaries {
	
	private P5Canvas p5Canvas;
	//LeftBoundary 0 RightBoundary 1 TopBoundary 2 BottomBoundary 3
	private Boundary[] boundaries;
	private final int boundaryNum = 4;
	private float x;
	private float y;
	private float w;
	private float h;
	private float maxH = 1100;
	
	private float boundaryWidth;
	

	public final int LEFT = 0;
	public final int RIGHT = 1;
	public final int TOP = 2;
	public final int BOT = 3 ;
	
	public float difVolume; //The difference between current and origin top boundary , in form of pixel coordinates
	private int volume = 0;    //Current volume
	private int volumeDefault; //Default volume
	//private boolean isTransformed;
	//public float multiplierVolume = 0.66f; // Multiplier from world coordinates to ml
	
	private boolean hasWeight = false; //If weight on top boundary needed
	
	public Boundaries(P5Canvas p5)
	{
		boundaries = new Boundary[boundaryNum];
		p5Canvas = p5;
		boundaryWidth =10;
	}
	
	//First time setup the canvas
	public void create(float xv, float yv, float wv, float hv,int defVolume)
	{
		if (hv > maxH)
			return;
		x=xv;
		y = yv;
		w = wv;
		h = hv;
		
		// Add a bunch of fixed boundaries
		//float bW = 10.f; // boundary width
		int sliderValue = 0;
		if (p5Canvas.getMain().volumeSlider != null)
			sliderValue = p5Canvas.getMain().volumeSlider.getValue();
		else
			sliderValue = p5Canvas.getMain().defaultVolume;
		
		//Clear boundary array
		for(int i = 0;i<4;i++){
			if (boundaries[i] != null)
				boundaries[i].destroy();
		}
		boundaries[0] = new Boundary(0, x, y, boundaryWidth, 2 * h,this);
		boundaries[1] = new Boundary(1, x + w, y, boundaryWidth, 2 * h,this);
		boundaries[2] = new Boundary(2, x + w / 2, y, w-boundaryWidth , boundaryWidth, this);
		boundaries[3] = new Boundary(3, x + w / 2, y + h, w + boundaryWidth, boundaryWidth,this);
		
		volumeDefault = defVolume;
		volume = volumeDefault;

	}
	
	public void moveBoundary(float xv, float yv) {
		for(int i  =0;i<boundaryNum;i++)
			boundaries[i].move(xv, yv );
	}
	
	public void resetBoundary(float xv, float yv, float wv, float hv,int currentVolume) {
		if (hv > maxH)
			return;
		x = xv;
		y = yv;
		w = wv;
		h = hv;
		
		boundaries[0].resetPosition(x, y);
		boundaries[1].resetPosition(x + w, y);
		boundaries[2].resetPosition(x + w / 2, y);
		boundaries[3].resetPosition(x + w / 2, y + h);
		
		//volumeDefault = defVolume;
		resetVolume(currentVolume);
		//hasWeight = false;
	}
	
	public void display()
	{
		for (int i = 0; i < boundaryNum; i++) {
			boundaries[i].display();
		}
	}
	
	public void setVolume(int v)
	{
		//Always move top boundary to change the volume
		//flofat yTrans = PBox2D.scalarPixelsToWorld(difVolume);
		volume = v;
		difVolume = (volume - volumeDefault)*p5Canvas.multiplierVolume;
		boundaries[TOP].setY(difVolume);
		
	}
	
	public void setVolume(float v)
	{
		//Always move top boundary to change the volume
		//flofat yTrans = PBox2D.scalarPixelsToWorld(difVolume);
		volume = (int)v;
		difVolume = (v - volumeDefault)*p5Canvas.multiplierVolume;
		boundaries[TOP].setY(difVolume);
		
	}
	
	public void resetVolume(int v)
	{	volume = v;
		difVolume = (volume - volumeDefault)*p5Canvas.multiplierVolume;
		boundaries[TOP].resetY(difVolume);
		
	}

	
	//Return boundary reference
	public Boundary getTopBoundary()
	{
		return boundaries[TOP];
	}
	public Boundary getBotBoundary()
	{
		return boundaries[BOT];
	}
	public Boundary getLeftBoundary()
	{
		return boundaries[LEFT];
	}
	public Boundary getRightBoundar()
	{
		return boundaries[RIGHT];
	}
	
	public P5Canvas getP5Canvas()
	{
		return p5Canvas;
	}
	
	public void setHasWeight(boolean flag)
	{
		hasWeight = flag;
	}
	
	public boolean hasWeight()
	{
		return hasWeight;
	}

}
