package Util;

import java.awt.Color;
import main.Main;

//Class ColorCollection holds global color settings
public class ColorCollection {
	
	private static Color colorMenu = new Color(200, 200, 150);
	private static Color colorBackground = Color.LIGHT_GRAY;
	
	/******* Colors ********/
	public static Color colorSimBackground = new Color(146,146,146);
	public static Color colorGraphBackground = new Color(146,146,146);
	public static Color colorSimBorder = Color.WHITE;
	public static Color colorSelectionRect = Color.WHITE;  //The select rect color in when molecule masking enabled
	public static Color colorSimBoundary = Color.WHITE;
	public static Color colorTableViewBackground = new Color(245,245,245);
	//Electrons
	public static Color colorElectronBk = Color.black;
	public static Color colorElectronBorder = Color.black;

	public static Color [] colorGraphLine;
	
	public static void setupColorGlobal()
	{
		int sat =222;
		colorGraphLine = new Color[Main.MAX_COMPOUND_NUM];	
		colorGraphLine[0]= new Color(255,0,0,sat);
		colorGraphLine[1]= new Color(0,255,0,sat);
		colorGraphLine[2]= new Color(0,0,255,sat);
		colorGraphLine[3]= new Color(255,255,0,sat);
		colorGraphLine[4]= new Color(0,255,255,sat);
		colorGraphLine[5]= new Color(255,0 ,255,sat);
		colorGraphLine[6]= Color.PINK;
		colorGraphLine[7]= Color.ORANGE;
		for (int i = 8;i<Main.MAX_COMPOUND_NUM;i++){
			colorGraphLine[i] = Color.BLACK;
		}
	}
	
	static public Color [] getColorGraphLine()
	{
		return colorGraphLine;
	}
	
	static public Color getColorMainBackground()
	{
		return colorBackground;
	}
	
	static public Color getColorSimBackground()
	{
		return colorSimBackground;
	}
	static public int getColorSimBackgroundInt()
	{
		return colorSimBackground.getRGB();
	}
	
	static public Color getColorMenu()
	{
		return colorMenu;
	}
	static public int getColorSimBorderInt()
	{
		return colorSimBorder.getRGB();
	}
	
	static public int getColorSelectionRectInt()
	{
		return colorSelectionRect.getRGB();
	}
	
	static public int getColorSimBoundaryInt()
	{
		return colorSimBoundary.getRGB();
	}
	
	static public Color getColorGraphBackground()
	{
		return colorGraphBackground;
	}
	static public Color getColorTableViewBackground()
	{
		return colorTableViewBackground;
	}
	
	static public int getColorElectronBkInt()
	{
		return colorElectronBk.getRGB();
	}
	
	static public int getColorElectronBorderInt()
	{
		return colorElectronBorder.getRGB();
	}

}
