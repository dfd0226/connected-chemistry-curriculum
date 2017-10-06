package data;
import static data.YAMLinterface.*;

import java.util.ArrayList;
import java.util.HashMap;

import simulations.models.Anchor;
import simulations.models.Boundary;
import simulations.models.ImageObject;
import simulations.models.Molecule;

public class State {
	// An ArrayList of particles that will fall on the surface
	public static ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	public static ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	public static ArrayList<ImageObject> imageObjects = new ArrayList<ImageObject>();
	// A list we'll use to track fixed objects

	public static HashMap<String,Integer> moleculesAdded = new HashMap<String,Integer>();
	
	public State()
	{
	
	}
	
	/*
	 * Unit, set and sim status
	 */
	private static int currentUnitNumber = 0;
	private static int currentSimNumber = 0;
	private static int currentSetNumber = 0;

	//public static void setCurrentUnit(int currUnit) {}
	//public static void setCurrentSim(int currSim) {}
	//public static void setCurrentSet(int currSet) {}
	
	
/*
	public static int getCurrentUnitNumber() {
		return currentUnitNumber;
	}
	public static int getCurrentSimNumber() {
		return currentSimNumber;
	}
	public static int getCurrentSetNumber() {
		return currentSetNumber;
	}*/
	//Return the number of molecules in simulation
	public static int getMoleculeNum()
	{
		return molecules.size();
	}
	public static int getMoleculeNumByName(String compoundName)
	{
		int count = 0;
		for( int i = 0 ;i<molecules.size();i++)
		{
			if(molecules.get(i).getName().equals(compoundName))
				count++;
		}
		return count;
	}
	public static ArrayList<String> getCompoundNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		String name = null;
		for(Molecule mole: molecules)
		{
			name = new String(mole.getName());
			if(!names.contains(name))
				names.add(name);
		}
		return names;
	}
	
	public static float getCompoundsMass()
	{
		float mass = 0f;
		if(molecules.isEmpty()||molecules==null)
			return mass;
		for(Molecule mole: molecules)
		{
			mass+=mole.getBodyMass();
		}
		
		return mass;
	}
	public static ArrayList<Molecule> getMolecules()
	{
		return molecules;
	}
	
	public static ArrayList<Anchor> getAnchors()
	{
		return anchors;
	}
	
	public static int getMoleculeIndex(Molecule mole)
	{
		return molecules.indexOf(mole); 
	}
	
	
	public static Molecule getMoleculeByName(String name)
	{
		for(Molecule m:molecules )
		{
			if(m.getName().equals(name))
			{
				return m;
			}
		}
		return null;
	}
	public static Molecule getMoleculeByIndex(int index)
	{
		float size = molecules.size();
		if(index>=0 && index<size)
		{
			return molecules.get(index);
		}
		return null;
	}
	public static ArrayList<Molecule>	getMoleculesByName(String name)
	{
		ArrayList<Molecule> moles = new ArrayList<Molecule>();
		for(Molecule m:molecules )
		{
			if(m.getName().equals(name))
			{
				moles.add(m);
			}
		}
		return moles;
	}
	
	public static ArrayList<ImageObject> getImageObjects()
	{
		return imageObjects;
	}
	
	

	

	
	public static void reset()
	{
		moleculesAdded.clear();
	}
	/*
	 * Default settings
	 */
	public static final Float defaultTemperature = 25.0f; // default temp for all sims/sets is 0° Celsius
	public static final Float defaultMinTemperature = -10.f;
	public static final Float defaultMaxTemperature = 200.f;
	
	/*
	 * Default compound settings
	 */
	public static final String defaultCompoundName = "Generic";
	public static final String defaultCompoundFormula = "G";
	public static final int defaultCompoundId = 0;
	public static final int polarity = 0;
	public static final int charge = 0;
	public static final Float density = 1.f;
	public static final Float boilingPointCelsius = 100.f;
	public static final Float freezingPointCelsius = 0.f;
}
