/**
 * 
 */
package simulations;

import static data.State.molecules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.Constants;
import Util.Integrator;

import data.DBinterface;
import data.State;

import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Molecule.mState;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 *
 */
public class Unit8 extends UnitBase {

	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupSimulations()
	 */
	private JLabel lblCompoundConText;
	private JLabel lblCompoundConValue;
	private JLabel lblProductConText;
	private JLabel lblProductConValue;
	private JLabel lblHConText;
	private JLabel lblHConValue;
	private JLabel lblOHConText;
	private JLabel lblOHConValue;
	private JLabel lblPHText;
	private JLabel lblPHValue;
	private JLabel lblPOHText;
	private JLabel lblPOHValue;
	private JLabel lblHNumberText;
	private JLabel lblHNumberValue;
	private JLabel lblOHNumberText;
	private JLabel lblOHNumberValue;
	private JLabel lblWaterNumberText;
	private JLabel lblWaterNumberValue;
	private JLabel lblKeqText;
	private JLabel lblKeqValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	//Labels for Sim 7
	private JLabel lblMolesCompound1Text;
	private JLabel lblMolesCompound1Value;
	private JLabel lblMolesCompound2Text;
	private JLabel lblMolesCompound2Value;
	private JLabel lblMolesCompound3Text;
	private JLabel lblMolesCompound3Value;
	private JLabel lblMolesWaterText;
	private JLabel lblMolesWaterValue;
	
	float reactProbability =0.5f;	
	private int numMoleculePerMole =10;
	float keq = 0.01f;
	float breakProbability = 0.75f; // The chance that compound will break apart
	private float pH;
	private float pKa=0f;   //pKa value used to calculate acids and bases equlibrium
	private float hydrogenIonRatio = 0.1f;   //The ratio of hydrogenIon in water
	
	int oldTime = -1;
	int curTime = -1;
	
	Integrator interpolatorPos1 = new Integrator(0);
	Integrator interpolatorPos2 = new Integrator(0);
	Integrator interpolatorAngle1 = new Integrator(0);
	Integrator interpolatorAngle2 = new Integrator(0);
	Integrator interpolatorHide = new Integrator(0);
	Integrator interpolatorShow = new Integrator(0);
	//private float totalDist = 0;
	private Vec2 lastPositionFirst = new Vec2(0,0);
	private Vec2 lastPositionSecond = new Vec2(0,0);
	private Vec2  translateVectorFirst = new Vec2(0,0);
	private Vec2 translateVectorSecond = new Vec2(0,0);
//	private float minDist = 0;
	private int electronView = 0; //If current simulation is for Lewis Law
	private boolean hasFading = false;   //If this simulation has fading transition
	private boolean isFading = false;    //If in fading process
//	private Molecule newMolecule = null;
	
	//IonHash: used in sim2 to map hydrogen-Ion and hydrogen-Atom to hydrogen-Ion
	private HashMap<String,String []> ionHash = new HashMap<String,String[]>();
	
	//New molecule list for reactions in Sim 2
	ArrayList<Molecule> newMolecules = new ArrayList<Molecule>();

	//Concentration map
	HashMap < String, Double [] > conMap = new HashMap< String, Double[] >();
	boolean reachEquilibrium = false;  //Flag of equilibrium
	boolean outputUpdated = false;	   //Flag of update equilibrium information
	String [][] bufferConTable  = null;  //Store concentration values in Sim 5 set 1
	private int numHclAdded = 0;  //Hcl added in Sim 5 Set 1
	//Sim 6 parameters
	private float [] baseAddedMap = new float [21];
	private int baseAdded =0;
	private int baseVolumeMultiplier = 10;
	private int baseUpdated = 0;
	
	
	public Unit8(P5Canvas parent, PBox2D box) {
		super(parent, box);

		unitNum = 8;
		setupSimulations();
		setupOutputLabels();
	}
	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Hydrogen-Ion","Chlorine-Ion", "Water" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Solvent,SpawnStyle.Solvent, SpawnStyle.Solvent };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Sodium-Hydroxide", "Water" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 1, 3);
		String[] elements2 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Solvent, SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 2, 1);
		String[] elements3 = { "Hydrogen-Chloride", "Sodium-Hydroxide" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 2, 2);
		String[] elements4 = { "Hydrogen-Chloride","Ammonia" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas,SpawnStyle.Gas};
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 2, 3);
		String[] elements5 = { "Cyanide","Hydrogen-Bromide" };
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas,SpawnStyle.Gas};
		simulations[5].setupElements(elements5, spawnStyles5);
		
		simulations[6] = new Simulation(unitNum, 2, 4);
		String[] elements6 = { "Boron-Trichloride","Chlorine-Ion" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 3, 1);
		String[] elements7 = { "Hydrogen-Chloride", "Water"};
		SpawnStyle[] spawnStyles7 = {SpawnStyle.Gas,SpawnStyle.Solvent};
		simulations[7].setupElements(elements7, spawnStyles7);
		
		simulations[8] = new Simulation(unitNum, 3, 2);
		String[] elements8 = { "Hydrogen-Fluoride","Water"};
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[8].setupElements(elements8, spawnStyles8);
		
		simulations[9] = new Simulation(unitNum, 3, 3);
		String[] elements9 = { "Sodium-Hydroxide", "Water" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Precipitation,SpawnStyle.Solvent};
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum, 3, 4);
		String[] elements10 = { "Ammonia","Water"};
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[10].setupElements(elements10, spawnStyles10);
		
		simulations[11] = new Simulation(unitNum, 4, 4);
		String[] elements11 = { "Acetic-Acid","Water"};
		SpawnStyle[] spawnStyles11 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[11].setupElements(elements11, spawnStyles11);
		
		simulations[12] = new Simulation(unitNum, 4, 2);
		String[] elements12 = { "Lithium-Hydroxide","Water"};
		SpawnStyle[] spawnStyles12 = { SpawnStyle.Precipitation,SpawnStyle.Solvent};
		simulations[12].setupElements(elements12, spawnStyles12);
		
		simulations[13] = new Simulation(unitNum, 4, 3);
		String[] elements13 = { "Methylamine","Water"};
		SpawnStyle[] spawnStyles13 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[13].setupElements(elements13, spawnStyles13);
		
		simulations[14] = new Simulation(unitNum, 4, 1);
		String[] elements14 = { "Nitric-Acid","Water"};
		SpawnStyle[] spawnStyles14 = { SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[14].setupElements(elements14, spawnStyles14);
		
//		simulations[15] = new Simulation(unitNum, 5, 1);
//		String[] elements15 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
//		SpawnStyle[] spawnStyles15 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
//		simulations[15].setupElements(elements15, spawnStyles15);
		
		simulations[16] = new Simulation(unitNum, 6, 1);
		String[] elements16 = { "Hydrogen-Ion","Chlorine-Ion", "Sodium-Ion","Hydroxide","Water" };
		SpawnStyle[] spawnStyles16 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[16].setupElements(elements16, spawnStyles16);
		
		simulations[17] = new Simulation(unitNum, 5, 1);
		String[] elements17 = { "Ammonia","Ammonium","Hydrogen-Ion","Chlorine-Ion","Water" };
		SpawnStyle[] spawnStyles17 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[17].setupElements(elements17, spawnStyles17);
		
		simulations[18] = new Simulation(unitNum, 6, 2);
		String[] elements18 = { "Hydrogen-Ion","Chlorine-Ion", "Ammonia","Water" };
		SpawnStyle[] spawnStyles18 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[18].setupElements(elements18, spawnStyles18);
		
		simulations[19] = new Simulation(unitNum, 6, 3);
		String[] elements19 = { "Hydrogen-Fluoride", "Sodium-Ion","Hydroxide","Water" };
		SpawnStyle[] spawnStyles19 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[19].setupElements(elements19, spawnStyles19);
		
		simulations[20] = new Simulation(unitNum, 6, 4);
		String[] elements20 = { "Hydrogen-Fluoride", "Ammonia","Water" };
		SpawnStyle[] spawnStyles20 = { SpawnStyle.Solvent,SpawnStyle.Solvent,SpawnStyle.Solvent};
		simulations[20].setupElements(elements20, spawnStyles20);
		
		ionHash.put("Hydrogen-Chloride", new String [] {"Hydrogen-Chloride","Hydrogen-Chloride"});
		ionHash.put("Sodium-Hydroxide", new String [] {"Sodium-Hydroxide","Sodium-Hydroxide"});
		ionHash.put("Ammonia", new String [] {"Ammonia","Ammonia"});
		ionHash.put("Ammonium", new String [] {"Ammonium","Ammonium"});
		ionHash.put("Cyanide", new String [] {"Cyanide","Cyanide"});
		ionHash.put("Hydrogen-Bromide", new String [] {"Hydrogen-Bromide","Hydrogen-Bromide"});
		ionHash.put("Boron-Trichloride", new String[] {"Boron-Trichloride","Boron-Trichloride"});
		ionHash.put("Chlorine-Ion", new String [] {"Chlorine-Ion","Chloride"});
		
		setMoleculeDensity();
		
		//Set up buffer values
		int size = 15;
		bufferConTable = new String [size][6];  //15 is the max num of HCL that can be added
		//The second column corresponds to output on dashboard of right panel
		for( int i = 0 ;i<size;i++)
		{
			for(int j = 0 ;j<6;j++)
			   bufferConTable [i][j] = "0 M" ;
		}
		bufferConTable[0][0] = "9.72";
		bufferConTable[0][1] = "0.799 M";
		bufferConTable[0][2] = "0.501 M";
		bufferConTable[0][3] = "0 M";
		bufferConTable[0][4] = "1.9E-10";
		bufferConTable[0][5] = "5.3E-5";
		
		bufferConTable[5][0] = "8.74";
		bufferConTable[5][1] = "0.25 M";
		bufferConTable[5][2] = "0.833 M";
		bufferConTable[5][3] = "0 M";
		bufferConTable[5][4] = "1.8E-9";
		bufferConTable[5][5] = "5.5E-6";
		
		bufferConTable[10][0] = "0.845";
		bufferConTable[10][1] = "7.2E-9 M";
		bufferConTable[10][2] = "0.93 M";
		bufferConTable[10][3] = "0.14 M";
		bufferConTable[10][4] = "0.143";
		bufferConTable[10][5] = "7E-14";

	}
	
	@Override
	public void setMoleculeDensity() {
		int sim = 0 ; 
		int set  = 0;

		sim = 1; set = 1;
		getSimulation(sim,set).setupElementDensity("Hydrogen-Ion", 500);
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
	
	private void setupOutputLabels()
	{
		lblHConText = new JLabel("<html>[H<sub>3</sub>O<sup>+</sup>]: </html>");
		lblHConValue = new JLabel();
		lblCompoundConText = new JLabel();
		lblCompoundConValue = new JLabel();
		lblProductConText = new JLabel();
		lblProductConValue = new JLabel();
		lblOHConText = new JLabel("<html>[OH<sup>-</sup>]: </html>");
		lblOHConValue  = new JLabel();
		lblPHText = new JLabel("PH: ");
		lblPHValue = new JLabel();
		lblPOHText = new JLabel("POH: ");
		lblPOHValue = new JLabel();
		lblHNumberText = new JLabel("<html>Number of H<sub>3</sub>O<sup>+</sup> Ions: </html>");
		lblHNumberValue = new JLabel();
		lblOHNumberText = new JLabel("<html>Number of OH<sup>-</sup> Ions: </html>");
		lblOHNumberValue = new JLabel();
		lblWaterNumberText = new JLabel("Number of Water Molecules: ");
		lblWaterNumberValue = new JLabel();
		lblKeqText = new JLabel("Keq: ");
		lblKeqValue = new JLabel();
		lblTempText = new JLabel("Temperature: ");
		lblTempValue = new JLabel();
		lblMolesCompound1Text = new JLabel("[<html>NH<sub>3</sub></html>]: ");
		lblMolesCompound1Value = new JLabel();
		lblMolesCompound2Text = new JLabel("[<html>NH<sub>4</sub>Cl</html>]: ");
		lblMolesCompound2Value = new JLabel();
		lblMolesCompound3Text = new JLabel("[HCl]:");
		lblMolesCompound3Value = new JLabel();
		lblMolesWaterText = new JLabel("Moles of Water: ");
		lblMolesWaterValue = new JLabel();
	}



	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false;
		Simulation simulation = getSimulation(sim, set);
		curTime = p5Canvas.getMain().time;
		
		
		switch(sim)
		{
		case 1:
			if(set==1)
				reactionHappened = reactSim1Set1(simulation);
			if(set==2)
				reactionHappened = reactSim1Set2(simulation);
			else if(set==3)
				reactionHappened = reactSim1Set3(simulation);
			break;
		case 2:
			//reactionHappened = reactSim2Set1(simulation);
			if(set==1)
			updatePositionSim2Set1(simulation);
			else if(set==2)
				updatePositionSim2Set2(simulation);
			else if(set==3)
				updatePositionSim2Set3(simulation);
			else
				updatePositionSim2Set4(simulation);
			break;
		case 3:
			if(set==1)
				reactionHappened = reactSim3Set1(simulation);
			else if(set==2)
				reactionHappened = reactSim3Set2(simulation);
			else if( set==3)
				reactionHappened = reactSim1Set2(simulation);
			else if(set==4)
				reactionHappened = reactSim3Set4(simulation);
			break;
		case 4:
			if(set==4)
				reactionHappened = reactSim4Set4(simulation);
			else if(set==2)
				reactionHappened = reactSim4Set2(simulation);
			else if(set==3)
				reactionHappened = reactSim4Set3(simulation);
			else if(set==1)
				reactionHappened = reactSim4Set1(simulation);
			break;
		case 5:	
			if(set==1)
			{
					reactionHappened = reactSim5Set1(simulation);

			}
			break;
		case 6:
			if(set==1)
				reactionHappened = reactSim1Set3(simulation);
			else if(set==2){
				reactionHappened = reactSim6Set2(simulation);
			}
			else if(set==3){
				reactionHappened = reactSim6Set3(simulation);
			}
			break;

		}
	}
	//Update the position of microscope molecules for Sim 2 Set 1
	private boolean updatePositionSim2Set1(Simulation simulation)
	{
		Molecule hydrogenIon = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
		Molecule hydroxide = State.getMoleculeByName(ionHash.get("Sodium-Hydroxide")[electronView]);

		updatePositionTwoMolcules(hydroxide,hydrogenIon,"Water","Sodium-Chloride");
		return true;
	}
	//Update the position of microscope molecules for Sim 2 Set 2
	private boolean updatePositionSim2Set2(Simulation simulation)
	{
		Molecule ammonia = State.getMoleculeByName(ionHash.get("Ammonia")[electronView]);
		Molecule hydrogen = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);

		updatePositionTwoMolcules(ammonia,hydrogen,"Ammonium","Chloride");
		return true;
	}
	
	//Update the position of microscope molecules for Sim 2 Set 3
	private boolean updatePositionSim2Set3(Simulation simulation)
	{
		Molecule cyanide = State.getMoleculeByName(ionHash.get("Cyanide")[electronView]);
		Molecule hydrogen = State.getMoleculeByName(ionHash.get("Hydrogen-Bromide")[electronView]);

		updatePositionTwoMolcules(cyanide,hydrogen,"Hydrogen-Cyanide","Bromide");
		return true;
	}
	
	//Update the position of microscope molecules for Sim 2 Set 4
	private boolean updatePositionSim2Set4(Simulation simulation)
	{
		Molecule boronTrichloride = State.getMoleculeByName(ionHash.get("Boron-Trichloride")[electronView]);
		Molecule chlorine = State.getMoleculeByName(ionHash.get("Chlorine-Ion")[electronView]);

		updatePositionTwoMolcules(boronTrichloride,chlorine,"Boron-Tetrachloride");
		return true;
	}
	
	private void updatePositionTwoMolcules(Molecule first, Molecule second,String ... product)
	{
		
		Vec2 pos = null;
		float angle =  0;
		
		if (!p5Canvas.isEnable || !p5Canvas.isSimStarted)
			return;
		
		//Rotate the first molecule
		if(interpolatorAngle1.isTargeting())
		{
			interpolatorAngle1.update();
			float newAngle = interpolatorAngle1.getValue();
			first.setAngle(newAngle);
			
//			if(!interpolatorAngle1.isTargeting())
//			{
//				System.out.println(" interpolatorAngle1 Stop targetting!");
//			}
		}
		//Rotate the second molecule
		if(interpolatorAngle2.isTargeting())
		{
			interpolatorAngle2.update();
			float newAngle = interpolatorAngle2.getValue();
			second.setAngle(newAngle);
			
//			if(!interpolatorAngle1.isTargeting())
//			{
//				System.out.println(" interpolatorAngle1 Stop targetting!");
//			}
		}
		
		//Move the first molecule
		if(interpolatorPos1.isTargeting())
		{
			interpolatorPos1.update();
			float currentPersentage = interpolatorPos1.getValue()/100;
			
			Vec2 translatePosition = new Vec2(translateVectorFirst.mul(currentPersentage));

			Vec2 currentPosition = new Vec2(lastPositionFirst.add(translatePosition));

			first.setPositionInPixel(currentPosition);

		}
		//Move the second molecule
		if(interpolatorPos2.isTargeting())
		{
			interpolatorPos2.update();
			float currentPersentage = interpolatorPos2.getValue()/100;
			
			Vec2 translatePosition = new Vec2(translateVectorSecond.mul(currentPersentage));

			Vec2 currentPosition = new Vec2(lastPositionSecond.add(translatePosition));

			second.setPositionInPixel(currentPosition);

//			if(!interpolatorPos2.isTargeting())
//			{
//				System.out.println(" interpolatorPos2 Stop targetting!");
//			}
		}
	
		
		if(hasFading)
		{
		//If both of the molecules finish moving, start fading out
		if(!interpolatorAngle1.isTargeting() && !interpolatorPos2.isTargeting()&& !isFading)
		{
			//Set up fade interpolator
			//Show`s transparency is from 0 - 100
			//Hide`s transparency is from 100 - 0
			interpolatorShow.set(100);
			interpolatorShow.target(0);
			interpolatorHide.set(0);
			interpolatorHide.target(100);
			
			Vec2 pos1 = first.getPositionInPixel();
			Vec2 pos2 = second.getPositionInPixel();
			//If there are two new molecules, seperate them a little bit
			if(product.length==2)
			{		
				Vec2 firstToSecond = pos2.sub(pos1);
				float ratio = 0.4f;
				firstToSecond.mulLocal(ratio);
				
				pos2.addLocal(firstToSecond);
				pos1.subLocal(firstToSecond);
			}
				
			//Create new molecules
			for(int i=0; i<product.length;i++)
			{
				if(i==0)
				{
					 pos = pos1;
					 angle = first.getAngle();
				}
				else if(i==1)
				{
					 pos = pos2;
					 angle = second.getAngle();
				}
				String compoundName = product[i];
				String svgFileName = null;
				if(electronView==1)  //Lewis Law
				{
					svgFileName = new String(compoundName+"-Dots");
			
				}
				else  //Bronsted Law
				{
					svgFileName = new String(compoundName);
				}
				Molecule newMole = new Molecule(pos.x,pos.y,compoundName,box2d,p5Canvas,angle,svgFileName);	
				newMole.setLinearVelocity(new Vec2(0, 0));
				newMole.setEnableAutoStateChange(false);
				newMole.setState(mState.Gas);
				newMole.setTransparent(1.0f);
				newMole.setFixtureCatergory(Constants.NOTMOLE_BOUND_ID, Constants.BOUNDARY_ID);
				if(this.electronView==1)
				{
					newMole.setImage(compoundName+"-Dots");
				}
				State.molecules.add(newMole);
				newMolecules.add(newMole);
				isFading = true;
			}
			

		}
		
		if(isFading)
		{
			
			if(interpolatorShow.isTargeting())
			{
				interpolatorShow.update();
				float tran = (float)interpolatorShow.getValue()/100;
				if(tran<0.01f)
					tran = 0.0f;
				//System.out.println("Show trans is "+tran);

				for(Molecule newMole:newMolecules)
				{
					newMole.setTransparent(tran);
				}
			}
			if(interpolatorHide.isTargeting())
			{
				interpolatorHide.update();
				float trans = (float)interpolatorHide.getValue()/100;
				if(trans>0.98f)
					trans=1.0f;
				//System.out.println("Hide trans is "+trans);
				first.setTransparent(trans);
				second.setTransparent(trans);
//				System.out.println("trans is "+trans);

			}
		}
		}

	}
	
	//Reaction function for sim 1 set 2
	public boolean reactSim1Set2(Simulation simulation) {

		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Sodium-Hydroxide")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Sodium-Hydroxide"))
							dissolveCompound = mOld[i];
					}

					Molecule mNew = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						
						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
						if(elementIndex !=-1 )
							loc.set(dissolveCompound.getElementLocation(elementIndex));
						x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								ionName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());

						State.molecules.add(mNew);
							mNew.body.setLinearVelocity(new Vec2(0,0));
							if(ionName.equals("Sodium-Ion")||ionName.equals("Hydroxide"))
							{
							//Set Sodium-Ion and Hydroxide tableIndex to "Sodium-Hydroxide"
							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Hydroxide");
							mNew.setTableIndex(tableIndex);
							}
						
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					
					updateCompoundNumber(simulation);
				}
			} 
		}
		return false;
	}
	//Reaction funciton for Sim 1 Set 3
	private boolean reactSim1Set3(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			
			// H+ + Water = H30
			if(p5Canvas.products.get(0).equals("Hydronium"))
			{
				Molecule hydrogenIon = null;
				Molecule water = null;
				// Get H-Ion and Water reference
				if (p5Canvas.killingList.get(0).getName()
						.equals("Hydrogen-Ion")) {
					hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
					water = (Molecule) p5Canvas.killingList.get(1);
				} else {
					water = (Molecule) p5Canvas.killingList.get(0);
					hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
				}
	
	//			Molecule silverChloride = null;
				Molecule newMole = null;
				Vec2 loc = null;
	
				//Create new molecule
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					loc = water.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
	
					String compoundName = new String(p5Canvas.products.get(i)); //"Hydronium"
					newMole = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					newMole.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(newMole);
					newMole.setLinearVelocity(water.body.getLinearVelocity());
					
					//Increate newMole count by 1
					int countIndex = Compound.names.indexOf(compoundName);
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
				}
	
				hydrogenIon.destroy();
				water.destroy();
				

				

				//Increase chlorine-Ion count by 1
				int countIndex = Compound.names.indexOf("Chloride");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
				//Change tableview value
				boolean chlorineChanged=false;
				Molecule mole = null;
				//Pick one chlorine-Ion  in reactants and set their table index as "Chloride"
				for( Molecule moleChlorine:State.getMoleculesByName("Chlorine-Ion"))
				{
					//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
					if(moleChlorine.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid")&&!chlorineChanged)
					{
						int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
						moleChlorine.setTableIndex(tableIndex);
						chlorineChanged=true;
					}	
				}
	
				//Decrease Hydrochloride-Acid count by 1
				countIndex = Compound.names.indexOf("Hydrochloric-Acid");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
				//Decrease Water count by 1
				countIndex = Compound.names.indexOf("Water");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
	
				}
//			//H3O + OH- = 2H2O
			else if(p5Canvas.products.get(0).equals("Water")) 
			{
				int hydroniumNum = State.getMoleculeNumByName("Hydronium");
				int hydroxideNum = State.getMoleculeNumByName("Hydroxide");
				Random rand = new Random();
				if (rand.nextFloat() > reactProbability) {
					 return false;
				}
				
					if (hydroniumNum>=1 && hydroxideNum>=1) {
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
							float width = Molecule.getShapeSize("Water", p5Canvas).x/2;
							Vec2 newVec = new Vec2( (x1 + (i%2==0?width:-width)), y1);
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
						int index = Compound.names.indexOf("Hydronium");
						Compound.counts.set(index, Compound.counts.get(index)-1);
						index = Compound.names.indexOf("Sodium-Hydroxide");
						Compound.counts.set(index, Compound.counts.get(index)-1);
						index = Compound.names.indexOf("Chloride");
						Compound.counts.set(index, Compound.counts.get(index)-1);
						index = Compound.names.indexOf("Sodium-Chloride");
						Compound.counts.set(index, Compound.counts.get(index)+1);
						index = Compound.names.indexOf("Water");
						Compound.counts.set(index, Compound.counts.get(index)+2);
						
						//Change tableview value
						boolean sodiumIonChanged = false;
						boolean chlorineChanged=false;
						Molecule mole = null;
						//Pick one chlorine-Ion  in reactants and set their table index as "Hydrochloric-Chloride"
						for( Molecule moleChlorine:State.getMoleculesByName("Chlorine-Ion"))
						{
							//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
							if(moleChlorine.getTableIndex()==p5Canvas.getTableView().getIndexByName("Chloride")&&!chlorineChanged)
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Chloride");
								moleChlorine.setTableIndex(tableIndex);
								chlorineChanged=true;
							}	
						}
						//Pick one Sodium-Ion  in reactants and set their table index as "Sodium-Chloride"
						for( Molecule moleSodium:State.getMoleculesByName("Sodium-Ion"))
						{
							//Change tableindex of Sodium-Ion from "Sodium-Hydroxide" to "Sodium-Chloride"
							if(moleSodium.getTableIndex()==p5Canvas.getTableView().getIndexByName("Sodium-Hydroxide")&&!sodiumIonChanged)
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Chloride");
								moleSodium.setTableIndex(tableIndex);
								sodiumIonChanged=true;
							}	
						}

						//updateMoleculeCon();
					}
				
