package minecraft.core.zocker.pro.util;

import java.text.DecimalFormat;
import java.util.*;

public class Util {

	public static int biggerValue(int value, int value2) {
		if (value > value2) return value;
		return value2;
	}

	public static int getRandomNumberBetween(int min, int max) {
		if (min == 0 && max == 0) return 0;

		if (!(min < 0 || max < 0)) {
			return (int) (Math.random() * ((max - min) + 1)) + min;

		} else {
			if (min >= max) {
				throw new IllegalArgumentException("Max must be greater than min");
			}
		}

		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;
	}

	public static double getRandomNumberBetween(double min, double max) {
		System.out.println("min : " + min + " max : " + max);
		if (min == 0 && max == 0) return 0;

		if (!(min < 0 || max < 0)) {
			return (Math.random() * ((max - min) + 1)) + min;

		} else {
			if (min >= max) {
				throw new IllegalArgumentException("Max must be greater than min");
			}
		}

		return 0;
	}

	public static String getBetweenStrings(String text, String textFrom, String textTo) {
		String returnValue;

		returnValue = text.substring(text.indexOf(textFrom) + textFrom.length());
		returnValue = returnValue.substring(0, returnValue.indexOf(textTo));

		return returnValue;
	}

	public static boolean isInteger(String number) {
		if (number == null) return false;
		return number.matches("[0-9]+") && number.length() < 10;
	}

	public static double roundDouble(double in) {
		return ((int) ((in * 100f) + 0.5f)) / 100f;
	}

	/**
	 * Format double to two decimals
	 *
	 * @param value value
	 * @return formatted value
	 */
	public static double formatDouble(double value) {
		if (value == 0) return 0;

		DecimalFormat df2 = new DecimalFormat(".##");
		String valueFormatted = df2.format(value);
		valueFormatted = valueFormatted.replace(",", ".");

		return Double.valueOf(valueFormatted);
	}
}
