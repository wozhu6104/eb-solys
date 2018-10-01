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

/**
 * This class is supposed to store a two dimensional vector. It will be used by the custom Layout algorithm for Zest.
 */
class Vector
{
    DisplayIndependentPoint m_Point;

    /**
     * Constructs a clone of another vector.
     * 
     * @param vect
     *            The vector to copy from.
     */
    public Vector(final Vector vect)
    {
        m_Point = new DisplayIndependentPoint( vect.m_Point.x, vect.m_Point.y );
    }

    public Vector(final DisplayIndependentPoint point)
    {
        m_Point = new DisplayIndependentPoint( point.x, point.y );
    }

    public Vector(final DisplayIndependentPoint pointA, final DisplayIndependentPoint pointB)
    {
        m_Point = new DisplayIndependentPoint( pointB.x - pointA.x, pointB.y - pointA.y );
    }

    /**
     * Creates a new Vector by a given x and y component.
     * 
     * @param x
     *            The x component for the vector.
     * @param y
     *            The y component for the vector.
     */
    public Vector(double x, double y)
    {
        m_Point = new DisplayIndependentPoint( x, y );
    }

    void makeUnit()
    {
        final double length = length();
        if (length != 0)
        {
            m_Point.x /= length;
            m_Point.y /= length;
        }
    }

    /**
     * Gets the x position of this vector
     * 
     * @return The x position of this vector.
     * @see #getY()
     */
    public double getX()
    {
        return m_Point.x;
    }

    /**
     * Gets the y position of this vector
     * 
     * @return The y position of this vector.
     * @see #getX()
     */
    public double getY()
    {
        return m_Point.y;
    }

    /**
     * Sets the x position of this vector. The y position will not be touched.
     * 
     * @param x
     *            The new x component.
     * @see #setY(double)
     */
    public void setX(double x)
    {
        m_Point.x = x;
    }

    /**
     * Sets the y position of this vector. The x position will not be touched.
     * 
     * @param y
     *            The new y component.
     * @see #setX(double)
     */
    public void setY(double y)
    {
        m_Point.y = y;
    }

    /**
     * Adds another vector to this vector object.
     * <p>
     * This method actually performs the operation <tt>this.x += inVect.x</tt> and <tt>this.y += inVect.y</tt>.
     * </p>
     * 
     * <p>
     * <b>This operation works 'in place' for this vector.</b>
     * </p>
     * The other vector will not be changed.
     * 
     * @param inVect
     *            The vector to add to this vector.
     */
    public void addSelf(final Vector inVect)
    {
        m_Point.x += inVect.m_Point.x;
        m_Point.y += inVect.m_Point.y;
    }

    /**
     * Scales this vector by a scalar.
     * <p>
     * This method actually performs the operation <tt>this.x *= scale</tt> and <tt>this.y *= scale</tt>.
     * </p>
     * <p>
     * <b>This operation works 'in place' for this vector.</b>
     * </p>
     * 
     * @param inScale
     *            the scale which will be used to perform the vector scaling.
     * @see #scaleYBy(double)
     * @see #scaleXBy(double)
     */
    public void scaleBy(double inScale)
    {
        m_Point.x *= inScale;
        m_Point.y *= inScale;
    }

    /**
     * Scale just the X component of this vector by a scalar value.
     * <p>
     * This method actually performs the operation <tt>this.x *= scale</tt>.
     * </p>
     * <p>
     * <b>This operation works 'in place' for this vector.</b>
     * </p>
     * 
     * @param inScale
     *            The scale to multiply the X component by.
     * @see #scaleBy(double)
     * @see #scaleYBy(double)
     */
    public void scaleXBy(double inScale)
    {
        m_Point.x *= inScale;
    }

    /**
     * Scale just the Y component of this vector by a scalar value.
     * <p>
     * This method actually performs the operation <tt>this.y *= scale</tt>.
     * </p>
     * <p>
     * <b>This operation works 'in place' for this vector.</b>
     * </p>
     * 
     * @param inScale
     *            The scale to multiply the Y component by.
     * @see #scaleBy(double)
     * @see #scaleXBy(double)
     */
    public void scaleYBy(double inScale)
    {
        m_Point.y *= inScale;
    }

    /**
     * Get the length of this vector.
     * 
     * @return The length of this vector.
     * @see #lengthSqu()
     */
    public double length()
    {
        return java.lang.Math.sqrt( lengthSqu() );
    }

    /**
     * Get the square of the length of this vector.
     * 
     * @return The square of the length of this vector.
     * @see #length()
     */
    public double lengthSqu()
    {
        return m_Point.x * m_Point.x + m_Point.y * m_Point.y;
    }

    /**
     * Clone this vector object.
     * 
     * @return A new Vector object which is an exact clone of this.
     */
    @Override
    protected Vector clone()
    {
        return new Vector( m_Point.x, m_Point.y );
    }

    protected Vector cloneAsReverse()
    {
        return new Vector( -m_Point.x, -m_Point.y );
    }

    /**
     * Returns a textual representation of this object.
     * 
     * @return A string which describes the contents of this object.
     */
    @Override
    public String toString()
    {
        return "Vector(" + hashCode() + "){x=" + m_Point.x + ", y=" + m_Point.y + "}";
    }
}
