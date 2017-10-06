/*
 * Unit 7: Chemical Equilibrium
 */
package simulations;

import static data.State.molecules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import main.TableView;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import data.DBinterface;
import data.State;

import simulations.models.Anchor;
import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

public class Unit7 extends UnitBase {
	
	// Output labels
//	private JLabel lblConText1; // Concentration output
//	private JLabel lblConValue1;
//	private JLabel lblConText2;
//	private JLabel lblConValue2;
//	private JLabel lblConText3;
//	private JLabel lblConValue3;
//	private JLabel lblConText4;
//	private JLabel lblConValue4;

	private JLabel lblVolumeText;
	private JLabel lblVolumeValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	private JLabel lblPressureText;
	private JLabel lblPressureValue;
	private JLabel lblKeqText;
	private JLabel lblKeqValue;
	
	private HashMap<String, Float> moleculeConHash;
	private int numMoleculePerMole = 10;

	boolean catalystAdded = false;
	boolean inertAdded = false;
	float keq = 0.01f;
	float keqOutput = 0;       //The parameter to how keq on the screen
	float defaultKeq =0.01f;
	float reactionProbability =0.6f;  //Probability of reaction in Sim 2
//	float breakProbability = 0.75f; // The chance that N2O4 will break apart
	float reactProbability =0.5f;
	int oldTime = -1;
	int curTime = -1;
	boolean forceUpdated = false;
	int addedNO2=0;
	int addedN2O4=0;
	//float initialPressure;
	float defaultPressure = 101.33f;
	int defaultVolume = 60;
	float defaultTemp = 25;
	
	//Keq change setting based on temp
	private int [] tempArray = {0,25,77,127,220};
	private float [] keqArray = {10.8967f,8.918f,6.4430f,5.1045f,3.68f};

