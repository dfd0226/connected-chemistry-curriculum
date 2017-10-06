package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.io.BufferedReader;

import org.xml.sax.InputSource;

import processing.core.PApplet;


import com.esotericsoftware.yamlbeans.YamlReader;

public class YAMLinterface {
	public static String url = "data/sims.yml";
	public static String yaml;


	public static Map getYamlText() {
		HashMap hm = new HashMap();
		try {
			if (yaml == null) {
				ResourceReader rr = new ResourceReader(url);
				yaml = rr.read();
			}
			YamlReader reader = new YamlReader(yaml);

			hm = (HashMap)reader.read();

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}

		return hm;
	}


	/*
	 * Unit Functions
	 */
	public static ArrayList getUnits() {
		try {
			ArrayList yaml = (ArrayList)getYamlText().get("units");
			return yaml;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static HashMap getUnit(int unitNumber) {
		ArrayList units = getUnits();
		HashMap unit = new HashMap();

		for (int i = 0; i<units.size(); i++) {
			unit = (HashMap)units.get(i);
			int n = Integer.parseInt((String)unit.get("unit"));
			if (unitNumber == n) {
				return unit;
			}
		}
		return null;
	}

	public static String getUnitName(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		String unitName = (String)unit.get("name");
		return unitName;
	}



	/*
	 * Sim Functions
	 */

	public static ArrayList getSims(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		if (unit!=null){
			ArrayList sims = (ArrayList)unit.get("sims");
			return sims;
		}
		else{
			return null;
		}
	}

	public static HashMap getSim(int unitNumber, int simNumber) {
		ArrayList sims = getSims(unitNumber);
		HashMap sim = new HashMap();

		if (sims==null) return null;
		for (int i = 0; i<sims.size(); i++) {
			sim = (HashMap)sims.get(i);
			int n = Integer.parseInt((String)sim.get("sim"));
			if (simNumber == n) {
				return sim;
			}
		}
		return null;
	}

	public static String getSimName(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		String simName = (String)sim.get("name");
		return simName;
	}

	/*
	 * Control Functions
	 */
	private static ArrayList getControls() {
		try {
			ArrayList yaml = (ArrayList)getYamlText().get("controls");
			return yaml;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	private static ArrayList getControls(int unitNumber) {
		HashMap unit = getUnit(unitNumber);
		if (unit==null) return null;

		ArrayList<HashMap> al1 = new ArrayList();
		al1 = getControls();

		ArrayList<HashMap> al2 = new ArrayList();
		al2 = (ArrayList)unit.get("controls");

		return combineControls(al1, al2);
	}

	private static ArrayList<HashMap> getControls(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		if (sim==null) return null;
		
		ArrayList<HashMap> al1 = new ArrayList();
		al1 = getControls(unitNumber);

		ArrayList<HashMap> al2 = new ArrayList();
		al2 = (ArrayList)sim.get("controls");

		return combineControls(al1, al2);
	}

	private static ArrayList<HashMap> combineControls(ArrayList<HashMap> al1, ArrayList<HashMap> al2) {
		ArrayList<HashMap> output = new ArrayList();

		if (al1 != null && al2 != null) {
			for (int i = 0; i < al1.size(); i++) {
				HashMap item1 = al1.get(i);
				String itemName = (String)item1.get("control");

				for (int j = 0; j<al2.size(); j++) {
					HashMap item2 = al2.get(j);
					if (item2.containsValue(itemName)) {
						item1.putAll(item2);
					}
				}
				output.add(item1);
			}
		} else if (al1 == null) {
			output = al2;
		} else {
			output = al1;
		}
		return output;
	}
	
	private static boolean getControlState(ArrayList<HashMap> controls, String controlName) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals(controlName) && control.get("state").equals("off")) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean getControlTimerState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "timer"); }
	private static boolean getControlTimerState() 								{ return getControlTimerState(getControls()); }
	private static boolean getControlTimerState(int unitNumber) 				{ return getControlTimerState(getControls(unitNumber)); }
	public static boolean getControlTimerState(int unitNumber, int simNumber) 	{ return getControlTimerState(getControls(unitNumber, simNumber)); }
	
	private static Float getControlTimerTime(ArrayList<HashMap> controls, String setting) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals("timer")) {
				return Float.valueOf((String) control.get(setting)).floatValue();
			}
		}
		return 0.f;
	}
	private static Float getControlTimerTimeMin(ArrayList<HashMap> controls) 			{ return getControlTimerTime(controls, "min"); }
	private static Float getControlTimerTimeInit(ArrayList<HashMap> controls) 			{ return getControlTimerTime(controls, "init"); }
	private static Float getControlTimerTimeMax(ArrayList<HashMap> controls) 			{ return getControlTimerTime(controls, "max"); }
	private static Float getControlTimerTimeMin() 										{ return getControlTimerTimeMin(getControls()); }
	private static Float getControlTimerTimeMin(int unitNumber) 						{ return getControlTimerTimeMin(getControls(unitNumber)); }
	public static Float getControlTimerTimeMin(int unitNumber, int simNumber) 			{ return getControlTimerTimeMin(getControls(unitNumber, simNumber)); }
	private static Float getControlTimerTimeInit() 									{ return getControlTimerTimeInit(getControls()); }
	private static Float getControlTimerTimeInit(int unitNumber) 						{ return getControlTimerTimeInit(getControls(unitNumber)); }
	public static Float getControlTimerTimeInit(int unitNumber, int simNumber) 		{ return getControlTimerTimeInit(getControls(unitNumber, simNumber)); }
	private static Float getControlTimerTimeMax() 										{ return getControlTimerTimeMax(getControls()); }
	private static Float getControlTimerTimeMax(int unitNumber) 						{ return getControlTimerTimeMax(getControls(unitNumber)); }
	public static Float getControlTimerTimeMax(int unitNumber, int simNumber) 			{ return getControlTimerTimeMax(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlVolumeSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "volume slider"); }
	private static boolean getControlVolumeSliderState() 								{ return getControlVolumeSliderState(getControls()); }
	private static boolean getControlVolumeSliderState(int unitNumber) 				{ return getControlVolumeSliderState(getControls(unitNumber)); }
	public static boolean getControlVolumeSliderState(int unitNumber, int simNumber) 	{ return getControlVolumeSliderState(getControls(unitNumber, simNumber)); }
	private static boolean getControlPressureSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "pressure slider"); }
	private static boolean getControlPressureSliderState() 								{ return getControlVolumeSliderState(getControls()); }
	private static boolean getControlPressureSliderState(int unitNumber) 				{ return getControlPressureSliderState(getControls(unitNumber)); }
	public static boolean getControlPressureSliderState(int unitNumber, int simNumber) { return getControlPressureSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlScaleSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "scale slider"); }
	private static boolean getControlScaleSliderState() 								{ return getControlScaleSliderState(getControls()); }
	private static boolean getControlScaleSliderState(int unitNumber) 					{ return getControlScaleSliderState(getControls(unitNumber)); }
	public static boolean getControlScaleSliderState(int unitNumber, int simNumber) 	{ return getControlScaleSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlSpeedSliderState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "speed slider"); }
	private static boolean getControlSpeedSliderState() 								{ return getControlSpeedSliderState(getControls()); }
	private static boolean getControlSpeedSliderState(int unitNumber) 					{ return getControlSpeedSliderState(getControls(unitNumber)); }
	public static boolean getControlSpeedSliderState(int unitNumber, int simNumber) 	{ return getControlSpeedSliderState(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlHeatSliderState(ArrayList<HashMap> controls) 		{ return getControlState(controls, "heat slider"); }
	private static boolean getControlHeatSliderState() 								{ return getControlHeatSliderState(getControls()); }
	private static boolean getControlHeatSliderState(int unitNumber) 					{ return getControlHeatSliderState(getControls(unitNumber)); }
	public static boolean getControlHeatSliderState(int unitNumber, int simNumber) 	{ return getControlHeatSliderState(getControls(unitNumber, simNumber)); }
	
	private static Float getControlHeatSliderHeat(ArrayList<HashMap> controls, String setting) {
		HashMap timer = new HashMap();
		for (int i = 0; i < controls.size(); i++) {
			HashMap control = controls.get(i);
			if (control.get("control").equals("heat slider")) {
				return Float.valueOf((String) control.get(setting)).floatValue();
			}
		}
		return 0.f;
	}
	private  Float getControlHeatSliderMin(ArrayList<HashMap> controls) 			{ return getControlHeatSliderHeat(controls, "min"); }
	private  Float getControlHeatSliderInit(ArrayList<HashMap> controls) 		{ return getControlHeatSliderHeat(controls, "init"); }
	private  Float getControlHeatSliderMax(ArrayList<HashMap> controls) 			{ return getControlHeatSliderHeat(controls, "max"); }
	private  Float getControlHeatSliderMin() 									{ return getControlHeatSliderMin(getControls()); }
	private  Float getControlHeatSliderMin(int unitNumber) 						{ return getControlHeatSliderMin(getControls(unitNumber)); }
	public  Float getControlHeatSliderMin(int unitNumber, int simNumber) 		{ return getControlHeatSliderMin(getControls(unitNumber, simNumber)); }
	private  Float getControlHeatSliderInit() 									{ return getControlHeatSliderInit(getControls()); }
	private  Float getControlHeatSliderInit(int unitNumber) 						{ return getControlHeatSliderInit(getControls(unitNumber)); }
	public  Float getControlHeatSliderInit(int unitNumber, int simNumber) 		{ return getControlHeatSliderInit(getControls(unitNumber, simNumber)); }
	private  Float getControlHeatSliderMax() 									{ return getControlHeatSliderMax(getControls()); }
	private  Float getControlHeatSliderMax(int unitNumber) 						{ return getControlHeatSliderMax(getControls(unitNumber)); }
	public  Float getControlHeatSliderMax(int unitNumber, int simNumber) 		{ return getControlHeatSliderMax(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlMoleculeSidebarState(ArrayList<HashMap> controls) { return getControlState(controls, "molecule sidebar"); }
	private static boolean getControlMoleculeSidebarState() 							{ return getControlMoleculeSidebarState(getControls()); }
	private static boolean getControlMoleculeSidebarState(int unitNumber) 				{ return getControlMoleculeSidebarState(getControls(unitNumber)); }
	public static boolean getControlMoleculeSidebarState(int unitNumber, int simNumber) 	{ return getControlMoleculeSidebarState(getControls(unitNumber, simNumber)); }
	
	private static boolean getControlPeriodicTableState(ArrayList<HashMap> controls) 	{ return getControlState(controls, "periodic table"); }
	private static boolean getControlPeriodicTableState() 								{ return getControlPeriodicTableState(getControls()); }
	private static boolean getControlPeriodicTableState(int unitNumber) 				{ return getControlPeriodicTableState(getControls(unitNumber)); }
	public static boolean getControlPeriodicTableState(int unitNumber, int simNumber) 	{ return getControlPeriodicTableState(getControls(unitNumber, simNumber)); }
	
	/*
	 * Set Functions
	 */

	public static ArrayList getSets(int unitNumber, int simNumber) {
		HashMap sim = getSim(unitNumber, simNumber);
		if (sim==null) return null;
		ArrayList sets = (ArrayList)sim.get("sets");
		return sets;
	}

	public static HashMap getSet(int unitNumber, int simNumber, int setNumber) {
		ArrayList sets = getSets(unitNumber, simNumber);
		HashMap set = new HashMap();

		if (sets==null) return null;
		for (int i = 0; i<sets.size(); i++) {
			set = (HashMap)sets.get(i);
			int n = Integer.parseInt((String)set.get("set"));
			if (setNumber == n) {
				return set;
			}
		}
		return null;
	}

	public static ArrayList getSetCompounds(int unitNumber, int simNumber, int setNumber) {
		HashMap set = getSet(unitNumber, simNumber, setNumber);
		if (set==null) return null;
		ArrayList compounds = (ArrayList)set.get("compounds");
		return compounds;
	}

	/*
	 * Temperature Functions
	 */

	private static Float getTemperature(int unitNumber) {
		Float temp = State.defaultTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getTemperature(int unitNumber, int simNumber) {
		Float temp = getTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("temperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMinTemperature(int unitNumber) {
		Float temp = State.defaultMinTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMinTemperature(int unitNumber, int simNumber) {
		Float temp = getMinTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getMinTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getMinTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("minTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMaxTemperature(int unitNumber) {
		Float temp = State.defaultMaxTemperature;

		try {
			HashMap unit = getUnit(unitNumber);
			temp = Float.valueOf((String)unit.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	private static Float getMaxTemperature(int unitNumber, int simNumber) {
		Float temp = getMaxTemperature(unitNumber);

		try {
			HashMap sim = getSim(unitNumber, simNumber);
			temp = Float.valueOf((String)sim.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}

	public static Float getMaxTemperature(int unitNumber, int simNumber, int setNumber) {
		Float temp = getMaxTemperature(unitNumber, simNumber);

		try {
			HashMap set = getSet(unitNumber, simNumber, setNumber);
			temp = Float.valueOf((String)set.get("maxTemperature")).floatValue();
		}
		catch (Exception e) {}
		return temp;
	}


	/*
	 * Compound Functions
	 */

	public static HashMap getCompound(int unitNumber, int simNumber, int setNumber, int comNumber) {
		ArrayList compounds = getSetCompounds(unitNumber, simNumber, setNumber);
		HashMap compound = new HashMap();

		if (compounds ==null) return null;
		compound = (HashMap) compounds.get(comNumber);
		return compound;
	}

	//Get initial quantity of Compounds
	public static String getCompoundQty(int unitNumber, int simNumber, int setNumber, int comNumber) {
		HashMap set = getCompound(unitNumber, simNumber, setNumber,comNumber);
		if (set==null) return null;
		return (String)set.get("qty");
	}
	//Get maximum available number of Compounds
	public static String getCompoundCap(int unitNumber, int simNumber, int setNumber, int comNumber) {
		HashMap set = getCompound(unitNumber, simNumber, setNumber,comNumber);
		String defaultMax = new String("95");
		if (set==null) 
			return null;
		else if (set.get("cap")==null)
			return defaultMax;
		else
		return (String)set.get("cap");
	}

	//Get Compound Name
	public static String getCompoundName(int unitNumber, int simNumber, int setNumber, int comNumber) {
		HashMap set = getCompound(unitNumber, simNumber, setNumber,comNumber);
		if (set==null) return null;
		return (String)set.get("compound");
	}
}
