package simulations.models;

import java.util.ArrayList;
import java.util.HashMap;

import data.DBinterface;

import Util.SVGReader;


// This class maintains all information about all compounds in a Simulation
public class Compound {
	public static ArrayList<String> names=new ArrayList<String>();
	public static ArrayList<Integer> counts = new ArrayList<Integer>();
	public static ArrayList<Integer> caps = new ArrayList<Integer>();
	public static ArrayList<Float> fTemp = new ArrayList<Float>();
	public static ArrayList<Float> bTemp = new ArrayList<Float>();
	public static ArrayList<Float> minLiquidEnergy = new ArrayList<Float>();
	public static ArrayList<Float> rangeLiquidEnergy = new ArrayList<Float>();
	public static ArrayList<Float> minGasEnergy = new ArrayList<Float>();
	public static ArrayList<Float> moleculeWeight = new ArrayList<Float>();
	
	
	public static void reset()
	{
		names.clear();
		counts.clear();
		caps.clear();
		fTemp.clear();
		bTemp.clear();
		minLiquidEnergy.clear();
		rangeLiquidEnergy.clear();
		minGasEnergy.clear();
		moleculeWeight.clear();
	}
	
	public static void setProperties(){
		fTemp = new ArrayList<Float>();
		bTemp = new ArrayList<Float>();
		minLiquidEnergy = new ArrayList<Float>();
		rangeLiquidEnergy = new ArrayList<Float>();
		minGasEnergy = new ArrayList<Float>();
		for (int i=0; i<names.size();i++){
			float freezingTemp = DBinterface.getCompoundFreezingPointCelsius(names.get(i));
			float boilingTemp = DBinterface.getCompoundBoilingPointCelsius(names.get(i));
			fTemp.add(freezingTemp);
			bTemp.add(boilingTemp);
			float[] liquidEnergy = DBinterface.getMinimumLiquidEnergy(names.get(i));
			minLiquidEnergy.add(liquidEnergy[0]);
			rangeLiquidEnergy.add(liquidEnergy[1]);
			float gasEnergy = DBinterface.getMinimumGasEnergy(names.get(i));
			minGasEnergy.add(gasEnergy);
			//Set up molecule weight
			float weight = DBinterface.getCompoundMass(names.get(i));
			moleculeWeight.add(weight);
			
		}
	}
	
	public static int getMoleculeNum(String name)
	{
		int index = names.indexOf(name);
		if(index>=0)
			return counts.get(index);
		else
			return 0;
	}
	
	public static float getMoleculeWeight(String name)
	{
		if(names.contains(name))
		{
			int index = names.indexOf(name);
			return moleculeWeight.get(index);
		}
		else
			return 0;
	}
	
	
	// Get max allowed number of molecules
	public static int getMoleculesCap(String name) {
		int index = names.indexOf(name);
		if(index>=0)
			return caps.get(index);
		else
			return 0;
	}
	public static boolean isIonOfCompound(String ion,String compoundName)
	{
		boolean res= false;
		if(ion.equals("Copper-II"))
		{
			if(compoundName.contains("Copper-II"))
			return true;	
		}
		else if(ion.equals("Sulfate"))
		{
				if(compoundName.contains("Sulfate"))
					return true;
		}
		else if(ion.equals("Silver-Ion"))
		{
			if(compoundName.contains("Silver"))
			return true;
		}
		else if(ion.equals("Potassium-Ion"))
		{
			if(compoundName.contains("Potassium"))
			return true;
		}
		else if(ion.equals("Bromine-Ion"))
		{
			if(compoundName.contains("Bromine")||compoundName.contains("Bromide"))
			return true;
		}
		else if(ion.equals("Ammonium"))
		{
			if(compoundName.contains("Ammonium"))
			return true;
		}
		else if(ion.equals("Chloride"))
		{
			if(compoundName.contains("Chloride"))
			return true;
		}
		else if(ion.equals("Sodium-Ion"))
		{
			if(compoundName.contains("Sodium"))
			return true;
			}
		else if(ion.equals("Carbonate"))
		{
			if(compoundName.contains("Carbonate"))
			return true;
		}
		else if(ion.equals("Hydroxide"))
		{
			if(compoundName.contains("Hydroxide"))
			return true;
		}
		else if(ion.equals("Lithium-Ion"))
		{
			if(compoundName.contains("Lithium"))
			return true;
		}
		else if(ion.equals("Nitrate"))
		{
			if(compoundName.contains("Nitrate"))
			return true;
		}
		return res;
	}
	public static int isIonOfElement(String ionName, Molecule compound)
	{
		//String ionName = ion.getName();
		String compoundName = compound.getName();
		int res  = -1;
		if (!isIonOfCompound(ionName,compoundName))
			return res;
		else
			for(int e=0;e<compound.elementNames.size();e++)
			{
				if(ionName.equals("Ammonium")) //NH4
				{
					if(compound.elementNames.get(e).equals("Nitrogen")) //N is at the center of NH4
					{
						res = e;
						break;
					}
				}
				else if(ionName.startsWith(compound.elementNames.get(e).substring(0,3))) //If they have a same prefix having 4 letters
				{
					res = e;
					break;
				}
			}
			
		return res;
		
	}
	

	
	
}
