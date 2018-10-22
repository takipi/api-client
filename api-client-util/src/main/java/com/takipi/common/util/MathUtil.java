package com.takipi.common.util;

public class MathUtil {
	public static long sum(long[] arr) {
		if ((arr == null) || (arr.length == 0)) {
			return 0L;
		}

		long result = 0L;

		for (long l : arr) {
			result += l;
		}

		return result;
	}

	public static double sum(double[] arr) {
		if ((arr == null) || (arr.length == 0)) {
			return 0.0;
		}

		double result = 0.0;

		for (double d : arr) {
			result += d;
		}

		return result;
	}

	public static double avg(long[] arr) {
		if ((arr == null) || (arr.length == 0)) {
			return 0.0;
		}

		double result = 0.0;

		for (long l : arr) {
			result += l;
		}

		return result / arr.length;
	}

	public static double avg(double[] arr) {
		if ((arr == null) || (arr.length == 0)) {
			return 0.0;
		}

		double result = 0.0;

		for (double d : arr) {
			result += d;
		}

		return result / arr.length;
	}

	public static double stdDev(long arr[]) {
		if ((arr == null) || (arr.length == 0)) {
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
		if ((arr == null) || (arr.length == 0)) {
			return 0.0;
		}

		double result = 0.0;
		double avg = avg(arr);

		for (double d : arr) {
			result += Math.pow(d - avg, 2);
		}

		return Math.sqrt(result / arr.length);
	}
}
