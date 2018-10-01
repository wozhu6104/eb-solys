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

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

class Range
{
    private final double origin;
    private final double size;

    public Range(double origin, double size)
    {
        this.origin = origin;
        this.size = size;
    }

    public Range unionRange(final Range otherRange)
    {
        final double xmin = Math.max( origin, otherRange.origin );
        final double xmax = Math.min( origin + size, otherRange.origin + otherRange.size );

        if (xmax >= xmin)
        {
            return new Range( xmin, xmax - xmin );
        }
        else
        {
            return new Range( 0, 0 );
        }
    }

    public boolean isEmpty()
    {
        return (0 == size);
    }

    public boolean collide(final Range otherRange)
    {
        return !unionRange( otherRange ).isEmpty();
    }

    public Range boundRange(Range otherRange)
    {
        final double xmin = Math.min( origin, otherRange.origin );
        final double xmax = Math.max( origin + size, otherRange.origin + otherRange.size );

        return new Range( xmin, xmax - xmin );
    }
}

public class Rectangle extends DisplayIndependentRectangle
{
    public Rectangle(double x, double y, double width, double height)
    {
        super( x, y, width, height );
    }

    public Rectangle(final DisplayIndependentRectangle rect)
    {
        super( rect );
    }

    public final DisplayIndependentDimension getSize()
    {
        return new DisplayIndependentDimension( width, height );
    }

    public void setSize(final DisplayIndependentDimension size)
    {
        width = size.width;
        height = size.height;
    }

    public Rectangle getInsetRect(double dx, double dy)
    {
        return new Rectangle( x + dx, y + dy, width - 2 * dx, height - 2 * dy );
    }

    public Rectangle getInsetRect(final Vector insetVect)
    {
        return getInsetRect( insetVect.getX(), insetVect.getY() );
    }

    boolean isHitByPoint(final DisplayIndependentPoint point)
    {
        return (point.x >= x) && (point.x < x + width) && (point.y >= y) && (point.y < y + height);
    }

    public DisplayIndependentPoint getCenterPointOfRectangle()
    {
        return new DisplayIndependentPoint( this.x + this.width * 0.5, this.y + this.height * 0.5 );
    }

    public boolean collidesWith(final Rectangle rectangle)
    {
        final Range thisRangeX = new Range( x, width );
        final Range otherRangeX = new Range( rectangle.x, rectangle.width );

        if (thisRangeX.collide( otherRangeX ))
        {
            final Range thisRangeY = new Range( y, height );
            final Range otherRangeY = new Range( rectangle.y, rectangle.height );

            if (thisRangeY.collide( otherRangeY ))
            {
                return true;
            }
        }
        return false;
    }

    public DisplayIndependentPoint calculateCutOfRectangleWithVectorFromCenter(final Vector inVect)
    {
        final DisplayIndependentPoint thisCenterPos = getCenterPointOfRectangle();

        // detect which edge of the rectangle will be cut.

        // if the x-coord is zero then this means that the pitch is infinite.
        if (inVect.getX() == 0)
        {
            return new DisplayIndependentPoint( thisCenterPos.x, (inVect.getY() < 0) ? this.y : this.y + this.height );
        }

        if (inVect.getX() > 0)
        {
            DisplayIndependentPoint pos = calculateCutOfRectangleWithVectorFromCenter( new Vector( -inVect.getX(),
                                                                                                   inVect.getY() ) );
            final double dx = pos.x - this.x;
            pos.x = this.x + this.width - dx;
            return pos;
        }

        if (inVect.getY() > 0)
        {
            DisplayIndependentPoint pos = calculateCutOfRectangleWithVectorFromCenter( new Vector( inVect.getX(),
                                                                                                   -inVect.getY() ) );
            final double dy = pos.y - this.y;
            pos.y = this.y + this.height - dy;
            return pos;
        }

        // determine which edge is hit - the left or the bottom

        final double mVect = inVect.getY() / inVect.getX();
        final double mRect = this.height / this.width;

        if ((mVect / mRect) > 1.0)
        {
            // the bottom line is hit

            // dx = dy / mVect
            final double dy = this.y - thisCenterPos.y;
            final double dx = dy / mVect;
            return new DisplayIndependentPoint( thisCenterPos.x + dx, this.y );
        }
        else
        {
            // the left line is hit

            // dy = dx * mVect
            final double dx = this.x - thisCenterPos.x;
            final double dy = dx * mVect;
            return new DisplayIndependentPoint( this.x, thisCenterPos.y + dy );
        }
    }

    final double kEdgeScale = Math.sqrt( 0.5 );

    public DisplayIndependentPoint calculateEllipseCutWithVectorFromCenter(final Vector inVect)
    {
        final DisplayIndependentPoint thisCenterPos = this.getCenterPointOfRectangle();

        final double aspectRatio = width / height;

        Vector normalizedVector = new Vector( inVect.getX() / aspectRatio, inVect.getY() );

        normalizedVector.makeUnit();
        normalizedVector.scaleBy( kEdgeScale );

        final double denormX = normalizedVector.getX() * width;
        final double denormY = normalizedVector.getY() * height;

        return new DisplayIndependentPoint( thisCenterPos.x + denormX, thisCenterPos.y + denormY );
    }
}
