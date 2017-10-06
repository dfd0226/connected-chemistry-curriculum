/**
 * 
 */
package simulations;

import static data.State.molecules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import main.Main;
import main.TableView;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import Util.Integrator;

import data.DBinterface;
import data.State;

import simulations.models.*;
import simulations.models.Simulation.SpawnStyle;

import java.util.Random;

import javax.swing.JPanel;

/**
 * @author Qin Li UnitBase class is base class of all Units class. Some simple
 *         functions implemented in this class
 * 
 */
public abstract class UnitBase {

	protected int num_total = 0;
	protected int num_dissolved = 0; // Number of molecules that has dissolved
	protected int numWater = 0; // Number of water added to container
	protected float massDissolved = 0;
	protected int water100mL = 25;
	protected int mToMass = 10;

	protected P5Canvas p5Canvas;
	protected PBox2D box2d;
	protected final float SODIUM_JOINT_FREQUENCY = 5;
	protected final int SIMULATION_NUMBER = 25;
	protected Simulation[] simulations;
	protected int unitNum;
	
	protected float lastVolume;
	protected int lastMole;
	protected float lastTemp;
	protected Integrator interpolator;
	public float volumeMagnifier =0f;
	protected final int celsiusToK= 273;



	public UnitBase(P5Canvas parent, PBox2D box) {
		p5Canvas = parent;
		box2d = box;
		interpolator = new Integrator(p5Canvas.temp);
		interpolator.setInterpolating(true);
	}

	// Setup parameters for all simulations in this unit
	protected abstract void setupSimulations();

	// Set up reaction products for graph showing
	public abstract void setupReactionProducts(int sim, int set);

	// Used to check in every frame if there is new molecule spawned and old
	// ones need to be killed
	public abstract void updateMolecules(int sim, int set);

	public abstract void initialize();

	// Reset all parameter to initial states
	protected abstract void reset();
	
	protected abstract void initializeSimulation(int sim,int set);
	
	//Update output labels while running
	public abstract void updateOutput(int sim,int set);
	
	//Update output text on right panel
	public void resetDashboard(int sim, int set)
	{
		JPanel dashboard = p5Canvas.getMain().dashboard;
		dashboard.add(p5Canvas.getMain().lblElapsedTimeText, "flowx,cell 0 0,alignx right");
		dashboard.add(p5Canvas.getMain().elapsedTime, "cell 1 0");
	}
	


	// Compute force for molecules, not necessary for all units
	protected abstract void computeForce(int sim, int set);
	
	protected void clearAllMoleculeForce() {
		for (Molecule mole : State.molecules) {
			mole.clearForce();
		}
	}

	// Find particular simulation object based on its sim and set number
	public Simulation getSimulation(int sim, int set) {
		Simulation res = null;
		for (int i = 0; i < simulations.length; i++) {
			if (simulations[i] != null) {
				if (simulations[i].getSimNum() == sim
						&& simulations[i].getSetNum() == set) // Find wanted
																// simulation
				{
					res = simulations[i];
					return res;
				}
			}
		}
		return res;
	}

	// Apply computed force to molecules
	protected void applyForce(int sim, int set) {
		for (int i = 0; i < molecules.size(); i++) {
			Molecule mole = molecules.get(i);
			if (mole != null && !p5Canvas.isDragging()) {

				for (int e = 0; e < mole.getNumElement(); e++) {
					mole.addForce(
							new Vec2(mole.sumForceX[e], mole.sumForceY[e]), e);
					mole.addForce(new Vec2(mole.sumForceWaterX[e],
							mole.sumForceWaterY[e]), e);

				}

			}

		}

	}

	// protected SpawnStyle getSpawnStyle(int selectedSim, int selectedSet);

