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

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;

public abstract class Force
{
    protected Force()
    {
    }

    protected abstract double getForceForDistance(double inDistance);

    public Vector calcForceVectorBetweenNodes(final LayoutNode nodeA, final LayoutNode nodeB)
    {
        final Vector distanceVector = getDistanceVectorToNode( nodeA, nodeB );
        final Vector centerDistanceVector = nodeA.getVectorToNode( nodeB );

        return getForceForVector( distanceVector, centerDistanceVector );
    }

    private Vector getForceForVector(final Vector inVector, Vector inCenterDistanceVector)
    {
        double distance = inVector.length();
        double force = getForceForDistance( distance );

        inCenterDistanceVector.makeUnit();
        inCenterDistanceVector.scaleBy( force );
        return inCenterDistanceVector;
    }

    private final Vector getDistanceVectorToNode(final LayoutNode nodeA, final LayoutNode nodeB)
    {
        final Vector vectorToB = nodeA.getVectorToNode( nodeB );
        final Vector vectorFromB = nodeB.getVectorToNode( nodeA );
        final int kInsetX = 0;
        final int kInsetY = 0;
        final Rectangle rectA = nodeA.getRectangle().getInsetRect( -kInsetX, -kInsetY );
        final Rectangle rectB = nodeB.getRectangle().getInsetRect( -kInsetX, -kInsetY );

        // final DisplayIndependentPoint p1 = rectA.calculateEllipseCutWithVectorFromCenter(vectorToB);
        // final DisplayIndependentPoint p2 = rectB.calculateEllipseCutWithVectorFromCenter(vectorFromB);

        final DisplayIndependentPoint p1 = rectA.calculateCutOfRectangleWithVectorFromCenter( vectorToB );
        final DisplayIndependentPoint p2 = rectB.calculateCutOfRectangleWithVectorFromCenter( vectorFromB );

        final boolean collision = rectA.collidesWith( rectB );

        return (!collision) ? new Vector( p1, p2 ) : new Vector( 0, 0 );
    }

} // class Force
