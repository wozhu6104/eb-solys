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

public class ProcResourceConsumptionParser
{

    private int pidPositionInLine;
    private int namePositionInLine;
    private int cpuUsagePositionInLine;
    private int memoryUsagePositionInLine;

    private int pid;
    private double cpuUsage;
    private long memoryUsage;
    private String name;

    public ProcResourceConsumptionParser(int sdkVersion)
    {
        if (sdkVersion == 26)
        {
            pidPositionInLine = 0;
            namePositionInLine = 1;
            cpuUsagePositionInLine = 2;
            memoryUsagePositionInLine = 3;
        }
        else if (sdkVersion == 23)
        {
            pidPositionInLine = 0;
            namePositionInLine = 9;
            cpuUsagePositionInLine = 2;
            memoryUsagePositionInLine = 6;
        }

    }

    boolean parseLine(String[] splitedLine)
    {
        boolean succes = false;

        succes = parsePID( splitedLine );
        if (succes)
        {
            succes = parseCPUsage( splitedLine );
        }
        if (succes)
        {
            succes = parseMemoryUsage( splitedLine );
        }
        if (succes)
        {
            succes = parseName( splitedLine );
        }

        return succes;
    }

    private boolean parsePID(String[] splitedLine)
    {
        boolean succes = false;
        String pidString = splitedLine[pidPositionInLine];

        try
        {
            pid = Integer.parseInt( pidString );
            succes = true;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return succes;
    }

    boolean parseCPUsage(String[] splitedLine)
    {
        boolean succes = false;
        String cpuUsageString = splitedLine[cpuUsagePositionInLine];

        try
        {
            cpuUsageString = cpuUsageString.replaceAll( "[^\\d.]", "" );
            cpuUsage = Double.parseDouble( cpuUsageString );
            succes = true;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return succes;
    }

    private boolean parseMemoryUsage(String[] splitedLine)
    {
        boolean succes = false;
        String memoryUsageString = splitedLine[memoryUsagePositionInLine];

        try
        {
            memoryUsageString = memoryUsageString.replaceAll( "[^\\d.]", "" );
            memoryUsage = Long.parseLong( memoryUsageString );
            succes = true;
        }
        catch (NumberFormatException e)
        {
            succes = false;
            e.printStackTrace();

        }

        return succes;
    }

    private boolean parseName(String[] splitedLine)
    {
        name = splitedLine[namePositionInLine];
        return true;
    }

    public int getPID()
    {
        return pid;
    }

    public void setPID(int pid)
    {
        this.pid = pid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String processName)
    {
        this.name = processName;
    }

    public long getMemoryUsage()
    {
        return memoryUsage;
    }

    public void setMemoryUsage(long memoryUsage)
    {
        this.memoryUsage = memoryUsage;
    }

    public double getCpuUsage()
    {
        return cpuUsage;
    }

    public void setCpuUsage(int cpuUsage)
    {
        this.cpuUsage = cpuUsage;
    }
}
