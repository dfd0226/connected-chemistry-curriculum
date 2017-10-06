/* -------------------- * MemoryUsageDemo.java * -------------------- * (C) Copyright 2002-2006, by Object Refinery Limited. */
package main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import Util.ColorCollection;

/**
 * * A demo application showing a dynamically updated chart that displays the *
 * current JVM memory usage. *
 * <p>
 * * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE.
 * * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class DynamicGraph extends JPanel {
	/** Time series for total memory used. */
	
	
	private int count = 0 ;
	
	private Canvas canvas;
	private TableView tableView ;
	NumberAxisAd [] domains;
	NumberAxisAd [] ranges;
	XYItemRenderer renderer;
	XYPlot plot;
	XYSeriesCollection dataset[];
	private ArrayList<XYSeries> [] dataList;
	private final int MAX_DATASET_NUM = 5;
	private int dataSetSize = 1 ; //The index number of this graph in canvas
	private int intialRangeX = 30;
	ArrayList<String []> labelString;
	
	


	/**
	 * * Creates a new application. * * @param maxAge the maximum age (in
	 * milliseconds).
	 */
	
	public DynamicGraph(Canvas p,int size) {
		
		super(new BorderLayout());

		canvas = (Canvas) p ;
		dataSetSize = size;
		tableView = canvas.getMain().getTableView();
		createGraph();
	}
	
	public DynamicGraph(Canvas p)
	{
		this(p,1);
	}
	
	public void createGraph()
	{
		
		initializeDataSet();

		initializeAxes();
		
		initializeRenderer();


		plot = new XYPlot(dataset[0], domains[0], ranges[0], renderer);
		plot.setBackgroundPaint(ColorCollection.getColorSimBackground());
		plot.setOutlineVisible(true);
		//plot.setOutlineStroke(new BasicStroke(1));
		plot.setOutlinePaint(Color.black);
		//plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.white);
		
	
		//Create chart
		JFreeChart chart = new JFreeChart("", new Font(
				"SansSerif", Font.BOLD, 24), plot, false);
		//Set Width for range labels
		AxisSpace spaceYLabel = new AxisSpace();
		spaceYLabel.setLeft(25.0);
		chart.getXYPlot().setFixedRangeAxisSpace(spaceYLabel);
		//Set Height for domain labels
		AxisSpace spaceXLabel = new AxisSpace();
		spaceXLabel.setBottom(25.0);
		chart.getXYPlot().setFixedDomainAxisSpace(spaceXLabel);
		
		//Create chart panel
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(300,200));
		chartPanel.setDisplayToolTips(true);
		chartPanel.setInitialDelay(0);
		
		//Disable zoom
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		
		//Disable right click menu
		chartPanel.setPopupMenu(null);
		
		add(chartPanel);
	}
	
	//Reset function
	public void reset(int size)
	{
		dataSetSize = size;
		labelString = canvas.getLabelStrings();
		resetAxes(labelString);
		resetDataset();
		
		for( int i = 0 ;i<size;i++)
		plot.mapDatasetToRangeAxis(i, i);
	}
	
	public void setRangeYAxis(int index, float lowerBound, float upperBound)
	{
		if(index<dataSetSize)
			ranges[index].setRange(lowerBound,upperBound);
		
	}
	
	public double getRangeYAxis(int index)
	{
		return ranges[index].getRange().getUpperBound();
		
	}
	
	public void showPlot(int index)
	{
		if(index>=dataSetSize)
			return;
		
		//plot.set
		//Show dataset
//		plot.setNotify(false);
	
//			for(int i = 0 ;i<dataSetSize;i++)
//			{
//				ranges[i].setAutoRange(false);
//			}
		
		plot.setDataset(dataset[index]);
		//Show axes
		plot.setDomainAxis(domains[index]);
		plot.setRangeAxis(ranges[index]);
//		plot.setNotify(true);

		
		
//			for(int i = 0 ;i<dataSetSize;i++)
//			{
//				ranges[i].setAutoRange(true);
//			}
		
		
	}
	
	
	private void resetDataset()
	{
		//Clean previous data
		for(int i=0;i<MAX_DATASET_NUM;i++)
		{
			dataList[i].clear();
			dataset[i].removeAllSeries();
		}
		if(dataSetSize<=MAX_DATASET_NUM)
		{
			for(int index= 0;index<dataSetSize;index++)
			{
				//Get column names from canvas
				ArrayList<String> compoundNames = canvas.getDataNames(index);
				
				for(String name:compoundNames)
				{
					XYSeries data = new XYSeries(name);
					dataList[index].add(data);
					dataset[index].addSeries(data);
				}
			}
		}
	}
	

	private void initializeDataSet()
	{
		// create data series 
		dataList = (ArrayList<XYSeries> [])new ArrayList[MAX_DATASET_NUM];
		dataset = new XYSeriesCollection[MAX_DATASET_NUM];
		//Initialize data structure

			for(int i=0;i<MAX_DATASET_NUM;i++)
			{
				dataList[i] = new ArrayList<XYSeries>();
				dataset[i] = new XYSeriesCollection();
			}
			
	}
	
	//Initialize axes structure
	private void initializeAxes()
	{
		domains = new NumberAxisAd[MAX_DATASET_NUM];
		ranges = new NumberAxisAd[MAX_DATASET_NUM];
		
		for( int i = 0 ;i<MAX_DATASET_NUM;i++)
		{
			domains[i] = new NumberAxisAd(AxisType.domain);
			ranges[i] = new NumberAxisAd(AxisType.range);

		}
	}
	
	//Initialize the render that control line style
	public void initializeRenderer()
	{
		renderer = new XYLineAndShapeRenderer(true, false);
//		  StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
//		            "{0}: {2}, {1}s", NumberFormat.getInstance(), NumberFormat.getInstance());
		  
		toolTipGenerator ttg = new toolTipGenerator();
		for(int i = 0 ;i< canvas.getColorLineNum();i++)
		{
			renderer.setSeriesPaint(i, canvas.getColorLine(i));
			renderer.setSeriesVisibleInLegend(i, false);
			renderer.setSeriesToolTipGenerator(i, ttg);

		}

		renderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
//		renderer.set(new toolTipGenerator());

		
	}
	
	private void resetAxes(ArrayList<String []> labelString)
	{
//		for(int k=0;k<MAX_DATASET_NUM;k++)
//		{
//			domains[k].setLabel("");
//			ranges[k].setLabel("");
//		}
		//Create NumberAxisAd with all label strings 
		for(int i = 0 ;i<labelString.size();i++)
		{
			
			String [] labelStr = labelString.get(i);
			domains[i] = new NumberAxisAd(labelStr[0],AxisType.domain);
			ranges[i] = new NumberAxisAd(labelStr[1],AxisType.range);
		}

		
	}

	
	/**
	 * * Adds an observation to the Õtotal memoryÕ time series. * * @param y the
	 * total memory used.
	 */
	public void addDataObservation() {
		
		if(!canvas.getPaintLineEnable())
			return;
		
		for(int index =0;index<dataSetSize;index++)
		{
			double x = canvas.getDataTickX(index);
			ArrayList<Double> yValues = canvas.getDataTickY(index);
			for(int i = 0 ;i<dataList[index].size();i++)
			{
				dataList[index].get(i).add(x,yValues.get(i));
			}
		}
	}
	
	public void setYAxisLabeInset(int index,float inset)
	{
		if(index<dataSetSize)
		ranges[index].setLabelInsets(new RectangleInsets(0, 0, 0, inset)	);
	}
	
	public void setYAxisTickLabeInset(int index,float inset)
	{
		if(index<dataSetSize)
		ranges[index].setTickLabelInsets(new RectangleInsets(0, 0, 0, inset)	);
	}
	
	
	
	class toolTipGenerator implements XYToolTipGenerator
	{
		public String generateToolTip(XYDataset dataset, int series, int item)
		{
			String res;
			ArrayList<String> nameList = canvas.getDataNames(canvas.getCurrentIndex());
			String name= new String();
			if(nameList!=null) {
				if(series<nameList.size())
					name = nameList.get(series);
			}
			double value = dataset.getYValue(series,item);
			double range = DynamicGraph.this.getRangeYAxis(0);
			DecimalFormat df;
			if(range<100)
			df = new DecimalFormat("###.##");
			else if(range>=100&&range<1000)
				df = new DecimalFormat("###.#");
			else
				df = new DecimalFormat("####");

			String strYValue = df.format(value); 
			res = new String(name+":"+strYValue+" at "+dataset.getX(series, item)+" s");
			return res;
		}
	}

	public enum AxisType { domain,range}
	
	class NumberAxisAd extends NumberAxis{
		
		private AxisType axisType;
		private int tickCountX = 3;
		private int tickCountY = 5;
		private String strLabel ;
		
		public NumberAxisAd(String str,AxisType at)
		{
			
			super(str);
			axisType = at;
			strLabel = new String(str);
			initialize();
			
		}
		public NumberAxisAd(AxisType at)
		{
			super();
			axisType = at;
			initialize();
		}
		
		private void initialize()
		{
			if(axisType==AxisType.domain)
			{
				setRangeType(RangeType.POSITIVE);	
				setTickLabelFont(new Font("Garamond", Font.PLAIN, 10));			
				setLabelFont(new Font("Garamond", Font.PLAIN, 12));			
				setLabelInsets(new RectangleInsets(-3, 0, 0, 0)	);

				setTickLabelsVisible(true);
				setAutoRangeIncludesZero(true);
				setAutoRangeMinimumSize(intialRangeX, true);		
				setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				
			}
			else if(axisType ==AxisType.range)
			{
				setRangeType(RangeType.POSITIVE);
				setTickLabelFont(new Font("Garamond", Font.PLAIN, 10));
				setLabelFont(new Font("Garamond", Font.PLAIN, 12));
				
//				System.out.println("getTickLabelInsets() is "+getTickLabelInsets());
//				System.out.println("getFixedDimension() is "+getFixedDimension());
				setLabelInsets(new RectangleInsets(0, 0, 0, -12.5)	);
				setAutoRangeIncludesZero(true);
				setAutoRangeMinimumSize(0.5, true);
				setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			}
		}
		
		protected void autoAdjustRange(){
	        Plot plot = getPlot();
	                if (plot == null) {
	                    return;  // no plot, no data
	                }
	        
	                if (plot instanceof ValueAxisPlot) {
	                    ValueAxisPlot vap = (ValueAxisPlot) plot;
	        
	                    Range r = vap.getDataRange(this);
	                    if (r == null) {
	                        r = getDefaultAutoRange();
	                    }
	        
	                    double upper = r.getUpperBound();
	                    double lower = r.getLowerBound();
	                    if (this.getRangeType() == RangeType.POSITIVE) {
	                        lower = Math.max(0.0, lower);
	                        upper = Math.max(0.0, upper);
	                    }
	                    else if (this.getRangeType() == RangeType.NEGATIVE) {
	                        lower = Math.min(0.0, lower);
	                        upper = Math.min(0.0, upper);
	                    }
	        
	                    if (getAutoRangeIncludesZero()) {
	                        lower = Math.min(lower, 0.0);
	                        upper = Math.max(upper, 0.0);
	                    }
	                    double range = upper - lower;
	                    double minRange = getAutoRangeMinimumSize();
	                    if(range>=minRange)
	                    	this.setAutoRangeMinimumSize(2*minRange);                   
	                }
	                super.autoAdjustRange();
	                }
		
	      protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
	            List ticks = new ArrayList();
	            
	            float total = (float) this.getUpperBound();
	            float interval = total/tickCountX;
	    		DecimalFormat myFormatter = new DecimalFormat("###");
	            //you'll need to have corresponding date objects around
	            //or know how to match them up on the graph
	            for (int i = 0; i <= total; i+=interval) {
	                String label = myFormatter.format(i);
	                NumberTick tick = new NumberTick(i, label, TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0);
	                ticks.add(tick);                    
	            }
	            return ticks;
	        }
	      
	      protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
	            List ticks = new ArrayList();
	            
	            float total = (float) this.getUpperBound();
	    		DecimalFormat myFormatter = new DecimalFormat("###.##");
	    		if(total>=100&&total<1000)
	    			myFormatter= new DecimalFormat("###.#");
	    		else if(total>=1000)
	    			myFormatter= new DecimalFormat("####");
	            //you'll need to have corresponding date objects around
	            //or know how to match them up on the graph
	                String label = myFormatter.format(total);
	                NumberTick tick = new NumberTick(total, label, TextAnchor.TOP_RIGHT, TextAnchor.CENTER, 0.0);
	                ticks.add(tick);                    
	            return ticks;
	        }
	    
	}
	

}
