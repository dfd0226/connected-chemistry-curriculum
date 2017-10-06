/**
 * 
 */
package simulations.models;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

/**
 * @author Qin Li
 * Simulation is the basic unit of our chemical simulations
 * This class includes all parameter a simulation needs
 */
public class Simulation {
	
	//Enum parameter defines spawn styles for different molecules
	public enum SpawnStyle {
		Gas,
		Liquid,
		Solvent,
		Precipitation, //Dissolvable compound spawn like precipitation
		SolidCube,
		SolidPavement,
		SolidSpecial,
		
}
	
	private int unit;
	private int sim;
	private int set;
//	private int elementNum;
	private final int MAX_ELEMENT_NUM = 25;
	private String [] elements;   //The compounds which will show in the sim
	private SpawnStyle [] elementSpawnStyles;    //The style that the compound spawn
	private float distanceBetweenMolecule;
	private int anchorNum = 0;
	private Vec2 anchorPos[];     //The position of anchors
	private float [] elementDensity;
	
	private float speed =1.0f; //Molecule speed moditifier for simulation
	
	public Simulation()
	{
		
	}
	public Simulation(int unit, int sim, int set)
	{
		this.unit = unit;
		this.sim = sim;
		this.set = set;
		elements = new String [MAX_ELEMENT_NUM];
		elementSpawnStyles =  new SpawnStyle[MAX_ELEMENT_NUM];
		elementDensity = new float [MAX_ELEMENT_NUM];
		for(int i = 0;i<MAX_ELEMENT_NUM;i++)
		{
			elementDensity[i] =1;
		}
	}
	public Simulation(int unit, int sim, int set,int elementNum)
	{
		this.unit = unit;
		this.sim = sim;
		this.set = set;
		elements = new String [elementNum];
		elementSpawnStyles =  new SpawnStyle[elementNum];
		elementDensity = new float [elementNum];
		
		for(int i = 0;i<elementNum;i++)
		{
			elementDensity[i] =1;
		}
	}
	public void setupElements(String []ele, SpawnStyle [] style)
	{
		elements = ele;
		elementSpawnStyles = style;
	}
	public void setupElementDensity(float [] den)
	{
		if(den!=null)
		{
			if(den.length==elements.length)
			{
				elementDensity = den;
			}
		}
	}
	public void setupElementDensity(String moleName,float den)
	{
		for(int i = 0;i<elements.length;i++)
		{
			if(elements[i].equals(moleName))
			{
				elementDensity[i]= den;
			}
		}
	}
	
	public float getElementDensity(String moleName)
	{
		for(int i = 0;i<elements.length;i++)
		{
			if(elements[i].equals(moleName))
			{
				return elementDensity[i];
			}
		}
		return 1; //If not find a particular, return 1 means does not change the density
	}
	public void setupAnchors( int num, Vec2 [] aPos)
	{
		anchorNum = num;
		anchorPos = aPos;
	}
	public void setSpeed( float v)
	{
		speed = v;
	}
	
	public float getSpeed( )
	{
		return speed;
	}
	public int getAnchorNum()
	{
		return anchorNum;
	}
	public Vec2 getAnchorPos(int index )
	{
		return anchorPos[index];
	}
	public int getSimNum()
	{
		return sim;
	}
	public int getSetNum()
	{
		return set;
	}
	public int getUnitNum()
	{
		return unit;
	}
	public boolean isSimSelected(int unit,int sim,int set)
	{
		if(this.unit==unit && this.sim==sim && this.set==set)
			return true;		
		else
			return false;
	}
	
	public String [] getElements()
	{
		return elements;
	}
	
	public int getElementIndex(String str)
	{
		for(int i = 0 ;i<elements.length;i++)
		{
			if(elements[i].equals(str))
			{
				return i;
			}
		}
		
		return -1;
		
	}
	
	//Set the name of element of the specified index
	public void setElementByIndex(String str, int index)
	{
		if(index>=0 && index<elements.length)
		{
			elements[index] = new String (str);
		}
	}
	
	//Get the spawn type of the specific element
	public SpawnStyle getSpawnStyle(String ele)
	{
		int index = getIndexOfElement(ele);
		if(index!=-1)
		return getSpawnStyle(index);
		else 
			return null;
	}
	
	//Get the spawn type of the element by index
	public SpawnStyle getSpawnStyle(int index)
	{
		if(index>=0 && index<this.elementSpawnStyles.length)
		return this.elementSpawnStyles[index];
		else
			return null;
	}
	
	
	private int getIndexOfElement(String ele)
	{
		for( int i = 0;i<elements.length;i++)
		{
			if( elements[i].equals(ele))
				return i;
		}
		return -1;
	}
	
	public float getDistanceBetweenMolecule()
	{
		return distanceBetweenMolecule;
	}
	public void setDistanceBetweenMolecule(float dist)
	{
		distanceBetweenMolecule = dist;
	}

}
