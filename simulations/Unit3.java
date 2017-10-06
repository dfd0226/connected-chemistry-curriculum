/**
 * 
 */
package simulations;

import static data.State.molecules;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


import main.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;

import data.DBinterface;
import data.State;

import simulations.models.Anchor;
import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.DistanceJointWrap;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author Qin Li The Unit3 class provides all specific computations encountered
 *         in Unit 3 simulation, Chemical Reactions Only be called by P5Canvas
 *         object
 */
public class Unit3 extends UnitBase {

	private float sodiumJointLength;
	private int frameCounter = 0;
	private int computeTriggerInterval = p5Canvas.FRAME_RATE;
	private boolean isAnchorSetup = false;
	public int combinationIndex = -1;
	public String [][] combination = new String[15][2];
	private HashMap<String,Float> moleculeMassHash ;

	private int numMoleculePerMole = 10;

	public Unit3(P5Canvas parent, PBox2D box) {
		super(parent, box);
		// TODO Auto-generated constructor stub
		//simulations = new Simulation[SIMULATION_NUMBER];
		unitNum = 3;
		moleculeMassHash =  new HashMap<String,Float>();
		setupSimulations();
		setupCombination();
	}

	@Override
	public void setupSimulations() {
		// TODO Auto-generated method stub
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Sodium", "Chlorine" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.SolidPavement, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 1, 2);
		String[] elements1 = { "Hydrogen-Iodide" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 1, 3);
		String[] elements2 = { "Ethene", "Oxygen" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 1, 4);
		String[] elements3 = { "Copper", "Silver-Ion", "Nitrate","Water" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.SolidPavement,
				SpawnStyle.Solvent,SpawnStyle.Solvent, SpawnStyle.Solvent };
		simulations[3].setupElements(elements3, spawnStyles3);

		simulations[4] = new Simulation(unitNum, 1, 5);
		String[] elements4 = { "Methane", "Oxygen" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);

		simulations[5] = new Simulation(unitNum, 1, 6);
		String[] elements5 = { "Iron", "Copper-II", "Sulfate","Water" };
		SpawnStyle[] spawnStyles5 = { SpawnStyle.SolidPavement,
				SpawnStyle.Solvent,SpawnStyle.Solvent, SpawnStyle.Solvent };
		simulations[5].setupElements(elements5, spawnStyles5);

		simulations[6] = new Simulation(unitNum, 1, 7);
		String[] elements6 = { "Hydrogen-Ion", "Chloride", "Lithium-Sulfide",
				"Water" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Solvent, SpawnStyle.Solvent,
				SpawnStyle.SolidSpecial, SpawnStyle.Solvent };
		simulations[6].setupElements(elements6, spawnStyles6);

		simulations[7] = new Simulation(unitNum, 1, 8);
		String[] elements7 = { "Hydrogen", "Chlorine" };
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);

		simulations[8] = new Simulation(unitNum, 1, 9);
		String[] elements8 = { "Hydrogen-Peroxide" };
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas };
		simulations[8].setupElements(elements8, spawnStyles8);

		simulations[9] = new Simulation(unitNum, 1, 10);
		String[] elements9 = { "Silver-Ion","Nitrate", "Sodium-Chloride", "Water" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Solvent,SpawnStyle.Solvent,
				SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[9].setupElements(elements9, spawnStyles9);

		simulations[10] = new Simulation(unitNum, 2, 1);
		String[] elements10 = { "Potassium-Bromide", "Silver-Nitrate",
				"Ammonium-Chloride", "Sodium-Carbonate", "Sodium-Hydroxide",
				"Lithium-Nitrate", "Water" };
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Precipitation,
				SpawnStyle.Precipitation, SpawnStyle.Precipitation,
				SpawnStyle.Precipitation, SpawnStyle.Precipitation,
				SpawnStyle.Precipitation, SpawnStyle.Solvent };
		simulations[10].setupElements(elements10, spawnStyles10);

	}

	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		String elements [];
		
		if(sim==1)
		{
			switch(set)
			{

			case 2:
			case 5:
			case 7:
				main.getCanvas().setYAxisLabeInset(0, -17);
				break;
			case 9: 
				main.getCanvas().setYAxisLabeInset(0, -12);
				break;
				default:
					main.getCanvas().setYAxisLabeInset(0, -16);
					break;
			}
		}
	}
	