	public Unit7(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 7;
		//moleculeNumHash = new HashMap<String, Integer>();
		moleculeConHash = new HashMap<String, Float>();
		setupSimulations();
		setupOutputLabels();	
		}

	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Sulfur", "Oxygen" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.SolidPavement, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Nitrogen-Dioxide" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 1, 3);
		String[] elements2 = { "Hydrogen", "Oxygen" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas,
				SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 1, 4);
		String[] elements3 = { "Phosphorus-Pentachloride"};
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 2, 1);
		String[] elements4 = { "Nitrogen-Dioxide","Dinitrogen-Tetroxide","Catalyst","Inert" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		ArrayList<String> products = new ArrayList<String>();
		if (true) {
			products = DBinterface.getReactionOutputs(this.unitNum, sim, set);
			if (products != null) {
				for (String s : products) {
					if (!Compound.names.contains(s)) {
						Compound.names.add(s);
						Compound.counts.add(0);
						Compound.caps.add(95);
					}
				}
			}
		}
	}

	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false;
		Simulation simulation = getSimulation(sim, set);
		switch(sim)
		{
		case 1:
			switch(set)
			{
			case 1:
				reactionHappened = reactSim1Set1(simulation);
				break;
			case 2:
				reactionHappened = reactSim1Set2(simulation);
				break;
			case 3:
				reactionHappened = reactSim1Set3(simulation);
				break;
			case 4:
				reactionHappened = reactSim1Set4(simulation);
				break;
			}
			break;
		case 2:
			reactionHappened = reactSim2Set1(simulation);
			break;
		}
		
		if(reactionHappened)
		{
			updateMoleculeCon();
		}
	}
	
	private boolean reactSim1Set1(Simulation simulation)
	{
		float yVelocity =10f;
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				velocity.addLocal(0, yVelocity);
					mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			return true;
		}
		return false;
	}

	//Sim 1 Set 4
	//PCl3 + Cl2 --> PCl5
	private boolean reactSim1Set4(Simulation simulation) {
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float smallDecimal = 0.000001f;
		Random rand = new Random();
		if (rand.nextFloat() > reactProbability) {
			 return false;
		}
		
		float conPCl3 = getConByName("Phosphorus-Trichloride")+smallDecimal;  //Plus small number to avoid 0
		float conPCl5 = getConByName("Phosphorus-Pentachloride")+smallDecimal;
		float conCl2 = getConByName("Chlorine")+smallDecimal;
		float currentRatio = conPCl5/(conPCl3*conCl2);
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && currentRatio<keq) {

			int numToKill = p5Canvas.killingList.size();
			int pCl3Index  =-1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if(mOld[i].getName().equals("Phosphorus-Trichloride"))
					pCl3Index = i;
			}

			Molecule mNew = null;
			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[pCl3Index].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1/simulation.getSpeed());
				molecules.add(mNew);

				if (i == 0)
					mNew.body.setLinearVelocity(mOld[0].body
							.getLinearVelocity());

				else {
					mNew.body.setLinearVelocity(mOld[0].body
							.getLinearVelocity());
				}
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();

			//updateCompoundNumber(simulation);
			int indexReactant = Compound.names.indexOf("Phosphorus-Pentachloride");
			Compound.counts.set(indexReactant, Compound.counts.get(indexReactant)+1);
			int indexProduct = Compound.names.indexOf("Phosphorus-Trichloride");
			Compound.counts.set(indexProduct, Compound.counts.get(indexProduct)-1);
			int indexProduct2 = Compound.names.indexOf("Chlorine");
			Compound.counts.set(indexProduct2, Compound.counts.get(indexProduct2)-1);
			
			updateMoleculeCon();  //Update Molecule Concentration for break apart check
		}

		 conPCl3 = getConByName("Phosphorus-Trichloride")+smallDecimal;
		 conPCl5 = getConByName("Phosphorus-Pentachloride")+smallDecimal;
		 conCl2 = getConByName("Chlorine")+smallDecimal;
		 currentRatio = conPCl5/(conPCl3*conCl2);
		
			if (currentRatio>keq) // If PCl5 is over numberred,break them up			
				{
					 return breakApartN2O4(simulation);
				}
		return true;
	}

	private boolean reactSim1Set3(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int oxygenIndex  =-1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if(mOld[i].getName().equals("Oxygen"))
					oxygenIndex = i;
			}

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[oxygenIndex].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				molecules.add(mNew);

				if (i == 0)
					mNew.body.setLinearVelocity(mOld[0].body
							.getLinearVelocity());

				else {
					mNew.body.setLinearVelocity(mOld[0].body
							.getLinearVelocity());
				}
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();

			updateCompoundNumber(simulation);
			return true;
		}
		return false;

	}

	//NO2 <--> N2O4
	private boolean reactSim1Set2(Simulation simulation) {
		
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		//Update equilibrium
		updateEquilibrium();
		
		if(catalystAdded)  //Speed up reaction by increasing chance
			reactProbability = 1.0f;
		else
			reactProbability =0.6f;
		
		Random rand = new Random();
		if (rand.nextFloat() > reactProbability) {
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			 return false;
		}


		//2NO2 == N2O4
		float conN2O4 = getConByName("Dinitrogen-Tetroxide");
		float conNO2 = getConByName("Nitrogen-Dioxide");
		float currentRatio = conN2O4/(conNO2*conNO2);
//		System.out.println("Current ratio is "+currentRation);
		
		if (!p5Canvas.killingList.isEmpty()) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0 && (currentRatio <= keq)) {
				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++) {
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				}

				Molecule mNew = null;
				String nameNew = null;
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					// Reacts at the postion of nitrylChloride
					Vec2 loc = mOld[0].getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					nameNew = p5Canvas.products.get(i);

					mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
							p5Canvas, (float) (Math.PI / 2));
					mNew.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(mNew);

					mNew.body.setLinearVelocity(mOld[i / 2].body
							.getLinearVelocity());
				}
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				
				//Update molecule number
				int index = Compound.names.indexOf("Dinitrogen-Tetroxide");
				Compound.counts.set(index, Compound.counts.get(index)+1);
				index = Compound.names.indexOf("Nitrogen-Dioxide");
				Compound.counts.set(index, Compound.counts.get(index)-2);

				updateMoleculeCon();
			}
		}
		
		 conN2O4 = getConByName("Dinitrogen-Tetroxide");
		 conNO2 = getConByName("Nitrogen-Dioxide");
		 currentRatio = conN2O4/(conNO2*conNO2);
		
			// Break up N2O4 if there are too many, in order to keep equalibrium
//			if (curTime != oldTime) {
				//totalNum = (float)numNO2 / 2 + numN2O4;
				if (currentRatio>keq) // If N2O4 is over numberred,
														// break them up
				{
//					Random rand = new Random();
//					if (rand.nextFloat() < breakProbability) {
						 return breakApartN2O4(simulation);
//					}
				}
