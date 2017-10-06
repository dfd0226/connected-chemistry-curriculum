package Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SVGReader {
	private static ArrayList<String> names;
	
	public static ArrayList<String> getNames(){
		return names;
	}
	public static String processNames(String name){

		if (name.contains("_"))
			name = name.split("_")[0];
		if (name.contains("-"))
			return name.split("-")[0];
		else
			return name;
		
	}
		
	public static float[][] getSVG(String s) {
		names = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br = null;
		String sText;
		float[][] circles = null; 
		try {
			is = SVGReader.class.getResourceAsStream("/"+s);
			br = new BufferedReader(new InputStreamReader(is));
			int lineCount = 0;
			ArrayList xList = new ArrayList();
			ArrayList yList = new ArrayList();
			ArrayList rList = new ArrayList();
			boolean reachCircle = false;
			sText = br.readLine();
			while (sText != null) {
				if (sText.contains("<g id=\"circles\">")) {
					reachCircle = true;
				}
				if (sText.contains("<circle id") && reachCircle) {
					String[] values = sText.split("\"");
					String elementName = processNames(values[1]);
					if(!elementName.contains("Dot"))
					{
					xList.add(values[5]);
					yList.add(values[7]);
					rList.add(values[9]);
					names.add(elementName);
					}
				}
				lineCount++;
				sText = br.readLine();
			}
			circles = new float[xList.size()][3];
			for (int i = 0; i < xList.size(); i++) {
				circles[i][0] = Float.parseFloat(rList.get(i).toString());
				circles[i][1] = Float.parseFloat(xList.get(i).toString());
				circles[i][2] = Float.parseFloat(yList.get(i).toString());
			}
			br.close();
			is.close();
			return circles;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return circles;
	}	
}
