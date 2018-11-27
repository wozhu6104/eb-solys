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

import org.apache.commons.lang.StringUtils;

import com.elektrobit.ebrace.targetdata.dlt.api.DltProcStatStatmEventConverter;
import com.elektrobit.ebrace.targetdata.dlt.api.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcMemEntry;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;

public class DltProcPayloadParser
{

    private final static String EXPECTED_APP_ID = "SYS";
    private final static String EXPECTED_SYS_CPU_CTX_ID = "PROC";
    private Integer numberOfCores = null;

    private final DltProcStatStatmEventConverter converter = new DltProcStatStatmEventConverter();

    public Measurement<ProcCpuEntry> parseCpuData(DltMessage dltMsg)
    {
        String message = StringUtils.join( dltMsg.getPayload(), " " );
        Measurement<ProcCpuEntry> result = converter.parseCpuData( dltMsg.getStandardHeader().getTimeStamp(), message );
        return result;
    }

    public Measurement<ProcMemEntry> parseMemData(DltMessage dltMsg)
    {
        String message = StringUtils.join( dltMsg.getPayload(), " " );
        Measurement<ProcMemEntry> result = converter.parseMemData( dltMsg.getStandardHeader().getTimeStamp(), message );
        return result;
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

}
