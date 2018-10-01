/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.util;

public class TimestampPositionInChartConverter
{
    public static final double INVALID_VALUE = -1;

    /**
     * Calculates the time stamp for a given click in the plot area.
     * 
     * @param min
     *            the left x-value of the area wich contains the element.
     * @param plotWidth
     *            the width of the area.
     * @param clicked
     *            the position of the mouse.
     * @param timespanStartValue
     *            the start time stamp in milisecs.
     * @param the
     *            completeTimspan in milisecs.
     * @return the calculated timestamp.
     */
    public static double calculateTimestampForClick(double min, double plotWidth, int clicked, long timespanStartValue,
            long timespanLength)
    {
        if (clicked < min)
        {
            clicked = (int)min;
        }
        if (clicked > min + plotWidth)
        {
            clicked = (int)Math.round( plotWidth );
        }

        double translatedToPlotClick = clicked - min;
        double diff = translatedToPlotClick / plotWidth;
        return timespanStartValue + (timespanLength * diff);
    }

    /**
     * Calculates the time xPosition within the chart plot for a given timestamp.
     * 
     * @param lowerEdgeCoordinate
     *            (offset) the lowest coordinate the area (corresponds to fullStartTime time).
     * @param viewSize
     *            the width or the height of the area.
     * @param timestamp
     *            the given time stamp.
     * @param fullStartTime
     *            the start time stamp in milisecs.
     * @param timespanLength
     *            analysis time span.
     * @return the calculated timestamp.
     */
    public static double calculatePositionForTimestamp(double lowerEdgeCoordinate, double viewSize, long timestamp,
            long fullStartTime, long timespanLength)
    {
        double result = INVALID_VALUE;
        if ((timestamp >= fullStartTime) && (timestamp <= fullStartTime + timespanLength))
        {
            double translatedToTimestamp = timestamp - fullStartTime;
            double diff = translatedToTimestamp / timespanLength;
            result = lowerEdgeCoordinate + (viewSize * diff);
        }
        return result;
    }
}
