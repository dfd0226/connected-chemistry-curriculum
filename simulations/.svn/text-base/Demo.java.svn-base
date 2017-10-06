package view;

import p5.Area;
import processing.core.*;

import view.*;

public class Demo extends Area {
	
	private int w, h;
	private int bgColor = color(127, 0, 0);

	// this is the P5 setup()
	public void setup(){
		w = 200;
		h = 200;
		size(w, h);
		smooth();
	}

	// this is the P5 draw()
	public void draw(){
		stroke(255, 0, 0);
		fill(bgColor);
		rect(0, 0, w, h);
		fill(200, 100, 100);
		rect(25, 25, 100, 100);


		line(0, 0, mouseX, mouseY);
		line(0, height, mouseX, mouseY);
		line(width, height, mouseX, mouseY);
		line(width, 0, mouseX, mouseY);
	}
	
	public void setBgColor(int input) {
		input = color(input, input, input);
		bgColor = input;
	};
}
