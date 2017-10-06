package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import processing.core.PApplet;

public class ResourceReader extends PApplet{
	private String url;

	BufferedReader reader;

	public ResourceReader(String url_) {
		URL resource = this.getClass().getResource("/" + url_);
		url = resource.toString();
		reader = createReader(url_);		
	}

	public String read() {
		String output = "";

		String lines[] = loadStrings(url);
		
		for (int i = 0; i<lines.length; i++) {
			output += lines[i] + "\n"; 
		}
		return output;
	}
}
