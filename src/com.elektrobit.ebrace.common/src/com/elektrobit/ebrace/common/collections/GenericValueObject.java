/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class GenericValueObject
{
    private final Map<String, Object> propertyValues = new HashMap<String, Object>();

    public Object setProperty(final String propertyName, final Object propertyValue)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "propertyName", propertyName );
        RangeCheckUtils.assertReferenceParameterNotNull( "propertyValue", propertyValue );

        return propertyValues.put( propertyName, propertyValue );
    }

    public Object getProperty(final String propertyName)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "propertyName", propertyName );
        return propertyValues.get( propertyName );
    }

    public Iterator<String> propertyNameIterator()
    {
        return propertyValues.keySet().iterator();
    }

    public Iterator<Object> propertyValueIterator()
    {
        return propertyValues.values().iterator();
    }
}