	/******************************************************************
	 * FUNCTION : addMolecules DESCRIPTION : Function to add molecules to
	 * PApplet, vary in different Unit
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public abstract boolean addMolecules(boolean isAppEnable,
			String compoundName, int count);

	/******************************************************************
	 * FUNCTION : addWaterMolecules DESCRIPTION : Function to add water
	 * molecules to PApplet Only do space check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addWaterMolecules(boolean isAppEnable, String compoundName,
			int count) {
		boolean res = true;

		int dimension = 0; // Decide molecule cluster is 2*2 or 3*3

		for (int k = 1; k < 10; k++) {
			if (count <= (k * k)) {
				dimension = k;
				break;
			}
		}
		int numCol = (int) Math.ceil((float) count / dimension);
		int numRow = dimension;

		float centerX = 0; // X Coordinate around which we are going to add
							// molecules
		float centerY = 0; // Y Coordinate around which we are going to add
							// molecules
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);
		float increX = p5Canvas.w / 16;
		float offsetX = size.x / 2 + size.x / 3;
		centerX = p5Canvas.x + offsetX;
		centerY = p5Canvas.y + size.y - p5Canvas.boundaries.difVolume;
		Random rand = null;

		float borderWidthX = size.x;
		if(compoundName.equals("Bromine"))
			borderWidthX = size.x/2;
		else if (compoundName.equals("Mercury"))
			borderWidthX = size.x*1.5f;
		else if (compoundName.equals("Pentane"))
			borderWidthX = size.x/2;
		Vec2 topLeft = new Vec2(centerX - borderWidthX, centerY - size.y / 2);
		Vec2 botRight = new Vec2(centerX + numCol * size.x, centerY + numRow
				* size.y);
		float increY = numRow*size.y;
		// boolean dimensionDecided = false;

		boolean isClear = false;

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);

		// Check if there are any molecules in add area. If yes, add molecules
		// to another area.
		while (!isClear) {

			// Reset flag
			isClear = true;

			for (int k = 0; k < molecules.size(); k++) {

				if (true) {
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
				topLeft.set(centerX - borderWidthX , centerY - size.y / 2);
				botRight.set(centerX + numCol * (size.x), centerY
						+ numRow * size.y);
				// If we have gone through all available areas.
				if (botRight.x > (p5Canvas.x + p5Canvas.w)) {
					centerX = p5Canvas.x + offsetX;
					centerY +=increY;
					topLeft.set(centerX - borderWidthX , centerY - size.y / 2);
					botRight.set(centerX + numCol * (size.x), centerY
							+ numRow * size.y);
//					isClear = true; // Ready to jump out
//					res = false; // Set output bolean flag to false
					// TO DO: Show tooltip on Add button when we cant add more
					// compounds
				}
			}
		}
		if (res) // If there is enough space, add compounds
		{

			for (int i = 0; i < count; i++) {
				float x, y, angle = 0;

				int r = i % numRow;
				x = centerX + (i / numRow) * (size.x);

				y = centerY + (i % numRow) * size.y;
				
				if(compoundName.equals("Water"))
				{
					x= centerX + (i / numRow) * (size.x*1.05f);
					y = centerY + (i % numRow) * (size.y * 1.05f);

				}
				else if(compoundName.equals("Hydrogen-Peroxide"))
					y= centerY+(i % numRow)*( size.y/5*3);
				
				molecules.add(new Molecule(x, y, compoundName, box2d, p5Canvas,
						angle));
				res = true;
			}
		}

		return res;

	}

	/******************************************************************
	 * FUNCTION : addSingleIon DESCRIPTION : Function to add single ion to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSingleIon(boolean isAppEnable, String compoundName,
			int count) {

		boolean res = true;
		int creationCount = 0;

		if (isAppEnable) // if Applet is enable
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
		int colNum = dimension;
		boolean isClear = false;
		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		float increX = p5Canvas.w / 3;

		// Initializing
		centerX = p5Canvas.x + 50;
		centerY = p5Canvas.y + 80 - p5Canvas.boundaries.difVolume;
		topLeft = new Vec2(centerX - 0.5f * size.x, centerY - 0.5f * size.y);
		botRight = new Vec2(centerX + colNum * size.x, centerY + rowNum
				* size.y);
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
				topLeft = new Vec2(centerX - 0.5f * size.x, centerY - 0.5f
						* size.y);
				botRight = new Vec2(centerX + colNum * size.x, centerY + rowNum
						* size.y);

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

	/******************************************************************
	 * FUNCTION : addSolidCube DESCRIPTION : Function to add solid molecules to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addSolidCube(boolean isAppEnable, String compoundName,
			int count, Simulation simulation) {
		boolean res = true;
		Vec2 size = Molecule.getShapeSize(compoundName, p5Canvas);
		float moleWidth = size.x;
		float moleHeight = size.y;

		int numCol = 3;
		if (count <= 3) {
			numCol = count;
		}
		int numRow = (int) Math.ceil((float) count / numCol);

		float centerX = p5Canvas.x + moleWidth / 2; // X coordinate around which
													// we are going to
		// add Ions, 50 is border width
		float centerY = (float) (p5Canvas.y + p5Canvas.h
				- ((float) numRow + 0.5) * moleHeight - p5Canvas.boundaries.difVolume); // Y
																						// coordinate
																						// around
		// which we are going to
		// add Ions

		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		boolean isClear = false;

		float increX = p5Canvas.w / 3;
		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);

		topLeft.set(centerX - 0.5f * size.x, centerY - 0.5f * size.y);
		botRight.set(centerX + numCol * size.x, centerY + numRow * size.y);

		while (!isClear) {

			isClear = true;
			for (int k = 0; k < molecules.size(); k++) {

				if (!((String) molecules.get(k).getName()).equals("Water")) {
					molePos.set(molecules.get(k).getPosition());
					molePosInPix.set(box2d.coordWorldToPixels(molePos));
					if (areaBodyCheck(molePosInPix, topLeft, botRight)) { // Check
																			// whether
																			// this
																			// area
																			// is
																			// clear
						isClear = false;
						break;
					}
				}
			}
			if (!isClear) {
				centerX += increX;
				topLeft.set(centerX - 0.5f * size.x, centerY - 0.5f * size.y);
				botRight.set(centerX + numCol * size.x, centerY + numRow
						* size.y);
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
			for (int i = 0; i < count; i++) {
				float x_ = centerX + (i % numCol) * size.x;
				float y_ = centerY + (i / numCol) * size.y;
				float angle = (float) ((i / numCol == 0) ? 0 : Math.PI);
				molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, angle));

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
		float maxVelocity = 40;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w - 2 * moleWidth);
				y_ = moleHeight + randY.nextFloat()
						* (p5Canvas.h - 2 * moleHeight);
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
					mole.setReactive(false);
					mole.setLinearVelocity(new Vec2(0, 0));
					// Make water lighter;
					mole.body.m_mass = (float) (mole.body.getMass() * scale);
				}
			
			}

		}

		return res;
	}

	protected boolean addPrecipitation(boolean isAppEnable,
			String compoundName, int count, Simulation simulation, float angle) {
		boolean res = true;

		int numRow = (int) Math.ceil((float) count / 3); // number of row
		int numCol = (int) Math.ceil((float) count / numRow); // number of
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
	 * FUNCTION : areaBodyCheck DESCRIPTION : Check whether pos is in area
	 * (topLeft,botRight), return true if yes
	 * 
	 * INPUTS : pos(Vec2), topLeft(Vec2), botRight(Vec2) OUTPUTS: boolean
	 *******************************************************************/
	public boolean areaBodyCheck(Vec2 pos, Vec2 topLeft, Vec2 botRight) {
		boolean res = false;
		if (pos.x > topLeft.x && pos.x < botRight.x && pos.y > topLeft.y
				&& pos.y < botRight.y) {
			res = true;

		}
		return res;
	}

