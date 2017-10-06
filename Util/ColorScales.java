package Util;

import java.awt.*;

public class ColorScales {
    private static final int[] categoricalColors;
    static {
        categoricalColors = new int[100];
        int delta = Integer.MAX_VALUE / 50;
        int step = Integer.MIN_VALUE;
        for (int i = 0; i < 100; i++) {
            categoricalColors[i] = step;
            step += delta;
        }
    }
    public static final String GRAY = "gray", RAINBOW = "rainbow", ALTITUDE = "altitude",
            BLUEGREEN = "bluegreen", CIRCULAR = "circular",
            BIPOLAR = "bipolar", REDBLUE = "redblue",
            REDGREEN = "redgreen", HEAT = "heat",  LIGHT = "light",
            RED = "red", BLUE = "blue", GREEN = "green", CATEGORICAL = "categorical";

    private ColorScales() {}

    public static Color getColor(double value, String method, float opacity) {
        if (Double.isNaN(value))
            return Color.BLACK;
        else if (method.equals(CATEGORICAL))
            return categorical(value);
        else if (method.equals(RAINBOW))
            return rainbow(value, opacity);
        else if (method.equals(ALTITUDE))
            return altitude(value, opacity);
        else if (method.equals(BLUEGREEN))
            return bluegreen(value, opacity);
        else if (method.equals(REDBLUE))
            return redblue(value, opacity);
        else if (method.equals(BIPOLAR))
            return bipolar(value, opacity);
        else if (method.equals(REDGREEN))
            return redgreen(value, opacity);
        else if (method.equals(CIRCULAR))
            return circular(value, opacity);
        else if (method.equals(HEAT))
            return heat(value, opacity);
        else if (method.equals(LIGHT))
            return light(value, opacity);
        else if (method.equals(GRAY))
            return gray(value, opacity);
        else if (method.equals(RED))
            return red(value, opacity);
        else if (method.equals(BLUE))
            return blue(value, opacity);
        else if (method.equals(GREEN))
            return green(value, opacity);
        else if (method.equals("red2"))
            return red2(value, opacity);
        else if (method.equals("blue2"))
            return blue2(value, opacity);
        else if (method.equals("green2"))
            return green2(value, opacity);
        else if (method.equals("pink2"))
            return pink2(value, opacity);
        else if (method.equals("yellow2"))
            return yellow3(value, opacity);
        else if (method.equals("gray2"))
            return gray2(value, opacity);
        else
            return Color.BLACK;
    }

    private static Color categorical(double value) {
        int v = (int) (value * categoricalColors.length) % categoricalColors.length;
        return new Color(categoricalColors[v]);
    }

    private static Color rainbow(double value, float transparency) {
        /* blue to red, approximately by wavelength */
        float v = (float) value * 255.f;
        float vmin = 0;
        float vmax = 255;
        float range = vmax - vmin;

        if (v < vmin + 0.25f * range)
            return new Color(0.f, 4.f * (v - vmin) / range, 1.f, transparency);
        else if (v < vmin + 0.5 * range)
            return new Color(0.f, 1.f, 1.f + 4.f * (vmin + 0.25f * range - v) / range, transparency);
        else if (v < vmin + 0.75 * range)
            return new Color(4.f * (v - vmin - 0.5f * range) / range, 1.f, 0, transparency);
        else
            return new Color(1.f, 1.f + 4.f * (vmin + 0.75f * range - v) / range, 0, transparency);
    }

    private static Color altitude(double value, float opacity) {
        /* green to yellow to red */
        float v = (float) value;
        float cut = .5f;

        if (v < cut)
            return new Color(v / cut, 1.f, 0.f, opacity);
        else
            return new Color(1.f, (1.f - v) / cut, 0.f, opacity);
    }

