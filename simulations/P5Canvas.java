package simulations;

import simulations.models.*;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
//import java.util.Timer;

import processing.core.PApplet;
import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.Molecule;

import main.Main;
import main.TableView;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.*;

import data.DBinterface;
import data.State;
import data.YAMLinterface;

import Util.ColorCollection;
import Util.ColorScales;
import static data.State.*;

public class P5Canvas extends PApplet {

	private Main main = null;
	private final long serialVersionUID = 1L;
	public float x = 0;
	public float y = 0;
	public float w;// width of the boundary
	public float h;// height of the boundary
	public float defaultW;
	public float defaultH;

	// A reference to our box2d world
	private PBox2D box2d;
	public int FRAME_RATE = 30;

	//FLAGS
	public boolean isEnable = false;       //Is simulation going on or stopped
	public boolean isHidingEnabled = false;//Is molecule hidding enabled
	public boolean isHidden = false;       //Is molecule hidding triggered
	public boolean isTrackingEnabled = false;
	public boolean isDisplayForces = false;
	public boolean isDisplayJoints = false;
	public boolean isBoundaryShow = true;  //If show boundary
	private boolean enableDrag = true;  //If enable drag
	private boolean isDragging = false;   //Is p5Canvas being dragged


	public int creationCount = 0;
	// Properties of container
	public float temp = 25.f;
	//public float lastTemp;
	public int tempMin = -20;   //The min temperature of current simulation
	public int tempMax = 200;   //The max temperature of current simulation
	public final float tempAbsoluteZero = -273;
	public int heat = 0;
	public float pressure = 0.0f;
	public float mol = 0.0f;
	public final float R = 8.314f ; // 8.314 J*K-1*mol -1
	public final float atmToKpa = 101.325f;

	// Default value of speed
	public float speedRate = 1.0f;
	// Default value of canvas scale
	public float canvasScale = 0.77f;
	public int currentVolume;
	//public int lastVolume;
	public int volumeMinBoundary = 10;
	public int volumeMaxBoundary = 100;
	
	public float maxH = 1100;// minimum height of container

	public int heatRGB = 0;
	public Boundaries boundaries = null;

	//Simulation parameters
	private int sim =0;
	private int set =0;
	private int unit =0;
	public int curTime = 0;
	public int oldTime = 0;
	public int xStart = 0;
	public int yStart = 0;
	public int xDrag = 0; // x offset after dragging
	public int yDrag = 0; // y offset after dragging
	ArrayList<String> products = new ArrayList<String>();
	ArrayList<Molecule> killingList = new ArrayList<Molecule>();
	public UnitList unitList = null;

	// Time step property
	private float defaultTimeStep = 1.0f / 60.0f;
	private float timeStep = 1.0f / 60.0f;
	private int velocityIterations = 6;
	private int positionIterations = 2;
	public static DBinterface db = new DBinterface();
	public YAMLinterface yaml = new YAMLinterface();

	public float averageKineticEnergy = 0;
	float totalKineticEnergy = 0f;
	public int heatMoleculeTimes = 0;
	public int heatMoleculeMaxTimes = 10;
	public Queue<Float> energyQueue = new LinkedList<Float>();
	public int energyQueueSize = 30;
	//public ArrayList<UnitBase> unitList = new ArrayList<UnitBase>();
	float K = 1.38f; // K is ke/temp constant
	float mole = 6.022f; // mole is another constant that used to calculate temp

	private int trackedId = -1; // Keep track of id of molecule that is
								// selected,used in Unit 4 Sim 2
	private Vec2 dragSpeed = new Vec2(0, 0);
	public boolean isSimStarted = false; // Flag indicating if this is the first
											// start of sim
	public int[] heaterLimit;
	public float heatSpeed;

	public float multiplierVolume = 13f; // Multiplier from world coordinates to ml

	private boolean firstRun = true;
	public boolean startDraggingMolecule = false;
	private boolean ifConstrainKE = true;	//If the sim wants to control Kinetic Energy.
	public float KEregulation = 2f;



	


	public P5Canvas(Main parent) {

		setMain(parent);
		box2d = new PBox2D(this);
		unitList = new UnitList(this,box2d);
		boundaries = new Boundaries (this);

	}