//				oldTime = curTime;
//			}
		
		return true;
	}
	
	
	
	//NO2 <--> N2O4
	private boolean reactSim2Set1(Simulation simulation) {
		
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		//Update equilibrium
		updateEquilibrium();
		
		if(catalystAdded)  //Speed up reaction by increasing chance
			reactProbability = 1.0f;
		else
			reactProbability =0.6f;
		
		Random rand = new Random();
		if (rand.nextFloat() > reactProbability) {
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			 return false;
		}


		//2NO2 == N2O4
		float conN2O4 = getConByName("Dinitrogen-Tetroxide");
		float conNO2 = getConByName("Nitrogen-Dioxide");
		float currentRatio = conN2O4/(conNO2*conNO2);
		
		if (!p5Canvas.killingList.isEmpty()) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0 && (currentRatio <= keq)) {
				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++) {
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				}

				Molecule mNew = null;
				String nameNew = null;
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					// Reacts at the postion of nitrylChloride
					Vec2 loc = mOld[0].getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					nameNew = p5Canvas.products.get(i);

					mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
							p5Canvas, (float) (Math.PI / 2));
					mNew.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(mNew);

					mNew.body.setLinearVelocity(mOld[i / 2].body
							.getLinearVelocity());
				}
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				
				//Update molecule number
				int index = Compound.names.indexOf("Dinitrogen-Tetroxide");
				Compound.counts.set(index, Compound.counts.get(index)+1);
				index = Compound.names.indexOf("Nitrogen-Dioxide");
				Compound.counts.set(index, Compound.counts.get(index)-2);

				updateMoleculeCon();
			}
		}
		
		 conN2O4 = getConByName("Dinitrogen-Tetroxide");
		 conNO2 = getConByName("Nitrogen-Dioxide");
		 currentRatio = conN2O4/(conNO2*conNO2);
		
			// Break up N2O4 if there are too many, in order to keep equalibrium
//			if (curTime != oldTime) {
				//totalNum = (float)numNO2 / 2 + numN2O4;
				if (currentRatio>keq) // If N2O4 is over numberred,
														// break them up
				{
//					Random rand = new Random();
//					if (rand.nextFloat() < breakProbability) {
						 return breakApartN2O4(simulation);
//					}
				}
