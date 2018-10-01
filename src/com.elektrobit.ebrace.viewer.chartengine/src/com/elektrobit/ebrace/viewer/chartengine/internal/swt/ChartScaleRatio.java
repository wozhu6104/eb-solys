/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt;

public class ChartScaleRatio
{
    private static double ratio = 1.0;

    public static double get()
    {
        return ratio;
    }

    public static void set(double ratio)
    {
        ChartScaleRatio.ratio = ratio;
    }
}