	public void setup() {
		smooth();
		frameRate(FRAME_RATE);

		// Initialize box2d physics and create the world
		box2d.createWorld();
		box2d.setGravity(0f, -10f);

		// Turn on collision detection
		box2d.listenForCollisions();
		defaultW = 560 / canvasScale;
		defaultH = 635 / canvasScale;
		size((int) (560), (int) (638));
		w = defaultW;
		h = defaultH;
		
		currentVolume = getMain().defaultVolume;
		boundaries.create(x, y, w, h,currentVolume);

		setupHeaterLimit();
		heatSpeed = 1;
		
		
		

	}

	public void draw() {
		drawBackground();
		// Add statement that need to do initialization in the first run

		if (isEnable && firstRun) {
			
			// Initialization function to intial parameters
			unitList.initialize(unit);

			firstRun = false;

		}
		
		updateTopBoundary();
		updateMolecules(); // update molecules which are newly created
		if (isEnable && isSimStarted)
		updateProperties(); // Update temperature and pressure etc

		if (isEnable&&isSimStarted)
		updateOutput();
			
		/* Change Scale */
		this.scale(canvasScale * ((float) getMain().currentZoom / 100));
		/* Change Time Speed */
		if (isEnable && ! isDragging()) {
			if (speedRate <= 1) {
				timeStep = speedRate * defaultTimeStep;
			}
			box2d.step(timeStep, velocityIterations, positionIterations);

			/* Constrain energy */
			constrainKineticEnergy();

			/* Compute Forces between different compounds */
			computeForces();
		}

		/* Show selected contour while user create rectangle by dragging mouse */
		if (isHidingEnabled && isHidden) {
			this.stroke(ColorCollection.getColorSelectionRectInt());
			this.noFill();
			this.rect(xStart / canvasScale, yStart / canvasScale, (mouseX
					/ canvasScale - xStart / canvasScale), (mouseY
					/ canvasScale - yStart / canvasScale));
		}

		// Draw boundary
		if(isBoundaryShow)
		boundaries.display();

		/*
		 * Random pick one molecule and track it if tracking molecule checkbox
		 * is selected
		 */
		if (isTrackingEnabled)
			unitList.getUnit4().displayTrail();

		// Draw all molecules
		for (int i = 0;i<State.getMoleculeNum();i++) {
			Molecule m = State.getMoleculeByIndex(i);
			if(m==null)
				continue;
			if (isHidingEnabled && isHidden) {
				Vec2 p = box2d.coordWorldToPixels(m.getPosition());
				if (xStart / canvasScale < p.x && p.x < mouseX / canvasScale
						&& yStart / canvasScale < p.y
						&& p.y < mouseY / canvasScale)
					m.isHidden = true;
				else
					m.isHidden = false;
			}
			m.display();
		}
		
		//Draw all ImageObjects
		for(ImageObject io: State.getImageObjects())
		{
			io.display();
		}

		// Update anchors position
		if(getUnit()==3)
			unitList.getUnit3().resetAnchors(xDrag, yDrag);

	}

