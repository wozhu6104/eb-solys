/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.kpimeasuring.api;

public class KPIResultMeasuredItem
{
    private final String name;
    private final String value;
    private final String unit;

    public KPIResultMeasuredItem(String name, String value, String unit)
    {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getUnit()
    {
        return unit;
    }

    @Override
    public String toString()
    {
        return "KPIResultMeasuredItem [name=" + name + ", value=" + value + ", unit=" + unit + "]";
    }

}
