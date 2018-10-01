package api

import java.util.List

class MathUtil {
	
	/**
	 * Calculates the average value of all values within the given list
	 * This function will only apply for values of type Double
	 * @param data The list of Double
	 * @return The average value of all Double values
	 */
	def static averageDouble(List<Double> data) {
		data.reduce[p1, p2|p1 + p2] / data.size
	}

	/**
	 * Calculates the average value of all values within the given list
	 * This function will only apply for values of type Integer
	 * @param data The list of Integer
	 * @return The average value of all Integer values
	 */
	def static averageInteger(List<Integer> data) {
		data.reduce[p1, p2|p1 + p2] / data.size
	}

	/**
	 * Calculates the minimum value of all values within the given list
	 * This function will only apply for values of type Double
	 * @param data The list of Double
	 * @return The minimum value of all Double values
	 */
	def static minDouble(List<Double> data) {
		data.sort.head
	}

	/**
	 * Calculates the minimum value of all values within the given list
	 * This function will only apply for values of type Integer
	 * @param data The list of Integer
	 * @return The minimum value of all Integer values
	 */
	def static minInteger(List<Integer> data) {
		data.sort.head
	}

	/**
	 * Calculates the maximum value of all values within the given list
	 * This function will only apply for values of type Double
	 * @param data The list of Double
	 * @return The maximum value of all Double values
	 */
	def static maxDouble(List<Double> data) {
		data.sort.last
	}

	/**
	 * Calculates the maximum value of all values within the given list
	 * This function will only apply for values of type Integer
	 * @param data The list of Integer
	 * @return The maximum value of all Integer values
	 */
	def static maxInteger(List<Integer> data) {
		data.sort.last
	}

	/**
	 * Calculates the variance value of all data of a given list
	 * This function will only apply for values of type Double
	 * @param data The list of Double
	 * @return The variance value of all data
	 */	
	def static variance(List<Double> data)
	{
		val m = data.averageDouble
		data.fold(0.0)[a, b|(m-b)*(m-b) + a]/data.size
	}
	
	/**
	 * Calculates the standard deviation of all data of a given list
	 * This function will only apply for events with values of type Double
	 * @param data The list of data
	 * @return The standard deviation value of all data
	 */	
	def static standardDev(List<Double> data)
	{
	    Math.sqrt(data.variance);
	}

}