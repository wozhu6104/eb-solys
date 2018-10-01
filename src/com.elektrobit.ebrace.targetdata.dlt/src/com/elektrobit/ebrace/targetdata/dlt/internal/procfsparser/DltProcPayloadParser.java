/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltProcPayloadParser
{

    private final static String EXPECTED_APP_ID = "SYS";
    private final static String EXPECTED_SYS_CPU_CTX_ID = "CPU";
    private final static String START_OF_LIST = "1";
    private Integer numberOfCores = null;

    private Map<Integer, ProcMemEntry> procMemList = new HashMap<Integer, ProcMemEntry>();

    private Map<Integer, ProcCpuEntry> procCpuListPrevious = new HashMap<Integer, ProcCpuEntry>();
    private Map<Integer, ProcCpuEntry> procCpuListCurrent = new HashMap<Integer, ProcCpuEntry>();

    private static final Pattern PROC_PID_STAT = Pattern
            .compile( "(-?[0-9]+)\\s(?:\\()([^\\)]*)(?:\\))\\s([a-zA-Z])\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)" );
    private static final Pattern PROC_PID_STATM = Pattern
            .compile( "(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)\\s(-?[0-9]+)" );

    public Measurement<ProcCpuEntry> parseCpuData(DltMessage dltMsg)
    {
        Measurement<ProcCpuEntry> result = null;

        if (isStartOfList( dltMsg ))
        {
            result = populateCpuResult();
            swapCpuMaps();
        }
        ProcPidStat stat = parseStatFile( dltMsg );
        if (stat != null)
        {
            updateCpuEntry( stat, dltMsg.getStandardHeader().getTimeStamp() );
        }

        return result;
    }

    private boolean isStartOfList(DltMessage dltMsg)
    {
        return dltMsg.getPayload().get( 0 ).equals( START_OF_LIST );
    }

    private Measurement<ProcCpuEntry> populateCpuResult()
    {
        HashMap<Integer, ProcCpuEntry> procCpuList_tmp = new HashMap<Integer, ProcCpuEntry>();
        Long timestamp = new Long( 0 );

        if (procCpuListCurrent.size() > 0)
        {
            timestamp = new Long( procCpuListCurrent.entrySet().iterator().next().getValue().getTimestamp() );
        }

        for (Map.Entry<Integer, ProcCpuEntry> entry : procCpuListCurrent.entrySet())
        {

            ProcCpuEntry previousMeasurementValue = procCpuListPrevious.get( entry.getKey() );
            Integer previousMeasurementPid = entry.getKey();

            if (previousMeasurementValue != null && previousMeasurementPid != null)
            {
                long timestampDiff = (entry.getValue().getTimestamp() - previousMeasurementValue.getTimestamp());
                if (timestampDiff > 0)
                {
                    long cpuUsageDiff = (entry.getValue().getCpuUsage() - previousMeasurementValue.getCpuUsage());

                    procCpuList_tmp.put( previousMeasurementPid,
                                         new ProcCpuEntry( timestampDiff,
                                                           previousMeasurementValue.getProcName(),
                                                           cpuUsageDiff ) );
                }

            }

        }
        return new Measurement<ProcCpuEntry>( timestamp, procCpuList_tmp );
    }

    private void swapCpuMaps()
    {

        procCpuListPrevious = procCpuListCurrent;
        procCpuListCurrent = new HashMap<Integer, ProcCpuEntry>();
    }

    private ProcPidStat parseStatFile(DltMessage dltMsg)
    {

        Matcher matchResult = PROC_PID_STAT.matcher( dltMsg.getPayload().get( dltMsg.getPayload().size() - 1 ) );
        if (matchResult.find())
        {
            ProcPidStat entry = new ProcPidStat();

            entry.setFullName( matchResult.group( ProcPidStat.FULLNAME_FIELD_NR ) );
            entry.setPid( Integer.parseInt( matchResult.group( ProcPidStat.PID_FIELD_NR ) ) );
            entry.setUtime( Long.parseLong( matchResult.group( ProcPidStat.UTIME_FIELD_NR ) )
                    * ProcPidStat.MSEC_PER_CLOCK );
            entry.setStime( Long.parseLong( matchResult.group( ProcPidStat.STIME_FIELD_NR ) )
                    * ProcPidStat.MSEC_PER_CLOCK );
            entry.setCutime( Integer.parseInt( matchResult.group( ProcPidStat.CUTIME_FIELD_NR ) )
                    * ProcPidStat.MSEC_PER_CLOCK );
            entry.setCstime( Integer.parseInt( matchResult.group( ProcPidStat.CSTIME_FIELD_NR ) )
                    * ProcPidStat.MSEC_PER_CLOCK );
            return entry;

        }
        else
        {
            log.error( "failed to parse stat file with the following content"
                    + dltMsg.getPayload().get( dltMsg.getPayload().size() - 1 ) );
        }

        return null;

    }

    private void updateCpuEntry(ProcPidStat entry, long timestamp)
    {
        if (procCpuListCurrent.get( entry.getPid() ) == null)

        {
            procCpuListCurrent.put( entry.getPid(),
                                    new ProcCpuEntry( timestamp,
                                                      entry.getFullName(),
                                                      entry.getCstime() + entry.getCutime() + entry.getUtime()
                                                              + entry.getStime() ) );
        }
        else
        {
            log.error( "process already in the list" + entry.getFullName() + "   pid " + entry.getPid() );
        }

    }

    public Measurement<ProcMemEntry> parseMemData(DltMessage dltMsg)
    {
        Measurement<ProcMemEntry> result = null;

        if (isStartOfList( dltMsg ))
        {
            result = populateMemResult();

        }
        ProcPidStatm statm = parseStatmFile( dltMsg );
        if (statm != null)
        {
            updateMemEntry( statm,
                            Integer.parseInt( dltMsg.getPayload().get( 0 ) ),
                            dltMsg.getStandardHeader().getTimeStamp() );
        }

        return result;

    }

    private Measurement<ProcMemEntry> populateMemResult()
    {
        Long timestamp = new Long( 0 );
        Map<Integer, ProcMemEntry> tmpMemMap = procMemList;

        if (procMemList.size() > 0)
        {
            timestamp = new Long( procMemList.entrySet().iterator().next().getValue().getTimestamp() );
        }

        procMemList = new HashMap<Integer, ProcMemEntry>();
        return new Measurement<ProcMemEntry>( timestamp, tmpMemMap );
    }

    private ProcPidStatm parseStatmFile(DltMessage dltMsg)
    {
        Matcher matchResult = PROC_PID_STATM.matcher( dltMsg.getPayload().get( dltMsg.getPayload().size() - 1 ) );

        ProcPidStatm entry = new ProcPidStatm();

        if (matchResult.find())
        {
            entry.setRss( Integer.parseInt( matchResult.group( ProcPidStatm.RESIDENT_SET_SIZE_FIELD_NR ) ) );
            return entry;
        }
        else
        {
            log.error( "failed to parse stat file with the following content"
                    + dltMsg.getPayload().get( dltMsg.getPayload().size() - 1 ) );
        }

        return null;
    }

    private void updateMemEntry(ProcPidStatm entry, int pid, long timestamp)
    {

        if (procMemList.get( pid ) == null)
        {
            procMemList.put( pid, new ProcMemEntry( timestamp, retrieveProcessName( pid ), entry.getRss() ) );
        }
        else
        {
            log.error( "process already in the list" + retrieveProcessName( pid ) + "   pid " + pid );
        }

    }

    public Integer parseSysCpuData(DltMessage dltMsg)
    {

        if (isCpuInfoMessage( dltMsg ) && (numberOfCores == null))
        {

            numberOfCores = StringUtils.countMatches( dltMsg.getPayload().toString(), "processor" );
            return numberOfCores;

        }
        else if (numberOfCores == null)
        {
            return 1;
        }

        return numberOfCores;

    }

    public boolean isCpuInfoMessage(DltMessage dltMsg)
    {
        boolean retVal = false;
        if (EXPECTED_SYS_CPU_CTX_ID.equals( dltMsg.getExtendedHeader().getContextId() )
                && EXPECTED_APP_ID.equals( dltMsg.getExtendedHeader().getApplicationId() ) && (numberOfCores == null))
        {

            retVal = true;

        }

        return retVal;
    }

    private String retrieveProcessName(int pid)
    {
        if (procCpuListPrevious.get( pid ) != null)
        {
            return procCpuListPrevious.get( pid ).getProcName();
        }
        else if (procCpuListCurrent.get( pid ) != null)
        {
            return procCpuListCurrent.get( pid ).getProcName();
        }
        else
        {
            return Integer.toString( pid );
        }
    }

}