//				oldTime = curTime;
//			}
		
		return true;
	}
	
	public void updateProperties(int sim,int set)
	{
		if(sim==2)
		{
			/*
			//We dont change pressure unless user drag volume slider	
			if(p5Canvas.currentVolume==defaultVolume)
			{
				p5Canvas.pressure = defaultPressure;
			}
			else
			{
				float ratio = (float)p5Canvas.currentVolume/defaultVolume;
				p5Canvas.pressure = defaultPressure/ratio;
			}
			*/
		}
	}
	
	//Change Keq based on temprature
	private void updateEquilibrium()
	{
		
		
		//Temperatur modifier
		float temp =  p5Canvas.temp;
		
		float slope = 0;
		int length = tempArray.length;
		float keqRes = 0 ;
		
		//We check the tempSetting here, go over all the elements in tempArray 
		//and keqArray, and get the keq number based on linear interpolation
		
		if(temp<tempArray[0])
			keq = keqArray[0];
		else if(temp>= tempArray[length-1])
			keq = keqArray[length-1];
		
		for(int i = 0 ;i<tempArray.length-1;i++)
		{
			if(temp>=tempArray[i]&&temp<tempArray[i+1])
			{
				slope = (keqArray[i+1]-keqArray[i])/(tempArray[i+1]-tempArray[i]);
				keqRes = keqArray[i]+slope*(temp-tempArray[i]);
				keq = keqRes;
				//System.out.println("Keq = "+keq);
			}
		}
		//Catalyst and Inert gas do nothing to equilibrium
	}
	
	//Break apart N2O4 in Sim 1 Set 2  and Sim 2
	private boolean breakApartN2O4(Simulation simulation)
	{
		Molecule mole = null;
		String nameReactant = null;
		String nameReactant2 = null;
		String nameProduct = null;
		float conReactant = 0;
		float conReactant2 = 0;
		float conProduct = 0;
		float currentRatio = 0;
		if(simulation.getSetNum()==4)//PCl3+Cl2<-->PCl5
		{
			nameReactant = "Phosphorus-Trichloride";
			nameReactant2 = "Chlorine";
			nameProduct = "Phosphorus-Pentachloride";

		}
		else  //2NO2<-->N2O4
 		{
			nameReactant = "Nitrogen-Dioxide";
			nameProduct = "Dinitrogen-Tetroxide";
		}
//		int index =-1;
		for (int i = 0; i < State.molecules.size(); i++) {
			mole = State.molecules.get(i);
			if (mole.getName().equals(nameProduct)) {
				Vec2 loc = mole.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				String nameNew = new String(nameReactant);
				Vec2 size = Molecule.getShapeSize(nameReactant,
				p5Canvas);
				if(simulation.getSetNum()==4)
				{
					//Phosphorus-Trichloride
						newVec.x += size.x;
						Molecule mNew = new Molecule(newVec.x, newVec.y, nameReactant, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew);
						mNew.body.setLinearVelocity(mole.body
								.getLinearVelocity());
					//Chlorine
						newVec.x -= size.x;
						Molecule mNew2 = new Molecule(newVec.x, newVec.y, nameReactant2, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew2.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew2);
						mNew2.body.setLinearVelocity(mole.body
								.getLinearVelocity().mulLocal(-1));
				}
				else {
					//Create two new NO2 molecules
					for(int k =0;k<2;k++)
					{
						if(k%2==0)
						newVec.x += size.x/2;
						else
							newVec.x -= size.x/2;
						Molecule mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew);
						if(k%2==0)
						mNew.body.setLinearVelocity(mole.body
								.getLinearVelocity());
						else
							mNew.body.setLinearVelocity(mole.body
									.getLinearVelocity().mul(-1));
					}
				}
			
				mole.destroy();
				
				//Update molecule number
				int indexProduct = Compound.names.indexOf(nameProduct);
				int indexReactant = Compound.names.indexOf(nameReactant);

				if(simulation.getSetNum()==4)
				{
					Compound.counts.set(indexProduct, Compound.counts.get(indexProduct)-1);
					Compound.counts.set(indexReactant, Compound.counts.get(indexReactant)+1);
					int indexReactant2 = Compound.names.indexOf(nameReactant2);
					Compound.counts.set(indexReactant2, Compound.counts.get(indexReactant2)+1);
				}
				else
				{
					Compound.counts.set(indexProduct, Compound.counts.get(indexProduct)-1);
					Compound.counts.set(indexReactant, Compound.counts.get(indexReactant)+2);
				}
				
				updateMoleculeCon();
				
				
				  conProduct = getConByName(nameProduct);
				  conProduct = Math.round(conProduct*1000)/1000f;
				  conReactant = getConByName(nameReactant);
				  conReactant = Math.round(conReactant*1000)/1000f;
				  if(nameReactant2!=null)  //PCl5 <--> PCl3 + Cl2
				  {
					  conReactant2 = getConByName(nameReactant2);
					  conReactant2 = Math.round(conReactant2*1000)/1000f;
					  currentRatio = (conReactant* conReactant2) / conProduct;
				  }
				  else  //2NO2 <--> N2O4
				  {
					  currentRatio = conProduct/(conReactant*conReactant);
				  }
				  
				 //System.out.println("Current Ratio is "+currentRatio);
				 keqOutput = currentRatio;
//				oldTime = curTime;
				return true;
			}
		}
		return false;
	}


	@Override
	public void initialize() {
		this.updateMoleculeCon();
	}

	@Override
	protected void reset() {
		
		// Reset parameters
		setupSimulations();
		moleculeConHash.clear();
		catalystAdded = false;
		curTime = 0;
		oldTime = 0;
		forceUpdated = false;
		defaultKeq = 8.92f;
		keq = defaultKeq;
		keqOutput = 0;
		addedNO2 =0;
		addedN2O4 =0;
		reactProbability = 0.5f; 

		
		// Customization
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Main main = p5Canvas.getMain();
		
		
		// Set up speed ratio for molecules
		setupSpeed();
		
		switch(sim)
		{
		case 1:
			if(set==1)
				keq=0;
			//In set 2 it is 8.92 by default
			else if(set ==3)
			{
				keq=0;
				p5Canvas.temp=105;
			}
			else if(set==4)
			{
				keq=20f;
				p5Canvas.temp = 170;
				reactProbability = 0.05f; 

			}
			
			volumeMagnifier =1000;
			
			break;
		case 2:
			p5Canvas.volumeMinBoundary = 20;
			p5Canvas.tempMin=0;
			p5Canvas.tempMax = 220;
			p5Canvas.heatSpeed = 5;
			volumeMagnifier = 1000;
			

			break;

		}
		updateMoleculeCon();
	}
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		Main main = p5Canvas.getMain();
		
		
		//Make adjust if we show Litter instead of mL
		float volumeMagnifier = getVolumeMagnifier()/1000;
		if( volumeMagnifier != 0)
		{
				main.volumeLabel.setText(p5Canvas.currentVolume + " L");
				lblVolumeValue.setText(p5Canvas.currentVolume +" L");
		}
		else
		{
			main.volumeLabel.setText(p5Canvas.currentVolume + " mL");
			lblVolumeValue.setText(p5Canvas.currentVolume +" mL");	
		}
		
		//Customization
		switch(p5Canvas.getSim())
		{
		case 1:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			
			main.getCanvas().setRangeYAxis(0, 0.05f);
			if(set==1)
			{
				main.boxDisplayForce.setEnabled(false);
			}
			break;
		case 2:
			
			//Set the range for Y axis
			main.getCanvas().setRangeYAxis(0, 0.07f);
			
			break;
		
		}

	}
	

	private void setupSpeed() {
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float speed = 1.0f;		
		
		if(sim ==1 )
		{
			
			switch(set)
			{
			case 1:
				speed = 4;
				break;
			case 2:
				speed = 8;
				break;
			case 3:
				speed = 4;
				break;
			case 4:
				speed = 8;
				break;
			}
		}
		else if( sim ==2)
		{
			speed = 8;
		}
		getSimulation(sim, set).setSpeed(speed);
	}
	
	public void resetTableView(int sim, int set)
	{
		p5Canvas.setVolume(defaultVolume);
		((TableView) p5Canvas.getTableView()).setColumnName(0, "Concentration");
		((TableView) p5Canvas.getTableView()).setColumnWidth(0, 30);
		((TableView) p5Canvas.getTableView()).setColumnWidth(1, 30);
		((TableView) p5Canvas.getTableView()).setColumnWidth(2, 100);
	}

	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;
