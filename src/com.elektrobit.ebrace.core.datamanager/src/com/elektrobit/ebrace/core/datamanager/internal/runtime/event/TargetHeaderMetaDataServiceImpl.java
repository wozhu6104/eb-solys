/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderMetaData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class TargetHeaderMetaDataServiceImpl implements TargetHeaderMetaDataService, ResetListener
{
    private static final int MICROS_TO_MILLIS_FACTOR = 1000;
    private final List<RTargetHeaderCPUValue> cpuValues;
    private RTargetHeaderMetaData previousCPUMetaData = null;
    private RuntimeEventAcceptor runtimeEventAcceptor;

    public TargetHeaderMetaDataServiceImpl()
    {
        cpuValues = new ArrayList<RTargetHeaderCPUValue>();
    }

    @Reference
    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Override
    public List<RTargetHeaderCPUValue> getTargetHeaderCPUValues()
    {
        return Collections.unmodifiableList( cpuValues );
    }

    @Override
    public void addMetaData(RTargetHeaderMetaData currentMetaData)
    {
        if (containsTargetHeaderCPUValue( currentMetaData ))
        {
            if (previousCPUMetaData == null)
            {
                previousCPUMetaData = currentMetaData;
            }
            else
            {
                try
                {
                    double cpuValue = computeCPUValue( previousCPUMetaData, currentMetaData );
                    cpuValues.add( new RTargetHeaderCPUValue( currentMetaData.getTimestamp(), cpuValue ) );
                    addCPUDataToChannelIfDebugOn( currentMetaData.getTimestamp(), cpuValue );
                    previousCPUMetaData = currentMetaData;
                }
                catch (NumberFormatException e)
                {
                    log.warn( "Couldn't parse CPU Value from Target-Header." );
                    log.warn( "Previous MetaData was " + previousCPUMetaData.toString() + "." );
                    log.warn( "Current MetaData was " + currentMetaData.toString() + "." );
                }

            }
        }
    }

    private void addCPUDataToChannelIfDebugOn(long timestamp, double cpuValue)
    {
        if (log.isDebugEnabled())
        {
            RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( "debug.cpu.system", Unit.PERCENT, "" );
            runtimeEventAcceptor.acceptEventMicros( timestamp, channel, null, cpuValue );
        }
    }

    private boolean containsTargetHeaderCPUValue(RTargetHeaderMetaData metaData)
    {
        return metaData.getKey().equals( MetaDataKeys.SYSTEM_CPU_VALUES.toString() );
    }

    private double computeCPUValue(RTargetHeaderMetaData previousCPUMetaData, RTargetHeaderMetaData currentMetaData)
            throws NumberFormatException
    {
        long timespanInMillis = (currentMetaData.getTimestamp() - previousCPUMetaData.getTimestamp())
                / MICROS_TO_MILLIS_FACTOR;
        long cpuTicksInTimespan = new Long( currentMetaData.getValue() ) - new Long( previousCPUMetaData.getValue() );

        double cpuValue = ((double)cpuTicksInTimespan) / ((double)timespanInMillis);

        double resultCPU = 100 * cpuValue;

        if (resultCPU < 0.0 || resultCPU > 100.0)
        {
            log.warn( "Header System CPU value was " + resultCPU );
        }

        return resultCPU;
    }

    @Override
    public void onReset()
    {
        cpuValues.clear();
    }

}
