package com.moofMonkey;

import java.util.Random;

public class NameGen extends Thread {
	public static String namegen = "abcdefghijklmnopqrstuvwxyz" + "0123456789";
	static int count = 15;
	static String name = "";

	public static String get() {
		try {
			int i = count;
			Random rand = new Random();
			while (true) {
				if (i == 0)
					break;
				name += namegen.charAt(rand.nextInt(namegen.length()));
				i--;
				if (i % 5 == 0)
					System.gc();
			}
			return name;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return name;
	}
}