	/******************************************************************
	 * FUNCTION : updateProperties DESCRIPTION : update pressure volume mol and
	 * temperature
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public void updateProperties() {


		temp = getTempFromKE();
		// Update molecule status base on new temp
		for (Molecule m: State.getMolecules()) {
			m.setPropertyByHeat(false);
		}

		
		getUnit2().satCount = 0;

		// Known: V-currentVolume n-mol T-temp R
		mol = (float)State.getMoleculeNum();

		// Unknown: Pressure
		// P is measured in kPa
		// V is measured in Liter
		// T is measured in Kelvin
		pressure = (mol * R * (temp - tempAbsoluteZero)) / (currentVolume);
		// Translate pressure from atmosphere into Kpa
		//pressure *= atmToKpa;

		if(getUnit()==6) //Fix temperature and pressure in Unit6
			getUnit6().updateProperties(getSim(), getSet());
		else if(getUnit()==7) //Update entropy and enthalpy
			getUnit7().updateProperties(getSim(),getSet());
		else if(getUnit()==8)
		{
			getUnit8().updateProperties(getSim(),getSet());
		}
	}
	
	//Print out properties on right panel
	//Called by p5Canvas draw
	public void updateOutput()
	{
		unitList.updateOutput(unit, sim,set);
	}
	
	
	//Update molecule count related information , e.g. concentration
	public void updateMoleculeCountRelated()
	{
		unitList.updateMoleculeCountRelated(unit,sim,set);
	}

	// Calculate temp from average kinetic energy
	public float getTempFromKE() {
		return (float) ((averageKineticEnergy * 2 * 100) / (0.15f * K * mole )) 
				+ tempMin;
	}

	// Calculate kinetic energy from temp
	public float getKEFromTemp() {
		return (float) (0.15f * K * mole  * (temp - tempMin)) / (2 * 100);
	}



	/******************************************************************
	 * FUNCTION : computeForces DESCRIPTION : Compute forces at the beginning of
	 * every frame
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public void computeForces() {
		unitList.computeForces(unit, sim, set);
		// Apply forces after set forces
		applyForce();

	}

	// Apply force at the begginning of every frame
	public void applyForce() {
		unitList.applyForce( unit, sim, set);
	}

	// Constrain Energy. To fake that molecules` average kinetic energy does not
	// change
	public void constrainKineticEnergy() {
		if(!ifConstrainKE)
			return;
		//Check if sim wants to control kinetic energy by itself
		if(unitList.constrainKineticEnergy(unit,sim,set,averageKineticEnergy))
			return;
		// First, sum up all average Energy to get total KE
		float idealKE = State.molecules.size() * averageKineticEnergy;
		// Second, find out the ratio of ideal stable KE to current real KE
		float currentKE = 0;
		for (Molecule mole: State.getMolecules()) {
			float ke = mole.getKineticEnergy();
			currentKE += ke;

		}
		
		float ratio = idealKE / currentKE;
		// Third, each molecule`s KE multiplied with ratio
		for (Molecule mole: State.getMolecules()) {
			//Not apply to those whose energy is below average too much
			if(mole.getKineticEnergy()>(averageKineticEnergy/1.25f))
				mole.constrainKineticEnergy(ratio);
		}

	}

	/*
	 * Background methods
	 */
	private void drawBackground() { // draw background
		pushStyle();
		//stroke(backgroundColor);
		fill(ColorCollection.getColorSimBackgroundInt());
		stroke(ColorCollection.getColorSimBorderInt());
		rect(0, 0, width, height);
		popStyle();
	}



	/******************************************************************
	 * FUNCTION : addMoleculeRandomly DESCRIPTION : Initially add molecule to
	 * applet when a new set gets selected. Called when reset.
	 * 
	 * INPUTS : compoundName(String), count(int) OUTPUTS: boolean
	 *******************************************************************/
	public boolean addMoleculeRandomly(String compoundName, int count) {

		boolean res = false;
		boolean tmp = isEnable;
		isEnable = false;

		res = unitList.addMolecule(unit, tmp, compoundName, count);

		// If we successfully added molecules, update compound number
		if (res) {
			// Compound.counts.set(index, addCount);
			int index = Compound.names.indexOf(compoundName);
			int cap = Compound.caps.get(index);
			int countNum = Compound.counts.get(index);
			// System.out.println("count is "+countNum+", cap is "+ cap);
			if (countNum >= cap) // Grey out add button
			{
				if (!getMain().addBtns.isEmpty())
					getMain().addBtns.get(compoundName).setEnabled(false);
			}


		}

		isEnable = tmp;
		return res;
	}

	/******************************************************************
	 * FUNCTION : addMolecule DESCRIPTION : Add current average Kinetic Energy
	 * to newly added molecule
	 * 
	 * INPUTS : count(int) OUTPUTS: None
	 *******************************************************************/
	/*
	 * public void AddEnergyToMolecule(int count) { int moleNum =
	 * State.molecules.size(); for(int i =0;i<count;i++) {
	 * State.molecules.get(moleNum-1-i).setKineticEnergy(averageKineticEnergy);
	 * } }
	 */

	/******************************************************************
	 * FUNCTION : addMolecule DESCRIPTION : Function to create compounds from
	 * outside the PApplet
	 * 
	 * INPUTS : compoundName(String), count(int) OUTPUTS: None
	 *******************************************************************/
	public boolean addMolecule(String compoundName, int count) {
		// The tmp variable helps to fix a Box2D Bug: 2147483647 because of
		// Multithreading
		boolean tmp = isEnable;
		isEnable = false;
		boolean res = false;

		int index = Compound.names.indexOf(compoundName);
		int addCount = Compound.counts.get(index) + count;

		res = unitList.addMolecule(unit, tmp, compoundName, count);

		// If we successfully added molecules, update compound number
		if (res) {
			Compound.counts.set(index, addCount);
		}

		isEnable = tmp;
		return res;
	}

