/**
 * Unit 6: Thermodynamics
 */
package simulations;

import static data.State.molecules;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import main.TableView;
import net.miginfocom.swing.MigLayout;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import Util.Integrator;

import data.DBinterface;
import data.State;

import simulations.models.Boundary;
import simulations.models.Compound;
import simulations.models.Molecule;
import simulations.models.Molecule.mState;
import simulations.models.Simulation;
import simulations.models.Simulation.SpawnStyle;

/**
 * @author administrator
 *
 */
public class Unit6 extends UnitBase {
	
	//Output labels
	//Sim 1
	private JLabel lblThermalEnergyText;
	private JLabel lblThermalEnergyValue;
	private JLabel lblChemicalPEText;
	private JLabel lblChemicalPEValue;
	private JLabel lblPistonKEText;
	private JLabel lblPistonPEText;
	private JLabel lblPistonTotalEText;
	private JLabel lblMoleculeAverageKEText;
	private JLabel lblMoleculeTotalEText;
	private JLabel lblSystemTotalEText;
	private JLabel lblPistonKEValue;
	private JLabel lblPistonPEValue;
	private JLabel lblPistonTotalEValue;
	private JLabel lblMoleculeAverageKEValue;
	private JLabel lblMoleculeTotalEValue;
	private JLabel lblSystemTotalEValue;
	//Sim 2
	private JLabel lblVolumeText;
	private JLabel lblVolumeValue;
	private JLabel lblPressureText;
	private JLabel lblPressureValue;
	private JLabel lblTempText;
	private JLabel lblTempValue;
	private JLabel lblSystemMassText;
	private JLabel lblSystemMassValue;
	private JLabel lblAverageVelocityText;
	private JLabel lblAverageVelocityValue;
	//Sim 3
	private JLabel lblPistonEntropyText;
	private JLabel lblPistonEntropyValue;
	private JLabel lblMoleculeEntropyText;
	private JLabel lblMoleculeEntropyValue;
	private JLabel lblSystemEntropyText;
	private JLabel lblSystemEntropyValue;
	//Sim 4
	private JLabel lblSubstanceText;
	private JLabel lblKineticEnergyText;
	private JLabel lblPotentialEnergyText;
	private JLabel lblPentaneNameText;
	private JLabel lblOxygenNameText;
	private JLabel lblCarbonDioxideNameText;
	private JLabel lblWaterNameText;
	private JLabel lblPentanePEText;
	private JLabel lblPentanePEValue;
	private JLabel lblPentaneKEText;
	private JLabel lblPentaneKEValue;	
	private JLabel lblOxygenPEText;
	private JLabel lblOxygenPEValue;
	private JLabel lblOxygenKEText;
	private JLabel lblOxygenKEValue;
	private JLabel lblCO2PEText;
	private JLabel lblCO2PEValue;
	private JLabel lblCO2KEText;
	private JLabel lblCO2KEValue;
	private JLabel lblWaterPEText;
	private JLabel lblWaterPEValue;
	private JLabel lblWaterKEText;
	private JLabel lblWaterKEValue;
	private JLabel lblHeatText;
	private JLabel lblHeatValue;
	//Sim 5
	private JLabel lblMolecule1EntropyText;
	private JLabel lblMolecule1EntropyValue;
	//Sim 7
	private JLabel lblReactantEnthalpyText;
	private JLabel lblReactantEnthalpyValue;
	private JLabel lblProductEnthalpyText;
	private JLabel lblProductEnthalpyValue;
	//Sim 8
	private JLabel lblMolecule1MassText;
	private JLabel lblMolecule1MassValue;
	private JLabel lblMolecule2MassText;
	private JLabel lblMolecule2MassValue;
	
	private float reactionProbability =1.0f;
	
	//Properties values
	float thermalEnergy = 0f;
	float chemicalPE = 0f;
	float pistonKE = 0f;
	float systemTotalEnergy = 0f;
	float systemEntropy = 0f;
	private float heat =0f;
	private float initialTemp = 0f;
	private float compoundEntropy = 0f;

	private int numMoleculePerMole = 10;
	private float averageVelocity = 0f;
	private boolean sparkAdded;
	private float defaultVolume=0;
	private float defaultTemp = 0;
	
	private HashMap<String,Float> compoundKEHash = new HashMap<String,Float>();
	private HashMap<String,Float> compoundPEHash = new HashMap<String,Float>();
	private HashMap<String,Float> compoundEnthalpyHash = new HashMap<String,Float>();
	
	private HashMap<String, Float> moleculeMassHash; //Prepare mass value for graph
	private int hitCount = 0; //Count how many water molecule has been hit by chlorine
	private String inertialGasName = null; //Gas name used in Sim 5 Set 1 and Set 2
	
	private Integrator tempInterpolator;
	private float interpolatorInitial=0;


	/**
	 * @param parent
	 * @param box
	 */
	public Unit6(P5Canvas parent, PBox2D box) {
		super(parent, box);
		unitNum = 6;
		//moleculeNumHash = new HashMap<String, Integer>();
		setupSimulations();
		setupOutputLabels();	
		sparkAdded = false;
		moleculeMassHash = new HashMap<String,Float>();
		inertialGasName=new String("Chlorine");
		tempInterpolator = new Integrator(p5Canvas.temp);
		tempInterpolator.setInterpolating(true);
		}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupSimulations()
	 */
	@Override
	public void setupSimulations() {
		simulations = new Simulation[SIMULATION_NUMBER];

		simulations[0] = new Simulation(unitNum, 1, 1);
		String[] elements0 = { "Butane", "Oxygen" };
		SpawnStyle[] spawnStyles0 = { SpawnStyle.Liquid, SpawnStyle.Gas };
		simulations[0].setupElements(elements0, spawnStyles0);

		simulations[1] = new Simulation(unitNum, 2, 1);
		String[] elements1 = { "Nitrogen","Oxygen","Carbon-Dioxide" };
		SpawnStyle[] spawnStyles1 = { SpawnStyle.Gas,SpawnStyle.Gas,SpawnStyle.Gas };
		simulations[1].setupElements(elements1, spawnStyles1);

		simulations[2] = new Simulation(unitNum, 3, 1);
		String[] elements2 = { "Butane", "Oxygen" };
		SpawnStyle[] spawnStyles2 = { SpawnStyle.Liquid,
				SpawnStyle.Gas };
		simulations[2].setupElements(elements2, spawnStyles2);

		simulations[3] = new Simulation(unitNum, 4, 1);
		String[] elements3 = { "Pentane", "Oxygen" };
		SpawnStyle[] spawnStyles3 = { SpawnStyle.Liquid,
				SpawnStyle.Gas };
		simulations[3].setupElements(elements3, spawnStyles3);
		
		simulations[4] = new Simulation(unitNum, 5, 1);
		String[] elements4 = { "Water", "Chlorine" };
		SpawnStyle[] spawnStyles4 = { SpawnStyle.SolidCube, SpawnStyle.Gas };
		simulations[4].setupElements(elements4, spawnStyles4);
		
		simulations[5] = new Simulation(unitNum, 5, 2);
		String[] elements5 = { "Water" ,"Chlorine"};
		SpawnStyle[] spawnStyles5 = { SpawnStyle.Liquid,SpawnStyle.Gas };
		simulations[5].setupElements(elements5, spawnStyles5);		

		simulations[6] = new Simulation(unitNum, 5, 3);
		String[] elements6 = { "Oxygen" };
		SpawnStyle[] spawnStyles6 = { SpawnStyle.Gas };
		simulations[6].setupElements(elements6, spawnStyles6);
		
		simulations[7] = new Simulation(unitNum, 6, 1);
		String[] elements7 = { "Hydrogen" };
		SpawnStyle[] spawnStyles7 = { SpawnStyle.Gas };
		simulations[7].setupElements(elements7, spawnStyles7);
		
		simulations[8] = new Simulation(unitNum, 6, 2);
		String[] elements8 = { "Methane" };
		SpawnStyle[] spawnStyles8 = { SpawnStyle.Gas };
		simulations[8].setupElements(elements8, spawnStyles8);
		
		simulations[9] = new Simulation(unitNum, 6, 3);
		String[] elements9 = { "Water" };
		SpawnStyle[] spawnStyles9 = { SpawnStyle.Gas };
		simulations[9].setupElements(elements9, spawnStyles9);
		
		simulations[10] = new Simulation(unitNum, 6, 4);
		String[] elements10 = { "Water" };
		SpawnStyle[] spawnStyles10 = { SpawnStyle.Liquid };
		simulations[10].setupElements(elements10, spawnStyles10);
		
		simulations[11] = new Simulation(unitNum, 6, 5);
		String[] elements11 = { "Water" };
		SpawnStyle[] spawnStyles11 = { SpawnStyle.SolidCube };
		simulations[11].setupElements(elements11, spawnStyles11);
		
		simulations[12] = new Simulation(unitNum, 6, 6);
		String[] elements12 = { "Carbon-Dioxide" };
		SpawnStyle[] spawnStyles12 = { SpawnStyle.Gas };
		simulations[12].setupElements(elements12, spawnStyles12);
		
		simulations[13] = new Simulation(unitNum, 6, 7);
		String[] elements13 = { "Propane" };
		SpawnStyle[] spawnStyles13 = { SpawnStyle.Gas };
		simulations[13].setupElements(elements13, spawnStyles13);
		
		simulations[14] = new Simulation(unitNum, 6, 8);
		String[] elements14 = { "Pentane" };
		SpawnStyle[] spawnStyles14 = { SpawnStyle.Liquid };
		simulations[14].setupElements(elements14, spawnStyles14);
		
		simulations[15] = new Simulation(unitNum, 7, 1);
		String[] elements15 = { "Hydrogen-Peroxide" };
		SpawnStyle[] spawnStyles15 = { SpawnStyle.Liquid };
		simulations[15].setupElements(elements15, spawnStyles15);
		
		simulations[16] = new Simulation(unitNum, 7, 2);
		String[] elements16 = { "Propane", "Oxygen" };
		SpawnStyle[] spawnStyles16 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[16].setupElements(elements16, spawnStyles16);

		simulations[17] = new Simulation(unitNum, 7, 3);
		String[] elements17 = { "Ammonia", "Oxygen" };
		SpawnStyle[] spawnStyles17 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[17].setupElements(elements17, spawnStyles17);
		
		simulations[18] = new Simulation(unitNum, 8, 1);
		String[] elements18 = { "Hydrogen", "Oxygen" };
		SpawnStyle[] spawnStyles18 = { SpawnStyle.Gas, SpawnStyle.Gas };
		simulations[18].setupElements(elements18, spawnStyles18);
		
		simulations[19] = new Simulation(unitNum, 8, 2);
		String[] elements19 = { "Acetic-Acid", "Sodium-Bicarbonate","Water" };
		SpawnStyle[] spawnStyles19 = { SpawnStyle.Precipitation, SpawnStyle.Precipitation,SpawnStyle.Solvent };
		simulations[19].setupElements(elements19, spawnStyles19);
		
		simulations[20] = new Simulation(unitNum, 8, 3);
		String[] elements20 = { "Pentane", "Oxygen" };
		SpawnStyle[] spawnStyles20 = { SpawnStyle.Liquid, SpawnStyle.Gas };
		simulations[20].setupElements(elements20, spawnStyles20);
	}
	