	/******************************************************************
	 * FUNCTION : joint2Element DESCRIPTION : Binding two elements together by
	 * creating joints between them. The joint has constant length if flag is false
	 * 
	 * 
	 * INPUTS : index1(int), index2(int), m1(Molecule),
	 * m2(Molecule),length(float),frequency(float) OUTPUTS: void
	 *******************************************************************/
	public void joint2Elements(Molecule m1, Molecule m2, float length,
			float frequency, float damp,boolean flag ) {

		if(!flag)
		{
		    frequency = 0;
		    damp = 1;
		}
		
		DistanceJointWrap djRef = new DistanceJointWrap(m1.body, m2.body,
				PBox2D.scalarPixelsToWorld(length), frequency, damp);
		/* Save joint reference */
		m1.compoundJoint.add(djRef);
		m2.compoundJoint.add(djRef);
		
		/* Save the other element`s index */
		// m1.compoundJointPair.add(m2);
		// m2.compoundJointPair.add(m1);
	}
	
	

	protected void joint2Elements(Molecule m1, Anchor anchor, float jointLen,
			float frequency, float damp) {
		// TODO Auto-generated method stub
		DistanceJointWrap djRef = new DistanceJointWrap(m1.body, anchor.body,
				PBox2D.scalarPixelsToWorld(jointLen), frequency, damp);
		/* Save joint reference */
		m1.compoundJoint.add(djRef);
		anchor.compoundJoint.add(djRef);
	}