    private static Color bluegreen(double value, float opacity) {
        /* blue to green through white */
        float v = (float) value;
        float cut = .5f;

        if (v < cut)
            return new Color(v / cut, v / cut, 1.f, opacity);
        else
            return new Color((1.f - v) / cut, 1.f, (1.f - v) / cut, opacity);
    }

    private static Color circular(double value, float opacity) {
        /* blue to blue, around color wheel */
        float v = (float) value;
        float sq = (float) Math.sqrt(2.);
        float length = 2.f + 2.f * sq;
        float cut1 = sq / length;
        float cut2 = (sq + 1.f) / length;
        float cut3 = (sq + 2.f) / length;

        if (v < cut1)
            return new Color(0.f, v / cut1, 1.f - v / cut1, opacity);
        else if (v < cut2)
            return new Color((v - cut1) / (cut2 - cut1), 1.f, 0.f, opacity);
        else if (v < cut3)
            return new Color(1.f, 1.f - (v - cut2) / (cut3 - cut2), 0.f, opacity);
        else
            return new Color(1.f - (v - cut3) / (1.f - cut3), 0.f, (v - cut3) / (1.f - cut3), opacity);
    }

    private static Color gray(double value, float opacity) {
        /* light to dark */
        float v = (float) (1.0 - value);
        return new Color(v, v, v, opacity);
    }

    private static Color red(double value, float opacity) {
        float v = (float) value;
        return new Color(1.f, 1.f - v, 1.f - v, opacity);
    }

    private static Color blue(double value, float opacity) {
        float v = (float) value;
        return new Color(1.f - v, 1.f - v, 1.f, opacity);
    }

    private static Color green(double value, float opacity) {
        float v = (float) value;
        return new Color(1.f - v, 1.f, 1.f - v, opacity);
    }
    
    private static Color red2(double value, float opacity) {
        float v = (float) value;
        v=(float) Math.pow(v, 1.5);
        if (v<0.5){
        	float v2 =v*2.f;
        	return new Color(1.f, 1.f - v2, 1.f - v2, opacity);
        }    
        else{
        	float v2= (float) (1-v)*1.2f +0.4f;
        	return new Color(v2, 0, 0, opacity);
        }
    }

    private static Color blue2(double value, float opacity) {
        float v = (float) value;
        v=(float) Math.pow(v, 1.5);
        if (v<0.5){
        	float v2 =v*2.f;
        	return new Color(1.f-v2, 1.f - v2, 1.f, opacity);
        }    
        else{
        	float v2= (float) (1-v)*1.2f+0.4f;
        	return new Color(0, 0, v2, opacity);
        }
    }

    private static Color green2(double value, float opacity) {
        float v = (float) value;
        v=(float) Math.pow(v, 1.5);
        if (v<0.5){
        	float v2 =v*2.f;
        	return new Color(1.f-v2, 1.f, 1.f - v2, opacity);
        }    
        else{
        	float v2= (float) (1-v)*1.2f+0.4f;
        	return new Color(0, v2, 0 , opacity);
        }
    }
    
    private static Color pink2(double value, float opacity) {
    	 float v = (float) value;
         v=(float) Math.pow(v, 1.5);
         if (v<0.5){
         	float v2 =v*2.f;
         	return new Color(1.f, 1.f-v2, 1.f, opacity);
         }    
         else{
         	float v2= (float) (1-v)*1.2f+0.4f;
         	return new Color(v2, 0,v2 , opacity);
         }   
    }
    private static Color yellow3(double value, float opacity) {
   	 float v = (float) value;
        v=(float) Math.pow(v, 1.5);
        if (v<0.5){
        	//float v2 =v+0.5f;
        	return new Color(1.f, 1.f, 0.5f-v, opacity);
        }    
        else{
        	float v2= (float) (1-v)*1.6f+0.2f;
        	return new Color(v2, v2, 0 , opacity);
        }   
   }
    private static Color gray2(double value, float opacity) {
   	 float v = (float) value;
        //v=(float) Math.pow(v, 1.5);
        v = (1-v)*0.8f +0.1f;
        return new Color(v, v,v , opacity);
   }

