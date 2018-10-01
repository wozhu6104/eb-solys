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

public class AttractForce extends Force
{
    private double m_NewtonPerPixel;

    public AttractForce(double inNewtonPerPixel)
    {
        m_NewtonPerPixel = inNewtonPerPixel / 200.0;
    }

    /**
     * Returns the force in Newton for a given distance in Pixel.
     */
    protected double getForceForDistance(double inDistance)
    {
        return inDistance * m_NewtonPerPixel;
    }
}