	/******************************************************************
	 * FUNCTION : joint2Element DESCRIPTION : Bing two elements together by
	 * creating joints between them Default function length = Molecule.clRadius,
	 * frequency = 0
	 * 
	 * INPUTS : index1(int), index2(int), m1(Molecule), m2(Molecule) OUTPUTS:
	 * void
	 *******************************************************************/
	public void joint2Elements(Molecule m1, Molecule m2) {
		float length = 2 * Molecule.clRadius;
		joint2Elements(m1, m2, length, 5, 0.5f,true);
	}

	public void joint2Elements(Molecule m1, Molecule m2, float length) {
		joint2Elements(m1, m2, length, 5, 0.5f,true);
	}

	public void joint2Elements(Molecule m1, Molecule m2, float length,
			float frequency) {
		joint2Elements(m1, m2, length, frequency, 0.5f,true);
	}

	/******************************************************************
	 * FUNCTION : beginReaction DESCRIPTION : Dummy function is to be
	 * implemented in children classes
	 * 
	 * INPUTS : c ( Contact) OUTPUTS: None
	 *******************************************************************/
	public abstract void beginReaction(Contact c);

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
	 * FUNCTION : updateCompoundNumber DESCRIPTION : Update compounds numbers
	 * for table view
	 * 
	 * INPUTS : unit(int), Sim(int), Set(int) OUTPUTS: None
	 *******************************************************************/
	protected void updateCompoundNumber(int unit, int sim, int set) {
		if (true) {
			int decreaseNum = 0;
			int resultNum = 0;
			int index = -1;
			// Get reaction input from DB
			ArrayList<String> input = DBinterface.getReactionInputs(unit, sim,
					set);
			if (input != null) {
				for (String compound : input) {
					index = -1;
					index = Compound.names.indexOf(compound);
					if (index >= 0) // Decrease products count by num
					{
						decreaseNum = DBinterface.getReactionCompoundsNum(unit,
								sim, set, compound);
						if (decreaseNum != -1) {
							resultNum = Compound.counts.get(index)
									- decreaseNum;
							if (resultNum < 0)
								resultNum = 0;
							Compound.counts.set(index, resultNum);
						}
					}

				}
			}
			// Get reaction output from DB
			ArrayList<String> products = DBinterface.getReactionOutputs(unit,
					sim, set);
			int increaseNum = 0;
			resultNum = 0;
			index = -1;
			if (products != null) {
				for (String compound : products) {
					index = -1;
					index = Compound.names.indexOf(compound);
					if (index >= 0) // Increase products count by num
					{
						increaseNum = DBinterface.getReactionCompoundsNum(unit,
								sim, set, compound);
						if (increaseNum != -1) {
							resultNum = Compound.counts.get(index)
									+ increaseNum;
							Compound.counts.set(index, resultNum);
						}
					}

				}
			}
		}
	}
	protected void updateCompoundNumber(Simulation simulation) {
		updateCompoundNumber(simulation.getUnitNum(),simulation.getSimNum(),simulation.getSetNum());
	}
	public int getDissolvedNum() {
		return this.num_dissolved;
	}
	
