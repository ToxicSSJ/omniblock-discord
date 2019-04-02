package net.omniblock.discord.manager.utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class ColorUtils {

	public static Color getRandomColor() {
		return new Color((int)(Math.random() * 0x1000000));
	}
	
	public static List<Color> getBrightColors() {
		
		return Arrays.asList(new Color(255, 0, 0),
							 new Color(255, 106, 0),
							 new Color(255, 174, 0),
							 new Color(255, 250, 0),
							 new Color(174, 255, 0),
							 new Color(93, 255, 0),
							 new Color(33, 255, 0),
							 new Color(0, 255, 38),
							 new Color(0, 255, 119),
							 new Color(0, 255, 165),
							 new Color(0, 255, 242),
							 new Color(0, 174, 255),
							 new Color(0, 127, 255),
							 new Color(0, 72, 255),
							 new Color(0, 8, 255),
							 new Color(51, 0, 255),
							 new Color(110, 0, 255),
							 new Color(144, 0, 255),
							 new Color(182, 0, 255),
							 new Color(242, 0, 255),
							 new Color(255, 0, 191),
							 new Color(255, 0, 135),
							 new Color(255, 0, 67));
		
	}
	
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf(colorStr.substring(1, 3), 16),
	            Integer.valueOf(colorStr.substring(3, 5), 16),
	            Integer.valueOf(colorStr.substring(5, 7), 16));
	}
	
}
