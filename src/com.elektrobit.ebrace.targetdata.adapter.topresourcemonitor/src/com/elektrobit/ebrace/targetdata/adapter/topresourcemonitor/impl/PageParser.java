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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PageParser
{
    private static final String PROC_HEADER_PATTERN_SDK_23 = "^(?=.*PID)(?=.*CPU)(?=.*RSS)(?=.*Name).*$";
    private static final String PROC_HEADER_PATTERN_SDK_26 = "^(?=.*PID)(?=.*NAME)(?=.*CPU)(?=.*RSS).*$";
    private static final String TOTAL_CPU_CONSUMPTION_PATTERN_SDK_23 = "^(?=.*User)(?=.*System)(?=.*IOW)(?=.*IRQ).*$";
    private static final String TOTAL_CPU_CONSUMPTION_PATTERN_SDK_26 = "^(?=.*cpu)(?=.*user)(?=.*nice)(?=.*sys).*$";
    private String procHeaderPattern;
    private String totalCpuConsumptionPattern;
    private final List<ProcResourceConsumptionParser> processInfoList;
    private double totalCpuConsumption;
    private long totalMemoryConsumption;
    private boolean parsingSuccess;
    private int sdkVersion;

    public PageParser()
    {
        processInfoList = new ArrayList<ProcResourceConsumptionParser>();
        setParsingSuccess( false );

    }

    public void OnNewContentReceived(byte[] content, int sdkVersion)
    {
        resetParsingState();
        configureSdkVersion( sdkVersion );
        parsePageContent( content );
    }

    private void resetParsingState()
    {
        setTotalMemoryConsumption( 0 );
        setTotalCpuConsumption( 0 );
        setParsingSuccess( false );
        processInfoList.clear();
    }

    private void configureSdkVersion(int sdkVersion)
    {
        this.sdkVersion = sdkVersion;

        if (sdkVersion == 23)
        {
            procHeaderPattern = PROC_HEADER_PATTERN_SDK_23;
            totalCpuConsumptionPattern = TOTAL_CPU_CONSUMPTION_PATTERN_SDK_23;
        }
        else if (sdkVersion == 26)
        {
            procHeaderPattern = PROC_HEADER_PATTERN_SDK_26;
            totalCpuConsumptionPattern = TOTAL_CPU_CONSUMPTION_PATTERN_SDK_26;
        }

    }

    private void parsePageContent(byte[] content)
    {
        boolean result;

        Scanner scanner = new Scanner( new String( content ) );

        result = calculateTotalCpuConsumption( scanner );
        if (result)
        {
            result = calculateProcRessourceConsumption( scanner );
        }
        if (result)
        {
            calculateTotalMemoryConsumption();
        }

        setParsingSuccess( result );

        scanner.close();
    }

    private boolean calculateTotalCpuConsumption(Scanner scanner)
    {
        boolean result = false;

        if (sdkVersion == 23)
        {
            TotalCpuConsumptionParserSDK23 totalCpuConsumptionParser = new TotalCpuConsumptionParserSDK23();
            result = calculateTotalCpuConsumptionSDK23( scanner, totalCpuConsumptionParser );
        }
        else if (sdkVersion == 26)
        {
            TotalCpuConsumptionParserSDK26 totalCpuConsumptionParser = new TotalCpuConsumptionParserSDK26();
            result = calculateTotalCpuConsumptionSDK26( scanner, totalCpuConsumptionParser );
        }
        else
        {
            result = false;
        }

        return result;
    }

    private boolean calculateTotalCpuConsumptionSDK23(Scanner scanner,
            TotalCpuConsumptionParserSDK23 totalCpuConsumptionParser)
    {
        boolean result = false;

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.matches( totalCpuConsumptionPattern ))
            {
                if (totalCpuConsumptionParser.parse( line ))
                {
                    totalCpuConsumption = totalCpuConsumptionParser.getTotalCpuConsumption();
                    result = true;
                }
                else
                {
                    result = false;
                }

                break;
            }
        }
        return result;

    }

    private boolean calculateTotalCpuConsumptionSDK26(Scanner scanner,
            TotalCpuConsumptionParserSDK26 totalCpuConsumptionParser)
    {
        boolean result = false;

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.matches( totalCpuConsumptionPattern ))
            {

                if (totalCpuConsumptionParser.parse( line ))
                {
                    totalCpuConsumption = totalCpuConsumptionParser.getTotalCpuConsumption();
                    result = true;
                }
                else
                {
                    result = false;
                }

                break;
            }
        }
        return result;
    }

    private boolean calculateProcRessourceConsumption(Scanner scanner)
    {
        boolean result;

        if (seekToProcessInfoLines( scanner ))
        {
            parseProcInfoLines( scanner );
            result = true;
        }
        else
        {
            System.out.println( "Warning : No processing information data found on this page" );
            result = false;
        }

        return result;
    }

    private boolean seekToProcessInfoLines(Scanner scanner)
    {
        boolean result = false;

        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.matches( procHeaderPattern ))
            {
                result = true;
                break;
            }
        }

        return result;
    }

    private void parseProcInfoLines(Scanner scanner)
    {
        while (scanner.hasNextLine())
        {
            String[] splitedLine = scanner.nextLine().trim().split( "\\s+" );
            storeLineContentToProcessInfoList( splitedLine );
        }
    }

    private void storeLineContentToProcessInfoList(String[] splitedLine)
    {
        ProcResourceConsumptionParser procResourceConsumptionParser = new ProcResourceConsumptionParser( sdkVersion );

        if (procResourceConsumptionParser.parseLine( splitedLine ))
        {
            processInfoList.add( procResourceConsumptionParser );
        }
    }

    private void calculateTotalMemoryConsumption()
    {
        for (ProcResourceConsumptionParser procResourceConsumptionParser : processInfoList)
        {
            totalMemoryConsumption += procResourceConsumptionParser.getMemoryUsage();
        }
    }

    public boolean isParsingSuccess()
    {
        return parsingSuccess;
    }

    public void setParsingSuccess(boolean parsingSuccess)
    {
        this.parsingSuccess = parsingSuccess;
    }

    public List<ProcResourceConsumptionParser> getProcessInfoList()
    {
        return processInfoList;
    }

    public double getTotalCpuConsumption()
    {
        return totalCpuConsumption;
    }

    public void setTotalCpuConsumption(double totalCpuConsumption)
    {
        this.totalCpuConsumption = totalCpuConsumption;
    }

    public long getTotalMemoryConsumption()
    {
        return totalMemoryConsumption;
    }

    public void setTotalMemoryConsumption(long totalMemoryConsumption)
    {
        this.totalMemoryConsumption = totalMemoryConsumption;
    }
}