    private static Color bipolar(double value, float opacity) {
        /* Used in Wall St. Smart Money Map */
        float v = (float) value;
        if (v < .5)
            return new Color(0.f, 1.f - v * 2.f, 0.f, opacity);
        else
            return new Color((v - .5f) * 2.f, 0.f, 0.f, opacity);
    }

    private static Color redblue(double value, float opacity) {
        float v = (float) value;
        Color color;
        float saturation;
        if (v < .5) {
            color = new Color(1.f, 0.f, 0.f);
            saturation = 1.f - 2.f * v;
        } else {
            color = new Color(0.f, 0.f, 1.f);
            saturation = 2.f * (v - .5f);
        }
        float[] rgb = color.getColorComponents(null);
        float[] hsb = Color.RGBtoHSB((int) (255 * rgb[0]), (int) (255 * rgb[1]), (int) (255 * rgb[2]), null);
        int irgb = Color.HSBtoRGB(hsb[0], saturation, 1.f);
        Color c = new Color(irgb);
        rgb = c.getColorComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }

    private static Color redgreen(double value, float opacity) {
        float v = (float) value;
        Color color;
        float saturation;
        if (v < .5) {
            color = new Color(1.f, 0.f, 0.f);
            saturation = 1.f - 2.f * v;
        } else {
            color = new Color(0.f, 1.f, 0.f);
            saturation = 2.f * (v - .5f);
        }
        float[] rgb = color.getColorComponents(null);
        float[] hsb = Color.RGBtoHSB((int) (255 * rgb[0]), (int) (255 * rgb[1]), (int) (255 * rgb[2]), null);
        int irgb = Color.HSBtoRGB(hsb[0], saturation, 1.f);
        Color c = new Color(irgb);
        rgb = c.getColorComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }

    private static Color heat(double value, float opacity) {
        /* Kelvin color temperature */
        double t = (1.0 - value) * 9000. + 500.;
        double[][] w = {
                {3.24071, -0.969258, 0.0556352},
                {-1.53726, 1.87599, -0.203996},
                {-0.498571, 0.0415557, 1.05707}};
        double xf, yf;
        if (t <= 4000)
            xf = 0.27475e9 / (t * t * t) - 0.98598e6 / (t * t) + 1.17444e3 / t + 0.145986;
        else if (t <= 7000)
            xf = -4.6070e9 / (t * t * t) + 2.9678e6 / (t * t) + 0.09911e3 / t + 0.244063;
        else
            xf = -2.0064e9 / (t * t * t) + 1.9018e6 / (t * t) + 0.24748e3 / t + 0.237040;
        yf = -3 * xf * xf + 2.87 * xf - 0.275;

        double x = xf / yf;
        double y = 1.0;
        double z = (1.0 - xf - yf) / yf;
        double max = 0.;
        double[] rgb = new double[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = (float) (x * w[0][i] + y * w[1][i] + z * w[2][i]);
            if (rgb[i] > max)
                max = rgb[i];
        }
        rgb[0] = Math.min(Math.max(.1, rgb[0] / max), .9);
        rgb[1] = Math.min(Math.max(.1, rgb[1] / max), .9);
        rgb[2] = Math.min(Math.max(.1, rgb[2] / max), .9);
        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], opacity);
    }

    private static Color light(double value, float opacity) {
        /* black to yellow through red */
        float v = (float) value;
        float cut1 = .3333f;
        float cut2 = .6666f;

        if (v < cut1)
            return new Color(0.f, 0.f, v / cut1, opacity);
        else if (v < cut2)
            return new Color((v - cut1) / (cut2 - cut1), 0.f, 1.f - (v - cut1) / (cut2 - cut1), opacity);
        else
            return new Color(1.f, (v - cut2) / (1.f - cut2), 0.f, opacity);
    }
}