	public void resetTableView(int sim, int set)
	{
		//Configure colume names and width
		((TableView) p5Canvas.getTableView()).setColumnName(0,"    #");
		((TableView) p5Canvas.getTableView()).setColumnWidth(0,10);
		((TableView) p5Canvas.getTableView()).setColumnWidth(1,40);
		((TableView) p5Canvas.getTableView()).setColumnWidth(2,120);
	}
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		
	}
	
	public void resetDynamicPanel(int sim, int set) 
	{
		
	}
	
	public void resetCheckboxPanel(int sim, int set) 
	{
		
	}
	
	// For Unit4 Sim4 Set2, move top boundary when temperature changes in order
	// to keep pressure constant
	protected void moveTopBoundary(Contact c) {
		
		if (!p5Canvas.isEnable || !p5Canvas.isSimStarted)
			return;
		Molecule mole = null;
		Boundary boundary = null;
	
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		if (o1 == null || o2 == null)
			return;

		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		// Make sure reaction only takes place between molecules and boundaries
		if (c1.equals("simulations.models.Molecule") && o2 == p5Canvas.boundaries.getTopBoundary()) {
			mole = (Molecule) o1;
			boundary = (Boundary) o2;
		} else if (o1 == p5Canvas.boundaries.getTopBoundary()
				&& c2.equals("simulations.models.Molecule")) {
			mole = (Molecule) o2;
			boundary = (Boundary) o1;
		}
		if (mole == null || boundary == null)
			return;
			if(!interpolator.isTargeting())
			{
	
				float oldPressure = p5Canvas.pressure;
		
				p5Canvas.temp = p5Canvas.getTempFromKE();
				//lastVolume = p5Canvas.currentVolume;
				// According to below equation, volume should go up with temp-tempMin
				// proportionally
				// pressure = (mol* R* (temp-tempMin))/(currentVolume);
				float ratioTemp = (p5Canvas.temp - p5Canvas.tempAbsoluteZero) / (lastTemp - p5Canvas.tempAbsoluteZero);
				float ratioMole = (float)State.molecules.size()/lastMole;
				
				//float target= (int) Math.round(ratioTemp*ratioMole * lastVolume);
				float target= ratioTemp*ratioMole * lastVolume;
				
				// Constrain volume slider
				if (target < p5Canvas.volumeMinBoundary)
					target = p5Canvas.volumeMinBoundary;
				if (target > p5Canvas.volumeMaxBoundary)
					target = p5Canvas.volumeMaxBoundary;
		
				//If lastVolume and target are too close
				if(Math.round(lastVolume)==Math.round(target))
					return;
				
				interpolator.set(lastVolume);
				interpolator.target(target);
				
				// Change volume label
				//p5Canvas.setVolume(p5Canvas.currentVolume);
				
				lastTemp = p5Canvas.temp;
				lastMole = State.getMoleculeNum();
				lastVolume = target;
			}
			else
			{
				//If top boundary is moving, change velocity of molecule
				Vec2 velocity = mole.getLinearVelocity();
				velocity.y*=-1;
				float boundaryVel = this.getPistonSpeed()*10000;
				if(boundaryVel<0) //top boundary moving down
				{
					if(Math.abs(velocity.y)<Math.abs(boundaryVel))
					{
						
						mole.sumForceY[0]=-100;
					}
				}
//				mole.body.setAwake(true);
				mole.setLinearVelocity(velocity);
			}

	}
	

	
	
	public void updateTopBoundary(int sim, int set)
	{
		//In Unit 4 Sim 4 Set 2, update volume every frame
		if(p5Canvas.boundaries.hasWeight())
		{
			if(interpolator.isTargeting())
			{
				p5Canvas.getBoundaries().getTopBoundary().setFriction(0.0f);
				interpolator.update();
				float value = interpolator.getValue();
				p5Canvas.setVolume(value);	
			}
			else
			{
				if(p5Canvas.getBoundaries().getTopBoundary().getFriction()==0.0f)
				{
					float defaultFriction = p5Canvas.getBoundaries().getTopBoundary().defaultFriction;
					p5Canvas.getBoundaries().getTopBoundary().setFriction(defaultFriction);
				}
			}
		}
//		System.out.println("Friction is "+p5Canvas.getBoundaries().getTopBoundary().getFriction());
	}
	

	public float getPistonSpeed()
	{
		return interpolator.getVelocity();
	}
	public float getPistonKE()
	{
		float velocity = getPistonSpeed();
		float mass = 1000f;
		float ke = 0.5f * mass * velocity * velocity;
		return ke;
	}
	
	//Constrain the kinetic energy of molecules by sim itself
	public boolean constrainKineticEnergy(int sim,int set,float averageKE)
	{
		return false;
	}
	