	/******************************************************************
	 * FUNCTION : reset DESCRIPTION : Reset function called by Main reset()
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public void reset() {
		//Reset parameters
		isEnable = false;
		isSimStarted = false;
		ifConstrainKE = true;
		temp = 25;
		tempMin = -20;
		tempMax = 200;
		currentVolume = getMain().defaultVolume;
		volumeMinBoundary = 10;
		volumeMaxBoundary = 100;
		isBoundaryShow = true;
		heatSpeed = 1;
		pressure = 0;
		boundaries.setHasWeight(false);
		setIfConstrainKE(true);
		setEnableDrag(true);
		main.getCanvas().setEnabled(true);



		if(products!=null)
		products.clear();
		else products = new ArrayList<String>();
		if(killingList!=null)
		killingList.clear();
		else killingList = new ArrayList<Molecule>();
		removeAllMolecules();
		removeAllAnchors();
		removeAllImageObjects();

		unit = getMain().getSelectedUnit();
		sim = getMain().getSelectedSim();
		set = getMain().getSelectedSet();
		curTime = 0;
		oldTime = 0;
		// Reset Gravity
		box2d.setGravity(0f, -10f);

		// Reset function set intial temperature of one simulation
		unitList.reset(unit);

		// Get initial Kinetic Energy from temp
		averageKineticEnergy = getKEFromTemp();
		updateProperties();

		// Clean collision points used for drawing trail
		isTrackingEnabled = false;
		getMain().boxMoleculeTracking.setSelected(isTrackingEnabled);

		// Reset boundaries
		boundaries.resetBoundary(0, 0, defaultW, defaultH,currentVolume);
		
		firstRun = true;

	}
	
	//Called when user click "Play button"
	public void play()
	{
		if(!isSimStarted)
			isSimStarted = true;
		isEnable = true;
		
		if(getUnit()==8)
			getUnit8().play();
		else if(getUnit()==9)
			getUnit9().play();
		
	}
	
	
	public void resetDynamicPanel()
	{
		unitList.resetDynamicPanel(unit, sim, set);
	}
	
	public void resetCheckboxPanel()
	{
		unitList.resetCheckboxPanel(unit, sim, set);
	}
	//Called by main when all the reset have been done
	//in order to initialize data
	public void initializeSimulation()
	{		
		unitList.initializeSimulation(unit,sim,set);
	}



	// Remove all existing molecules, called by reset()
	public void removeAllMolecules() {
		boolean tmp = isEnable;
		isEnable = false;

		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = (Molecule) molecules.get(i);
			m.killBody();
		}
		molecules.clear();

		isEnable = tmp;
	}
	
	// Remove all existing molecules in a certain area
	public void removeAllMolecules(Vec2 topLeft, Vec2 botRight) {
		boolean tmp = isEnable;
		isEnable = false;

		for (int i = molecules.size()-1; i >=0; i--) {
			Molecule m = (Molecule) molecules.get(i);
			Vec2 pos = m.getPositionInPixel();
			if (pos.x > topLeft.x && pos.x < botRight.x && pos.y > topLeft.y
					&& pos.y < botRight.y) 
			{
				State.molecules.remove(m);
				m.destroy();
			}
		}

		isEnable = tmp;
	}

	// Remove existing anchors, called by reset()
	public void removeAllAnchors() {
		boolean tmp = isEnable;
		isEnable = false;

		for (int i = 0; i < anchors.size(); i++) {
			Anchor anchor = (Anchor) anchors.get(i);
			anchor.destroy();
		}
		anchors.clear();

		isEnable = tmp;
	}
	// Remove existing Image Objects, called by reset()
	public void removeAllImageObjects()
	{
		State.imageObjects.clear();
	}
	
	

	// Set Speed of Simulation; values are from 0 to 100; 100 is default value
	public void setSpeed(float speed) {
		speedRate = speed;
	}

	/*
	 * //Set Pressure of Container. Value is from 0 to 10, 1 is default public
	 * void setPressure(float pressure) { pressureRate = pressure; }
	 */