//	if ((selectedSim == 1 && (selectedSet == 4 || selectedSet == 6
//	|| selectedSet == 7 || selectedSet == 10))
//	|| selectedSim == 2)
//rightPanel.add(cBoxHideWater, "cell 0 3");

	public void resetCheckboxPanel(int sim, int set) 
	{
		Main main = p5Canvas.getMain();
		if(sim==1)
		{
			switch(set)
			{
			case 4:
			case 6:
			case 7:
			case 10:
				main.checkBoxPanel.add(main.cBoxHideWater);
				break;
			default:
				break;
				
			}
		}
		else if(sim==2)
		{
			main.checkBoxPanel.add(main.cBoxHideWater);
		}
	}
	
	@Override
	public void updateMolecules(int sim, int set) {
		int unit = 3;
		boolean reactionHappened = false; // Boolean flag indicating is
											// reactions have taken place

		Simulation simulation = this.getSimulation(sim, set);
		if (sim == 1) {
			switch (set) {
			case 1:
				reactionHappened = this.reactNaCl(simulation);
				break;
			case 2:
			case 3:
			case 5:
			case 8:
			case 9:
				reactionHappened = this.reactGeneric(simulation);
				break;
			case 4:
				reactionHappened = reactCopperToSilver(simulation);
				break;
			case 6:
				reactionHappened = reactIronToCopper(simulation);
				break;
			case 7:
				reactionHappened = reactLi2S(simulation);
				break;
			case 10:
				reactionHappened = reactAgNo3AndNacl(simulation);
				break;

			}
		} else if (sim == 2) {
			reactionHappened = reactSolubility(simulation);
		}

		//updateCompoundNumber(unit,sim,set);
		

	}
	
	


	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = false;

		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		Simulation simulation = this.getSimulation(sim, set);
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
		}
		else if (spawnStyle == SpawnStyle.SolidPavement) {
			res = this.addSolidPavement(isAppEnable, compoundName, count,
					simulation);
		} else if (spawnStyle == SpawnStyle.SolidSpecial) {
			if (compoundName.equals("Lithium-Sulfide"))
				res = this.addSolidLi2S(isAppEnable, compoundName, count,
						simulation);
		}
		
		if(res)
		{
			//Connect new created molecule to table index
			int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
			int lastIndex = State.molecules.size()-1;
			for(int i = 0;i<count;i++)
			{
				State.molecules.get(lastIndex-i).setTableIndex(tIndex);
				State.molecules.get(lastIndex - i).setRatioKE(
						1 / simulation.getSpeed());
			}
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addSolidMoleculeSodium DESCRIPTION : Function to add Sodium
	 * molecules to PApplet The molecule alignment is different from that of
	 * general solid
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidPavement(boolean isAppEnable,
			String compoundName, int count, Simulation simulation) {
		boolean res = true;

		// TODO: Add style parameter Cube or paved
		// Style depends how the solid molecules would be aligned
		SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3
		float offsetX = 0; // Offset x from left border
		int leftBorder = 0; // left padding from left border
		int startIndex = molecules.size(); // Start index of this group in
											// molecules arraylist
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float moleWidth = size.x;
		float moleHeight = size.y;
		float jointLength = size.y;
		sodiumJointLength = jointLength;
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
		int colNum = dimension;
		boolean isClear = false;
		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		float increX = p5Canvas.w / 3;

		offsetX = p5Canvas.w / 2 - (colNum * moleWidth) / 2;
		centerX = p5Canvas.x + leftBorder + offsetX;
		centerY = p5Canvas.y + p5Canvas.h - rowNum * moleHeight
				- p5Canvas.boundaries.difVolume;

		if (spawnStyle == SpawnStyle.SolidCube) {
			// Create molecules align in cube pattern
			for (int i = 0; i < count; i++) {
				if ((i / dimension) % 2 == 0) /* Odd line */
				{
					x_ = centerX + i % dimension * moleWidth * 1.4f;
				} else /* even line */
				{
					x_ = centerX + 0.7f * moleWidth + i % dimension * moleWidth
							* 1.4f;
				}

				y_ = centerY + i / dimension * moleHeight;
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI / 2)));
			}

			/* Add joint for solid molecules */
			if (count > 1) {
				int index1 = 0;
				int index2 = 0;
				Molecule m1 = null;
				Molecule m2 = null;
				float xInterval = jointLength * 1.8f;

				for (int i = 0; i < count; i++) {
					/*
					 * In horizontal direction, all molecules create a joint
					 * connecting to its right next molecule
					 */
					if ((i + 1) % dimension != 0 && (i != count - 1)) /*
																	 * right
																	 * most
																	 * molecules
																	 */
					{
						index1 = i + startIndex;
						index2 = i + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.8f);
					}
					/*
					 * In vertical direction, all molecules create a joint
					 * connecting to its down next molecule
					 */
					if (((i / dimension + 1) != rowNum)
							&& ((i + dimension) < count)) /* bottom most molecules */
					{
						index1 = i + startIndex;
						index2 = i + dimension + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
					/*
					 * In diagonal direction, all molecules create a joint
					 * connecting to its bottom right molecule
					 */
					if (((i + 1) % dimension != 0)
							&& ((i + dimension + 1) < count)
							&& (i / dimension) % 2 != 0) {
						index1 = i + startIndex;
						index2 = i + dimension + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
					/*
					 * In diagonal direction, all molecules create a joint
					 * connecting to its top right molecule
					 */
					if ((i - dimension + 1) >= 0 && (i + 1) % dimension != 0
							&& (i / dimension) % 2 != 0) {
						index1 = i + startIndex;
						index2 = i - dimension + 1 + startIndex;
						m1 = molecules.get(index1);
						m2 = molecules.get(index2);
						joint2Elements(m1, m2, jointLength * 1.4f);
					}
				}
			}
		} else if (spawnStyle == SpawnStyle.SolidPavement)// Create molecules
															// align in a paved
															// way
		{
			dimension = 7;
			float xInterval = moleWidth * 2.0f;
			float lineSpace = moleHeight * 1.25f;
			rowNum = (int) Math.ceil((double) count / dimension);
			// SPECIFIED spawn location designed for particular molecules
			if (compoundName.equals("Sodium"))
				offsetX = p5Canvas.w / 2
						- ((dimension - 1) * (xInterval) + moleWidth) / 2;
			else if (compoundName.equals("Copper")
					|| compoundName.equals("Iron")) {
				dimension = 5;
				xInterval = moleWidth * 1.5f;
				offsetX = p5Canvas.w
						- ((dimension - 1) * (xInterval) + moleWidth);
				lineSpace = moleHeight * 1.0f;
			}
			centerX = p5Canvas.x + offsetX;
			for (int i = 0; i < count; i++) {
				if ((i / dimension) % 2 == 0) /* Odd line */
				{
					x_ = centerX + i % dimension * xInterval;
				} else /* even line */
				{
					x_ = centerX + 0.7f * moleWidth + i % dimension * xInterval;
				}

				centerY = p5Canvas.y + p5Canvas.h - rowNum * lineSpace
						- p5Canvas.boundaries.difVolume;
				y_ = centerY + i / dimension * lineSpace;
				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, (float) (Math.PI / 2)));
				if ((i / dimension) != 0) // If molecules are at the bottom, we
											// set them as inreactive ones
					molecules.get(molecules.size() - 1).setReactive(false);

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
					thisMolecule = molecules.get(thisIndex);
					neighborMolecule = molecules.get(neighborIndex);
					thisMolecule.neighbors.add(neighborMolecule);
				}
			}

			/* Add joint for solid molecules */
			if (count > 1) {
				int index1 = 0;
				int index2 = 0;
				Molecule m1 = null;
				Molecule m2 = null;
				float frequency = 5;
				float damp = 0.4f;
				float jointLen = 3.0f;
				float xp = 0;
				float yp = 0;

				for (int i = 0; i < count; i++) {

					/* For every molecule, create a anchor to fix its position */
					index1 = i + startIndex;
					m1 = molecules.get(index1);
					Vec2 m1Pos = box2d.coordWorldToPixels(m1.getPosition());
					Anchor anchor = new Anchor(m1Pos.x, m1Pos.y, box2d,
							p5Canvas);
					State.anchors.add(anchor);
					joint2Elements(m1, anchor, jointLen, frequency, damp);
		
				}

			}
		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : addSolidLi2S DESCRIPTION : Function to add Sodium molecules
	 * Li2S
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidLi2S(boolean isAppEnable, String compoundName,
			int count, Simulation simulation) {
		boolean res = true;

		// Li2S has fixed molecule number 6

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		float x_ = 0; // X Coordinate for a specific molecule
		float y_ = 0; // Y Coordinate for a specific molecule
						// molecules arraylist
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);

		float angle = 0;
		float moleWidth = size.x;
		float moleHeight = size.y;

		// offsetX = p5Canvas.w / 2 - (colNum * moleWidth) / 2;
		centerX = (p5Canvas.x + p5Canvas.w) / 2;
		centerY = p5Canvas.y + p5Canvas.h - (moleHeight + moleWidth)
				- p5Canvas.boundaries.difVolume;
		float lineSpace = moleHeight / 2 + moleWidth / 2;

		int startIndex = State.molecules.size() - 1;
		if (startIndex == -1)
			startIndex = 0;

		for (int i = 0; i < count; i++) {
			x_ = centerX + lineSpace * (i % 3 - 1);
			y_ = centerY + i / 3 * lineSpace;
			angle = (i % 2 == 0) ? 0.0f : ((float) Math.PI / 2);
			res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
					p5Canvas, angle));
			State.molecules.get(molecules.size() - 1).body
					.setFixedRotation(true);

		}
		float jointLength = 0;
		float frequency = 15;
		float damp = 0;
		Molecule m1 = null;
		Molecule m2 = null;

		// Create joints in horizontal direction
		jointLength = moleHeight / 2 + moleWidth / 2;
		for (int i = 0; i < count; i++) {
			if ((i + 1) % 3 != 0) {
				m1 = State.molecules.get(startIndex + i);
				m2 = State.molecules.get(startIndex + i + 1);
				joint2Elements(m1, m2, jointLength, frequency, damp,true);
			}
		}
		// Create joints in vertical direction
		for (int i = 0; i < count / 2; i++) {
			m1 = State.molecules.get(startIndex + i);
			m2 = State.molecules.get(startIndex + i + 3);
			joint2Elements(m1, m2, jointLength, frequency, damp,true);
		}

		// Create joints in diagonal direction
		jointLength = (float) (Math.sqrt(2) * (moleHeight / 2 + moleWidth / 2));
		m1 = State.molecules.get(startIndex + 0);
		m2 = State.molecules.get(startIndex + 4);
		joint2Elements(m1, m2, jointLength, frequency, damp,true);

		m1 = State.molecules.get(startIndex + 1);
		m2 = State.molecules.get(startIndex + 3);
		joint2Elements(m1, m2, jointLength, frequency, damp,true);

		m1 = State.molecules.get(startIndex + 1);
		m2 = State.molecules.get(startIndex + 5);
		joint2Elements(m1, m2, jointLength, frequency, damp,true);

		m1 = State.molecules.get(startIndex + 2);
		m2 = State.molecules.get(startIndex + 4);
		joint2Elements(m1, m2, jointLength, frequency, damp,true);

		return res;
	}

	/******************************************************************
	 * FUNCTION : beginReaction DESCRIPTION : Reaction function happens after
	 * collision
	 * 
	 * INPUTS : c ( Contact) OUTPUTS: None
	 *******************************************************************/
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
	 * FUNCTION : reactNaCl DESCRIPTION : Reaction for Sim 1 Set
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactNaCl(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Molecule m1 = (Molecule) p5Canvas.killingList.get(0);
			Molecule m2 = (Molecule) p5Canvas.killingList.get(1);

			Molecule mNew = null;
			Molecule mNew2 = null;

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = m1.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());

				molecules.add(mNew);
				mNew.body.setFixedRotation(true);
				if (i == 0)
					mNew.body.setLinearVelocity(m1.body.getLinearVelocity());

				else {
					mNew.body.setLinearVelocity(m2.body.getLinearVelocity());
				}
			}

			// Get joints to which this molecule is connecting to
			ArrayList<DistanceJointWrap> m1Joint = m1.destroy();
			ArrayList<DistanceJointWrap> m2Joint = m2.destroy();
			Molecule molecule1 = null;
			Molecule molecule2 = null;
			// ArrayList<Molecule> neighborMolecules = new
			// ArrayList<Molecule>();
			Molecule sodium = null;

			Anchor anchorTarget = null;
			Molecule jointTarget = null;
			// Get joint length and frequency
			float length = 0;
			float frequency = 0;
			float damp = 0;
			if (m1Joint == null) // m2 is Sodium
			{
				sodium = m2;
				for (int m = 0; m < m2Joint.size(); m++) {
					if (m2Joint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) m2Joint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) m2Joint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(m2Joint.get(m)
							.getLength());
					frequency = m2Joint.get(m).getFrequency();
					damp = m2Joint.get(m).getDampingRatio();
					joint2Elements(mNew, anchorTarget, length, frequency, damp);

				}
			} else // m1 is Sodium
			{
				sodium = m1;
				for (int m = 0; m < m1Joint.size(); m++) {
					if (m1Joint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) m1Joint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) m1Joint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(m1Joint.get(m)
							.getLength());
					frequency = m1Joint.get(m).getFrequency();
					damp = m1Joint.get(m).getDampingRatio();
					joint2Elements(mNew, anchorTarget, length, frequency, damp);

				}

			}

			// After we killed current sodium and created a sodium-Chloride at
			// the same location
			// We need to pick another sodium in its neighbor which is the
			// closest to reacted Chlorine

			if (!sodium.neighbors.isEmpty()) {

				Molecule secondSodium = null;

				// secondSodium = compareDistance(chlorine, neighborMolecules);
				// secondSodium = pickRightBottomOne(mNew, neighborMolecules);
				secondSodium = sodium.neighbors.get(0);

				// Create a new Sodium-Chloride
				Vec2 loc = secondSodium.getPosition();
				float x1 = PBox2D.scalarWorldToPixels(loc.x);
				float y1 = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x1, y1);
				mNew2 = new Molecule(newVec.x, newVec.y, mNew.getName(), box2d,
						p5Canvas, (float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());

				molecules.add(mNew2);
				mNew2.body.setFixedRotation(true);
				mNew2.body.setLinearVelocity(secondSodium.body
						.getLinearVelocity());

				// Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> sodiumJoint = secondSodium
						.destroy();

				// Create new joints for new molecules
				for (int m = 0; m < sodiumJoint.size(); m++) {

					if (sodiumJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) sodiumJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) sodiumJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// existing anchors
					length = PBox2D.scalarWorldToPixels(sodiumJoint.get(m)
							.getLength());
					frequency = sodiumJoint.get(m).getFrequency();
					damp = sodiumJoint.get(m).getDampingRatio();
					joint2Elements(mNew2, anchorTarget, length, frequency, damp);

				}
			}

			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation.getUnitNum(),simulation.getSimNum(),simulation.getSetNum());
			return true;
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactCopperToSilver DESCRIPTION : Reaction for Sim 1 Set 4.
	 * Silver swap with copper
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactCopperToSilver(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		// If it is dissolving process
		if (p5Canvas.killingList.get(0).getName().equals("Silver-Nitrate")) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;
				Molecule mNew2 = null;
				float offsetX = 0; // Set an offset to spawn position to make it
									// look real

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 5;
					if (p5Canvas.products.get(i).equals("Silver-Ion")) // Set an
																		// offset
																		// x for
																		// silver-ion
						x1 = PBox2D.scalarWorldToPixels(loc.x)
								+ mOld[0].getMaxSize() / 2 - offsetX;
					else
						x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					String compoundName = new String(p5Canvas.products.get(i));
					mNew = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					mNew.setRatioKE(1 / simulation.getSpeed());

					molecules.add(mNew);

					if (i == 0)
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());

					else {
						mNew.body.setLinearVelocity(mOld[0].body
								.getLinearVelocity());
					}
					//Update molecule legends on right panel
					//The index of ions will be the same with that of compound which they were before
					int index = p5Canvas.getTableView().getIndexByName("Silver-Nitrate");
					mNew.setTableIndex(index);
				}
				
				
				for (int i = 0; i < numToKill; i++)
					mOld[i].destroy();

				
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
			}
		} else { // If this is reaction process
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
				Molecule copper = null;
				Molecule silverIon = null;
				
				if (p5Canvas.killingList.get(0).getName().equals("Copper")) { //The third one will always be silver-Ion
					copper = (Molecule) p5Canvas.killingList.get(0);
					silverIon = (Molecule) p5Canvas.killingList.get(1);
				} else {
					silverIon = (Molecule) p5Canvas.killingList.get(0);
					copper = (Molecule) p5Canvas.killingList.get(1);
				}

				Molecule newCopperII = null;
				ArrayList<Molecule> newSilver = new ArrayList<Molecule>();

				Vec2 loc = null;
				int silverIndex = -1;
				float silverSize = 2.25f; //In world coordinates
				Vec2 newVec = null;

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					if (p5Canvas.products.get(i).equals("Silver"))
					{
						silverIndex ++; //Index of the 1st one is 0, 2nd one is 1
						loc = copper.getPosition();
					}
					else
						loc = silverIon.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y + silverSize*silverIndex);
					 newVec = new Vec2(x1, y1);
					if (p5Canvas.products.get(i).equals("Silver")) {
						String compoundName = new String(p5Canvas.products.get(i));
						 Molecule mole = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mole.setRatioKE(1 / simulation.getSpeed());

						 newSilver.add(mole);
						molecules.add(mole);
						mole.body.setLinearVelocity(silverIon.body
								.getLinearVelocity());
//						//Set tableIndex for Silver-Ion
//						int tableIndex = p5Canvas.getTableView().getIndexByName(compoundName);
//						mole.setTableIndex(tableIndex);
//						//Increase Silver count by 1
//						int countIndex = Compound.names.indexOf(compoundName);
//						Compound.counts.set(countIndex,Compound.counts.get(countIndex)+1);
						
						
					} else // If new molecules is copper-II
					{
						String compoundName = new String(p5Canvas.products.get(i));
						newCopperII = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						newCopperII.setRatioKE(1 / simulation.getSpeed());

						molecules.add(newCopperII);
						newCopperII.body.setLinearVelocity(copper.body
								.getLinearVelocity());
						//Set tableIndex for Copper-II
//						int tableIndex = p5Canvas.getTableView().getIndexByName("Copper-II-Nitrate");
//						newCopperII.setTableIndex(tableIndex);
						//Copper-II won`t be added to Compound.counts
						//Decrease copper count by 1
//						int countIndex = Compound.names.indexOf("Copper");
//						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					}

				}
				this.updateCompoundNumber(simulation);
