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

public class TotalCpuConsumptionParserSDK26
{
    private static final int CPU_POWER_POS = 0;
    private static final int CPU_IDLE_POS = 4;

    private double totalCpuPower;
    private double cpuIdleConsumption;
    private double cpuCoreFactor;

    public TotalCpuConsumptionParserSDK26()
    {
    }

    boolean parse(String headerLine)
    {
        boolean result = false;

        resetPreviousParsing();

        headerLine = headerLine.trim();
        headerLine = headerLine.replaceAll( "[^0-9. ]", "" );
        String[] headerLineSplitted = headerLine.split( "\\s+" );

        try
        {
            totalCpuPower = Integer.valueOf( headerLineSplitted[CPU_POWER_POS] );
            cpuIdleConsumption = Integer.valueOf( headerLineSplitted[CPU_IDLE_POS] );
            cpuCoreFactor = totalCpuPower / 100;
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
        totalCpuPower = 0;
        cpuIdleConsumption = 0;
        cpuCoreFactor = 0;
    }

    double getTotalCpuConsumption()
    {
        return (totalCpuPower - cpuIdleConsumption) / cpuCoreFactor;
    }

    double getCpuCoreFactor()
    {
        return cpuCoreFactor;
    }
}