//						if (hydroniumNum<1 || hydroxideNum<1) // If N2O4 is over numberred,
//																// break them up
//						{
//								 return breakApartCompound(simulation);
//						}
				
				return true;
			}
			
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
		}
		return false;
	}
		
	//Reaction funciton for Sim 1 Set 1
	private boolean reactSim1Set1(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			
			Random rand = new Random();
			if (rand.nextFloat() > reactProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
			
			Molecule hydrogenIon = null;
			Molecule water = null;
			if (p5Canvas.killingList.get(0).getName()
					.equals("Hydrogen-Ion")) {
				hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
				water = (Molecule) p5Canvas.killingList.get(1);
			} else {
				water = (Molecule) p5Canvas.killingList.get(0);
				hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
			}

			Molecule newMole = null;
			Vec2 loc = null;

			//Create new molecule
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				loc = water.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);

				String compoundName = new String(p5Canvas.products.get(i)); //"Hydronium"
				newMole = new Molecule(newVec.x, newVec.y,
						compoundName, box2d, p5Canvas,
						(float) (Math.PI / 2));
				newMole.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(newMole);
				newMole.setLinearVelocity(water.body.getLinearVelocity());
				
				//Increate newMole count by 1
				int countIndex = Compound.names.indexOf(compoundName);
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
				
			}

			hydrogenIon.destroy();
			water.destroy();
			
			//Change tableview value
			boolean chlorineChanged=false;
			Molecule mole = null;
			
			//Pick one chlorine-Ion  in reactants and set their table index as "Chloride"
			for( int i = 0;i<State.molecules.size();i++)
			{
				mole = State.molecules.get(i);
				//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Chloride"
				if(mole.getName().equals("Chlorine-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid")&&!chlorineChanged)
				{ 
					int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
					mole.setTableIndex(tableIndex);
					chlorineChanged=true;
				}

			}
			//Increase chlorine-Ion count by 1
			int countIndex = Compound.names.indexOf("Chloride");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

			//Decrease Hydrochloride-Acid count by 1
			countIndex = Compound.names.indexOf("Hydrochloric-Acid");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
			//Decrease Water count by 1
			countIndex = Compound.names.indexOf("Water");
			Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			return true;
		}
		return false;
	}
	
	
	//Reaction funciton for Sim 3 Set 1
	private boolean reactSim3Set1(Simulation simulation)
	{
		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Hydrogen-Chloride")) {
					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Hydrogen-Chloride"))
							dissolveCompound = mOld[i];
					}

					Molecule mNew = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						
						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
						if(elementIndex !=-1 )
							loc.set(dissolveCompound.getElementLocation(elementIndex));
						x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								ionName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());

						State.molecules.add(mNew);
							mNew.body.setLinearVelocity(new Vec2(0,0));
						
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					
					//updateCompoundNumber(simulation);
					//Increase chlorine-Ion count by 1
					int countIndex = Compound.names.indexOf("Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					//Increase Hydrogen-Ion count by 1
					countIndex = Compound.names.indexOf("Hydrogen-Ion");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					//Decrease Hydrogen-Chloride count by 1
					countIndex = Compound.names.indexOf("Hydrogen-Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
		
				
			}
			else //H+ + H20 = H3O +
			{
				Random rand = new Random();
				if (rand.nextFloat() > reactProbability) {
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					 return false;
				}
				
				Molecule hydrogenIon = null;
				Molecule water = null;
				if (p5Canvas.killingList.get(0).getName()
						.equals("Hydrogen-Ion")) {
					hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
					water = (Molecule) p5Canvas.killingList.get(1);
				} else {
					water = (Molecule) p5Canvas.killingList.get(0);
					hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
				}
	
				Molecule newMole = null;
				Vec2 loc = null;
	
				//Create new molecule
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					loc = water.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
	
					String compoundName = new String(p5Canvas.products.get(i)); //"Hydronium"
					newMole = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					newMole.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(newMole);
					newMole.setLinearVelocity(water.body.getLinearVelocity());
					
					
				}
	
				hydrogenIon.destroy();
				water.destroy();
				

				//Decrease Hydrochloride-Acid count by 1
				int countIndex = Compound.names.indexOf("Water");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
				//Decrease Water count by 1
				countIndex = Compound.names.indexOf("Hydrogen-Ion");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
				//Increase Hydronium count by 1
				countIndex = Compound.names.indexOf("Hydronium");
				Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
	
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;
	}
	
	//Reaction function for Sim 3 Set 2
	private boolean reactSim3Set2(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		int numHydronium  = State.getMoleculeNumByName("Hydronium");
		
		
		if (!p5Canvas.killingList.isEmpty())
		{
			if (p5Canvas.products != null && p5Canvas.products.size() > 0 && numHydronium<=1 ) {
	
				Random rand = new Random();
				if (rand.nextFloat() <= reactProbability) {				
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
						State.molecules.add(mNew);
		
						//Add upward velocity
						Vec2 velocity = mOld[0].body.getLinearVelocity();
						mNew.body.setLinearVelocity(velocity);
		
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					updateCompoundNumber(simulation);
					return true;
				}
				else 
				{
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			}
		}
		// Break up Compound if there are too many, in order to keep equalibrium
			
			if (numHydronium >1) // If PCl5 is over numberred,break them up			
			{
					 return breakApartCompound(simulation);
				
			}
		
		return false;
	}
	
	//Break apart N2O4 in Sim 1 Set 2  and Sim 2
	private boolean breakApartCompound(Simulation simulation)
	{
		Molecule product2 = null;
		String nameReactant1 = null;
		String nameReactant2 = null;
		String nameProduct1 = null;
		String nameProduct2 = null;
		float radius = 200;
		
		if(simulation.isSimSelected(unitNum, 3, 2)||simulation.isSimSelected(unitNum, 6, 3))//PCl3+Cl2<-->PCl5
		{
			nameReactant1 = "Hydrogen-Fluoride";
			nameReactant2 = "Water";
			nameProduct1 = "Fluoride";
			nameProduct2 = "Hydronium";
			if(simulation.getSimNum()==6&&simulation.getSetNum()==3)
			radius=750;


		}
		else if(simulation.isSimSelected(unitNum, 3, 4)||simulation.isSimSelected(unitNum, 5, 1)||simulation.isSimSelected(unitNum, 6, 2))
 		{
			nameReactant1 = "Ammonia";
			nameReactant2 = "Water";
			nameProduct1 = "Ammonium";
			nameProduct2 = "Hydroxide";
			if((simulation.getSimNum()==5&&simulation.getSetNum()==1)||(simulation.getSimNum()==6&&simulation.getSetNum()==2))
				radius=1000;
		}
		else if(simulation.isSimSelected(unitNum, 4, 1))
		{
			nameReactant1 = "Nitric-Acid";
			nameReactant2 = "Water";
			nameProduct1 = "Hydronium";
			nameProduct2 = "Nitrate";
		}
		else if(simulation.isSimSelected(unitNum, 4, 2))
		{
			nameReactant1 = "Lithium-Hydroxide";
			nameReactant2 = "Water";
			nameProduct1 = "Lithium-Ion";
			nameProduct2 = "Hydroxide";
		}
		else if(simulation.isSimSelected(unitNum, 4, 3))
		{
			nameReactant1 = "Methylamine";
			nameReactant2 = "Water";
			nameProduct1 = "Methylammonium";
			nameProduct2 = "Hydroxide";
		}
		else if(simulation.isSimSelected(unitNum, 4,4))
		{
			nameReactant1 = "Acetic-Acid";
			nameReactant2 = "Water";
			nameProduct1 = "Acetate";
			nameProduct2 = "Hydronium";
		}
		else if(simulation.isSimSelected(unitNum, 1, 3))
		{
			nameReactant1 = "Hydronium";
			nameReactant2 = "Hydroxide";
			nameProduct1 = "Water";
			nameProduct2 = "Water";
		}
		
		else if(simulation.isSimSelected(unitNum,1,1))
		{
			nameReactant1 = "Hydrogen-Ion";
			nameReactant2 = "Water";
			nameProduct1 = "Hydronium";
			nameProduct2 = "";
		}
		
		
		for (Molecule moleProduct1: State.getMoleculesByName(nameProduct1))
		{
				if(simulation.getSimNum()==6 && simulation.getSetNum()==2)
				{
					if(moleProduct1.getTableIndex()!= p5Canvas.getTableView().getIndexByName("Ammonium"))
					{
						continue;
					}
				}
				Vec2 loc = moleProduct1.getPosition();
			

				Vec2 locThis = box2d.coordWorldToPixels(loc); //Pixel coordiate of this molecule
				
				if(!nameProduct2.isEmpty()) //If there are two products
				{
					//Find the other product
					Vec2 locOther = null;
					boolean foundProduct2 = false;
	
					// Go through all molecules to check if there are any molecules
					// nearby
					for (Molecule moleProduct2:State.getMoleculesByName(nameProduct2))
					{
						if (moleProduct2!= moleProduct1 ) {
							 locOther = box2d.coordWorldToPixels(moleProduct2.getPosition());
							if (radius > computeDistance(locThis, locOther)) {
								foundProduct2 = true;
								product2 = moleProduct2;
								break; // Break after we find one nearby
							}
						}
					}
					
					//Return if we didnt find another product2
					if(!foundProduct2)
						return false;
				}
				
					
					//Ready to create reactants
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					
					Vec2 size = Molecule.getShapeSize(nameReactant1,
					p5Canvas);
				
				
					//Reactant 1
						newVec.x += size.x;
						Molecule mNew = new Molecule(newVec.x, newVec.y, nameReactant1, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew);
						mNew.setFreezingPoint(0);
						mNew.setBoillingPoint(100);
						mNew.body.setLinearVelocity(moleProduct1.body
								.getLinearVelocity());
					//Reactant 2
						newVec.x -= size.x;
						Molecule mNew2 = new Molecule(newVec.x, newVec.y, nameReactant2, box2d,
								p5Canvas, (float) (Math.PI / 2));
						mNew2.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(mNew2);
						mNew2.setFreezingPoint(0);
						mNew2.setBoillingPoint(100);
						mNew2.body.setLinearVelocity(moleProduct1.body
								.getLinearVelocity().mulLocal(-1));
				
				
						//Destroy product molecules and update molecule number
				if(mNew!=null &&mNew2!=null)
				{
					moleProduct1.destroy();
					if(product2!=null)
						product2.destroy();
	
					
					//Update molecule number
					if(simulation.isSimSelected(unitNum,1,1))
					{

						//Increase chlorine-Ion count by 1
						int countIndex = Compound.names.indexOf("Chloride");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
						countIndex = Compound.names.indexOf("Hydronium");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

						//Decrease Hydrochloride-Acid count by 1
						countIndex = Compound.names.indexOf("Hydrochloric-Acid");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						
						//Decrease Water count by 1
						countIndex = Compound.names.indexOf("Water");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						
						//Pick one Chloride  in reactants and set their table index as "Hydrochloric-Acid"
						boolean chlorineChanged = false;
						for( Molecule chloride:State.getMoleculesByName("Chlorine-Ion"))
						{
							//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
							if(chloride.getTableIndex()==p5Canvas.getTableView().getIndexByName("Chloride")&&!chlorineChanged)
							{ 
								int tableIndex = p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid");
								chloride.setTableIndex(tableIndex);
								chlorineChanged=true;
							}

						}
						boolean hydrogenChanged = false;
						for( Molecule hydrogen:State.getMoleculesByName("Hydrogen-Ion"))
						{
							//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
							if(hydrogen.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrogen-Ion")&&!hydrogenChanged)
							{ 
								int tableIndex = p5Canvas.getTableView().getIndexByName("Hydrochloric-Acid");
								hydrogen.setTableIndex(tableIndex);
								hydrogenChanged=true;
							}

						}
						
					}
					
					else if (simulation.isSimSelected(unitNum,5,1)|| simulation.isSimSelected(unitNum, 6,2))
					{
						//Increase Ammonia by 1
						int countIndex = Compound.names.indexOf("Ammonia");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						
						//Increase Water by 1
						countIndex = Compound.names.indexOf("Water");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

						//Decrease Ammonium by 1
						countIndex = Compound.names.indexOf("Ammonium");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
						//Decrease Hydroxide count by 1
						countIndex = Compound.names.indexOf("Hydroxide");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
						boolean ammoniumChanged = false;
						for( Molecule ammonium:State.getMoleculesByName("Ammonium"))
						{
							//Change table index of Ammonium from "Ammonium" to "Ammonium Chloride"
							if(ammonium.getTableIndex()!=p5Canvas.getTableView().getIndexByName("Ammonium")&&!ammoniumChanged)
							{ 
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								ammonium.setTableIndex(tableIndex);
								ammoniumChanged=true;
							}

						}
					}
					else
					{
					int indexProduct1 = Compound.names.indexOf(nameProduct1);
					int indexProduct2 = Compound.names.indexOf(nameProduct2);
					int indexReactant1 = Compound.names.indexOf(nameReactant1);
					int indexReactant2 = Compound.names.indexOf(nameReactant2);
	
					Compound.counts.set(indexProduct1, Compound.counts.get(indexProduct1)-1);
					if(indexProduct2!=-1)
					Compound.counts.set(indexProduct2, Compound.counts.get(indexProduct2)-1);
					Compound.counts.set(indexReactant1, Compound.counts.get(indexReactant1)+1);
					Compound.counts.set(indexReactant2, Compound.counts.get(indexReactant2)+1);
					}
					
					reachEquilibrium = true;
					oldTime = curTime;
					return true;
				}
			
		}
		return false;
	}
	
	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}
	
	//Reaction function for Sim 3 Set 4
	private boolean reactSim3Set4(Simulation simulation)
	{
		
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		int numAmmonia = State.getMoleculeNumByName("Ammonia");
		int threshold = 9;
		
		if (!p5Canvas.killingList.isEmpty())
		{		
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && numAmmonia >= threshold ) {
			Random rand = new Random();
			if (rand.nextFloat() > reactProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
						
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
						mNew.setFreezingPoint(0);
						mNew.setBoillingPoint(100);
						State.molecules.add(mNew);
		
						//Add upward velocity
						Vec2 velocity = mOld[0].body.getLinearVelocity();
						mNew.body.setLinearVelocity(velocity);
		
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					updateCompoundNumber(simulation);
	
		}
		
		numAmmonia = State.getMoleculeNumByName("Ammonia");
			// Break up Compound if there are too many, in order to keep equalibrium
			
			if (numAmmonia<threshold) // If PCl5 is over numberred,break them up			
			{
				return breakApartCompound(simulation);
			}
		}
		return false;
	}
	
	//Reaction function for Sim 4 Set 1
	private boolean reactSim4Set4(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		int numAceticAcid = State.getMoleculeNumByName("Acetic-Acid");
		int threshold  = 9;

		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && numAceticAcid>=threshold) {

			Random rand = new Random();
			if (rand.nextFloat() > reactProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
			
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
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
		}
		
		numAceticAcid = State.getMoleculeNumByName("Acetic-Acid");
			
			if (numAceticAcid<threshold) // If PCl5 is over numberred,break them up			
			{
					 return breakApartCompound(simulation);
			}
		
		return false;
	}
	
	//Reaction function for Sim 5 Set 1
	private boolean reactSim5Set1(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;

		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			if(p5Canvas.killingList.get(0).getName().equals("Hydrogen-Ion")||p5Canvas.killingList.get(1).getName().equals("Hydrogen-Ion"))
			{
					Molecule hydrogenIon = null;
					Molecule water = null;
					String name1 = p5Canvas.killingList.get(0).getName();
					String name2 = p5Canvas.killingList.get(1).getName();
					// Get Iron and copperIon reference
					if (name1.equals("Hydrogen-Ion")&&(name2.equals("Water")||name2.equals("Ammonia"))) {
						hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
						water = (Molecule) p5Canvas.killingList.get(1);
					} else if (name2.equals("Hydrogen-Ion")&&(name1.equals("Water")||name1.equals("Ammonia"))){
						water = (Molecule) p5Canvas.killingList.get(0);
						hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
					}
					if(hydrogenIon==null || water==null)
						return false;

					Molecule newMole = null;
					Vec2 loc = null;

					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = water.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);

						String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
						newMole = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						newMole.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(newMole);
						newMole.setLinearVelocity(water.body.getLinearVelocity());
						if(newMole.getName().equals("Ammonium"))
						{
							int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
							newMole.setTableIndex(tableIndex);
						}
					}

					hydrogenIon.destroy();
					water.destroy();
					

					if(water.getName().equals("Ammonia"))
					{
						//Change count numbers in Table View
						int countIndex=0;
						//Increase Ammonium-Chloride count by 1
						countIndex = Compound.names.indexOf("Ammonium-Chloride");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

						//Decrease Hydrochloric-Acid count by 1
						countIndex = Compound.names.indexOf("Hydrochloric-Acid");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
						//Decrease Ammonia count by 1
						countIndex = Compound.names.indexOf("Ammonia");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
								
						//Associate molecule with correct table view index
						for(Molecule mole:State.getMoleculesByName("Chlorine-Ion"))
						{
							//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric Acid"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
						}
					}
					else if(water.getName().equals("Water"))
					{
						int countIndex=0;
						//Increate Hydronium count by 1
						countIndex = Compound.names.indexOf("Hydronium");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						//Increate Chloride count by 1
						countIndex = Compound.names.indexOf("Chloride");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						
						//Decrease Ammonium count by 1
						countIndex = Compound.names.indexOf("Water");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						countIndex = Compound.names.indexOf("Hydrochloric-Acid");
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
						//Associate molecule with correct table view index
						for( Molecule mole : State.getMoleculesByName("Chlorine-Ion"))
						{
							//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric Acid"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
						}
						
					}
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
	
					//Check if reach equilibrium
					if(State.getMoleculeNumByName("Ammonia")==0&&State.getMoleculeNumByName("Hydrogen-Ion")==0)
					{
						reachEquilibrium = true;
						System.out.println("Reached Equilibrium");
					}
					return true;
			
			}
			else if(p5Canvas.killingList.get(0).getName().equals("Hydronium")||p5Canvas.killingList.get(1).getName().equals("Hydronium"))
			{
				Molecule ammonia = null;
				Molecule hydronium = null;
				String name1 = p5Canvas.killingList.get(0).getName();
				String name2 = p5Canvas.killingList.get(1).getName();

				if (name1.equals("Ammonia")&&name2.equals("Hydronium")) {
					ammonia = (Molecule) p5Canvas.killingList.get(0);
					hydronium = (Molecule) p5Canvas.killingList.get(1);
				} else if (name2.equals("Ammonia")&&name1.equals("Hydronium")){
					hydronium = (Molecule) p5Canvas.killingList.get(0);
					ammonia = (Molecule) p5Canvas.killingList.get(1);
				}
				if(ammonia==null || hydronium==null)
					return false;

				Molecule newMole = null;
				Vec2 loc = null;

				for (int i = 0; i < p5Canvas.products.size(); i++) {
					loc = ammonia.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);

					String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
					newMole = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					newMole.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(newMole);
					newMole.setLinearVelocity(ammonia.body.getLinearVelocity().mul(i%2==0?1f:-1f));
					if(newMole.getName().equals("Ammonium"))
					{
						int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
						newMole.setTableIndex(tableIndex);
					}
					
				}

					ammonia.destroy();
					hydronium.destroy();
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					//Change count numbers in Table View
					int countIndex=0;
					//Increase Ammonium-Chloride count by 1
					countIndex = Compound.names.indexOf("Ammonium-Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Increase Water count by 1
					countIndex = Compound.names.indexOf("Water");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

					//Decrease Chloride count by 1
					countIndex = Compound.names.indexOf("Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Decrease Hydronium count by 1
					countIndex = Compound.names.indexOf("Hydronium");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Decrease Ammonia count by 1
					countIndex = Compound.names.indexOf("Ammonia");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					
					//Change tableview value
					boolean chlorineChanged=false;
					Molecule mole = null;
					
					//Associate molecule with correct table view index
					for( int i = 0;i<State.molecules.size();i++)
					{
						mole = State.molecules.get(i);
						//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
						if(mole.getName().equals("Chlorine-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Chloride")&&!chlorineChanged)
						{
							int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
							mole.setTableIndex(tableIndex);
							chlorineChanged=true;
						}
					}
			}
			else
			{	//NH3 + H20 == NH4 +OH-
				int numAmmonia = State.getMoleculeNumByName("Ammonia");
				int threshold = 8;
				if(numHclAdded ==5)
					threshold = 3;
				if(numAmmonia>=threshold)
				{
					Random rand = new Random();
					if (rand.nextFloat() > reactProbability) {
						p5Canvas.products.clear();
						p5Canvas.killingList.clear();
						 return false;
					}
					
					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					for (int i = 0; i < numToKill; i++)
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
		
					Molecule mNew = null;
					Molecule mNew2 = null;
		
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
						mNew.setFreezingPoint(0);
						mNew.setBoillingPoint(100);
						State.molecules.add(mNew);
		
						//Add upward velocity
						Vec2 velocity = mOld[0].body.getLinearVelocity();
						mNew.body.setLinearVelocity(velocity);

					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					
					//update compound number
					//Increase Ammonium count by 1
					int countIndex = Compound.names.indexOf("Ammonium");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					//Increase Hydroxide count by 1
					countIndex = Compound.names.indexOf("Hydroxide");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					//Decrease Ammonia count by 1
					countIndex = Compound.names.indexOf("Ammonia");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					//Decrease Water count by 1
					countIndex = Compound.names.indexOf("Water");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
	
				}
		
			numAmmonia = State.getMoleculeNumByName("Ammonia");
			// Break up Compound if there are too many, in order to keep equalibrium
			
			if (numAmmonia<threshold) // If PCl5 is over numberred,break them up			
			{
				return breakApartCompound(simulation);
			}
		}
		}
		return false;
	}
	
	//Reaction function for Sim 4 Set 3
	private boolean reactSim4Set3(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		int numMethylamine = State.getMoleculeNumByName("Methylamine");
		int threshold = 9;
		
		
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && numMethylamine>=threshold) {

			Random rand = new Random();
			if (rand.nextFloat() > reactProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
			
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
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
		}
		
		// Break up Compound if there are too many, in order to keep equalibrium
			numMethylamine = State.getMoleculeNumByName("Methylamine");
			if (numMethylamine<threshold) // If PCl5 is over numberred,break them up			
				{
					 return breakApartCompound(simulation);
				}
		return false;
	}
	
	//Reaction funciton for Sim 4 Set 4
	private boolean reactSim4Set1(Simulation simulation)
	{
		int numNitric = State.getMoleculeNumByName("Nitric-Acid");
		
		int threshold = 1;
	
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0 && numNitric>=threshold) {

			Random rand = new Random();
			if (rand.nextFloat() > reactProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
			
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
				mNew.setFreezingPoint(0);
				mNew.setBoillingPoint(100);
				State.molecules.add(mNew);

				//Add upward velocity
				Vec2 velocity = mOld[0].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
		}
		//updateMoleculeCon();

		// Break up Compound if there are too many, in order to keep equalibrium

		numNitric = State.getMoleculeNumByName("Nitric-Acid");
			if (numNitric<threshold) // If PCl5 is over numberred,break them up			
				{
					 return breakApartCompound(simulation);
			}
		
		return false;
		
	}
	
	private boolean reactSim4Set2(Simulation simulation)
	{
		
		int numLithiumHydroxide = State.getMoleculeNumByName("Lithium-Hydroxide");
		int threshold = 2;
		
		if (p5Canvas.killingList.isEmpty()) {
			return false;
		}
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Lithium-Hydroxide") ) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0&& numLithiumHydroxide>=threshold) {
					
					Random rand = new Random();
					if (rand.nextFloat() > reactProbability) {
						p5Canvas.products.clear();
						p5Canvas.killingList.clear();
						 return false;
					}

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Lithium-Hydroxide"))
							dissolveCompound = mOld[i];
					}

					Molecule mNew = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						
						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
						if(elementIndex !=-1 )
							loc.set(dissolveCompound.getElementLocation(elementIndex));
						x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						mNew = new Molecule(newVec.x, newVec.y,
								ionName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());

						State.molecules.add(mNew);
						mNew.body.setLinearVelocity(new Vec2(0,0));
						
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					
					updateCompoundNumber(simulation);
				}
			} 
			
			numLithiumHydroxide = State.getMoleculeNumByName("Lithium-Hydroxide");
			if(numLithiumHydroxide<threshold)
			{
				return this.breakApartCompound(simulation);
			}
		
		return false;
	}
	
	
	//Reaction funciton for Sim 6 Set 2
	private boolean reactSim6Set2(Simulation simulation)
	{
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			if(p5Canvas.killingList.get(0).getName().equals("Hydrogen-Ion")||p5Canvas.killingList.get(1).getName().equals("Hydrogen-Ion")) //H+ + NH3 or H+ + Water
			{
				Molecule hydrogenIon = null;
				Molecule water = null;
				String name1 = p5Canvas.killingList.get(0).getName();
				String name2 = p5Canvas.killingList.get(1).getName();
				// Get Iron and copperIon reference
				if (name1.equals("Hydrogen-Ion")&&(name2.equals("Water")||name2.equals("Ammonia"))) {
					hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
					water = (Molecule) p5Canvas.killingList.get(1);
				} else if (name2.equals("Hydrogen-Ion")&&(name1.equals("Water")||name1.equals("Ammonia"))){
					water = (Molecule) p5Canvas.killingList.get(0);
					hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
				}
				if(hydrogenIon==null || water==null)
					return false;
	
	//			Molecule silverChloride = null;
				Molecule newMole = null;
				Vec2 loc = null;
	
				//Create new molecule
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					loc = water.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
	
					String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
					newMole = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					newMole.setRatioKE(1 / simulation.getSpeed());
					State.molecules.add(newMole);
					newMole.setLinearVelocity(water.body.getLinearVelocity());
					if(newMole.getName().equals("Ammonium"))
					{
						int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
						newMole.setTableIndex(tableIndex);
					}
					
				}
	
				hydrogenIon.destroy();
				water.destroy();
				

				
	
				
				if(water.getName().equals("Ammonia"))
				{
					//Change count numbers in Table View
					int countIndex=0;
					//Increase Ammonium-Chloride count by 1
					countIndex = Compound.names.indexOf("Ammonium-Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
	
					//Decrease Hydrochloric-Acid count by 1
					countIndex = Compound.names.indexOf("Hydrochloric-Acid");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Decrease Ammonia count by 1
					countIndex = Compound.names.indexOf("Ammonia");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
					for(Molecule mole: State.getMoleculesByName("Chlorine-Ion"))
					{
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric Acid"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
					}
					
					//Change tableindex of Ammonium from "Ammonium" to "Ammonium-Chloride"
					for(Molecule mole: State.getMoleculesByName("Ammonium"))
					{
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Ammonium"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
					}
						
							
					
					
				}
				else if(water.getName().equals("Water"))
				{
					int countIndex=0;
					//Increate Hydronium count by 1
					countIndex = Compound.names.indexOf("Hydronium");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Increate Hydronium count by 1
					countIndex = Compound.names.indexOf("Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Decrease Water count by 1
					countIndex = Compound.names.indexOf("Water");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					countIndex = Compound.names.indexOf("Hydrochloric-Acid");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					


					//Associate molecule with correct table view index
					//Change tableindex of Chlorine-Ion from "Hydrochloric-Acid" to "Ammonium-Chloride"
					for(Molecule mole: State.getMoleculesByName("Chlorine-Ion"))
					{
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric Acid"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
					}
//					//Change tableindex of Chlorine-Ion from "Hydrochloride Acid" to "Chloride"
//					for(Molecule mole: State.getMoleculesByName("Chlorine-Ion"))
//					{
//						if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Hydrochloric Acid"))
//						{
//							int tableIndex = p5Canvas.getTableView().getIndexByName("Chloride");
//							mole.setTableIndex(tableIndex);
//							break;
//						}
//					}
				
				}
	
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				
				return true;
			}
			else if(p5Canvas.killingList.get(0).getName().equals("Ammonia")||p5Canvas.killingList.get(1).getName().equals("Ammonia"))
			{// H3O+ + NH3 = NH4+	
				Molecule hydronium = null;
				Molecule ammonia = null;
				String name1 = p5Canvas.killingList.get(0).getName();
				String name2 = p5Canvas.killingList.get(1).getName();
				// Get Iron and copperIon reference
				if ((name1.equals("Hydronium")||name1.equals("Water"))&&name2.equals("Ammonia")) {
					hydronium = (Molecule) p5Canvas.killingList.get(0);
					ammonia = (Molecule) p5Canvas.killingList.get(1);
				} else if ((name2.equals("Hydronium")||name2.equals("Water"))&&name1.equals("Ammonia")){
					ammonia = (Molecule) p5Canvas.killingList.get(0);
					hydronium = (Molecule) p5Canvas.killingList.get(1);
				}
				if(hydronium==null || ammonia==null)
					return false;
				if(hydronium.getName().equals("Hydronium"))
				{
					
	//			Molecule silverChloride = null;
				Molecule newMole = null;
				Vec2 loc = null;
	
				//Create new molecule
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					loc = ammonia.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
	
					String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
					newMole = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					newMole.setRatioKE(1 / simulation.getSpeed());
					newMole.setState(mState.Liquid);
					State.molecules.add(newMole);
					newMole.setLinearVelocity(ammonia.body.getLinearVelocity());
					if(newMole.getName().equals("Ammonium"))
					{
						int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium");
						newMole.setTableIndex(tableIndex);
					}
				}
	
				hydronium.destroy();
				ammonia.destroy();
	
					//Change count numbers in Table View
					int countIndex=0;
					//Increase Ammonium-Chloride count by 1
					countIndex = Compound.names.indexOf("Ammonium-Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Increase Water count by 1
					countIndex = Compound.names.indexOf("Water");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Decrease Chloride count by 1
					countIndex = Compound.names.indexOf("Chloride");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
	
					//Decrease Hydronium count by 1
					countIndex = Compound.names.indexOf("Hydronium");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Decrease Ammonia count by 1
					countIndex = Compound.names.indexOf("Ammonia");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					//Change tableindex of Chlorine-Ion from "Chloride" to "Ammonium-Chloride"
					for(Molecule mole: State.getMoleculesByName("Chlorine-Ion"))
					{
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Chloride"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
					}
					
					//Change tableindex of Ammonium from "Ammonium" to "Ammonium-Chloride"
					for(Molecule mole: State.getMoleculesByName("Ammonium"))
					{
							if(mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Ammonium"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium Chloride");
								mole.setTableIndex(tableIndex);
								break;
							}
					}
						
					
					
				
				
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				
				return true;
				}
				else //Ammonia reacts with Water
				{
					int numAmmonia = State.getMoleculeNumByName("Ammonia");
					int threshold = 9;
					if(numAmmonia>=threshold)
					{
						
						Random rand = new Random();
						if (rand.nextFloat() > reactProbability) {
							p5Canvas.products.clear();
							p5Canvas.killingList.clear();
							 return false;
						}
					Molecule newMole = null;
					Vec2 loc = null;
		
					//Create new molecule
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = ammonia.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
		
						String compoundName = new String(p5Canvas.products.get(i)); //"Water" or "Ammonium"
						newMole = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						newMole.setRatioKE(1 / simulation.getSpeed());
						State.molecules.add(newMole);
						newMole.setLinearVelocity(ammonia.body.getLinearVelocity());
						if(newMole.getName().equals("Ammonium"))
						{
							int tableIndex = p5Canvas.getTableView().getIndexByName("Ammonium");
							newMole.setTableIndex(tableIndex);
						}
					}
		
					hydronium.destroy();
					ammonia.destroy();
				
					//Change count numbers in Table View
					int countIndex=0;
					//Increase Ammonium count by 1
					countIndex = Compound.names.indexOf("Ammonium");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Increase Hydroxide count by 1
					countIndex = Compound.names.indexOf("Hydroxide");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
					//Decrease Ammonia count by 1
					countIndex = Compound.names.indexOf("Ammonia");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
	
					//Decrease Water count by 1
					countIndex = Compound.names.indexOf("Water");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						
					
				
	
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					}
					numAmmonia = State.getMoleculeNumByName("Ammonia");
					if(numAmmonia<threshold)
					{
						this.breakApartCompound(simulation);
					}
					
				}
			}
		}
		return false;
	}
	
	
	//Reaction function for Sim 3 Set 2
	private boolean reactSim6Set3(Simulation simulation)
	{
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		
		int numHydronium  = State.getMoleculeNumByName("Hydronium");
		
		
		if (!p5Canvas.killingList.isEmpty())
		{
			if(p5Canvas.products != null && p5Canvas.products.size() > 0)
			{
			
				if ( (p5Canvas.killingList.get(0).getName().equals("Hydrogen-Fluoride")||p5Canvas.killingList.get(1).getName().equals("Hydrogen-Fluoride")) && numHydronium<=1 ) {
		
					Random rand = new Random();
					int numHF = State.getMoleculeNumByName("Hydrogen-Fluoride");
					if (rand.nextFloat() <= reactProbability*(10f/numHF)) {				
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
							State.molecules.add(mNew);
			
							//Add upward velocity
							Vec2 velocity = mOld[0].body.getLinearVelocity();
							mNew.body.setLinearVelocity(velocity);
			
						}
						for (int i = 0; i < numToKill; i++)
							mOld[i].destroy();
						p5Canvas.products.clear();
						p5Canvas.killingList.clear();
						//Update molecule number
						int index = Compound.names.indexOf("Hydrogen-Fluoride");
						Compound.counts.set(index, Compound.counts.get(index)-1);
						index = Compound.names.indexOf("Water");
						Compound.counts.set(index, Compound.counts.get(index)-1);
						index = Compound.names.indexOf("Hydronium");
						Compound.counts.set(index, Compound.counts.get(index)+1);
						index = Compound.names.indexOf("Fluoride");
						Compound.counts.set(index, Compound.counts.get(index)+1);

						return true;
					}
					else 
					{
						p5Canvas.products.clear();
						p5Canvas.killingList.clear();
					}
				}
				else if(p5Canvas.products.get(0).equals("Water")) //H3O+ + OH- = H2O
					{
								int numToKill = p5Canvas.killingList.size();
								Molecule[] mOld = new Molecule[numToKill];
								for (int i = 0; i < numToKill; i++) 
									mOld[i] = (Molecule) p5Canvas.killingList.get(i);
								

								Molecule mNew = null;
								String nameNew = null;
								for (int i = 0; i < p5Canvas.products.size(); i++) {
									// Reacts at the postion of nitrylChloride
									Vec2 loc = mOld[0].getPosition();
									float x1 = PBox2D.scalarWorldToPixels(loc.x);
									float y1 = p5Canvas.h * p5Canvas.canvasScale
											- PBox2D.scalarWorldToPixels(loc.y);
									float width = Molecule.getShapeSize("Water", p5Canvas).x/2;
									Vec2 newVec = new Vec2( (x1 + (i%2==0?width:-width)), y1);
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
								int index = Compound.names.indexOf("Hydronium");
								Compound.counts.set(index, Compound.counts.get(index)-1);
								index = Compound.names.indexOf("Hydroxide");
								Compound.counts.set(index, Compound.counts.get(index)-1);
								index = Compound.names.indexOf("Water");
								Compound.counts.set(index, Compound.counts.get(index)+2);

								//updateMoleculeCon();
							}
						
			}
		}
		// Break up Compound if there are too many, in order to keep equalibrium
			numHydronium  = State.getMoleculeNumByName("Hydronium");

			if (numHydronium >1) // If PCl5 is over numberred,break them up			
			{
					 return breakApartCompound(simulation);
				
			}
		
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {
		
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Molecule first = null;
		Molecule second = null;
		
		if(sim==2)
		{
			if(set==1)
			{
				second = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
				first  = State.getMoleculeByName(ionHash.get("Sodium-Hydroxide")[electronView]);
				initializeTwoMolcules(first	, second,(float)Math.PI,0f,0.7f);
			}
			else if(set==2)
			{
				first = State.getMoleculeByName(ionHash.get("Ammonia")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Hydrogen-Chloride")[electronView]);
				initializeTwoMolcules(first	, second,(float)Math.PI/2,(float)Math.PI,0.75f);
			}
			else if(set==3)
			{
				first = State.getMoleculeByName(ionHash.get("Cyanide")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Hydrogen-Bromide")[electronView]);
				initializeTwoMolcules(first	, second,(float)Math.PI,(float)Math.PI,1.1f);
			}
			else if(set==4)
			{
				first = State.getMoleculeByName(ionHash.get("Boron-Trichloride")[electronView]);
				second  = State.getMoleculeByName(ionHash.get("Chlorine-Ion")[electronView]);
				initializeTwoMolcules(first	, second,(float)Math.PI/2,0, 0.5f);
			}
	
			
		}
	}
	
	
	//Initialize the interpolators for later molecule update
	//The first one rotates and the second translate
	//minRangeRatio means the ratio of minimum distance between the two molecules
	private void initializeTwoMolcules(Molecule first, Molecule second, float angle1,float angle2,float minRangeRatio)
	{

		Vec2 posFirst = first.getPositionInPixel();
		Vec2 posSecond = second.getPositionInPixel();
		float distance = computeDistance(posFirst,posSecond);
		
		float theta = 0;
		float angleFirst = 0 ;
		float angleSecond = 0;
		
		if(!interpolatorAngle1.isTargeting() && !interpolatorAngle2.isTargeting())
		{
			float acosValue = (posSecond.x-posFirst.x)/distance;
			theta = (float) Math.acos(acosValue);

			if(posFirst.y>posSecond.y) //In phase I and II cos function is monotone decreasing
			{
				//first = - theta
				//second = PI - theta
				angleFirst = theta;
				angleSecond = (float) (theta-Math.PI);
			}
			else // posFirst.y < posSecond
			{
				//first = theta
				//second = PI + theta
				angleFirst = -theta;
				angleSecond = (float) (Math.PI - theta);
			}
			
			angleFirst+=angle1;
			angleSecond+=angle2;
//			angleBetween+=Math.PI + angle; 
//			if(angleBetween>Math.PI)
//			{
//				angleBetween-=2*Math.PI;
//			}
			interpolatorAngle1.set(first.getAngle());
			interpolatorAngle1.target(angleFirst);
			interpolatorAngle2.set(second.getAngle());
			interpolatorAngle2.target(angleSecond);
		}

		if(!interpolatorPos2.isTargeting() && !interpolatorPos1.isTargeting())
		{
			float minDist = first.getShapeSize().x*minRangeRatio;
			if(distance>minDist)
			{
				
				float ratio = (distance - minDist)/distance;
				ratio/=2;
				lastPositionSecond.set(posSecond);
				translateVectorSecond.set((posFirst.sub(posSecond)).mul(ratio));
				
				lastPositionFirst.set(posFirst);
				translateVectorFirst.set((posSecond.sub(posFirst)).mul(ratio));
				
				interpolatorPos1.set(0);
				interpolatorPos1.target(100);  // 0 - 100 %
				interpolatorPos2.set(0);
				interpolatorPos2.target(100);  // 0 - 100 %

			}
		}

	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {
		
		keq = 0.01f;
		curTime = 0;
		oldTime = 0;
		breakProbability = 0.75f; // The chance that N2O4 will break apart
		pH = 7;
		interpolatorAngle1.setTargeting(false);
		interpolatorAngle1.setAttraction(0.25f);
		interpolatorAngle1.setDamping(0.4f);
		interpolatorAngle2.setTargeting(false);
		interpolatorAngle2.setAttraction(0.25f);
		interpolatorAngle2.setDamping(0.4f);
		interpolatorPos1.setTargeting(false);
		interpolatorPos1.setAttraction(0.25f);
		interpolatorPos1.setDamping(0.4f);
		interpolatorPos2.setTargeting(false);
		interpolatorPos2.setAttraction(0.25f);
		interpolatorPos2.setDamping(0.4f);
		
		interpolatorHide.setTargeting(false);
		interpolatorHide.setAttraction(0.15f);
		interpolatorHide.setDamping(0.2f);
		interpolatorShow.setTargeting(false);
		interpolatorShow.setAttraction(0.15f);
		interpolatorShow.setDamping(0.2f);
		hasFading = false;
		isFading = false;
		reactProbability =0.5f;
		volumeMagnifier =31.746f;  //In order to make the volume 2L

		
		newMolecules.clear();

		// Customization
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		resetConcentrationMap(simulation);

		
		// Set up speed ratio for molecules
		setupSpeed();
		
		switch(sim)
		{
		default:
			break;
		case 1:
			if(set==1)
			{
				reactProbability = 0.5f;
			}
			break;
		case 2:
			p5Canvas.isBoundaryShow = false;
			p5Canvas.setIfConstrainKE(false);
			p5Canvas.setEnableDrag(false);  //Disable drag function
			if(electronView==0)  //Bronsted Lowry Law
			{
				hasFading = true;
				//Set up simulation
				for(String name: ionHash.keySet())
				{
				simulation.setElementByIndex(ionHash.get(name)[0], simulation.getElementIndex(ionHash.get(name)[1]));
				}
			}
			else  //Lewis Law
			{
				hasFading = true;
				//Set up simulation
				for(String name: ionHash.keySet())
				{
				simulation.setElementByIndex(ionHash.get(name)[1], simulation.getElementIndex(ionHash.get(name)[0]));
				}
			}
			
			//disable view selection button
			main.btnBronsted.setEnabled(true);
			main.btnLewis.setEnabled(true);
			break;
		case 3:
			if(set==1)
				reactProbability = 1.0f;
			else if(set==2)
				reactProbability = 0.025f;
			else if(set==3)
				;
			else if(set==4)
				reactProbability = 0.015f;
			break;
		case 4:
			if(set==1)
			{
				reactProbability = 0.6f;
				keq =18f;
			}
			else if(set==2)
			{
				reactProbability = 0.5f;
				keq = 4.120f;
			}
			else if(set==4)
			{
			
				keq = 0.000032f;
//				System.out.println("keq is "+keq);
				reactProbability = 0.01f;
			}
			else if(set==3)
			{
				reactProbability = 0.008f;
				keq = 0.0008f;
			}
			break;
		case 5:
			if(set==1)
				{
				reactProbability = 0.035f;
				numHclAdded=0;
			}
			break;
		case 6:
			baseAdded =0;
			baseUpdated = 0;
			
			if(set==1)
				{
					baseAddedMap[0] = 0;
					baseAddedMap[1] = 0.1f;
					baseAddedMap[2] = 0.25f;
					baseAddedMap[3] = 0.45f;
					baseAddedMap[4] = 0.7f;
					baseAddedMap[5] = 1.0f;
					baseAddedMap[6] = 1.4f;
					baseAddedMap[7] = 2.0f;
					baseAddedMap[8] = 3f;
					baseAddedMap[9] = 4.5f;
					baseAddedMap[10] = 7;
					baseAddedMap[11] = 9.5f;
					baseAddedMap[12] = 11f;
					baseAddedMap[13] = 12f;
					baseAddedMap[14] = 12.6f;
					baseAddedMap[15] = 13f;
					baseAddedMap[16] = 13.3f;
					baseAddedMap[17] = 13.55f;
					baseAddedMap[18] = 13.75f;
					baseAddedMap[19] = 13.9f;
					baseAddedMap[20] = 14;
				}
			else if(set==2)
			{
				baseAddedMap[0] = 0;
				baseAddedMap[1] = 0.1f;
				baseAddedMap[2] = 0.15f;
				baseAddedMap[3] = 0.20f;
				baseAddedMap[4] = 0.25f;
				baseAddedMap[5] = 0.3f;
				baseAddedMap[6] = 0.5f;
				baseAddedMap[7] = 0.7f;
				baseAddedMap[8] = 0.9f;
				baseAddedMap[9] = 1.2f;
				baseAddedMap[10] = 1.6f;
				baseAddedMap[11] = 2.2f;
				baseAddedMap[12] = 3f;
				baseAddedMap[13] = 5f;
				baseAddedMap[14] = 7f;
				baseAddedMap[15] = 8.4f;
				baseAddedMap[16] = 9.0f;
				baseAddedMap[17] = 9.25f;
				baseAddedMap[18] = 9.34f;
				baseAddedMap[19] = 9.40f;
				baseAddedMap[20] = 9.46f;
				
				reactProbability = 0.015f;
			}
			else if(set==3)
			{
				baseAddedMap[20] = 14;
				baseAddedMap[19] = 13.98f;
				baseAddedMap[18] = 13.96f;
				baseAddedMap[17] = 13.94f;
				baseAddedMap[16] = 13.90f;
				baseAddedMap[15] = 13.8f;
				baseAddedMap[14] = 13.6f;
				baseAddedMap[13] = 13.4f;
				baseAddedMap[12] = 13.1f;
				baseAddedMap[11] = 12.8f;
				baseAddedMap[10] = 12.4f;
				baseAddedMap[9] = 11.8f;
				baseAddedMap[8] = 10.8f;
				baseAddedMap[7] = 9f;
				baseAddedMap[6] = 7f;
				baseAddedMap[5] = 5.6f;
				baseAddedMap[4] = 5.0f;
				baseAddedMap[3] = 4.65f;
				baseAddedMap[2] = 4.45f;
				baseAddedMap[1] = 4.30f;
				baseAddedMap[0] = 4.2f;
				
				reactProbability = 0.025f;
			}
			break;

		}
		//updateMoleculeCon();
	
	}
	
	private void resetConcentrationMap(Simulation simulation)
	{
		conMap.clear();
		reachEquilibrium = false;
		outputUpdated = false;
		int sim = simulation.getSimNum();
		int set = simulation.getSetNum();
		
		switch(sim)
		{
		case 4:
			if(set==1)
			{
				Double [] nitricAcid = {0.0,0.05,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
//				Double [] water = new Double [25];
//				for(int i= 0;i<25;i++)
//					water[i] = 55.35;
				Double [] nitrate = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.95,1.0};
				Double [] hydronium = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.95,1.0};
				conMap.put("Nitric-Acid", nitricAcid);
				//conMap.put("Water",water);
				conMap.put("Nitrate",nitrate);
				conMap.put("Hydronium", hydronium);
			}
			else if(set==2)
			{
				Double [] lithiumHydroxide = {0.0,0.1,0.17,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] lithiumIon = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.83,0.9,1.0};
				Double [] hydroxide = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.83,0.9,1.0};
				
				conMap.put("Lithium-Hydroxide", lithiumHydroxide);
				conMap.put("Lithium-Ion", lithiumIon);
				conMap.put("Hydroxide", hydroxide);				
			}
			else if(set==3)
			{
				Double [] methylamine = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.972,1.0};
				Double [] methylammonium = {0.0,0.028,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] hydroxide = {0.0,0.028,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				conMap.put("Methylamine",methylamine);
				conMap.put("Methylammonium",methylammonium);
				conMap.put("Hydroxide",hydroxide);
			}
			else if (set==4)
			{
				Double [] aceticAcid = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.994,1.0};
				Double [] acetate = {0.0,0.006,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] hydronium = {0.0,0.006,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				conMap.put("Acetic-Acid",aceticAcid);
				conMap.put("Acetate",acetate);
				conMap.put("Hydronium",hydronium);
			}
			break;
		case 5:
			if(set==1)
			{
				Double [] ammonia = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] ammoniumChloride = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5};
				Double [] hydroxide = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] hydronium = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
				Double [] hydrochloricAcid = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5};
				
				conMap.put("Ammonia", ammonia);
				conMap.put("Ammonium-Chloride", ammoniumChloride);
				conMap.put("Hydroxide", hydroxide);	
				conMap.put("Hydronium", hydronium);	
				conMap.put("Hydrochloric-Acid", hydrochloricAcid);
			}
			break;
		case 6:
			break;
		}
	}
	
	//Called when user hit "PLAY"
	public void play()
	{
		if(p5Canvas.getSim()==2)
		{
			//disable view selection button
			Main main = p5Canvas.getMain();
			main.btnBronsted.setEnabled(false);
			main.btnLewis.setEnabled(false);
		}
	}
	
	private void setupSpeed() {
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float speed = 1.0f;		
		
		switch(sim)
		{
		default:
			speed = 1;
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		}
		getSimulation(sim, set).setSpeed(speed);
	}
	
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		JPanel dashboard = main.dashboard;

		
		
		switch(sim)
		{
		case 1:

			break;
		case 2:
			break;
		case 3:
			if(set==1)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("25");
			}
			else if(set==2)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("25");
			}
			else if(set==3)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("10");
				lblWaterNumberValue.setText("25");
			}
			else if(set==4)
			{
				lblHNumberValue.setText("0");
				lblOHNumberValue.setText("0");
				lblWaterNumberValue.setText("25");
			}
			dashboard.add(this.lblHNumberText,"cell 0 1,align right");
			dashboard.add(this.lblHNumberValue,"cell 1 1,align left");
			dashboard.add(this.lblOHNumberText,"cell 0 2, align right");
			dashboard.add(this.lblOHNumberValue,"cell 1 2, align left");
			dashboard.add(this.lblWaterNumberText,"cell 0 3,align right");
			dashboard.add(this.lblWaterNumberValue,"cell 1 3,align left");
			break;
		case 4:
			lblTempValue.setText( (p5Canvas.temp+celsiusToK)+" K");
			if(set==4)
			{
				lblHConValue.setText("0 M");
				lblOHConValue.setText("0 M");
				lblKeqValue.setText("0");
				lblCompoundConText.setText("<html>[CH<sub>3</sub>COOH]: </html>");
				lblCompoundConValue.setText("1 M");
				lblProductConText.setText("<html>[CH<sub>3</sub>COO<sup>-</sup>]: </html>");
				lblProductConValue.setText("0 M");
			}
			else if(set==2)
			{
				lblCompoundConText.setText("<html>[LiOH]: </html>");
				lblCompoundConValue.setText("1 M");
				lblHConValue.setText("0 M");
				lblOHConValue.setText("0 M");
				lblKeqValue.setText("0");
				lblProductConText.setText("<html>[Li<sup>-</sup>]: </html>");
				lblProductConValue.setText("0 M");
			}
			else if(set==3)
			{
				lblCompoundConText.setText("<html>[CH<sub>3</sub>NH<sub>2</sub>]: </html>");
				lblCompoundConValue.setText("1 M");
				lblHConValue.setText("0 M");
				lblOHConValue.setText("0 M");
				lblKeqValue.setText("0");
				lblProductConText.setText("<html>[CH<sub>3</sub>NH<sub>3</sub><sup>+</sup>]: </html>");
				lblProductConValue.setText("0 M");
			}
			else if(set==1)
			{
				lblCompoundConText.setText("<html>[HNO<sub>3</sub>]: </html>");
				lblCompoundConValue.setText("1 M");
				lblHConValue.setText("0 M");
				lblOHConValue.setText("0 M");
				lblKeqValue.setText("0");
				lblProductConText.setText("<html>[NO<sub>3</sub><sup>-</sup>]: </html>");
				lblProductConValue.setText("0 M");
			}
			dashboard.add(this.lblCompoundConText,"cell 0 1, align right");
			dashboard.add(this.lblCompoundConValue,"cell 1 1, align left");
			dashboard.add(this.lblHConText,"cell 0 2,align right");
			dashboard.add(this.lblHConValue,"cell 1 2,align left");
			dashboard.add(this.lblOHConText,"cell 0 3,align right");
			dashboard.add(this.lblOHConValue,"cell 1 3,align left");
			dashboard.add(this.lblProductConText,"cell 0 4,align right");
			dashboard.add(this.lblProductConValue,"cell 1 4,align left");
			dashboard.add(this.lblKeqText,"cell 0 5,align right");
			dashboard.add(this.lblKeqValue,"cell 1 5, align left");
			dashboard.add(this.lblTempText,"cell 0 6, align right");
			dashboard.add(this.lblTempValue,"cell 1 6, align left");
			break;
