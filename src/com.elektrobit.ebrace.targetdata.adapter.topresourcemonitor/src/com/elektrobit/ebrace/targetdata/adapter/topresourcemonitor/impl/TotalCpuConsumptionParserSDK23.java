/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.impl;

public class TotalCpuConsumptionParserSDK23
{
    private static final int TOTAL_USER_CPU_CONSUMPTION_POS = 1;
    private static final int TOTAL_SYSTEM_CPU_CONSUMPTION_POS = 3;
    private double totalSystemCpuConsumption;
    private double totalUserCpuConsumption;

    public TotalCpuConsumptionParserSDK23()
    {
    }

    boolean parse(String headerLine)
    {
        boolean result = false;

        resetPreviousParsing();

        headerLine = headerLine.trim();
        headerLine = headerLine.replaceAll( "%,", "" );

        String[] headerLineSplitted = headerLine.split( "\\s+" );

        try
        {
            totalSystemCpuConsumption = Integer.valueOf( headerLineSplitted[TOTAL_SYSTEM_CPU_CONSUMPTION_POS] );
            totalUserCpuConsumption = Integer.valueOf( headerLineSplitted[TOTAL_USER_CPU_CONSUMPTION_POS] );
            result = true;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private void resetPreviousParsing()
    {
        totalSystemCpuConsumption = 0;
        totalUserCpuConsumption = 0;
    }

    double getTotalCpuConsumption()
    {
        return totalSystemCpuConsumption + totalUserCpuConsumption;
    }
}