//		updateMoleculeCon();

		//Update keq value label
		if(keqOutput!=0)
		{
//			if(sim==1&&set==4)
//			{
//				lblKeqValue.setText("0.5");
//			}
			if (sim==1 && set==2)
			{
				lblKeqValue.setText("0.5");
			}
			else if (sim==1 && set==4)
			{
				lblKeqValue.setText("8.92");
			}
//			else
			{
				output = myFormatter.format(keqOutput);
				lblKeqValue.setText(output);
			}
			
		}
		else
			lblKeqValue.setText("Infinity");
		
		//update PVT label
		if (lblVolumeValue.isShowing()) {

			float volumeMagnifier = getVolumeMagnifier()/1000;
			if( volumeMagnifier != 0)
				lblVolumeValue.setText(p5Canvas.currentVolume + " L");
			else
				lblVolumeValue.setText(p5Canvas.currentVolume
						+ " mL");
		}
		if (lblTempValue.isShowing()) {
			float temp = p5Canvas.temp;
			if(temp<=p5Canvas.tempMin)
				temp =p5Canvas.tempMin;
			else if( temp>=p5Canvas.tempMax)
				temp = p5Canvas.tempMax;
			output = myFormatter.format(temp+celsiusToK);
			lblTempValue.setText(output + " K");
		}

		if (lblPressureValue.isShowing()) {
			output = myFormatter.format(p5Canvas.pressure);
			lblPressureValue.setText(output + " kPa");
		}  
	}
	
	// Add molecule function for Sim 1 Set 1  S+O2-->SO2
	public boolean addSolidPavement(boolean isAppEnable,
			String compoundName, int count, Simulation simulation) {
		boolean res = true;
		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3
		float offsetX = 0; // Offset x from left border
		//int leftBorder = 0; // left padding from left border
		int startIndex = State.molecules.size(); // Start index of this group in
											// molecules arraylist
		int rowNum=0;
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		//float jointLength = size.y;

		/*
		// boolean dimensionDecided = false;
		int k = 0;
		for (k = 1; k < 10; k++) {
			if (count <= (k * k)) {
				dimension = k;
				break;
			}
		}

		int rowNum = count / dimension + 1;
		int colNum = dimension;

		offsetX = p5Canvas.w / 2 - (colNum * moleWidth) / 2;
		centerX = p5Canvas.x + leftBorder + offsetX;
		centerY = p5Canvas.y + p5Canvas.h - rowNum * moleHeight
				- p5Canvas.boundaries.difVolume;
		*/
			dimension = 11;
			float xInterval = moleWidth * 1.75f;
			float lineSpace = moleHeight * 1.25f;
			rowNum = (int) Math.ceil((double) count / dimension);

			offsetX = p5Canvas.w / 2- ((dimension - 1) * (xInterval) + moleWidth) / 2;
			centerX = p5Canvas.x + offsetX;
			for (int i = 0; i < count; i++) {
				if ((i / dimension) % 2 == 0) /* Odd line */
				{
					x_ = centerX + i % dimension * xInterval;
				} else /* even line */
				{
					x_ = centerX + 0.7f * moleWidth + i % dimension * xInterval;
				}

				centerY = p5Canvas.y + p5Canvas.h  -rowNum * lineSpace
						- p5Canvas.boundaries.difVolume;
				y_ = centerY + i / dimension * lineSpace;
				res = State.molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI / 2)));

			}
			// Add molecule which is at bottom right of this molecule to
			// neighbor list of this molecule
			int thisIndex = 0;
			int neighborIndex = 0;
			Molecule thisMolecule = null;
			Molecule neighborMolecule = null;

			for (int i = 0; i < count; i++) {
				if ((i + dimension) < count) // bottom most molecules
				{
					thisIndex = i + startIndex;
					neighborIndex = i + dimension + startIndex;
					thisMolecule = State.molecules.get(thisIndex);
					neighborMolecule = State.molecules.get(neighborIndex);
					thisMolecule.neighbors.add(neighborMolecule);
				}
			}

			/* Add joint for solid molecules */
			if (count > 1) {
				int index1 = 0;
				Molecule m1 = null;
				float frequency = 5;
				float damp = 0.6f;
				float jointLen = 3.0f;

				for (int i = 0; i < count; i++) {

					/* For every molecule, create a anchor to fix its position */
					index1 = i + startIndex;
					m1 = State.molecules.get(index1);
					Vec2 m1Pos = box2d.coordWorldToPixels(m1.getPosition());
					Anchor anchor = new Anchor(m1Pos.x, m1Pos.y, box2d,
							p5Canvas);
					State.anchors.add(anchor);
					joint2Elements(m1, anchor, jointLen, frequency, damp);
				}

			}
		

		return res;
	}
	
	//Update Concentration value for all compounds
		private void updateMoleculeCon() {
			
			float mole = 0;
			float con = 0;
			float volume = (float)p5Canvas.currentVolume/1000 * getVolumeMagnifier(); 
			String name = null;

			for (int i =0;i<Compound.names.size();i++) {
				name = new String(Compound.names.get(i));
				//Special cases
				if(name.equals("Sulfur"))
				{
					con = 62.3f;
				}
//				else if(name.equals("Water"))  //Water is gas in Sim 1
//				{
//					con =55.35f;
//				}
				else  //General case
				{
				mole = (float) Compound.counts.get(i) / numMoleculePerMole;
				con = mole / volume;
				}
				moleculeConHash.put(Compound.names.get(i), con);
			}
			
		}

	@Override
	protected void computeForce(int sim, int set) {
		//Clear old force
		
		clearAllMoleculeForce();
		curTime = p5Canvas.getMain().time;		
		switch (sim)
		{
		case 1:
			if(set==1)
			{
				if(!forceUpdated)
				{
					if (curTime!=oldTime)
					{					
						clearAllMoleculeForce();
						computeForceSim1Set1();
						oldTime = curTime;
						forceUpdated = true;
					}
				}
				computeForceSim1Set1SO2();
			}
//			else if( set ==2 )
//				computeForceSim1Set2();
			else if(set ==3)
				computeForceSim1Set3();
			break;
		case 2:
//			computeForceSim2();
			break;

		}
	}
	
