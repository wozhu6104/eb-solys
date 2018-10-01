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

import java.text.SimpleDateFormat;
import java.util.Date;

public class KPIResultBuilder
{
    private KPIResult kpiResult;
    private final SimpleDateFormat formatter;

    public KPIResultBuilder()
    {
        kpiResult = new KPIResult();
        formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    }

    public KPIResultBuilder addDate(Date date)
    {
        kpiResult.addMetaData( "date", formatter.format( date ) );
        return this;
    }

    public KPIResultBuilder addMetaData(String key, String value)
    {
        kpiResult.addMetaData( key, value );
        return this;
    }

    public KPIResult build()
    {
        return kpiResult;
    }

    public KPIResultBuilder addErrorMessage(String errorMessage)
    {
        kpiResult.addErrorMessage( errorMessage );
        return this;
    }

    public KPIResultBuilder addMeasuredItem(String name, String value, String unit)
    {
        kpiResult.addMeasuredItem( new KPIResultMeasuredItem( name, value, unit ) );
        return this;
    }

    public KPIResultBuilder reset()
    {
        kpiResult = new KPIResult();
        return this;
    }

}
