package simulations;

import java.util.ArrayList;

import org.jbox2d.dynamics.contacts.Contact;

public class UnitList {
	
	private P5Canvas p5Canvas;
	private PBox2D box2D;
	
	private UnitBase [] unitList = null;
	private final int UNIT_NUM = 9;
	
	public UnitList(P5Canvas parent, PBox2D world)
	{
		p5Canvas = parent;
		box2D = world;
		unitList = new UnitBase [UNIT_NUM] ;
		
		unitList[0]=new Unit1(p5Canvas,box2D);
		unitList[1] = new Unit2(p5Canvas,box2D);
		unitList[2] = new Unit3(p5Canvas,box2D);
		unitList[3] = new Unit4(p5Canvas,box2D);
		unitList[4] = new Unit5(p5Canvas,box2D);
		unitList[5] = new Unit6(p5Canvas,box2D);
		unitList[6] = new Unit7(p5Canvas,box2D);
		unitList[7] = new Unit8(p5Canvas,box2D);
		unitList[8] = new Unit9(p5Canvas,box2D);
		
	}
	
	public void initialize(int unit)
	{
		if(isExist(unit))
			unitList[unit-1].initialize();
	}
	
	public void applyForce(int unit, int sim, int set)
	{
		if(isExist(unit))
		unitList[unit-1].applyForce(sim, set);
	}
	
	public void computeForces(int unit,int sim,int set) {
		if(isExist(unit))
		unitList[unit - 1].computeForce(sim,set);

	}
	
	public boolean addMolecule(int unit, boolean enable,String compoundName,int count)
	{
		if(isExist(unit))
		return unitList[unit - 1].addMolecules(enable,
				compoundName, count);
		else return false;
	}
	
	public void reset(int unit)
	{
		if(isExist(unit))
		unitList[unit - 1].reset();
	}
	
	public void updateTopBoundary(int unit,int sim,int set)
	{
		if(isExist(unit))
			unitList[unit -1].updateTopBoundary(sim,set);
	}
	
	public void updateMolecules(int unit,int sim, int set)
	{
		if(isExist(unit))
		unitList[unit - 1].updateMolecules(sim,set);
	}
	
	public void beginContact(int unit, Contact c)
	{
		if(isExist(unit))
		unitList[unit -1].beginReaction(c);
	}
	
	public void setupReactionProducts(int unit, int sim,int set)
	{
		if(isExist(unit))
		unitList[unit-1].setupReactionProducts(sim, set);
	}
	
	public void resetDynamicPanel(int unit, int sim, int set) 
	{
		if(isExist(unit))
			unitList[unit-1].resetDynamicPanel(sim, set);
	}
	public void resetCheckboxPanel(int unit, int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].resetCheckboxPanel(sim, set);
	}
	
	public void resetDashboard(int unit,int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].resetDashboard(sim,set);
	}
	
	public void initializeSimulation(int unit, int sim,int set)
	{
		if(isExist(unit))
			unitList[unit-1].initializeSimulation(sim,set);
	}
	
	public void updateOutput(int unit,int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].updateOutput(sim,set);
	}
	
	public void resetTableView(int unit,int sim, int set)
	{
		if(isExist(unit))
			unitList[unit-1].resetTableView(sim,set);
	}
	
	public void customizeInterface(int unit, int sim,int set)
	{
		if(isExist(unit))
			unitList[unit-1].customizeInterface(sim,set);
	}
	
	public float getVolumeMagnifier(int unit)
	{
		if(isExist(unit))
			return unitList[unit-1].getVolumeMagnifier();
		return 1;
	}
	
	public float getDataTickY(int unit, int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		float res =0;
		if(isExist(unit))
			res = unitList[unit-1].getDataTickY(sim, set, indexOfGraph, indexOfCompound);
		return res;
	}
	public float getDataTickX(int unit, int sim,int set,int indexOfGraph)
	{
		float res =0;
		if(isExist(unit))
			res = unitList[unit-1].getDataTickX(sim, set, indexOfGraph);
		return res;
	}
	public ArrayList<String> getNameTableView(int unit, int sim,int set)
	{
		ArrayList<String> res = null;
		if(isExist(unit))
		{
			res = unitList[unit-1].getNameTableView(sim,set);
		}
		return res;
	}
	
	public float getDataTableView(int unit, int sim,int set,int indexOfCompound)
	{
		float res = 0 ;
		if(isExist(unit))
		{
			res = unitList[unit-1].getDataTableView(sim,set,indexOfCompound);
		}
		return res;
	}
	
	public void updateMoleculeCountRelated(int unit, int sim,int set)
	{
		if(isExist(unit))
			unitList[unit-1].updateMoleculeCountRelated(sim,set);
	}
	
	public float getMoleculeDensity(int unit, int sim, int set, String moleName)
	{
		if(isExist(unit))
			return unitList[unit-1].getMoleculeDensity(sim,set,moleName);
		else return 1;
	}
	
	public boolean constrainKineticEnergy(int unit,int sim,int set,float averageKE)
	{
		if(isExist(unit))
			return unitList[unit-1].constrainKineticEnergy(sim,set,averageKE);
		else return false;
	}

	
	public Unit2 getUnit2()
	{
		return (Unit2) unitList[1];
	}
	public Unit3 getUnit3()
	{
		return (Unit3) unitList[2];
	}
	public Unit4 getUnit4()
	{
		return (Unit4) unitList[3];
	}
	
	public Unit5 getUnit5()
	{
		return (Unit5)unitList[4];
	}
	public Unit6 getUnit6()
	{
		return (Unit6)unitList[5];
	}
	public Unit7 getUnit7()
	{
		return (Unit7)unitList[6];
	}
	public Unit8 getUnit8()
	{
		return (Unit8)unitList[7];
	}
	public Unit9 getUnit9()
	{
		return (Unit9)unitList[8];
	}
	
	private boolean isExist(int unit)
	{
		return !(unitList[unit-1]==null);
	}
	
	
	


}
