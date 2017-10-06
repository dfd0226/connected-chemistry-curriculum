package simulations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import main.Main;
import net.miginfocom.swing.MigLayout;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.ColorScales;
import Util.SimpleBar;

import data.State;

import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

public class Unit4 extends UnitBase {
	
	private int collisionCount = 0;
	private int frameCounter = 0;
	private int computeTriggerInterval = p5Canvas.FRAME_RATE*5;
//	private float lastXDrag=0;
//	private float lastYDrag=0;
	private int numMoleculePerMole = 1;
	private double actualVolumePerMole = 0.0272;
	
	//Parameter for trails
	int trailFastColor = Color.RED.getRGB();
	int trailSlowColor = Color.BLUE.getRGB();
	ArrayList<Vec2> collisionPositions = new ArrayList<Vec2>();
	ArrayList<Color> collisionColors = new ArrayList<Color>();
	Color colorMax = Color.red;
	Color colorMin = Color.blue;
	int trailMoleculeId = 0; // Keep track of id of molecule whose trail is
	// showing, used in Unit 4 Sim 2
	private float trailDist = 600f;
	
	//Output labels
	//Lables used in Unit 4
	public JLabel lblPressureText;
	public JLabel lblPressureTitle;
	public JLabel lblPressureValue;
	public JLabel lblCollisionTitle;
	public JLabel lblCollisionValue;
	public JLabel lblMolecule1MolText;
	public JLabel lblMolecule2MolText;
	public JLabel lblMolecule1MolValue;
	public JLabel lblMolecule2MolValue;
	public JLabel lblVolumeText;
	public JLabel lblVolumeTitle;
	public JLabel lblVolumeTitle2;   //for Unit4 Sim1 Set2
	public JLabel lblVolumeValue;   
	public JLabel lblVolumeValue2; //for Unit4 Sim1 Set2
	public JLabel lblActualVolumeTitle;
	public JLabel lblActualVolumeValue;
	
	public JLabel lblEqualText;
	public JLabel lblMolText;
	public JLabel lblMolValue;
	public JLabel lblRText;
	public JLabel lblRValue;
	public JLabel lblTempText;
	public JLabel lblTempTitle;
	public JLabel lblTempValue;
	public JLabel lblKETitle;
	public JLabel lblKEValue;
	public JLabel lblMoleTitle;
	public JLabel lblMoleValue;
	public JLabel lblMultiplicationText1;
	public JLabel lblMultiplicationText2;
	public JLabel lblMultiplicationText3;
	public SimpleBar barPressure;
	public SimpleBar barVolume;
	public SimpleBar barMol;
	public SimpleBar barTemp;
	
	
	public JButton btnRealGas;
	public JButton btnIdealGas;
	public JLabel lblRealGas;
	public JLabel lblIdealGas;
	//Attraction value for real gas
	float attraction = 0.0f;
	boolean attractionEnabled = false;
	ActionListener btnRealGasListener;
	ActionListener btnIdealGasListener;
	float defaultVolume;
	float compressedVolume;
	float volumeIncrement;


	public Unit4(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 4;
		setupSimulations();
		lastTemp = p5Canvas.temp;
		lastVolume = (int)p5Canvas.currentVolume;
		setupListeners();
		setupOutputLabels();

	}
	