//		case 5:
//			break;
		case 6:
			main.btnGraphSwitch.setEnabled(true);
			break;
		case 5:
			if(set==1)
			{
				lblPHValue.setText("7");
				lblMolesCompound1Text.setText("<html>[NH<sub>3</sub>]: </html>");
				lblMolesCompound1Value.setText("0.8 M");
				lblMolesCompound2Text.setText("<html>[NH<sub>4</sub>Cl]: </html>");
				lblMolesCompound2Value.setText("0.5 M");
				lblMolesCompound3Text.setText("[HCl]: ");
				lblMolesCompound3Value.setText("0 M");
				lblMolesWaterValue.setText("0 M");
				lblHConValue.setText("0 M");
				lblOHConValue.setText("0 M");
			}
			main.btnGraphSwitch.setEnabled(true);
			dashboard.add(this.lblPHText,"cell 0 1, align right");
			dashboard.add(this.lblPHValue,"cell 1 1, align left");
			dashboard.add(this.lblMolesCompound1Text,"cell 0 2, align right");
			dashboard.add(this.lblMolesCompound1Value,"cell 1 2, align left");
			dashboard.add(this.lblMolesCompound2Text,"cell 0 3, align right");
			dashboard.add(this.lblMolesCompound2Value,"cell 1 3, align left");
			dashboard.add(this.lblMolesCompound3Text,"cell 0 4, align right");
			dashboard.add(this.lblMolesCompound3Value,"cell 1 4, align left");
			dashboard.add(this.lblHConText,"cell 0 5,align right");
			dashboard.add(this.lblHConValue,"cell 1 5, align left");
			dashboard.add(this.lblOHConText,"cell 0 6,align right");
			dashboard.add(this.lblOHConValue,"cell 1 6, align left");
			break;
		
		}
		
	}
	

	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		Main main = p5Canvas.getMain();
				
		//Set volume
		p5Canvas.setVolume(63);
		
		//Customization
		main.heatSlider.setEnabled(false);
		main.volumeSlider.setEnabled(false);
		switch(p5Canvas.getSim())
		{
		case 2:
//			main.volumeLabel.setText(p5Canvas.currentVolume+" L");
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			main.currentZoom = 200;
			main.zoomSlider.setEnabled(false);

			break;
		case 6:
			main.getCanvas().setRangeYAxis(1, 0, 14);

			break;
		}
		resetRightPanel(sim,set);

		

	}

	private void resetRightPanel(int sim, int set) {
		Main main = p5Canvas.getMain();

		switch(sim)
		{
		case 2:
			main.rightPanel.removeAll();
			main.rightPanel.add(main.lblOutput, "cell 0 1");
			main.rightPanel.add(main.dashboard, "cell 0 2,growy");
			break;

		}
		main.rightPanel.repaint();

	}
	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateOutput(int, int)
	 */
	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.###");
		String output = null;
		
		switch(sim)
		{
		case 1:
			break;
		case 2:
			break;
		case 3:
				output = myFormatter.format(Compound.getMoleculeNum("Hydronium"));
				lblHNumberValue.setText(output);

				output = myFormatter.format(Compound.getMoleculeNum("Hydroxide"));
				lblOHNumberValue.setText(output);
	
				output = myFormatter.format(Compound.getMoleculeNum("Water"));
				lblWaterNumberValue.setText(output);
			
			break;
		case 4:	
			String compoundName = null;
			String productName = null;
			if(set==1)
			{
				compoundName = "Nitric-Acid";
				productName = "Nitrate";
			}
			else if(set==2)
			{
				compoundName = "Lithium-Hydroxide";
				productName = "Lithium-Ion";
			}
			else if(set==3)
			{
				compoundName = "Methylamine";
				productName = "Methylammonium";
			}
			else if(set ==4)
			{
				compoundName = "Acetic-Acid";
				productName = "Acetate";
			}
			output = myFormatter.format(getConByName(compoundName));
			lblCompoundConValue.setText(output + " M");
			output = myFormatter.format(getConByName(productName));
			lblProductConValue.setText(output + " M");
			
			output = myFormatter.format(getConByName("Hydronium"));
			lblHConValue.setText(output + " M");
			output = myFormatter.format(getConByName("Hydroxide"));
			lblOHConValue.setText(output + " M");
			//Update keq value label
			if(reachEquilibrium)
			{
				if(keq!=0)
				{
					if(keq<0.01)
					 myFormatter = new DecimalFormat("###.########");

					output = myFormatter.format(keq);
					lblKeqValue.setText(output);
				}
				else
					lblKeqValue.setText("Infinity");
			}
			else
				lblKeqValue.setText("0");
			break;
		case 5:
			if(set==1)
			{
				if(!outputUpdated)
				{
				output = myFormatter.format(getConByName("Ammonia"));
				lblMolesCompound1Value.setText(output + " M");
				output = myFormatter.format(getConByName("Ammonium-Chloride"));
				lblMolesCompound2Value.setText(output + " M");
				output = myFormatter.format(getConByName("Hydrochloric-Acid"));
				lblMolesCompound3Value.setText(output + " M");
//				output = myFormatter.format(getConByName("Hydronium"));
//				lblHConValue.setText(output + " M");
//				output = myFormatter.format(getConByName("Hydroxide"));
//				lblOHConValue.setText(output + " M");
				
				
				
					if(reachEquilibrium)
					{						
						lblPHValue.setText(bufferConTable[numHclAdded][0]);
						lblMolesCompound1Value.setText(bufferConTable[numHclAdded][1]);
						lblMolesCompound2Value.setText(bufferConTable[numHclAdded][2]);		
						lblMolesCompound3Value.setText(bufferConTable[numHclAdded][3]);
						lblHConValue.setText(bufferConTable[numHclAdded][4]);
						lblOHConValue.setText(bufferConTable[numHclAdded][5]);
						outputUpdated = true;
					}
				}
			}
			break;
		case 6:
			break;
		
		}







	}
	

	