	// Set Heat of Molecules; values are from 0 to 100; 50 is default value
	public void setHeat(int value) {

		// Change bottom boundary color based on temp change
		double v = (double) (value - main.heatMin)
				/ (main.heatMax - main.heatMin);
		Color color = ColorScales.getColor(1 - v, "redblue", 1f);
		heatRGB = color.getRGB();

		// Record current heat.
		heat = value;

	}

	// Set Volume; values are from 0 to 100; 50 is default value
	public void setVolume(int v) {
		boolean tmp = isEnable;
		isEnable = false;
		currentVolume = v;
		if (currentVolume < volumeMinBoundary) 
			currentVolume = volumeMinBoundary;
		else if(currentVolume > volumeMaxBoundary)
			currentVolume = volumeMaxBoundary;
		
		//main.volumeSlider.setValue(currentVolume);
		//main.volumeSlider.updateUI();
		main.volumeLabel.setText(currentVolume + " mL");
		float volumeMagnifier = unitList.getVolumeMagnifier(unit)/1000;
			if( volumeMagnifier != 0)
			{
				float outputVolume = currentVolume*volumeMagnifier;
				DecimalFormat formatter = new DecimalFormat("###.#");
				String output = formatter.format(outputVolume);
			main.volumeLabel.setText(output + " L");
			}

		boundaries.setVolume(currentVolume);
		isEnable = tmp;
	}
	
	// Set Volume
	// Accept float value so that slider can move smoothly
	public void setVolume(float v) {
		boolean tmp = isEnable;
		isEnable = false;
		currentVolume = Math.round(v);
		if (currentVolume < volumeMinBoundary) 
			currentVolume = volumeMinBoundary;
		else if(currentVolume > volumeMaxBoundary)
			currentVolume = volumeMaxBoundary;
		
		//main.volumeSlider.setValue(currentVolume);
		//main.volumeSlider.updateUI();
		main.volumeLabel.setText(currentVolume + " mL");
		float volumeMagnifier = unitList.getVolumeMagnifier(unit)/1000;
		if( volumeMagnifier != 0)
		{
			float outputVolume = currentVolume*volumeMagnifier;
			DecimalFormat formatter = new DecimalFormat("###.#");
			String output = formatter.format(outputVolume);
			main.volumeLabel.setText(output + " L");
		}
		boundaries.setVolume(v);
		isEnable = tmp;
	}
	
	private void updateTopBoundary(){
		if(isEnable)
			unitList.updateTopBoundary(unit,sim,set);
	}

	/******************************************************************
	 * FUNCTION : updateMolecules DESCRIPTION : Kill molecules which have gone
	 * after reaction, and add new created molecules
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	private void updateMolecules() {
		if(isEnable)
		unitList.updateMolecules(unit, sim, set);

	}

	/******************************************************************
	 * FUNCTION : beginContact DESCRIPTION : Molecule collision detect function
	 * Called when contact happens
	 * 
	 * INPUTS : c(Contact) OUTPUTS: None
	 *******************************************************************/
	public void beginContact(Contact c) {

		// Check if molecule contacts with heater
		heatMolecule(c);

		// Specified beginReaction function for each unit
		unitList.beginContact(unit, c);
	}

