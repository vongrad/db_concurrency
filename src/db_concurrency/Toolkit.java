/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import java.util.Random;

/**
 *
 * @author adamv
 */
public class Toolkit {
	public static int getSleepTime(int min, int max) {
		Random rn = new Random();
		int range = min - max + 1;
		return rn.nextInt(range) + min;
	}
}
