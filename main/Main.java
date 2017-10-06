package main;

//Connected Chemistry Simulations
//part of the Connected Chemistry Curriculum

//Project Leader: Mike Stieff, PhD, University of Illinois at Chicago
//Modeled in Processing by: Tuan Dang, Qin Li and Allan Berry

//This software is Copyright � 2010, 2011 University of Illinois at Chicago,
//and is released under the GNU General Public License.
//Please see "resources/copying.txt" for more details.

/*--------------------------------------------------------------------------*/

//This file is part of the Connected Chemistry Simulations (CCS).

//CCS is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//CCS is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with CCS.  If not, see <http://www.gnu.org/licenses/>.

/*--------------------------------------------------------------------------*/

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Component;


import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//import java.awt.event.MouseListener;


import simulations.P5Canvas;
import simulations.models.Compound;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
//import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
//import java.awt.Graphics2D;
import java.awt.Image;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
//import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.apple.eawt.Application;

import static data.YAMLinterface.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import Util.ColorCollection;

//import Util.SimpleBar;

//import com.jtattoo.plaf.smart.SmartLookAndFeel;

//import data.DBinterface;
import data.State;
//import data.YAMLinterface;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//import java.awt.image.BufferedImage;

public class Main {

	public JFrame mainFrame;
	public JMenu simMenu = new JMenu("Choose Simulation");
	private int selectedUnit = 2;
	private int selectedSim = 4;
	private int selectedSet = 1;
	public boolean isWelcomed = true;
	private int sliderValue = 5;

	private int minSliderValue = 1;
	private int maxSliderValue = 9;
	public JScrollPane dynamicScrollPane;
	public ArrayList additionalPanelList = new ArrayList();
	public ArrayList defaultSetMolecules = new ArrayList();
	private CustomPopupMenu scrollablePopupMenu;
	public String[] moleculeNames = null;

	private String lblAddTitle = new String("Add "); // Label parameter on left
														// panel

	public JPanel leftPanel; // "Input" panel on left of application
	public JPanel centerPanel; // "Simulation" panel in the middle of
								// application
	public WelcomePanel welcomePanel; // "Welcome" Panel showing welcome info when
								// application is first opened up
	BufferedImage welcomeImage = null;
	private Canvas canvas = null;
	private TableView tableView;
	private TableSet tableSet;
	private JMenuBar menuBar;

	/**************************** Left Panel parameters ****************************/
	public JPanel dynamicPanel; //Panel that holes molecule icon and add button
	public HashMap<String, JSlider> moleculeSliderMap = new HashMap<String, JSlider>();
	private JLabel lblInput;
	private JLabel lblInputTipR;
	private JLabel lblInputTipL;
	private String [] inputTipTextR;
	private String [] inputTipTextL;
	public HashMap<String, JButton> addBtns = new HashMap<String, JButton>(); // Used
																				// to
																				// store
																				// add
																				// button
																				// for
																				// every
																				// compound
	//Check box parameter
	public JPanel checkBoxPanel;
	public JCheckBox boxMoleculeHiding;
	public JCheckBox boxMoleculeTracking;
	public JCheckBox boxDisplayForce;
	public JCheckBox boxDisplayJoint;
	
	public static final int MAX_COMPOUND_NUM = 50;
	public JButton btnBronsted;   //Unit 8 button
	public JButton btnLewis;      //Unit 8 button

	

	/**************************** Central Panel parameters ****************************/
	private JPanel clPanel; // Center Left control Panel containing volume
							// slider and Zoom Slider
	private JPanel crPanel;
	public JLabel volumeLabel = null;
	public JSlider volumeSlider = null;
	public int minVolume = 0;
	public int maxVolume = 100;
	public int defaultVolume = 63;
	private JLabel lblVolumeText;

	// Pressure slider used to replace Volume Slider in Unit 2
	public JSlider pressureSlider = new JSlider(0, 10, 1);
	public JLabel pressureLabel;
	private JLabel canvasControlLabel_main_pressure;
	public int defaultPressure = 1;
	public int defaultZoom = 100;
	public int currentZoom = defaultZoom;
	public int zoomMin = 30;
	public int zoomMax = 200;
	public JSlider zoomSlider = new JSlider(zoomMin, zoomMax, defaultZoom);
	public JLabel canvasControlLabel_main_scale;
	public JLabel zoomLabel = null;
	public int defaultSpeed = 100;
	public JLabel speedLabel;
	public JSlider speedSlider = new JSlider(0, 100, defaultSpeed);
	public JLabel canvasControlLabel_main_speed;
	//Heat is input
	public int defaultHeat = 50;
	public int heatMin = 0;
	public int heatMax = 100;
	public int heatTickSpacing = 10;
	public JLabel heatLabel ;
	public JLabel canvasControlLabel_main_heat;
	public JSlider heatSlider = new JSlider(heatMin, heatMax, defaultHeat);
	//Temp is output. Used for simple bar

	
	public JLabel lblPlaceHolder;
	
	//play, reset button and their listeners
	public JButton playBtn;
	private ActionListener playBtnListener;
	public boolean isFirst = true;
	public JButton resetBtn;
	private ActionListener resetBtnListener;

	/***************************** Right Panel Parameter ***********************************/
	public JPanel rightPanel; // Right panel container
	public JLabel lblOutput; // output label
	public JTabbedPane tabpanelGraph;
	private JPanel panelCanvas;
	public JButton btnGraphSwitch;
	JLabel lblSubMicroscopid;
	JLabel lblOutputMacroscopicLevel;
	public JCheckBox cBoxHideWater;
	ItemListener cBoxHideWaterListener;
	public JPanel dashboard; // Subpanel on right side showing parameter values

	public boolean isVolumeblocked = false;
	public JLabel totalSystemEnergy;
	public JLabel averageSystemEnergy;

	public JLabel lblElapsedTimeText;
	public JLabel elapsedTime; // "Elapsed Set Time" label

	//public JPanel outputControls;





	private P5Canvas p5Canvas;
	public int pause = 0; // the length of the pause at the beginning
	public int speed = 1000; // recur every second.
	public Timer timer;
	public int time = 0;

	private MouseAdapter mulBtnListener; // mouseListener used for 6 buttons in
											// Unit3 Sim1
	private ActionListener btnCatalystListener;
//	private ActionListener btnInhibitorListener;
	private ActionListener btnInertListener;
	private ActionListener btnSparkListener;
	private ActionListener btnBronstedListener;
	private ActionListener btnLewisListener;
	private ActionListener btnGraphSwitchListener;
	private ActionListener btnAddMoleculeListener;
	private ArrayList<Integer> btnIds = new ArrayList<Integer>(); // Button Ids
																	// in Unit3
																	// Sim2
	private ArrayList<String> btnNames = new ArrayList<String>(); // Button
																	// names in
																	// Unit3
																	// Sim2
	// private int btnStartId =-1;
	private boolean started = false; // boolean flag for unit 3 sim 2

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Connected Chemistry");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					// take the menu bar off the jframe
					//System.setProperty("apple.laf.useScreenMenuBar", "true");

					// set the name of the application menu item
					//System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Connected Chemistry");

					/*
		            // setup the look and feel properties
		            Properties props = new Properties();
		            
		            props.put("logoString", "my company"); 
		            props.put("licenseKey", "INSERT YOUR LICENSE KEY HERE");
		            
		            props.put("selectionBackgroundColor", "180 240 197"); 
		            props.put("menuSelectionBackgroundColor", "180 240 197"); 
		            
		            props.put("controlColor", "218 254 230");
		            props.put("controlColorLight", "218 254 230");
		            props.put("controlColorDark", "180 240 197"); 

		            props.put("buttonColor", "218 230 254");
		            props.put("buttonColorLight", "255 255 255");
		            props.put("buttonColorDark", "244 242 232");

		            props.put("rolloverColor", "218 254 230"); 
		            props.put("rolloverColorLight", "218 254 230"); 
		            props.put("rolloverColorDark", "180 240 197"); 

		            props.put("windowTitleForegroundColor", "0 0 0");
		            props.put("windowTitleBackgroundColor", "180 240 197"); 
		            props.put("windowTitleColorLight", "218 254 230"); 
		            props.put("windowTitleColorDark", "180 240 197"); 
		            props.put("windowBorderColor", "218 254 230");
		            
		            // set your theme
		            SmartLookAndFeel.setCurrentTheme(props);*/
		            //com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme("Blue-Large-Font", "INSERT YOUR LICENSE KEY HERE", "my company");
					//UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
					Main window = new Main();
					ImageIcon imageIcon = new ImageIcon(Main.class.getResource("/resources/png64x64/simIcon.png"));
					Image icon = imageIcon.getImage();
					window.mainFrame.setIconImage(icon);
					window.mainFrame.setVisible(true);
					window.mainFrame.setTitle("Connected Chemistry");
					