	public void heatMolecule(Contact c) {
		Molecule mole = null;
		Boundary boundary = null;
		// If heater is not on, return
		if (heat == getMain().defaultHeat)
			return;
		// Get our objects that reference these bodies
		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		if (o1 == null || o2 == null)
			return;

		String c1 = o1.getClass().getName();
		String c2 = o2.getClass().getName();
		
		// Make sure reaction only takes place between molecules and boundaries
		if (c1.equals("simulations.models.Molecule") && o2 == boundaries.getBotBoundary()) {
			mole = (Molecule) o1;
			boundary = (Boundary) o2;
		} else if (o1 == boundaries.getBotBoundary()
				&& c2.equals("simulations.models.Molecule")) {
			mole = (Molecule) o2;
			boundary = (Boundary) o1;
		}
		if (mole == null || boundary == null)
			return;
		
		//For Unit 4 Sim 4 Set 2, if top boundary reach top or low point we are not going to heat anymore
		if(isSimSelected(4, 4, 2))
		{
			int midLevel = (main.heatMax - main.heatMin)/2;
			if((currentVolume<=volumeMinBoundary && heat<midLevel) || (currentVolume>=volumeMaxBoundary && heat>midLevel))
				return;
		}

		// If temp has not reached max, keep heating.
		if (!reachHeatLimit(temp)) {
				float scale = 1f;
				curTime = getMain().time;
				// Change molecule speed base on heat
				scale = (float) (heat - (main.heatMax - main.heatMin) / 2)
						/ (main.heatMax - main.heatMin);
				scale *= (0.4f*heatSpeed);
				scale += 1.0f;
				Vec2 velocity = mole.getLinearVelocity();
				velocity = velocity.mul(scale);
				
				//Heating always go faster than freezing,
				//So we add time limit to heat process
				if(scale>1) 
				{
					if(curTime == oldTime)  //If timer has not ticked yet
					{
						if(heatMoleculeTimes<heatMoleculeMaxTimes) //heat
						{
							mole.setLinearVelocity(velocity);
							
							//Add some small force to make solid vibrate
							if(scale>1 && temp<=mole.freezingTem)
							{
								Random rand = new Random();
								float x = rand.nextFloat()*0.5f;
								float y = rand.nextFloat()*0.5f;
								x*=(rand.nextBoolean())?1:-1;
								Vec2 force = new Vec2(x, y);
								force.mulLocal(scale);
								mole.addForce(force);
							}
							heatMoleculeTimes++;
							// Calculate new KE
							calculateKE(scale); 
							//System.out.println("Time = "+curTime+", Temp = "+temp);
						}
					}
					else //update curTime
					{
						oldTime = curTime;
						mole.setLinearVelocity(velocity);
						calculateKE(scale); 
						heatMoleculeTimes = 1;
					}
					
				}
				else //Freeze process
				{
					mole.setLinearVelocity(velocity);
					// Calculate new KE
					calculateKE(scale); 
				}

		}

	}

	// The only function that can change kinetic energy.
	// Should be called after all the function that changes velocity or mole.
	private void calculateKE(float limit) {
		totalKineticEnergy = 0;
		float lastAverageKE = averageKineticEnergy;
		for (int i = 0; i < State.molecules.size(); i++) {
			totalKineticEnergy += State.molecules.get(i).getKineticEnergy();
		}
		averageKineticEnergy = totalKineticEnergy / State.molecules.size();
		
		//Add restrictions
		if( limit <= 1.0f) //AKE should go down, but cant be smaller than limit
		{
			if(averageKineticEnergy> lastAverageKE) //AKE cant be larger
				averageKineticEnergy = lastAverageKE;
			else
			{
				if((averageKineticEnergy/lastAverageKE)<limit) //AKE cant be too smaller
					averageKineticEnergy=limit*lastAverageKE;
			}
		}
		else //AKE should go up, but cant be larger than limit
		{
			if(averageKineticEnergy < lastAverageKE)
				averageKineticEnergy = lastAverageKE;
			else
			{
				if((averageKineticEnergy/lastAverageKE) > limit )
					averageKineticEnergy=limit*lastAverageKE;
			}
			
		}
	}
	protected void calculateKE() {
		totalKineticEnergy = 0;
		for (int i = 0; i < State.molecules.size(); i++) {
			totalKineticEnergy += State.molecules.get(i).getKineticEnergy();
		}
		this.averageKineticEnergy = totalKineticEnergy / State.molecules.size();
	}

	private void setupHeaterLimit() {
		heaterLimit = new int[((main.heatMax - main.heatMin) / main.heatTickSpacing) + 1];
		heaterLimit[0] = (int) tempMin; // -5
		heaterLimit[1] = (int) tempMin; // -4
		heaterLimit[2] = (int) tempMin; // -3
		heaterLimit[3] = (int) tempMin; // -2
		heaterLimit[4] = (int) tempMin; // -1
		// 0 does not change anything， no need to set a limit
		heaterLimit[6] = 50; // +1
		heaterLimit[7] = 75; // +2
		heaterLimit[8] = 100; // +3
		heaterLimit[9] = 180; // +4
		heaterLimit[10] = 260; // +5

	}

