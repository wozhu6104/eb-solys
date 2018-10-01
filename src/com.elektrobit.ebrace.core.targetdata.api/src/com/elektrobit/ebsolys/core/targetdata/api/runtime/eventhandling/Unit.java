/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

public interface Unit<T>
{
    public static Unit<Double> PERCENT = new UnitImpl<Double>( "Percent", Double.class );
    public static Unit<Long> KILOBYTE = new UnitImpl<Long>( "Kilobyte", Long.class );
    public static Unit<Long> COUNT = new UnitImpl<Long>( "Count", Long.class );
    public static Unit<String> TEXT = new UnitImpl<String>( "Text", String.class );
    public static Unit<Boolean> BOOLEAN = new UnitImpl<Boolean>( "Boolean", Boolean.class );
    public static Unit<STimeSegment> TIMESEGMENT = new UnitImpl<STimeSegment>( "TimeSegment", STimeSegment.class );

    public static <E> Unit<E> createCustomUnit(String name, Class<E> dataType)
    {
        return new UnitImpl<E>( name, dataType );
    }

    public String getName();

    /**
     * Gets the data type of this runtime channel unit. A runtime channel can handle RuntimeEvents of matching types
     * only!
     * 
     * @return
     */
    public Class<T> getDataType();
}