					//Set up dock icon on Mac
					String osName = System.getProperty("os.name").toLowerCase();
					boolean isMacOs = osName.startsWith("mac os x");
					if (isMacOs) 
					{
						Application application = Application.getApplication();
						application.setDockIconImage(icon);

					}
					

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		ColorCollection.setupColorGlobal();

		setP5Canvas(new P5Canvas(this));
		setTableView(new TableView(this));
		setCanvas(new Canvas(this));

		setTableSet(new TableSet(this));
		scrollablePopupMenu = new CustomPopupMenu(this);
		initialize();
//		try {
//		    welcomeImage = ImageIO.read(new File("resources/png64x64/simSplashScreen.png"));
//		} catch (IOException e) {
//		}
	}

	protected String[] parseNames(String[] files) {
		int numMolecules = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".png")) {
				numMolecules++;
			}
		}
		String[] moleculeNames = new String[numMolecules];
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".png")) {
				moleculeNames[count] = files[i].split(".png")[0];
				count++;
			}
		}
		return moleculeNames;
	}

	String[] getResourceListing(Class clazz, String path)
			throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		// System.out.println(dirURL);

		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5,
					dirURL.getPath().indexOf("!")); // strip out only the JAR
													// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries
															// in jar
			ArrayList result = new ArrayList(); // avoid duplicates in case it
												// is a subdirectory
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();

				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory
						// name
						entry = entry.substring(0, checkSubdir);

					}
					result.add(entry);
				}
			}
			Collections.sort(result);
			String[] resultArray = new String[result.size()];
			for (int i = 0; i < result.size(); i++) {
				resultArray[i] = result.get(i).toString();
			}
			return resultArray;
		}

		throw new UnsupportedOperationException("Cannot list files for URL "
				+ dirURL);
	}


	/******************************************************************
	 * FUNCTION : updateDynamicPanel DESCRIPTION : Update molecule legends on
	 * the left panel. Called when reset.
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public void resetDynamicPanel() {
		if (dynamicPanel != null) {
			dynamicPanel.removeAll();
			//defaultSetMolecules = new ArrayList();
			started = false;
			btnIds.clear();
			btnNames.clear();
			moleculeSliderMap.clear();

			if (!(selectedUnit == 3 && selectedSim == 2)) {
				// Get Compounds information in selected set from Yaml file
				ArrayList compounds = getSetCompounds(selectedUnit,
						selectedSim, selectedSet);
				if (compounds != null) {

					dynamicPanel.setLayout(new MigLayout("insets 6",
							"[200.00,grow]", "[][]"));
					dynamicScrollPane
							.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					int start =0;
					if(selectedUnit==4||(selectedUnit==7&&selectedSim==2))
					{
						JPanel volumePanel = new JPanel();
						//volumePanel.setBackground(backgroundColor);
						volumePanel.setLayout(new MigLayout("insets 0, gap 0",
								"[78][][85]", "[50]"));
						dynamicPanel.add(volumePanel, "cell 0 0,grow");
						volumePanel.add(volumeLabel,"cell 2 0,align right");
						volumeSlider.setOrientation(SwingConstants.HORIZONTAL);
						volumeSlider.setEnabled(true);
						volumePanel.add(volumeSlider,"cell 1 0,grow");
						volumePanel.add(lblVolumeText,"cell 0 0,align left");
						JPanel heatPanel = new JPanel();
						heatPanel.setLayout(new MigLayout("insets 0, gap 0",
								"[78][][85]", "[50]"));
						dynamicPanel.add(heatPanel,"cell 0 1, grow");
						heatPanel.add(heatLabel,"cell 2 0,align right");
						heatSlider.setOrientation(SwingConstants.HORIZONTAL);
						heatSlider.setEnabled(true);
						heatPanel.add(heatSlider,"cell 1 0, grow");
						heatPanel.add(canvasControlLabel_main_heat,"cell 0 0,align left");
						start=2;
					}
					//LOOP: Add molecule legends for all the molecules
					int i = 0;
					for (i =0; i < compounds.size(); i++) {
						// Get Compound Name
						String cName = getCompoundName(selectedUnit,
								selectedSim, selectedSet, i);
						final String fixedName = cName.replace(" ", "-");
						if(getSelectedUnit()==7&&getSelectedSim()==2 && (fixedName.equals("Catalyst")||fixedName.equals("Inert")))
						{
							State.moleculesAdded.put(fixedName, 0); // Initialize
							continue;
						}
						JPanel panel = new JPanel();
						panel.setBackground(ColorCollection.getColorMainBackground());
						panel.setLayout(new MigLayout("insets 6, gap 0",
								"[][][69.00]", "[][]"));
						dynamicPanel.add(panel, "cell 0 " + (i+start) + ",grow");
						//additionalPanelList.add(panel);
						panel.setName(cName);

						//Add Compound Name label
						JLabel label = new JLabel(cName);
						// Repaint molecules icon
							String location = new String ("/resources/compoundsPng50/"+ fixedName + ".png");
							URL url = Main.class.getResource(location);
							if (url==null) {
							  label.setText(cName);
							} 
							else
							{
								ImageIcon icon = new ImageIcon(url);
								//icon = scaleImageIcon(icon, 40, 30);
								if(icon!=null)
									label.setIcon(icon);
							}
						
						panel.add(label, "cell 0 0 3 1,growx");

						// Add Slider label
						final JLabel label_1 = new JLabel(lblAddTitle + sliderValue);
						label_1.setName("lblAmount");
						
						//In Unit 2, we show "grams" that will be added instead of number of molecules
						//mToMass should be set the same value as that in Unit2
						if(selectedUnit == 2 )
						{
							int mToMass = 10;
							if(selectedSet == 4  )
								mToMass = 20;
							label_1.setText(lblAddTitle + sliderValue*mToMass + "g" );
						}
						panel.add(label_1, "cell 0 1");

						// Add slider and set up slider event listener
						final JSlider slider = new JSlider(minSliderValue,
								maxSliderValue, sliderValue);
						panel.add(slider, "cell 1 1");
						moleculeSliderMap.put(fixedName,slider);
						slider.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								int value = ((JSlider) e.getSource())
										.getValue();

								if(selectedUnit == 2 )
								{
									int mToMass = 10;
									if((selectedSim==2 &&selectedSet == 4 )||(selectedSim==3&&selectedSet==4) )
										mToMass = 20;
									label_1.setText(lblAddTitle + value*mToMass + "g" );
								}
								else
									label_1.setText(lblAddTitle + value);
							}
						});

						// Repaint Add buttion and set up button event listener
						JButton addBtn = new JButton("");
						addBtn.setIcon(new ImageIcon(Main.class
								.getResource("/resources/png16x16/plus.png")));
						panel.add(addBtn, "cell 2 1,growy");
						addBtns.put(fixedName, addBtn); // Associate add button
														// info with compound
														// names
						State.moleculesAdded.put(fixedName, 0); // Initialize
																// moleculesAdded
						addBtn.addActionListener(btnAddMoleculeListener);

					}
					if(getSelectedUnit()==7&&getSelectedSim()==2) //Add catalyst button and inert gas button in Unit 6 Sim 2
					{
						JPanel buttonPanel = new JPanel();
						buttonPanel.setLayout(new MigLayout("insets 6, gap 4","[grow]20[grow]", "[][]"));
						dynamicPanel.add(buttonPanel, "cell 0 " + (i+start) + ",grow");
						// Draw Molecule button
						JButton btnCatalyst = new JButton();
						JLabel lblCatalyst = new JLabel("Add Catalyst");
						btnCatalyst.setIcon(new ImageIcon(Main.class
								.getResource("/resources/compoundsPng50/Catalyst.png")));
						btnCatalyst.addActionListener(btnCatalystListener);
						JButton btnInertGas = new JButton();
						JLabel lblInertGas = new JLabel("Add Inert Gas");
						btnInertGas.setIcon(new ImageIcon(Main.class
								.getResource("/resources/compoundsPng50/Inert.png")));
						btnInertGas.addActionListener(btnInertListener);
						buttonPanel.add(btnCatalyst,"cell 0 0, align center,growx");
						buttonPanel.add(lblCatalyst, "cell 0 1, align center");
						buttonPanel.add(btnInertGas,"cell 1 0, align center,growx");
						buttonPanel.add(lblInertGas,"cell 1 1, align center");
					}
					if(getSelectedUnit()==6)  //Add "Add Spark" button 
					{
						if(getSelectedSim()==1||getSelectedSim()==3)//Add spark button to Unit7 Sim1 and Sim3
						{
							JPanel buttonPanel = new JPanel();
							buttonPanel.setLayout(new MigLayout("insets 6, gap 4","[100]20[]","[50][]"));
							dynamicPanel.add(buttonPanel, "cell 0 " + (i+start) + ",grow");
							
							JButton btnSpark = new JButton();
							JLabel lblSpark = new JLabel("Add Spark");
							btnSpark.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/Spark.png")));
							//Dimension di = new Dimension(buttonPanel.getWidth(),30);
							//btnSpark.setSize(di);
							btnSpark.addActionListener(btnSparkListener);
							buttonPanel.add(btnSpark,"cell 0 0, align center, grow");
							buttonPanel.add(lblSpark,"cell 0 1, align center");
						}
					}
					if(getSelectedUnit()==8 && getSelectedSim()==2)
					{
						JPanel buttonPanel = new JPanel();
						buttonPanel.setLayout(new MigLayout("insets 6, gap 4","[grow]20[grow]", "[][]"));
						dynamicPanel.add(buttonPanel, "cell 0 " + (i+start) + ",grow");
						// Draw Molecule button
						JLabel lblBronsted = new JLabel("<html>Bronsted<br>/Lowry</html>");

						JLabel lblLewis = new JLabel("Lewis");
						buttonPanel.add(btnBronsted,"cell 0 0, align center,growx");
						buttonPanel.add(lblBronsted, "cell 0 1, align center");
						buttonPanel.add(btnLewis,"cell 1 0, align center,growx");
						buttonPanel.add(lblLewis,"cell 1 1, align center");
					}
				}
				
			}
			else // In unit3 sim 2 case
			{
				// Get Compounds information in set 1 from Yaml file
				ArrayList compounds = getSetCompounds(selectedUnit,
						selectedSim, selectedSet);
				if (compounds != null) {
					int rowNum = 4;
					int colNum = 2;
					// Panel containing multi-buttons
					JPanel multiButtonPanel = new JPanel();
					multiButtonPanel.setLayout(new MigLayout("insets 8,gap 0",
							"[50]10[50]", "[][]5[][]5[][]"));
					// Panel rendering instruction
					JLabel instructionPanel = new JLabel();
					instructionPanel
							.setText("<html>Select two substances below"
									+ "<p> and then press play button</html>");
					dynamicPanel.setLayout(new MigLayout("insets 0,gap 0", "",
							"[][]"));
					dynamicScrollPane
							.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					dynamicPanel.add(instructionPanel, "cell 0 0, center");
					dynamicPanel.add(multiButtonPanel, "cell 0 1, center");
					for (int i = 0; i < compounds.size(); i++) {

						// Get Compound Name
						String cName = getCompoundName(selectedUnit,
								selectedSim, selectedSet, i);
						defaultSetMolecules.add(cName);
						if (cName.equals("Water"))
							continue;
						JLabel lblMolecule = new JLabel(cName);
						lblMolecule.setFont(new Font("Lucida Grande",
								Font.PLAIN, 12));
						multiButtonPanel.add(lblMolecule, "cell " + i % colNum
								+ " " + ((i / colNum) * 2 + 1) + ", center"); // cell
																				// column
																				// row
						final String fixedName = cName.replace(" ", "-");
						// Draw Molecule button
						JButton btMolecule = new JButton();
						multiButtonPanel
								.add(btMolecule,
										"cell "
												+ i
												% colNum
												+ " "
												+ ((i / colNum) * 2)
												+ ", center, width 80:100:120, height 50: 55: 80");
						btMolecule.setIcon(new ImageIcon(Main.class
								.getResource("/resources/compoundsPng50/"
										+ fixedName + ".png")));
						// At the same time store fixed names into btnNames list
						this.btnNames.add(fixedName);
						btMolecule.addMouseListener(mulBtnListener);
					}
				}
			}
			
			p5Canvas.resetDynamicPanel();
			
		}
	}
	
	private ImageIcon scaleImageIcon(ImageIcon origin,int w, int h)
	{
		/*Image src = origin.getImage();
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        g2.drawImage(src, 0, 0, w, h, this);
        g2.dispose();
        return new ImageIcon(dst);*/
		
		Image img = origin.getImage();
		Image newImg = img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(newImg);

	}

	public void createPopupMenu() {

		for (int i = 0; i < moleculeNames.length; i++) {
			CustomButton xx = new CustomButton(this, moleculeNames[i].replace(
					"-", " "));
			xx.setIcon(new ImageIcon(Main.class
					.getResource("/resources/compoundsPng50/"
							+ moleculeNames[i] + ".png")));
			xx.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// scrollablePopupMenu.hidemenu();
				}
			});
			scrollablePopupMenu.add(xx, i);
		}
	}

	public void reset() {
		// boolean temp = getP5Canvas().isEnable;
		
		//Stop drawing lines on graph
		getCanvas().setPaintLineEnable(false);
		
		// Disable p5Canvas and stop timer
		timer.stop();
		
		getP5Canvas().isEnable=false;
		
		//Reset parameter
		resetParameter();
	
		/* Check if welcome menu showing */
		if (isWelcomed && welcomePanel != null) {
			mainFrame.remove(welcomePanel);
			mainFrame.getContentPane().setLayout(
					new MigLayout("insets 0, gap 0", "[285.00][480px,grow][320px]",
							"[][][grow]"));
			mainFrame.getContentPane().add(leftPanel, "cell 0 0,grow");
			mainFrame.getContentPane().add(centerPanel, "cell 1 0,grow");
			mainFrame.getContentPane().add(rightPanel, "cell 2 0,grow");
			isWelcomed = false;
		}
		// Reset state parameter
		State.reset();
		
		//Reset Compound
		Compound.reset();

		// Reset p5Canvas
		getP5Canvas().reset();
		
		// Update sliders around the center panel
		resetCenterPanel();
		
		resetLeftPanel();

		// Reset right panel
		resetRightPanel();
		
		addMolecules();
		
		//Initialize mass and consentration for simulation
		p5Canvas.initializeSimulation();
		
		//Reset table view on the panelCanvas 
		getTableView().reset();
		
		getCanvas().reset();
		
		//Customization based on requirements
		p5Canvas.customizeInterface();
		// getP5Canvas().isEnable =temp;

		// reset timer
		resetTimer();
			
		//Initialize molecule Count Related Value to get the right output
		p5Canvas.updateMoleculeCountRelated();
		getTableView().updateTableView();
		//Add the inital data to Graph
		canvas.addDataPerTick();
		


		

	}

	public void addMolecules()
	{
		// Load information of new generation
		if (!(selectedUnit == 3 && selectedSim == 2)) {
			ArrayList a = getSetCompounds(selectedUnit, selectedSim,
					selectedSet);
			if (a != null) {
//				Compound.names.clear();
//				Compound.counts.clear();
//				Compound.caps.clear();
				getTableView().clearData();
				for (int i = 0; i < a.size(); i++) {
					String s = (String) getCompoundName(selectedUnit,
							selectedSim, selectedSet, i);
					int num = Integer.parseInt(getCompoundQty(selectedUnit,
							selectedSim, selectedSet, i).toString());
					int cap = Integer.parseInt(getCompoundCap(selectedUnit,
							selectedSim, selectedSet, i).toString());
					s = s.replace(" ", "-");
					Compound.names.add(s);
					Compound.counts.add(num);
					Compound.caps.add(cap);
					// Update tableview before new molecule added
					getTableView().addData(s);
					if (num > 0) {
						// Add initial number of molecules into p5Canvas
						if (getP5Canvas().addMoleculeRandomly(s, num)) {
							State.moleculesAdded.put(s, num);
							// Need to add these molecules to canvas also
						}
					}
				}
				getP5Canvas().setupReactionProducts();
				Compound.setProperties();
			}
		}
	}

	
	//Reset Global Parameter
	public void resetParameter()
	{

		currentZoom = defaultZoom;
		//volumeSlider.setValue(defaultVolume);
		pressureSlider.setValue(defaultPressure);
		zoomSlider.setValue(currentZoom);
		speedSlider.setValue(defaultSpeed);
		heatSlider.setValue(defaultHeat);
		volumeSlider.setValue(defaultVolume);
		volumeLabel.setText(defaultVolume+ " mL");
		
		addBtns.clear();

	
		
	}
	
	//Reset left panel
	private void resetLeftPanel()
	{
		//Change play icon to "PAUSE"
		playBtn.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png48x48/iconPlay.png")));
		
		// For UNIT 2, Sim 3, ALL SETS, add input tip below Input title
//		if (selectedUnit == 2 && (selectedSim == 3||selectedSim==2)) {
//			leftPanel.add(lblInputTipL,
//					"cell 0 1,gaptop 5,gapleft 5,alignx left,width 45::");
//			leftPanel.add(lblInputTipR, "cell 0 1,gaptop 5,alignx right");
//			if(selectedSim==1||selectedSim==2)
//			{
//				lblInputTipL.setText(inputTipTextL[0]);
//				lblInputTipR.setText(inputTipTextR[0]);
//			}
//			else
//			{
//				lblInputTipL.setText(inputTipTextL[1]);
//				lblInputTipR.setText(inputTipTextR[1]);
//			}
//		} else
//		
//		{
//			if (leftPanel.isAncestorOf(lblInputTipL)) {
//				leftPanel.remove(lblInputTipL);
//				leftPanel.remove(lblInputTipR);
//			}
//
//		}
		
		//Reset TableSet
		getTableSet().reset();
		
		// Update Molecule Legends on left panel
		resetDynamicPanel();
		
		resetCheckboxPanel();
		
		leftPanel.updateUI();
		
	}
	private void resetCheckboxPanel()
	{
		checkBoxPanel.removeAll();
		//Reset selected value
		boxMoleculeHiding.setSelected(false);
		boxMoleculeHiding.setEnabled(true);
		boxMoleculeTracking.setSelected(false);
		boxMoleculeTracking.setEnabled(true);
		boxDisplayForce.setSelected(false);
		boxDisplayForce.setEnabled(true);
		boxDisplayJoint.setSelected(false);
		boxDisplayJoint.setEnabled(true);
		cBoxHideWater.setSelected(false);
		cBoxHideWater.setEnabled(true);
		//Re-add checkboxes to parent panel
		checkBoxPanel.add(boxMoleculeHiding, BorderLayout.NORTH);
		//checkBoxPanel.add(boxDisplayForce, BorderLayout.SOUTH);
		checkBoxPanel.add(boxDisplayForce, BorderLayout.SOUTH);
		//,checkBoxPanel.add(boxDisplayJoint, BorderLayout.SOUTH);

		p5Canvas.resetCheckboxPanel();
//		if(p5Canvas.isSimSelected(4, 2))
//			checkBoxPanel.add(boxMoleculeTracking,BorderLayout.CENTER);
		
		//checkBoxPanel.add(boxDisplayJoint, BorderLayout.SOUTH);
		checkBoxPanel.repaint();
	}

	// Reset right panel
	private void resetRightPanel() {
		
		//Reset right layout
		rightPanel.removeAll();
		switch (selectedUnit)
		{
		default:
			rightPanel.add(lblOutput, "cell 0 1");
			rightPanel.add(tabpanelGraph, "cell 0 2,grow");
			rightPanel.add(dashboard, "cell 0 4,growy");
			break;
		case 3:
			rightPanel.add(lblOutput, "cell 0 1");
			rightPanel.add(tabpanelGraph, "cell 0 2,grow");
			rightPanel.add(dashboard, "cell 0 4,growy");

//			if ((selectedSim == 1 && (selectedSet == 4 || selectedSet == 6
//					|| selectedSet == 7 || selectedSet == 10))
//					|| selectedSim == 2)
//				rightPanel.add(cBoxHideWater, "cell 0 3");

		break;
		case 2: 
			rightPanel.add(lblOutput, "cell 0 0");
			rightPanel.add(lblSubMicroscopid, "cell 0 1");
			rightPanel.add(tabpanelGraph, "cell 0 2,grow");
			rightPanel.add(lblOutputMacroscopicLevel, "cell 0 3");
			rightPanel.add(dashboard, "cell 0 4,growy");

			break;

		}
		
		//Reset Canvas and table view
		resetTabPanelGraph();
		
		resetDashboard(); // Reset dashboard on right panel
		
		rightPanel.validate();
	}
	
	//Reset tab panel(Canvas and table view) on right panel
	private void resetTabPanelGraph()
	{
		//Change tabpanelGraph
		
		
		//Change sub-panel: panelGraph
		panelCanvas.removeAll();
		btnGraphSwitch.setEnabled(false);
		
		switch(selectedUnit)
		{
			default:
				panelCanvas.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]",
						"[235.00:n][65,grow]"));
				panelCanvas.add(getCanvas(), "cell 0 0,grow");
				panelCanvas.add(btnGraphSwitch, "cell 1 0,aligny top");
				//Add molecule table to panelCanvas
				panelCanvas.add(getTableView(), "cell 0 1,grow");
			break;
			case 5:
				//In Unit 5 Sim1 graph does not show
				if(selectedSim==1 || selectedSim == 4)
				{
					panelCanvas.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]",
							"[235.00:n]"));
					panelCanvas.add(getCanvas(), "cell 0 0,grow");
					panelCanvas.add(btnGraphSwitch, "cell 1 0,aligny top");
				}
				else
				{
					panelCanvas.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]",
							"[235.00:n][65,grow]"));
					panelCanvas.add(getCanvas(), "cell 0 0,grow");
					panelCanvas.add(btnGraphSwitch, "cell 1 0,aligny top");
					//Add molecule table to panelCanvas
					panelCanvas.add(getTableView(), "cell 0 1,grow");
				}
				break;
		}
	


	}

	// Reset dashboard on right panel
	private void resetDashboard() {
		dashboard.removeAll();
		//rightPanel.remove(outputControls);
		
		dashboard.setLayout(new MigLayout("", "[grow,right][100]",
				"[][][][][][]"));
		
		getP5Canvas().unitList.resetDashboard(selectedUnit, selectedSim, selectedSet);
		//dashboard.repaint();
	}

	/******************************************************************
	 * FUNCTION : updateCenterPanel() DESCRIPTION : Update sliders on the center
	 * panel Only be called in reset()
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	private void resetCenterPanel() {
		if (playBtn != null && centerPanel != null) {

			clPanel.removeAll();
			crPanel.removeAll();
			
			heatSlider.setOrientation(SwingConstants.VERTICAL);
			volumeSlider.setOrientation(SwingConstants.VERTICAL);

			switch(selectedUnit)
			{
			default:
				setupCentralStyle1();
				break;
			case 2:
				
				//Add Pressure slider
					clPanel.add(pressureLabel, "cell 0 0,alignx right");
					clPanel.add(pressureSlider, "cell 0 1,alignx right");
					clPanel.add(canvasControlLabel_main_pressure,
							"cell 0 2, alignx center");
					canvasControlLabel_main_pressure.setVisible(true);
				//Add Zoom slider	
					clPanel.add(zoomLabel, "cell 0 4,alignx right");
					clPanel.add(zoomSlider, "cell 0 5,alignx right,growy");
					clPanel.add(canvasControlLabel_main_scale, "cell 0 6,alignx right");
				
					heatSlider.setEnabled(true);
//					pressureSlider.requestFocus();
				
			    //Add Speed slider
				crPanel.add(speedLabel, "cell 0 0,alignx center");
				crPanel.add(speedSlider, "cell 0 1,alignx left,growy");
				crPanel.add(canvasControlLabel_main_speed, "cell 0 2");
				//Add Heat slider
				crPanel.add(heatLabel, "cell 0 4,alignx center");
				crPanel.add(heatSlider, "cell 0 5,alignx left,growy");
				crPanel.add(canvasControlLabel_main_heat, "cell 0 6");
			break;
			case 4:
				setupCentralStyle2();
				break;
			case 7:
				if(selectedSim==1)
				 setupCentralStyle1();
				else
					setupCentralStyle2();
			}
			
			centerPanel.repaint();
		}

	}
	
	//Set up central panel layout
	private void setupCentralStyle1()
	{
		//Add Volume Slider
		clPanel.add(volumeLabel, "cell 0 0,alignx right");
		clPanel.add(volumeSlider, "cell 0 1,alignx right");
		clPanel.add(lblVolumeText,
				"cell 0 2, alignx center");
	//Add Zoom Slider
		clPanel.add(zoomLabel, "cell 0 4,alignx right");
		clPanel.add(zoomSlider, "cell 0 5,alignx right,growy");
		clPanel.add(canvasControlLabel_main_scale, "cell 0 6,alignx right");
	// Reset zoomSlider
	zoomSlider.setValue(defaultZoom);
	zoomLabel.setText(defaultZoom  + "%");

	heatSlider.setMaximum((int) heatMax);
	heatSlider.setMinimum((int) heatMin);
	heatSlider.setValue((int) defaultHeat);
	if (this.selectedUnit == 3) {
		heatSlider.setEnabled(false);
		volumeSlider.setEnabled(false);
	}
	else
	{
		heatSlider.setEnabled(true);
		volumeSlider.setEnabled(true);
	}
	// Reset animation speed
	speedSlider.setValue(defaultSpeed);
	float speedRate = defaultSpeed / defaultSpeed;
	getP5Canvas().setSpeed(speedRate);

	
	//Add Speed Slider
	crPanel.add(speedLabel, "cell 0 0,alignx center");
	crPanel.add(speedSlider, "cell 0 1,alignx left,growy");
	crPanel.add(canvasControlLabel_main_speed, "cell 0 2");
	//Add Heat Slider
	crPanel.add(heatLabel, "cell 0 4,alignx center");
	crPanel.add(heatSlider, "cell 0 5,alignx left,growy");
	crPanel.add(canvasControlLabel_main_heat, "cell 0 6");
	}
	
	private void setupCentralStyle2()
	{
		//Add Speed Slider
		crPanel.add(speedLabel, "cell 0 0,alignx left");
		crPanel.add(speedSlider, "cell 0 1,alignx left,growy");
		crPanel.add(canvasControlLabel_main_speed, "cell 0 2");
		//Add Zoom Slider
		crPanel.add(zoomLabel, "cell 0 4,alignx left");
		crPanel.add(zoomSlider, "cell 0 5,alignx left,growy");
		crPanel.add(canvasControlLabel_main_scale, "cell 0 6");
		
		//Place holder
		clPanel.add(lblPlaceHolder, "cell 0 0,alignx right");
		lblPlaceHolder.setVisible(false);
	}

	/******************************************************************
	 * FUNCTION : resetTimer DESCRIPTION : Rest Timer, Only be called in reset()
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	public  void resetTimer() {
		time = 0;
		if (this.elapsedTime !=null)
			this.elapsedTime.setText(formatTime(0));
	}
	
	//format Timer output 
	public String formatTime(long count){
		long s = count %60; 
		long m = count /60;
		String sStr =""+s;
		String mStr =""+m;
		if (s<10)
			sStr = "0"+sStr;
		if (m<1)
			return sStr;
		else{
			if (m<10)
				mStr = "0"+mStr;
			return mStr+":"+sStr;
		}
	}

	/******************************************************************
	 * FUNCTION : initialize() DESCRIPTION : Initialize all swing components
	 * when program starts
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	private void initialize() {
		
		mainFrame = new JFrame();
		mainFrame.setBounds(0, 0, 1280, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getP5Canvas().setBackground(Color.WHITE);
		getP5Canvas().init();


		// Set up MouseListerns
		setupMouseListeners();
		// Set up Button Listerns
		setupComponentListener();
		

		mainFrame.getContentPane().setLayout(
				new MigLayout("insets 0, gap 0", "[285.00][480px,grow][320px]",
						"[][][grow]"));
		
		
		/******************************** LEFT PANEL*********************************/
		leftPanel = new JPanel();
		mainFrame.getContentPane().add(leftPanel, "cell 0 2,grow");
		leftPanel.setLayout(new MigLayout("insets 6, gap 0", "[260]",
				"[][]20[215,top]18[][]"));

		// Add Input label and Initialize Input Tip label
		AddInputLabel();

		JPanel timerSubpanel = new JPanel();

		leftPanel.add(timerSubpanel, "cell 0 2,growx");
		timerSubpanel.setLayout(new MigLayout("insets 3, gap 4",
				"[110px][50px]", "[180px][grow]"));

		// Add Play button to timerSubpanel
		playBtn = new JButton("");
		playBtn.addActionListener(playBtnListener);

		if (getP5Canvas().isEnable)
			playBtn.setIcon(new ImageIcon(Main.class
					.getResource("/resources/png48x48/iconPause.png")));
		else
			playBtn.setIcon(new ImageIcon(Main.class
					.getResource("/resources/png48x48/iconPlay.png")));

		timerSubpanel.add(playBtn, "cell 1 0, align center");

		// Add Reset button to timerSubpanel
		resetBtn = new JButton("");
		resetBtn.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png48x48/iconReset.png")));
		resetBtn.addActionListener(resetBtnListener);
		timerSubpanel.add(resetBtn, "cell 1 0, align center");

		// Add Checkbox to checkBoxPanel
		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BorderLayout());
		boxMoleculeHiding = new JCheckBox("Enable Molecule Hiding");
		boxMoleculeHiding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isHidingEnabled = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isHidingEnabled = false;
			}
		});
		boxMoleculeTracking = new JCheckBox("Enable Molecule Tracking");
		boxMoleculeTracking.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isTrackingEnabled = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isTrackingEnabled = false;
			}
		});
		boxDisplayForce = new JCheckBox("Display Forces");
		boxDisplayForce.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isDisplayForces = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isDisplayForces = false;
			}
		});

		boxDisplayJoint = new JCheckBox("Display Joints");
		boxDisplayJoint.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					getP5Canvas().isDisplayJoints = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					getP5Canvas().isDisplayJoints = false;
			}
		});
		//checkBoxPanel.add(boxMoleculeHiding, BorderLayout.NORTH);
		//checkBoxPanel.add(boxDisplayForce, BorderLayout.CENTER);
		//checkBoxPanel.add(boxDisplayJoint, BorderLayout.SOUTH);
		timerSubpanel.add(checkBoxPanel, "cell 1 1, align center");

		timerSubpanel.add(getTableSet(), "cell 0 0 1 2,growy");

		// **************   Add elements Control panel
		// *******************************************
		dynamicScrollPane = new JScrollPane();
		leftPanel.add(dynamicScrollPane, "cell 0 3,grow");

		dynamicPanel = new JPanel();
		dynamicScrollPane.setViewportView(dynamicPanel);
		dynamicPanel.setLayout(new MigLayout("insets 4", "[200.00,grow]",
				"[][]"));
		
		//Button setting for Unit 8
		btnBronsted = new JButton();
		btnBronsted.setIcon(new ImageIcon(Main.class
				.getResource("/resources/compoundsPng50/Catalyst.png")));
		btnBronsted.addActionListener(btnBronstedListener);
		btnLewis = new JButton();
		btnLewis.setIcon(new ImageIcon(Main.class
				.getResource("/resources/compoundsPng50/Inert.png")));
		btnLewis.addActionListener(btnLewisListener);

		/**************************CENTER PANEL********************************/
		centerPanel = new JPanel();
		volumeLabel = new JLabel(defaultVolume + "mL");
		volumeSlider = new JSlider(minVolume, maxVolume, defaultVolume);
		/*
		centerPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int volume = (int) getP5Canvas().getSize().height
						/ getP5Canvas().multiplierVolume;
				getP5Canvas().updateSize(getP5Canvas().getSize(), volume);
				volumeLabel.setText(volume + " mL");
			}
		});*/
		mainFrame.getContentPane().add(centerPanel, "cell 1 2,grow");
		// leftPanel Width=282 rightPanel Width =255
		centerPanel.setLayout(new MigLayout("insets 0, gap 2",
				"[][560.00px][]", "[690px][center]"));

		// Add P5Canvas
		centerPanel.add(getP5Canvas(), "cell 1 0,align right, grow");

		clPanel = new JPanel();
		clPanel.setLayout(new MigLayout("insets 0, gap 0", "[]",
				"[][210.00][][40.00][][210.00][]"));

		// Set up Volume Slider

		volumeSlider.setOrientation(SwingConstants.VERTICAL);
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!isVolumeblocked) {
					JSlider vSlider = (JSlider) e.getSource();
					int value = (vSlider).getValue();
					
					getP5Canvas().setVolume(value);
					
					/*
					p5Canvas.currentVolume = value;
					
					if(value<p5Canvas.volumeMinBoundary)
					{
						value = p5Canvas.volumeMinBoundary;
						volumeLabel.setText(value+ " mL");
					}*/
				}
			}
		});
		lblVolumeText = new JLabel("<html>Container<br>Volume</html>");

		// Set up Pressure Slide
		//getP5Canvas().setPressure(defaultPressure);
		pressureSlider.setOrientation(SwingConstants.VERTICAL);
		pressureLabel = new JLabel(defaultPressure + " atm");

		// Pressure is doing nothing, but we need event listener to change
		// number on pressure label
		pressureSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue();
				//getP5Canvas().setPressure(value);
				pressureLabel.setText(value + " atm");

			}
		});
		canvasControlLabel_main_pressure = new JLabel("Pressure");

