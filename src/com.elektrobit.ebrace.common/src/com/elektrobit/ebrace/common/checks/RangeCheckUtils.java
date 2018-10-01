/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.checks;

import java.util.List;

public class RangeCheckUtils
{
    public static void assertReferenceParameterNotNull(final String paramName, final Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException( "parameter '" + paramName + "' " + "must not be null!" );
        }
    }

    public static void assertStringParameterNotNullOrEmpty(final String paramName, final String value)
    {
        if (value == null || value.length() == 0)
        {
            throw new IllegalArgumentException( "parameter '" + paramName + "' " + "must not be empty or null!" );
        }
    }

    public static void assertSizeOfListIsExactly(final String paramName, List<?> list, int expectedListSize)
    {
        if (list.size() != expectedListSize)
        {
            throw new IllegalArgumentException( "Length of list " + paramName + " must be " + expectedListSize + "." );
        }
    }

    public static void assertSizeOfArrayIsExactly(final String paramName, Object[] array, int expectedArraySize)
    {
        if (array.length != expectedArraySize)
        {
            throw new IllegalArgumentException( "Length of array " + paramName + " must be " + expectedArraySize
                    + "." );
        }
    }

    public static void assertListIsNotEmpty(final String paramName, List<?> list)
    {
        if (list.isEmpty())
        {
            throw new IllegalArgumentException( "List " + paramName + " must not be empty." );
        }
    }

    public static void assertTypeOfParameterIsInstanceOf(final String paramName, final Object param, Class<?> clazz)
    {
        if (!clazz.isInstance( param ))
        {
            throw new IllegalArgumentException( "Parameter " + paramName + " must be of " + clazz + ", but it is of "
                    + param.getClass() + "." );
        }
    }

    public static void assertCorrectIntervalBoundaries(String intervalName, long intervalStart, long intervalEnd)
    {
        if (intervalStart > intervalEnd)
        {
            throw new IllegalArgumentException( "Interval " + intervalName + " not consistent. Start value "
                    + intervalStart + " end value " + intervalEnd );
        }
    }

    public static void assertGreaterOrEqual(String name, long value, long compareValue)
    {
        if (compareValue <= value)
        {
            throw new IllegalArgumentException( name + "(" + value + ") not greater or equal to " + compareValue
                    + "." );
        }
    }

    public static void assertIntegerInInterval(String name, int value, int intervalStart, int intervalEnd)
    {
        assertCorrectIntervalBoundaries( name, intervalStart, intervalEnd );
        if (value < intervalStart || value > intervalEnd)
        {
            throw new IllegalArgumentException( name + "(" + value + ") is not in interval " + intervalStart + " to "
                    + intervalEnd + "." );
        }
    }

}