/*
	public void computeDissolved() {

		// Compute saturation and the max number can be dissovled at current
		// saturation
		float dissovledCap = numDissolved_atSaturation();
		DecimalFormat df = new DecimalFormat("###.#");
		// If dissolved molecules are less than saturation cap
		if (num_dissolved < dissovledCap || dissovledCap == 0) {
			float dis = num_dissolved * molToMass();
			if (getMassDissolved() <= dis)
				setMassDissolved(dis);
		}
		// If dissolved molecules equals to saturation cap
		else if (getDissolvedNum() == numDissolved_atSaturation()) {
			float sat = computeSat();
			float dis = 0; // Distance of Ion
			setMassDissolved(sat - dis);

		}

		// Make sure dissovled mass is not larger than total mass
		double dis = getMassDissolved();
		double total = getTotalNum() * molToMass();
		if (dis > total) {
			setMassDissolved((float) total);
		}

		// Update UI
		// Set dissolved label on right panel
		p5Canvas.getMain().m1Disolved.setText(df.format(getMassDissolved())
				+ " g");
		float temp = p5Canvas.temp;
		// Boundary conditions: if there is no liquid water
		if (temp <= 0 || temp >= 100) {
			setMassDissolved(0);
			p5Canvas.getMain().m1Disolved.setText("0 g");
		}

		// If ConvertToMol checkbox is selected, we need to change 'g' to 'mol'
		if (p5Canvas.isConvertMol) {
			p5Canvas.convertMassMol2();
		}

	}
*/
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim, int set, int indexOfGraph,
			int indexOfCompound) {
		return (float)Compound.counts.get(indexOfCompound);
	}
	
	public float getDataTickX(int sim, int set, int indexOfGraph) {
		return (float)p5Canvas.getMain().time;
	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		return (float)Compound.counts.get(indexOfCompound);
	}
	
	//Function to return the correct compound name on the 3rd column of TableView
	public ArrayList<String> getNameTableView(int sim, int set)
	{
		ArrayList<String> res = new ArrayList<String>();
		String name;
		for(int i = 0;i<Compound.names.size();i++)
		{
			name = new String((String)Compound.names.get(i));
			name = name.replace("-", " ");
			res.add(name);
		}
		
		return res;
	}
	
	public int numDissolved_atSaturation() {
		int num = Math.round(computeSat() / molToMass());
		return num;
	}

	// Compute saturation
	public float computeSat() {
		if (p5Canvas.temp > 99 || p5Canvas.temp < 0) {
			return 0;
		}
		float r = (float) (p5Canvas.temp / 99.);
		float sat = 0;
		if (p5Canvas.getSet() == 1
				&& p5Canvas.getSim()<= 3)
			sat = (35.7f + r * (39.9f - 35.7f)); // Take number of Water to
													// account
		else if (p5Canvas.getSet() == 2)
			sat = 0;
		else if (p5Canvas.getSet() == 3)
			sat = 0;
		else if (p5Canvas.getSet() == 4) {
			if (0 < p5Canvas.temp && p5Canvas.temp <= 20) {
				r = (float) (p5Canvas.temp / 20.);
				sat = (59.5f + r * (74.5f - 59.5f));
			}
			if (20 < p5Canvas.temp && p5Canvas.temp <= 40) {
				r = (float) ((p5Canvas.temp - 20) / 20.);
				sat = (74.5f + r * (128f - 74.5f));
			}
			if (40 < p5Canvas.temp && p5Canvas.temp <= 60) {
				r = (float) ((p5Canvas.temp - 40) / 20.);
				sat = (128f + r * (137f - 128f));
			}
			if (60 < p5Canvas.temp && p5Canvas.temp <= 80) {
				r = (float) ((p5Canvas.temp - 60) / 20.);
				sat = (137f + r * (147f - 137));
			}
			if (80 < p5Canvas.temp && p5Canvas.temp <= 100) {
				r = (float) ((p5Canvas.temp - 80) / 20.);
				sat = (147f + r * (159f - 147f));
			}
		} else if (p5Canvas.getSet()== 5) {
			sat = 0;
		} else if (p5Canvas.getSet() == 6) {
			sat = 0;
		} else if (p5Canvas.getSet() == 7)
			sat = (6.9f + r * (19.2f - 6.9f));
		else if (p5Canvas.getSet() == 1
				&& p5Canvas.getSim() == 4)
			sat = (28f + r * (56.3f - 28f));
		return sat * ((float) numWater / water100mL);
	}

	// Reset anchors based on the distance user dragged
	protected void resetAnchors(float xDrag, float yDrag) {
		if (!State.anchors.isEmpty()) {
			for (int i = 0; i < State.anchors.size(); i++) {
				State.anchors.get(i).setPosition(xDrag, yDrag);
			}
		}
	}
	
	public float getMoleculeDensity(int sim,int set, String moleName)
	{
		return getSimulation(sim, set).getElementDensity(moleName);
	}
	public abstract void setMoleculeDensity();
	
	public abstract void updateMoleculeCountRelated(int sim,int set) ; 


	public int getTotalNum() {
		return num_total;
	}

	public int getWaterNum() {
		return numWater;
	}

	public float getMassDissolved() {
		return this.massDissolved;
	}

	public void setMassDissolved(float mass) {
		this.massDissolved = mass;
	}

	public int molToMass() {
		return this.mToMass;
	}

	public int getWater100Ml() {
		return this.water100mL;
	}
	
	public float getVolumeMagnifier()
	{
		return volumeMagnifier;
	}
	

	
	public void needWeight()
	{
		p5Canvas.boundaries.setHasWeight(true);
	}




}
