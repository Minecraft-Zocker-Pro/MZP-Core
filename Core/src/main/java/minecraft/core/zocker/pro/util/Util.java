package minecraft.core.zocker.pro.util;

import java.text.DecimalFormat;
import java.util.Random;

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

	public static boolean convertStringToBoolean(String input) {
		if (input == null) return false;

		if (input.length() == 1) {
			if (input.equalsIgnoreCase("1")) return true;
			if (input.equalsIgnoreCase("0")) return false;
		}

		if (input.equalsIgnoreCase("true")) return true;
		if (input.equalsIgnoreCase("false")) return false;

		return false;
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

	/**
	 * Get a converted string
	 *
	 * @param integer the integer you want to convert
	 * @return converted integer {@value "123456" -> "123k"}
	 */
	public static String formatInt(int integer) {
		String n = Integer.toString(integer);
		int l = n.length();
		if (l < 4) return n;

		if (l < 7) {
			return decimal(n, 3) + "k";
		}

		if (l < 10) {
			return decimal(n, 6) + "M";
		}

		if (l < 13) {
			return decimal(n, 9) + "B";
		}

		if (l < 16) {
			return decimal(n, 12) + "T";
		}

		if (l < 19) {
			return decimal(n, 15) + "Q";
		}

		return n;
	}

	private static String decimal(String s, int i) {
		int b = s.length() - i;
		return s.substring(0, b) + "." + s.substring(b, b + 2);
	}

	public static String formatSeconds(long initialSeconds) {
		long years = initialSeconds / 31536000;
		long months = initialSeconds % 31557600 / 2629800;
		long weeks = initialSeconds % 2419200 / 604800;
		long days = initialSeconds % 604800 / 86400;
		long hours = initialSeconds % 86400 / 3600;
		long minutes = initialSeconds % 3600 / 60;
		long seconds = initialSeconds % 60;

		if (minutes < 1) {
			return String.format("%ds", seconds);
		}

		if (hours < 1) {
			return String.format("%dm %ds", minutes, seconds);
		}

		if (days < 1) {
			return String.format("%dh %dm %ds", hours, minutes, seconds);
		}

		if (weeks < 1) {
			return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
		}

		if (months < 1) {
			return String.format("%dw %dd %dh %dm %ds", weeks, days, hours, minutes, seconds);
		}

		if (years < 1) {
			return String.format("%dmo %dw %dd %dh %dm %ds", months, weeks, days, hours, minutes, seconds);
		}

		return String.format("%dy %dmo %dw %dd %dh %dm %ds", years, months, weeks, days, hours, minutes, seconds);
	}
}
