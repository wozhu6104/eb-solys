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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KPIResult
{
    private final Map<String, String> metaDataItems = new HashMap<String, String>();
    private final List<String> errorMessages = new ArrayList<String>();
    private final List<KPIResultMeasuredItem> measuredItems = new ArrayList<KPIResultMeasuredItem>();

    public void addMetaData(String key, String value)
    {
        metaDataItems.put( key, value );
    }

    public List<String> getMetaDataItems()
    {
        return new ArrayList<String>( metaDataItems.keySet() );
    }

    public void addErrorMessage(String errorMessage)
    {
        errorMessages.add( errorMessage );
    }

    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    public void addMeasuredItem(KPIResultMeasuredItem kpiResultMeasuredItem)
    {
        measuredItems.add( kpiResultMeasuredItem );
    }

    public List<KPIResultMeasuredItem> getMeasuredItems()
    {
        return measuredItems;
    }

    public String getMetaDataValue(String key)
    {
        return metaDataItems.get( key );
    }

}