//				//We also need to change a Nitrate which belongs to Silver-Nitrate to Copper-II-Nitrate
//				int nitrateNum = 0 ;
//				for( int i = 0;i<State.molecules.size();i++)
//				{
//					if(State.molecules.get(i).getName().equals("Nitrate")&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName("Silver-Nitrate"))
//					{
//						
//						int tableIndex = p5Canvas.getTableView().getIndexByName("Copper-II-Nitrate");
//						State.molecules.get(i).setTableIndex(tableIndex);
//						nitrateNum++;
//						if(nitrateNum>=2)
//						break;
//					}
//				}
//				
//				if(nitrateNum==2)
//				{
//					//Increase Copper-II-Nitrate by 1
//					int countIndex = Compound.names.indexOf("Copper-II-Nitrate");
//					Compound.counts.set(countIndex,Compound.counts.get(countIndex)+1);
//					//Decrease Silver-Nitrate by 2
//					countIndex = Compound.names.indexOf("Silver-Nitrate");
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-2);
//				}

				ArrayList<DistanceJointWrap> mJoint = null;
				// Get joints to which this molecule is connecting to
				for( int k=0;k<p5Canvas.killingList.size();k++)
				{
					if( p5Canvas.killingList.get(k).getName().equals("Copper") ) //Get copper joint information
					{
						 mJoint = p5Canvas.killingList.get(k).destroy();
					}
					else
					{
						p5Canvas.killingList.get(k).destroy();
					}
				}
				

				Anchor anchorTarget = null;

				// Get joint length and frequency
				float length = 0;
				float frequency = 0;
				float damp = 0;

				//Connect the first silver to first anchor which connected to copper previously
				for (int m = 0; m < mJoint.size(); m++) {
					if (mJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) mJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) mJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(mJoint.get(m)
							.getLength());
					frequency = mJoint.get(m).getFrequency();
					damp = mJoint.get(m).getDampingRatio();
					joint2Elements(newSilver.get(0), anchorTarget, length, frequency,
							damp);

				}
				//Create a anchor for 2nd silver and attach silver to it
				for (int m = 0; m < mJoint.size(); m++) {
					//Create anchor. newVec stores the second silver-Ion position
					//float size = box2d.scalarPixelsToWorld(newSilver.get(1).getMaxSize());
					Anchor anchor = new Anchor(newVec.x, newVec.y, box2d, p5Canvas);

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(mJoint.get(m)
							.getLength());
					frequency = mJoint.get(m).getFrequency();
					damp = mJoint.get(m).getDampingRatio();
					joint2Elements(newSilver.get(1), anchor, length, frequency,
							damp);
				}
				
				//Adjust copper location in case it will not stuck in silvers
				if(newCopperII.getPosition().y<newSilver.get(1).getPosition().y)
				{
					Vec2 pos = new Vec2(newSilver.get(1).getPosition());
					pos.y+=silverSize;
					newCopperII.setPosition(pos, newCopperII.getAngle());
				}
				

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactIronToCopper DESCRIPTION : Reaction for Sim 1 Set 6.
	 * Silver swap with copper
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactIronToCopper(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		// If it is dissolving process
		if (p5Canvas.killingList.get(0).getName().equals("Copper-II-Sulfate")) {
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;
				Molecule mNew2 = null;
				float offsetX = 0; // Set an offset to spawn position to make it
									// look real

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 5;
					if (p5Canvas.products.get(i).equals("Copper-II")) // Set an
																		// offset
																		// x for
																		// silver-ion
						x1 = PBox2D.scalarWorldToPixels(loc.x)
								+ mOld[0].getMaxSize() / 2 - offsetX;
					else
						x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					String compoundName = new String(p5Canvas.products.get(i));
					mNew = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					mNew.setRatioKE(1 / simulation.getSpeed());

					molecules.add(mNew);
					//Set Copper-II and Sulfate tableIndex to "Copper-II-Sulfate"
					int tableIndex = p5Canvas.getTableView().getIndexByName("Copper-II-Sulfate");
					mNew.setTableIndex(tableIndex);				

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
			}
		} else // Reaction: swap Iron with Copper-II
		{
			if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
				Molecule iron = null;
				Molecule copperIon = null;
				// Get Iron and copperIon reference
				if (p5Canvas.killingList.get(0).getName().equals("Copper-II")) {
					copperIon = (Molecule) p5Canvas.killingList.get(0);
					iron = (Molecule) p5Canvas.killingList.get(1);
				} else {
					iron = (Molecule) p5Canvas.killingList.get(0);
					copperIon = (Molecule) p5Canvas.killingList.get(1);
				}

				Molecule newIronII = null;
				Molecule newCopper = null;

				Vec2 loc = null;

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					if (p5Canvas.products.get(i).equals("Copper"))
						loc = iron.getPosition();
					else
						loc = copperIon.getPosition();
					float x1 = PBox2D.scalarWorldToPixels(loc.x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					if (p5Canvas.products.get(i).equals("Copper")) {
						String compoundName = new String(p5Canvas.products.get(i));
						newCopper = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						newCopper.setRatioKE(1 / simulation.getSpeed());

						molecules.add(newCopper);
						newCopper.body.setLinearVelocity(copperIon.body
								.getLinearVelocity());
//						//Set tableIndex of newCopper to "Copper" row
//						int tableIndex = p5Canvas.getTableView().getIndexByName(compoundName);
//						newCopper.setTableIndex(tableIndex);
//						//Increase copper count by 1
//						int countIndex = Compound.names.indexOf("Copper");
//						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					} else // If new molecules is copper-II
					{
						newIronII = new Molecule(newVec.x, newVec.y,
								p5Canvas.products.get(i), box2d, p5Canvas,
								(float) (Math.PI / 2));
						newIronII.setRatioKE(1 / simulation.getSpeed());

						molecules.add(newIronII);
						newIronII.body.setLinearVelocity(iron.body
								.getLinearVelocity());
//						//Set tableIndex of newIronII to "Iron-II-Sulfate" row
//						int tableIndex = p5Canvas.getTableView().getIndexByName("Iron-II-Sulfate");
//						newIronII.setTableIndex(tableIndex);
//						//Decrease Iron count by 1
//						int countIndex = Compound.names.indexOf("Iron");
//						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					}

				}
//				//We also need to change a Sulfate which belongs to Copper-II-Sulfate to Iron-II-Sulfate
//				for( int i = 0;i<State.molecules.size();i++)
//				{
//					if(State.molecules.get(i).getName().equals("Sulfate")&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName("Copper-II-Sulfate"))
//					{
//						int tableIndex = p5Canvas.getTableView().getIndexByName("Iron-II-Sulfate");
//						State.molecules.get(i).setTableIndex(tableIndex);
//						break;
//					}
//				}		
//					//Increase Iron-II-Sulfate by 1
//					int countIndex = Compound.names.indexOf("Iron-II-Sulfate");
//					Compound.counts.set(countIndex,Compound.counts.get(countIndex)+1);
//					//Decrease Copper-II-Sulfate by 1
//					countIndex = Compound.names.indexOf("Copper-II-Sulfate");
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
				this.updateCompoundNumber(simulation);

				// Get joints to which this molecule is connecting to
				ArrayList<DistanceJointWrap> mJoint = iron.destroy();
				ArrayList<DistanceJointWrap> m2Joint = copperIon.destroy();
				Molecule molecule1 = null;
				Molecule molecule2 = null;

				Anchor anchorTarget = null;
				Molecule jointTarget = null;
				// Get joint length and frequency
				float length = 0;
				float frequency = 0;
				float damp = 0;

				// copper = m2;
				for (int m = 0; m < mJoint.size(); m++) {
					if (mJoint.get(m).getBodyA().getUserData() instanceof Molecule)
						anchorTarget = (Anchor) mJoint.get(m).getBodyB()
								.getUserData();
					else
						anchorTarget = (Anchor) mJoint.get(m).getBodyA()
								.getUserData();

					// Create new joints between reaction created molecule and
					// old molecules
					length = PBox2D.scalarWorldToPixels(mJoint.get(m)
							.getLength());
					frequency = mJoint.get(m).getFrequency();
					damp = mJoint.get(m).getDampingRatio();
					joint2Elements(newCopper, anchorTarget, length, frequency,
							damp);

				}

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactAgNo3AndNacl DESCRIPTION : Reaction for Sim 1 Set 10.
	 * AgNo3 reacts with Nacl
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactAgNo3AndNacl(Simulation simulation) {

		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Silver-Nitrate")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Sodium-Chloride")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Sodium-Chloride")||mOld[i].getName().equals("Silver-Nitrate"))
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

						molecules.add(mNew);
							mNew.body.setLinearVelocity(new Vec2(0,0));
							
//							if(ionName.equals("Sodium-Ion")||ionName.equals("Chloride"))
//							{
//							//Set Sodium-Ion and Chloride tableIndex to "Sodium-Chloride"
//							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Chloride");
//							mNew.setTableIndex(tableIndex);
//							}
//							else if(ionName.equals("Silver-Ion")||ionName.equals("Nitrate"))
//							{
//								//Set Silver-Ion and Silver-Ion tableIndex to "Silver-Nitrate"
//								int tableIndex = p5Canvas.getTableView().getIndexByName("Silver-Nitrate");
//								mNew.setTableIndex(tableIndex);
//							}
							int countIndex = Compound.names.indexOf(ionName);
							Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					}
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i].destroy();
						int countIndex = Compound.names.indexOf(mOld[i].getName());
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					}

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			} 
			
			else // Reaction: Silver-Ion reacts with Chlorine, generate
					// Silver-Chloride
			{
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
					Molecule silverIon = null;
					Molecule chloride = null;
					// Get Iron and copperIon reference
					if (p5Canvas.killingList.get(0).getName()
							.equals("Silver-Ion")) {
						silverIon = (Molecule) p5Canvas.killingList.get(0);
						chloride = (Molecule) p5Canvas.killingList.get(1);
					} else {
						chloride = (Molecule) p5Canvas.killingList.get(0);
						silverIon = (Molecule) p5Canvas.killingList.get(1);
					}

					Molecule silverChloride = null;

					Vec2 loc  = silverIon.getPosition();
					Vec2 loc2 = chloride.getPosition();
					float x = (loc2.x<loc.x)?loc2.x:loc.x;
					float y = (loc2.y<loc.y)?loc2.y:loc.y;
					
					float x1 = PBox2D.scalarWorldToPixels(x);
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(y);
					Vec2 newVec = new Vec2(x1, y1);

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {

						String compoundName = new String(p5Canvas.products.get(i)); //"Silver-Chloride"
						silverChloride = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								0);
						silverChloride.setRatioKE(1 / simulation.getSpeed());
						molecules.add(silverChloride);
						silverChloride.body.setLinearVelocity(silverIon.body
								.getLinearVelocity());
//						//Set silverChloride tableSet to "SilverChloride"
//						int tableIndex = p5Canvas.getTableView().getIndexByName(compoundName);
//						silverChloride.setTableIndex(tableIndex);
//						//Increase Silver-Chloride count by 1
//						int countIndex = Compound.names.indexOf(compoundName);
//						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);

					}

					silverIon.destroy();
					chloride.destroy();
