package net.omniblock.discord.manager.utils;

public class AlphaStringUtils {

	public static int countLines(String str){
		String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}
	
}