	private void setupOutputLabels()
	{
		Main main = p5Canvas.getMain();
		//Intialize labels for unit 4
		lblPressureText = new JLabel ("P (kPa)");
		lblPressureTitle = new JLabel ("Pressure:" );
		lblPressureValue = new JLabel("");
		lblCollisionTitle = new JLabel("Collisions in last 5 sec:");
		lblCollisionValue = new JLabel("");
		lblMolecule1MolText = new JLabel();
		lblMolecule1MolValue = new JLabel(" mol");
		lblMolecule2MolText = new JLabel();
		lblMolecule2MolValue = new JLabel(" mol");
		lblVolumeText = new JLabel("V (L)");
		lblVolumeTitle = new JLabel("Volume of gas:");
		lblVolumeValue = new JLabel (" L");
		lblVolumeTitle2 = new JLabel("Volume of gas:");
		lblVolumeValue2 = new JLabel(" L");
		lblActualVolumeTitle = new JLabel("Atomic volume of Helium:");
		lblActualVolumeValue = new JLabel("0.550 L");
		lblEqualText = new JLabel("=");
		lblMolText = new JLabel ("n (mol)");
		lblMolValue = new JLabel ("");
		lblRText = new JLabel ("R");
		lblRValue = new JLabel("<html><br>8.3145<br><u>L*kPa</u><br>mol*K</html>");
		lblTempText = new JLabel("T (K)");
		lblTempTitle = new JLabel ("Temperature:");
		lblTempValue = new JLabel (" K");
		lblKETitle = new JLabel("Kinetic Energy:");
		lblKEValue = new JLabel(" J");
		lblMoleTitle = new JLabel("Mole of gas:");
		lblMoleValue = new JLabel(" mol");
		lblMultiplicationText1 = new JLabel("*");
		lblMultiplicationText2 = new JLabel("*");
		lblMultiplicationText3 = new JLabel("*");
		barPressure = new SimpleBar(0,6300,30);
		barVolume = new SimpleBar(main.minVolume,main.maxVolume,63);
		barMol = new SimpleBar(0,50,10);
		barTemp  = new SimpleBar(0,550,298);
		
		btnRealGas = new JButton();
		btnIdealGas = new JButton();
		lblRealGas =new JLabel("Real Gas");
		lblIdealGas = new JLabel("Ideal Gas");
		btnRealGas.addActionListener(btnRealGasListener);
		btnIdealGas.addActionListener(btnIdealGasListener);
	}

	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 2);
		String[] elements0 = {"Helium"};
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);
		
		simulations[1] = new Simulation(unitNum, 1, 1);
		String[] elements1 = { "Chlorine","Oxygen"};
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);
		
		simulations[2] = new Simulation(unitNum, 2, 1);
		String[] elements2 = {"Bromine"};
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);
		
		simulations[3] = new Simulation(unitNum, 3, 1);
		String[] elements3 = {"Helium"};
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 5, 1);
		String[] elements4 = {"Helium"};
		SpawnStyle[] spawnStyles4 = { SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 5, 2);
		String[] elements5 = {"Helium"};
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyles5);
		
		simulations[6] = new Simulation(unitNum, 5, 3);
		String[] elements6 = {"Helium"};
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 4, 1);
		String[] elements7 = {"Helium","Oxygen","Chlorine","Carbon-Dioxide"};
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);
		
		simulations[8] = new Simulation(unitNum, 6, 1);
		String[] elements8 = {"Ammonia"};
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas };
		simulations[8].setupElements(elements8, spawnStyles8);
				
	}
	
	private void setupListeners()
	{
		btnRealGasListener = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				attractionEnabled  = true;
				
				//Set up a timer that push down weight 3 sec after hit real gas button
				if(p5Canvas.currentVolume>compressedVolume)
				{
					// Set up timer, start immediately
					Timer timer = new Timer(1000,  new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							interpolator.setDamping(0.1f);
							interpolator.setAttraction(0.1f);
							interpolator.set(p5Canvas.currentVolume);
							interpolator.target(compressedVolume);
						}
					});
					
					timer.setInitialDelay(2500);
					timer.setRepeats(false);
					timer.start();

				}
				
			}
		};
		
		btnIdealGasListener = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				attractionEnabled = false;

			}
		};
	}

	@Override
	public void updateMolecules(int sim, int set) {
		//No reactions happen in this unit
		if(p5Canvas.isEnable)
		{
			frameCounter++;
			if (frameCounter >= this.computeTriggerInterval)
			{
				//System.out.println("Collision count is "+collisionCount);
				lblCollisionValue.setText(Integer.toString(collisionCount));
				frameCounter = 0;
				collisionCount = 0;
			}
			
			//Update attraction based on temp and pressure
			if(sim==6 && set==1)
			{
				//If real gas, update attraction
				if(attractionEnabled)
				{
					attraction  = 0.1f;
				}
				else //If ideal gas, set attraction to 0
				{
					attraction =0.0f;
				}
			}
		}
	}

	@Override
	protected void reset() {		
		
		//Reset Parameters
		lastTemp = p5Canvas.temp;
		p5Canvas.getMain().volumeSlider.setValue(p5Canvas.currentVolume);
		p5Canvas.getMain().volumeSlider.setEnabled(true);
		interpolator.setDamping(0.3f);
		interpolator.setAttraction(0.1f);
		interpolator.reset();
		collisionPositions.clear();
		collisionColors.clear();
		volumeMagnifier =1000;
		collisionCount = 0;
		attraction = 0.0f;
		attractionEnabled = false;
		
		
		barMol.reset();
		barPressure.reset();
		barVolume.reset();
		barTemp.reset();
		//Setup speed
		setupSpeed();
		
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		
		//Setup temperature
				switch(sim)
				{
				case 2:
					p5Canvas.temp =100;
					break;

				case 4:
					p5Canvas.temp=60;
					break;
				case 5:
					if(set==2)
						needWeight();
					p5Canvas.heatSpeed = 2.5f;
					break;
				case 6:
					if(set==1)
					{	
						needWeight();
						defaultVolume = p5Canvas.currentVolume;
						compressedVolume = 2*defaultVolume/3;
						volumeIncrement = (defaultVolume-compressedVolume)/6;
						interpolator.setDamping(0.1f);
						interpolator.setAttraction(0.08f);
						
					}
					default:
						break;
					
				}
		

	}
	
	
	//Add components to the dynamicPanel of the left panel
	
	public void resetDynamicPanel(int sim, int set) 
	{
		if(sim==6 && set==1)
		{
		JPanel dynamicPanel = p5Canvas.getMain().dynamicPanel;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("insets 6, gap 4","[grow]20[grow]", "[][]"));
		dynamicPanel.add(buttonPanel,"cell 0 3, grow ");
		// Draw Molecule button
		buttonPanel.add(btnIdealGas,"cell 0 0, align center,growx");
		buttonPanel.add(lblIdealGas, "cell 0 1, align center");
		buttonPanel.add(btnRealGas,"cell 1 0, align center,growx");
		buttonPanel.add(lblRealGas,"cell 1 1, align center");
		}
	}
	
	public void resetCheckboxPanel(int sim, int set) 
	{
		Main main = p5Canvas.getMain();
		if(sim==2)
		{
			main.checkBoxPanel.add(main.boxMoleculeTracking,BorderLayout.CENTER);
		}
	}
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		//Customization
		switch(p5Canvas.getSim())
		{
		case 1:
			//Heat slider control disabled
			p5Canvas.getMain().heatSlider.setEnabled(false);
			break;
		case 3:
			p5Canvas.getMain().heatSlider.setEnabled(false);
			break;
		case 5:
			if(p5Canvas.getSet()==1)
			{
				//barPressure.setMax(800);
				p5Canvas.getMain().heatSlider.setEnabled(false);
			}
			else if (p5Canvas.getSet()==2)
			{
				//Make initial volume smaller
				p5Canvas.getMain().volumeSlider.setValue(p5Canvas.currentVolume/2);
				p5Canvas.getMain().volumeSlider.setEnabled(false);
				lastTemp = p5Canvas.temp;
			}
			else if (p5Canvas.getSet()==3)
			{
				p5Canvas.getMain().volumeSlider.setEnabled(false);
				barPressure.setMax(2000);
			}
			break;
		case 4:
			p5Canvas.getMain().volumeSlider.setEnabled(false);
			p5Canvas.getMain().heatSlider.setEnabled(false);
			
			
			HashMap moleculeSliderMap = p5Canvas.getMain().moleculeSliderMap;
			if(!moleculeSliderMap.isEmpty())
			{
			JSlider slider =(JSlider) moleculeSliderMap.get("Helium");
			slider.setValue(8);
			slider.setEnabled(false);
			slider = (JSlider)moleculeSliderMap.get("Oxygen");
			slider.setValue(6);
			slider.setEnabled(false);
			slider = (JSlider) moleculeSliderMap.get("Chlorine");
			slider.setValue(8);
			slider.setEnabled(false);
			slider = (JSlider) moleculeSliderMap.get("Carbon-Dioxide");
			slider.setValue(5);
			slider.setEnabled(false);
			}
			p5Canvas.temp=60;
			
			break;
		case 6:
			p5Canvas.getMain().heatSlider.setEnabled(false);
			p5Canvas.getMain().volumeSlider.setEnabled(false);
			default:
				break;
		
		}
		lastVolume = (int)p5Canvas.currentVolume;

	}

	// Set up speed ratio for molecules
	public void setupSpeed() {

		int set = p5Canvas.getSet();
		int sim = p5Canvas.getSim();
		float speed = 1.0f;
		switch(sim)
		{
		case 1:
			speed = 4;
			if(set==1)
				speed =8 ;
			break;
		case 2:
			speed = 8;
			break;
		case 3:
			speed = 4;
			break;
		case 4:
			speed = 4;
			break;
		case 5:
			speed = 4;
			break;
		case 6:
			speed = 6;
			break;
		}
		getSimulation(sim, set).setSpeed(speed);

	}

	@Override
	protected void computeForce(int sim, int set) {
		clearAllMoleculeForce();

		switch (sim)
		{
		case 6:
				if(set==1)
				{
					computeForceSim6Set1();
				}
			break;
			default:
				break;
		}

		
	}
	
	private void computeForceSim6Set1()
	{
		float forceX = 0.0f;
		float forceY =0.0f;
		
		Vec2 center = new Vec2();
		Vec2 molePos = new Vec2();
		Vec2 distance = new Vec2();
		int numMole = State.getMoleculeNum();
		
		//Get the summation of all pos vectors
		for(Molecule mole: State.getMolecules())
		{
			molePos.set(mole.getPosition());
			center.addLocal(molePos);
		}
		
		//Average the sum
		center.mulLocal(1.0f/numMole);
		
		//Add force to each molecule
		for(Molecule mole : State.getMolecules())
		{
			molePos.set(mole.getPosition());
			distance.set(center.sub(molePos));
			Vec2 direction = normalizeForce(distance);
			forceX = direction.x*attraction;
			forceY = direction.y *attraction;

			for(int i = 0;i<mole.getNumElement();i++)
			{
				mole.sumForceX[i]+=forceX;
				mole.sumForceY[i]+=forceY;
			}
		}
	}
	public Vec2 normalizeForce(Vec2 v) {
		float dis = (float) Math.sqrt(v.x * v.x + v.y * v.y);
		return new Vec2(v.x / dis, v.y / dis);
	}


	// Molecule Trail rendering function used in Unit 4 Sim 2
	public void  displayTrail() {
		Molecule mole = null;
		if (p5Canvas.isEnable && p5Canvas.isSimStarted) {
			if (State.molecules.size() > trailMoleculeId)
				;
			mole = State.molecules.get(trailMoleculeId);
		}
		if (mole == null)
			return;

		p5Canvas.stroke(204, 102, 0);
		Vec2 ori = null;
		Vec2 des = new Vec2(0, 0);
		Vec2 oriPixel = new Vec2();
		Vec2 desPixel = new Vec2();
		float totalDist = 0;
		float segDist = 0;
		float xDiff = 0; // X difference between origin and dest
		float yDiff = 0; // Y difference between origin and dest
		
		// We need to draw collision points in reverse order so that the old
		// ones do not overlay newer one
		// Use stack to save collsion points
		Stack<Vec2> collisionStack = new Stack<Vec2>();
		Stack<Color> cColor = new Stack<Color>();
		boolean isFull = false;
		
		//Add color based on current velocity
		Color color = calculateTrailColor(mole);
		cColor.push(color);

		p5Canvas.strokeWeight(3);
		for (int i = collisionPositions.size() - 1; i >= 0; i--) {
			if (!isFull) {
				if (ori == null) { //Get current molecule position
					ori = new Vec2(mole.getPosition());
					ori = box2d.coordWorldToPixels(ori);
					collisionStack.push(new Vec2(ori));
				}
				des.set(collisionPositions.get(i));
				if (!ori.equals(des)) {
					oriPixel.set(ori);
					desPixel.set(des);
					// Update trail position when user is dragging
					if (p5Canvas.isDragging()) {	
							desPixel.x += (p5Canvas.xDrag);
							desPixel.y += (p5Canvas.yDrag);
					}
					else
					{
						// Calculate distance of the whole trail
						xDiff = (desPixel.x - oriPixel.x);
						yDiff = desPixel.y - oriPixel.y;
						segDist = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
						//stroke(collisionColors.get(i).getRGB());				
						//If trail is too long, trim from tail
						if (totalDist + segDist > trailDist) {
							float realDist = (trailDist - totalDist);
							float ratio = realDist / segDist;
							desPixel.x = xDiff * ratio + oriPixel.x;
							desPixel.y = yDiff * ratio + oriPixel.y;
							// line(oriPixel.x,oriPixel.y,desPixel.x,desPixel.y);
							totalDist += realDist;
							isFull = true;
						} else {
							totalDist += segDist;
							// line(oriPixel.x,oriPixel.y,desPixel.x,desPixel.y);
						}
					}
					if(!collisionColors.isEmpty())
					{	
						if(i<collisionColors.size())
						    cColor.push(collisionColors.get(i));
					}
					collisionStack.push(new Vec2(desPixel));
					ori.set(des);

				}
			} 
			
			else // If the points are enough to draw, we delete redundency
			{
				collisionPositions.remove(i);
				collisionColors.remove(i);
			}
			
		}
		//Ready to draw trail
		if( collisionStack.empty())
			return;
		Vec2 start=new Vec2(collisionStack.pop());
		Vec2 end = null;

		
		while(!collisionStack.empty())
		{
			end = new Vec2(collisionStack.pop());
			if(start!=null && end!=null)
			{
				p5Canvas.stroke(cColor.pop().getRGB());
				p5Canvas.line(start.x,start.y,end.x,end.y);
			}
			else
				break;
			start.set(end);
		}

	}
	
	private Color calculateTrailColor(Molecule mole)
	{
		float averageVelocity = (float) Math.sqrt(2 * p5Canvas.averageKineticEnergy
				/ mole.getBodyMass());
		float ratio = mole.getLinearVelocityScalar() / (averageVelocity);
		float factor = 0.5f;
		float min = 1 - factor;
		float max = 1 + factor;
		int red, green, blue;
		double v = (double) (ratio - min) / (max - min);
		if (v < 0)
			v = 0;
		else if (v > 1)
			v = 1;
		Color color = ColorScales.getColor(1 - v, "redblue", 1f);
		return color;
	}

	@Override
	public boolean addMolecules(boolean isAppEnable, String compoundName,
			int count) {
			boolean res = false;

			int sim = p5Canvas.getSim();
			int set = p5Canvas.getSet();
			Simulation simulation = this.getSimulation(sim, set);
			SpawnStyle spawnStyle = simulation.getSpawnStyle(compoundName);
			if (spawnStyle == SpawnStyle.Gas) {
				res = this.addGasMolecule(isAppEnable, compoundName, count);
			}
			if (res) {
				// Connect new created molecule to table index
				int tIndex = p5Canvas.getTableView().getIndexByName(compoundName);
				int lastIndex = State.molecules.size() - 1;
				for (int i = 0; i < count; i++) {
					State.molecules.get(lastIndex - i).setTableIndex(tIndex);
					State.molecules.get(lastIndex - i).setRatioKE(
							1 / simulation.getSpeed());
				}
			}
		return res;
	}

	@Override
	public void setupReactionProducts(int sim, int set) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginReaction(Contact c) {
		// TODO Auto-generated method stub


		
		switch(p5Canvas.getSim())
		{
		case 2:
				trackMoleculeCollision(c);
		case 3:
			countWallCollision(c);
			break;
		case 5:
			if(p5Canvas.getSet()==2)
				moveTopBoundary(c);
			break;
		case 6:
			if(p5Canvas.getSet()==1)
				interpolateTopBoundary(c);
			break;
		}
	}
	
	private void interpolateTopBoundary(Contact c)
	{
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
		
			float currentVolume = p5Canvas.currentVolume;
			float target = 0f;

			if(!this.attractionEnabled)
			{
				if(currentVolume<defaultVolume-1)
				{

					
					target = currentVolume + volumeIncrement;
					if(target >= defaultVolume)
					{
						target = defaultVolume;
					}
					
					interpolator.setDamping(0.3f);
					interpolator.setAttraction(0.15f);
					interpolator.set(currentVolume);
					interpolator.target(target);
				}
			}

			


	}
	
	
	
	// Function that save collision point of a overwatched molecule
	// Called by beginContact
	private void trackMoleculeCollision(Contact c) {
		Molecule mole = State.molecules.get(trailMoleculeId);
		if (mole == null)
			return;

		Object o1 = c.m_fixtureA.m_body.getUserData();
		Object o2 = c.m_fixtureB.m_body.getUserData();
		if (o1 == null || o2 == null || (o1 != mole && o2 != mole))
			return;

		// There must be one object that equals to mole
		Vec2 pos = new Vec2(mole.getPosition());
		pos = box2d.coordWorldToPixels(pos);
		if(!pos.equals(collisionPositions.get(collisionPositions.size()-1)))
		{
		collisionPositions.add(pos);
		// Calculate colors
		Color color = calculateTrailColor(mole);
		collisionColors.add(color);
		}
		// System.out.println("Collision Saved:"+mole.getPosition());
	}
	

	
	//For Unit4 Sim3, count collision time between molecules and walls
	private void countWallCollision(Contact c)
	{
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
		if (c1.equals("simulations.models.Molecule") && c2.equals("simulations.models.Boundary")) {
			mole = (Molecule) o1;
			boundary = (Boundary) o2;
		}
		else if ( c1.equals("simulations.models.Boundary") && c2.equals("simulations.models.Molecule"))
		{
			mole = (Molecule) o2;
			boundary = (Boundary)o1;
		}
		if( mole ==null ||boundary ==null)
			return;
		
		collisionCount ++;
		
	}

	@Override
	public void initialize() {
		
		// Add the start position of molecule whose trail shows
		if (State.molecules.size() > trailMoleculeId) {
			Vec2 pos = new Vec2(State.molecules.get(trailMoleculeId)
					.getPosition());
			pos = box2d.coordWorldToPixels(pos);
			collisionPositions.add(pos);
			//Color initialColor = ColorScales.getColor(0.5f, "redblue", 1f);
			//collisionColors.add(initialColor);
			// State.molecules.get(trailMoleculeId).setShowTrail(true);
		}
		lastMole = State.getMoleculeNum();
		
	}
	
	public void mouseReleased()
	{
		updateTrailPosition();
	}
	
	//Update collision positions of trail after mouse dragging
	public void updateTrailPosition()
	{
		if(!p5Canvas.startDraggingMolecule)
		{
			
			for(Vec2 vec: this.collisionPositions)
			{
				vec.x+=p5Canvas.xDrag;
				vec.y+=p5Canvas.yDrag;
			}
			
//			lastXDrag = p5Canvas.xDrag;
//			lastYDrag = p5Canvas.yDrag;
		}
	}
	
	public void resetDashboard(int sim,int set)
	{
		//Gas law, showing PV=nRT
		Main main = p5Canvas.getMain();
		JPanel dashboard = main.dashboard;
			String alignStr = new String(", align center");
			
			float volumeMagnifier = getVolumeMagnifier()/1000;
				if( volumeMagnifier != 0)
					main.volumeLabel.setText(p5Canvas.currentVolume + " L");
				else
					main.volumeLabel.setText(p5Canvas.currentVolume + " mL");

			if( sim==1)
			{
				dashboard.add(main.lblElapsedTimeText, "flowx,cell 0 0,alignx right");
				dashboard.add(main.elapsedTime, "cell 1 0");
				if(set ==1 )
				{
					lblVolumeTitle.setText("Volume of Chlorine:");
					lblVolumeValue.setText("63 L");
					lblVolumeTitle2.setText("Volume of Oxygen:");
					lblVolumeValue2.setText("63 L");
					lblMolecule1MolText.setText("Mole of Chlorine:");
					lblMolecule1MolValue.setText("10.0 mol");
					lblMolecule2MolText.setText("Mole of Oxygen:");
					lblMolecule2MolValue.setText("10.0 mol");
					dashboard.add(lblMolecule1MolText, "cell 0 3");
					dashboard.add(lblMolecule1MolValue,"cell 1 3");
					dashboard.add(lblMolecule2MolText, "cell 0 4");
					dashboard.add(lblMolecule2MolValue,"cell 1 4");

					dashboard.add(lblVolumeTitle2, "cell 0 2");
					dashboard.add(lblVolumeValue2,"cell 1 2");
				}
				else if(set ==2 )
				{
					lblVolumeTitle.setText("Volume of Helium:");
					lblVolumeValue.setText("63 L");
					lblMolecule1MolText.setText("Mole of Helium:");
					lblMolecule1MolValue.setText("10.0 mol");
					lblActualVolumeValue.setText("0.272 L");
					dashboard.add(lblMolecule1MolText, "cell 0 2");
					dashboard.add(lblMolecule1MolValue,"cell 1 2");
					dashboard.add(lblActualVolumeTitle, "cell 0 3");
					dashboard.add(lblActualVolumeValue,"cell 1 3");
				}

				dashboard.add(lblVolumeTitle,"cell 0 1");
				dashboard.add(lblVolumeValue,"cell 1 1");
			}
			else if (sim==2)
			{
				dashboard.add(main.lblElapsedTimeText, "flowx,cell 0 0,alignx right");
				dashboard.add(main.elapsedTime, "cell 1 0");
				lblMoleTitle.setText("Mole of gas:");
				lblMoleValue.setText("10.0 mol");
				lblTempValue.setText("373 K");
				lblKETitle.setText("Kinetic Energy:");
				lblKEValue.setText("0.75 J");
				lblVolumeTitle.setText("Volume of Bromine:");
				lblVolumeValue.setText("63 L");
				dashboard.add(lblMoleTitle, "cell 0 1");
				dashboard.add(lblMoleValue, "cell 1 1");
				dashboard.add(lblVolumeTitle,"cell 0 2");
				dashboard.add(lblVolumeValue,"cell 1 2");
				dashboard.add(lblTempTitle,"cell 0 3");
				dashboard.add(lblTempValue,"cell 1 3");
				dashboard.add(lblKETitle,"cell 0 4");
				dashboard.add(lblKEValue,"cell 1 4");
			}
			else if( sim==3)
			{
				dashboard.add(main.lblElapsedTimeText, "flowx,cell 0 0,alignx right");
				dashboard.add(main.elapsedTime, "cell 1 0");
				dashboard.add(lblCollisionTitle,"cell 0 1");
				dashboard.add(lblCollisionValue,"cell 1 1");
				dashboard.add(lblPressureTitle,"cell 0 2");
				dashboard.add(lblPressureValue,"cell 1 2");
				lblCollisionTitle.setText("Collisions in last 5 sec:");
				lblCollisionValue.setText("0");
				lblPressureValue.setText("196.63 kPa");
				
			}
			else if( sim==4)
			{
				dashboard.add(main.lblElapsedTimeText, "flowx,cell 0 0,alignx right");
				dashboard.add(main.elapsedTime, "cell 1 0");
				dashboard.add(lblPressureTitle,"cell 0 1");
				dashboard.add(lblPressureValue,"cell 1 1");
				dashboard.add(lblTempTitle,"cell 0 2");
				dashboard.add(lblTempValue,"cell 1 2");
				lblPressureValue.setText("0 kPa"); 

			}
			else if( sim ==5)
			{
			int barWidth = 40;
			int barHeight = 120;
			dashboard.setLayout(new MigLayout("","[45][8][45][25][45][8][10][8][45]","[][][grow][]"));
			dashboard.add(main.lblElapsedTimeText, "cell 0 3 4 1, align right");
			dashboard.add(main.elapsedTime, "cell 4 3 3 1, align left");
			
			dashboard.add(lblPressureText, "cell 0 0"+alignStr);
			dashboard.add(lblMultiplicationText1, "cell 1 0"+alignStr);
			dashboard.add(lblVolumeText,"cell 2 0"+alignStr);
			main.volumeLabel.setText(main.defaultVolume+ " L");

			dashboard.add(lblEqualText,"cell 3 0"+alignStr);
			dashboard.add(lblMolText,"cell 4 0"+alignStr); 
			dashboard.add(lblMultiplicationText2, "cell 5 0"+alignStr);
			dashboard.add(lblRText,"cell 6 0"+alignStr); 
			dashboard.add(lblMultiplicationText3, "cell 7 0"+alignStr);
			dashboard.add(lblTempText,"cell 8 0"+alignStr); 

			barPressure.setPreferredSize(new Dimension(barWidth,barHeight));
			barVolume.setPreferredSize(new Dimension(barWidth,barHeight));
			barMol.setPreferredSize(new Dimension(barWidth,barHeight));
			barTemp.setPreferredSize(new Dimension(barWidth,barHeight));
			
			//Make R value font size smaller
			Font font = lblRValue.getFont();
			font = font.deriveFont(10.0f);	
			lblRValue.setFont(font);
			
			dashboard.add(barPressure,"cell 0 2"+alignStr);
			dashboard.add(barVolume,"cell 2 2"+alignStr);
			dashboard.add(barMol,"cell 4 2"+alignStr);
			dashboard.add(lblRValue,"cell 6 2,top");
			dashboard.add(barTemp,"cell 8 2"+alignStr);
			if(set==1)
				barPressure.setValue(393.27f);
			else if(set==2)
			{
				barPressure.setValue(1198.83f);
				barVolume.setValue(31.0f);
				barMol.setValue(15.0f);
			}
			else if(set==3)
				barPressure.setValue(393.27f);
			
			}
			else if(sim==6)
			{
				dashboard.add(main.lblElapsedTimeText, "flowx,cell 0 0,alignx right");
				dashboard.add(main.elapsedTime, "cell 1 0");

					lblVolumeTitle.setText("Volume of Ammonia:");
					lblVolumeValue.setText("63 L");
//					lblMolecule1MolText.setText("Mole of Ammonia:");
//					lblMolecule1MolValue.setText("20.0 mol");
					lblTempValue.setText("298 K");
//					lblPressureValue.setText("786.53 kPa");
					
					dashboard.add(lblVolumeTitle,"cell 0 1");
					dashboard.add(lblVolumeValue,"cell 1 1");
//					dashboard.add(lblMolecule1MolText, "cell 0 2");
//					dashboard.add(lblMolecule1MolValue,"cell 1 2");

					dashboard.add(lblTempTitle,"cell 0 2");
					dashboard.add(lblTempValue,"cell 1 2");
//					dashboard.add(lblPressureTitle,"cell 0 3");
//					dashboard.add(lblPressureValue,"cell 1 3");
			}
		
	}

	@Override
	public void updateOutput(int sim, int set) {
		
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;
		if(lblMolecule1MolValue.isShowing())
		{
			
			if(sim==1&&set==2)
			{
				output = myFormatter.format(State.getMoleculeNumByName("Helium")/numMoleculePerMole);
				lblMolecule1MolValue.setText(output+ " mol");
			}
			else if(sim==1&&set==1)
			{
				output = myFormatter.format(State.getMoleculeNumByName("Chlorine")/numMoleculePerMole);
				lblMolecule1MolValue.setText(output + " mol");
			}
			else if( sim==6 &&set==1)
			{
				output = myFormatter.format(State.getMoleculeNumByName("Helium")/numMoleculePerMole);
				lblMolecule1MolValue.setText(output+ " mol");
			}

		}
		if(lblMolecule2MolValue.isShowing())
		{
			output = myFormatter.format(State.getMoleculeNumByName("Oxygen")/numMoleculePerMole);

			lblMolecule2MolValue.setText(output+ " mol");
		}
		
		if (lblVolumeValue.isShowing()) {

			float volumeMagnifier = getVolumeMagnifier()/1000;
			if( volumeMagnifier != 0)
				{
				lblVolumeValue.setText(p5Canvas.currentVolume + " L");
				lblVolumeValue2.setText(p5Canvas.currentVolume + " L");
				}
			else
			{
				lblVolumeValue.setText(p5Canvas.currentVolume
						+ " mL");
				lblVolumeValue2.setText(p5Canvas.currentVolume
						+ " mL");
			}
		}
		if(lblActualVolumeValue.isShowing())
		{
			DecimalFormat formatter = new DecimalFormat("###.###");
			output = formatter.format(this.actualVolumePerMole* State.getMoleculeNumByName("Helium")/this.numMoleculePerMole);
			lblActualVolumeValue.setText(output+" L");
		}
		if (lblTempValue.isShowing()) {
			//Showing temperature as K in this unit
			output = myFormatter.format(p5Canvas.temp+celsiusToK);
			lblTempValue.setText(output + " K");
		}
		if (lblKEValue.isShowing()) {
			output = myFormatter.format(p5Canvas.averageKineticEnergy);
			lblKEValue.setText(output + " J");
		}
		if (lblPressureValue.isShowing()) {
			
			output = myFormatter.format(p5Canvas.pressure);
			lblPressureValue.setText(output + " kPa");
		}
		if (lblMoleValue.isShowing())
		{
			lblMoleValue.setText( p5Canvas.mol + " mol");
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
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		return super.getDataTickY(sim, set, indexOfGraph, indexOfCompound);

	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		return super.getDataTableView(sim, set, indexOfCompound);
	}

	@Override
	protected void initializeSimulation(int sim, int set) {
		
	}

	@Override
	//Update actual volume of gas number after customizeInterface call
	public void updateMoleculeCountRelated(int sim, int set) {

		//this.updateOutput(sim, set);
	}

	@Override
	public void setMoleculeDensity() {
		// TODO Auto-generated method stub
		
	}

}