	//Set up output labels on the bottom of right panel
	public void setupOutputLabels(){
		
		//Sim 1
		 lblThermalEnergyText = new JLabel("Thermal Energy: ");
		 lblThermalEnergyValue = new JLabel(" kJ/mol");
		 lblChemicalPEText = new JLabel("Chemical PE: ");
		 lblChemicalPEValue = new JLabel();
		 lblPistonKEText = new JLabel("Kinetic Energy of Piston: ");
		 lblPistonPEText = new JLabel("Potential Energy of Piston: ") ;
		 lblPistonTotalEText = new JLabel("Total Energy of Piston: ");
		 lblMoleculeAverageKEText = new JLabel("Average Kinetic Energy of Molecules: ");
		 lblMoleculeTotalEText = new JLabel("Total Energy of Molecules: ");
		 lblSystemTotalEText = new JLabel("Total Energy of System: ");
		 lblPistonKEValue = new JLabel(" kJ");
		 lblPistonPEValue = new JLabel();
		 lblPistonTotalEValue = new JLabel(" kJ");
		 lblMoleculeAverageKEValue = new JLabel();
		 lblMoleculeTotalEValue = new JLabel();
		 lblSystemTotalEValue = new JLabel();
		//Sim 2
		 lblVolumeText = new JLabel("Volume: ");
		 lblVolumeValue = new JLabel(" mL");
		 lblPressureText = new JLabel("Pressure: ");
		 lblPressureValue = new JLabel(" kPa");
		 lblTempText = new JLabel("Temperature: ");
		 lblTempValue = new JLabel(" \u2103");
		 lblSystemMassText = new JLabel("Total Mass: ");
		 lblSystemMassValue = new JLabel();
		 lblAverageVelocityText = new JLabel("Total Average Velocity: ");
		 lblAverageVelocityValue = new JLabel("");
		//Sim 3
		 lblPistonEntropyText = new JLabel("Entropy of Piston: ");
		 lblPistonEntropyValue = new JLabel();
		 lblMoleculeEntropyText = new JLabel("Entropy of Molecules: ");
		 lblMoleculeEntropyValue = new JLabel();
		 lblSystemEntropyText = new JLabel("Entropy of System: ");
		 lblSystemEntropyValue = new JLabel(" kJ/K");
		//Sim 4
		 lblSubstanceText = new JLabel("Substances");
		 lblKineticEnergyText = new JLabel("K.E.");
		 lblPotentialEnergyText = new JLabel("P.E.");
		 lblPentaneNameText = new JLabel("Pentane");
		 lblOxygenNameText = new JLabel("Oxygen");
		 lblCarbonDioxideNameText = new JLabel("Carbon Dioxide");
		 lblWaterNameText = new JLabel("Water");
		 lblPentanePEText = new JLabel("PE Pentane: ");
		 lblPentanePEValue = new JLabel();
		 lblPentaneKEText = new JLabel("KE Pentane: ");
		 lblPentaneKEValue = new JLabel();	
		 lblOxygenPEText = new JLabel("PE Oxygen: ");
		 lblOxygenPEValue = new JLabel();
		 lblOxygenKEText = new JLabel("KE Oxygen: ");
		 lblOxygenKEValue = new JLabel();
		 lblCO2PEText = new JLabel("PE CO2: ");
		 lblCO2PEValue = new JLabel();
		 lblCO2KEText = new JLabel("KE CO2: ");
		 lblCO2KEValue = new JLabel();
		 lblWaterPEText = new JLabel("PE H2O: ");
		 lblWaterPEValue = new JLabel();
		 lblWaterKEText = new JLabel("KE H2O: ");
		 lblWaterKEValue = new JLabel();
		 lblHeatText = new JLabel("q: ");
		 lblHeatValue = new JLabel();
		//Sim 5
		 lblMolecule1EntropyText = new JLabel("Entropy of Water:");
		 lblMolecule1EntropyValue = new JLabel();
		//Sim 7
		 lblReactantEnthalpyText = new JLabel("Enthalpy of Reactants: ");
		 lblReactantEnthalpyValue = new JLabel();
		 lblProductEnthalpyText = new JLabel("Enthalpy of Products: ");
		 lblProductEnthalpyValue = new JLabel();
		//Sim 8
		 lblMolecule1MassText = new JLabel();
		 lblMolecule1MassValue = new JLabel();
		 lblMolecule2MassText = new JLabel();
		 lblMolecule2MassValue = new JLabel();
	}
	public void resetDashboard(int sim,int set)
	{
		super.resetDashboard(sim, set);
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		JPanel dashboard = main.dashboard;
		
		//setupOutputLabels();
		DecimalFormat df = new DecimalFormat("###.#");
		String output = df.format(p5Canvas.temp+ this.celsiusToK);
		lblTempValue.setText(output +" K");
		lblVolumeValue.setText(p5Canvas.currentVolume +" mL");
		lblPressureValue.setText(p5Canvas.pressure+ " kPa");
		lblTempText.setText("Temperature: ");
		lblVolumeText.setText("Volume: " );
		
		switch(sim)
		{
		case 1:
			lblVolumeValue.setText("31 mL");
			lblThermalEnergyValue.setText("2.8 kJ/mol");
			lblChemicalPEValue.setText("-59.04 kJ");
			lblPistonKEValue.setText("0 kJ");
			lblSystemTotalEValue.setText("1493.76 kJ");
				dashboard.add(lblVolumeText, "cell 0 1");
				dashboard.add(lblVolumeValue,"cell 1 1");
				dashboard.add(lblTempText,"cell 0 2");
				dashboard.add(lblTempValue,"cell 1 2");
				dashboard.add(lblThermalEnergyText, "cell 0 3");
				dashboard.add(lblThermalEnergyValue, "cell 1 3");
				dashboard.add(lblChemicalPEText, "cell 0 4");
				dashboard.add(lblChemicalPEValue, "cell 1 4");
				dashboard.add(lblPistonKEText,"cell 0 5");
				dashboard.add(lblPistonKEValue,"cell 1 5");
				dashboard.add(lblSystemTotalEText,"cell 0 6");
				dashboard.add(lblSystemTotalEValue,"cell 1 6");
			break;
		case 2:

			lblAverageVelocityValue.setText("0 m/s");
			lblSystemTotalEValue.setText("0 kJ");
			
			dashboard.add(lblTempText,"cell 0 1");
			dashboard.add(lblTempValue,"cell 1 1");
			dashboard.add(lblVolumeText,"cell 0 2");
			dashboard.add(lblVolumeValue,"cell 1 2");
			dashboard.add(lblPressureText,"cell 0 3");
			dashboard.add(lblPressureValue,"cell 1 3");
//			dashboard.add(lblSystemMassText,"cell 0 5");
//			dashboard.add(lblSystemMassValue,"cell 1 5");
			dashboard.add(lblAverageVelocityText,"cell 0 4	");
			dashboard.add(lblAverageVelocityValue,"cell 1 4");
			dashboard.add(lblSystemTotalEText,"cell 0 5");
			dashboard.add(lblSystemTotalEValue,"cell 1 5");
			break;
		case 3:
			
			lblVolumeValue.setText("31 mL");
			lblSystemEntropyValue.setText("0.63 kJ/K");
			lblChemicalPEValue.setText("-59.04 kJ");
			lblPistonKEValue.setText("0 kJ");
			lblThermalEnergyValue.setText("2.8 kJ");
			
			dashboard.add(lblTempText, "cell 0 1");
			dashboard.add(lblTempValue,"cell 1 1");
			dashboard.add(lblVolumeText,"cell 0 2");
			dashboard.add(lblVolumeValue,"cell 1 2");
			dashboard.add(lblSystemEntropyText,"cell 0 3");
			dashboard.add(lblSystemEntropyValue,"cell 1 3");
			dashboard.add(lblChemicalPEText,"cell 0 4");
			dashboard.add(lblChemicalPEValue,"cell 1 4");
			dashboard.add(lblPistonKEText,"cell 0 5");
			dashboard.add(lblPistonKEValue,"cell 1 5");
			dashboard.add(lblThermalEnergyText,"cell 0 6");
			dashboard.add(lblThermalEnergyValue,"cell 1 6");
			break;
		case 4:
			
			lblPentaneKEValue.setText("0 kJ");
			lblPentanePEValue.setText("-86.75 kJ");
			lblOxygenKEValue.setText("0 kJ");
			lblOxygenPEValue.setText("0 kJ");
			lblCO2KEValue.setText("0 kJ");
			lblCO2PEValue.setText("0 kJ");
			lblWaterKEValue.setText("0 kJ");
			lblWaterPEValue.setText("0 kJ");
			lblSystemTotalEValue.setText("1475.88 kJ");
			lblHeatValue.setText("0 kJ");
			
			dashboard.setLayout(new MigLayout("","[]15[60]10[60]","[][][][][][][][][]"));
			dashboard.add(main.lblElapsedTimeText, "cell 0 0 , align left");
			dashboard.add(main.elapsedTime, "cell 1 0 2 1, align left");
			dashboard.add(lblSubstanceText,"cell 0 1");
			dashboard.add(this.lblKineticEnergyText,"cell 2 1 ");
			dashboard.add(this.lblPotentialEnergyText,"cell 1 1 ");
			dashboard.add(this.lblPentaneNameText,"cell 0 2");
			dashboard.add(this.lblPentanePEValue,"cell 1 2");
			dashboard.add(this.lblPentaneKEValue,"cell 2 2");

			dashboard.add(this.lblOxygenNameText,"cell 0 3");
			dashboard.add(this.lblOxygenPEValue,"cell 1 3");
			dashboard.add(this.lblOxygenKEValue,"cell 2 3");

			dashboard.add(this.lblCarbonDioxideNameText,"cell 0 4");
			dashboard.add(this.lblCO2KEValue,"cell 2 4");
			dashboard.add(this.lblCO2PEValue,"cell 1 4");


			dashboard.add(this.lblWaterNameText,"cell 0 5");
			dashboard.add(this.lblWaterKEValue,"cell 2 5");
			dashboard.add(this.lblWaterPEValue,"cell 1 5");
			
			dashboard.add(this.lblSystemTotalEText,"cell 0 6 3 1");
			dashboard.add(this.lblSystemTotalEValue,"cell 0 6 3 1");
			dashboard.add(this.lblTempText,"cell 0 7 3 1");
			dashboard.add(this.lblTempValue,"cell 0 7 3 1");
			dashboard.add(this.lblHeatText,"cell 0 8 3 1");
			dashboard.add(this.lblHeatValue,"cell 0 8 3 1");

			break;
		case 5:
			
			if(set ==1 )
			{
				dashboard.add(lblTempText, "cell 0 1");
				dashboard.add(this.lblTempValue,"cell 1 1");
				lblTempText.setText("Temperature of System: ");
				lblMoleculeEntropyText.setText("Entropy of Water: ");
				lblMolecule1EntropyValue.setText("102.5 J/K");
			}
			else if(set==2)
			{
				dashboard.add(lblTempText, "cell 0 1");
				dashboard.add(this.lblTempValue,"cell 1 1");
				lblTempText.setText("Temperature of System: ");
				lblMoleculeEntropyText.setText("Entropy of Water: ");
				lblMolecule1EntropyValue.setText("174.87 J/K");
			}
			else if(set==3)
			{
				dashboard.add(lblVolumeText, "cell 0 1");
				dashboard.add(lblVolumeValue,"cell 1 1");
				lblVolumeText.setText("Volume of Oxygen: ");
				lblMoleculeEntropyText.setText("Entropy of Oxygen: ");	
				lblMolecule1EntropyValue.setText("205.07 J/K");
			}
			
			dashboard.add(this.lblMolecule1EntropyText, "cell 0 2");
			dashboard.add(this.lblMolecule1EntropyValue,"cell 1 2");

			break;
		case 6:
			float entropy = 0;
			switch(set)
			{
			case 1:
				entropy = 326.75f;
				break;
			case 2:
				entropy = 186.26f;
				break;
			case 3:
				entropy = 472.1f;
				break;
			case 4:
				entropy = 174.87f;
				break;
			case 5:
				entropy = 102.5f;
				break;
			case 6:
				entropy = 213.74f;
				break;
			case 7:
				entropy = 270.3f;
				break;
			case 8:
				entropy = 131.74f;
				break;
			}
			
			lblMolecule1EntropyValue.setText(Float.toString(entropy)+" J/K");
			
			dashboard.add(this.lblMolecule1EntropyText,"cell 0 1");
			dashboard.add(this.lblMolecule1EntropyValue,"cell 1 1");
			String [] molecules = simulation.getElements();
			lblMolecule1EntropyText.setText(molecules[0]+": ");
			break;
		case 7:
			switch(set)
			{
			case 1:
				lblSystemEntropyValue.setText("0.22 kJ/K");
				lblReactantEnthalpyValue.setText("-375.56 J/K");
				break;
			case 2:
				lblSystemEntropyValue.setText("0.65 kJ/K");
				lblReactantEnthalpyValue.setText("-52.35 J/K");
				break;
			case 3:
				lblSystemEntropyValue.setText("0.36 kJ/K");
				lblReactantEnthalpyValue.setText("-36.72 J/K");
				break;
			}
			lblProductEnthalpyValue.setText("0 J/K");
			
			dashboard.add(this.lblTempText,"cell 0 1");
			dashboard.add(this.lblTempValue,"cell 1 1");
			dashboard.add(this.lblSystemEntropyText,"cell 0 2");
			dashboard.add(this.lblSystemEntropyValue,"cell 1 2");
			dashboard.add(this.lblReactantEnthalpyText,"cell 0 3");
			dashboard.add(this.lblReactantEnthalpyValue,"cell 1 3");
			dashboard.add(this.lblProductEnthalpyText,"cell 0 4");
			dashboard.add(this.lblProductEnthalpyValue,"cell 1 4");
			break;
		case 8:
			if(set==1)
			{
				lblSystemEntropyValue.setText("0.23 kJ/K");
				lblReactantEnthalpyValue.setText("0 J/K");
			}
			else if (set==2)
			{
				lblSystemEntropyValue.setText("0.32 kJ/K");
				lblReactantEnthalpyValue.setText("-708.4 J/K");
			}
			else if (set==3)
			{
				lblSystemEntropyValue.setText("0.95 kJ/K");
				lblReactantEnthalpyValue.setText("-86.75 J/K");
			}

			lblProductEnthalpyValue.setText("0 J/K");
			dashboard.add(this.lblTempText, "cell 0 1");
			dashboard.add(this.lblTempValue, "cell 1 1");
			dashboard.add(this.lblSystemEntropyText,"cell 0 2");
			dashboard.add(this.lblSystemEntropyValue,"cell 1 2");
			dashboard.add(this.lblReactantEnthalpyText,"cell 0 3");
			dashboard.add(this.lblReactantEnthalpyValue,"cell 1 3");
			dashboard.add(this.lblProductEnthalpyText,"cell 0 4");
			dashboard.add(this.lblProductEnthalpyValue,"cell 1 4");
//			dashboard.add(this.lblMolecule1MassText,"cell 0 5");
//			dashboard.add(this.lblMolecule1MassValue,"cell 1 5");
//			dashboard.add(this.lblMolecule2MassText,"cell 0 6");
//			dashboard.add(this.lblMolecule2MassValue,"cell 1 6" );
//			if(set==1)
//			{
//				lblMolecule1MassText.setText("Mass of Hydrogen: ");
//				lblMolecule2MassText.setText("Mass of Oxygen: ");	
//			}
//			else if(set ==3)
//			{
//				lblMolecule1MassText.setText("Pentane: ");
//				lblMolecule2MassText.setText("Oxygen: ");
//			}
			break;
		}
		dashboard.repaint();
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#setupReactionProducts(int, int)
	 */
	@Override
	public void setupReactionProducts(int sim, int set) {
		ArrayList<String> products = new ArrayList<String>();
		if (true) {
			products = DBinterface.getReactionOutputs(unitNum, sim, set);
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

	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateMolecules(int, int)
	 */
	@Override
	public void updateMolecules(int sim, int set) {
		boolean reactionHappened = false;
		Simulation simulation = getSimulation(sim, set);
		switch(sim)
		{
		case 1:
		case 3:
			reactionHappened = reactSim1Set1(simulation);
			break;
		case 2:
			//reactionHappened = reactSim2Set1(simulation);
			break;
		case 4:
				reactionHappened = reactSim4Set1(simulation);
			break;
		case 5:
			constrainWaterMolecule();
			break;
		case 6:
			//reactionHappened = reactSim6Set1(simulation);
			break;
		case 7:
			if(set==1)
				reactionHappened = reactSim7Set1(simulation);
			else if (set==2)
				reactionHappened = reactSim7Set2(simulation);
			else if (set ==3)
				reactionHappened = reactSim7Set3(simulation);
			break;
		case 8:
			if(set==1)
				reactionHappened = reactSim8Set1(simulation);
			else if(set==2)
				reactionHappened = reactSim8Set2(simulation);
			else if (set ==3)
				reactionHappened = reactSim4Set1(simulation);
			break;
		}
	}

	// 2 Butane + 13 O2 --> 8 CO2 + 10 H2O
	private boolean reactSim1Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			Vec2 posCenter = new Vec2(0,0);

			Molecule mNew = null;
			Vec2 newVec = new Vec2();

			//Get reactants molecules and get center position of them
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				posCenter.addLocal(mOld[i].getPosition());
			}
			 posCenter.mulLocal(1.0f/numToKill);
			 Vec2 size = Molecule.getShapeSize("Water", p5Canvas);
			float x = PBox2D.scalarWorldToPixels(posCenter.x);
			float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(posCenter.y);
			
			 //Add molecule into simulation
			 //Shuffle products list to guarantee randomness
			 Collections.shuffle(p5Canvas.products);			 
			 for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Set up positions for molecules in such pattern
				//     o o o o o o
				//     o o o o o o
				//     o o o o o o
				newVec.set(x+size.x*(i%6-2), y + size.y *(i/6) );
				
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				Vec2 velocity = mOld[i<numToKill?i:i-numToKill].body.getLinearVelocity();
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

	//Reaction Sim 8 Set 2: NaHCO3(aq) + CH3COOH(aq) -->H2O(l) + CO2(g) + NaCOOH3(aq)
	private boolean reactSim8Set2(Simulation simulation) {

		if (!p5Canvas.killingList.isEmpty()) {
			// If it is dissolving process
			if (p5Canvas.killingList.get(0).getName().equals("Sodium-Bicarbonate")
					|| p5Canvas.killingList.get(0).getName()
							.equals("Acetic-Acid")) {
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

					int numToKill = p5Canvas.killingList.size();
					Molecule[] mOld = new Molecule[numToKill];
					Molecule dissolveCompound = null;
					for (int i = 0; i < numToKill; i++)
					{
						mOld[i] = (Molecule) p5Canvas.killingList.get(i);
						if(mOld[i].getName().equals("Sodium-Bicarbonate")||mOld[i].getName().equals("Acetic-Acid"))
							dissolveCompound = mOld[i];
					}

					Molecule mNew = null;
					//Create new molecules 
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						Vec2 loc = dissolveCompound.getPosition();
						String ionName = p5Canvas.products.get(i);
						float x1;
						
//						int elementIndex = Compound.isIonOfElement(ionName, dissolveCompound);
//						if(elementIndex !=-1 )
//							loc.set(dissolveCompound.getElementLocation(elementIndex));
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
							
							//Set Sodium-Ion and Bicarbonate tableIndex to "Sodium-Bicarbonate"
							if(ionName.equals("Sodium-Ion")||ionName.equals("Bicarbonate"))
							{
								int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Bicarbonate");
								mNew.setTableIndex(tableIndex);
							}
							else if(ionName.equals("Acetate")||ionName.equals("Hydrogen-Ion"))
							{
								//Set Acetate and Hydrogen-Ion tableIndex to "Silver-Nitrate"
								int tableIndex = p5Canvas.getTableView().getIndexByName("Acetic-Acid");
								mNew.setTableIndex(tableIndex);
							}
						
					}
					for (int i = 0; i < numToKill; i++)
						mOld[i].destroy();

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
				}
			} else // Reaction:  H+ + HCO3- = H2O + CO2
			{
				if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
					Molecule hydrogenIon = null;
					Molecule bicarbonate = null;
					// Get Iron and copperIon reference
					if (p5Canvas.killingList.get(0).getName()
							.equals("Bicarbonate")) {
						bicarbonate = (Molecule) p5Canvas.killingList.get(0);
						hydrogenIon = (Molecule) p5Canvas.killingList.get(1);
					} else {
						hydrogenIon = (Molecule) p5Canvas.killingList.get(0);
						bicarbonate = (Molecule) p5Canvas.killingList.get(1);
					}

//					Molecule silverChloride = null;
					Molecule newMole = null;
					Vec2 loc = null;

					//Create new molecule
					for (int i = 0; i < p5Canvas.products.size(); i++) {
						loc = bicarbonate.getPosition();
						float x1 = PBox2D.scalarWorldToPixels(loc.x);
						float y1 = p5Canvas.h * p5Canvas.canvasScale
								- PBox2D.scalarWorldToPixels(loc.y);
						Vec2 newVec = new Vec2(x1, y1);

						String compoundName = new String(p5Canvas.products.get(i)); //"Silver-Chloride"
						newMole = new Molecule(newVec.x, newVec.y,
								compoundName, box2d, p5Canvas,
								(float) (Math.PI / 2));
						newMole.setRatioKE(1 / simulation.getSpeed());
						molecules.add(newMole);
						float direction = i%2==0?1:-1;
						newMole.setLinearVelocity(bicarbonate.body.getLinearVelocity().mul(direction));
						
//						int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Acetate");
//						silverChloride.setTableIndex(tableIndex);

						//Increate newMole count by 1
						//They are Water and Carbon-Dioxide
						int countIndex = Compound.names.indexOf(compoundName);
						Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
						
					}

					hydrogenIon.destroy();
					bicarbonate.destroy();
					
					//Change tableview value
					boolean sodiumIonChanged=false;
					boolean acetateChanged = false;
					Molecule mole = null;
					
					//Pick one Sodium-Ion and Acetate in reactants and set their table index as "Sodium-Acetate"
					for( int i = 0;i<State.molecules.size();i++)
					{
						mole = State.molecules.get(i);
						//Change tableindex of Sodium-Ion from "Sodium-Bicarbonate" to "Sodium-Acetate"
						if(mole.getName().equals("Sodium-Ion")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Sodium-Bicarbonate")&&!sodiumIonChanged)
						{
							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Acetate");
							mole.setTableIndex(tableIndex);
							sodiumIonChanged=true;
						}
						//Change tableindex of Acetate from "Acetic-Acid" to "Sodium-Acetate"
						if(mole.getName().equals("Acetate")&&mole.getTableIndex()==p5Canvas.getTableView().getIndexByName("Acetic-Acid")&&!acetateChanged)
						{
							int tableIndex = p5Canvas.getTableView().getIndexByName("Sodium-Acetate");
							State.molecules.get(i).setTableIndex(tableIndex);
							acetateChanged = false;
						}
					}
					//Increase Sodium-Acetate count by 1
					int countIndex = Compound.names.indexOf("Sodium-Acetate");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)+1);
		
					//Decrease Sodium-Bicarbonate count by 1
					countIndex = Compound.names.indexOf("Sodium-Bicarbonate");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);
					//Decrease Acetic-Acid count by 1
					countIndex = Compound.names.indexOf("Acetic-Acid");
					Compound.counts.set(countIndex, Compound.counts.get(countIndex)-1);

					p5Canvas.products.clear();
					p5Canvas.killingList.clear();
					updateTemperature(simulation);
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	//Reaction Sim 8 Set 1: 2H2(g) + O2(g) -->2H2O(g)
	private boolean reactSim8Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			Vec2 posCenter = new Vec2(0,0);

			Molecule mNew = null;
			Vec2 newVec = new Vec2();

			//Get reactants molecules and get center position of them
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				posCenter.addLocal(mOld[i].getPosition());
			}
			 posCenter.mulLocal(1.0f/numToKill);
			 Vec2 size = Molecule.getShapeSize("Water", p5Canvas);
			float x = PBox2D.scalarWorldToPixels(posCenter.x);
			float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(posCenter.y);
			
			 
			 for (int i = 0; i < p5Canvas.products.size(); i++) {

					newVec.set(x+size.x*(i%2==1?0.25f:-0.25f), y);
				
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				Vec2 velocity = mOld[i<numToKill?i:i-numToKill].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			updateTemperature(simulation);
			return true;
		}
		return false;
	}
	
	private void updateTemperature(Simulation simulation)
	{
		int tempIncrement = 0;
		int sim = simulation.getSimNum();
		int set = simulation.getSetNum();
		if(sim==7)
		{
			if(set==1)
			{
				tempIncrement = 14;
			}
			else if(set==2)
			{
				tempIncrement = 66;
			}
			else if(set==3)
			{
				tempIncrement = 50;
			}
		}
		else if(sim==8)
		{
			if(set==1) //Exothermic
			{
				tempIncrement = 20;
			}
			else if(set==2) //Endothermic
			{
				tempIncrement = -2;
			}
			else if(set==3) //Exothermic
			{
				tempIncrement = 10;
			}
			
		}
		p5Canvas.temp+=tempIncrement;
		p5Canvas.averageKineticEnergy = p5Canvas.getKEFromTemp();
	}

	private boolean reactSim7Set3(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Random rand = new Random();
			if (rand.nextFloat() < reactionProbability) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				return false;
			}
			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			Vec2 posCenter = new Vec2(0,0);

			Molecule mNew = null;
			Vec2 newVec = new Vec2();

			//Get reactants molecules and get center position of them
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				posCenter.addLocal(mOld[i].getPosition());
			}
			 posCenter.mulLocal(1.0f/numToKill);
			 Vec2 size = Molecule.getShapeSize("Water", p5Canvas);
			float x = PBox2D.scalarWorldToPixels(posCenter.x);
			float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(posCenter.y);
			
			 //Add molecule into simulation
			 //Shuffle products list to guarantee randomness
			 Collections.shuffle(p5Canvas.products);			 
			 for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Set up positions for molecules in such pattern
				//       o o o
				//      o o o o
				//       o o o
				if(i<3)
					 newVec.set(x+size.x*(i-1), y - size.y);
				else if( i<8)
					newVec.set(x+size.x*((i-3)-1), y );
				else
					newVec.set(x+size.x*((i-7)-1), y + size.y );
				
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				Vec2 velocity = mOld[i<numToKill?i:i-numToKill].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			updateTemperature(simulation);

			return true;
		}
		return false;
	}

	private boolean reactSim7Set2(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		int numPropane = State.getMoleculeNumByName("Propane");
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Random rand = new Random();
			if (rand.nextFloat() > reactionProbability*(1f/numPropane)) {
				p5Canvas.products.clear();
				p5Canvas.killingList.clear();
				 return false;
			}
			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			Vec2 posCenter = new Vec2(0,0);

			Molecule mNew = null;
			Vec2 newVec = new Vec2();

			//Get reactants molecules and get center position of them
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				posCenter.addLocal(mOld[i].getPosition());
			}
			 posCenter.mulLocal(1.0f/numToKill);
			 Vec2 size = Molecule.getShapeSize("Water", p5Canvas);
			float x = PBox2D.scalarWorldToPixels(posCenter.x);
			float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(posCenter.y);
			
			 //Add molecule into simulation
			 //Shuffle products list to guarantee randomness
			 Collections.shuffle(p5Canvas.products);			 
			 for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Set up positions for molecules in such pattern
				//       o o 
				//      o o o 
				//       o o 
				if(i<2)
					 newVec.set(x+size.x*i, y - size.y);
				else if( i<5)
					newVec.set(x+size.x*((i-1)-2), y );
				else
					newVec.set(x+size.x*(i-5), y + size.y );
				
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				Vec2 velocity = mOld[i<numToKill?i:i-numToKill].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			updateTemperature(simulation);

			return true;
		}
		return false;
	}

	private boolean reactSim7Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {
			Random rand = new Random();
			int numH2O2 = State.getMoleculeNumByName("Hydrogen-Peroxide");
			reactionProbability = 2f/numH2O2;
			if (rand.nextFloat() > reactionProbability) {
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
			Vec2 sizeOxygen = Molecule.getShapeSize("Oxygen", p5Canvas);

			// Actually there is only one reaction going in each frame
			for (int i = 0; i < p5Canvas.products.size(); i++) {
				Vec2 loc = mOld[0].getPosition();
				float x = PBox2D.scalarWorldToPixels(loc.x);
				float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(loc.y);
				Vec2 newVec = new Vec2(x, y);
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				if (mNew.getName().equals("Water")) {
					x = (i%2==0?1:-1)*sizeOxygen.x;
				}
				State.molecules.add(mNew);
				mNew.setRatioKE(1/simulation.getSpeed());
				
				mNew.body.setLinearVelocity(mOld[i%2].body
							.getLinearVelocity());
				}
			
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			int unit = p5Canvas.getUnit();
			int sim = p5Canvas.getSim();
			int set = p5Canvas.getSet();
			updateCompoundNumber(unit, sim, set);
			updateTemperature(simulation);
			return true;
		}
		return false;
	}

	//Sim 4 Set 1: C5H12 + 8O2 --> 5CO2 + 6H2O
	private boolean reactSim4Set1(Simulation simulation) {
		if (p5Canvas.killingList.isEmpty())
			return false;
		if (p5Canvas.products != null && p5Canvas.products.size() > 0) {

			int numToKill = p5Canvas.killingList.size();
			Molecule[] mOld = new Molecule[numToKill];
			Vec2 posCenter = new Vec2(0,0);

			Molecule mNew = null;
			Vec2 newVec = new Vec2();

			//Get reactants molecules and get center position of them
			for (int i = 0; i < numToKill; i++)
			{
				mOld[i] = (Molecule) p5Canvas.killingList.get(i);
				posCenter.addLocal(mOld[i].getPosition());
			}
			 posCenter.mulLocal(1.0f/numToKill);
			 Vec2 size = Molecule.getShapeSize("Water", p5Canvas);
			float x = PBox2D.scalarWorldToPixels(posCenter.x);
			float y = p5Canvas.h * p5Canvas.canvasScale
						- PBox2D.scalarWorldToPixels(posCenter.y);
			
			 //Add molecule into simulation
			 //Shuffle products list to guarantee randomness
			 Collections.shuffle(p5Canvas.products);			 
			 for (int i = 0; i < p5Canvas.products.size(); i++) {
				//Set up positions for molecules in such pattern
				//       o o o
				//     o o o o o
				//       o o o
				if(i<3)
					 newVec.set(x+size.x*(i-1), y - size.y);
				else if( i<8)
					newVec.set(x+size.x*((i-3)-2), y );
				else
					newVec.set(x+size.x*((i-8)-1), y + size.y );
				
				mNew = new Molecule(newVec.x, newVec.y,
						p5Canvas.products.get(i), box2d, p5Canvas,
						(float) (Math.PI / 2));
				mNew.setRatioKE(1 / simulation.getSpeed());
				State.molecules.add(mNew);

				Vec2 velocity = mOld[i<numToKill?i:i-numToKill].body.getLinearVelocity();
				mNew.body.setLinearVelocity(velocity);

			}
			for (int i = 0; i < numToKill; i++)
				mOld[i].destroy();
			p5Canvas.products.clear();
			p5Canvas.killingList.clear();
			updateCompoundNumber(simulation);
			updateTemperature(simulation);
			return true;
		}
		return false;
	}
	
	private void constrainWaterMolecule()
	{
		float keThreshold =1.4f;
		if(p5Canvas.getSet()==2)
			keThreshold = 1.6f;
		float ratio =1 ;
		for(Molecule waterMole: State.getMoleculesByName("Water"))
		{
			float ke = waterMole.getKineticEnergy();
			if(ke>keThreshold)
			{
				ratio = keThreshold/ke;
				waterMole.constrainKineticEnergy(ratio);
			}
		}
	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#initialize()
	 */
	@Override
	public void initialize() {		
		
		//Initialize
		lastMole = State.getMoleculeNum();
		initialTemp = p5Canvas.temp;
				
		if(p5Canvas.isSimSelected(unitNum, 5, 1));
		{
			//Give Chlorine huge huge momentum 
			Vec2 velocity = null;
			for(Molecule mole:State.getMoleculesByName(inertialGasName))
			{
				velocity = mole.getLinearVelocity();
				if(p5Canvas.getSet()==1)
					velocity.mulLocal(2.0f);
				else if(p5Canvas.getSet()==2)
					velocity.mulLocal(3.0f);
			}
			//Set water moleculet 
			for(Molecule moleWater:State.getMoleculesByName("Water"))
			{
				moleWater.setEnableAutoStateChange(false);
				if(p5Canvas.getSet()==1)
					moleWater.setState(mState.Solid);
				else if(p5Canvas.getSet()==2)
					moleWater.setState(mState.Liquid);
			}
		}

	}

	/* (non-Javadoc)
	 * @see simulations.UnitBase#reset()
	 */
	@Override
	protected void reset() {
		//Reset parameters
		sparkAdded = false;
		interpolator.setDamping(0.3f);
		interpolator.setAttraction(0.1f);
		interpolator.reset();
		tempInterpolator.setDamping(0.3f);
		tempInterpolator.setAttraction(0.1f);
		tempInterpolator.reset();
		this.compoundKEHash.clear();
		this.compoundPEHash.clear();
		this.compoundEnthalpyHash.clear();
		moleculeMassHash.clear();
		compoundEntropy = 0;
		thermalEnergy = 0f;
		chemicalPE = 0f;
		pistonKE = 0f;
		systemTotalEnergy = 0f;
		systemEntropy = 0f;
		heat =0f;
		initialTemp = 0f;
		hitCount=0;
		defaultVolume = 0;
		defaultTemp = p5Canvas.temp;
		
		//Customization
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		//Setup simulation speed
		setupSpeed(sim,set);
		
		switch(sim)
		{
		default:
			break;
		case 1:
		case 3:
			needWeight();
			p5Canvas.temp = -5;
			break;
		case 2:
			
			break;
		case 4:
			needWeight();
			p5Canvas.heatSpeed = 5;
			break;
		case 5:
			if(set==1||set==2)
				p5Canvas.setIfConstrainKE(false);
			break;
		case 6:
			if(set==3)
				p5Canvas.temp = 105;
			else if (set ==5)
				p5Canvas.temp = -5;
			else if (set ==7)
				p5Canvas.temp = -5;
			break;
		case 7:
			if(set==1)
				reactionProbability = 0.15f;
			else if(set==2)
				reactionProbability =0.5f;
			else if(set==3)
				reactionProbability =0.4f;
			break;
		case 8:
			
			if(set==3)
				p5Canvas.heatSpeed = 4;
			break;
		}
	}
	
	
	//Customize Interface in Main reset after all interface have been initialized
	public void customizeInterface(int sim, int set)
	{
		
		Main main = p5Canvas.getMain();
		Simulation simulation = getSimulation(sim,set);
		String elements [];
		
		switch(sim)
		{
		default:
			break;
		case 1:
		case 3:
			//Make initial volume smaller
			p5Canvas.getMain().volumeSlider.setValue(p5Canvas.currentVolume/2);
			p5Canvas.getMain().volumeSlider.setEnabled(false);
			break;
		case 2:
			main.heatSlider.setEnabled(false);
			main.volumeSlider.setEnabled(false);
			break;

		case 4:
			main.volumeSlider.setEnabled(false);
			p5Canvas.getMain().volumeSlider.setValue(p5Canvas.currentVolume/2);
			break;
		case 5:
			if(set==2)
			{
				main.volumeSlider.setEnabled(false);
			}
			else if( set ==3)
			{
				main.heatSlider.setEnabled(false);
			}
			elements = simulation.getElements();
			lblMolecule1EntropyText.setText("Entropy of "+elements[0] +": ");
			break;
		case 6:
			elements  = simulation.getElements();
			lblMolecule1EntropyText.setText("Entropy of "+elements[0] +": ");
			main.heatSlider.setEnabled(false);

			break;
		case 7:
			break;
		case 8:
			elements  = simulation.getElements();
			this.lblMolecule1MassText.setText("Mass of "+elements[0] +": ");
			this.lblMolecule2MassText.setText("Mass of "+elements[1] +": ");
			break;
		}
		
		lastVolume = (int)p5Canvas.currentVolume;
		lastTemp = p5Canvas.temp;


	}
	

	
	private void setupSpeed(int sim, int set) {
		float speed = 1.0f;		

		switch(sim)
		{
		default:
			speed = 1;
			break;
		case 1:
		case 3:
			speed = 4;
			break;
		case 2:
			speed = 4; 
			break;
		case 4:
			speed = 4;
			break;
		case 5:
			if(set==3)
				speed =4 ;
			break;
		case 6:
			if(set==2)
				speed = 4;
			else if(set==6)
				speed = 8;
			else if (set==7)
				speed =8 ;
			break;
		case 7:
			speed=  4;
			break;
		case 8:
			speed = 4;
			if(set==2)
				speed =2;
			break;
		}
		getSimulation(sim, set).setSpeed(speed);

	}
	/* (non-Javadoc)
	 * @see simulations.UnitBase#updateOutput(int, int)
	 */
	@Override
	public void updateOutput(int sim, int set) {
		
		// Update lblTempValue
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = null;
		updateMoleculeMass();

		if (lblVolumeValue.isShowing()) {
			lblVolumeValue.setText(Float.toString(p5Canvas.currentVolume)
					+ " mL");
		}
		if (lblTempValue.isShowing()) {
			myFormatter = new DecimalFormat("###.#");
			output = myFormatter.format(p5Canvas.temp+ celsiusToK);
			lblTempValue.setText(output + " K");
//			if(sim==5)
//				lblTempValue.setText("25 \u2103");
		}
		if (lblPressureValue.isShowing()) {
			output = myFormatter.format(p5Canvas.pressure);
			lblPressureValue.setText(output + " kPa");
		}
		if(lblThermalEnergyValue.isShowing())
		{
			output = myFormatter.format(thermalEnergy);
			lblThermalEnergyValue.setText( output + " kJ");
		}
		if(lblChemicalPEValue.isShowing())
		{
			output = myFormatter.format(chemicalPE);
			lblChemicalPEValue.setText(output + " kJ");
		}
		
		if(this.lblPistonKEValue.isShowing())
		{
			output = myFormatter.format(pistonKE);
			lblPistonKEValue.setText(output + " kJ");
		}
		
		if(this.lblSystemTotalEValue.isShowing())
		{
			output = myFormatter.format(systemTotalEnergy);
			lblSystemTotalEValue.setText(output + " kJ");
		}
		
		if(this.lblAverageVelocityValue.isShowing())
		{
			output = myFormatter.format(averageVelocity);
			this.lblAverageVelocityValue.setText(output+ " m/s");
		}
		if(this.lblSystemEntropyValue.isShowing())
		{
			output = myFormatter.format(systemEntropy);
			this.lblSystemEntropyValue.setText(output+ " kJ/K");
		}
		if(this.lblPentaneKEValue.isShowing())
		{
			output = myFormatter.format(getCompoundKE("Pentane"));
			lblPentaneKEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundKE("Oxygen"));
			lblOxygenKEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundKE("Carbon-Dioxide"));
			lblCO2KEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundKE("Water"));
			lblWaterKEValue.setText(output+ " kJ");
			
			output = myFormatter.format(getCompoundPE("Pentane"));
			lblPentanePEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundPE("Oxygen"));
			lblOxygenPEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundPE("Carbon-Dioxide"));
			lblCO2PEValue.setText(output+ " kJ");
			output = myFormatter.format(getCompoundPE("Water"));
			lblWaterPEValue.setText(output+ " kJ");
		}
		if(this.lblHeatValue.isShowing())
		{
			output = myFormatter.format(heat);
			this.lblHeatValue.setText(output +" kJ");
		}
		if(this.lblMolecule1EntropyValue.isShowing())
		{
			output = myFormatter.format(compoundEntropy);
			this.lblMolecule1EntropyValue.setText(output+" J/K");
		}
		
		if(this.lblReactantEnthalpyValue.isShowing())
		{
			output = myFormatter.format(this.getReactantsEntralpy());
			this.lblReactantEnthalpyValue.setText(output+" J/K");
		}
		if(this.lblProductEnthalpyValue.isShowing())
		{
			output = myFormatter.format(getProductsEnthalpy());
			this.lblProductEnthalpyValue.setText(output+" J/K");
		}
		
	}
	
	public void updateProperties(int sim,int set)
	{
		switch(sim)
		{
		case 1:
			updateTempInterpolator();
			updateThermalEnergy();
			updateChemicalPE();
			updatePistonKE();
			updateSystemTotalEnergy();
			break;
		case 2:
			
			updateAverageVelocity();
			updateThermalEnergy();
			updateSystemTotalEnergy();
			
			break;
		case 3:
			updateTempInterpolator();
			updateThermalEnergy();
			updateChemicalPE();
			updatePistonKE();
			updateSystemEntropy();
			break;
		case 4:
			updateCompoundPE();
			updateCompoundKE();
			updateSystemTotalEnergy();
			updateHeat();
			break;
		case 5:
		
			updateMoleculeStatus();
			updateCompoundEntropy();
			break;
		case 6:
			updateCompoundEntropy();
			break;
		case 7:
		case 8:
			updateSystemEntropy();
			updateCompoundsEnthalpy();

			break;
			
		}
	}
	
	private void updateThermalEnergy ()
	{
		thermalEnergy = State.getMoleculeNum()*p5Canvas.averageKineticEnergy;
	}
	
	//Sum up enthalpy of all the molecules
	private void updateChemicalPE()
	{
		chemicalPE = 0;
		float pe = 0;
		HashMap<String,Float> enthalpyHash = new HashMap<String,Float>();
		for(Molecule mole:State.molecules)
		{
			if(enthalpyHash.containsKey(mole.getName()))
				pe = enthalpyHash.get(mole.getName());
			else
			{
				pe = mole.getEnthalpy();
				enthalpyHash.put(mole.getName(), pe);
			}
			chemicalPE+=pe /numMoleculePerMole;
		}
		//chemicalPE/=1000; //kJ/mol
	}
	
	private void updatePistonKE()
	{
		pistonKE = getPistonKE();
	}
	
	private void updateSystemTotalEnergy()
	{
		//systemTotalEnergy = thermalEnergy+chemicalPE+pistonKE + pistonPE;
		switch(p5Canvas.getSim())
		{
		case 2:
			systemTotalEnergy = thermalEnergy;
			break;
		case 1:
		case 3:
			//Estimate Piston PE: m =491.69 kg   PE = mgh
			if(!sparkAdded)
			systemTotalEnergy = 1493.76f;
			else
				systemTotalEnergy = 1686.95f;
			break;
		case 4:
			systemTotalEnergy = 1475.88f + heat;
			break;
		}
		
	}
	private void updateHeat()
	{
		//Q = m * c * deltaT;
		float mass = State.getCompoundsMass();
		float c = 273.15f; //constant
		float deltaT = p5Canvas.temp-initialTemp;
		heat = mass*c*deltaT;
		heat/=1000; //showing kJ
	}
	
	private void updateAverageVelocity()
	{
		float totalVelocity = 0;
		int size = State.getMoleculeNum();
		if(size ==0)
		{
			averageVelocity = 0;
			return ;
		}
		for(Molecule m: State.getMolecules())
		{
			//totalVelocity += (m.getLinearVelocityScalar()+m.getAngularVelocityScalar());
			float velocity = m.getKineticEnergy()*2/m.getMoleculeMass();
			velocity = (float)Math.sqrt(velocity);
			
			if(m.getName().equals("Oxygen"))
				velocity *=1.6f;
			else if(m.getName().equals("Nitrogen"))
				velocity *=2.0f;
			else if(m.getName().equals("Carbon-Dioxide"))
				velocity *= 1.4f;
			
			totalVelocity += velocity;
		}
		averageVelocity = totalVelocity/size;
	}
	
	private void updateSystemEntropy()
	{
		systemEntropy = 0;
		float pe = 0;
		HashMap<String,Float> entropyHash = new HashMap<String,Float>();
		for(Molecule mole:State.getMolecules())
		{
			if(entropyHash.containsKey(mole.getName()))
				pe = entropyHash.get(mole.getName());
			else
			{
				pe = mole.getEntropy();
				entropyHash.put(mole.getName(), pe);
			}
			systemEntropy+=pe ;
		}
		systemEntropy/= numMoleculePerMole;
		systemEntropy/=1000; //From J/K to kJ/K
		
//		if(defaultTemp ==0)
//			defaultTemp = p5Canvas.temp;
		
		//Q= mc *deltaT
		float Q = (p5Canvas.temp-defaultTemp)*0.2f;
		
		//deltaS = deltaQ/T
		float deltaS = (Q-0)/p5Canvas.temp;
		systemEntropy+=deltaS;
		
	}
	
	private void updateCompoundPE()
	{
		compoundPEHash.clear();
		int compoundNum = 0;
		float chemicalPE = 0;
		Molecule mole = null;
		ArrayList<String> compoundNames = State.getCompoundNames();
		for(String name:compoundNames)
		{
			
			mole = State.getMoleculeByName(name);
			if(mole!=null)
			{
				chemicalPE = mole.getEnthalpy();
				compoundNum = State.getMoleculeNumByName(name);
				chemicalPE*= ((float)compoundNum/numMoleculePerMole);
				compoundPEHash.put(name, chemicalPE);
			}
			else
				compoundPEHash.put(name, (float) 0);

		}
	}
	private float getCompoundPE(String name)
	{
		if(compoundPEHash.containsKey(name))
			return compoundPEHash.get(name);
		else
			return 0;
	}
	private void updateCompoundKE()
	{
		compoundKEHash.clear();
		float ke = 0 ;
		ArrayList<Molecule> moles = null;
		
		ArrayList<String> compoundNames = State.getCompoundNames();
		for(String name:compoundNames)
		{
			ke=0;
			moles = State.getMoleculesByName(name);
			if(!moles.isEmpty())
			{
				for(Molecule m:moles)
				{
					ke+=m.getKineticEnergy();
				}
				compoundKEHash.put(name, ke);
			}
			else
				compoundKEHash.put(name, (float) 0);

		}
	}
	
	private float getCompoundKE(String name)
	{
		if(compoundKEHash.containsKey(name))
			return compoundKEHash.get(name);
		else
			return 0;
	}
	
	//update entropy of single compound system
	private void updateCompoundEntropy()
	{
		ArrayList<String> compoundNames = State.getCompoundNames();
		float entropy = 0;
		if(compoundNames==null || compoundNames.isEmpty())
		{
			compoundEntropy = 0;
		}
		else
		{
			String name = new String(compoundNames.get(0));
			for(Molecule mole: State.getMoleculesByName(name))
			{
			   entropy += mole.getEntropy();
			}
			compoundEntropy = entropy /this.numMoleculePerMole;
		}
		
		if(p5Canvas.getSet()==3) //In Sim 5 Set 3, entropy increases with temperature
		{
			if(defaultVolume ==0)
				defaultVolume = p5Canvas.currentVolume;
			float ratio = p5Canvas.currentVolume/defaultVolume;
			compoundEntropy*=ratio;
		}
	}
	//Update entropy of multiple compounds system
	private void updateCompoundsEnthalpy()
	{
		compoundEnthalpyHash.clear();
		int compoundNum = 0;
		int count =0;
		float enthalpy = 0;
		Molecule mole = null;
		ArrayList<String> compoundNames = State.getCompoundNames();

		Molecule mole1 = null;
		Molecule mole2 = null;
		float compoundEnthalpy1 = 0;
		float compoundEnthalpy2 = 0;
		
		//If there is no compound in system
		if(compoundNames==null || compoundNames.isEmpty())
		{
			return;
		}
		if(p5Canvas.isSimSelected(unitNum, 8, 2))
		{
			compoundNames = p5Canvas.getTableView().getCompoundNames();
			for(String name: compoundNames)
			{
				mole = State.getMoleculeByName(name);
				count = (int)p5Canvas.getTableView().getCountByName(name);
				if(name.equals("Water"))
				{
					//The number of water product is always the same with that of CO2
					count = (int)p5Canvas.getTableView().getCountByName("Carbon-Dioxide"); 
				}
				if(count ==0)
					compoundEnthalpyHash.put(name, (float) 0);
				else
				{
					if(mole!=null)
					{
						compoundEnthalpy1 = State.getMoleculeByName(name).getEnthalpy();
						if(name.equals("Sodium-Bicarbonate"))
							compoundEnthalpy1 = State.getMoleculeByName(name).getEnthalpy("liquid");
						enthalpy = ((float)count/numMoleculePerMole)*(compoundEnthalpy1);
					}
					else
					{
						if(name.equals("Sodium-Bicarbonate"))
						{
							mole1 = State.getMoleculeByName("Sodium-Ion");
							mole2 = State.getMoleculeByName("Bicarbonate");
						}
						else if(name.equals("Acetic-Acid"))
						{
							mole1 = State.getMoleculeByName("Acetate");
							mole2 = State.getMoleculeByName("Hydrogen-Ion");
						}
						else if(name.equals("Sodium-Acetate"))
						{
							mole1 = State.getMoleculeByName("Sodium-Ion");
							mole2 = State.getMoleculeByName("Acetate");
						}
						//If both ions exist, calculate enthalpy as sum of ions
						if(mole1!=null && mole2!=null)
						{
						compoundEnthalpy1 = mole1.getEnthalpy("liquid");
						compoundEnthalpy2 = mole2.getEnthalpy("liquid");
						enthalpy = ((float)count/numMoleculePerMole)*(compoundEnthalpy1+compoundEnthalpy2);
						}
						else //No ions, no such compound
						{
							enthalpy = 0;
						}

					}
					compoundEnthalpyHash.put(name, enthalpy);

				}
			}
		}
		else
		{
			for(String name:compoundNames)
			{
				mole = State.getMoleculeByName(name);
				if(mole!=null)
				{
					enthalpy = mole.getEnthalpy();
					if(name.equals("Sodium-Bicarbonate"))
						enthalpy= mole.getEnthalpy("liquid");
					compoundNum = State.getMoleculeNumByName(name);
					enthalpy*= ((float)compoundNum/numMoleculePerMole);
					compoundEnthalpyHash.put(name, enthalpy);
				}
				else
					compoundEnthalpyHash.put(name, (float) 0);
	
			}
		}
		return;
	}
	
	//Get the total entropy of reactants
	private float getReactantsEntralpy()
	{
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float totalEnthalpy = 0;
		
		ArrayList<String> reactantName = new ArrayList<String>();
		if(sim==7)
		{
			switch(set)
			{
			case 1:
				reactantName.add("Hydrogen-Peroxide");
				break;
			case 2:
				reactantName.add("Propane");
				reactantName.add("Oxygen");
				break;
			case 3:
				reactantName.add("Ammonia");
				reactantName.add("Oxygen");
				break;
			}
		}
		else if(sim ==8)
		{
			switch(set)
			{
			case 1:
				reactantName.add("Hydrogen");
				reactantName.add("Oxygen");
				break;
			case 2:
				reactantName.add("Sodium-Bicarbonate");
				reactantName.add("Acetic-Acid");
				//reactantName.add("Water");
				break;
			case 3:
				reactantName.add("Pentane");
				reactantName.add("Oxygen");
				break;
			}
		}
		
		if(reactantName.isEmpty()||reactantName == null)
			return 0;
		for(String name: reactantName)
		{
			if(compoundEnthalpyHash.containsKey(name))
				totalEnthalpy += compoundEnthalpyHash.get(name);
		}
		return totalEnthalpy;
	}
	
	//Get the total entropy of products
	private float getProductsEnthalpy()
	{
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		float totalEnthalpy = 0;
		
		ArrayList<String> productName = new ArrayList<String>();
		if(sim==7)
		{
			switch(set)
			{
			case 1:
				productName.add("Water");
				productName.add("Oxygen");
				break;
			case 2:
				productName.add("Carbon-Dioxide");
				productName.add("Water");
				break;
			case 3:
				productName.add("Nitric-Oxide");
				productName.add("Water");
				break;
			}
		}
		else if(sim ==8)
		{
			switch(set)
			{
			case 1:
				productName.add("Water");
				break;
			case 2:
				productName.add("Sodium-Acetate");
				productName.add("Carbon-Dioxide");
				productName.add("Water");
				break;
			case 3:
				productName.add("Carbon-Dioxide");
				productName.add("Water");
				break;
			}
		}
		
		if(productName.isEmpty()||productName == null)
			return 0;
		for(String name: productName)
		{
			if(compoundEnthalpyHash.containsKey(name))
				totalEnthalpy += compoundEnthalpyHash.get(name);
		}
		return totalEnthalpy;
	}
	
	//Called in Unit7 updateProperties after P5Canvas updateProperties
	//in order to make Iodine act like gas
	private void updateMoleculeStatus()
	{
		for(Molecule mole: State.getMoleculesByName(inertialGasName))
		{
			mole.setGravityScale(0f);
		}
	}
	

	/* (non-Javadoc)
	 * @see simulations.UnitBase#computeForce(int, int)
	 */
	@Override
	protected void computeForce(int sim, int set) {
		
		clearAllMoleculeForce();

		switch (sim) {
		case 1:
		case 3:
			break;
		case 2:

			break;
		case 4:
			break;
		case 5:

			if(set==1)
			{
			shakeIceMolecule();
//			maintainGasSpeed();
			computeForceIce(sim,set);
			}
			else if(set==2)
				computeForceWater(sim,set);
			break;
		case 6:
			if(set==5)
			{
				averageKineticEngergy();
				computeForceGeneric(sim,set);
			}
			else if(set==4)
			{
				computeForceGeneric(sim,set);
			}
			break;
		case 7:
			if(set==1)
				computeForceSim7Set1();
			else if(set==2)
				computeForceSim7Set2();
			break;
		case 8:
			if(set==1)
				computeForceSim8Set1();
			else if(set==2)
				computeForceSim8Set2();
		break;
		}
	}
	
	private void computeForceSim7Set1()
	{
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.3f; // How strong the attract force is


		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		
		int numH2O2 = State.getMoleculeNumByName("Hydrogen-Peroxide");
		if(numH2O2!=2)
			return;
		//Pull Hydrogen-Peroxide together
		ArrayList<Molecule> h2o2Array = State.getMoleculesByName("Hydrogen-Peroxide");
		for(int i = 0;i<h2o2Array.size();i++)
		{
			Molecule mole = h2o2Array.get(i);				//Attract Bicarbonate to Hydrogen-Ion
			locThis.set(mole.getPosition());	
					for(int j = i+1 ;j<h2o2Array.size();j++)
					{
						Molecule moleOther = h2o2Array.get(j);
						locOther.set(moleOther.getPosition());
			
								xValue = locOther.x - locThis.x;
								yValue = locOther.y - locThis.y;
								dis = (float) Math.sqrt(xValue * xValue + yValue
										* yValue);
								forceX = (float) (xValue / dis) * scale;
								forceY = (float) (yValue / dis) * scale;
								for(int e=0 ;e<mole.getNumElement();e++)
								{
								mole.sumForceX[e] += forceX;
								mole.sumForceY[e] += forceY;
								moleOther.sumForceX[e] -= forceX;
								moleOther.sumForceY[e] -= forceY;
								}
					}
				
		}
			
	}
	
	private void computeForceSim7Set2()
	{
		float xValue = 0 ;
		float yValue = 0;
		float dis = 0;
		float forceX = 0;
		float forceY = 0;
		float scale = 0.3f; // How strong the attract force is


		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		
		int numPropane = State.getMoleculeNumByName("Propane");
		if(numPropane!=1)
			return;
		//There is only one propane left, attract oxygen molecules to it
		for(Molecule mole: State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());
				//Attract Bicarbonate to Hydrogen-Ion
				if(mole.getName().equals("Propane"))
				{
						locThis.set(mole.getPosition());
						ArrayList<Molecule> oxygen = State.getMoleculesByName("Oxygen");
						for(Molecule moleOther:oxygen)
						{
							locOther.set(moleOther.getPosition());
							xValue = locOther.x - locThis.x;
							yValue = locOther.y - locThis.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
							forceX = (float) (xValue / dis) * scale;
							forceY = (float) (yValue / dis) * scale;
							
							moleOther.sumForceX[0] -= forceX;
							moleOther.sumForceY[0] -= forceY;
							moleOther.sumForceX[1] -= forceX;
							moleOther.sumForceY[1] -= forceY;
						}				
				}
			}
	}
	
	private void computeForceSim8Set1()
	{
		float topBoundary = p5Canvas.h/4*3;
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;
		float maxForce = -1.2f;
		int numOxygen = State.getMoleculeNumByName("Oxygen");
		if(numOxygen!=1)
			return;
		
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float xValue =  0;
		float yValue = 0;
		float dis = 0;
		float forceX = 0 ;
		float forceY  =0;
		float scale  =0.1f;
		
		// Check positions of all liquid molecules, in case they are not going
		// to high
		if(p5Canvas.temp<100)
		{
		for(Molecule mole:State.getMoleculesByName("Water"))
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());				
					if (pos.y < topBoundary) {
						for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
																						// element
							mole.sumForceX[thisE] += 0;
							float value = (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
							value = (value<maxForce)?maxForce:value	;
							mole.sumForceY[thisE] += value;
			
						}
					}
				
		}
		}	
						for(Molecule mole: State.getMoleculesByName("Oxygen"))
						{
								locThis.set(mole.getPosition());
								ArrayList<Molecule> hydrogen = State.getMoleculesByName("Hydrogen");
								for(Molecule moleOther:hydrogen)
								{
									locOther.set(moleOther.getPosition());
									xValue = locOther.x - locThis.x;
									yValue = locOther.y - locThis.y;
									dis = (float) Math.sqrt(xValue * xValue + yValue
											* yValue);
									forceX = (float) (xValue / dis) * scale;
									forceY = (float) (yValue / dis) * scale;
									
									moleOther.sumForceX[0] -= forceX;
									moleOther.sumForceY[0] -= forceY;
									moleOther.sumForceX[1] -= forceX;
									moleOther.sumForceY[1] -= forceY;
								}				
						}
				
			
		
	}
	
	private void computeForceSim8Set2()
	{
		float topBoundary = p5Canvas.h/2;
		float gravityCompensation = 0.2f;
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
			if(mole.getName().equals("Carbon-Dioxide"))  //Give gas molecule anti-gravity force
			{
				for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
										// element
					mole.sumForceX[thisE] += 0;
					mole.sumForceY[thisE] += gravityCompensation;
					
					}
			}

			else{
				//Separate Sodium-Ion
				if(mole.getName().equals("Sodium-Ion")) //Make sodium-Ion
				{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { 
								locThis.set(mole.getElementLocation(thisE));
								mole.sumForceX[thisE] = 0;
								mole.sumForceY[thisE] = 0;
								ArrayList<Molecule> sodiumIons = State.getMoleculesByName("Sodium-Ion");
		 						for (Molecule moleOther: sodiumIons) {
		 							if(mole==moleOther)
		 								continue;
									locOther.set(moleOther.getPosition());
									xValue = locThis.x - locOther.x;
									yValue = locThis.y - locOther.y;
									dis = (float) Math.sqrt(xValue * xValue + yValue
									* yValue);
									forceX = (float) ((xValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
									forceY = (float) ((yValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
									
									mole.sumForceX[thisE] += forceX;
									mole.sumForceY[thisE] += forceYCompensation+forceY;						
								
							}
						}
				}
				//Attract Bicarbonate to Hydrogen-Ion
				else if(mole.getName().equals("Bicarbonate"))
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

							mole.sumForceX[thisE] += forceX*2;
							mole.sumForceY[thisE] += forceY*2;
							
							moleOther.sumForceX[0] -= forceX;
							moleOther.sumForceY[0] -= forceY;
						}
						
					}
				}
				//separate Acetate
				else if (mole.getName().equals("Acetate"))
					{
					for (int thisE = 0; thisE < mole.getNumElement(); thisE++) { // Select
											// element
					
						locThis.set(mole.getElementLocation(thisE));
						mole.sumForceX[thisE] = 0;
						mole.sumForceY[thisE] = 0;
						ArrayList<Molecule> acetates = State.getMoleculesByName("Acetic-Acid");
						acetates.addAll(State.getMoleculesByName("Acetate"));
 						for (Molecule moleOther: acetates) {
 							if(mole==moleOther)
 								continue;
							locOther.set(moleOther.getPosition());
							xValue = locThis.x - locOther.x;
							yValue = locThis.y - locOther.y;
							dis = (float) Math.sqrt(xValue * xValue + yValue
							* yValue);
							forceX = (float) ((xValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
							forceY = (float) ((yValue / dis) * (repulsiveForce/Math.pow(dis, 2)));
							
							mole.sumForceX[thisE] += forceX*2;
							mole.sumForceY[thisE] += forceYCompensation*0.6;						
						
					}
					
					}
					
					}
				
				// Check positions of all liquid molecules, in case they are not going
				// to high
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
	/*
	private void computeForceSim7Set1()
	{
		float topBoundary = p5Canvas.h/2;
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;
		// Check positions of all liquid molecules, in case they are not going
		// to high
		for(Molecule mole:State.getMolecules())
		{
			Vec2 pos = box2d.coordWorldToPixels(mole.getPosition());

			if(mole.getName().equals("Water"))
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
	}*/
	
	private void computeForceIce(int sim, int set) {
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;
		int index = -1;
		float topBoundary = p5Canvas.h/2;
		Vec2 pos = new Vec2();		
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;
		ArrayList<Molecule> molecules = State.getMoleculesByName("Water");

		for (Molecule moleThis : molecules) {

			index = State.getMoleculeIndex(moleThis);
			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (Molecule moleOther:molecules) {
				if (moleThis==moleOther)
					continue;
				locOther = moleOther.getPosition();
				
				if (locOther == null || locThis == null)
					continue;
				
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = normalizeForce(new Vec2(x, y));

				//Add attractive force to same kind molecule
				if(moleThis.getName().equals(moleOther.getName()))
				{
					
					float gravityX=0, gravityY=0;
					if(moleThis.isSolid()){ // Solid case
						gravityY = 1.05f;
						gravityX = gravityY * 2f;
					} else if(moleThis.isLiquid()) { // Liquid case
//						gravityY = 0.75f;
//						gravityX = gravityY * 0.6f;
					}
					
					forceX = (-direction.x / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;

					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {
						// Water case
							if (thisE == 2) {
								moleThis.sumForceX[thisE] += forceX * 3000;
								moleThis.sumForceY[thisE] += forceY * 3000;
							}
						

					}
				} 

			}
			// Check positions of all water molecules, in case they are not going
			// to high
			
			pos = box2d.coordWorldToPixels(moleThis.getPosition());
			if (pos.y < topBoundary) {
				for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
																				// element
					moleThis.sumForceX[thisE] += 0;
					moleThis.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;

				}
			}
			
		}
	}
	
	//Force Computation function for Sim 5 Set 2
	private void computeForceWater(int sim, int set) {
		Vec2 locThis = new Vec2();
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;
		int index = -1;
		float topBoundary = p5Canvas.h/2;
		Vec2 pos = new Vec2();		
		float gravityCompensation = 0.2f;
		float gravityScale = 0.01f;
		ArrayList<Molecule> molecules = State.getMoleculesByName("Water");

		for (Molecule moleThis : molecules) {

			index = State.getMoleculeIndex(moleThis);
			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (Molecule moleOther:molecules) {
				if (moleThis==moleOther)
					continue;
				locOther = moleOther.getPosition();
				
				if (locOther == null || locThis == null)
					continue;
				
				float x = locThis.x - locOther.x;
				float y = locThis.y - locOther.y;
				float disSquare = x * x + y * y;
				Vec2 direction = normalizeForce(new Vec2(x, y));

				//Add attractive force to same kind molecule
				if(moleThis.getName().equals(moleOther.getName()))
				{
					
					float gravityX=0, gravityY=0;
					if(moleThis.isLiquid()) { // Liquid case
						gravityY = 0.75f;
						gravityX = gravityY * 0.6f;
					}
					else //Gas case
					{
						
					}
					forceX = (-direction.x / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityX;
					forceY = (-direction.y / disSquare)
							* moleOther.getBodyMass() * moleThis.getBodyMass()
							* gravityY;

					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) {
						// Water case
							if (thisE == 2) {
								moleThis.sumForceX[thisE] += forceX * 3000;
								moleThis.sumForceY[thisE] += forceY * 3000;
							}
						

					}
				} 

			}
			// Check positions of all water molecules, in case they are not going
			// to high
			
			if (moleThis.isLiquid())
			{
				pos = box2d.coordWorldToPixels(moleThis.getPosition());
				if (pos.y < topBoundary) {
					for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
																					// element
						moleThis.sumForceX[thisE] += 0;
						moleThis.sumForceY[thisE] += (gravityCompensation+ gravityScale*(topBoundary-pos.y)) * -1;
	
					}
				}
			}
			
		}
	}
	
	public void computeForceGeneric(int sim, int set) {
		Molecule moleThis = null;
		Vec2 locThis = new Vec2();
		Molecule moleOther = null;
		Vec2 locOther = new Vec2();
		float forceX = 0;
		float forceY = 0;

		for (int i = 0; i < State.molecules.size(); i++) {

			moleThis = State.molecules.get(i);
			locThis = moleThis.getPosition();
			for (int thisE = 0; thisE < moleThis.getNumElement(); thisE++) { // Select
				moleThis.sumForceX[thisE] = 0;
				moleThis.sumForceY[thisE] = 0;
				moleThis.sumForceWaterX[thisE] = 0;
				moleThis.sumForceWaterY[thisE] = 0;
			}

			for (int k = 0; k < State.molecules.size(); k++) {
				moleOther = State.molecules.get(k);
				if (k == i
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
				Vec2 direction = normalizeForce(new Vec2(x, y));

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
						} else if (moleThis.getName().equals("Pentane")) // No
																			// force
																			// applied
																			// on
																			// Pentane
						{
							
						}

						else if (moleThis.getName().equals("Mercury")) {
							moleThis.sumForceX[thisE] += forceX * 200;
							moleThis.sumForceY[thisE] += forceY * 250;
						} else if (moleThis.getName().equals("Bromine")) {
							if(!((sim==5&&set==1)||(sim==5&&set==5)))
							{
							moleThis.sumForceX[thisE] += forceX * 50;
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

			}
		}
	}
	
	//To make all molecules of ice shake
	//we need to distribute the kinetic energy evenly to every molecules
	private void shakeIceMolecule()
	{
		int index = - 1;
		for(Molecule mole :State.getMoleculesByName("Water"))
		{
			index = State.getMoleculeIndex(mole);
			//If water molecule has not been hit yet, stay in solid phase
			if(mole.isSolid())
			{
				float ratio = 0.1f/mole.getKineticEnergy();
				//float ratio = 1;
				mole.shakeMolecule(ratio);
			}
		}
	}

	
	//To make all molecules of ice shake
	//we need to distribute the kinetic energy evenly to every molecules
	private void averageKineticEngergy()
	{
		int index = - 1;
		for(Molecule mole :State.getMoleculesByName("Water"))
		{
			index = State.getMoleculeIndex(mole);
			//If water molecule has not been hit yet, stay in solid phase
			{
			float ratio = p5Canvas.averageKineticEnergy/mole.getKineticEnergy();
			mole.shakeMolecule(ratio);
			}
		}
	}
	public float getMoleByName(String name)
	{
		int countIndex = Compound.names.indexOf(name);
		int num = Compound.counts.get(countIndex);
		return (float)num/numMoleculePerMole;
	}
	
	//Normalize the input force
	public Vec2 normalizeForce(Vec2 v) {
		float dis = (float) Math.sqrt(v.x * v.x + v.y * v.y);
		return new Vec2(v.x / dis, v.y / dis);
	}
	/* (non-Javadoc)
	 * @see simulations.UnitBase#addMolecules(boolean, java.lang.String, int)
	 */
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
			res = this.addLiquid(isAppEnable, compoundName, count);
		}
		else if (spawnStyle == SpawnStyle.Precipitation) {
			res = this.addPrecipitation(isAppEnable, compoundName, count, simulation, 0);
		}
		else if (spawnStyle ==SpawnStyle.Solvent)
		{
			res= this.addSolvent(isAppEnable, compoundName, count, simulation);
		}
		else if (spawnStyle ==SpawnStyle.SolidCube)
		{
			res = this.addSolidCube(isAppEnable, compoundName, count, simulation);
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
		
		int numRow = 4;
		if (count <= 4) {
			numRow = count;
		}
		int numCol = (int) Math.ceil((float) count / numRow);


		float centerX = p5Canvas.x + moleWidth ; // X coordinate around which
													// we are going to
		float centerY = (float) (p5Canvas.y + p5Canvas.h
				- ((float) numRow ) * moleHeight - p5Canvas.boundaries.difVolume); // Y
																						// coordinate
																						// around
		// which we are going to
		// add Ions

		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		boolean isClear = false;

		float increX = p5Canvas.w / 8;
		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);

		topLeft.set(centerX - moleWidth, centerY - moleHeight);
		botRight.set(centerX + numCol * moleWidth, centerY + numRow * size.y);

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
				topLeft.set(centerX - size.x, centerY - 0.5f * size.y);
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
				float x_ = centerX + (i % numCol) * moleWidth;
				float y_ = centerY + (i / numCol) * moleHeight;
				float angle = (float) ((i / numCol == 0) ? 0 : Math.PI);
				molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, angle));

			}
		}

		return res;
	}
	
	/******************************************************************
	 * FUNCTION : addLiquid DESCRIPTION : Function to add liquid molecules to
	 * PApplet Do area clear check when spawn
	 * 
	 * INPUTS : isAppEnable(boolean), compoundName(String), count(int) OUTPUTS:
	 * None
	 *******************************************************************/
	public boolean addLiquid(boolean isAppEnable, String compoundName,
			int count) {

		boolean res = true;

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
		if(p5Canvas.getSim()==5 && p5Canvas.getSet()==2)
		{
			rowNum =3 ;
			colNum = count/rowNum+1;
		}
		boolean isClear = false;
		Vec2 molePos = new Vec2(0, 0); // Molecule position parameter
		Vec2 molePosInPix = new Vec2(0, 0);
		float increX = p5Canvas.w / 3;

		// Initializing
		centerX = p5Canvas.x + moleWidth;
		centerY = p5Canvas.y + p5Canvas.h - rowNum * moleHeight
				- p5Canvas.boundaries.difVolume;
			
		topLeft = new Vec2(centerX -0.5f*moleWidth, centerY - 0.5f * moleHeight);
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
				topLeft = new Vec2(centerX -0.5f*moleWidth, centerY - 0.5f
						* moleHeight);
				botRight = new Vec2(centerX + colNum * moleWidth, centerY + rowNum
						* moleHeight);

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

				x_ = centerX + i % colNum * moleWidth ;
				y_ = centerY + i / colNum * moleHeight;

				res = molecules.add(new Molecule(x_, y_, compoundName, box2d,
						p5Canvas, 0));
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
		
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();

		Vec2 molePos = new Vec2(0, 0);
		Vec2 molePosInPix = new Vec2(0, 0);
		Vec2 topLeft = new Vec2(0, 0);
		Vec2 botRight = new Vec2(0, 0);
		float spacing = moleWidth;
		float maxVelocity = 40;
		float spaceHeight = p5Canvas.h;
		if(p5Canvas.getSim()==3||p5Canvas.getSim()==1||p5Canvas.getSim()==4||p5Canvas.isSimSelected(unitNum,8,3))
			spaceHeight = p5Canvas.h/2;

		boolean isClear = false;

		for (int i = 0; i < count; i++) {

			isClear = false;
			while (!isClear) {
				isClear = true;
				x_ = moleWidth + randX.nextFloat()
						* (p5Canvas.w - 2 * moleWidth);
				y_ = (p5Canvas.y+p5Canvas.h) - (moleHeight + randY.nextFloat()
						* (spaceHeight - 2 * moleHeight));
				if(sim==5&&(set==1||set==2)||p5Canvas.isSimSelected(unitNum,8,3))
					y_ = (p5Canvas.y+p5Canvas.h/2) - (moleHeight + randY.nextFloat()
							* (p5Canvas.h/2 - 2 * moleHeight));
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

	
	
	public void updateTempInterpolator()
	{

			if(tempInterpolator.isTargeting())
			{
				tempInterpolator.update();
				
				float ratio = (tempInterpolator.getValue()*interpolatorInitial)/p5Canvas.temp;
				
				//decrease temperature
				for(Molecule m:State.molecules)
				{
				m.constrainKineticEnergy(ratio);
				}
				p5Canvas.calculateKE();
				p5Canvas.temp = p5Canvas.getTempFromKE();
			}
		
	}
	
	/* (non-Javadoc)
	 * @see simulations.UnitBase#beginReaction(org.jbox2d.dynamics.contacts.Contact)
	 */
	@Override
	public void beginReaction(Contact c) {
		
		switch(p5Canvas.getSim())
		{
		case 1:
		case 3:
				moveTopBoundary(c);
				if(!tempInterpolator.isTargeting()&&interpolator.isTargeting())
				{   
					//Start temp interpolator
					float diff = interpolator.getTarget()- interpolator.getValue();
					if (diff>3)
					{
						interpolatorInitial = p5Canvas.temp;
						float ratio  = 0.94f;
						tempInterpolator.set(1.0f);
						tempInterpolator.target(ratio);
					}
				}
			break;
		case 4:
			moveTopBoundary(c);
			break;
		case 5:
			if(p5Canvas.getSet()==1||p5Canvas.getSet()==2)
				markCollision(c);
			break;

		}
		
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
		case 3:
			if(sparkAdded)
			{
			if(reactants.contains("Butane")&&reactants.contains("Oxygen"))
			{
				float radius = 550;
				probability = 1.0f;
				randomFloat = rand.nextFloat();
				int oxygenTotalNum = 13 ;
				int butaneTotalNum = 2;
				ArrayList<Molecule> oxygenList = new ArrayList<Molecule>();
				ArrayList<Molecule> butaneList = new ArrayList<Molecule>();
					Vec2 initialPos = null;
					
					if(m1.getName().equals("Butane"))
						initialPos = box2d.coordWorldToPixels(m1.getPosition());
					else
						initialPos = box2d.coordWorldToPixels(m2.getPosition());
					
				// Go through all molecules to check if there are any molecules
				// nearby
				for (int i = 0; i < State.molecules.size(); i++) {
					Molecule mole = State.molecules.get(i);
					if (mole.getName().equals("Oxygen")
							&& mole != m1 && mole != m2 && oxygenList.size()<oxygenTotalNum-1) {
						Vec2 posOxygen = box2d.coordWorldToPixels(State.molecules
								.get(i).getPosition());
						if (radius > computeDistance(initialPos, posOxygen)) {
							oxygenList.add(mole);
						}
					}
					else if( mole.getName().equals("Butane") && mole!=m1 && mole!=m2 && butaneList.size()<butaneTotalNum-1)
					{
						Vec2 posButane = box2d.coordWorldToPixels(State.molecules
								.get(i).getPosition());
						if (radius > computeDistance(initialPos, posButane)) {
							butaneList.add(mole);
						}
					}
					if(oxygenList.size()>=oxygenTotalNum-1&&butaneList.size()>=butaneTotalNum-1)
							break; // Break after we find one nearby
				}
				//If enough oxygen and ammonia have been found
				if(oxygenList.size()==oxygenTotalNum-1 && butaneList.size()==butaneTotalNum-1){
					for( int k = 0 ;k<8;k++)
					products.add("Carbon-Dioxide");
					for( int j = 0;j<10;j++)
						products.add("Water");
					// Need to kill reactants molecule
					p5Canvas.killingList.addAll(oxygenList);
					p5Canvas.killingList.addAll(butaneList);
				}
			
			}
			}
		break;
		case 2:
			// Sim 2 2NO2 <--> N2O4
			if (reactants.get(0).equals("Nitrogen-Dioxide")
					&& reactants.get(1).equals("Nitrogen-Dioxide")) {
				reactionProbability = 0.1f;
				randomFloat = rand.nextFloat();
				if (randomFloat <= reactionProbability) {
					products.add("Dinitrogen-Tetroxide");
				}
			}
			break;

		case 4:
			// C5H12 + 8O2 --> 5CO2 +6H2O
			if(p5Canvas.temp>=260)
				products = this.getReactionProductsSim4(reactants, m1, m2);
			break;
		case 5:
			break;
		case 6:
			break;
		case 7 :
			if(set==1)
			{
				if(reactants.get(0).equals("Hydrogen-Peroxide") && reactants.get(1).equals("Hydrogen-Peroxide"))
				{
					products.add("Water");
					products.add("Water");
					products.add("Oxygen");
				}
			}
			else if( set==2)
			{
				if(reactants.contains("Propane")&&reactants.contains("Oxygen"))
				{
					float radius = 450;
					probability = 1.0f;
					randomFloat = rand.nextFloat();
					int oxygenTotalNum = 5 ;
					ArrayList<Molecule> oxygenList = new ArrayList<Molecule>();
					if (randomFloat <= probability) {
						Vec2 propanePos = null;
						if(m1.getName().equals("Propane"))
							propanePos = box2d.coordWorldToPixels(m1.getPosition());
						else
							propanePos = box2d.coordWorldToPixels(m2.getPosition());
						
					// Go through all molecules to check if there are any molecules
					// nearby
					for (int i = 0; i < State.molecules.size(); i++) {
						Molecule mole = State.molecules.get(i);
						if (mole.getName().equals("Oxygen")
								&& mole != m1 && mole != m2) {
							Vec2 posOxygen = box2d.coordWorldToPixels(State.molecules
									.get(i).getPosition());
							if (radius > computeDistance(propanePos, posOxygen)) {
								oxygenList.add(mole);
								if(oxygenList.size()>=oxygenTotalNum-1)
									break; // Break after we find one nearby
							}
						}
					}
					//If enough oxygen molecules have been found
					if(oxygenList.size()==oxygenTotalNum-1){
						for( int i = 0 ;i<3;i++)
						products.add("Carbon-Dioxide");
						for( int j = 0;j<4;j++)
							products.add("Water");
						// Need to kill reactants molecule
						p5Canvas.killingList.addAll(oxygenList);
					}
				}
				}
			}
			else if(set==3)
			{
				if(reactants.contains("Ammonia")&&reactants.contains("Oxygen"))
				{
					float radius = 550;
					probability = 1.0f;
					randomFloat = rand.nextFloat();
					int oxygenTotalNum = 5 ;
					int ammoniaTotalNum = 4;
					ArrayList<Molecule> oxygenList = new ArrayList<Molecule>();
					ArrayList<Molecule> ammoniaList = new ArrayList<Molecule>();
						Vec2 initialPos = null;
						
						if(m1.getName().equals("Ammonia"))
							initialPos = box2d.coordWorldToPixels(m1.getPosition());
						else
							initialPos = box2d.coordWorldToPixels(m2.getPosition());
						
					// Go through all molecules to check if there are any molecules
					// nearby
					for (int i = 0; i < State.molecules.size(); i++) {
						Molecule mole = State.molecules.get(i);
						if (mole.getName().equals("Oxygen")
								&& mole != m1 && mole != m2 && oxygenList.size()<oxygenTotalNum-1) {
							Vec2 posOxygen = box2d.coordWorldToPixels(State.molecules
									.get(i).getPosition());
							if (radius > computeDistance(initialPos, posOxygen)) {
								oxygenList.add(mole);
							}
						}
						else if( mole.getName().equals("Ammonia") && mole!=m1 && mole!=m2 && ammoniaList.size()<ammoniaTotalNum-1)
						{
							Vec2 posAmmonia = box2d.coordWorldToPixels(State.molecules
									.get(i).getPosition());
							if (radius > computeDistance(initialPos, posAmmonia)) {
								ammoniaList.add(mole);
							}
						}
						if(oxygenList.size()>=oxygenTotalNum-1&&ammoniaList.size()>=ammoniaTotalNum-1)
								break; // Break after we find one nearby
					}
					//If enough oxygen and ammonia have been found
					if(oxygenList.size()==oxygenTotalNum-1 && ammoniaList.size()==ammoniaTotalNum-1){
						for( int k = 0 ;k<4;k++)
						products.add("Nitric-Oxide");
						for( int j = 0;j<6;j++)
							products.add("Water");
						// Need to kill reactants molecule
						p5Canvas.killingList.addAll(oxygenList);
						p5Canvas.killingList.addAll(ammoniaList);
					}
				
				}
			}
			
			break;
		case 8 :
			if(set ==1)
			{
				if(reactants.contains("Hydrogen")&&reactants.contains("Oxygen"))
				{
					float radius = 225;
					probability = 1.0f;
					randomFloat = rand.nextFloat();


						Vec2 initialPos = null;
						
						if(m1.getName().equals("Oxygen"))
							initialPos = box2d.coordWorldToPixels(m1.getPosition());
						else
							initialPos = box2d.coordWorldToPixels(m2.getPosition());
						
					//Find the second hydrogen molecule that nearby
					for (int i = 0; i < State.molecules.size(); i++) {
						Molecule mole = State.molecules.get(i);
						if (mole.getName().equals("Hydrogen")
								&& mole != m1 && mole != m2 ) {
							Vec2 posHydrogen = box2d.coordWorldToPixels(State.molecules
									.get(i).getPosition());
							if (radius > computeDistance(initialPos, posHydrogen)) {
								products.add("Water");
								products.add("Water");
								p5Canvas.killingList.add(mole);
								break;
							}
						}
					}
					
					}
				
			}
			else if(set ==2)
			{
				//H+ + HCO3- 
				if (reactants.contains("Hydrogen-Ion")
						&& reactants.contains("Bicarbonate")) {
		
						products.add("Water");
						products.add("Carbon-Dioxide");
					
				}
			}
			else
			{
				if(p5Canvas.temp>=260)
					products = this.getReactionProductsSim4(reactants, m1, m2);
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
		// Sim 8 set 2  
		if (collider.contains("Acetic-Acid")) {
			products.add("Acetate");
			products.add("Hydrogen-Ion");
		}
		else if(collider.contains("Sodium-Bicarbonate"))
		{
			products.add("Sodium-Ion");
			products.add("Bicarbonate");
		}
		return products;

	}
	
	private ArrayList<String> getReactionProductsSim4(ArrayList<String> reactants, Molecule  m1, Molecule m2)
	{
		float probability;
		Random rand =  new Random();
		float randomFloat =0;
		ArrayList<String> products = new ArrayList<String>();
		// C5H12 + 8O2 --> 5CO2 +6H2O
		if(reactants.contains("Pentane")&&reactants.contains("Oxygen"))
		{
			float radius = 350;
			probability = 1.0f;
			randomFloat = rand.nextFloat();
			int oxygenTotalNum = 8 ;
			ArrayList<Molecule> oxygenList = new ArrayList<Molecule>();
			if (randomFloat <= probability) {
				Vec2 pentanePos = null;
				if(m1.getName().equals("Pentane"))
					pentanePos = box2d.coordWorldToPixels(m1.getPosition());
				else
					pentanePos = box2d.coordWorldToPixels(m2.getPosition());
				
			// Go through all molecules to check if there are any molecules
			// nearby
			for (int i = 0; i < State.molecules.size(); i++) {
				Molecule mole = State.molecules.get(i);
				if (mole.getName().equals("Oxygen")
						&& mole != m1 && mole != m2) {
					Vec2 posOxygen = box2d.coordWorldToPixels(State.molecules
							.get(i).getPosition());
					if (radius > computeDistance(pentanePos, posOxygen)) {
						oxygenList.add(mole);
						if(oxygenList.size()>=oxygenTotalNum-1)
							break; // Break after we find one nearby
					}
				}
			}
			//If enough oxygen molecules have been found
			if(oxygenList.size()==oxygenTotalNum-1){
				for( int i = 0 ;i<5;i++)
				products.add("Carbon-Dioxide");
				for( int j = 0;j<6;j++)
					products.add("Water");
				// Need to kill reactants molecule
				p5Canvas.killingList.addAll(oxygenList);
			}
			//else //Reset killing list
				
			//}
		}
		}
		return products;
	}
	
	private float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}
	
	//In Sim 5 set 1 and 2, Mark oxygen molecules that hit by Iodine
	private void markCollision(Contact c)
	{
		Object o1 = c.m_fixtureA.m_body.getUserData(); 
		Object o2 = c.m_fixtureB.m_body.getUserData();
		Molecule waterMole = null;
		
		if( o1==null || o2 ==null)
			return;
		String s1 = o1.getClass().getName();
		String s2 = o2.getClass().getName();
		if(s1.contains("Molecule") && s2.contains("Molecule")) 
		{
			Molecule m1 = (Molecule)o1;
			Molecule m2 = (Molecule)o2;
			int index = -1;
			
			if(m1.getName().equals(inertialGasName) && m2.getName().equals("Water"))
			{
				index = State.getMoleculeIndex(m2);
				waterMole = m2;
			}
			else if(m1.getName().equals("Water")&&m2.getName().equals(inertialGasName))
			{
				index= State.getMoleculeIndex(m1);
				waterMole = m1;
			}
			
			if(index!=-1)
			{
//				collisionMark[index] = true;
				if(p5Canvas.getSet()==1)
				{
					if(waterMole.getState()==mState.Solid)
					{
						hitCount++;
						waterMole.setState(mState.Liquid);
					}
				}
				else if(p5Canvas.getSet()==2)
				{
					if(waterMole.getState()==mState.Liquid)
					{
						hitCount++;
						waterMole.setState(mState.Gas);
					}
				}
				
			}
		}
		else if(s1.contains("Boundary")||s2.contains("Boundary"))
		{
			Molecule mole = null;
			Boundary boundary = null;
			if (s1.contains("Molecule") && s2.contains("Boundary")) {
				mole = (Molecule) o1;
				boundary = (Boundary) o2;
			} else if (s1.contains("Boundary") && s2.contains("Molecule")) {
				mole = (Molecule) o2;
				boundary = (Boundary) o1;
			}
			if(mole.getName().equals(inertialGasName)) //It is inertial gas that hit the wall
			{
				if(hitCount<State.getMoleculeNumByName("Water")) //Still has ice left
				{
						maintainGasSpeed(mole); //Speed up gas if it is slow
				}
				
			}
		}
		
	}
	
	//Maintain gas speed so that it has enough scalar speed
	private void maintainGasSpeed(Molecule mole)
	{
		float velThreshold = 40;
		if(p5Canvas.getSet()==2) //If Sim 5 Set 2
			velThreshold = 50;
		float ratio = 1;
		
			if(mole.getLinearVelocityScalar()<velThreshold)
			{
				ratio = velThreshold/mole.getLinearVelocityScalar();
				ratio = (float) Math.sqrt(ratio);
				Vec2 velocity = mole.getLinearVelocity();
				velocity.mulLocal(ratio);
			}
		
	}
	public void addSpark()
	{
		if(!sparkAdded)
		{
			sparkAdded = true;
			
			//Speed up molecules
			//Ratio is set from 5 to 8
			float ratio = 9;
			for(Molecule m:State.molecules)
			{
				m.constrainKineticEnergy(ratio);
			}
			p5Canvas.calculateKE();
			p5Canvas.temp = p5Canvas.getTempFromKE();
		}

	}
	
	private void updateMoleculeMass()
	{
		float mass = 0;
		int count = 0;
		float totalMass =0 ;
		ArrayList<String> names = State.getCompoundNames();
		for(String name: names)
		{
			mass = Compound.getMoleculeWeight(name);
			count = State.getMoleculeNumByName(name);
			totalMass = (float)count/numMoleculePerMole * mass;
			moleculeMassHash.put(name, totalMass);
		}
	}
	
	public float getMassByName(String name)
	{
		if(moleculeMassHash.containsKey(name))
		return moleculeMassHash.get(name);
		else
			return 0;
	}
	
	public void resetTableView(int sim,int set)
	{
			
		if(sim==2)
		{
			((TableView) p5Canvas.getTableView()).setColumnName(0, "Mass");
			((TableView) p5Canvas.getTableView()).setColumnWidth(0, 20);
			((TableView) p5Canvas.getTableView()).setColumnWidth(1, 30);
			((TableView) p5Canvas.getTableView()).setColumnWidth(2, 100);
		}
		else if(sim==8||sim==7)
		{
			((TableView) p5Canvas.getTableView()).setColumnName(0, "Moles");
			((TableView) p5Canvas.getTableView()).setColumnWidth(0, 20);
			((TableView) p5Canvas.getTableView()).setColumnWidth(1, 30);
			((TableView) p5Canvas.getTableView()).setColumnWidth(2, 100);
		}
		else {
			super.resetTableView(sim, set);
		}
	}
	
	
	//Function that return the specific data to Canvas
	public float getDataTickY(int sim,int set,int indexOfGraph, int indexOfCompound)
	{
		float res = 0;
		String name = null;
		if(sim==2)
		{
			name = (String)Compound.names.get(indexOfCompound);
			res =  getMassByName(name);
		}
		else if(sim==8){
			name = (String)Compound.names.get(indexOfCompound);
			res =  getMoleByName(name);
		}
		else 
		{
			res = super.getDataTickY(sim, set, indexOfGraph, indexOfCompound);
		}
		return res;

	}
	
	//Function to return the specific data to TableView
	public float getDataTableView(int sim, int set, int indexOfCompound) {
		String name = null;
		float res = 0;
		if(p5Canvas.getSim()==2) //Sim 2 output mass
		{
			name = (String)Compound.names.get(indexOfCompound);
			res =  getMassByName(name);	
		}
		else if(p5Canvas.getSim()==8||p5Canvas.getSim()==7) // Sim 8 output mole
		{
			name = (String)Compound.names.get(indexOfCompound);
			res = getMoleByName(name);
		}
		else //output number of molecules
		{
			res = super.getDataTableView(sim, set, indexOfCompound);

		}
		return res;		
	}

	@Override
	protected void initializeSimulation(int sim, int set) {


		updateMoleculeMass();

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