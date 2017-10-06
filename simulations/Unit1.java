package simulations;

import static data.State.molecules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.MathFunction;

import data.DBinterface;
import data.State;

import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Molecule.mState;
import simulations.models.Simulation.SpawnStyle;

public class Unit1 extends UnitBase {
	// private Water waterComputation;
	
	private JLabel lblTempTitle;
	private JLabel lblTempValue;
	private JLabel lblActualVolumeTitle;
	private JLabel lblActualVolumeValue;
	private int numMoleculePerMole = 10;
	
	private HashMap<String,Float> compoundDensity;

	public Unit1(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		unitNum = 1;
		setupSimulations();
		// waterComputation = new Water(p5Canvas);
		setupOutputLabels();
		compoundDensity = new HashMap<String,Float>();
	}
	private void setupOutputLabels()
	{
		lblTempTitle = new JLabel ("Temperature:");
		lblTempValue = new JLabel (" \u2103");
		lblActualVolumeTitle = new JLabel("Volume:");
		lblActualVolumeValue = new JLabel();
	}

	@Override
	public void setupSimulations() {
		// TODO Auto-generated method stub
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Water" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Solvent };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 2, 1);
		String[] elements1 = { "Water" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Solvent };
		simulations[1].setupElements(elements1, spawnStyles1);

	}

	@Override
	public void updateMolecules(int sim, int set) {
		Simulation simulation = new Simulation(unitNum,sim,set);

		switch(sim)
		{
		case 4:
			reactH202(simulation);
		break;
		}
	}

