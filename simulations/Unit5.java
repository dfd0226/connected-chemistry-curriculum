/**
 * Unit5: Kinetics
 */
package simulations;

import static data.State.molecules;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import main.Main;
import main.TableView;
import net.miginfocom.swing.MigLayout;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.SimpleBar;

import data.DBinterface;
import data.State;

import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 * 
 */
public class Unit5 extends UnitBase {

	// Output labels
	private JLabel lblConText1; // Concentration output
	private JLabel lblConValue1;
	private JLabel lblConText2;
	private JLabel lblConValue2;
	private JLabel lblConText3;
	private JLabel lblConValue3;
	private JLabel lblConText4;
	private JLabel lblConValue4;

	private JLabel lblVolumeText;
	private JLabel lblVolumeValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	private JLabel lblPressureText;
	private JLabel lblPressureValue;

	// Lables above bars
	public JLabel lblMultiplicationText1;
	public JLabel lblMultiplicationText2;
	public JLabel lblMultiplicationText3;
	public JLabel lblEqualText;
	public JLabel lblRText;

	public JLabel lblBarLabelPressure;
	public JLabel lblBarLabelVolume;
	public JLabel lblBarLabelMol;
	public JLabel lblBarLabelTemp;

	private int numMoleculePerMole = 10;

	// Bars on right panel
	public SimpleBar barPressure;
	public SimpleBar barVolume;
	public SimpleBar barMol;
	public SimpleBar barTemp;

	//private HashMap<String, Integer> moleculeNumHash;
	private HashMap<String, Float> moleculeConHash;


	boolean catalystAdded = false;
	private boolean inhibitorAdded = false;
	float equalRatio = 0.4f; // Only 0.4 molecules in form of N2O4
	float breakProbability = 0.5f; // The chance that N2O4 will break apart
	int oldTime = 0;
	int curTime = 0;
	float keq = 0.1f;
	private float defaultKeq = 0.01f;
	private boolean equilReached = false;