//		//Blank label
//		JLabel l2 = new JLabel(" ");
//		clPanel.add(l2, "cell 0 3,alignx center");

		// Set up Zoom Slider
		zoomLabel = new JLabel(defaultZoom + "%");
		
		zoomSlider.setOrientation(SwingConstants.VERTICAL);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue();
				zoomLabel.setText(value  + "%");
				currentZoom = value;
			}
		});
		canvasControlLabel_main_scale = new JLabel("Zoom");
		lblPlaceHolder = new JLabel("Zoom");
		
		centerPanel.add(clPanel, "cell 0 0");

		// Center bottom

		 crPanel = new JPanel();
		 crPanel.setLayout(new MigLayout("insets 0, gap 0", "[]",
				"[][210.00][][40.00][][210.00][]"));

		// Set up Speed slider
		
		speedLabel = new JLabel("1x");
		canvasControlLabel_main_speed = new JLabel("Speed");
		speedSlider = new JSlider(0, 100, defaultSpeed);
		speedSlider.setOrientation(SwingConstants.VERTICAL);
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float value = ((JSlider) e.getSource()).getValue();
				float speedRate = value / defaultSpeed;
				getP5Canvas().setSpeed(speedRate);

				DecimalFormat df = new DecimalFormat("#.##");
				speedLabel.setText(df.format(speedRate) + "x");
			}
		});
		//crPanel.add(new JLabel("    "), "cell 0 3,alignx center");

		// Set up Heat Slider
		int level = (defaultHeat-(heatMax+heatMin)/2)/heatTickSpacing;
		heatLabel = new JLabel(Integer.toString(level));
		canvasControlLabel_main_heat = new JLabel("Heat");
		getP5Canvas().setHeat(defaultHeat);
		heatSlider.setOrientation(SwingConstants.VERTICAL);
		heatSlider.setMinorTickSpacing(heatTickSpacing);
		heatSlider.setSnapToTicks(true);
		heatSlider.setPaintTicks(true);
		heatSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = ((JSlider) e.getSource()).getValue();
				getP5Canvas().setHeat(value);
				int level = (value-(heatMax+heatMin)/2)/heatTickSpacing;
				if(level>0)
					heatLabel.setText("+ "+level);
				else if( level==0)
					heatLabel.setText("0");
				else 
					heatLabel.setText("- "+Math.abs(level));
			}
		});
	

		// After cbPanel has been set up, add it to CenterPanel
		centerPanel.add(crPanel, "cell 2 0");

		/******************************* RIGHT PANEL*******************************/
		rightPanel = new JPanel();
		mainFrame.getContentPane().add(rightPanel, "cell 2 2,grow");
		rightPanel.setLayout(new MigLayout("insets 0, gap 2",
				"[320.00,grow,center]", "[6][][grow][][grow][grow]"));

		// Set up "Output" title
		lblOutput = new JLabel("Output");
		lblOutput.setLabelFor(rightPanel);
		lblOutput.setFont(new Font("Lucida Grande", Font.BOLD, 14));

		lblSubMicroscopid = new JLabel("Submicroscopic Level");
		lblSubMicroscopid.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		// Set up Graph
		tabpanelGraph = new JTabbedPane(JTabbedPane.TOP);
		lblSubMicroscopid.setLabelFor(tabpanelGraph);

		panelCanvas = new JPanel();
		tabpanelGraph.addTab("Compounds", null, panelCanvas, null);
		panelCanvas.setLayout(new MigLayout("insets 0, gap 0", "[150:n,grow][]",
				"[235.00:n][65,grow]"));
		panelCanvas.add(getCanvas(), "cell 0 0,grow");

		btnGraphSwitch = new JButton("");
		btnGraphSwitch.setEnabled(false);
		btnGraphSwitch.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png24x24/iconZoom.png")));
		btnGraphSwitch.addActionListener(btnGraphSwitchListener);
		panelCanvas.add(btnGraphSwitch, "cell 1 0,aligny top");
		//Add molecule table to panelCanvas
		panelCanvas.add(getTableView(), "cell 0 1,grow");

		// "Macrosopic Level" label
		lblOutputMacroscopicLevel = new JLabel("Macroscopic Level");
		lblOutputMacroscopicLevel.setFont(new Font("Lucida Grande", Font.PLAIN,
				10));

		cBoxHideWater = new JCheckBox("Hide Water Molecules");
		cBoxHideWater.addItemListener(cBoxHideWaterListener);

		// Set up dashboard on Right Panel
		dashboard = new JPanel();
		lblOutputMacroscopicLevel.setLabelFor(dashboard);
		dashboard.setLayout(new MigLayout("", "[grow,right][100]",
				"[][][][][][]"));

		lblElapsedTimeText = new JLabel("Elapsed Set Time:");
		elapsedTime = new JLabel("00");
		elapsedTime.setForeground(new Color(0, 128, 0));
		elapsedTime.setFont(new Font("Digital", Font.PLAIN, 28));


		//Set up welcome menu
		if (isWelcomed) {
			welcomePanel = new WelcomePanel();


//			welcomePanel.setLayout(new MigLayout("insets 10, gap 10", "[][]",
//					"[100px][]"));
//
//			JLabel label1 = new JLabel(
//					" Welcome to The Connected Chemistry Curriculum");
//			label1.setFont(new Font("Serif", Font.BOLD, 55));
//			label1.setForeground(Color.blue);
//			welcomePanel.add(label1, "cell 1 1,alignx center");
//
//			JLabel label2 = new JLabel(
//					"Please select a simuation from the menu at top left.");
//			label2.setFont(new Font("Serif", Font.PLAIN, 33));
//			// label2.setForeground(Color.RED);
//			welcomePanel.add(label2, "cell 1 2,alignx center");
//
//			JLabel label3 = new JLabel("");
//			label3.setIcon(new ImageIcon(Main.class
//					.getResource("/resources/png16x16/cccLogo.png")));
//			welcomePanel.add(label3, "cell 1 3,alignx center");

			mainFrame.remove(leftPanel);
			mainFrame.remove(centerPanel);
			mainFrame.remove(rightPanel);
			mainFrame.setLayout(new MigLayout("insets 10, gap 10","[grow]","[grow]"));
			mainFrame.getContentPane().add(welcomePanel,"cell 0 0, align center, grow");
		}

		//If you place Swing popup components in a window containing heavyweight  components and it's possible that the popup windows will intersect a  heavyweight, then invoke
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		//before the popup components are instantiated.
		
		// Set up Menu
		initMenu();
		try {
			moleculeNames = parseNames(getResourceListing(Main.class,
					"resources/compoundsPng50/"));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		final JButton moleculeChooserBtn = new JButton("");
		createPopupMenu();
		moleculeChooserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollablePopupMenu.show(moleculeChooserBtn, -160, 52,
						mainFrame.getHeight() - 39);
			}
		});
		moleculeChooserBtn.setEnabled(false);
		moleculeChooserBtn.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png24x24/iconCompound.png")));
		menuBar.add(moleculeChooserBtn);

		JButton periodicTableBtn = new JButton("\n");
		periodicTableBtn.setEnabled(false);
		periodicTableBtn.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png24x24/iconPeriodicTable.png")));
		menuBar.add(periodicTableBtn);

		
		// Set up timer, start when users press PLAY button
		timer = new Timer(speed, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.print(time+"  ");
				time++;
				getTableView().updateTableView();
				getCanvas().addDataPerTick();
				
				elapsedTime.setText(formatTime(time));
			}
		});
		timer.setInitialDelay(pause);

	}

	// Set up MouseListerners
	private void setupMouseListeners() {
		mulBtnListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = getComponentIndex(e.getComponent());

				if (!started) // Before simulation started user can change
								// selections, but after simulation started they
								// cant
				{
					if (index != -1) {
						if (!btnIds.contains(index)) // If this button has not
														// been selected yet
						{
							if (btnIds.size() < 2) {
								btnIds.add(index);
								// Set selected as true this button after it
								// gets selected
								((JButton) e.getComponent()).setSelected(true);

							}
						} else // If this button has been selected, deselect it
						{
							((JButton) e.getComponent()).setSelected(false);
							btnIds.remove(new Integer(index));
						}

					}
				}

			}
		};
	}
	

	//Set up listener for different components.
	private void setupComponentListener() {
		playBtnListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (getP5Canvas().isEnable) { // playing, turning to PAUSE
					// pause timer
					timer.stop();
					getP5Canvas().isEnable = false;
					playBtn.setIcon(new ImageIcon(Main.class
							.getResource("/resources/png48x48/iconPlay.png")));
				} else { // Pausing, turning to PLAY

					// Special case
					if (selectedUnit == 3 && selectedSim == 2) {
						if (!started) // If simulation has not started yet
						{
							if (btnIds.size() == 2) // If there are two
													// molecules have been
													// selected
							{
								String fixedName = null;
								String waterName = new String("Water");
								int count = 5, waterCount = 20;
								ArrayList<String> compoundNames = new ArrayList<String>();

								// Get molecule names from selected buttons
								for (int i = 0; i < btnIds.size(); i++) {
									// names.add(btnNames.get(btnIds.get(i)-btnStartId));
									fixedName = new String(btnNames.get((btnIds
											.get(i) - 1) / 2)); // Label ids are
																// all even
																// number
									compoundNames.add(fixedName);
								}
								getP5Canvas().getUnit3().setCombination(
										compoundNames);
								compoundNames.add("Water");
								// Set up counts for graph and molecule
								// properties
								ArrayList a = getSetCompounds(selectedUnit,
										selectedSim, selectedSet);

								if (a != null) {
									Compound.names.clear();
									Compound.counts.clear();
									Compound.caps.clear();
									for (int i = 0; i < a.size(); i++) {

										String s = (String) getCompoundName(
												selectedUnit, selectedSim,
												selectedSet, i);
										if (compoundNames.contains(s)) {

											int num = 5;
											if (compoundNames
													.contains("Sodium-Carbonate")
													&& compoundNames
															.contains("Silver-Nitrate")) {
												if (s.equals("Sodium-Carbonate")) // Add
																					// 4
																					// Sodium-Carbonate
													num = 4;
												else
													num = 8; // Add 8
																// Silver-Nitrate
											}
											if (s.equals("Water"))
												num = 20;
											int cap = num;
											s = s.replace(" ", "-");
											Compound.names.add(s);
											Compound.counts.add(num);
											Compound.caps.add(cap);
											// Update tableview before new
											// molecule added
											//getTableView().updateTableView();
											if (num > 0) {
												// Add initial number of
												// molecules into p5Canvas
												if (getP5Canvas()
														.addMoleculeRandomly(s,
																num)) {
													State.moleculesAdded.put(s,
															num);
												}
											}
											
										}
									}
									p5Canvas.setupReactionProducts();
									Compound.setProperties();
									
								}
								started = true;
								getP5Canvas().isSimStarted = true;

								// Disable all molecule buttons on dynamic panel
								for (Component btn : dynamicPanel
										.getComponents())
									if (btn.getClass().getName()
											.equals("javax.swing.JButton")) {
										if (!((JButton) btn).isSelected()) // We
																			// disable
																			// those
																			// non-selected
																			// button
											btn.setEnabled(false);
									}

								playBtn.setIcon(new ImageIcon(
										Main.class
												.getResource("/resources/png48x48/iconPause.png")));
								//Reset canvas and tableView
								getTableView().reset();
								canvas.reset();
								
								getP5Canvas().isEnable = true;
								timer.start();
							}
						} else // Simulation already started
						{
							playBtn.setIcon(new ImageIcon(
									Main.class
											.getResource("/resources/png48x48/iconPause.png")));
							getP5Canvas().isEnable = true;
							timer.start();
						}
					} else // General case
					{
						
						playBtn.setIcon(new ImageIcon(
								Main.class
										.getResource("/resources/png48x48/iconPause.png")));
						p5Canvas.play();
						timer.start();
					}

				}
			}
		};

		resetBtnListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		};

		cBoxHideWaterListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (getTableView().contains("Water")) {
					if (e.getStateChange() == ItemEvent.SELECTED) // Hide water
					{
						// Select all elements in tableview except water
						getTableView().selectAllRows();
						int[] rows = new int[1];
						rows[0] = getTableView().indexOf("Water");
						getTableView().deselectRows(rows);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) // Show
																			// water
					{
						// Show water
						if (!getTableView().selectedRowsIsEmpty()) {
							int waterIndex = getTableView().indexOf("Water");
							if (!getTableView().selectedRowsContain(waterIndex)) {
								int[] rows = { waterIndex };
								getTableView().addSelectedRow(rows);
							}
						}

					}
				}
			}
		};
		
		btnCatalystListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addMolecule(e,"Catalyst",5);
			}
		};