//	private void computeForceSim2() {
//		// TODO Auto-generated method stub
//		
//	}

//	private void computeForceSim1Set2() {
//		// TODO Auto-generated method stub
//		
//	}
	
	private void computeForceSim1Set3() {
		
		float gravityCompensation = 0.05f;
		float topBoundary = p5Canvas.h/2;
		float gravityScale = 0.01f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.075f; // How strong the attract force is
		float forceYCompensation = 0.02f;
		float repulsiveForce = 1.2f; //How strong the repulsive force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		
		//There are still many oxygen left, this function does not affect
		if(State.getMoleculeNumByName("Oxygen")>2)
			return;
//		float topBoundary = p5Canvas.h/4*3;
//		float gravityCompensation = 0.2f;
//		float gravityScale = 0.01f;

		for (Molecule moleHydro: State.getMoleculesByName("Hydrogen")) {
	
			
				
					for (int thisE = 0; thisE < moleHydro.getNumElement(); thisE++) {
							locThis.set(moleHydro.getElementLocation(thisE));
							moleHydro.sumForceX[thisE] = 0;
							moleHydro.sumForceY[thisE] = 0;
						ArrayList<Molecule> oxygenIon = State.getMoleculesByName("Oxygen");
						for(Molecule moleOxy:oxygenIon)
						{
							locOther.set(moleOxy.getPosition());
							xValue = locOther.x - locThis.x;
							yValue = locOther.y - locThis.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;

							moleHydro.sumForceX[thisE] += forceX*2;
							moleHydro.sumForceY[thisE] += forceY*2;

						}
						
					}
				
			
			
		}
		
	}


	//Compute force function for Sim 1 Set 1
	private void computeForceSim1Set1() {
		Molecule mole = null;
		Random randX = new Random();
		Random randY = new Random();
		float scale = 2.0f; // How strong the force is

		float randXValue = 0;
		float randYValue = 0;
		boolean randXDir = false;
		boolean randYDir = false;
//		float topBoundary = p5Canvas.h/4*3;
//		float gravityCompensation = 0.2f;
//		float gravityScale = 0.01f;

		for (int i = 0; i < State.molecules.size(); i++) {
			if (State.molecules.get(i).getName().equals("Sulfur")) // Only compute
																// force for
																// solid
			{
				randXValue = randX.nextFloat() * scale;
				randYValue = randY.nextFloat() * scale;
				randXDir = randX.nextBoolean();
				randXValue *= (float) (randXDir ? 1 : -1);
				randYDir = randY.nextBoolean();
				randYValue *= (float) (randYDir ? 1 : -1);
				mole = State.molecules.get(i);
				for (int e = 0; e < mole.getNumElement(); e++) {

					mole.sumForceX[e] = randXValue;
					mole.sumForceY[e] = randYValue;
				}
			}
		}
	}
	
	private void computeForceSim1Set1SO2()
	{
		float topBoundary = p5Canvas.h/4*3;
		float gravityCompensation = 1f;
		float gravityScale = 0.1f;
		
		// Check positions of all Gas molecules, in case they are not going
		// to low
		for(Molecule mole:State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

			if(mole.getName().equals("Sulfur-Dioxide"))
			{
				if (pos.y > topBoundary) {
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																					// element
						mole.sumForceX[thisE] += 0;
						mole.sumForceY[thisE] += (gravityCompensation+ gravityScale*(pos.y-topBoundary));
		
					}
				}
			}
		}
	}
	
	@Override
	protected void applyForce(int sim, int set) {
				if(sim==1 && set ==1)
				{
					if (forceUpdated)
					{
						super.applyForce(sim, set);
						forceUpdated = false;
					}
				}
				else {
				super.applyForce(sim, set);
				}

	}

	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Simulation simulation = getSimulation(sim, set);
		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
		if (spawnStyle == SpawnStyle.Gas) {
			res = this.addGasMolecule(isAppEnable, compoundName, count);
		} else if (spawnStyle == SpawnStyle.Liquid) {
			res = this.addSingleIon(isAppEnable, compoundName, count);
		}
		else if (spawnStyle == SpawnStyle.SolidPavement) {
			res = this.addSolidPavement(isAppEnable, compoundName, count, simulation);
		}
		if (res) {
			// Connect new created molecule to table index
			int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
			int lastIndex = State.molecules.size() - 1;
			if (compoundName.equals("Catalyst"))
				catalystAdded = true;
			else if(compoundName.equals("Inert"))
				inertAdded = true;
			for (int i = 0; i < count; i++) {
				State.molecules.get(lastIndex - i).setTableIndex(tIndex);
				State.molecules.get(lastIndex - i).setRatioKE(
						1 / simulation.getSpeed());
			}
			if(!p5Canvas.isFirstRun())
			{
			if(compoundName.equals("Nitrogen-Dioxide"))
				addedNO2 = count;
			else if(compoundName.equals("Dinitrogen-Tetroxide"))
				addedN2O4 = count;
			}
		}

		return res;		
	}

	@Override
	public void beginReaction(Contact c) {
		// If there are some molecules have not been killed yet.
		// We skip this collision
		if (!p5Canvas.killingList.isEmpty())
			return;
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		if (o1 == null || o2 == null)
			return;
		// TODO: Get reaction elements based on Simulation object parameter
		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();

		// Make sure reaction only takes place between molecules or ions
		if (c1.contains("Molecule") && c2.contains("Molecule")) {
			Molecule m1 = (Molecule) o1;
			Molecule m2 = (Molecule) o2;

			// Check if both of these two molecules are reactive
			if (m1.getReactive() && m2.getReactive()) {

				ArrayList<String> reactants = new ArrayList<String>();
				reactants.add(m1.getName());
				reactants.add(m2.getName());
				if (true) { /* TODO: Maybe there are some conditions */

					p5Canvas.products = getReactionProducts(reactants, m1, m2);
					if (p5Canvas.products != null
							&& p5Canvas.products.size() > 0) {
						/*
						 * If there are some new stuff in newProducts, kill old
						 * ones and add new ones
						 */
						p5Canvas.killingList.add(m1);
						p5Canvas.killingList.add(m2);

					}

				}
			}
		
		}
	}
	
	/******************************************************************
	 * FUNCTION : getReactionProducts DESCRIPTION : Return objects based on
	 * input name Called by beginReaction
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants,
			Molecule m1, Molecule m2) {
		ArrayList<String> products = new ArrayList<String>();
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Random rand = new Random();
		float probability = 1.0f;
		float randomFloat = 0f;
		switch (sim) {
		case 1:
			// Sim 1 set 1  S + O2 --> SO2
			if (reactants.contains("Sulfur") && reactants.contains("Oxygen")) {
				products.add("Sulfur-Dioxide");

			}
			// Sim 1 set 2 2NO2 <--> N2O4
			else if (reactants.get(0).equals("Nitrogen-Dioxide")
					&& reactants.get(1).equals("Nitrogen-Dioxide")
					&& reactants.size() == 2) {
				probability = 1.0f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= probability) {
					products.add("Dinitrogen-Tetroxide");
				}

			}
			//Sim 1 Set 3 2H2 + O2 --> 2H2O
			else if(reactants.contains("Hydrogen") && reactants.contains("Oxygen"))
			{
				float radius = 150;
				probability = 1.0f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= probability) {
				// Compute midpoint of collision molecules
				Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
				Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
				Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);

				// Go through all molecules to check if there are any molecules
				// nearby
				for (int i = 0; i < State.molecules.size(); i++) {
					if (State.molecules.get(i).getName().equals("Hydrogen")
							&& State.molecules.get(i) != m1 && State.molecules.get(i) != m2) {
						Vec2 thirdMolecule = box2d.coordWorldToPixels(State.molecules
								.get(i).getPosition());
						if (radius > computeDistance(midpoint, thirdMolecule)) {
							products.add("Water");
							products.add("Water");
							// Need to kill the third molecule
							p5Canvas.killingList.add(State.molecules.get(i));
							break; // Break after we find one nearby
						}
					}
				}
				}
			}
			//Sim 1 Set 4 PCl3 + Cl2 <--> PCl5 
			else if(reactants.contains("Phosphorus-Trichloride") && reactants.contains("Chlorine"))
			{
				products.add("Phosphorus-Pentachloride");
			}
			break;
		case 2:
			// Sim 2 2NO2 <--> N2O4
			if (reactants.get(0).equals("Nitrogen-Dioxide")
					&& reactants.get(1).equals("Nitrogen-Dioxide")) {
				if(catalystAdded)
					reactionProbability = 0.4f;
				else
				reactionProbability = 0.1f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= reactionProbability) {
					products.add("Dinitrogen-Tetroxide");
				}
			}
			break;
		

		}
		return products;
	}

	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}
	
	//Set up output labels on the bottom of right panel
	private void setupOutputLabels() {
//		lblConText1 = new JLabel();
//		lblConText2 = new JLabel();
//		lblConText3 = new JLabel();
//		lblConText4 = new JLabel();
//		lblConText1 = new JLabel(" M");
//		lblConText2 = new JLabel(" M");
//		lblConText3 = new JLabel(" M");
//		lblConText4 = new JLabel(" M");
		lblVolumeText = new JLabel("Volume:");
		lblVolumeValue = new JLabel(" mL");
		lblTempText = new JLabel("Temperature:");
		lblTempValue = new JLabel(" K");
		lblPressureText = new JLabel("Pressure:");
		lblPressureValue = new JLabel(" kPa");
		lblKeqText = new JLabel("Keq:");
		lblKeqValue = new JLabel("");

	}
	
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		JPanel dashboard = main.dashboard;
		
		setupOutputLabels();
		

		
		//Make adjust if we show Liter instead of mL
		float volumeMagnifier = getVolumeMagnifier()/1000;
		if( volumeMagnifier != 0)
		{
				main.volumeLabel.setText(p5Canvas.currentVolume + " L");
				lblVolumeValue.setText(p5Canvas.currentVolume +" L");
		}
		else
		{
			main.volumeLabel.setText(p5Canvas.currentVolume + " mL");
			lblVolumeValue.setText(p5Canvas.currentVolume +" mL");
		}
		
		switch(sim)
		{
		case 1:
			if(set==1)
			{
				keqOutput = 0;
				dashboard.add(lblKeqText,"cell 0 1");
				dashboard.add(lblKeqValue,"cell 1 1");
			}
			else if( set==2)
			{
				//keqOutput = 4.12f;
				
				keqOutput = 0.5f;

				dashboard.add(lblKeqText,"cell 0 1");
				dashboard.add(lblKeqValue,"cell 1 1");
			}
			else if( set ==3 )
			{
				keqOutput = 0;

				dashboard.add(lblKeqText,"cell 0 1");
				dashboard.add(lblKeqValue,"cell 1 1");
			}
			else
			{
				//keqOutput = 0.5f;
				
				keqOutput = 8.92f;

				dashboard.add(lblKeqText,"cell 0 1");
				dashboard.add(lblKeqValue,"cell 1 1");
			}
			break;
		case 2:
			keqOutput = 4.12f;

			dashboard.add(lblKeqText,"cell 0 1");
			dashboard.add(lblKeqValue,"cell 1 1");
			dashboard.add(lblTempText, "cell 0 2");
			dashboard.add(lblTempValue,"cell 1 2");
			dashboard.add(lblVolumeText, "cell 0 3");
			dashboard.add(lblVolumeValue,"cell 1 3");
			dashboard.add(lblPressureText, "cell 0 4");
			dashboard.add(lblPressureValue,"cell 1 4");
			
			lblTempValue.setText("298 K");
			lblPressureValue.setText("825.86 kPa");
			
			break;
		}
		
		//Update keq value label
		if(keqOutput!=0)
		{
			
			DecimalFormat myFormatter = new DecimalFormat("###.###");
			String output = myFormatter.format(keqOutput);
			lblKeqValue.setText(output);
			
		}
		else
			lblKeqValue.setText("Infinity");
		
		dashboard.repaint();
	}
	
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		String name = (String)Compound.names.get(indexOfCompound);
		return getConByName(name);
	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		float res= 0;
		String name = (String)Compound.names.get(indexOfCompound);
		res = getConByName(name);
		return res;
		
	}

	
	public float getConByName(String s) {
		if (moleculeConHash.containsKey(s))
			return moleculeConHash.get(s);
		else
			return 0;
	}

	@Override
	protected void initializeSimulation(int sim, int set) {

		updateMoleculeCon();
		
	}

	@Override
	public void updateMoleculeCountRelated(int sim, int set) {

		updateMoleculeCon();
	}

	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
