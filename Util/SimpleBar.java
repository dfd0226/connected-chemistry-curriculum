package Util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import javax.swing.JPanel;

public class SimpleBar extends JPanel {
	private float min;
	private float max;
	private float value;
	private float defaultValue;
	private Color foreColor = new Color(16, 20, 132);
	private Color bgColor = new Color(16, 20, 132,128);
	private Color fontColor = new Color(255,255,255);
	private Color borderColor = new Color(max, max, max);
	
	
	public SimpleBar(float vMin, float vMax,float vValue)
	{
		super();
		min = vMin;
		max = vMax;
		value = vValue;	
		defaultValue = value;
	}
	public SimpleBar(float vMin, float vMax)
	{
		this(vMin,vMax,vMin);
	}

	 public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		//Graphics does not support anti-aliasing, so we transform it into graphics2d
		Graphics2D g2D =(Graphics2D) graphics;
	    RenderingHints  qualityHints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);   
	    qualityHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);   
	    g2D.setRenderingHints(qualityHints);   
		Dimension dim = getPreferredSize();
		int panelWidth = dim.width;
		int panelHeight = dim.height;
		int x = getInsets().left;
		int y = getInsets().top;
		int cornerRadius = panelWidth/6;
		
		Font valueFont = new Font("Helvetica", Font.PLAIN, 10);

		int barHeight = (int) (((value-min)/(max-min))*panelHeight);
		//Draw background
		g2D.setColor(bgColor);
		drawCustomBar(g2D,x,y,panelWidth,panelHeight,cornerRadius);
		
		//Draw bar
		g2D.setColor(foreColor);
		drawCustomBar(g2D,x, y+panelHeight-barHeight, panelWidth, barHeight,cornerRadius);
		g2D.setColor(fontColor);
		g2D.setFont(valueFont);
		FontMetrics fm = g2D.getFontMetrics();

		DecimalFormat formatter = new DecimalFormat("0.00");
		String txtValue = formatter.format(value);
		int stringWidth = fm.stringWidth(txtValue);
		float txtX = x+ (panelWidth-stringWidth)/2;
		float txtY = y+panelHeight-barHeight+fm.getHeight();
		int descent = fm.getDescent();
		//System.out.println("Descent is "+descent);
		if( txtY> y+panelHeight)
			txtY = y+panelHeight-descent;
		g2D.drawString(txtValue, txtX,txtY);
		
		//TODO: Draw border
	 }
	 
	 public void drawCustomBar(Graphics2D graphics,int x,int y,int w,int h,int radius)
	 {
		 graphics.fillRect(x, y+radius, w, h-radius);
		 graphics.fillRect(x+radius, y, w-radius*2, radius);
		 graphics.fillArc(x, y, radius*2, radius*2,90,90);//Top left coner
		 graphics.fillArc(x+w-radius*2, y, radius*2, radius*2,0,90); //Top right corner
	 }
	 
	 public void setValue(float v)
	 {
		 if( v > max)
		 value = max;
		 else if ( v<min)
			 value = min;
		 else
			 value = v;
	 }
	 
	 public void setMax(float v)
	 {
		 if(v>min)
			 max = v;
	 }
	 public void reset()
	 {
		 value = defaultValue;
		 this.updateUI();
	 }

}