	public Unit5(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 5;
		//moleculeNumHash = new HashMap<String, Integer>();
		moleculeConHash = new HashMap<String, Float>();
		setupSimulations();
		setupOutputLabels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#setupSimulations()
	 */
	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Methane", "Oxygen" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Hydrogen-Iodide" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 2, 1);
		String[] elements2 = { "Sodium-Bicarbonate", "Acetic-Acid" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Precipitation,
				SpawnStyle.Solvent };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 2, 2);
		String[] elements3 = { "Sodium-Bicarbonate", "Acetic-Acid" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.SolidSpecial,
				SpawnStyle.Solvent };
		simulations[3].setupElements(elements3, spawnStyles3);

		simulations[4] = new Simulation(unitNum, 3, 3);
		String[] elements4 = { "Nitryl-Chloride", "Nitric-Oxide" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
		simulations[4].setSpeed(12);

		simulations[5] = new Simulation(unitNum, 3, 4);
		String[] elements5 = { "Nitryl-Chloride", "Nitric-Oxide" };
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyles5);

		simulations[6] = new Simulation(unitNum, 3, 5);
		String[] elements6 = { "Nitryl-Chloride", "Nitric-Oxide" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);

		simulations[7] = new Simulation(unitNum, 3, 6);
		String[] elements7 = { "Nitryl-Chloride", "Nitric-Oxide" };
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);

		simulations[8] = new Simulation(unitNum, 3, 7);
		String[] elements8 = { "Nitryl-Chloride", "Nitric-Oxide" };
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[8].setupElements(elements8, spawnStyles8);

		simulations[9] = new Simulation(unitNum, 3, 1);
		String[] elements9 = { "Dinitrogen-Tetroxide","Catalyst" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum, 3, 2);
		String[] elements10 = { "Dinitrogen-Tetroxide","Inhibitor" };
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[10].setupElements(elements10, spawnStyles10);

		simulations[11] = new Simulation(unitNum, 4, 1);
		String[] elements11 = { "Ammonia" };
		SpawnStyle[] spawnStyles11 = { SpawnStyle.Gas };
		simulations[11].setupElements(elements11, spawnStyles11);

		simulations[12] = new Simulation(unitNum, 4, 2);
		String[] elements12 = { "Nitrogen-Dioxide" };
		SpawnStyle[] spawnStyles12 = { SpawnStyle.Gas };
		simulations[12].setupElements(elements12, spawnStyles12);

		simulations[13] = new Simulation(unitNum, 4, 3);
		String[] elements13 = { "Carbon-Monoxide", "Nitrogen-Dioxide" };
		SpawnStyle[] spawnStyles13 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[13].setupElements(elements13, spawnStyles13);

	}

	private void setupOutputLabels() {
		lblConText1 = new JLabel();
		lblConText2 = new JLabel();
		lblConText3 = new JLabel();
		lblConText4 = new JLabel();
		lblConText1 = new JLabel(" M");
		lblConText2 = new JLabel(" M");
		lblConText3 = new JLabel(" M");
		lblConText4 = new JLabel(" M");
		lblVolumeText = new JLabel("Volume:");
		lblVolumeValue = new JLabel(" mL");
		lblTempText = new JLabel("Temperature:");
		lblTempValue = new JLabel(" K");
		lblPressureText = new JLabel("Pressure:");
		lblPressureValue = new JLabel(" kPa");

		// Set up bar parameter;
		Main main = p5Canvas.getMain();
		lblMultiplicationText1 = new JLabel("*");
		lblMultiplicationText2 = new JLabel("*");
		lblMultiplicationText3 = new JLabel("*");
		lblRText = new JLabel("R");
		lblEqualText = new JLabel("=");
		lblBarLabelPressure = new JLabel("P (kPa)");
		lblBarLabelVolume = new JLabel("V (L)");
		lblBarLabelMol = new JLabel("n (mol)");
		lblBarLabelTemp = new JLabel("T (K)");
		barPressure = new SimpleBar(0, 5000, 30);
		barVolume = new SimpleBar(main.minVolume, main.maxVolume, 63);
		barMol = new SimpleBar(0, 50, 10);
		barTemp = new SimpleBar(0,550,298);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#setupReactionProducts(int, int)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		if(!p5Canvas.isEnabled())
			return;
		boolean reactionHappened = false; // Boolean flag indicating is
											// reactions have taken place

		Simulation simulation = getSimulation(sim, set);
		switch (sim) {
		case 1:
			if (set == 1)
				reactionHappened = reactSim1Set1(simulation);
			else if (set == 2)
				reactionHappened = reactSim1Set2(simulation);
			break;
		case 2:
			reactionHappened = reactSim2Set1(simulation);
			break;
		case 3:
				
			if(set==1)
				reactionHappened = reactSim3Set1(simulation);
			else if(set==2)
				reactionHappened = reactSim3Set2(simulation);
			else
				reactionHappened = reactSim3Set3to7(simulation);
			break;
		case 4:
			if (set == 1)
				reactionHappened = reactSim4Set1(simulation);
			else if (set == 2)
				reactionHappened = reactSim4Set2(simulation);
			else if (set == 3)
				reactionHappened = reactSim4Set3(simulation);
			break;
		}

	}

	// Reaction funciton for Sim 2 Set 1
	private boolean reactSim2Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

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
				Vec2 size = Molecule.getShapeSize("Acetate", p5Canvas);
				if (nameNew.equals("Carbon-Dioxide")) // Add x off to
														// Nitrogen-Dioxide
														// molecule
					newVec.x += size.x / 2;
				else if (nameNew.equals("Water"))
					newVec.x -= size.x / 2;
				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i / 2].body
						.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	// Update function for Sim 3 set 3 - set 7
	private boolean reactSim3Set3to7(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int indexNitrylChloride = -1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++) {
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if (mOld[i].getName().equals("Nitryl-Chloride"))
					indexNitrylChloride = i;
			}

			if(indexNitrylChloride<0)
				indexNitrylChloride = 0;
			Molecule mNew = null;
			String nameNew = null;
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				// Reacts at the postion of nitrylChloride
				Vec2 loc = mOld[indexNitrylChloride].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if (nameNew.equals("Nitrogen-Dioxide")) // Add x off to
														// Nitrogen-Dioxide
														// molecule
				{
					Vec2 size = Molecule.getShapeSize("Nitrogen-Dioxide",
							p5Canvas);
					newVec.x += size.x / 2;
				}
				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i].body.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	// Reaction function for Sim 4 Set 3
	private boolean reactSim4Set3(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
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
				if (nameNew.equals("Carbon-Dioxide")) {
					Vec2 size = Molecule.getShapeSize("Nitric-Oxide", p5Canvas);
					newVec.x += size.x;
				}

				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i].body.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	// Reaction function for Sim 4 Set 2
	private boolean reactSim4Set2(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++) {
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
			}

			Molecule mNew = null;
			String nameNew = null;
			boolean firstNO = true;
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				// Reacts at the postion of nitrylChloride
				Vec2 loc = mOld[0].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if (nameNew.equals("Nictric-Oxide")) {
					Vec2 size = Molecule.getShapeSize("Nitric-Oxide", p5Canvas);
					newVec.x += size.x;
					if (firstNO) {
						newVec.y += size.y;
						firstNO = false;
					} else
						newVec.y -= size.y / 2;
				}

				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i % 2].body
						.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	// Reaction function for Sim 4 Set 1
	private boolean reactSim4Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
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
				Vec2 size = Molecule.getShapeSize("Nitrogen", p5Canvas);
				newVec.x += size.x * (i / 2);
				newVec.y += size.y * (i % 2);

				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i % 2].body
						.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	// Reaction function for Sim 3 Set 1
	// 2 NO2 = NO4
	private boolean reactSim3Set1(Simulation simulation) {
		
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		float conN2O4 = getConByName("Dinitrogen-Tetroxide");
		float conNO2 = getConByName("Nitrogen-Dioxide");
		float currentRatio = conN2O4/(conNO2*conNO2);
		
		curTime = p5Canvas.getMain().time;

		float decomposeIntervel = 4;
		if(catalystAdded)
			decomposeIntervel = 2;
		equilReached = false;
//		System.out.println("current Ratio is " +currentRatio);

		if (!p5Canvas.killingList.isEmpty()) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0 && (currentRatio < keq)) {
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
					molecules.add(mNew);

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
				
				return true;
			}
		}
		if(curTime!=oldTime && currentRatio>keq)
		{
			oldTime = curTime;
			//If has not reached equilibrium yet
			if(!equilReached)
			{
				if(curTime%decomposeIntervel==0 )
				{
					equilReached = true;
					return breakApartN2O4(simulation);
				}
			}
			else //If reached equilibrium
			{
							 return breakApartN2O4(simulation);
//									
			}
		}
		

		return false;

	}
	
	// Reaction function for Sim 3 Set 2
	// 2 NO2 = NO4
	private boolean reactSim3Set2(Simulation simulation) {
		if(!p5Canvas.isSimStarted) //Reaction has not started yet
			return false;
		
		if(inhibitorAdded)
			return false;
		
		float conN2O4 = getConByName("Dinitrogen-Tetroxide");
		float conNO2 = getConByName("Nitrogen-Dioxide");
		float currentRatio = conN2O4/(conNO2*conNO2);
		
		curTime = p5Canvas.getMain().time;

		float decomposeIntervel = 4;

		equilReached = false;
		
//		System.out.println("current Ratio is " +currentRatio);
		
		if (!p5Canvas.killingList.isEmpty()) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0 && (currentRatio < keq)) {
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
					molecules.add(mNew);

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
				
				return true;
			}
		}
		if(curTime!=oldTime && currentRatio>keq)
		{
			oldTime = curTime;
			//If has not reached equilibrium yet
			if(!equilReached)
			{
				if(curTime%decomposeIntervel==0 )
				{
					equilReached = true;
					return breakApartN2O4(simulation);
				}
			}
			else //If reached equilibrium
			{
				
							 return breakApartN2O4(simulation);
//										
			}
		}
		

		return false;
	}
	
	//Break apart N2O4 in Sim 3 Set 6
	private boolean breakApartN2O4(Simulation simulation)
	{
		Molecule mole = null;
		int index =-1;
		for (int i = 0; i < State.molecules.size(); i++) {
			mole = State.molecules.get(i);
			if (mole.getName().equals("Dinitrogen-Tetroxide")) {
				Vec2 loc = mole.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				String nameNew = new String("Nitrogen-Dioxide");
				Vec2 size = Molecule.getShapeSize("Nitrogen-Dioxide",
				p5Canvas);
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
					molecules.add(mNew);
					if(k%2==0)
					mNew.body.setLinearVelocity(mole.body
							.getLinearVelocity());
					else
						mNew.body.setLinearVelocity(mole.body
								.getLinearVelocity().mul(-1));
				}
			
				mole.destroy();
				
				//Update molecule number
				index = Compound.names.indexOf("Dinitrogen-Tetroxide");
				Compound.counts.set(index, Compound.counts.get(index)-1);
				index = Compound.names.indexOf("Nitrogen-Dioxide");
				Compound.counts.set(index, Compound.counts.get(index)+2);
				oldTime = curTime;
				return true;
			}
		}
		return false;
	}


	/******************************************************************
	 * FUNCTION : reactGeneric DESCRIPTION : Function for Sim 1 Set 1 raction
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactSim1Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int methaneIndex = -1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++) {
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if (mOld[i].getName().equals("Methane"))
					methaneIndex = i;
			}
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			if(methaneIndex<0)
				methaneIndex = 0;
			Molecule mNew = null;
			String nameNew = null;
			boolean isFirstWater = true;
			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				// Reacts at the postion of Methane
				Vec2 loc = mOld[methaneIndex].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if (nameNew.equals("Water")) // Add x off to water molecule
				{
					Vec2 size = Molecule.getShapeSize("Carbon-Dioxide",
							p5Canvas);
					if (isFirstWater) {
						newVec.x += size.x;
						isFirstWater = false;
					} else
						newVec.x -= size.x;

				}
				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				// Setup speed
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);

				mNew.body.setLinearVelocity(mOld[i].body.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	/******************************************************************
	 * FUNCTION : reactGeneric DESCRIPTION : Function for Sim 1 Set 1 raction
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactSim1Set2(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			int hydrogenIodideIndex = -1;
			Molecule[] mOld = new Molecule[numToKill];
			for (int i = 0; i < numToKill; i++) {
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				if (mOld[i].getName().equals("Hydrogen-Iodide"))
					hydrogenIodideIndex = i;
			}
			// Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			String nameNew = null;
			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				// Reacts at the postion of Methane
				Vec2 loc = mOld[hydrogenIodideIndex].getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				nameNew = p5Canvas.products.get(i);
				if (nameNew.equals("Hydrogen")) // Add x off to water molecule
				{
					Vec2 size = Molecule.getShapeSize("Iodine", p5Canvas);
					newVec.x += size.y;
				}
				mNew = new Molecule(newVec.x, newVec.y, nameNew, box2d,
						p5Canvas, (float) (Math.PI / 2));
				// Set speed for new molecule
				mNew.setRatioKE(1 / simulation.getSpeed());
				molecules.add(mNew);
				mNew.body.setLinearVelocity(mOld[i].body.getLinearVelocity());
			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int set = p5Canvas.getSet();
			int sim = p5Canvas.getSim();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {
		
		if(p5Canvas.getSim()==1 || p5Canvas.getSim()==4) //No graph in Sim 1 and Sim 4
		{
			p5Canvas.getMain().getCanvas().setPaintLineEnable(false);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {
		setupSimulations();
		// Reset parameters
		barMol.reset();
		barPressure.reset();
		barVolume.reset();
		barTemp.reset();
		oldTime =0;
		curTime = 0;
		volumeMagnifier = 1000;
		//moleculeNumHash.clear();
		moleculeConHash.clear();
		catalystAdded = false;
		inhibitorAdded = false;
		equilReached = false;
		// Set up speed ratio for molecules
		setupSpeed();
		
		defaultKeq =0.01f;
		keq = defaultKeq;
		
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		switch (sim) {
		case 1:
			if (set == 1)
				p5Canvas.temp = 125;
			else if (set == 2)
				p5Canvas.temp = 190;
			break;
		case 2:
			break;
		case 3:
			if (set == 3) {
			} else if (set == 4) {
				p5Canvas.temp = 40;
			} else if (set == 5) {
				p5Canvas.temp = 60;
			} else if (set == 6) {
				p5Canvas.temp = 100;
			} else if (set == 7) {
			} 
			else if (set == 1) {
				keq =80.0f;
			}
			else if(set==2)
			{
				keq =80.0f;
			}
			break;
		case 4:
			break;
		}
		
	}
	
	public void customizeInterface(int sim, int set)
	{
		// Customization
		Main main = p5Canvas.getMain();
		
		resetRightPanel(sim,set);
		//Set volume
		p5Canvas.setVolume(60);
	
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
		


		switch (sim) {
		case 1:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			if (set == 1)
				p5Canvas.temp = 125;
			else if (set == 2)
				p5Canvas.temp = 190;
			break;
		case 2:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);

			//Set the range for Y axis
			main.getCanvas().setRangeYAxis(0, 0.05f);
			
			break;
		case 3:
			if (set == 3) {
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			} else if (set == 4) {
				p5Canvas.temp = 40;
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			} else if (set == 5) {
				p5Canvas.temp = 60;
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			} else if (set == 6) {
				p5Canvas.temp = 100;
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			} else if (set == 7) {
				main.heatSlider.setEnabled(false);
				p5Canvas.volumeMinBoundary = 20;
			} else if (set == 1) {
				HashMap moleculeSliderMap = p5Canvas.getMain().moleculeSliderMap;
				if (!moleculeSliderMap.isEmpty()) {
					JSlider slider = (JSlider) moleculeSliderMap
							.get("Catalyst");
					slider.setValue(5);
					slider.setEnabled(false);
				}
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			}
			else if (set == 2) {
				HashMap moleculeSliderMap = p5Canvas.getMain().moleculeSliderMap;
				if (!moleculeSliderMap.isEmpty()) {
					JSlider slider = (JSlider) moleculeSliderMap
							.get("Inhibitor");
					slider.setValue(5);
					slider.setEnabled(false);
				}
				main.heatSlider.setEnabled(false);
				main.volumeSlider.setEnabled(false);
			}
			main.volumeLabel.setText(main.defaultVolume+ " L");
			
			//Set the range for Y axis
			main.getCanvas().setRangeYAxis(0, 0.05f);

			break;
		case 4:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			break;
		}
	}
	
	private void resetRightPanel (int sim, int set) {
		Main main = p5Canvas.getMain();

		switch(sim)
		{
		case 1:
			if(set==1 || set==2){
				main.rightPanel.removeAll();
				main.rightPanel.add(main.lblOutput, "cell 0 1");
				main.rightPanel.add(main.dashboard, "cell 0 2,growy");
			}
		break;
			default:
				break;
		}

	}
	
	public void resetTableView(int sim,int set)
	{
		((TableView) p5Canvas.getTableView()).setColumnName(0, "Molarity");
		((TableView) p5Canvas.getTableView()).setColumnWidth(0, 30);
		((TableView) p5Canvas.getTableView()).setColumnWidth(1, 30);
		((TableView) p5Canvas.getTableView()).setColumnWidth(2, 100);
	}

	// Set up speed ratio for molecules
	public void setupSpeed() {

		int set = p5Canvas.getSet();
		int sim = p5Canvas.getSim();
		float speed = 1.0f;
		switch (sim) {
		case 1:
			if (set == 1) {
				speed = 4;
			} else if (set == 2) {
				speed = 8;
			}
			break;
		case 2:
			speed = 4;
			break;
		case 3:
			if (set==3||set==4||set==7) {
				speed = 6;
			}
			else if(set==5)
			{
				speed = 8;
			}
			else if (set==6)
			{
				speed = 10;
			}
			else {
				speed = 12;
				
			}
			break;
		case 4:
			if (set == 1) {
				speed = 2;
			} else if (set == 2) {
				speed = 4;
			} else {
				speed = 8;
			}
			break;
		}
		getSimulation(sim, set).setSpeed(speed);
	}

	public void resetDashboard(int sim, int set) {
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		JPanel dashboard = main.dashboard;

		lblConText1 = new JLabel();
		lblConText2 = new JLabel();
		lblConText3 = new JLabel();
		lblConText4 = new JLabel();
		lblConValue1 = new JLabel(" M");
		lblConValue2 = new JLabel(" M");
		lblConValue3 = new JLabel(" M");
		lblConValue4 = new JLabel(" M");
		lblVolumeText = new JLabel("Volume:");
		lblVolumeValue = new JLabel(" mL");
		lblTempText = new JLabel("Temperature:");
		lblTempValue = new JLabel(" \u2103");
		lblPressureText = new JLabel("Pressure:");
		lblPressureValue = new JLabel(" kPa");
		
		//Set up labels
		lblTempValue.setText((int)p5Canvas.temp +celsiusToK+" K");
		lblPressureValue.setText(p5Canvas.pressure+ " kPa");
		
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

		switch (sim) {
		case 1:
			if (set == 1) {
				lblConText1.setText("Methane:");
				lblConValue1.setText("0.017 M");
				lblConText2.setText("Oxygen:");
				lblConValue2.setText("0.033 M");
				lblConText3.setText("Carbon Dioxide:");
				lblConValue3.setText("0 M");
				lblConText4.setText("Water:");
				lblConValue4.setText("0 M");
				dashboard.add(lblConText1, "cell 0 1");
				dashboard.add(lblConValue1, "cell 1 1");
				dashboard.add(lblConText2, "cell 0 2");
				dashboard.add(lblConValue2, "cell 1 2");
				dashboard.add(lblConText3, "cell 0 3");
				dashboard.add(lblConValue3, "cell 1 3");
				dashboard.add(lblConText4, "cell 0 4");
				dashboard.add(lblConValue4, "cell 1 4");
				dashboard.add(lblVolumeText, "cell 0 5");
				dashboard.add(lblVolumeValue, "cell 1 5");
			} else if (set == 2) {
				lblConText1.setText("Hydrogen Iodide:");
				lblConValue1.setText("0.033 M");
				lblConText2.setText("Hydrogen:");
				lblConValue2.setText("0 M");
				lblConText3.setText("Iodine:");
				lblConValue3.setText("0 M");
				dashboard.add(lblConText1, "cell 0 1");
				dashboard.add(lblConValue1, "cell 1 1");
				dashboard.add(lblConText2, "cell 0 2");
				dashboard.add(lblConValue2, "cell 1 2");
				dashboard.add(lblConText3, "cell 0 3");
				dashboard.add(lblConValue3, "cell 1 3");
				dashboard.add(lblVolumeText, "cell 0 4");
				dashboard.add(lblVolumeValue, "cell 1 4");
			}
			break;
		case 2:
			lblPressureValue.setText("825.86 kPa");
			dashboard.add(lblPressureText, "cell 0 1");
			dashboard.add(lblPressureValue, "cell 1 1");
			dashboard.add(lblTempText, "cell 0 2");
			dashboard.add(lblTempValue, "cell 1 2");
			dashboard.add(lblVolumeText, "cell 0 3");
			dashboard.add(lblVolumeValue, "cell 1 3");
			break;
		case 3:

			String alignStr = new String(", align center");

			int barWidth = 40;
			int barHeight = 120;
			dashboard.setLayout(new MigLayout("",
					"[45][8][45][25][45][8][10][8][45]", "[][][grow][]"));
			dashboard
					.add(main.lblElapsedTimeText, "cell 0 3 4 1, align center");
			dashboard.add(main.elapsedTime, "cell 4 3 3 1");

			dashboard.add(lblBarLabelPressure, "cell 0 0" + alignStr);
			dashboard.add(lblMultiplicationText1, "cell 1 0" + alignStr);
			dashboard.add(lblBarLabelVolume, "cell 2 0" + alignStr);

			dashboard.add(lblEqualText, "cell 3 0" + alignStr);
			dashboard.add(lblBarLabelMol, "cell 4 0" + alignStr);
			dashboard.add(lblMultiplicationText2, "cell 5 0" + alignStr);
			dashboard.add(lblRText, "cell 6 0" + alignStr);
			dashboard.add(lblMultiplicationText3, "cell 7 0" + alignStr);
			dashboard.add(lblBarLabelTemp, "cell 8 0" + alignStr);

			barPressure.setPreferredSize(new Dimension(barWidth, barHeight));
			barVolume.setPreferredSize(new Dimension(barWidth, barHeight));
			barMol.setPreferredSize(new Dimension(barWidth, barHeight));
			barTemp.setPreferredSize(new Dimension(barWidth, barHeight));
			dashboard.add(barPressure, "cell 0 2" + alignStr);
			dashboard.add(barVolume, "cell 2 2" + alignStr);
			dashboard.add(barMol, "cell 4 2" + alignStr);
			dashboard.add(barTemp, "cell 8 2" + alignStr);
				switch(set)
				{
				case 3:
					barPressure.setValue(825.86f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(25+celsiusToK);
					break;
				case 4:
					barPressure.setValue(867.43f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(40+celsiusToK);
					break;
				case 5:
					barPressure.setValue(922.85f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(60+celsiusToK);
					break;
				case 6:
					barPressure.setValue(1033.71f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(100+celsiusToK);
					break;
				case 7:
					barPressure.setValue(825.86f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(25+celsiusToK);
					break;
				case 1:
					barPressure.setValue(825.86f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(25+celsiusToK);
					break;
				case 2:
					barPressure.setValue(825.86f);
					barVolume.setValue(60);
					barMol.setValue(20);
					barTemp.setValue(25+celsiusToK);
					break;
				}
			break;
		case 4:
			if (set == 1) {
				lblConText1.setText("[Ammonia]:");
				lblConValue1.setText("16.67 M");
				lblConText2.setText("[Hydrogen]:");
				lblConValue2.setText("0 M");
				lblConText3.setText("[Nitrogen]:");
				lblConValue3.setText("0 M");
				dashboard.add(lblConText1, "cell 0 1");
				dashboard.add(lblConValue1, "cell 1 1");
				dashboard.add(lblConText2, "cell 0 2");
				dashboard.add(lblConValue2, "cell 1 2");
				dashboard.add(lblConText3, "cell 0 3");
				dashboard.add(lblConValue3, "cell 1 3");
				dashboard.add(lblVolumeText, "cell 0 4");
				dashboard.add(lblVolumeValue, "cell 1 4");
			} else if (set == 2) {
				lblConText1.setText("[Nitrogen-Dioxide]:");
				lblConValue1.setText("16.67 M");
				lblConText2.setText("[Nitric-Oxide]:");
				lblConValue2.setText("0 M");
				lblConText3.setText("[Oxygen]:");
				lblConValue3.setText("0 M");
				dashboard.add(lblConText1, "cell 0 1");
				dashboard.add(lblConValue1, "cell 1 1");
				dashboard.add(lblConText2, "cell 0 2");
				dashboard.add(lblConValue2, "cell 1 2");
				dashboard.add(lblConText3, "cell 0 3");
				dashboard.add(lblConValue3, "cell 1 3");
				dashboard.add(lblVolumeText, "cell 0 4");
				dashboard.add(lblVolumeValue, "cell 1 4");
			} else if (set == 3) {
				lblConText1.setText("[Carbon-Monoxide]:");
				lblConValue1.setText("16.67 M");
				lblConText2.setText("[Nitrogen-Dioxide]:");
				lblConValue2.setText("16.67 M");
				lblConText3.setText("[Carbon-Dioxide]:");
				lblConValue3.setText("0 M");
				lblConText4.setText("[Nitric-Oxide]:");
				lblConValue4.setText("0 M");
				dashboard.add(lblConText1, "cell 0 1");
				dashboard.add(lblConValue1, "cell 1 1");
				dashboard.add(lblConText2, "cell 0 2");
				dashboard.add(lblConValue2, "cell 1 2");
				dashboard.add(lblConText3, "cell 0 3");
				dashboard.add(lblConValue3, "cell 1 3");
				dashboard.add(lblConText4, "cell 0 4");
				dashboard.add(lblConValue4, "cell 1 4");
				dashboard.add(lblVolumeText, "cell 0 5");
				dashboard.add(lblVolumeValue, "cell 1 5");
			}
			break;
		default:
			break;
		}
		
		dashboard.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {
		//Clear old force
		clearAllMoleculeForce();
		
		switch (sim)
		{
		case 1:
			if(set==1)
				computeForceSim1Set1();
			else if( set ==2 )
				computeForceSim1Set2();
			break;
		case 2:
			computeForceSim2();
			break;
		case 3:
			break;
		case 4:
			break;
			default:
				break;
		}

	}
	
	
	//Compute force function for Sim 1 Set 1
	private void computeForceSim1Set1()
	{
		Molecule moleThis = null;
		Molecule moleOther = null;
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float xValue =0 ;
		float yValue = 0 ;
		float forceX = 0;
		float forceY = 0;
		float dis = 0;
		float forceScale = 0.1f;
		curTime = p5Canvas.getMain().time;
		//Pull Methane and Oxygen together after 15 secs
		if(curTime>15)
		{
			for( int i=0;i<State.molecules.size();i++)
			{
				moleThis = State.molecules.get(i);
				if(moleThis.getName().equals("Oxygen"))
				{
					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { 
						locThis.set(moleThis.getElementLocation(thisE));
						moleThis.sumForceX[thisE] = 0;
						moleThis.sumForceY[thisE] = 0;
						for (int k = 0; k < molecules.size(); k++) {
							if (k == i)
								continue;
							moleOther = molecules.get(k);
							if (moleOther.getName().equals("Methane"))
							{
								locOther.set(moleOther.getPosition());
								if (locThis == null || locOther == null)
									continue;
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * forceScale;
								forceY = (float) (yValue / dis) * forceScale;

								// Add attraction force to thisMolecule
								moleThis.sumForceX[thisE] += forceX;
								moleThis.sumForceY[thisE] += forceY;
								break;
							}
						}
						
					}
		
				}
			}
		}
	}
	
	//Compute force function for Sim 1 Set 2
	private void computeForceSim1Set2()
	{
		Molecule moleThis = null;
		Molecule moleOther = null;
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float xValue =0 ;
		float yValue = 0 ;
		float forceX = 0;
		float forceY = 0;
		float dis = 0;
		float forceScale = 0.6f;
		curTime = p5Canvas.getMain().time;
		//Pull Methane and Oxygen together after 15 secs
		if(curTime>15)
		{
			for( int i=0;i<State.molecules.size();i++)
			{
				moleThis = State.molecules.get(i);
				if(moleThis.getName().equals("Hydrogen-Iodide"))
				{
					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { 
						locThis.set(moleThis.getElementLocation(thisE));
						moleThis.sumForceX[thisE] = 0;
						moleThis.sumForceY[thisE] = 0;
						for (int k = 0; k < molecules.size(); k++) {
							if (k == i)
								continue;
							moleOther = molecules.get(k);
							if (moleOther.getName().equals("Hydrogen-Iodide"))
							{
								locOther.set(moleOther.getPosition());
								if (locThis == null || locOther == null)
									continue;
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * forceScale;
								forceY = (float) (yValue / dis) * forceScale;

								// Add attraction force to thisMolecule
								moleThis.sumForceX[thisE] += forceX;
								moleThis.sumForceY[thisE] += forceY;
								break;
							}
						}
						
					}
		
				}
			}
		}
	}
	
	//Compute force function for Sim 2
	private void computeForceSim2()
	{
		Molecule moleThis =null;
		float topBoundary = p5Canvas.h / 2;
		float gravityCompensation = 0.2f;


		// high boundary check
		for(int i =0;i<State.molecules.size();i++)
		{
			moleThis = molecules.get(i);
			if(!moleThis.getName().equals("Carbon-Dioxide"))
			{
					Vec2 pos = box2d.coordWorldToPixels(moleThis.getPosition());
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
																							// element
		
							moleThis.sumForceX[thisE] = 0;
							moleThis.sumForceY[thisE] = (float) (gravityCompensation * -1*Math.sqrt(topBoundary- pos.y));
		
						}
				}
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */
	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int set = p5Canvas.getSet();
		int sim = p5Canvas.getSim();
		Simulation simulation = getSimulation(sim, set);
		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
		if (spawnStyle == SpawnStyle.Gas) {
			res = this.addGasMolecule(isAppEnable, compoundName, count);
		} else if (spawnStyle == SpawnStyle.Liquid) {
			res = this.addSingleIon(isAppEnable, compoundName, count);
		} else if (spawnStyle == SpawnStyle.Solvent) {
			res = this.addSolvent(isAppEnable, compoundName, count, simulation);
		} else if (spawnStyle == SpawnStyle.Precipitation) // Dissolvable
															// compound spawn
															// like
															// precipitation
		{
			res = this.addPrecipitation(isAppEnable, compoundName, count,
					simulation, (float) Math.PI);
		} else if (spawnStyle == SpawnStyle.SolidSpecial) {
			res = this.addSolidPowder(isAppEnable, compoundName, count,
					simulation);
		}

		if (res) {
			// Connect new created molecule to table index
			int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
			int lastIndex = State.molecules.size() - 1;
			if (compoundName.equals("Catalyst"))
				catalystAdded = true;
			else if(compoundName.equals("Inhibitor"))
				inhibitorAdded = true;
			for (int i = 0; i < count; i++) {
				State.molecules.get(lastIndex - i).setTableIndex(tIndex);
				State.molecules.get(lastIndex - i).setRatioKE(
						1 / simulation.getSpeed());
			}
		}

		return res;
	}

	// Add Sodium-Bicarbonate in chunk in Sim 2 Set 1
	protected boolean addPrecipitation(boolean isAppEnable,
			String compoundName, int count, Simulation simulation, float angle) {
		boolean res = true;

		int numCol = (int) Math.ceil((float) count / 3); // number of row
		int numRow = (int) Math.ceil((float) count / numCol); // number of
																// column

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float increX = p5Canvas.w / 16;
		float offsetX = size.x / 2 + size.x / 6;
		float centerX = p5Canvas.x + offsetX; // X coordinate around which we
												// are going to add
		// Ions, 50 is border width
		float centerY = p5Canvas.y + p5Canvas.h - size.y * numRow
				- p5Canvas.boundaries.difVolume; // Y coordinate around
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

				y = centerY + ((numRow - 1) - i % numRow) * size.y;

				molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
						angle));

				// Set precipitation inreactive
				// Precipitation will get dissolved first, and the ions
				// generated are reactive
				int index = molecules.size() - 1;
				Molecule m = molecules.get(index);

				res = true;
			}
		}

		return res;

	}

	// Add Sodium-Bicarbonate in form of powder in Sim 2 Set 2
	protected boolean addSolidPowder(boolean isAppEnable, String compoundName,
			int count, Simulation simulation) {
		boolean res = true;

		// Powder spawns at bottom and both sides initially, and number is fixed
		// at 10 // column

		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);
		float offsetX = size.x / 2 + size.x / 6;
		float leftX = p5Canvas.x + offsetX; // X coordinate around which we
											// are going to add
		// Ions, 50 is border width
		float botY = p5Canvas.y + p5Canvas.h - size.y
				- p5Canvas.boundaries.difVolume; // Y coordinate around
		int leftStartIndex = 0;
		int botStartIndex = 2;
		int rightStartIndex = 8;
		float rightX = leftX + (rightStartIndex - botStartIndex - 1) * size.x;

		int angle = 0;
		float x = 0, y = 0;
		// Left side 0-1
		for (int i = leftStartIndex; i < botStartIndex; i++) {

			x = leftX;
			y = botY - (i - leftStartIndex + 1) * size.y;
			molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
					angle));
		}
		// Bottom side 2-7
		for (int i = botStartIndex; i < rightStartIndex; i++) {

			x = leftX + (i - botStartIndex) * (size.x);
			y = botY;
			molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
					angle));
		}
		// Right side 8-9
		for (int i = rightStartIndex; i < count; i++) {
			x = rightX;
			y = botY - (i - rightStartIndex + 1) * size.y;
			molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
					angle));
		}

		res = true;
		return res;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simulations.UnitBase#beginReaction(org.jbox2d.dynamics.contacts.Contact)
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

		int set = p5Canvas.getSet();
		int sim = p5Canvas.getSim();
		Simulation simulation = getSimulation(sim, set);

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
		int set = p5Canvas.getSet();
		int sim = p5Canvas.getSim();
		Random rand = new Random();
		float probability = 1.0f;
		float randomFloat = 0f;
		switch (sim) {
		case 1:
			// Sim 1 set 1
			if (reactants.contains("Methane") && reactants.contains("Oxygen")) {
				float radius = 175;
				probability = 0.6f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= probability) {
				// Compute midpoint of collision molecules
				Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
				Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
				Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);

				// Go through all molecules to check if there are any molecules
				// nearby
				for (int i = 0; i < State.molecules.size(); i++) {

					if (molecules.get(i).getName().equals("Oxygen")
							&& molecules.get(i) != m1 && molecules.get(i) != m2) {
						Vec2 thirdMolecule = box2d.coordWorldToPixels(molecules
								.get(i).getPosition());
						if (radius > computeDistance(midpoint, thirdMolecule)) {
							products.add("Water");
							products.add("Water");
							products.add("Carbon-Dioxide");
							// Need to kill the third molecule
							p5Canvas.killingList.add(molecules.get(i));
							break; // Break after we find one nearby
						}
					}
				}
				}
			}
			// Sim 1 set 2
			else if (reactants.get(0).equals("Hydrogen-Iodide")
					&& reactants.get(1).equals("Hydrogen-Iodide")
					&& reactants.size() == 2) {
				probability = 0.75f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= probability) {
				products.add("Hydrogen");
				products.add("Iodine");
				}

			}
			break;
		case 2:
			// Sim 2 CH3COOH + NaHCO3 -> CH3COONa + H2O + CO2
			if (reactants.contains("Sodium-Bicarbonate")
					&& reactants.contains("Acetic-Acid")) {
				products.add("Sodium-Ion");
				products.add("Acetate");
				products.add("Carbon-Dioxide");
				products.add("Water");
			}
			break;
		case 3:
			
			// Sim 3 set 6 N2O4(g) <-> 2NO2(g)
			if(set==1 || set ==2)
			{
				if (reactants.get(0).equals("Nitrogen-Dioxide")
						&& reactants.get(1).equals("Nitrogen-Dioxide")) {
					if (!catalystAdded)
						probability = 0.75f;
					else
						probability = 0.95f;
					randomFloat = rand.nextFloat();
					if (randomFloat <= probability) {
						products.add("Dinitrogen-Tetroxide");
					}
				}
			}
			// Sim 3 set 1- set 5 ClNO2(g) + NO(g) -> NO2(g) + ClNO(g)
			else  
			{
				if (reactants.contains("Nitryl-Chloride")
						&& reactants.contains("Nitric-Oxide")) {
						probability = 0.4f;
					randomFloat = rand.nextFloat();
					if (randomFloat <= probability) {
						products.add("Nitrosyl-Chloride");
						products.add("Nitrogen-Dioxide");
					}
				}
			}

			break;
		case 4:
			if (set == 1) {
				// Sim 4 set 1 2NH3 -> 3H2 + N2
				if (reactants.get(0).equals("Ammonia")
						&& reactants.get(1).equals("Ammonia")) {
					products.add("Nitrogen");
					products.add("Nitrogen");
					products.add("Hydrogen");
					products.add("Hydrogen");
					products.add("Hydrogen");

				}
			} else if (set == 2) {
				// Sim 4 set 2 2NO2 -> 2NO +O2
				if (reactants.get(0).equals("Nitrogen-Dioxide")
						&& reactants.get(1).equals("Nitrogen-Dioxide")) {
					products.add("Nitric-Oxide");
					products.add("Nitric-Oxide");
					products.add("Oxygen");
				}
			} else if (set == 3) {
				// Sim 4 Set 3 CO + NO2 -> CO2 +NO
				if (reactants.contains("Carbon-Monoxide")
						&& reactants.contains("Nitrogen-Dioxide")) {
					products.add("Carbon-Dioxide");
					products.add("Nitric-Oxide");
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

	@Override
	public void updateOutput(int sim, int set) {
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.###");
		String output = null;
		updateMoleculeCon();
		switch (sim) {
		case 1:
			if (set == 1) {
				output = myFormatter.format(getConByName("Methane"));
				lblConValue1.setText(output + " M");
				output = myFormatter.format(getConByName("Oxygen"));
				lblConValue2.setText(output + " M");
				output = myFormatter.format(getConByName("Carbon-Dioxide"));
				lblConValue3.setText(output + " M");
				output = myFormatter.format(getConByName("Water"));
				lblConValue4.setText(output + " M");

			} else if (set == 2) {
				
				output = myFormatter.format(getConByName("Hydrogen-Iodide"));
				lblConValue1.setText(output + " M");
				output = myFormatter.format(getConByName("Hydrogen"));
				lblConValue2.setText(output + " M");
				output = myFormatter.format(getConByName("Iodine"));
				lblConValue3.setText(output + " M");
				
			}
			break;

		case 4:
			
			myFormatter = new DecimalFormat("###.##");
			int magnifier = 1000;
			if (set == 1) {
				output = myFormatter.format(getConByName("Ammonia")*magnifier);
				lblConValue1.setText(output + " M");
				output = myFormatter.format(getConByName("Hydrogen")*magnifier);
				lblConValue2.setText(output + " M");
				output = myFormatter.format(getConByName("Nitrogen")*magnifier);
				lblConValue3.setText(output + " M");
			} else if (set == 2) {

				output = myFormatter.format(getConByName("Nitrogen-Dioxide")*magnifier);
				lblConValue1.setText(output + " M");
				output = myFormatter.format(getConByName("Nitric-Oxide")*magnifier);
				lblConValue2.setText(output + " M");
				output = myFormatter.format(getConByName("Oxygen")*magnifier);
				lblConValue3.setText(output + " M");
			} else if (set == 3) {
				output = myFormatter.format(getConByName("Carbon-Monoxide")*magnifier);
				lblConValue1.setText(output + " M");
				output = myFormatter.format(getConByName("Nitrogen-Dioxide")*magnifier);
				lblConValue2.setText(output + " M");
				output = myFormatter.format(getConByName("Carbon-Dioxide")*magnifier);
				lblConValue3.setText(output + " M");
				output = myFormatter.format(getConByName("Nitric-Oxide")*magnifier);
				lblConValue4.setText(output + " M");
				
			}
			break;
		default:
			break;

		}

		if (lblVolumeValue.isShowing()) {
			

				float volumeMagnifier = getVolumeMagnifier()/1000;
				if( volumeMagnifier != 0)
					lblVolumeValue.setText(p5Canvas.currentVolume + " L");
				else
					lblVolumeValue.setText(p5Canvas.currentVolume
							+ " mL");

		}
		if (lblTempValue.isShowing()) {
			output = myFormatter.format(p5Canvas.temp+celsiusToK);
			lblTempValue.setText(output + " K");
		}

		if (lblPressureValue.isShowing()) {
			myFormatter = new DecimalFormat("###.##");
			output = myFormatter.format(p5Canvas.pressure);
			lblPressureValue.setText(output + " kPa");
		}
		// Update bars
		if (barPressure != null)
	
			if (barPressure.isShowing()) {
				barPressure.setValue(p5Canvas.pressure);
				barVolume.setValue(p5Canvas.currentVolume);
				barMol.setValue(p5Canvas.mol);
				barTemp.setValue(p5Canvas.temp+celsiusToK);
				barPressure.getParent().repaint();
			}

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
			if(name.equals("Sodium-Bicarbonate"))
			{
				con = 26.2f;
			}
			else if(name.equals("Water"))
			{
				if(State.getMoleculeNumByName("Water")!=0)
				con =55.35f;
				else
					con =0f;
			}
			else if(name.equals("Acetic-Acid")||name.equals("Sodium-Acetate")||name.equals("Carbon-Dioxide"))
			{
				mole = (float) Compound.counts.get(i) / numMoleculePerMole;
				con = mole / (volume/2);
			}
			else  //General case
			{
			mole = (float) Compound.counts.get(i) / numMoleculePerMole;
			con = mole / volume;
			}
			moleculeConHash.put(Compound.names.get(i), con);
		}
		
	}

	public float getConByName(String s) {
		if (moleculeConHash.containsKey(s))
			return moleculeConHash.get(s);
		else
			return 0;
	}
	
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
	    String name = (String)Compound.names.get(indexOfCompound);
		return getConByName(name);

	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {

	    String name = (String)Compound.names.get(indexOfCompound);
		float res = getConByName(name);
		return res;
	}

	@Override
	protected void initializeSimulation(int sim, int set) {

		this.updateMoleculeCon();		
	}

	public void addInhibitor()
	{
		inhibitorAdded = true;
	}

	@Override
	public void updateMoleculeCountRelated(int sim, int set) {
		this.updateMoleculeCon();		
		
	}

	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