	private void reactH202(Simulation simulation) {
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule m1 = (Molecule) p5Canvas.killingList.get(0);
			Molecule m2 = (Molecule) p5Canvas.killingList.get(1);
			Vec2 loc = null;
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				if (i == 0)
					loc = m1.getPosition();
				else
					loc = m2.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				Molecule m = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas, 0);
				//m.setRatioKE(v);
				molecules.add(m);
				if (i == 0)
					m.body.setLinearVelocity(m1.body.getLinearVelocity());
				else {
					m.body.setLinearVelocity(m2.body.getLinearVelocity());
				}
			}
			m1.destroy();
			m2.destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			this.updateCompoundNumber(simulation);
		}
	}

	@Override
	protected void reset() {

		//Reset temperature
		p5Canvas.temp = 25;
		compoundDensity.clear();
		
		//Reset output Labels
		lblTempValue.setText(" \u2103");

		switch (p5Canvas.getSim()) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			// p5Canvas.setRestitutionDamp(true);
			break;
		case 4:
			// p5Canvas.setRatioKE(4.0f); // Hydrogen-Peroxide
			break;
		case 5:
			break;
		}

	}
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		//Customization
		switch(p5Canvas.getSim())
		{
		case 1:
			break;
		case 2:
			p5Canvas.getMain().heatSlider.setEnabled(false);
			break;
		case 3:
			break;
		case 4:
				break;
		case 5:
			p5Canvas.getMain().heatSlider.setEnabled(false);

			break;
		
		}


	}

	public void beginReaction(Contact c) {
		reactAfterContact(c);
	}

	@Override
	protected void computeForce(int sim, int set) {
		
		this.clearAllMoleculeForce();

		switch (sim) {
		case 1:
		case 3:
			computeForceGeneric(sim,set);
			break;
		case 4:
			computeForceSim4Set1(sim,set);
			break;
		case 2:
			if (set == 7)
				computeForceSiO2();
			else if( set==6)
				computeForceSilver();
			else
				computeForceGeneric(sim,set);
			break;

		case 5:
			computeForceGeneric(sim,set);
			if(set==1) 	{//Bromine and water
				computeForceBromine(sim,set);
				computeForceTopBoundary();
				}
			else if (set ==4) { //Pentane and water
				computeForcePentane(sim,set);
			}
			else if( set==5) {
				computeForceBromine(sim,set);
			}
			else if( set==6) {
				
				computeForceBromine(sim,set);
				computeForcePentane(sim,set);
				computeForceTopBoundary();

			}

			break;

		}

	}

	public void computeForceGeneric(int sim, int set) {
		//Molecule moleThis = null;
		Vec2 locThis = new Vec2();
		//Molecule moleOther = null;
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;
		// float scale = 3000;

		for (Molecule moleThis: State.getMolecules()) {

			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (Molecule moleOther: State.getMolecules()) {
				if (moleThis==moleOther
						|| (!moleThis.getName().equals(moleOther.getName()) && !moleOther
								.getName().equals("Water")))
					// Only have forces on the same kind of molecule
					continue;
				locOther = moleOther.getPosition();
				if (locOther == null || locThis == null)
					continue;
				
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = MathFunction.normalizeForce(new Vec2(x, y));

				//Add attractive force to same kind molecule
				if(moleThis.getName().equals(moleOther.getName()))
				{
					
					float fTemp = moleThis.freezingTem;
					float bTemp = moleThis.boilingTem;
					float gravityX, gravityY;
					if (p5Canvas.temp >= bTemp) { // Gas case
						gravityX = 0;
						gravityY = 0;
					} else if (p5Canvas.temp <= fTemp) { // Solid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 2f;
					} else { // Liquid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 0.6f;
					}
					forceX = (-direction.x / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;

					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {

						// Water case
						if (moleThis.getName().equals("Water")) {
							if (thisE == 2) {
								moleThis.sumForceX[thisE] += forceX * 3000;
								moleThis.sumForceY[thisE] += forceY * 3000;
							}
						}
						// Hydrogen-Peroxide case
						else if (moleThis.getName().equals("Hydrogen-Peroxide")) {
							if(!((sim==5&&set==3)||(sim==5&&set==5) ))
							{
								if (thisE == 2 || thisE == 3) {
									moleThis.sumForceX[thisE] += forceX * 1200;
									moleThis.sumForceY[thisE] += forceY * 1200;
								}
							}
						} 

						else if (moleThis.getName().equals("Mercury")) {
							moleThis.sumForceX[thisE] += forceX * 300;
							moleThis.sumForceY[thisE] += forceY * 300;
						} else if (moleThis.getName().equals("Bromine")) {
							if(!((sim==5&&set==1)||(sim==5&&set==5)))
							{
							moleThis.sumForceX[thisE] += forceX * 100;
							moleThis.sumForceY[thisE] += forceY * 100;
							}
						}
						// Silver case
						else if (moleThis.getName().equals("Silver")) {
							moleThis.sumForceX[thisE] += forceX * 1000;
							moleThis.sumForceY[thisE] += forceY * 1000;
						}

						else {
							moleThis.sumForceX[thisE] += forceX * 30;
							moleThis.sumForceY[thisE] += forceY * 30;
						}

					}
				}
				//If mixture, add attractive force to both water and this molecule
				else {
					if(moleThis.getName().equals("Water"))
						continue;
					forceX =(-direction.x/disSquare)*1f;
					forceY =(-direction.y / disSquare)*1f;
				for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {

						// Hydrogen-Peroxide case
						if (moleThis.getName().equals("Hydrogen-Peroxide")) {
							if (thisE == 2 || thisE == 3) {
								moleThis.sumForceWaterX[thisE] += forceX ;
								moleThis.sumForceWaterY[thisE] += forceY ;
								//Add reversed force on water
								moleOther.sumForceWaterX[2]-=forceX;
								moleOther.sumForceWaterY[2]-=forceY;

							}
						} else if (moleThis.getName().equals("Pentane")) 
						{
							if(!((sim==5&&set==4)||(sim==5&&set==6)))
							{
							float rate = 0.3f;
							//moleThis.sumForceWaterX[thisE] += forceX*rate ;
							//moleThis.sumForceWaterY[thisE] += forceY*rate ;
							}
							
						}

						else if (moleThis.getName().equals("Mercury")) {
							//Mercury doesnt mix with water
							
						} else if (moleThis.getName().equals("Bromine")) {
							moleThis.sumForceWaterX[thisE] += forceX*0.2 ;
							moleThis.sumForceWaterY[thisE] += forceY ;
							//Double the force when at the bottom
							if(box2d.coordWorldToPixels(locThis).y > 2*p5Canvas.h/3)
							{
								moleThis.sumForceWaterX[thisE] += forceX*1 ;
								moleThis.sumForceWaterY[thisE] += forceY*1 ;
								
							}
						}
						// Silver case
						else if (moleThis.getName().equals("Silver")) {
							moleThis.sumForceWaterX[thisE] += forceX ;
							moleThis.sumForceWaterY[thisE] += forceY ;
						}

						else {
							moleThis.sumForceWaterX[thisE] += forceX ;
							moleThis.sumForceWaterY[thisE] += forceY ;
						}

					}

				} 

			}
		}
	}
	
	
	public void computeForceSilver() {
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;
		float scale = 4f;

		for (Molecule moleThis: State.getMolecules()) {

			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (Molecule moleOther: State.getMolecules()) {
				if (moleThis==moleOther)
					continue;
				locOther = moleOther.getPosition();
				if (locOther == null || locThis == null)
					continue;
				
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = MathFunction.normalizeForce(new Vec2(x, y));

				//Add attractive force to same kind molecule
				if(moleThis.getName().equals(moleOther.getName()))
				{
					
					float fTemp = moleThis.freezingTem;
					float bTemp = moleThis.boilingTem;
					float gravityX, gravityY;
					if (p5Canvas.temp >= bTemp) { // Gas case
						gravityX = 0;
						gravityY = 0;
					} else if (p5Canvas.temp <= fTemp) { // Solid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 2f;
					} else { // Liquid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 0.6f;
					}
					forceX = (-direction.x * disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y * disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;

					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {
						// Silver case
							moleThis.sumForceX[thisE] += forceX*scale ;
							moleThis.sumForceY[thisE] += forceY*scale ;
					}
				}

			}
		}
	}
	
	public void computeForceSim4Set1(int sim, int set) {
		//Molecule moleThis = null;
		Vec2 locThis = new Vec2();
		//Molecule moleOther = null;
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;
		// float scale = 3000;

		for (Molecule moleThis: State.getMolecules()) {

			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (Molecule moleOther: State.getMolecules()) {
				if (moleThis==moleOther
						|| (!moleThis.getName().equals(moleOther.getName()) && !moleOther
								.getName().equals("Water")))
					// Only have forces on the same kind of molecule
					continue;
				locOther = moleOther.getPosition();
				if (locOther == null || locThis == null)
					continue;
				
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = MathFunction.normalizeForce(new Vec2(x, y));

				//Add attractive force to H2O2 and H2O
				if(moleThis.getName().equals(moleOther.getName()))
				{
					float fTemp = moleThis.freezingTem;
					float bTemp = moleThis.boilingTem;
					float gravityX, gravityY;
					if (p5Canvas.temp >= bTemp) { // Gas case
						gravityX = 0;
						gravityY = 0;
					} else if (p5Canvas.temp <= fTemp) { // Solid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 2f;
					} else { // Liquid case
						gravityY = (bTemp - p5Canvas.temp) / (bTemp - fTemp);
						gravityX = gravityY * 0.6f;
					}
					forceX = (-direction.x / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;

					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {

						// Water case
						if (moleThis.getName().equals("Water")) {
							if (thisE == 2) {
								moleThis.sumForceX[thisE] += forceX * 3000;
								moleThis.sumForceY[thisE] += forceY * 3000;
							}
						}
						// Hydrogen-Peroxide case
						else if (moleThis.getName().equals("Hydrogen-Peroxide")) {
							if(!((sim==5&&set==3)||(sim==5&&set==5) ))
							{
								if (thisE == 2 || thisE == 3) {
									moleThis.sumForceX[thisE] += forceX * 1200;
									moleThis.sumForceY[thisE] += forceY * 1200;
								}
							}
						} 


					}
				}
				//If mixture, add attractive force to both water and this molecule
				else {
					if(moleThis.getName().equals("Water"))
						continue;
					forceX =(-direction.x/disSquare)*1f;
					forceY =(-direction.y / disSquare)*1f;
				for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {

						// Hydrogen-Peroxide case
						if (moleThis.getName().equals("Hydrogen-Peroxide")) {
							if (thisE == 2 || thisE == 3) {
								moleThis.sumForceWaterX[thisE] += forceX ;
								moleThis.sumForceWaterY[thisE] += forceY ;
								//Add reversed force on water
								moleOther.sumForceWaterX[2]-=forceX;
								moleOther.sumForceWaterY[2]-=forceY;

							}
						} 

					}

				} 

			}
		}
	}
	
	
	//Add some up forces to bromine so that they wont stay at the bottom
	//Add repulsive force for Sim 5 set 6
	public void addForceBromine(int sim, int set)
	{
		Molecule moleThis = null;
		Molecule moleOther = null;
		float gravityCompensation = 0.0000007f;
		float diffY=0.0f;
		float forceY =1.0f;
		float forceRepulsive = 0.025f;
		if(sim==5&&set==6)
			forceRepulsive = 0.05f;
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		for( int i=0;i<State.molecules.size();i++)
		{
			moleThis = State.molecules.get(i);
			if(moleThis.getName().equals("Bromine"))
			{
				
				for(int element=0;element<moleThis.getNumElement();element++)
				{
					//Add up force
					locThis.set(moleThis.getElementLocation(element));
					diffY = p5Canvas.y+p5Canvas.h-box2d.scalarWorldToPixels(locThis.y);
					forceY = gravityCompensation*diffY*diffY;
					moleThis.sumForceY[element]+=forceY;
					
				
						for( int k=0;k<State.molecules.size();k++)
						{
							moleOther = State.molecules.get(k);
							if(moleOther.getName().equals("Bromine"))
							{
								locOther = moleOther.getPosition();
								Vec2 vecDiff = locThis.sub(locOther);
								vecDiff = MathFunction.normalizeForce(vecDiff);
								moleThis.sumForceX[element]+= vecDiff.x * forceRepulsive;
								moleThis.sumForceY[element]+= vecDiff.y * forceRepulsive;
							} 
						}
					
					
				}
			}
		}
	}
	
	//Compute force for Bromine in Sim 5 set 1 and set 6
	public void computeForceBromine(int sim, int set)
	{
		
		float topBoundary = 2*p5Canvas.h/3;
		float gravityCompensation = 0.5f;
		float forceRepulsive = 2.5f;
		float distSquare = 0f;
 
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		//First remove the force that ComputeForceGeneration added
		for(Molecule mole:State.getMoleculesByName("Bromine"))
		{
			if(mole.getPositionInPixel().y>topBoundary)
			{
				//mole.clearForce();
				locThis.set(mole.getPosition());
				
				//Add some up force
				for(int e =0;e<mole.getNumElement();e++)
				{
					mole.sumForceY[e]+=gravityCompensation;
				
					//Add repulsive force if there are any other bromine around
					for( Molecule moleOther:State.getMoleculesByName("Bromine"))
					{
						if(moleOther !=mole)
						{
							
								locOther.set(moleOther.getPosition());
								Vec2 vecDiff = locThis.sub(locOther);
								distSquare = vecDiff.x* vecDiff.x + vecDiff.y* vecDiff.y;
								vecDiff = MathFunction.normalizeForce(vecDiff);
								mole.sumForceX[e]+= 1f/distSquare*vecDiff.x * forceRepulsive*2;
								mole.sumForceY[e]+= 1f/distSquare*vecDiff.y * forceRepulsive;
							
						}
					}
				}
			}
			
		}
		
		
		
	}
	
	private void computeForceTopBoundary()
	{
			float topBoundary = p5Canvas.h/2;
			float gravityCompensation = 0.2f;
			float gravityScale = 0.01f;
			// Check positions of all liquid molecules, in case they are not going
			// to high
			for(Molecule mole:State.getMolecules())
			{
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

//				if(mole.getName().equals("Water"))
				{
					if (pos.y < topBoundary && mole.getLinearVelocity().y>0) {
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																						// element
							mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
			
						}
					}
				}
			}
	}
	
	//Add some up forces to pentane so that they will always be on top of water
	public void computeForcePentane(int sim, int set)
	{
		Molecule moleThis = null;
		float gravityCompensation = 0.010f;
		float topBoundary = 2*p5Canvas.h/3;
		float forceRepulsive = 0.25f;
		float distSquare = 0f;

		//float diffY=0.0f;
		
		float forceYTop =0.002f;
		float forceYBot =0.05f;
		if(sim==5&&set==6)
		{
			gravityCompensation = 0.010f;
			forceYTop =  0.01f;
			forceYBot =0.08f;
			 forceRepulsive = 0.8f;

		}
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		for( int i=0;i<State.molecules.size();i++)
		{
			moleThis = State.molecules.get(i);
			if(moleThis.getName().equals("Pentane"))
			{

					
				for(int e=0;e<moleThis.getNumElement();e++)
				{
					locThis.set(moleThis.getElementLocation(e));
					moleThis.sumForceY[e] += gravityCompensation; //Add gravity compensation to Pentane
					if(moleThis.getPositionInPixel().y > topBoundary) //There is no force is Pentane is above 2h/3
					{	
						//Add up force if it is beneath water
						for( Molecule moleOther:State.getMoleculesByName("Water"))
						{	
							
								locOther.set(moleOther.getPosition());
								if( Math.abs(locThis.x-locOther.x)<10)
								{
									if(locOther.y>locThis.y&&locOther.y - locThis.y<=1 ) //If there is any water molecule on top of pentane
									{
										moleThis.sumForceWaterY[e] += forceYTop;
									}
									else if(locOther.y< locThis.y && locThis.y - locOther.y<=1)
									{
										moleThis.sumForceWaterY[e] += forceYBot;
									}
									
								}
							
						}
						//Add repulsive force if there are any other pentane around
						for( Molecule moleOther:State.getMoleculesByName("Pentane"))
						{
							if(moleOther !=moleThis)
							{
								
									locOther.set(moleOther.getPosition());
									Vec2 vecDiff = locThis.sub(locOther);
									distSquare = vecDiff.x* vecDiff.x + vecDiff.y* vecDiff.y;
									vecDiff = MathFunction.normalizeForce(vecDiff);
									moleThis.sumForceX[e]+= 1f/distSquare*vecDiff.x * forceRepulsive*2;
									//moleThis.sumForceY[e]+= 1f/distSquare*vecDiff.y * forceRepulsive;
								
							}
						}
				}
					
					
				}
			
			}
		}
	}

	public void applyForce(int sim, int set) {
		super.applyForce(sim, set);
	}

//	//Normalize the input force
//	public Vec2 normalizeForce(Vec2 v) {
//		float dis = (float) Math.sqrt(v.x * v.x + v.y * v.y);
//		return new Vec2(v.x / dis, v.y / dis);
//	}

	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		// TODO Auto-generated method stub
		boolean res = false;
		// TO DO: Check if molecules are in gas or water
		if (compoundName.equals("Silicon-Dioxide"))
			res = addSiO2(compoundName, count, box2d, p5Canvas);
		else
			res = addWaterMolecules(isAppEnable, compoundName, count);
		return res;
	}
	
	/******************************************************************
	 * FUNCTION : addPentane DESCRIPTION : Specific function used to add Pentane
	 * 
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: None
	 *******************************************************************/
	/*
	public boolean addPentane(String compoundName, int count) {

		boolean res = true;
		int creationCount = 0;

		if (p5Canvas.isEnable) // if Applet is enable
			creationCount = 0;
		else
			creationCount++;
		// variables are used to distribute molecules
		int mod = creationCount % 4; // When the system is paused; Otherwise,
										// molecules are create at the same
										// position

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		// boolean dimensionDecided = false;
		int k = 0;
		for (k = 1; k < 10; k++) {
			if (count <= (k * k)) {
				dimension = k;
				break;
			}
		}
		int rowNum = count / dimension + 1;
		int colNum = (int)Math.ceil((float)count/rowNum);
		boolean isClear = false;
		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		float increX = p5Canvas.w / 12;

		// Initializing
		centerX = p5Canvas.x + moleWidth / 2;
		centerY = p5Canvas.y + moleHeight - p5Canvas.boundaries.difVolume;
		topLeft = new Vec2(centerX - 0.5f * moleWidth, centerY - 0.5f
				* moleHeight);
		botRight = new Vec2(centerX + colNum * moleWidth, centerY + rowNum
				* moleHeight);
		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.

		while (!isClear) {
			// Specify new add area.

			// Reset flag
			isClear = true;

			for (int m = 0; m < molecules.size(); m++) {

				if (!((String) molecules.get(m).getName()).equals("Water")) {
					molePos.set(molecules.get(m).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft = new Vec2(centerX - moleWidth / 2, centerY
						- moleHeight);
				botRight = new Vec2(centerX + colNum * moleWidth, centerY
						+ rowNum * moleHeight);

				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w)
						|| topLeft.x < p5Canvas.x) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if (res) {
			// Add molecules into p5Canvas
			for (int i = 0; i < count; i++) {

				x_ = centerX + i % dimension * moleWidth + creationCount;
				y_ = centerY + i / dimension * moleHeight;

				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0));
			}
		}

		return res;
	}
*/
	
	/******************************************************************
	 * FUNCTION : reactAfterContact DESCRIPTION : react function after collision
	 * detected Called by beginContact()
	 * 
	 * INPUTS : c( Contact) OUTPUTS: None
	 *******************************************************************/
	private void reactAfterContact(Contact c) {
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();

		if (o1 == null || o2 == null)
			return;
		// What class are they? Box or Particle?
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		if (c1.contains("Molecule") && c2.contains("Molecule")) {
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;
			ArrayList<String> reactants = new ArrayList<String>();
			reactants.add(m1.getName());
			reactants.add(m2.getName());
			if (p5Canvas.temp >= 110) {
				float random = p5Canvas.random(110, 210);
				if (random < p5Canvas.temp) {
					p5Canvas.products = getReactionProducts(reactants);
					if (p5Canvas.products != null
							&& p5Canvas.products.size() > 0) {
						p5Canvas.killingList.add(m1);
						p5Canvas.killingList.add(m2);
					}
				}
			}
		}

	}

	/******************************************************************
	 * FUNCTION : getReactionProducts DESCRIPTION : Reture objects based on
	 * input name Called by beginContact
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants) {
		if (reactants.get(0).equals("Hydrogen-Peroxide")
				&& reactants.get(1).equals("Hydrogen-Peroxide")) {
			ArrayList<String> products = new ArrayList<String>();
			products.add("Water");
			products.add("Water");
			products.add("Oxygen");
			return products;
		} else {
			return null;
		}
	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		if ((sim == 2 && set == 2) || (sim == 4 && set == 1)) {
			Compound.names.add("Water");
			Compound.counts.add(0);
			Compound.names.add("Oxygen");
			Compound.counts.add(0);
		}
	}

	@Override
	public void initialize() {
		// Set up speed ratio for molecules
		setupSpeed();

		switch (p5Canvas.getSim()) {
		case 3:
			// p5Canvas.setRestitutionDamp(true);
			break;
		case 4:
			break;
		}

	}
	
	//Set up speed ratio for molecules
	//Called by reset()
	public void setupSpeed()
	{
		String name = null;
		Molecule mole = null;
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		for(int i =0;i<State.molecules.size();i++)
		{
			mole = State.molecules.get(i);
			name = new String(mole.getName());
			if(name.equals("Water"))
			{
				if((sim==5 && set==1)|| (sim==5 && set==6))
					mole.setRatioKE(1.0f/1.5f);
			}
			else if (name.equals("Hydrogen-Peroxide"))
				mole.setRatioKE(0.25f);
			else if (name.equals("Pentane"))
				mole.setRatioKE(1.0f/6);
			else if (name.equals("Mercury"))
				mole.setRatioKE(1.0f/12);
			else if (name.equals("Bromine"))
				mole.setRatioKE(1.0f/12);
			else if (name.equals("Silver"))
				mole.setRatioKE(1.0f/12);
			else if (name.equals("Silicon-Dioxide"))
				mole.setRatioKE(1.0f/4);
		}
	}

	/******************************************************************
	 * FUNCTION : addSiO2 DESCRIPTION : Specific function used to add addSiO2,
	 * Called by addMolecule() The shape of molecule cluster is like a pyramid
	 * INPUTS : CompoundName(String), count(int),
	 * box2d_(PBox2D),parent_(P5Canvas) OUTPUTS: boolean
	 *******************************************************************/
	public boolean addSiO2(String compoundName_, int count, PBox2D box2d_,
			P5Canvas parent_) {

		boolean res = true;
		int numRow = 1;
		int sum = 0;
		for (int i = 1; i <= 6; i++) {
			sum = (numRow * (numRow + 1)) / 2;
			if (sum >= count)
				break;
			else
				numRow++;
		}

		int numCol = numRow;

		Vec2 size1 = Molecule.getShapeSize(compoundName_, parent_);

		float centerX = p5Canvas.x + 50; // X coordinate around which we are
											// going to add
		// Ions, 260 is to make SiO2 spawn in the middle
		float centerY = p5Canvas.y + 80 - p5Canvas.boundaries.difVolume; // Y
																			// coordinate
																			// around
		// which we are going to
		// add Ions
		Vec2 topLeft = new Vec2(centerX, centerY);
		Vec2 botRight = new Vec2();
		boolean isClear = false;

		topLeft.set(centerX, centerY);
		botRight.set(centerX + numCol * size1.x, centerY + numRow * size1.y);
		float increX = p5Canvas.w / 3;
		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);

		topLeft.set(centerX, centerY);
		botRight.set(centerX + numCol * size1.x, centerY + numRow * size1.y);

		while (!isClear) {

			isClear = true;
			for (int k = 0; k < molecules.size(); k++) {

				if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));

					if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
						isClear = false;
						break;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft.set(centerX, centerY);
				botRight.set(centerX + numCol * size1.x, centerY + numRow
						* size1.y);
				// If we have gone through all available areas.
				if (centerX > (p5Canvas.x + p5Canvas.w)) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}

		if (res) {
			int curRow = 1; // row number
			int rowSum = 0; // Max number of compound based on curRow
			int rowSumNext = 1; // Max number of compound based on next Row
			float x = 0;
			float y = 0;
			float angle = 0;
			int midOddCol = 0; // mid column ID of current odd row
			float midEvenCol = 0; // mid column ID of current even row
			int idOnCurRow = 0; // Id of i on current row
			float midX = centerX + numCol * size1.x / 2;
			for (int i = 0; i < count; i++) {

				idOnCurRow = i - rowSum + 1;
				y = centerY + ((float) curRow - 0.5f) * size1.y;

				// Odd row alignment
				if (curRow % 2 != 0) {
					midOddCol = curRow / 2 + 1;
					x = midX + (idOnCurRow - midOddCol) * size1.x;
				} else // Even row alignment
				{
					midEvenCol = (float) curRow / 2 + 0.5f;
					x = midX + (idOnCurRow - midEvenCol) * size1.x;
				}

				if ((i + 1) >= rowSumNext) {
					rowSum = rowSumNext;
					curRow++;
					rowSumNext = (curRow * (curRow + 1)) / 2;
				}

				angle = 0;
				molecules.add(new Molecule(x, y, compoundName_, box2d_,
						parent_, angle));

			}
		}

		return res;
	}

	public void computeForceSiO2() { // draw
		// background
		float rateX = 25;
		float rateY = 12;
		for (int n = 0; n < molecules.size(); n++) {
			Molecule mole = molecules.get(n);
			if (mole.getName().equals("Silicon-Dioxide")) {
				for (int e = 0; e < mole.getNumElement(); e++) {
					int indexCharge = mole.elementCharges.get(e);
					Vec2 locIndex = mole.getElementLocation(e);
					mole.sumForceX[e] = 0;
					mole.sumForceY[e] = 0;
					for (int i = 0; i < molecules.size(); i++) {
						if (i == n)
							continue;
						Molecule m = molecules.get(i);
						if (m.getName().equals("Water"))
							continue;

						float forceX;
						float forceY;
						for (int e2 = 0; e2 < m.getNumElement(); e2++) {
							Vec2 loc = m.getElementLocation(e2);
							if (loc == null || locIndex == null)
								continue;
							float x = locIndex.x - loc.x;
							float y = locIndex.y - loc.y;
							float dis = x * x + y * y;
							forceX = (float) ((x / Math.pow(dis, 1.5)) * rateX);
							forceY = (float) ((y / Math.pow(dis, 1.5)) * rateY);

							int charge = m.elementCharges.get(e2);
							int mul = charge * indexCharge;
							if (mul < 0) {
								mole.sumForceX[e] += mul * forceX;
								mole.sumForceY[e] += mul * forceY;
							} else if (mul > 0) {
								mole.sumForceX[e] += mul * forceX
										* mole.chargeRate;
								mole.sumForceY[e] += mul * forceY
										* mole.chargeRate;
							}
						}
					}
				}
			}
		}
	}
	
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim,set);
		JPanel dashboard = p5Canvas.getMain().dashboard;
		lblTempValue.setText("25 \u2103");
		switch(sim)
		{
		case 1:
			dashboard.add(lblTempTitle," cell 0 1, alignx right");
			dashboard.add(lblTempValue,"cell 1 1");
			break;
		case 2:
			dashboard.add(lblActualVolumeTitle,"cell 0 1, alignx right");
			dashboard.add(lblActualVolumeValue,"cell 1 1");
			break;
		case 3:
			dashboard.add(lblTempTitle," cell 0 1, alignx right");
			dashboard.add(lblTempValue,"cell 1 1");
			break;
		case 4:
			dashboard.add(lblTempTitle," cell 0 1, alignx right");
			dashboard.add(lblTempValue,"cell 1 1");
			break;
		case 5:
			break;
			default:
				break;
		}

	}
	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;
		if (lblTempValue.isShowing()) {
			output = myFormatter.format(p5Canvas.temp);
			lblTempValue.setText(output + " \u2103");
		}
		
		if(lblActualVolumeValue.isShowing()){
			Molecule mole = State.getMoleculeByIndex(0);
			String name = mole.getName();
			if(name!=null)
			{
				float mass = State.getCompoundsMass()/numMoleculePerMole*1000; //Get total mass of sim
				float density = 0;
				if(compoundDensity.containsKey(name))
					density = compoundDensity.get(name);
				else
				{
					density = DBinterface.getCompoundDensity(name);
					compoundDensity.put(name, density);
				}
				float volume = mass/density;
				myFormatter.applyPattern("###.#");
				output = myFormatter.format(volume);
				lblActualVolumeValue.setText(output+" mL");
			}
		}
		
	}
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		return super.getDataTickY(sim, set, indexOfGraph, indexOfCompound);

	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		return super.getDataTableView(sim, set, indexOfCompound);
	}
	
	//Function to return the correct compound name on the 3rd column of TableView
	public ArrayList<String> getNameTableView(int sim, int set)
	{
		ArrayList<String> res = super.getNameTableView(sim, set);
		
		switch(sim){
		case 2:
			if(set==2)
			{
				if(res.contains("Oxygen"))
					res.remove("Oxygen");
				if(res.contains("Water"))
					res.remove("Water");
			}
			break;
			default:
				break;
		}

		
		return res;
	}
	
	
	//Constrain the kinetic energy of molecules by sim itself
	public boolean constrainKineticEnergy(int sim,int set,float averageKE)
	{
		// First, sum up all average Energy to get total KE
		float idealKE = 0;
		// Second, find out the ratio of ideal stable KE to current real KE
		float currentKE = 0;
		int countNum = 0;
		
		for (Molecule mole: State.getMolecules()) {
			if(p5Canvas.temp>=0 || mole.getState() != mState.Gas)
			{
				float ke = mole.getKineticEnergy();
				currentKE += ke;
				countNum++;
			}
		}
		
		idealKE = countNum * averageKE;
		float ratio = idealKE / currentKE;
		// Third, each molecule`s KE multiplied with ratio
		for (Molecule mole: State.getMolecules()) {
			//Not apply to those whose energy is above average too much
//			if(mole.getKineticEnergy()<(averageKE*1.25f))
			 if( p5Canvas.temp>=0 || mole.getState() != mState.Gas )
			 {
				mole.constrainKineticEnergy(ratio);
			 }
		}
		
		//After 
		return true;
	}
	
	@Override
	protected void initializeSimulation(int sim, int set) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateMoleculeCountRelated(int sim, int set) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
