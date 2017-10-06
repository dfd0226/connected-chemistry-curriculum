package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.ZipEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.JarURLConnection;
import java.net.URL;
import java.sql.*;



public class DBinterface {
	
	private Connection conn= null;
	private static Statement stat = null;
	private String destDir = null;
	private String dbFileName= new String("chemdb");
	private String jarFileName = new String("Simulation.jar");
	public static String versionString = new String ("Ver 1.7.2");
	
	public DBinterface()
	{
		
		try{
			Class.forName("org.sqlite.JDBC");
	
			
			//********************* Distribution Configuration ***************************
			ExtractFileFromJar();
			File dbFile = new File(destDir, dbFileName);
						
			if (dbFile.exists()) {
			    // DB file is in the same directory with jar file
				conn = DriverManager.getConnection("jdbc:sqlite:"+dbFile.getPath());
			}
			//******************************************************************************
			
			 
			
			
			/*
			//************* Debug Configuration, please comment this block before make a release version ***********
			String dbPath = ClassLoader.getSystemResource("data/"+dbFileName).toString();
			dbPath = dbPath.replace("file:", "");
			conn = DriverManager.getConnection("jdbc:sqlite:src/data/chemdb");
			//************************************************************************************************************
		    */
			
			stat = conn.createStatement();
			
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void ExtractFileFromJar()
	{
		//Get path of DBinterface class
		String thisDir = getClass().getResource("").getPath();
		
		if(thisDir.contains("file:"))  //Mac system path
			thisDir= thisDir.replace("file:", "");
		else  //Windows system path "/C:/", we need to get rid of the first '/'
			thisDir= thisDir.substring(1);
		
		//System.out.println("thisDir is "+thisDir);
		String jarPath = thisDir.replace("!/data/", "");  //Get path of jar file
		int lastSlashIndex = jarPath.lastIndexOf("/");
		jarFileName = new String(jarPath.substring(lastSlashIndex+1));
		destDir = thisDir.replace(jarFileName+"!/data/", "");  //Set destDir as the current folder in which jar file sits

		//Find Jar file
		try {
			File jarFile = new File(jarPath);
			if (jarFile.isDirectory() || !jarFile.exists()) { //If we cant find jar File in this jarPath
				//In windows it is like this "C:/Users/Esheen/Desktop/ConnChem_1.1.0/Simulation/data/" 
				File newJarfile = new File(jarPath);
				String parent = newJarfile.getParentFile().getParent();
				parent = parent.concat(new String("\\"+jarFileName));
				
				jarPath= new String(parent);
				
			} 
			java.util.jar.JarFile jar = new java.util.jar.JarFile(jarPath);
			
			//Unzip database from jar file
			ZipEntry entry = jar.getEntry("data/chemdb");
			File outputFile = new File(destDir, dbFileName);
			
				if (entry.isDirectory()) { // if its a directory, create it
					outputFile.mkdir();
				}
				InputStream in = jar.getInputStream(entry);
				FileOutputStream fos = new java.io.FileOutputStream(outputFile);
				while (in.available() > 0) { // write contents of 'is' to 'fos'
					fos.write(in.read());
				}
				fos.close();
				in.close();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList dbConnect(String[] args) {
		ArrayList output = new ArrayList();
		try {

			ResultSet rs = stat.executeQuery(args[0]);

			while (rs.next()) {
				output.add(rs.getString(args[1]));
			}
			rs.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		return output;
	}
	protected void finalize() throws Throwable
	{
		try {
			conn.close();
		}
		finally{
			super.finalize();
		}
	}

	public static ArrayList getAllCompoundNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);

		return output;
	}

	public static ArrayList getAllCompoundFormulas(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM compounds ORDER BY " + order_;
		args[1] = "formula";
		output = dbConnect(args);

		return output;
	}

	public static ArrayList getAllElementNames(String order_) {
		ArrayList output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT * FROM elements ORDER BY " + order_;
		args[1] = "name";
		output = dbConnect(args);

		return output;
	}

	public static Float getElementMass(String elementName_) {

		//ArrayList results = new ArrayList();
		
		if(elementName_.equals("Carbonate"))
			return 75f;
		else if(elementName_.equals("Hydroxide"))
			return 17f;
			
		String[] args = new String[2];
		args[0] = "SELECT mass FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "mass";
		ArrayList results = dbConnect(args);

		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	// TUAN
	public static int getElementCharge(String elementName_) {
		if (elementName_.equals("Hydrogen"))
			return 1;
		else if (elementName_.equals("Oxygen"))
			return -2;	
		else if (elementName_.equals("Sodium"))
			return 1;	
		else if (elementName_.equals("Chloride"))
			return -1;	
		else if (elementName_.equals("Chlorine"))
			return -1;	
		else if (elementName_.equals("Chlorine"))
			return -1;	
		else if (elementName_.equals("Silicon"))
			return 4;	
		else if (elementName_.equals("Calcium"))
			return 2;
		else if (elementName_.equals("Potassium"))
			return 1;	
		else if (elementName_.equals("Silver"))
			return 1;
		else if (elementName_.equals("Lithium"))
			return 1;
		else if (elementName_.equals("Sulfur"))
			return -2;
		else if (elementName_.equals("Bromide"))
			return -1;
		else if (elementName_.equals("Bromine"))
			return -1;
		else if (elementName_.equals("Carbonate"))
			return -2;
		else if (elementName_.equals("Hydroxide"))
			return -1;
		
		return 0;  	
	}
		
	
	public static float[] getMinimumLiquidEnergy(String elementName_) {
		float[] result = new float[2];
		float min = 0.2f;
		float range = 0.45f;
		if (elementName_.equals("Glycerol")){
			min = 3.5f;
			range =4;
		}
		else if (elementName_.equals("Pentane")){
			min = 0.035f;
			range =0.0f;
		}
		else if (elementName_.equals("Acetic-Acid")){
			min = 2f;
			range =0.5f;
		}
		else if (elementName_.equals("Mercury")){
			min = 0.5f;
			range =0.5f;
		}
		result[0]=min;
		result[1]=range;
		return result;  	
	}
	public static float getMinimumGasEnergy(String elementName_) {
		float min = 3f;
		if (elementName_.equals("Acetic-Acid")){
			min = 17f;
		}
		else if (elementName_.equals("Pentane")){
			min = 0.7f;
		}
		return min;  	
	}
	
	
	//********************************************
	
	public static Float getElementRadiusAtomic(String elementName_) {
		String[] args = new String[2];
		args[0] = "SELECT radiusAtomic FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "radiusAtomic";
		ArrayList results = dbConnect(args);

		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static Float getElementRadiusIonic(String elementName_) {
		String[] args = new String[2];
		args[0] = "SELECT radiusIonic FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "radiusIonic";
		ArrayList results = dbConnect(args);

		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static Float getElementArea(String elementName_) {
		Float radius = getElementRadiusAtomic(elementName_);
		return (float) (Math.PI * (radius * radius));
	}

	public static Float getElementAreaIonic(String elementName_) {
		Float radius = getElementRadiusIonic(elementName_);
		return (float) (Math.PI * (radius * radius));
	}

	public static Float getElementDensity(String elementName_) {
		String[] args = new String[2];
		args[0] = "SELECT density FROM elements WHERE name = \"" + elementName_ + "\"";
		args[1] = "density";
		ArrayList results = dbConnect(args);

		try {
			return Float.valueOf((String)results.get(0)).floatValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static ArrayList<String> getCompoundConstituentElements(String compoundName_) {
		ArrayList<String> output = new ArrayList();

		String[] args = new String[2];
		args[0] = "SELECT E.name FROM compounds as C, compounds_elements as CE, elements as E WHERE CE.compound_id = C.id and CE.element_id = E.id and C.name = \"" + compoundName_ + "\"";
		args[1] = "name";
		output = dbConnect(args);

		return output;
	}

	public static Float getCompoundMass(String compoundName_) {
		ArrayList elementMasses = new ArrayList();
		float mass = 0;

		String[] args = new String[2];
		args[0] = "SELECT E.mass, E.name FROM compounds as C, compounds_elements as CE, elements as E WHERE C.name = \"" + compoundName_ + "\" and C.id = CE.compound_id and E.id = CE.element_id";
		args[1] = "mass";
		elementMasses = dbConnect(args);

		try {
			for (int i = 0; i<elementMasses.size();i++) {
				String elementMass = (String)elementMasses.get(i);
				mass += Float.valueOf(elementMass);
			}
			return mass;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static String getCompoundFormula(String compoundName_) {
		String formula = "blork";

		String[] args = new String[2];
		args[0] = "SELECT formula FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "formula";
		ArrayList results = dbConnect(args);

		try {
			return (String)results.get(0);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static Integer getCompoundCharge(String compoundName_) {
		Integer charge = 0;

		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT charge FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "charge";
		results = dbConnect(args);

		try {
			charge = Integer.valueOf((String)results.get(0)).intValue();
			return charge;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public static Float getCompoundBoilingPointCelsius(String compoundName_) {
		Float boilPoint = 100.f;

		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT boilingPointCelsius FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "boilingPointCelsius";
		results = dbConnect(args);

		if (results.get(0)==null) return 100f;
		boilPoint = Float.valueOf((String) results.get(0)).floatValue();
		return boilPoint;
	}

	public static Float getCompoundBoilingPointKelvin(String compoundName_) {
		Float c = getCompoundBoilingPointCelsius(compoundName_);
		return c - 273.15f;
	}

	public static Float getCompoundFreezingPointCelsius(String compoundName_) {
		Float freezePoint = 0.f;

		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT freezingPointCelsius FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "freezingPointCelsius";
		results = dbConnect(args);

		if (results.get(0)==null) return 0f;
		freezePoint = Float.valueOf((String) results.get(0)).floatValue();
		return freezePoint;
		
	}

	public static Float getCompoundFreezingPointKelvin(String compoundName_) {
		Float c = getCompoundFreezingPointCelsius(compoundName_);
		return c - 273.15f;
	}

	public static Float getCompoundDensity(String compoundName_) {
		Float density = 0.f;

		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT density FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "density";
		results = dbConnect(args);

		try {
			density = Float.valueOf((String) results.get(0)).floatValue();
			return density;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}


	}

	public static Boolean getCompoundPolarity(String compoundName_) {
		String[] args = new String[2];
		args[0] = "SELECT polarity FROM compounds WHERE name = \"" + compoundName_ + "\"";
		args[1] = "polarity";
		ArrayList results = dbConnect(args);

		try {
			Integer truthy = Integer.valueOf((String)results.get(0)).intValue();
			if (truthy == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	/*
	public static Integer getReactionNumber(ArrayList<String> reactants) {

		String reactant;
		String[] args = new String[2];
		ArrayList possibleReactionsMatrix = new ArrayList();

		// poll database for each reactant
		for (int i = 0; i<reactants.size(); i++) {
			reactant = reactants.get(i);
			args[0] = "SELECT R.id FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and C.name = \"" + reactant + "\" and RC.type = \"input\"";
			args[1] = "id";
			possibleReactionsMatrix.add(dbConnect(args));
		}

		// short circuit if no reactions match reactants
		if (possibleReactionsMatrix.size() == 0) {
			return null;
		}

		// make sure that a reaction is common for all reactants
		ArrayList commonReactions = new ArrayList();
		commonReactions = (ArrayList)possibleReactionsMatrix.get(0);
		for (int i = 0; i<possibleReactionsMatrix.size(); i++) {
			commonReactions.retainAll((Collection) possibleReactionsMatrix.get(i));
		}

		// short circuit if anything other than 1 reaction possible
		if (commonReactions.size() != 1) {
			return null;
		}

		String reaction = (String)commonReactions.get(0);

		// short circuit if quantity of input reactants does not match required number 
		args = new String[2];
		args[0] = "SELECT C.name FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and RC.type = \"input\" and R.id = " + reaction;
		args[1] = "name";
		if (dbConnect(args).size() != reactants.size()) {
			return null;
		}
		return Integer.parseInt(reaction);
	}

	public static ArrayList<String> getReactionProducts(ArrayList<String> reactants) {
		ArrayList<String> products = new ArrayList<String>();

		Integer reaction = getReactionNumber(reactants);

		// get products
		String[] args = new String[2];
		args[0] = "SELECT C.name FROM reactions as R, reactions_compounds as RC, compounds as C WHERE C.id = RC.compound_id and R.id = RC.reaction_id and RC.type = \"output\" and R.id = " + reaction;
		args[1] = "name";
		products = dbConnect(args);

		// short circuit if no products
		if (products.size() == 0) {
			return null;
		}
		else {
			return products;
		}
	}
	
	

	public static Float getReactionProbability(ArrayList<String> reactants) {
		Integer reaction = getReactionNumber(reactants);
		Float defaultReactProb = 0.f;

		try {
			return getReactionProbability(reaction);
		} catch (Exception e) {
			//System.out.println("Reaction invalid: " + e);
			return defaultReactProb;
		}
	}
	*/
	public static int getReactionId(Integer unit,Integer sim,Integer set)
	{
		// get reaction Id
		int id = -1;
		ArrayList<String> result = new ArrayList<String>();
		String[] args = new String[2];
		args[0] = "SELECT R.id FROM reactions as R WHERE R.unit = "+unit+" and R.sim = "+sim+" and R.set_ = "+set;
		args[1] = "id";
		result = dbConnect(args);

		// short circuit if no result
		if (result.isEmpty()) {
			return id;
		}
		else {
			//System.out.println("getReactionId: "+result);
			id = Integer.parseInt(result.get(0));
			return id;
		}
	}
	public static ArrayList<String> getReactionOutputs(int unit,int sim,int set)
	{
		ArrayList<String> products = new ArrayList<String>();
		int id = getReactionId(unit,sim,set);
		
		String[] args = new String[2];
		args[0] = "SELECT C.name FROM reactions_compounds as RC, compounds as C  WHERE RC.reaction_id ="+id+" and RC.type = \'output\' and RC.compound_id = C.id";
		args[1] = "name";
		products = dbConnect(args);
		
		if (products.isEmpty()) {
			return null;
		}
		else {
			//System.out.println("getReactionProducts: "+products);
			return products;
		}
		
	}
	public static int getReactionCompoundsNum(int unit,int sim,int set,String compound)
	{
		ArrayList<String> result = new ArrayList<String>();
		int id = getReactionId(unit,sim,set);
		int num = -1;
		
		String[] args = new String[2];
		args[0] = "SELECT RC.compound_num FROM reactions_compounds as RC, compounds as C  WHERE RC.reaction_id ="+id+" and RC.compound_id = C.id and C.name = \""+compound+"\"";
		args[1] = "compound_num";
		result = dbConnect(args);
		
		if (result.isEmpty()) {
			return num;
		}
		else {
			//System.out.println("getReactionProducts: "+result);
			num = Integer.parseInt(result.get(0));
			return num;
		}
		
	}
	
	public static ArrayList<String> getReactionInputs(int unit,int sim,int set)
	{
		ArrayList<String> products = new ArrayList<String>();
		int id = getReactionId(unit,sim,set);
		
		String[] args = new String[2];
		args[0] = "SELECT C.name FROM reactions_compounds as RC, compounds as C  WHERE RC.reaction_id ="+id+" and RC.type = \'input\' and RC.compound_id = C.id";
		args[1] = "name";
		products = dbConnect(args);
		
		if (products.isEmpty()) {
			return null;
		}
		else {
			//System.out.println("getReactionProducts: "+products);
			return products;
		}
		
	}
	/*
	public static float getMoleculeWeight(String compound)
	{
		ArrayList<String> result = new ArrayList<String>();
		float mass=0;
		
		String[] args = new String[2];
		args[0] = "SELECT C.mass FROM compounds as C  WHERE C.name = \""+compound+"\"";
		args[1] = "mass";
		result = dbConnect(args);
		
		if (result.isEmpty()) {
			return mass;
		}
		else {
			mass = Float.parseFloat(result.get(0));
			return mass;
		}
		
	}*/
	
	

	public static Float getReactionProbability(int reactionNumber_) {
		Float defaultReactProb = 0.f;

		ArrayList results = new ArrayList();
		String[] args = new String[2];
		args[0] = "SELECT probability FROM reactions WHERE id = " + reactionNumber_;
		args[1] = "probability";
		results = dbConnect(args);

		try {
			return Float.parseFloat((String)results.get(0));
		} catch (Exception e) {
			//System.out.println("Reaction invalid: " + e);
			return defaultReactProb;
		}
	}
	public static float getEntalpy(String compoundName_,String state)
	{
		float enthalpy = 0f;
		float defaultEnthalpy = 0f;
		ArrayList<String> results = new ArrayList<String>();
		String args[]= new String[2];
		args[0] = "SELECT [enthalpy(kJ/mol)] FROM compounds C, compounds_thermo T WHERE C.id = T.compound_id AND C.name = \""+ compoundName_+"\" AND T.state = \""+state+"\"";
		args[1] = "enthalpy(kJ/mol)";
		results = dbConnect(args);
		
		if (results==null || results.isEmpty()) return defaultEnthalpy;
		enthalpy = Float.valueOf((String) results.get(0)).floatValue();
		return enthalpy;
		
	}
	
	public static float getEntropy(String compoundName_,String state)
	{
		float entropy = 0f;
		float defaultEntropy = 0f;
		ArrayList<String> results = new ArrayList<String>();
		String args[]= new String[2];
		args[0] = "SELECT [entropy(J/mol*K)] FROM compounds C, compounds_thermo T WHERE C.id = T.compound_id AND C.name = \""+ compoundName_+"\" AND T.state = \""+state+"\"";
		args[1] = "entropy(J/mol*K)";
		results = dbConnect(args);
		
		if (results==null || results.isEmpty()||results.get(0)==null) return defaultEntropy;
		entropy = Float.valueOf((String) results.get(0)).floatValue();
		return entropy;
		
	}
}
