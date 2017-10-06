package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.plaf.ToolTipUI;

import data.DBinterface;
import data.State;


import simulations.P5Canvas;
import simulations.Unit2;
import simulations.models.Compound;
import simulations.models.Molecule;

import static simulations.P5Canvas.*;
import static simulations.models.Compound.*;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//import main.DynamicGraph.DataGenerator;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import Util.ColorCollection;

/**
 * * A demo application showing a dynamically updated chart that displays the *
 * current JVM memory usage. *
 * <p>
 * * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE.
 * * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class Canvas extends JPanel {
	/** Time series for total memory used. */
	private XYSeries total;
	/** Time series for free memory. */
	private XYSeries free;
	
	private int count = 0 ;
	
	private AxisChangeListener rangeListener;
	
	private P5Canvas p5Canvas;
	private TableView tableView;
	private Main main;
	private int numGraph;
	private int indexGraph;
	private ArrayList<String[]> labelStrings;
	private boolean paintLineEnabled;
	private final int numGraphMax = 5;
	private DynamicGraph  dynamicGraph;
	private int tickInterval = 1000;   //In millisecond
	Color colorLine[];

	/**
	 * * Creates a new application. * * @param maxAge the maximum age (in
	 * milliseconds).
	 */
	public Canvas( Main parent) {
		super(new BorderLayout());
		main = parent;
		p5Canvas = main.getP5Canvas();
		tableView = main.getTableView();
		colorLine = ColorCollection.getColorGraphLine();
		dynamicGraph = new DynamicGraph(this);
		this.add(dynamicGraph);

	}
	

	
	public void reset(){
		if(this.isEnabled())
		{

		setupGraphNum();
		setupLabels();
		
		//Reset dynamic graph
		dynamicGraph.reset(numGraph);
		
		//Choose to show graph with current index
		showGraphByIndex(indexGraph);
		
		setPaintLineEnable(true);

		this.repaint();
		}
	}
	
	//Not show the selected row by name


	
	private void setupGraphNum()
	{
		int unit = main.getSelectedUnit();
		int sim = main.getSelectedSim();
		numGraph = 1;
		if(unit==8 && (sim==6))
			numGraph = 2;
		indexGraph = 0;

	}
	
	protected void setupLabels()
	{
		
		int unit = main.getSelectedUnit();
		int sim = main.getSelectedSim();
		
		//Set up String arrays

		labelStrings = new ArrayList<String []>();
		
		//Set up X Labels
		labelStrings.add(new String[2]);
		labelStrings.get(0)[0]= new String("Time (s)");
		if(unit==8)
		{
			if(sim==6) //Volume of Base - PH
			{
				labelStrings.add(new String[2]);
				labelStrings.get(1)[0]= new String("Volume Added (mL)");
			}

		}
		
		//Set up Y Labels
		switch(unit)
		{
		default:
			labelStrings.get(0)[1] = new String("# molecules");
		break;
		case 3:
		case 4:
			labelStrings.get(0)[1] = new String("total mass (g)");
			break;
		case 5:
		case 7:
			labelStrings.get(0)[1] = new String("Concentration (M)");
			break;
		case 6:
			if(sim==2)
				labelStrings.get(0)[1] = new String("total mass (g)");
			else if(p5Canvas.getSim()==8||p5Canvas.getSim()==7)
				labelStrings.get(0)[1] = new String("Moles");
			else
				labelStrings.get(0)[1] = new String("# molecules");
			break;
		case 8:
				labelStrings.get(0)[1] = new String("# molecules");
				if(sim==6)
				{
					
					labelStrings.get(1)[1] = new String("    pH    ");
				}
			
			break;

		}
			
	}
	
	
	//Called by timer every time timer ticks
	public void addDataPerTick()
	{
		//Create and Add dynamic graphs to Canvas
		
		dynamicGraph.addDataObservation();
		
	}
	
	//Choose which graph to show 
	public void showGraphByIndex(int index)
	{
		dynamicGraph.showPlot(index);
	}
	
	//Change graph when user click on switch graph button
	public void switchGraph()
	{
		indexGraph = (++indexGraph)%numGraph;
		showGraphByIndex(indexGraph);
	}
	//Set the minimum tick value for Y axis
	public void setRangeYAxis(float lowerBound, float upperBound)
	{
		this.dynamicGraph.setRangeYAxis(0, lowerBound, upperBound);
	}
	//Set the minimum tick value for Y axis
	public void setRangeYAxis(int index, float lowerBound, float upperBound)
	{
		this.dynamicGraph.setRangeYAxis(index, lowerBound, upperBound);
	}
	
	//Define the data input type for different simulations 
	private float dataConversion(int index,int indexOfCompound)
	{
		
		float res = p5Canvas.getDataTickY(index,indexOfCompound);
		return res;
	}
	

	
	public void setPaintLineEnable(boolean flag)
	{
		paintLineEnabled = flag;
		
	}
	public boolean getPaintLineEnable()
	{
		return paintLineEnabled;
	}
	
	//Get molecules number from simulation before painting
	public void updateMoleculeCount(){
		
		int unit = p5Canvas.getUnit();
		int sim = p5Canvas.getSim();
		int set = p5Canvas.getSet();
		
		
		
		//For particular cases
		if(unit==1)
		{
			if (sim==4||(sim==2&&set==2)){
				int H2OIndex = names.indexOf("Water");
				int OIndex = names.indexOf("Oxygen");
				int H2O2Index = names.indexOf("Hydrogen-Peroxide");
				int H2OCount =0;
				int OCount =0;
				int H2O2Count =0;
				for (int i=0; i<State.molecules.size();i++){
					Molecule m = (Molecule) State.molecules.get(i);
					if (m.getName().equals("Water")){
						H2OCount++;
					}
					else if (m.getName().equals("Oxygen")){
						OCount++;
					}
					else if (m.getName().equals("Hydrogen-Peroxide")){
						H2O2Count++;
					}
				}
				Compound.counts.set(H2OIndex,H2OCount);
				Compound.counts.set(OIndex, OCount);
				Compound.counts.set(H2O2Index, H2O2Count);
				
			}
		}
		else if(unit==2)
		{
			main.getP5Canvas().getUnit2().updateMoleculeCount(sim, set);
		}
		
		
	}


	//GetLabelString
	//Input: index - index of graph
	public ArrayList<String []> getLabelStrings()
	{
		return labelStrings;
	}
	public double getDataTickX(int index)
	{
		double xValue=0;
//		switch(index)
//		{
//		case 0:
//			xValue = (double)main.time;
//			break;
//		case 1: //ph graph
//			xValue= (double)main.time;
//			break;
//		}
		xValue = p5Canvas.getDataTickX(index);
		return xValue;
	}
	
	public Color getColorLine(int index)
	{
		return colorLine[index];
	}
	public int getColorLineNum()
	{
		return colorLine.length;
	}
	
	public ArrayList<Double> getDataTickY(int index)
	{
		//0 is x Value, 1 is Y value 
		ArrayList<Double > dataTick= new ArrayList<Double>();
		int unit = p5Canvas.getUnit();
		int sim = p5Canvas.getSim();
		
		float ratio = 0.01f; //The ratio which will apply to overlap value
		double yMax = dynamicGraph.getRangeYAxis(indexGraph);
		
		switch(index)
		{
		case 0:
				int size = tableView.getItemNum(); 
				
				for (int i=0; i<size;i++){
					double value = dataConversion(index, i);	
					
					//Make a little change to the value which is exactly the same as existing ones
					if(dataTick.contains(value))
						value+= yMax*ratio;
					dataTick.add(value);
				}
				break;
		case 1:

			if(unit==8 && sim==6)
			{
				//Show Time - PH graph
				double value = dataConversion(index,0);
				dataTick.add(value);
			}
			break;
		}
		return dataTick;
		
	}
	
	//Get the name of series for dynamic graph
	public ArrayList<String> getDataNames(int index)
	{
		ArrayList<String> nameList = null;
		switch(index)
		{
		case 0:
			nameList = tableView.getCompoundNames();
			if(p5Canvas.isSimSelected(5, 2))
				nameList.remove("Water");
			break;
		case 1:
			nameList = new ArrayList<String>();
			nameList.add("PH");
			break;
		}
		return nameList;
	}
	
	public void setYAxisLabeInset(int index,float inset)
	{
		dynamicGraph.setYAxisLabeInset(index, inset);
	}
	
	public void setYAxisTickLabeInset(int index,float inset)
	{
		dynamicGraph.setYAxisTickLabeInset(index, inset);
	}
	
	
	public int getCurrentIndex()
	{
		return indexGraph;
	}

	
	public Main getMain(){
		return this.main;
	}

}

