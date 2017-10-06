package simulations.models;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import simulations.P5Canvas;

public class ElectronList {
	
	ArrayList<Electron> list;
	Molecule parent = null;
	P5Canvas p5Canvas = null;
	public float x;
	public float y;
	private float lastAngle =0;
	
	public ElectronList(Molecule mole)
	{
		parent = mole;
		list = new ArrayList<Electron>();
		p5Canvas= parent.getP5Canvas();
		createList();
	}
	
	//Create electron list for a given molecule
	//The indice are increasing in counter-clockwise order
	private void createList()
	{
		String name = parent.getName();
		if(name.equals("Hydrogen-Ion"))  //Zero electron
		{
		}
		else if(name.equals("Chlorine-Ion")) //Eight
		{
			//createList(8);
		}
		else if(name.equals("Hydroxide"))
		{
			createList(8);
		}
		else if(name.equals("Sodium-Ion"))
		{
			
		}
	}
	
	private void createList(int num)
	{
		Vec2 shapeSize = parent.getShapeSize();
		float padding = 8;
		float moleWidth = shapeSize.x/2+padding;
		float moleHeight = shapeSize.y/2+padding;
		Vec2 molePosition = parent.getPositionInPixel();
//		float moleX = molePosition .x;
//		float moleY = molePosition.y;
		float moleAngle = parent.getAngle();
		double axisAngle = 0;
		double offAngleSize = Math.PI/14;
		double offAngle = 0;
		double eleAngle = 0 ;
		double x = 0, y =0;
		int phase = 0;
		
		switch(num)
		{
		default:
			break;
			
		case 8:
		case 6:
			for(int i =0;i<num;i++)
			{
				phase = i/2;
				axisAngle = Math.PI/2 * phase;
				offAngle = ((i%2==1)?1:-1)*offAngleSize;
				eleAngle = axisAngle + offAngle;
				
				if((i/2)%2==0) //If index is 0,1, 4,5
				{
					x= Math.cos(axisAngle)*moleWidth;
					y= Math.tan(offAngle)*moleWidth;
				}
				else  //If index is 2,3,6,7
				{
					x= Math.tan(offAngle)*moleHeight;
					y = Math.sin(axisAngle) * moleHeight;
				}
				
				Electron electron = new Electron(parent,(float)x,(float)y);
				list.add(electron);
			}
			break;
		}
		lastAngle = 0;
		//Rotate electrons by molecule angle
		rotate(moleAngle);
		
	}
	
	//Rotate the whole list by the angle
	public void rotate(float angle)
	{
		float angleDiff = angle-lastAngle;
		float sin = (float) Math.sin(angleDiff);
		float cos = (float) Math.cos(angleDiff);
		float xNew = 0;
		float yNew = 0 ;
		
		for(int i = 0 ;i<list.size();i++)
		{
			Electron ele = list.get(i);
			xNew = cos*ele.x - sin*ele.y;
			yNew = sin* ele.x + cos*ele.y;
			ele.x = xNew;
			ele.y = yNew;
		}
		
		lastAngle = angle;
		 
		
	}
	
	//Show the electrons
	public void display()
	{
		//Translate to the center of molecule
		Vec2 molePosition = parent.getPositionInPixel();
		//p5Canvas.pushMatrix();
		//p5Canvas.translate(molePosition.x, molePosition.y);
		//rotate(parent.getAngle());
		for(int i = 0;i<list.size();i++)
		{
			list.get(i).display();
		}
		//p5Canvas.popMatrix();
		
	}


}