	// Check current temperature to see if we reach the max temp to which heater
	// can heat up
	// t: temperature
	public boolean reachHeatLimit(float t) {
		
		boolean res = false;
		//Current heat level
		int scale = (heat - (main.heatMax + main.heatMin) / 2)
				/ main.heatTickSpacing;
		switch(getUnit())
		{
		default:
			int midLevel = ((main.heatMax + main.heatMin) / 2)
					/ main.heatTickSpacing;
			if (scale < 0) // Set up minimum limit
			{
				if (t < heaterLimit[scale + midLevel])
					res = true;
			} else if (scale > 0) // Set up maximum limit
			{
				if (t > heaterLimit[scale + midLevel])
					res = true;
			}
			break;
		case 7:
			if(getSim()==2) //Min = 0, Max = 220
			{
				if (scale < 0) // Set up minimum limit
				{
					if (t < tempMin)
						res = true;
				} else if (scale > 0) // Set up maximum limit
				{
					if (t > tempMax)
						res = true;
				}
			}
			break;
		}

		return res;
	}

	// Set up reaction products while initializing for graph rendering
	public void setupReactionProducts() {
		unitList.setupReactionProducts(unit, sim, set);
	}
	
	public void customizeInterface()
	{
		unitList.customizeInterface(unit, sim, set);
	}

	/******************************** MOUSE EVENT ******************************/
	public void keyPressed() {

	}

	public void mouseMoved() {

		/*
		 * //Deprecated: mouse resize top boundary //Check the top boundary int
		 * id = boundaries[2].isIn(mouseX, mouseY); if (id==2)
		 * this.cursor(Cursor.N_RESIZE_CURSOR); else
		 * this.cursor(Cursor.DEFAULT_CURSOR);
		 */
	}
	@Override
	public void mousePressed() {
		isHidden = true;
		xStart = mouseX;
		yStart = mouseY;
		// draggingBoundary = boundaries[2].isIn(mouseX, mouseY);
		// In Unit 4 Sim 2, molecules are able to be selected by mouse
		if (unit == 4 && sim == 2) {
			trackedId = -1;
			for (int i = 0; i < State.molecules.size(); i++) {
				float scale = canvasScale
						* ((float) getMain().currentZoom / 100);
				if (State.molecules.get(i).contains(mouseX / scale,
						mouseY / scale)) {
					//bind molecule with mouse
					trackedId = i;
					startDraggingMolecule = true;
					// System.out.println("Selected id:"+trackedId);
					break;
				}
			}
		}
	}

	public void mouseReleased() {
		isHidden = false;
		isDragging = false;
		
		unitList.getUnit4().mouseReleased();
		
		xDrag = 0;
		yDrag = 0;

		if (unit == 4 && sim == 2) {
			if(startDraggingMolecule)
			{
				if (trackedId != -1) {
					// Set molecule speed as drag speed
					State.molecules.get(trackedId).setLinearVelocity(dragSpeed);
					calculateKE();
					trackedId = -1;
				}
				startDraggingMolecule = false;
			}
			
		}
		

	
	}

	public void mouseDragged() {
		if (isHidingEnabled) {

		} else {
			if(this.getEnableDrag()){

			int xTmp = xDrag;
			int yTmp = yDrag;
			xDrag = (int) ((mouseX - xStart) / canvasScale);
			yDrag = (int) ((mouseY - yStart) / canvasScale);

			//Enable throw molecule function in Unit 4 Sim 2
			if (isSimSelected(4, 2)) {
				if (trackedId != -1) {
					float scale = canvasScale
							* ((float) getMain().currentZoom / 100);
					Vec2 pos = new Vec2(mouseX / scale, mouseY / scale);
					pos = box2d.coordPixelsToWorld(pos);
					float angle = State.molecules.get(trackedId).getAngle();
					Molecule trackedMole = State.molecules.get(trackedId);
					// System.out.println("Velocity is "+trackedMole.getLinearVelocity());
					Vec2 prePos = new Vec2(trackedMole.getPosition());
//					if(startDraggingMolecule) 
//					//Set molecule velocity to (0,0) when drag starts
//					{
						trackedMole.setLinearVelocity(new Vec2(0, 0));
//						startDraggingMolecule = false;
//					}
					trackedMole.setPosition(pos, angle);
					float ratio = 20;
					dragSpeed = pos.sub(prePos).mul(ratio);
					calculateKE();
					// System.out.println("Drag speed id "+dragSpeed);
				} else // Drag the canvas
				{
					// Dragging all molecules
					isDragging = true;
					// Reseting boundaries position
					boundaries.moveBoundary(xDrag - xTmp, yDrag - yTmp);
					moveAllMolecules(xDrag - xTmp, yDrag - yTmp);
					moveAllAnchors(xDrag - xTmp, yDrag - yTmp);
				}
			} else // Drag the whole canvas, everything on the canvas moves with
					// dragging
			{
				// Dragging all molecules
				isDragging = true;
				// Reseting boundaries position
				boundaries.moveBoundary(xDrag - xTmp, yDrag - yTmp);
				moveAllMolecules(xDrag - xTmp, yDrag - yTmp);
				moveAllAnchors(xDrag - xTmp, yDrag - yTmp);
			}
			// TODO: reset anchors
		}
		}
	}
	
