package minecraft.core.zocker.pro.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomGenerator {

	private final Map<Object, Double> distribution;
	private double distributionSum;

	public RandomGenerator() {
		this.distribution = new HashMap<>();
	}

	public void addObject(Object value, double distribution) {
		if (this.distribution.get(value) != null) {
			distributionSum -= this.distribution.get(value);
		}
		this.distribution.put(value, distribution);
		distributionSum += distribution;
	}

	public Object getNextObject() {
		double rand = Math.random();
		double ratio = 1.0f / distributionSum;
		double tempDist = 0;
		for (Object i : distribution.keySet()) {
			tempDist += distribution.get(i);
			if (rand / ratio <= tempDist) {
				return i;
			}
		}

		return null;
	}

	public static boolean getRandomByChance(double chance) {
		return new Random().nextDouble() < chance;
	}
}
