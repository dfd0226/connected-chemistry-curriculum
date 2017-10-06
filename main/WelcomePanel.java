package main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.File;
import java.io.IOException;
import java.awt.font.GlyphVector;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import data.DBinterface;


public class WelcomePanel extends JPanel {

	private String bkImagePath = "/resources/png64x64/simSplashScreen.png";  //Put the default background image path here
	private Image bkImage;
	
	
	
	public WelcomePanel()
	{
		super();
		ImageIcon imageIcon = new ImageIcon(Main.class.getResource(bkImagePath));
		bkImage = imageIcon.getImage();
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    int width = getWidth();
	    int height = getHeight();
	    int x = (width - bkImage.getWidth(null)) / 2;
	    int y = (height - bkImage.getHeight(null)) / 2;
	    g2d.drawImage(bkImage, x, y, null);
	    float ratio = 9f/10;
        drawCenteredString( g2d, DBinterface.versionString, (float)width*ratio, (float)height*ratio);

	}
	
    private void drawCenteredString( final Graphics2D g2d, final String text, final float f, final float g )
    {
//    	Font font = g2d.getFont();
//    	font.deriveFont(64);
    	Font font = new Font("Lucida Grande",
				Font.PLAIN, 18);
    	final FontRenderContext fr = g2d.getFontRenderContext();
    	GlyphVector glyphVector = font.createGlyphVector(fr, text);
        g2d.drawGlyphVector(glyphVector, f, g);
    }

}