	//Move molecule with a specified vector
	private void moveAllMolecules(float xVec,float yVec)
	{
		for(Molecule mole: State.getMolecules())
		{
			mole.move(xVec,yVec);
		}
	}
	
	//Move Anchors with a specified vector
	private void moveAllAnchors(float xVec,float yVec)
	{
		for(Anchor anchor: State.getAnchors())
		{
			anchor.move(xVec, yVec);
		}
	}

	public void endContact(Contact c) {
	}

	public void postSolve(Contact c, ContactImpulse i) {
	}

	public void preSolve(Contact c, Manifold m) {
	}

	/********************************* Get and Set functions *************************************/
	
	public float getDataTickY(int indexOfGraph, int indexOfCompound)
	{
		return unitList.getDataTickY(unit,sim,set,indexOfGraph,indexOfCompound);
	}
	public double getDataTickX(int indexOfGraph)
	{
		return unitList.getDataTickX(unit,sim,set,indexOfGraph);
	}
	
	public ArrayList<String> getNameTableView()
	{
		return unitList.getNameTableView(unit,sim,set);
	}
	
	public float getDataTableView(int indexOfCompound)
	{
		return unitList.getDataTableView(unit,sim,set,indexOfCompound);
	}
	
	public UnitList getUnitList()
	{
		return unitList;
	}
	public Unit2 getUnit2() {
		return unitList.getUnit2();
	}
	public Unit3 getUnit3() {
		return unitList.getUnit3();
	}
	public Unit4 getUnit4() {
		return unitList.getUnit4();
	}
	public Unit5 getUnit5()	{
		return unitList.getUnit5();
	}
	public Unit6 getUnit6()	{
		return unitList.getUnit6();
	}
	public Unit7 getUnit7(){
		return unitList.getUnit7();
	}
	public Unit8 getUnit8(){
		return unitList.getUnit8();
	}
	public Unit9 getUnit9(){
		return unitList.getUnit9();
	}
	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public TableView getTableView() {
		return main.getTableView();
	}
	public PBox2D getBox2d()
	{
		return box2d;
	}
	
	public int getUnit()
	{
		return unit;
	}
	public int getSim()
	{
		return sim;
	}
	public int getSet()
	{
		return set;
	}
	public boolean isFirstRun()
	{
		return firstRun;
	}
	/*
	//Set restitution of molecules, in order to make them not that bouncy
	public void setRestitutionDamp(boolean b)
	{
		for( int i =0;i<State.molecules.size();i++)
		{
			State.molecules.get(i).setRestitutionDamp(b);
		}
	}*/
	
	//Get molecule density which is specified in each unit
	public float getMoleculeDensity(String moleName)
	{
		return unitList.getMoleculeDensity(unit,sim,set,moleName);
	}
	public boolean isSimSelected(int unitNum,int simNum, int setNum)
	{
		return (unit==unitNum && sim ==simNum && set ==setNum);
	}
	
	public boolean isSimSelected(int unitNum,int simNum)
	{
		return (unit==unitNum && sim ==simNum );
	}
	
	public void setIfConstrainKE(boolean flag)
	{
		ifConstrainKE = flag;
	}
	public void setEnableDrag(boolean flag)
	{
		enableDrag = flag;
	}
	public boolean getEnableDrag()
	{
		return enableDrag;
	}
	public boolean isDragging()
	{
		return isDragging;
	}
	public Boundaries getBoundaries()
	{
		return boundaries;
	}
}
