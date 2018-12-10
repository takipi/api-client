package com.takipi.common.util;

public class MathUtil {
	public static long sum(long[] arr) {
		if (!validArr(arr)) {
			return 0L;
		}

		long result = 0L;

		for (long l : arr) {
			result += l;
		}

		return result;
	}

	public static double sum(double[] arr) {
		if (!validArr(arr)) {
			return 0.0;
		}

		double result = 0.0;

		for (double d : arr) {
			result += d;
		}

		return result;
	}

	public static double avg(long[] arr) {
		if (!validArr(arr)) {
			return 0.0;
		}

		double result = 0.0;

		for (long l : arr) {
			result += l;
		}

		return result / arr.length;
	}

	public static double avg(double[] arr) {
		if (!validArr(arr)) {
			return 0.0;
		}

		double result = 0.0;

		for (double d : arr) {
			result += d;
		}

		return result / arr.length;
	}

	public static double weightedAvg(double[] arr, double[] weights) {
		if (!validWeights(arr, weights)) {
			return 0.0;
		}

		double result = 0.0;

		for (int i = 0; i < arr.length; i++) {
			result += arr[i] * weights[i];
		}

		return result / sum(weights);
	}

	public static double stdDev(long arr[]) {
		if (!validArr(arr)) {
			return 0.0;
		}

		double result = 0.0;
		double avg = avg(arr);

		for (long l : arr) {
			result += Math.pow(l - avg, 2);
		}

		return Math.sqrt(result / arr.length);
	}

	public static double stdDev(double arr[]) {
		if (!validArr(arr)) {
			return 0.0;
		}

		double result = 0.0;
		double avg = avg(arr);

		for (double d : arr) {
			result += Math.pow(d - avg, 2);
		}

		return Math.sqrt(result / arr.length);
	}

	public static double wightedStdDev(double[] arr, double[] weights) {
		if (!validWeights(arr, weights)) {
			return 0.0;
		}

		int nonZeroWeights = 0;
		double result = 0.0;
		double avg = weightedAvg(arr, weights);

		for (int i = 0; i < arr.length; i++) {
			if (weights[i] != 0) {
				nonZeroWeights++;
			}

			result += Math.pow(arr[i] - avg, 2) * weights[i];
		}

		double denominator = ((nonZeroWeights - 1) / (nonZeroWeights)) * sum(weights);

		return Math.sqrt(result / denominator);
	}

	private static boolean validArr(long[] arr) {
		return ((arr != null) && (arr.length > 0));
	}

	private static boolean validArr(double[] arr) {
		return ((arr != null) && (arr.length > 0));
	}

	public static boolean validWeights(double[] arr, double[] weights) {
		return ((validArr(arr)) && (validArr(weights)) && (arr.length == weights.length) && (sum(weights) != 0));
	}
}
