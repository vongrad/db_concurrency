package db_concurrency;

import java.util.Random;

/**
 *
 * @author adamv
 */
public class Toolkit {
	public static long getSleepTime(long min, long max) {
		Random rn = new Random();
		long range = max - min + 1;
		return rn.nextInt((int) range) + min;
	}
}
