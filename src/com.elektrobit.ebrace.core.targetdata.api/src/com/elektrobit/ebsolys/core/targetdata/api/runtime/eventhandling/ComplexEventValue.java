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

public class ComplexEventValue
{
    private final String summary;
    private final Object complexValue;

    public ComplexEventValue(final String summary, Object complexValue)
    {
        this.summary = summary;
        this.complexValue = complexValue;
    }

    public String getSummary()
    {
        return summary;
    }

    public Object getComplexValue()
    {
        return complexValue;
    }

    @Override
    public String toString()
    {
        return "ComplexEventValue [summary=" + summary + ", complexValue=" + complexValue + "]";
    }
}
