package simulations.models;

import Util.ColorCollection;
import simulations.P5Canvas;

public class Electron {
	
	public boolean isShowing = false;
	Molecule parent;
	
	// x and y coordinates relative to electron list
	public float x;
	public float y;
	private float radius;
	private int strokeWeight;
	
	public Electron(Molecule mole,float xv, float yv)
	{
		parent = mole;
		radius = 7;
		strokeWeight=1;
		x = xv;
		y = yv;
	}
	
	public Electron(Molecule mole)
	{
		this(mole,0,0);
	}
	
	public void display()
	{
		P5Canvas p5Canvas = parent.getP5Canvas();
		if(isShowing)
		{
			//Draw Background
			p5Canvas.fill(ColorCollection.getColorElectronBkInt());
			p5Canvas.ellipse(x, y, radius, radius);
			//Draw Border
			p5Canvas.strokeWeight(strokeWeight);
			p5Canvas.stroke(ColorCollection.getColorElectronBorderInt());
			p5Canvas.ellipse(x, y, radius, radius);
		}
	}
	
	public void setShowing(boolean flag)
	{
		isShowing = flag;
	}
	
	public boolean getShowing()
	{
		return isShowing;
	}

}
