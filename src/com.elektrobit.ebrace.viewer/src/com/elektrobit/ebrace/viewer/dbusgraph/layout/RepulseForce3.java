/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph.layout;

public class RepulseForce3 extends Force
{
    private double m_NewtonPerCubicPixel;

    public RepulseForce3(double inNewtonPerCubicPixel)
    {
        m_NewtonPerCubicPixel = inNewtonPerCubicPixel;
    }

    private static double kMaxForce = 0.001;
    private static double kForceOffset = Math.pow( kMaxForce, 0.3333333 );

    /**
     * Returns the force in Newton for a given distance in Pixel.
     */
    protected double getForceForDistance(double inDistance)
    {
        if (m_NewtonPerCubicPixel == 0)
        {
            return 0;
        }

        // if (inDistance > 10000)
        // {
        // return 0;
        // }

        if (inDistance < 0)
            inDistance = 0;

        inDistance /= m_NewtonPerCubicPixel;

        final double tmpA = (inDistance + kForceOffset);

        return -(1 * kMaxForce) / (tmpA * tmpA * tmpA);
    }
}
