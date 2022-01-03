package ch.get.util;

import java.util.Random;

public class RandomUtil {
	
	private static Random random = new Random(); 
	
	public static int getRandomValue(int min, int max) {
		int n = random.nextInt((max - min) + 1) % max;
		return min + n;
	}
}