//		btnInhibitorListener = new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				getP5Canvas().getUnit5().addInhibitor();
//			}
//		};
		btnInertListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addMolecule(e,"Inert",5);
			}
		};
		btnSparkListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(p5Canvas.isEnable)
				{
					if(((JButton)e.getSource()).isEnabled())
					{
					p5Canvas.getUnit6().addSpark();
					((JButton)e.getSource()).setEnabled(false);
					}
				}
			}
		};
		
		btnBronstedListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				getP5Canvas().getUnit8().setElectronView(0);
				reset();
			}
		};
		btnLewisListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				getP5Canvas().getUnit8().setElectronView(1);
				reset();
			}
		};
		btnGraphSwitchListener  = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCanvas().switchGraph();
			}
		};
		
		btnAddMoleculeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
					addMolecule(e,null,0);
					
					//If the simulation has not started yet
					//Update tableview and canvas after adding molecule
					if(!p5Canvas.isSimStarted)
					{
						//update molecule count in Compound
						p5Canvas.updateMoleculeCountRelated();
						//update tableview and canvas
						getTableView().updateTableView();
						getCanvas().addDataPerTick();
						
						//Update pressure
						p5Canvas.updateProperties();
						p5Canvas.updateOutput();
						
					}
			}
		};
		
	}
	
	public void addMolecule(ActionEvent e, String compoundName,int num)
	{
		if (((JButton)e.getSource()).isEnabled()) {
			
			int count = 0;
			String fixedName = null;
			if(compoundName==null)
			{
				// Check if molecule number is going over
				// cap number
				// If yes, add molecules no more than cap
				// number
				for (Entry<String, JButton> entry : addBtns
						.entrySet()) {
					if (((JButton)e.getSource()).equals(
							entry.getValue())) {
						fixedName = entry.getKey();
					}
				}
				JSlider slider = (JSlider)moleculeSliderMap.get(fixedName);
				count = slider.getValue();

			}
			else
			{
				fixedName= new String(compoundName);
				count = num;
			}

			int cap = Compound.getMoleculesCap(
					fixedName);
			int currentNum = State.moleculesAdded
					.get(fixedName);

			if (cap <= (count + currentNum)) {
				count = cap - currentNum;
				if (count < 0)
					count = 0;
				// Disable Add button
				if (getP5Canvas().addMolecule(
						fixedName, count)) {
					{
						State.moleculesAdded.put(
								fixedName, currentNum
										+ count);
						
					}
					((JButton)e.getSource()).setEnabled(
							false);
				}
			} else {
				if (getP5Canvas().addMolecule(
						fixedName, count)) {
					State.moleculesAdded.put(fixedName,
							currentNum + count);
				}
			}

		}
	}

	//Return compoent index number under current parent
	public int getComponentIndex(Component component) {
		if (component != null && component.getParent() != null) {
			Container c = component.getParent();
			for (int i = 0; i < c.getComponentCount(); i++) {
				if (c.getComponent(i) == component)
					return i;
			}
		}

		return -1;
	}
	

	/******************************************************************
	 * FUNCTION : AddInputLabel() DESCRIPTION : Add Input tips label for
	 * particular simulation Called in Initialize()
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	private void AddInputLabel() {
		lblInput = new JLabel("Input");
		lblInput.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		leftPanel.add(lblInput, "cell 0 0,alignx center");

		// Add Input Tip label to left panel
		lblInputTipL = new JLabel();
		//lblInputTipL.setText();
		lblInputTipL.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		lblInputTipR = new JLabel();
		//lblInputTipR.setText();
		int textCount = 2;
		inputTipTextL = new String[textCount];
		inputTipTextR = new String[textCount];
		inputTipTextR[0] = new String("<html>Hit \"PLAY\"."
				+ "<p>Add desired amount of solvent."
				+ "<p>Allow a few seconds for solvent and solute to interact."
				+ "<p>Add more solute or solvent if desired.</html>"); //Unit 2 Set 1 and 2
		inputTipTextL[0] = new String("<html>step 1:<p>step 2:<p>step 3:<p><p>step 4:<p><p></html>");
		inputTipTextR[1]= new String("<html>Select the amount of desired solute, and press \"ADD\"."
				+ "<p>Select the amount of desired water, and press \"ADD\"."
				+ "<p>Move the Heat slider to heat solution."
				+ "<p> Press \"PLAY\".</html>"); //Unit 2 set 3
		inputTipTextL[1] = new String("<html>step 1:<p><p>step 2:<p><P>step 3:<p><p>step 4:</html>");

		lblInputTipR.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

	}

	/******************************************************************
	 * FUNCTION : initMenu() DESCRIPTION : Initialize Menu Bar when application
	 * starts Called in Initialize()
	 * 
	 * INPUTS : None OUTPUTS: None
	 *******************************************************************/
	private void initMenu() {
		menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		JMenu logoMenu = new JMenu("");
		logoMenu.setIcon(new ImageIcon(Main.class
				.getResource("/resources/png24x24/iconCcLogo.png")));
		menuBar.add(logoMenu);

		JMenuItem mntmAbout = new JMenuItem("About");
		logoMenu.add(mntmAbout);

		JMenuItem mntmHelp = new JMenuItem("Help");
		logoMenu.add(mntmHelp);

		/*
		 * Simulation Menu
		 */

		simMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		menuBar.add(simMenu);
		simMenu.setBackground(ColorCollection.getColorMenu());

		// populate Units (first level of sim menu)
		final ArrayList units = getUnits();

		JMenu[] menuArray = new JMenu[units.size()];
		final ArrayList[] subMenuArrayList = new ArrayList[units.size()];

		for (int i = 0; i < units.size(); i++) {
			try {
				HashMap unit = (HashMap) units.get(i);
				int unitNo = Integer.parseInt((String) unit.get("unit"));
				String unitName = getUnitName(unitNo);

				JMenu menuItem = new JMenu("Unit " + Integer.toString(unitNo)
						+ ": " + unitName);
				menuArray[i] = menuItem;
				simMenu.add(menuArray[i]);

				// populate Sims (second level of sim menu)
				try {
					final ArrayList sims = getSims(unitNo);
					// Make sure we get sim information from yaml file

					subMenuArrayList[i] = new ArrayList();
					for (int j = 0; j < sims.size(); j++) {
						HashMap sim = (HashMap) sims.get(j);
						int simNo = Integer.parseInt((String) sim.get("sim"));
						String simName = getSimName(unitNo, simNo);

						JMenuItem subMenu = new JMenuItem("Sim "
								+ Integer.toString(simNo) + ": " + simName);

						// Add new subMenuItem
						menuItem.add(subMenu);
						subMenuArrayList[i].add(subMenu);

						subMenu.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								for (int i = 0; i < units.size(); i++) {
									for (int j = 0; j < subMenuArrayList[i]
											.size(); j++) {
										if (e.getSource() == subMenuArrayList[i]
												.get(j)) {
											if (selectedUnit > 0) {
												simMenu.getItem(
														selectedUnit - 1)
														.setBackground(
																Color.WHITE);
												((JMenuItem) (subMenuArrayList[selectedUnit - 1]
														.get(selectedSim - 1)))
														.setBackground(Color.WHITE);
											}
											selectedUnit = i + 1;
											selectedSim = j + 1;
											simMenu.setText("Unit "
													+ selectedUnit
													+ ": "
													+ getUnitName(selectedUnit)
													+ ", Sim "
													+ selectedSim
													+ ": "
													+ getSimName(selectedUnit,
															selectedSim));
											simMenu.getItem(i).setBackground(
													ColorCollection.getColorMenu());
											((JMenuItem) (subMenuArrayList[i]
													.get(j)))
													.setBackground(ColorCollection.getColorMenu());

										}
									}
								}
								getTableSet().updateSet();
								getTableSet().setSelectedRow(0);
							}
						});

					}
				} catch (Exception e) {
					System.out.println("No Submenu Items: " + e);
				}
			} catch (Exception e) {
				System.out.println("No menu items: " + e);
			}
		}

		Component headHGlue = Box.createHorizontalGlue();
		menuBar.add(headHGlue);

		/*
		 * Menubar Unit/Sim/Set status area
		 */

		Component headHStrut = Box.createHorizontalStrut(20);
		menuBar.add(headHStrut);

	}

	/**
	 * @return the p5Canvas
	 */
	public P5Canvas getP5Canvas() {
		return p5Canvas;
	}

	/**
	 * @param p5Canvas
	 *            the p5Canvas to set
	 */
	public void setP5Canvas(P5Canvas p5Canvas) {
		this.p5Canvas = p5Canvas;
	}

	/**
	 * @return the canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * @param canvas
	 *            the canvas to set
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * @return the tableSet
	 */
	public TableSet getTableSet() {
		return tableSet;
	}

	/**
	 * @param tableSet
	 *            the tableSet to set
	 */
	public void setTableSet(TableSet tableSet) {
		this.tableSet = tableSet;
	}

	/**
	 * @return the tableView
	 */
	public TableView getTableView() {
		return tableView;
	}

	/**
	 * @param tableView
	 *            the tableView to set
	 */
	public void setTableView(TableView tableView) {
		this.tableView = tableView;
	}
	

	public int getSelectedUnit()
	{
		return selectedUnit;
	}
	public int getSelectedSim()
	{
		return selectedSim;
	}
	public int getSelectedSet()
	{
		return selectedSet;
	}
	public void setSelectedSet(int v)
	{
		selectedSet = v;
	}
}