//	//Update Concentration value for all compounds
//	private void updateMoleculeCon() {
//		
//		int sim = p5Canvas.getSim();
//		int set = p5Canvas.getSet();
//		
//		float mole = 0;
//		float con = 0;
//		float volume = (float) ((float)p5Canvas.currentVolume/2) / 1000;
//		//Clean old data
//		Iterator<String> it = moleculeConHash.keySet().iterator();
//		while(it.hasNext())
//		{
//			String name = (String) it.next();
//			moleculeConHash.put(name, 0.004f);
//		}
//		
//		if(sim==4&&set==1)
//		{
//		}
//		else
//		{
//		
//			for (String name: State.getCompoundNames()) {
//				//Special cases
//				if(name.equals("Water"))
//				{
//					con =55.35f;
//				}
//				else  //General case
//				{
//					mole = (float) State.getMoleculeNumByName(name) / numMoleculePerMole;
//					con = mole / volume;
//				}
//				moleculeConHash.put(name, con);
//			}
//		}
//		
//	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {

		clearAllMoleculeForce();

		switch (sim) {
		case 1:
			if(set==1)
				computeForceLiftChloride();
			else if(set==2)
				computeForceSim1Set2();
			else if(set ==3)
			{
				computeForceSim1Set3();
				computeForceLiftChloride();
			}
			computeForceTopBoundary();
			break;
		case 2:
			break;
		case 3:
			if(set==1)
			{
				computeForceLiftChloride();
				computeForceSim3Set1();
	
			}
			else if(set==2)
				computeForceSim3Set2();
			else if(set==3)
				computeForceSim1Set2();
			else if(set==4)
				;
			computeForceTopBoundary();

			break;
		case 4:
			computeForceSim4Set1();
			computeForceTopBoundary();
			break;
		case 5:
			computeForceSim5Set1();
			computeForceTopBoundary();
			break;
		case 6:
			
			if(set==1)
				computeForceSim6Set1();	
			else if(set==2)
				computeForceSim6Set2();
			else if(set==3)
				computeForceSim6Set3();
			computeForceTopBoundary();
			break;

		}
	}
	
	private void computeForceLiftChloride()
	{
		float gravityCompensation = 0.05f;

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Hydroxide to Hydrogen-Ion
				else if(mole.getName().equals("Hydroxide"))
				{
					
				}
		}		
		
	}
	
	// Foce computation for sim 1 set 2
	private void computeForceSim1Set2() {
		Molecule mole = null;
		String moleName = null;
		float repulsiveForceX = 1.0f;
		float repulsiveForceY = 0.5f;
		
		float forceYCompensation = 0.01f;


		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float disSquare = 0;
		float forceX = 0;
		float forceY = 0;
		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);

		for (int i = 0; i < State.getMoleculeNum(); i++) {
			mole = State.molecules.get(i);
			moleName = new String(mole.getName());

			if (moleName.equals("Sodium-Ion")) 
				//Force compute for NaOH, to separate Sodium-Ion from the solid
			{
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < State.getMoleculeNum(); k++) {
						//Check all other molecules
						if (k == i)
							continue;
						Molecule m = State.molecules.get(k);
						String mName = m.getName();
						if (mName.equals("Sodium-Hydroxide")||mName.equals("Sodium-Ion")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
							{
								otherLoc.set(m.getElementLocation(otherE));
								if (thisLoc == null || otherLoc == null)
									continue;
								xValue = thisLoc.x - otherLoc.x;
								yValue = thisLoc.y - otherLoc.y;
								disSquare = xValue * xValue + yValue* yValue;
								dis = (float) Math.sqrt(disSquare);
								xValue/=dis;
								yValue/=dis;
								forceX = (float) (xValue * (repulsiveForceX/disSquare));
								forceY = (float) (yValue * (repulsiveForceY/disSquare));
								if(forceY<0)
									forceY*=0;
								mole.sumForceX[thisE] += forceX;
								//mole.sumForceY[thisE] += forceY;
//							
								mole.sumForceY[thisE]+=forceYCompensation; 
							}

						}
					}

				}

			}

		}
	}
	

	// Foce computation for sim 1 set 2
	private void computeForceSim1Set3() {
		
		float attractiveForce = 0.5f;
		
		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float disSquare = 0;
		float forceX = 0;
		float forceY = 0;
		Vec2 posThis = new Vec2(0, 0);
		Vec2 posOther = new Vec2(0, 0);
		float maxNumber = 5f;  //Number to scale the force when number of molecule is small
		int numHydronium = State.getMoleculeNumByName("Hydronium");
		int numHydroxide = State.getMoleculeNumByName("Hydroxide");
		
		if(numHydronium<=5 && numHydroxide <=5)
		{
			for(Molecule moleHydroxide: State.getMoleculesByName("Hydroxide"))
			{
				posThis.set(moleHydroxide.getPosition());

					for(Molecule moleHydronium: State.getMoleculesByName("Hydronium"))
					{
						posOther.set(moleHydronium.getPosition());
						xValue = posOther.x - posThis.x;
						yValue = posOther.y - posThis.y;
						disSquare = xValue * xValue + yValue* yValue;
						dis = (float) Math.sqrt(disSquare);
//						xValue/=dis;
//						yValue/=dis;
						forceX = (float) (xValue/disSquare * attractiveForce * maxNumber/numHydroxide );
						forceY = (float) (yValue/disSquare * attractiveForce * maxNumber/numHydroxide );
						for(int e = 0 ;e<moleHydroxide.getNumElement();++e)
						{
							moleHydroxide.sumForceX[e]+=forceX;
							moleHydroxide.sumForceY[e]+=forceY;

						}
						for(int e2 = 0 ; e2<moleHydronium.getNumElement();++e2)
						{
							moleHydronium.sumForceX[e2]+= -forceX;
							moleHydronium.sumForceY[e2]+= -forceY;
						}
					}
				
			}
		}
	}
	private void computeForceSim3Set1()
	{
		float gravityCompensation = 0.035f;

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add gravity to avoid them floating
				if(mole.getName().equals("Hydrogen-Chloride")) //Make sodium-Ion
				{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
							mole.sumForceY[thisE] -= gravityCompensation*3;
						}
				}
				
				//Add gravity to avoid them floating
				else if(mole.getName().equals("Hydrogen-Ion")) //Make sodium-Ion
				{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
							mole.sumForceY[thisE] -= gravityCompensation;
						}
				}
		}		
		
	}
	
	private void computeForceSim3Set2()
	{
		float gravityCompensation = 0.05f;

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Hydrogen-Fluoride")||mole.getName().equals("Fluoride")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
		}		
		
	}
	
	private void computeForceSim4Set1()
	{
		float gravityCompensation = 0.075f;

		for(Molecule mole: State.getMolecules())
		{
			//Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Nitrate")) 
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						mole.sumForceY[thisE] += gravityCompensation;

						
						}
				}
					
		}		
		
	}
	
	private void computeForceSim6Set1()
	{
		float gravityCompensation = 0.05f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.085f; // How strong the attract force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
						if(mole.getName().equals("Hydrogen-Fluoride"))
						mole.sumForceY[thisE] += gravityCompensation;
						else
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Hydroxide to Hydronium
				else if(mole.getName().equals("Hydroxide"))
				{
					
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydronium");
							for(Molecule moleOther:hydrogenIon)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX;
								mole.sumForceY[thisE] += forceY;
								
								moleOther.sumForceX[0] -= forceX*1.5;
								moleOther.sumForceY[0] -= forceY*1.5;
							}
							
						}
					
				}
		}		
		
	}
	
	private void computeForceSim6Set2()
	{
		float gravityCompensation = 0.05f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.085f; // How strong the attract force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] += gravityCompensation*3;
						}
				}
				//Add anti-gravity to make them floating
				if(mole.getName().equals("Ammonium")) //Make sodium-Ion
				{
					//Add Gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] -= gravityCompensation*0.5f;
						}
				}
				//Attract Hydroxide to Hydronium
				else if(mole.getName().equals("Hydrogen_Ion"))
				{
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> water = State.getMoleculesByName("Water");
							for(Molecule moleOther:water)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX;
								mole.sumForceY[thisE] += forceY;
							}
							
						}
					
				}
				else if(mole.getName().equals("Hydronium"))
				{
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> water = State.getMoleculesByName("Ammonia");
							for(Molecule moleOther:water)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX;
								mole.sumForceY[thisE] += forceY;
								
								moleOther.sumForceX[thisE] -= forceX;
								moleOther.sumForceY[thisE] -= forceY;
							}
							
						}
					
				}
				
		}		
		
	}
	
	private void computeForceSim6Set3()
	{
		float gravityCompensation = 0.05f;
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.065f; // How strong the attract force is
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
			if(mole.getName().equals("Hydrogen-Fluoride")) //Make sodium-Ion
			{
				//Anti-gravity force
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
					mole.sumForceX[thisE] += 0;
						mole.sumForceY[thisE] += gravityCompensation*2;

					
					}
			}
				//Attract Hydroxide to Hydronium
				else if(mole.getName().equals("Hydronium"))
				{
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> water = State.getMoleculesByName("Hydroxide");
							for(Molecule moleOther:water)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX;
								mole.sumForceY[thisE] += forceY;
							}
							
						}
					
				}
				
		}		
		
	}
	
	private void computeForceSim5Set1()
	{
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

		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

				//Add anti-gravity to make them floating
				if(mole.getName().equals("Chlorine-Ion")) //Make sodium-Ion
				{
					//Anti-gravity force
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // element
						mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] += gravityCompensation*3;

						
						}
				}
				//Attract Ammonia to Hydrogen-Ion
				else if(mole.getName().equals("Ammonia"))
				{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
							locThis.set(mole.getElementLocation(thisE));
							mole.sumForceX[thisE] = 0;
							mole.sumForceY[thisE] = 0;
						ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydrogen-Ion");
						ArrayList<Molecule> hydronium = State.getMoleculesByName("Hydronium");
						hydrogenIon.addAll(hydronium);
						for(Molecule moleOther:hydrogenIon)
						{
							locOther.set(moleOther.getPosition());
							xValue = locOther.x - locThis.x;
							yValue = locOther.y - locThis.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY;
							for( int otherE = 0; otherE<moleOther.getNumElement();otherE++)
							{
								moleOther.sumForceX[otherE] -= forceX/10;
								moleOther.sumForceY[otherE] -= forceY/10;
							}
						}
						
					}
				}
				//Attract Hydroxide to Hydrogen-Ion
				else if(mole.getName().equals("Water"))
				{
					if(State.getMoleculeNumByName("Ammonia")==0)
					{
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) {
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
							ArrayList<Molecule> hydrogenIon = State.getMoleculesByName("Hydrogen-Ion");
							for(Molecule moleOther:hydrogenIon)
							{
								locOther.set(moleOther.getPosition());
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale+gravityCompensation*0.2f;
	
								mole.sumForceX[thisE] += forceX;
								mole.sumForceY[thisE] += forceY;
								
								moleOther.sumForceX[0] -= forceX/10;
								moleOther.sumForceY[0] -= forceY/10;
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

				if(!mole.getName().equals("Hydrogen-Chloride"))
				{
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																						// element
							mole.sumForceX[thisE] += 0;
							mole.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
			
						}
					}
				}
			}
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */
	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;
		Molecule mole = null;

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Simulation simulation = getSimulation(sim, set);

		if(simulation.getSpawnStyle(compoundName) ==SpawnStyle.Precipitation)
		{
			res = this.addPrecipitation(isAppEnable, compoundName, count, simulation, 0);
			if (res) {
				// Connect new created molecule to table index
				int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
				int lastIndex = State.molecules.size() - 1;

				for (int i = 0; i < count; i++) {
					mole = State.molecules.get(lastIndex - i);
		
					//Set up table view index
					mole.setTableIndex(tIndex);
					//Set up speed
					mole.setRatioKE(1 / simulation.getSpeed());
					mole.setState(mState.Solid);
					//Set up boiling point and freezing point
//					State.molecules.get(lastIndex - i).setBoillingPoint(100);
//					State.molecules.get(lastIndex - i).setFreezingPoint(0);
				}

			}
		}
		else //If compound is solvent or gas
		{
			String [] ionName = getIonsByName(compoundName);
			int len = ionName.length;
			for(int i = 0;i<len;i++)
			{
				SpawnStyle spawnStyle = simulation.getSpawnStyle(ionName[i]);
				if (spawnStyle == SpawnStyle.Solvent) {
					res = this.addSolvent(isAppEnable, ionName[i], count, simulation);
				}
				else if (spawnStyle == SpawnStyle.Gas)
				{
					if(sim==2)
						res = this.addStaticMolecule(isAppEnable, ionName[i], count);
					else
					{
						res = this.addGasMolecule(isAppEnable, compoundName, count);
						
					}
				}
			}
			if (res) {
				// Connect new created molecule to table index
				int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
				int lastIndex = State.molecules.size() - 1;
				if(sim==6&&set==3 && compoundName.equals("Sodium-Hydroxide"))
				{
					int index = Compound.names.indexOf("Sodium-Ion");
					Compound.counts.set(index, Compound.counts.get(index)+count);
					index = Compound.names.indexOf("Hydroxide");
					Compound.counts.set(index, Compound.counts.get(index)+count);
				}

				for (int i = 0; i < len*count; i++) {
					//Set up table view index
					if(!(sim==6&&set==3))
					State.molecules.get(lastIndex - i).setTableIndex(tIndex);
					else
					{
						Molecule ion = State.molecules.get(lastIndex - i);
						String name = ion.getName();
						name.replace('-', ' ');
						int tableIndex = p5Canvas.getTableView().getIndexByName(name);
						ion.setTableIndex(tableIndex);
					}
					
					//Set up speed
					State.molecules.get(lastIndex - i).setRatioKE(
							1 / simulation.getSpeed());
					if(!(sim==3&set==1))
					{
					//Set up boiling point and freezing point
					State.molecules.get(lastIndex - i).setBoillingPoint(100);
					State.molecules.get(lastIndex - i).setFreezingPoint(0);
					}
				}
				if(compoundName.equals("Hydrochloric-Acid"))
				{
					numHclAdded +=count;
					outputUpdated = false;
					reachEquilibrium = false;
					if(numHclAdded ==5)
						reactProbability = 0.1f;
					
					//System.out.println("Added "+count);
				}
				if(sim==6)
				{
					if(set==1)
					{
						if(compoundName.equals("Sodium-Hydroxide"))
							baseAdded+=count;
					}
					if(set==2)
					{
						if(compoundName.equals("Ammonia"))
							baseAdded+=count;
					}
					else if(set==3)
					{
						if(compoundName.equals("Sodium-Hydroxide"))
							baseAdded+=count;
					}
				}

			}
		}
		

		return res;	
	}
	
	/******************************************************************
	 * FUNCTION : addSolvent DESCRIPTION : Function to add solvent molecules to
	 * PApplet Usually they are water
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	protected boolean addSolvent(boolean isAppEnable, String compoundName,
			int count, Simulation simulation) {
		boolean res = true;

		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule

		// molecules arraylist
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;

		Random randX = new Random();
		Random randY = new Random();

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		float spacing = moleWidth / 2;

		float solventTop = p5Canvas.h / 2;
		float solventHeight = p5Canvas.h / 4;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w - 2 * moleWidth);
				y_ = solventTop + randY.nextFloat()
						* (solventHeight - moleHeight);
				molePos.set(x_, y_);
				topLeft.set(x_ - spacing, y_ - spacing);
				botRight.set(x_ + spacing, y_ + spacing);
				for (int m = 0; m < molecules.size(); m++) {

					if (!((String) molecules.get(m).getName()).equals("Water")) {
						molePos.set(molecules.get(m).getPosition());
						molePosInPix.set(box2d.coordWorldToPixels(molePos));

						if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
							isClear = false;
						}
					}
				}

			}
			if (isClear) // We are able to add new molecule to current area if
							// it is clear
			{
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0));
				// If the solvent is water, we set it as inactive
				if (compoundName.equals("Water")) {
					Molecule mole = molecules.get(molecules.size() - 1);
					float scale = 0.3f;
//					mole.setReactive(false);
					if(simulation.isSimSelected(unitNum, 1, 2)|| simulation.isSimSelected(unitNum, 3, 1)|| simulation.isSimSelected(unitNum, 3, 3)||simulation.isSimSelected(unitNum, 4, 2))
						mole.setReactive(false);
					mole.setLinearVelocity(new Vec2(0, 0));
					// Make water lighter;
					mole.body.m_mass = (float) (mole.body.getMass() * scale);
				}
			
			}

		}

		return res;
	}
	
	public boolean addGasMolecule(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;

		Random randX = new Random();
		Random randY = new Random();

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		float spacing = moleWidth;
		float maxVelocity = 200;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w - 2 * moleWidth);
				y_ = moleHeight + randY.nextFloat()
						* (p5Canvas.h/2 - 2 * moleHeight);
				molePos.set(x_, y_);
				topLeft.set(x_ - spacing, y_ - spacing);
				botRight.set(x_ + spacing, y_ + spacing);
				for (int m = 0; m < molecules.size(); m++) {

					if (!((String) molecules.get(m).getName()).equals("Water")) {
						molePos.set(molecules.get(m).getPosition());
						molePosInPix.set(box2d.coordWorldToPixels(molePos));

						if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
							isClear = false;
						}
					}
				}

			}
			if (isClear) // We are able to add new molecule to current area if
							// it is clear
			{
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0));
				Random rand = new Random(System.nanoTime());
				float velocityX = (rand.nextFloat() - 0.5f) * maxVelocity;

				float velocityY = (rand.nextFloat() - 0.5f) * maxVelocity;
				State.molecules.get(State.molecules.size() - 1)
						.setLinearVelocity(new Vec2(velocityX, velocityY));
				State.molecules.get(State.molecules.size() - 1).setReactive(false);
			}

		}

		return res;
	}
	
	
	protected boolean addPrecipitation(boolean isAppEnable,
			String compoundName, int count, Simulation simulation, float angle) {
		boolean res = true;

		int numRow = (int) Math.ceil((float) count / 5); // number of row
		int numCol = (int) Math.ceil((float) count / numRow); // number of
																// column

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float increX = p5Canvas.w / 16;
		float offsetX = size.x / 2 + size.x / 6;
		float centerX = p5Canvas.x + offsetX; // X coordinate around which we
												// are going to add
		// Ions, 50 is border width
		float centerY = p5Canvas.y + p5Canvas.h - size.y * numRow
				- p5Canvas.boundaries.difVolume + size.y/2; // Y coordinate around
		// which we are going to
		// add Ions

		Vec2 topLeft = new Vec2(centerX - size.x / 2, centerY - size.y / 2);
		if (compoundName.equals("Ammonium-Chloride")
				|| compoundName.equals("Sodium-Carbonate"))
			topLeft = new Vec2(centerX - size.x, centerY - size.y);
		Vec2 botRight = new Vec2(centerX + numCol * (size.x), centerY + numRow
				* size.y);

		boolean isClear = false;

		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);

		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.
		while (!isClear) {

			// Reset flag
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
				topLeft = new Vec2(centerX - size.x / 2, centerY - size.y / 2);
				if (compoundName.equals("Ammonium-Chloride")
						|| compoundName.equals("Sodium-Carbonate"))
					topLeft = new Vec2(centerX, centerY);
				botRight = new Vec2(centerX + numCol * (size.x), centerY
						+ numRow * size.y);
				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w)) {
					isClear = true; // Ready to jump out
					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}
		if (res) // If there is enough space, add compounds
		{
			if (compoundName.equals("Sodium-Carbonate"))
				angle = 0;
			for (int i = 0; i < count; i++) {
				float x, y;

				int r = i % numRow;
				x = centerX + (i / numRow) * (size.x);

				y = centerY + (i % numRow) * size.y;

				molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
						angle));

				// Set precipitation inreactive
				// Precipitation will get dissolved first, and the ions
				// generated are reactive
				int index = molecules.size() - 1;
				Molecule m = molecules.get(index);
				m.setReactive(false);

				res = true;
			}
		}

		return res;

	}
	
	
	/******************************************************************
	 * FUNCTION : addGasMolecule DESCRIPTION : Function to add gas molecules to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addStaticMolecule(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;

		Random randX = new Random();
		Random randY = new Random();

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		float spacing = moleWidth;
		float maxVelocity = 40;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w/2 - 2 * moleWidth);
				y_ = moleHeight + randY.nextFloat()
						* (p5Canvas.h/2 - 2 * moleHeight);
				molePos.set(x_, y_);
				topLeft.set(x_ - spacing, y_ - spacing);
				botRight.set(x_ + spacing, y_ + spacing);
				for (int m = 0; m < State.molecules.size(); m++) {

					if (!((String) State.molecules.get(m).getName()).equals("Water")) {
						molePos.set(State.molecules.get(m).getPosition());
						molePosInPix.set(box2d.coordWorldToPixels(molePos));

						if (areaBodyCheck(molePosInPix, topLeft, botRight)) {
							isClear = false;
						}
					}
				}

			}
			if (isClear) // We are able to add new molecule to current area if
							// it is clear
			{
				String svgFileName = null;
				if(electronView==1)  //Lewis Law
				{
					svgFileName = new String(compoundName+"-Dots");
			
				}
				else  //Bronsted Law
				{
					svgFileName = new String(compoundName);
				}
				Molecule newMole = new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0,svgFileName);
				res = State.molecules.add(newMole);
				float velocityX = 0;
				float velocityY = 0;
				newMole.setLinearVelocity(new Vec2(velocityX, velocityY));
				newMole.setEnableAutoStateChange(false);
				newMole.setState(mState.Gas);

				if(electronView==1)  //Lewis Law
				{
					newMole.setImage(compoundName+"-Dots");
			
				}

			}

		}

		return res;
	}
	
	
	public void updateProperties(int sim,int set)
	{
	
//		if(sim==2)
//		{
//			for(Molecule mole: State.getMolecules())
//			{
//				mole.setGravityScale(0f);
//			}
//		}
	}
	
	
	//Translate compound in yaml config file into ions
	public String[] getIonsByName(String compound)
	{
		String [] res = new String [2];
		int index = 0;
		if(p5Canvas.getSim()==2)
			index = electronView;
		
		if(p5Canvas.getSim()!=2)
		{			
			if(compound.equals("Hydrochloric-Acid"))
			{
				res[0] = new String("Hydrogen-Ion");
				res[1] = new String ("Chlorine-Ion");
			}
			else if(compound.equals("Sodium-Hydroxide"))
			{
				res[0] = new String("Sodium-Ion");
				res[1] = new String("Hydroxide");
			}
			else if(compound.equals("Hydrogen-Bromide"))
			{
				res[0] = new String("Hydrogen-Ion");
				res[1] = new String("Bromine-Ion");
			}
//			else if(compound.equals("Lithium-Hydroxide"))
//			{
//				res[0] = new String("Lithium-Ion");
//				res[1] = new String("Hydroxide");
//			}
//			else if(compound.equals("Nitric-Acid"))
//			{
//				res[0] = new String("Nitrate");
//				res[1] = new String("Hydrogen-Ion");
//			}
			else if(compound.equals("Ammonium-Chloride"))
			{
				res[0] = new String("Ammonium");
				res[1] = new String("Chlorine-Ion");
			}
			else //Copy original compound to res
			{
				res = new String[1];
				if(compound.equals("Chloride"))
				{
					compound = new String("Chlorine-Ion");
				}
//				if(ionHash.containsKey(compound))
//				{
//					res[0] = new String(ionHash.get(compound)[index]);
//				}
				else
				{
				res[0] = new String(compound);
				}
			}
		}
		else   //For sim 2
		{
			res = new String[1];
			if(compound.equals("Chloride"))
			{
				compound = new String("Chlorine-Ion");
			}
			if(ionHash.containsKey(compound))
			{
				res[0] = new String(ionHash.get(compound)[index]);
			}
			else
			{
			res[0] = new String(compound);
			}
		}
		
			
		return res;
		
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#beginReaction(org.jbox2d.dynamics.contacts.Contact)
	 */
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
			if (m1.getReactive() || m2.getReactive()) 
			{

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
			
			// If inreactive molecules collide
			else if (!m1.getReactive() && !m2.getReactive()) {
				// If one of these two molecules is a water molecule
				// Handle dissolution
				if ((m1.getName().equals("Water") && !m2.getName().equals(
						"Water"))
						|| (!m1.getName().equals("Water") && m2.getName()
								.equals("Water"))) {

					ArrayList<String> collider = new ArrayList<String>();
					if (m1.getName().equals("Water")) {
						collider.add(m2.getName());
						p5Canvas.products = getDissolutionProducts(collider);
						if (p5Canvas.products.size() > 0) {
							p5Canvas.killingList.add(m2);
						}
					} else {
						collider.add(m1.getName());
						p5Canvas.products = getDissolutionProducts(collider);
						if (p5Canvas.products.size() > 0) {
							p5Canvas.killingList.add(m1);
						}
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
			
			if(set==1)
			{
				// Sim 1 set 1  HCl + H2O -> H3O+ + Cl-
				if (reactants.contains("Hydrogen-Ion") && reactants.contains("Water")) {
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				// Sim 1 set 2 NaOH + H2O -> Na+ + OH- + H2O
			}
			else if(set==3)
			{
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Water"))
				{
					products.add("Hydronium");
				}
				else if(reactants.contains("Hydronium")&&reactants.contains("Hydroxide"))
				{
					products.add("Water");
					products.add("Water");
				}
			}
	
			break;
		case 2:
			if(set==1)
			{
				
			}
			else if(set==2)
			{
				
			}
			else if(set==3)
			{
				
			}
			break;
		case 3:
			if(set==1)
			{
				// Sim 3 set 1  HCl + H2O -> H3O+ + Cl-
				if (reactants.contains("Hydrogen-Ion") && reactants.contains("Water")) {
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				if(reactants.contains("Hydrogen-Fluoride") && reactants.contains("Water"))
				{
					products.add("Hydronium");
					products.add("Fluoride");
				}
			}
			else if(set==3)
			{
				//Sim 3 set 3 NaOH+H20 ->Na+ + OH- +H2O
			}
			else if(set==4)
			{
				if(reactants.contains("Ammonia") && reactants.contains("Water"))
				{
					products.add("Ammonium");
					products.add("Hydroxide");
				}
			}
			break;
		case 4:
			if(set==4)
			{
				if(reactants.contains("Acetic-Acid") && reactants.contains("Water"))
				{
					products.add("Acetate");
					products.add("Hydronium");
				}
			}
			else if(set==2)
			{
				if(reactants.contains("Lithium-Hydroxide") && reactants.contains("Water"))
				{
					products.add("Lithium-Ion");
					products.add("Hydroxide");
				}
			}
			else if(set==3)
			{
				if(reactants.contains("Methylamine") && reactants.contains("Water"))
				{
					products.add("Methylammonium");
					products.add("Hydroxide");
				}
			}
			else if(set==1)
			{
				// Sim 4 set 1  HNO3 + H2O -> H3O+ + NO3-
				if(reactants.contains("Nitric-Acid") && reactants.contains("Water"))
				{
					products.add("Nitrate");
					products.add("Hydronium");
				}
			}
			break;


		case 5:
			if(set==1)
			{
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Ammonia"))
				{
					products.add("Ammonium");
				}
				else if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Water"))
				{
						products.add("Hydronium");
				}
				else if(reactants.contains("Hydronium")&&reactants.contains("Ammonia"))
				{
					products.add("Ammonium");
					products.add("Water");
				}
				else if(reactants.contains("Ammonia") && reactants.contains("Water"))
				{
					if(State.getMoleculeNumByName("Hydrogen-Ion")==0&&State.getMoleculeNumByName("Hydronium")==0)
					{
						products.add("Ammonium");
						products.add("Hydroxide");
					}
				}
			}
			break;
		case 6:
			if(set==1)
			{
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Water"))
				{
					products.add("Hydronium");
				}
				else if(reactants.contains("Hydronium")&&reactants.contains("Hydroxide"))
				{
					products.add("Water");
					products.add("Water");
				}
				
			}
			else if(set==2){
				
				if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Ammonia"))
				{
					products.add("Ammonium");
				}
				else if(reactants.contains("Hydrogen-Ion")&&reactants.contains("Water"))
				{
						products.add("Hydronium");
				}
				else if(reactants.contains("Hydronium")&&reactants.contains("Ammonia"))
				{
						products.add("Ammonium");
						products.add("Water");
				}
				else if(reactants.contains("Ammonia") && reactants.contains("Water"))
				{
					if(State.getMoleculeNumByName("Hydrogen-Ion")==0&&State.getMoleculeNumByName("Hydronium")==0)
					{
						products.add("Ammonium");
						products.add("Hydroxide");
					}
				}
				
				
			}
			else if(set==3)
			{
				if(reactants.contains("Hydrogen-Fluoride") && reactants.contains("Water"))
				{
					products.add("Hydronium");
					products.add("Fluoride");
				}
				else if(reactants.contains("Hydronium")&&reactants.contains("Hydroxide"))
				{
					products.add("Water");
				}
			}
			break;
		

		}
		return products;
	}
	
	/******************************************************************
	 * FUNCTION : getDissolutionProducts DESCRIPTION : Return elements of
	 * reactants
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getDissolutionProducts(ArrayList<String> collider) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 2 
		if (collider.contains("Sodium-Hydroxide")) {
			products.add("Sodium-Ion");
			products.add("Hydroxide");
		}
		else if(collider.contains("Lithium-Hydroxide")) //Sim 4 Set 2
		{
			products.add("Lithium-Ion");
			products.add("Hydroxide");
		}
		else if(collider.contains("Hydrogen-Chloride"))
		{
			products.add("Hydrogen-Ion");
			products.add("Chlorine-Ion");
		}
		return products;

	}
	
	public float getConByName(String s) {
			
		//look up conMap
		if(conMap.containsKey(s))
		{
			
			Double [] map = conMap.get(s);
			
			
			float volume = 1+ 0.2f*numHclAdded/5;
				
			//Handle special case
			if(s.equals("Ammonium-Chloride"))
			{
//				int numChloride = State.getMoleculeNumByName("Chlorine-Ion");
//				int numHydrogen = State.getMoleculeNumByName("Hydrogen-Ion");
//				int num = numChloride-numHydrogen ;
				int num = (int) p5Canvas.getTableView().getCountByName("Ammonium Chloride");
				return map[num].floatValue()/volume;
			}
			else if(s.equals("Hydrochloric-Acid"))
			{
				int num = (int) p5Canvas.getTableView().getCountByName("Hydrochloric Acid");
				return map[num].floatValue()/volume;
			}
			else  //general case
			{
				return map[State.getMoleculeNumByName(s)].floatValue()/volume;
			}
		}
		else
		{	
			return 0;
		}
	}
	
	public float getDataTickX(int sim,int set, int indexOfGraph)
	{
		if(indexOfGraph==1) //return pH based on volume added
		{
			float res = 0;
			

			res = baseUpdated*this.baseVolumeMultiplier;

			return res;
		}
		else
		{
			return super.getDataTickX(sim, set, indexOfGraph);
		}
	}
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		if(sim==6)
		{
			if(indexOfGraph==0)
			{
				if(set==1||set==2)
				return super.getDataTickY(sim, set, indexOfGraph, indexOfCompound);
				else if(set==3)
				{	
					if(Compound.names.get(indexOfCompound).equals("Sodium-Hydroxide"))
						return 0;
					else
						return (float)Compound.counts.get(indexOfCompound);
				}
			}
			else if(indexOfGraph==1)
			{
				float res = baseAddedMap[baseUpdated];
				++baseUpdated;
				if(baseUpdated>baseAdded)
					baseUpdated = baseAdded;
				return res;
			}
		}
		
		else
		{
			return super.getDataTickY(sim, set, indexOfGraph, indexOfCompound);
		}
		return 0;

	}
	
	//Function that return the specific data to Canvas
	public float getDataTableView(int sim,int set, int indexOfCompound)
	{
		if(sim==6)
		{

				if(set==1||set==2)
				return super.getDataTableView(sim, set, indexOfCompound);
				else if(set==3)
				{	
					if(Compound.names.get(indexOfCompound).equals("Sodium-Hydroxide"))
						return 0;
					else
						return super.getDataTableView(sim, set, indexOfCompound);
				}

		}
		else
		{
			return super.getDataTableView(sim, set, indexOfCompound);
		}
		
		return 0;

	}

	//Last step of reset
	protected void initializeSimulation(int sim, int set) {
		//this.updateMoleculeCon();
		
	}
	public void setElectronView(int v)
	{
		if(v==0 || v==1)
		{
			electronView =v;
		}
			
	}
	@Override
	public void updateMoleculeCountRelated(int sim, int set) {

		//updateMoleculeCon();
	}



}