//					int nitrateNum=0;
//					int sodiumNum=0;
//					for( int i = 0;i<State.molecules.size();i++)
//					{
//						//Change tableindex of Nitrate to "Sodium-Nitrate"
//						if(State.molecules.get(i).getName().equals("Nitrate")&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName("Silver-Nitrate")&&nitrateNum==0)
//						{
//							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Nitrate");
//							State.molecules.get(i).setTableIndex(tableIndex);
//							nitrateNum++;
//						}
//						//Change tableindex of Sodium to "Sodium-Nitrate"
//						if(State.molecules.get(i).getName().equals("Sodium-Ion")&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName("Sodium-Chloride")&&sodiumNum==0)
//						{
//							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Nitrate");
//							State.molecules.get(i).setTableIndex(tableIndex);
//							sodiumNum++;
//						}
//					}
//					//Increase Sodium-Nitrate count by 1
//					int countIndex = Compound.names.indexOf("Sodium-Nitrate");
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
//					//Decrease Sodium-Chloride count by 1
//					countIndex = Compound.names.indexOf("Sodium-Chloride");
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
//					//Decrease Silver-Nitrate count by 1
//					countIndex = Compound.names.indexOf("Silver-Nitrate");
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					
					this.updateCompoundNumber(simulation);

					// Set up anchors if they have not been set up yet.
					if (!isAnchorSetup) {
						Vec2 pos = null;
						for (int i = 0; i < simulation.getAnchorNum(); i++) {
							pos = simulation.getAnchorPos(i);
							Anchor anchor = new Anchor(pos.x, pos.y, box2d,
									p5Canvas);
							State.anchors.add(anchor);
						}
						isAnchorSetup = true;

					}

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					return true;
				}
			}
		}
		return false;

	}

	/******************************************************************
	 * FUNCTION : reactLi2S DESCRIPTION : Reaction for Sim 1 Set 7. Li2S reacts
	 * with 2HCl
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactLi2S(Simulation simulation) {

		if (p5Canvas.killingList.isEmpty())
			return false;
		// If it is dissolving process

		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			if (p5Canvas.products.contains("Hydrogen-Sulfide")) {
				int numToKill = p5Canvas.killingList.size();
				Molecule[] mOld = new Molecule[numToKill];
				for (int i = 0; i < numToKill; i++)
					mOld[i] = (Molecule) p5Canvas.killingList.get(i);

				Molecule mNew = null;

				float offsetX = 0; // Set an offset to spawn position to make it
									// look real
				boolean left = true; // Boolean parameter used to set offsetX

				// Actually there is only one reaction going in each frame
				for (int i = 0; i < p5Canvas.products.size(); i++) {
					Vec2 loc = mOld[0].getPosition();
					float x1;
					offsetX = mOld[0].getMaxSize() / 3;
					if (p5Canvas.products.get(i).equals("Lithium-Ion")) // Set
																		// an
																		// offset
																		// x for
																		// silver-ion
					{
						if (left) {
							x1 = PBox2D.scalarWorldToPixels(loc.x) - offsetX;
							left = false;
						} else {
							x1 = PBox2D.scalarWorldToPixels(loc.x) + offsetX;
						}
					} else
						x1 = PBox2D.scalarWorldToPixels(loc.x); // H2S
					float y1 = p5Canvas.h * p5Canvas.canvasScale
							- PBox2D.scalarWorldToPixels(loc.y);
					Vec2 newVec = new Vec2(x1, y1);
					String compoundName = new String(p5Canvas.products.get(i)); //Hydrogen-Sulfide
					mNew = new Molecule(newVec.x, newVec.y,
							compoundName, box2d, p5Canvas,
							(float) (Math.PI / 2));
					mNew.setRatioKE(1 / simulation.getSpeed());

					//Set tableIndx of mNew to Hydrogen-Sulfide or Lithium-Chloride
//					if(compoundName.equals("Lithium-Ion"))
//						compoundName = new String("Lithium-Chloride");
//					int tableIndex = p5Canvas.getTableView().getIndexByName(compoundName);
//					mNew.setTableIndex(tableIndex);
					//Increase Hydrogen-Sulfide or Lithium-Chloride count by 1
//					int countIndex = Compound.names.indexOf(compoundName);
//					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					
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

				
//				int tableIndex = -1;
//				int chlorideNum=0;
//				//Change tableIndex of Chloride from Hydrogen-Chloride to Lithium-Chloride
//				for( int i = 0;i<State.molecules.size();i++)
//				{
//					
//					if(State.molecules.get(i).getName().equals("Chloride")&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName("Chloride"))
//					{
//						tableIndex = p5Canvas.getTableView().getIndexByName("Lithium-Chloride");
//						State.molecules.get(i).setTableIndex(tableIndex);
//						chlorideNum++;
//						if(chlorideNum==2)
//							break;
//					}
//				}
//				//Decrease Lithium-Sulfide count by 1
//				int countIndex = Compound.names.indexOf("Lithium-Sulfide");
//				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
//				//Decrease Hydrogen-Ion and Chloride count by 2
//				countIndex = Compound.names.indexOf("Hydrogen-Ion");
//				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-2);
//				countIndex = Compound.names.indexOf("Chloride");
//				Compound.counts.set(countIndex, Compound.counts.get(countIndex)-2);
				
				updateCompoundNumber(simulation);

				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return true;
			}
		}
		return false;
	}

	/******************************************************************
	 * FUNCTION : reactSolubility DESCRIPTION : Reaction for Sim 2 all sets
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactSolubility(Simulation simulation) {

		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName()
					.equals("Potassium-Bromide")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Silver-Nitrate")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Ammonium-Chloride")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Sodium-Carbonate")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Sodium-Hydroxide")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Lithium-Nitrate")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(!mOld[i].getName().equals("Water"))
							dissolveCompound = mOld[i]; //Get compound before dissolved, actually it is killinglist.get(0)
					}

					Molecule mNew = null;
					Molecule mNew2 = null;
					float offsetX = 0; // Set an offset to spawn position to
										// make it
										// look real

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						//offsetX = mOld[0].getMaxSize() / 5;
						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
						if(elementIndex !=-1 )
						{
							if(dissolveCompound.getName().equals("Sodium-Carbonate")&&ionName.equals("Sodium-Ion")&& i==1&&elementIndex==0)
								elementIndex =1; //Set corrent index for the second sodium-Ion in Sodium-Carbonate
							loc.set(dissolveCompound.loc[elementIndex]);
						}
						x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						String compoundName = new String(p5Canvas.products.get(i));
						mNew = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						mNew.setRatioKE(1 / simulation.getSpeed());

						molecules.add(mNew);
						
						//Set tableIndex of mNew to that of compound which it was
						int tableIndex = p5Canvas.getTableView().getIndexByName(dissolveCompound.getName());
						mNew.setTableIndex(tableIndex);
						
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
				}
			}

			else // Reactions
			{
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
					Molecule ion1 = null;
					Molecule ion2 = null;
					Molecule ion3 = null;
					Molecule bigIon = null;

					ion1 = (Molecule) p5Canvas.killingList.get(0);
					ion2 = (Molecule) p5Canvas.killingList.get(1);
					if(p5Canvas.killingList.size()>2)
					{
						ion3 = (Molecule) p5Canvas.killingList.get(2);
						bigIon= ion1.getMaxSize()>ion2.getMaxSize()?(ion1.getMaxSize()>ion3.getMaxSize()?ion1:ion3):(ion2.getMaxSize()>ion3.getMaxSize()?ion2:ion3);
					}
					else
						bigIon= ion1.getMaxSize()>ion2.getMaxSize()?ion1:ion2;


					Molecule newCompound = null;

					Vec2 loc = null;

					// Actually there is only one reaction going in each frame
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = bigIon.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);
						String compoundName = new String(p5Canvas.products.get(i));
						newCompound = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								bigIon.getAngle());
						newCompound.setRatioKE(1 / simulation.getSpeed());

						molecules.add(newCompound);
						newCompound.body.setLinearVelocity(ion1.body
								.getLinearVelocity());
						//Set tableIndex of newCompound to compoundName
						int tableIndex = p5Canvas.getTableView().getIndexByName(compoundName);
						newCompound.setTableIndex(tableIndex);
						//Increase newCompound count by 1
						int countIndex = Compound.names.indexOf(compoundName);
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
					}

					ion1.destroy();
					ion2.destroy();
					if(ion3!=null)
						ion3.destroy();
					
					if(combinationIndex == 5 ||combinationIndex == 9 ||combinationIndex == 10 || combinationIndex == 11  )
					{
						String restIon1 = null; //Ions that do not react
						String restIon2 = null;
						String restIon1Compound = null;
						String restIon2Compound = null;
						String agCompound = null;
						switch(combinationIndex)
						{
						case 5:
							restIon1 = new String("Potassium-Ion");
							restIon2 = new String("Nitrate");
							agCompound = new String("Potassium-Nitrate");
							break;
						case 9:
							restIon1 = new String("Nitrate");
							restIon2 = new String("Ammonium");
							agCompound = new String("Ammonium-Nitrate");
							break;
						case 10:
							restIon1 = new String("Nitrate");
							restIon2 = new String("Sodium-Ion");
							agCompound = new String("Sodium-Nitrate");
						case 11:
							restIon1 = new String("Nitrate");
							restIon2 = new String("Sodium-Ion");
							agCompound = new String("Sodium-Nitrate");
						}
						int restIon1Num=0,restIon2Num = 0;
						int restIon1Max=1,restIon2Max = 1; 
						if(combinationIndex==10) 	//Two Sodium-Ion and two Nitrate react
							restIon1Max=restIon2Max=2;
						for(int i = 0;i<State.molecules.size();i++)
						{
							if(State.molecules.get(i).getName().equals(restIon1)&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName(combination[combinationIndex][0])&&restIon1Num<restIon1Max)
							{	int tableIndex = p5Canvas.getTableView().getIndexByName(agCompound);
									State.molecules.get(i).setTableIndex(tableIndex);
									restIon1Num++;
							}	
							if(State.molecules.get(i).getName().equals(restIon2)&&State.molecules.get(i).getTableIndex()==p5Canvas.getTableView().getIndexByName(combination[combinationIndex][1])&&restIon2Num<restIon2Max)
							{	int tableIndex = p5Canvas.getTableView().getIndexByName(agCompound);
									State.molecules.get(i).setTableIndex(tableIndex);
									restIon2Num++;
							}	
						}
						
						//Decrease compound1 count by 1
						int countIndex = Compound.names.indexOf(combination[combinationIndex][0]);
						if(combinationIndex!=10) 
							Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						else//combination[10][0] is "Silver-Nitrate"
							Compound.counts.set(countIndex, Compound.counts.get(countIndex)-2);
						//Decrease compound1 count by 1
						countIndex = Compound.names.indexOf(combination[combinationIndex][1]);
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
						//Increase agCompound count by 1
						countIndex = Compound.names.indexOf(agCompound);
						if(combinationIndex!=10) 
							Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						else// agCompound of combination[10] is Sodium-Nitrate
							Compound.counts.set(countIndex, Compound.counts.get(countIndex)+2);
					}
					
					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					return true;
				}
			}

		}
		return false;

	}
	
	/******************************************************************
	 * FUNCTION : reactGeneric DESCRIPTION : Function for generic raction
	 * 
	 * INPUTS : simulation(Simulation) OUTPUTS: None
	 *******************************************************************/
	public boolean reactGeneric(Simulation simulation) {
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
				
				mNew.setRatioKE(1/simulation.getSpeed());
				if (mNew.getName().equals("Hydrogen")) {
					// TODO: Add max velocity restriction
				}
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
			int unit = p5Canvas.getUnit();
			int sim = p5Canvas.getSim();
			int set = p5Canvas.getSet();
			updateCompoundNumber(unit, sim, set);
			return true;
		}
		return false;
	}

	/******************************************************************
	 * FUNCTION : pickBottomOne DESCRIPTION : Pick a molecule which is under a
	 * reacted molecule Return reference of that picked molecule
	 * 
	 * INPUTS : source(ArrayList<Molecule>) OUTPUTS: Molecule
	 *******************************************************************/
	private Molecule pickRightBottomOne(Molecule source,
			ArrayList<Molecule> neighborMolecules) {
		// TODO Auto-generated method stub
		Molecule res = null;
		if (neighborMolecules.size() < 1)
			return res;
		else if (neighborMolecules.size() == 1)
			return neighborMolecules.get(0);
		else {
			float highY = 0;
			int highIndex = -1;
			Vec2 pos = new Vec2();
			Vec2 posSource = new Vec2(this.box2d.coordWorldToPixels(source
					.getPosition()));
			float y = 0;
			// Go through each molecule in source list and check their position
			// Write down the index of molecule which has lowest y
			for (int i = 0; i < neighborMolecules.size(); i++) {
				pos = this.box2d.coordWorldToPixels(neighborMolecules.get(i)
						.getPosition());
				// Check if this molecule is at right bottom of source molecule
				if (pos.x >= posSource.x && pos.y >= posSource.y) {
					// If it is, compare their y value
					y = pos.y;

					if (y > highY) {
						highY = y;
						highIndex = i;
					}
				}
			}
			res = neighborMolecules.get(highIndex);

		}

		return res;
	}

	/******************************************************************
	 * FUNCTION : compareDistance DESCRIPTION : Compare distances between source
	 * molecules and targets Return reference of one source molecule which is
	 * the closest to target
	 * 
	 * INPUTS : target(Molecule), source(ArrayList<Molecule>) OUTPUTS: Molecule
	 *******************************************************************/
	private Molecule compareDistance(Molecule target, ArrayList<Molecule> source) {
		Molecule res = null;
		if (source.size() < 1)
			return res;
		else if (source.size() == 1)
			return source.get(0);
		else {
			float minDistance = 10000;
			int minIndex = 0;
			float dis = 0;
			// Go through each molecule in source list and calculate their
			// distance from target
			// Write down the index of molecule which has minimum distance
			for (int i = 0; i < source.size(); i++) {
				dis = calculateDistance(target, source.get(i));
				if (dis < minDistance) {
					minDistance = dis;
					minIndex = i;
				}
			}
			res = source.get(minIndex);
		}

		return res;
	}

	private float calculateDistance(Molecule target, Molecule source) {
		float distance = 0;
		Vec2 posTarget = target.getPosition();
		Vec2 posSource = source.getPosition();
		float xDifference = posTarget.x - posSource.x;
		float yDifference = posTarget.y - posSource.y;
		distance = (float) Math.sqrt(xDifference * xDifference + yDifference
				* yDifference);

		return distance;
	}

	/******************************************************************
	 * FUNCTION : getReactionProducts DESCRIPTION : Reture objects based on
	 * input name Called by beginReaction
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getReactionProducts(ArrayList<String> reactants,
			Molecule m1, Molecule m2) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 1
		if (reactants.contains("Sodium") && reactants.contains("Chlorine")
				&& reactants.size() == 2) {

			products.add("Sodium-Chloride");

		}
		// Sim 1 set 2
		else if (reactants.get(0).equals("Hydrogen-Iodide")
				&& reactants.get(1).equals("Hydrogen-Iodide")
				&& reactants.size() == 2) {
			products.add("Hydrogen");
			products.add("Iodine");

		}
		// Sim 1 set 3
		else if (reactants.contains("Ethene") && reactants.contains("Oxygen")
				&& reactants.size() == 2) {
			
			float radius = 175;
			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
			ArrayList<Molecule> oxygen = new ArrayList<Molecule>();

			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if (molecules.get(i).getName().equals("Oxygen")
						&& molecules.get(i) != m1 && molecules.get(i) != m2) {
					Vec2 thirdMolecule = box2d.coordWorldToPixels(molecules
							.get(i).getPosition());
					if (radius > computeDistance(midpoint, thirdMolecule)) {
						oxygen.add(molecules.get(i));
						if(oxygen.size()>=2)
						break; // Break after we find one nearby
					}
				}
			}
			
			if(oxygen.size()==2)
			{
				products.add("Carbon-Dioxide");
				products.add("Carbon-Dioxide");
				products.add("Water");
				products.add("Water");
			// Need to kill the third and forth molecule
				p5Canvas.killingList.add(oxygen.get(0));
				p5Canvas.killingList.add(oxygen.get(1));

			}

		}
		// Sim 1 set 4
		else if (reactants.contains("Copper")
				&& reactants.contains("Silver-Ion") && reactants.size() == 2) {

			float radius = 125;
			
			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);

			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if (molecules.get(i).getName().equals("Silver-Ion")
						&& molecules.get(i) != m1 && molecules.get(i) != m2) {
					Vec2 thirdMolecule = box2d.coordWorldToPixels(molecules
							.get(i).getPosition());
					if (radius > computeDistance(midpoint, thirdMolecule)) {
						products.add("Copper-II");
						products.add("Silver");
						products.add("Silver");
						// Need to kill the third molecule
						p5Canvas.killingList.add(molecules.get(i));
						break; // Break after we find one nearby
					}
				}
			}
		}
		// Sim1 set 5
		else if (reactants.contains("Methane") && reactants.contains("Oxygen")) {
			float radius = 175;

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
		// Sim 1 set 6
		else if (reactants.contains("Copper-II") && reactants.contains("Iron")) {
			products.add("Iron-II");
			products.add("Copper");
		}
		// Sim 1 set 7
		else if (reactants.contains("Hydrogen-Ion")
				&& reactants.contains("Lithium-Sulfide")) {
			float radius = 200;
			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
			ArrayList<Molecule> chlorides = new ArrayList<Molecule>();
			ArrayList<Molecule> hydrogens = new ArrayList<Molecule>();
			if (m1.getName().equals("Hydrogen-Ion"))
				hydrogens.add(m1);
			else
				hydrogens.add(m2);
			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if (hydrogens.size() < 2) {
					if (molecules.get(i).getName().equals("Hydrogen-Ion")
							&& molecules.get(i) != m1 && molecules.get(i) != m2) {
						Vec2 loc = box2d.coordWorldToPixels(molecules.get(i)
								.getPosition());
						if (radius > computeDistance(midpoint, loc)) {
							hydrogens.add(molecules.get(i));
						}
					}
				}
				if (chlorides.size() < 2) {
					if (molecules.get(i).getName().equals("Chloride")
							&& molecules.get(i) != m1 && molecules.get(i) != m2) {
						Vec2 loc = box2d.coordWorldToPixels(molecules.get(i)
								.getPosition());
						if (radius > computeDistance(midpoint, loc)) {
							chlorides.add(molecules.get(i));
						}
					}
				}
				if (chlorides.size() == 2 && hydrogens.size() == 2) // We got
																	// enough
																	// molecules
				{
					break;
				}
			}
			if (chlorides.size() == 2 && hydrogens.size() == 2) // We got enough
																// molecules
			{
				products.add("Lithium-Ion");
				products.add("Lithium-Ion");
				products.add("Hydrogen-Sulfide");
				// Kill other 2 hydrogen-Ions
				// p5Canvas.killingList.add(chlorides.get(1));
				p5Canvas.killingList.add(hydrogens.get(1));
			}
		}
		// Sim 1 set 8
		else if (reactants.contains("Hydrogen")
				&& reactants.contains("Chlorine") && reactants.size() == 2) {
			products.add("Hydrochloric-Acid");
			products.add("Hydrochloric-Acid");

		}

		// Sim 1 set 9
		else if (reactants.get(0).equals("Hydrogen-Peroxide")
				&& reactants.get(1).equals("Hydrogen-Peroxide")
				&& reactants.size() == 2) {
			products.add("Oxygen");
			products.add("Water");
			products.add("Water");

		}
		// Sim 1 set 10
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Chloride")) {
			products.add("Silver-Chloride");
		} 
	
		
		//Sim 2 set 1 Ag+ + Br- = AgBr(s)
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Bromine-Ion")) {
			products.add("Silver-Bromide");
		}
		//Sim 2 set 1 Ag+ + Cl- = AgCl(s)
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Chloride")) {
			products.add("Silver-Chloride");
		}
		//Sim 2 set 1 2Ag+ + CO3 -2 = Ag2CO3(s)
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Carbonate")) {
			float radius = 125;
			// Compute midpoint of collision molecules
			Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
			Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());
			Vec2 midpoint = new Vec2((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);

			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {

				if (molecules.get(i).getName().equals("Silver-Ion")
						&& molecules.get(i) != m1 && molecules.get(i) != m2) {
					Vec2 thirdMolecule = box2d.coordWorldToPixels(molecules
							.get(i).getPosition());
					if (radius > computeDistance(midpoint, thirdMolecule)) {
						products.add("Silver-Carbonate");
						// Need to kill the third molecule
						p5Canvas.killingList.add(molecules.get(i));
						break; // Break after we find one nearby
					}
				}
			}
			
		}
		//Sim 2 set 1 Ag+ + OH- = AgOH(s)
		else if (reactants.contains("Silver-Ion")
				&& reactants.contains("Hydroxide")) {
			products.add("Silver-Hydroxide");
		}

		else {
			return null;
		}
		return products;
	}

	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}

	// Compute Distance in pixel between molecule m1 and m2
	private float computeDistance(Molecule m1, Molecule m2) {
		float dis = 0;
		Vec2 v1 = box2d.coordWorldToPixels(m1.getPosition());
		Vec2 v2 = box2d.coordWorldToPixels(m2.getPosition());

		dis = computeDistance(v1, v2);

		return dis;

	}

	/******************************************************************
	 * FUNCTION : getDissolutionProducts DESCRIPTION : Return elements of
	 * reactants
	 * 
	 * INPUTS : reactants (Array<String>) OUTPUTS: None
	 *******************************************************************/
	private ArrayList<String> getDissolutionProducts(ArrayList<String> collider) {
		ArrayList<String> products = new ArrayList<String>();
		// Sim 1 set 4, set 10 and sim 2 AgNO3
		if (collider.contains("Silver-Nitrate")) {
			products.add("Silver-Ion");
			products.add("Nitrate");
		}
		// Sim 1 set 6
		else if (collider.contains("Copper-II-Sulfate")) {
			products.add("Copper-II");
			products.add("Sulfate");
		}
		// Sim 1 set 10
		else if (collider.contains("Sodium-Chloride")) {
			products.add("Sodium-Ion");
			products.add("Chloride");
		}
		
		//Sim 2 KBr
		else if (collider.contains("Potassium-Bromide"))
		{
			products.add("Potassium-Ion");
			products.add("Bromine-Ion");
		}

		// Sim 2 NH4Cl
		else if (collider.contains("Ammonium-Chloride")) {
			products.add("Ammonium");
			products.add("Chloride");
		}
		// Sim 2 Na2CO3
		else if (collider.contains("Sodium-Carbonate")) {
			products.add("Sodium-Ion");
			products.add("Sodium-Ion");
			products.add("Carbonate");
		}
		// Sim 2 NaOH
		else if (collider.contains("Sodium-Hydroxide")) {
			products.add("Sodium-Ion");
			products.add("Hydroxide");
		}
		// Sim 2 LiNO3
		else if (collider.contains("Lithium-Nitrate")) {
			products.add("Lithium-Ion");
			products.add("Nitrate");
		} else {
			// return null;
		}
		return products;

	}

	@Override
	protected void computeForce(int sim, int set) {
		// Set computeForce trigger interval
		// This function is triggered every 10(computeTriggerInterval) frame

		if (sim == 1) {
			switch (set) {
			case 1:
				if (frameCounter >= this.computeTriggerInterval)
					frameCounter = 0;
				if (frameCounter == 0) {
					clearAllMoleculeForce();
					computeForceNaCl();
				}
				break;
			case 2:
				break;
			case 3:
				clearAllMoleculeForce();
				computeForceEtheneOxygen();
				break;
			case 4:
				clearAllMoleculeForce();
				computeForceAgCopper();
				break;
			case 5:
				break;
			case 6:
				clearAllMoleculeForce();
				computeForceCopperIron();
				break;
			case 7:
				clearAllMoleculeForce();
				computeForceLi2S();
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				clearAllMoleculeForce();
				computeForceSilverChloride();
				break;

			}
		} else if (sim == 2) {
			clearAllMoleculeForce();
			computeForceSolubility();
		}

	}


	//Compute force funciton for Sim 1 Set 1
	private void computeForceNaCl() {
		Molecule mole = null;
		Vec2 force = new Vec2();
		Random randX = new Random();
		Random randY = new Random();
		float scale = 2.0f; // How strong the force is

		float randXValue = 0;
		float randYValue = 0;
		boolean randXDir = false;
		boolean randYDir = false;

		for (int i = 0; i < molecules.size(); i++) {
			if (!molecules.get(i).getName().equals("Chlorine")) // Only compute
																// force for
																// solid
			{
				randXValue = randX.nextFloat() * scale;
				randYValue = randY.nextFloat() * scale;
				randXDir = randX.nextBoolean();
				randXValue *= (float) (randXDir ? 1 : -1);
				randYDir = randY.nextBoolean();
				randYValue *= (float) (randYDir ? 1 : -1);
				mole = molecules.get(i);
				for (int e = 0; e < mole.getNumElement(); e++) {

					mole.sumForceX[e] = randXValue;
					mole.sumForceY[e] = randYValue;
				}
			}
		}

	}
	
	//Force computation for Sim 1 Set 3
	private void computeForceEtheneOxygen(){
		float attractForce = 0.3f;
		Molecule lastEthene = null;
		int etheneNum = 0;
		Molecule otherMole = null;
		Vec2 thisLoc = null;
		Vec2 otherLoc = null;
		float xValue = 0;
		float yValue =0;
		float dis =  0;
		float forceX =0;
		float forceY =0;
		
		ArrayList<Molecule> oxygenList = new ArrayList<Molecule>();
		for(int i = 0;i<State.molecules.size();i++)
		{
			if(State.molecules.get(i).getName().equals("Ethene"))
			{
				lastEthene= State.molecules.get(i);
				etheneNum++;
			}
			else if(State.molecules.get(i).getName().equals("Oxygen"))
			{
				oxygenList.add(State.molecules.get(i));
			}
		}
		if( etheneNum==1 ) //If there is only one ethene left, pull it with oxygen together
		{
			for(int thisE =0;thisE<lastEthene.getNumElement();thisE++)
			{
				thisLoc = new Vec2 (lastEthene.getElementLocation(thisE));

				for (int k = 0; k < oxygenList.size(); k++) { 
					otherMole = oxygenList.get(k);
					for (int otherE = 0; otherE < otherMole.getNumElement(); otherE++)
					{
						otherMole.sumForceX[otherE] = 0;
						otherMole.sumForceY[otherE] = 0;
					otherLoc= new Vec2 (otherMole.getElementLocation(otherE));
					if (thisLoc == null || otherLoc == null)
					continue;
					xValue = thisLoc.x - otherLoc.x;
					yValue = thisLoc.y - otherLoc.y;
					dis = (float) Math.sqrt(xValue * xValue + yValue
					* yValue);
					forceX = (float) (xValue / dis) * attractForce;
					forceY = (float) (yValue / dis) * attractForce;
					otherMole.sumForceX[otherE] += forceX;
					otherMole.sumForceY[otherE] += forceY;
					}
					
				}
			}
		}
	}

	// Foce computation for sim 1 set 4
	private void computeForceAgCopper() {
		Molecule mole = null;
		float scale = 0.2f; // How strong the force is
		float repulsiveForce = 1.5f;
		float forceYCompensation = 0.20f;
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;
		float sulfateforceYCompensation =0.005f;

		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);
		float topBoundary = p5Canvas.h/2;
		int silverIonNum = State.getMoleculeNumByName("Silver-Ion");
		int silverNitrateNum = State.getMoleculeNumByName("Silver-Nitrate");

		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i).getName().equals("Silver-Ion")) // Compute
																	// force for
																	// silver-ion,
																	// in order
																	// to
																	// attract
																	// them to
																	// copper
			{

				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Copper")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY
									+ forceYCompensation;
							if(silverIonNum<=2 && silverNitrateNum==0)
							{
								mole.sumForceX[thisE] += forceX*2;
								mole.sumForceY[thisE] += (forceY
										+ forceYCompensation)*2;
							}

						}
					}

				}
			} else if (molecules.get(i).getName().equals("Copper-II")||
					molecules.get(i).getName().equals("Nitrate")) // Compute
																		// force
																		// for
																		// copper-II,
																		// in
																		// order
																		// to
																		// push
																		// them
																		// away
																		// from
																		// silver
			{
				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Silver")
								|| m.getName().equals("Copper")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = thisLoc.x - otherLoc.x;
							yValue = thisLoc.y - otherLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) ((xValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
							forceY = (float) ((yValue / dis) * (repulsiveForce/Math.pow(dis, 2)));

							
							//mole.sumForceX[thisE] += forceX;
							if(forceY<0)
								forceY*=-1;
							mole.sumForceY[thisE] += forceY;
							if(m.getName().equals("Nitrate"))
								mole.sumForceY[thisE]+=sulfateforceYCompensation; 

							
						}
						
					}

				}

			}
			// Check positions of all the molecules, in case they are not going
			// to high
			if (true) {
				mole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());
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

	// Foce computation form sim 1 set 6
	private void computeForceCopperIron() {
		Molecule mole = null;

		float scale = 0.15f; // How strong the force is
		float repulsiveForce = 1.5f;
		float forceYCompensation = 0.05f;
		float sulfateforceYCompensation =0.005f;
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;


		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;

		Vec2 thisLoc = new Vec2(0, 0);
		Vec2 otherLoc = new Vec2(0, 0);
		
		float topBoundary = p5Canvas.h/2;

		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i).getName().equals("Copper-II")) // Compute force
																// for
																// copper-ion,
																// in order to
																// attract them
																// to copper
			{

				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Iron")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += forceY
									+ forceYCompensation;

						}
					}

				}
			} 
			//Separate Iron-II 
			else if (molecules.get(i).getName().equals("Iron-II")||molecules.get(i).getName().equals("Sulfate")) 
			{
				mole = molecules.get(i);
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																				// element

					thisLoc.set(mole.getElementLocation(thisE));
					mole.sumForceX[thisE] = 0;
					mole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Copper")
								|| m.getName().equals("Iron")) {
							for (int otherE = 0; otherE < m.getNumElement(); otherE++)
								otherLoc.set(m.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = thisLoc.x - otherLoc.x;
							yValue = thisLoc.y - otherLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) ((xValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
							forceY = (float) ((yValue / dis) * (repulsiveForce/Math.pow(dis, 2)));

							//mole.sumForceX[thisE] += forceX;
							mole.sumForceY[thisE] += sulfateforceYCompensation+forceY;

						}
					}

				}

			}
			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				mole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());
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

	// Compute force for sim 1 set 10
	private void computeForceSilverChloride() {
		Molecule thisMole = null;
		Molecule otherMole = null;
		Vec2 thisLoc = new Vec2();
		Vec2 otherLoc = new Vec2();
		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.15f;
		float chlorideScale = 0.8f;
		float forceYCompensation = 0.05f;
		float gravityCompensation = 0.4f;
		float topBoundary = p5Canvas.h / 2;
		float gravityScale = 0.01f;
		float repulsiveForce = 1.5f;


		for (int i = 0; i < molecules.size(); i++) {
			//Attract silver-Ion to chloride
			if (molecules.get(i).getName().equals("Silver-Ion")) 
			{

				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																					// element

					thisLoc.set(thisMole.getElementLocation(thisE));
					thisMole.sumForceX[thisE] = 0;
					thisMole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
																	// forces
																	// from
																	// other
																	// molecules
						if (k == i)
							continue;
						otherMole = molecules.get(k);
						if (otherMole.getName().equals("Chloride")) 
						{
							for (int otherE = 0; otherE < otherMole
									.getNumElement(); otherE++)
								otherLoc.set(otherMole
										.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale*1.5f;
							forceY = (float) (yValue / dis) * scale*1.5f;

							// Add attraction force to sodium-Ion
							thisMole.sumForceX[thisE] += forceX;
							thisMole.sumForceY[thisE] += forceY*4
									+ forceYCompensation*6.0f;
							// At the same time add attraction force to Chloride
							// In this case, number of both Silver and Chloride
							// elements are 1;
							otherMole.sumForceX[thisE] += forceX * (-1.5f)
									* chlorideScale;
							otherMole.sumForceY[thisE] += forceY
									* (-2.0f) * chlorideScale+ forceYCompensation*3.0f;

						}
					}

				}
			}
			// If there are some Silver-chloride have been created
			// Pull them together
			else if (molecules.get(i).getName().equals("Silver-Chloride")) {
				float silverChlorideScale = 8f;
				thisMole = molecules.get(i);

				for (int e = 0; e < thisMole.getNumElement(); e++) {
					int indexCharge = thisMole.elementCharges.get(e);
					Vec2 locIndex = thisMole.getElementLocation(e);
					thisMole.sumForceWaterX[e] = 0;
					thisMole.sumForceWaterY[e] = 0;
					thisMole.sumForceX[e] = 0;
					thisMole.sumForceY[e] = 0;
					for (int k = 0; k < molecules.size(); k++) {
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals("Silver-Chloride")) // Find
																	// another
																	// silver-chloride
						{
							for (int e2 = 0; e2 < m.getNumElement(); e2++) {
								Vec2 loc = m.getElementLocation(e2);
								float x = locIndex.x - loc.x;
								float y = locIndex.y - loc.y;
								dis = (float) Math.sqrt(x * x + y * y);
								forceX = (x / dis) / (float) (Math.sqrt(dis))
										* silverChlorideScale;
								forceY = (y / dis) / (float) (Math.sqrt(dis))
										* silverChlorideScale;

								int charge = m.elementCharges.get(e2);
								int mul = charge * indexCharge;
								// If mul>0 replusive force
								// If mul<0 attractive force
								thisMole.sumForceX[e] += mul * forceX;
								thisMole.sumForceY[e] += mul * forceY;

							}
						}
					}
				}
			}
			//separate sodium-Ions
			else if (molecules.get(i).getName().equals("Sodium-Ion"))
				{
					thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
										// element
				
					thisLoc.set(thisMole.getElementLocation(thisE));
					thisMole.sumForceX[thisE] = 0;
					thisMole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
								// forces
								// from
								// other
								// molecules
					if (k == i)
						continue;
					Molecule m = molecules.get(k);
					if (m.getName().equals("Sodium-Ion")) {
					for (int otherE = 0; otherE < m.getNumElement(); otherE++)
						otherLoc.set(m.getElementLocation(otherE)); //Only one element
					
						if (thisLoc == null || otherLoc == null)
							continue;
						xValue = thisLoc.x - otherLoc.x;
						yValue = thisLoc.y - otherLoc.y;
						dis = (float) Math.sqrt(xValue * xValue + yValue
						* yValue);
						forceX = (float) ((xValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
						forceY = (float) ((yValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
						
						thisMole.sumForceX[thisE] += forceX;
						thisMole.sumForceY[thisE] += forceYCompensation*0.5f+forceY;
					
					
					}
				}
				
				}
				
				}
			//Make Nitrate floating 
			else if(molecules.get(i).getName().equals("Silver-Nitrate"))
			{
				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++)
				{
					thisMole.sumForceY[thisE] += forceYCompensation*5f;
				}

			}
			else if(molecules.get(i).getName().equals("Nitrate"))
			{
				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++)
				{
					thisMole.sumForceY[thisE] += forceYCompensation*2f;
				}

			}
			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				thisMole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());
				if (pos.y < topBoundary) {
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																						// element

						thisMole.sumForceX[thisE] += 0;
						thisMole.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;

					}
				}
			}
		}
	}

	// Compute force for set 1 sim 7
	private void computeForceLi2S() {
		Molecule thisMole = null;
		float topBoundary = p5Canvas.y + p5Canvas.h / 2;
		float gasBotBoundary = p5Canvas.h/3;
		float gravityCompensation = 0.3f;
		float gravityScale =0.01f;

		for (int i = 0; i < State.molecules.size(); i++) {

			// Check position of other molecules, in case they are not going too
			// high
			if (true) {
				thisMole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());

				if(!thisMole.getName().equals("Hydrogen-Sulfide"))
				{
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																							// element
	
							thisMole.sumForceX[thisE] = 0;
							thisMole.sumForceY[thisE] = (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
							
						}
					}
				}
				else //Add up force to Hydrogen-Sulfide
				{
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) {
					
					thisMole.sumForceX[thisE] = 0;
					thisMole.sumForceY[thisE] = gravityCompensation;
					Vec2 velocity = thisMole.getLinearVelocity();
					//System.out.println("velocity is "+velocity);
					if(pos.y > gasBotBoundary && velocity.y<0)
						thisMole.sumForceY[thisE] = (gravityCompensation+ gravityScale*(pos.y-gasBotBoundary));
					}
				}
			}
		} // End loop

	}

	// Compute force for sim 2
	private void computeForceSolubility() {
		Molecule thisMole = null;
		Molecule otherMole = null;
		Vec2 thisLoc = new Vec2();
		Vec2 otherLoc = new Vec2();
		float xValue = 0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.15f;
		float thisForceScale = 0.8f;
		float otherForceScale = 1.2f;
		float gravityCompensation = 0.2f;
		float topBoundary = p5Canvas.h / 2;
		float forceYCompensation = 2f;
		float forceSodiumYCompensation = 0.1f;				
		float attractForceScale = 15f;

		String [] targetNames = setupForceTargets();
		

		for (int i = 0; i < molecules.size(); i++) {
			

			// Compute force for Ion, in order to attract them to molecules
			if (molecules.get(i).getName().equals(targetNames[0])) 
			{

				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
					// element

					thisLoc.set(thisMole.getElementLocation(thisE));
					thisMole.sumForceX[thisE] = 0;
					thisMole.sumForceY[thisE] = 0;
					for (int k = 0; k < molecules.size(); k++) { // Go check
						if (k == i)
							continue;
						otherMole = molecules.get(k);
						if (otherMole.getName().equals(targetNames[1])) // We are
						// looking
						// for
						// chloride
						{
							for (int otherE = 0; otherE < otherMole
									.getNumElement(); otherE++)
								otherLoc.set(otherMole
										.getElementLocation(otherE));
							if (thisLoc == null || otherLoc == null)
								continue;
							xValue = otherLoc.x - thisLoc.x;
							yValue = otherLoc.y - thisLoc.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;

							// Add attraction force to thisMolecule
							thisMole.sumForceX[thisE] += forceX* thisForceScale;
							thisMole.sumForceY[thisE] += forceY
									* thisForceScale + forceYCompensation*thisMole.getBodyMass();
							
							// At the same time add attraction force to other molecule
							otherMole.sumForceX[thisE] += forceX * (-1)*otherForceScale
									;
							otherMole.sumForceY[thisE] += (forceY )
									* (-1)*otherForceScale + forceYCompensation*otherMole.getBodyMass();

						}
					}

				}
			}
			//Atract force 
			// Pull solid molecules together
			if ( molecules.get(i).getName().equals("Silver-Bromide")
					||molecules.get(i).getName().equals("Silver-Chloride")||molecules.get(i).getName().equals("Silver-Carbonate")
					||molecules.get(i).getName().equals("Silver-Hydroxide")) {
				thisMole = molecules.get(i);
				String thisName = new String(thisMole.getName());

				for (int e = 0; e < thisMole.getNumElement(); e++) {
					int indexCharge = thisMole.elementCharges.get(e);
					Vec2 locIndex = thisMole.getElementLocation(e);
					thisMole.sumForceWaterX[e] = 0;
					thisMole.sumForceWaterY[e] = 0;
					thisMole.sumForceX[e] = 0;
					thisMole.sumForceY[e] = 0;
					for (int k = 0; k < molecules.size(); k++) {
						if (k == i)
							continue;
						Molecule m = molecules.get(k);
						if (m.getName().equals(thisName)) // Find
																	// another
																	// silver-chloride
						{
							for (int e2 = 0; e2 < m.getNumElement(); e2++) {
								Vec2 loc = m.getElementLocation(e2);
								float x = locIndex.x - loc.x;
								float y = locIndex.y - loc.y;
								dis = (float) Math.sqrt(x * x + y * y);
								forceX = (x / dis) / (float) (Math.sqrt(dis))
										* attractForceScale;
								forceY = (y / dis) / (float) (Math.sqrt(dis))
										* attractForceScale;

								int charge = m.elementCharges.get(e2);
								int mul = charge * indexCharge;
								// If mul>0 replusive force
								// If mul<0 attractive force
								thisMole.sumForceX[e] += mul * forceX;
								thisMole.sumForceY[e] += mul * forceY;

							}
						}
					}
				}
			}
			//Add some y force to Sodium-Ion to make it floating
			if(molecules.get(i).getName().equals("Sodium-Ion")) 
			{
				thisMole = molecules.get(i);
				for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) {
					thisMole.sumForceY[thisE] += forceSodiumYCompensation;
					}
			}
			// high boundary check
			if (true) {
				thisMole = molecules.get(i);
				Vec2 pos = box2d.coordWorldToPixels(thisMole.getPosition());
				if (pos.y < topBoundary) {
					for (int thisE = 0; thisE < thisMole.getNumElement(); thisE++) { // Select
																						// element

						thisMole.sumForceX[thisE] += 0;
						thisMole.sumForceY[thisE] += (gravityCompensation) * -1*Math.sqrt(topBoundary- pos.y);

					}
				}
			}
		}
	}
	
	//Set up the two compounds between which force applies
	private String [] setupForceTargets()
	{
		String [] targets = new String[2];
		switch(combinationIndex)
		{
		case 5:
			targets[0] = new String("Silver-Ion");
			targets[1]= new String("Bromine-Ion");
			break;
		case 9:
			targets[0] = new String("Silver-Ion");
			targets[1]= new String("Chloride");
			break;
		case 10:
			targets[0] = new String("Silver-Ion");
			targets[1]= new String("Carbonate");
			break;
		case 11:
			targets[0] = new String("Silver-Ion");
			targets[1]= new String("Hydroxide");
			break;
			
		}
		return targets;
	}

	@Override
	protected void applyForce(int sim, int set) {
		if (sim == 1) {
			switch (set) {
			case 1:
				if (frameCounter >= this.computeTriggerInterval)
					frameCounter = 0;

				if (frameCounter == 0) {

					super.applyForce(sim, set);
				}

				break;
			case 2:
				break;
			case 3:
				super.applyForce(sim, set);
				break;
			case 4:
				super.applyForce(sim, set);
				break;
			case 5:
				break;
			case 6:
				super.applyForce(sim, set);
				break;
			case 7:
				super.applyForce(sim, set);
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				super.applyForce(sim, set);
				break;

			}
		} else if (sim == 2) {
			super.applyForce(sim, set);
		}

		this.frameCounter++;
		// System.out.println("Apply force "+this.frameCounter);

	}

	@Override
	protected void reset() {

		//Reset parameters
		this.frameCounter = 0;
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		this.isAnchorSetup = false;
		setupSimulations();
		this.moleculeMassHash.clear();
		
		setupSpeed();

		//Customization
		if (sim == 1 && set == 2) {
			p5Canvas.getMain().heatSlider.setValue(185);
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && set == 9) {
			p5Canvas.getMain().heatSlider.setValue(160);
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && (set == 1 || set == 3 || set == 5 || set == 8)) {
			box2d.setGravity(0f, 0f);
		} else if (sim == 1 && set == 10) {
			p5Canvas.getMain().heatSlider.setValue(50);
		}

		// Add particular reaction into Compound global parameter
		
		//Initiate Molecule mass output
		updateMoleculeMass();
	}
	
	private void setupSpeed() {
		String name = null;
		Molecule mole = null;
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float speed = 1.0f;		
		
		if(sim ==1 )
		{
			
			switch(set)
			{
			default:
				speed = 1;
				getSimulation(sim, set).setSpeed(speed);
				break;
			case 1:
				speed = 8;
				getSimulation(sim, set).setSpeed(speed);
				break;
			case 2:
				speed = 12;
				getSimulation(sim, set).setSpeed(speed);
				break;
			case 3:
				speed = 8;
				getSimulation(sim, set).setSpeed(speed);
				break;				
			case 5:
				speed = 8;
				getSimulation(sim, set).setSpeed(speed);
				break;
			case 8:
				speed = 4;
				getSimulation(sim, set).setSpeed(speed);
				break;
			case 9:
				speed = 8;
				getSimulation(sim, set).setSpeed(speed);
				break;
			}
		}
		else if( sim ==2)
		{
			speed = 1;
			getSimulation(sim, set).setSpeed(speed);
		}
	}

	//Set Combination index for Unit3 Sim 2
	public boolean setCombination(ArrayList<String> compoundNames) {
		// TODO Auto-generated method stub
		boolean res = false;
		
		if(p5Canvas.getUnit()==3 && p5Canvas.getSim()==2)
		{
			for(int i=0;i<combination.length;i++)
			{
				List<String> com = Arrays.asList(combination[i]);
				if(compoundNames.containsAll(com))
				{
					combinationIndex =i;
					res = true;
					break;
				}
			}
		}
		
		return res;
	}
	
	//Set up combinations for Unit3 Sim2
	private void setupCombination()
	{
		for(int i =0;i<combination.length;i++)
			combination[i] = new String[2];
		combination[0][0]= new String("Lithium-Nitrate");
		combination[0][1]= new String("Potassium-Bromide");
		
		combination[1][0]= new String("Lithium-Nitrate");
		combination[1][1]= new String("Silver-Nitrate");
		
		combination[2][0]= new String("Lithium-Nitrate");
		combination[2][1]= new String("Ammonium-Chloride");
		
		combination[3][0]= new String("Lithium-Nitrate");
		combination[3][1]= new String("Sodium-Carbonate");
		
		combination[4][0]= new String("Lithium-Nitrate");
		combination[4][1]= new String("Sodium-Hydroxide");
		
		combination[5][0]= new String("Potassium-Bromide");
		combination[5][1]= new String("Silver-Nitrate");
		
		combination[6][0]= new String("Potassium-Bromide");
		combination[6][1]= new String("Ammonium-Chloride");
		
		combination[7][0]= new String("Potassium-Bromide");
		combination[7][1]= new String("Sodium-Carbonate");
		
		combination[8][0]= new String("Potassium-Bromide");
		combination[8][1]= new String("Sodium-Hydroxide");
		
		combination[9][0]= new String("Silver-Nitrate");
		combination[9][1]= new String("Ammonium-Chloride");
		
		combination[10][0]= new String("Silver-Nitrate");
		combination[10][1]= new String("Sodium-Carbonate");
		
		combination[11][0]= new String("Silver-Nitrate");
		combination[11][1]= new String("Sodium-Hydroxide");
		
		combination[12][0]= new String("Ammonium-Chloride");
		combination[12][1]= new String("Sodium-Carbonate");
		
		combination[13][0]= new String("Ammonium-Chloride");
		combination[13][1]= new String("Sodium-Hydroxide");
		
		combination[14][0]= new String("Sodium-Carbonate");
		combination[14][1]= new String("Sodium-Hydroxide");
		

	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		 
				ArrayList<String> products = new ArrayList<String>();
				if (!(sim == 2)) {
					products = DBinterface.getReactionOutputs(this.unitNum,
							sim, set);
					if (products != null) {
						for (String s : products) {
							if (!Compound.names.contains(s)) {
								Compound.names.add(s);
								Compound.counts.add(0);
								Compound.caps.add(95);
							}
						}
					}
					if(set==10)  //Sim 1 set 10
					{
						Compound.names.add("Sodium-Ion");
						Compound.counts.add(0);
						Compound.caps.add(20);
						Compound.names.add("Chloride");
						Compound.counts.add(0);
						Compound.caps.add(20);
					}
				}
				else //Set raction outcome for Unit3 sim 2
				{	
					switch(this.combinationIndex)
					{
					case 5:
						Compound.names.add("Silver-Bromide");
						Compound.counts.add(0);
						Compound.caps.add(95);
						Compound.names.add("Potassium-Nitrate");
						Compound.counts.add(0);
						Compound.caps.add(95);
						break;
					case 9:
						Compound.names.add("Silver-Chloride");
						Compound.counts.add(0);
						Compound.caps.add(95);
						Compound.names.add("Ammonium-Nitrate");
						Compound.counts.add(0);
						Compound.caps.add(95);
						break;
					case 10:
						Compound.names.add("Silver-Carbonate");
						Compound.counts.add(0);
						Compound.caps.add(95);
						Compound.names.add("Sodium-Nitrate");
						Compound.counts.add(0);
						Compound.caps.add(95);
						break;
					case 11:
						Compound.names.add("Silver-Hydroxide");
						Compound.counts.add(0);
						Compound.caps.add(95);
						Compound.names.add("Sodium-Nitrate");
						Compound.counts.add(0);
						Compound.caps.add(95);
						break;
					}
				}
			
		
	}
	
	public void updateMoleculeMass()
	{
		//String name = null;
		float mole = 0;
		float moleculeWeight = 0;
		float count = 0;
		float mass = 0;
		for (int i =0;i<Compound.names.size();i++) {
			//name = new String(Compound.names.get(i));
			//Special cases

			//General case
			{
			//mole = (float) Compound.counts.get(i) / numMoleculePerMole;
				count = Compound.counts.get(i) ;
				mole = (float)count/numMoleculePerMole;
				moleculeWeight = Compound.moleculeWeight.get(i);
				mass = moleculeWeight* count;
			}
			moleculeMassHash.put(Compound.names.get(i), mass);
		}
	}
	
	public float getMassByName(String s) {
		if (moleculeMassHash.containsKey(s))
			return moleculeMassHash.get(s);
		else
			return 0;
	}
	
	
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		String name = (String)Compound.names.get(indexOfCompound);
		return getMassByName(name);

	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		return super.getDataTableView(sim, set, indexOfCompound);
	}
	
	
	//Function to return the correct compound name on the 2nd column of TableView
	public ArrayList<String> getNameTableView(int sim, int set)
	{
		ArrayList<String> res = super.getNameTableView(sim, set);
		
		if(sim==1)
		{
			switch(set)
			{
			case 8:
				int index = res.indexOf("Hydrochloric Acid");
				if(index>=0 && index<res.size())
				{
					res.set(index, "Hydrogen Chloride");
				}
				break;
				default:
					break;
			}
		}
		
		return res;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public void updateOutput(int sim, int set) {

		updateMoleculeMass();
	}

	@Override
	protected void initializeSimulation(int sim, int set) {
		this.updateMoleculeMass()	;	
	}

	@Override
	public void updateMoleculeCountRelated(int sim, int set) {

			updateMoleculeMass();
	}

	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
