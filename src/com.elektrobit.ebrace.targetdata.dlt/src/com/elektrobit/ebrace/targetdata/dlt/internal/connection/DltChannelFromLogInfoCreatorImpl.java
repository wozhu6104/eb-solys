/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.targetdata.dlt.internal.DltExtendedHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

@Component
public class DltChannelFromLogInfoCreatorImpl implements DltChannelFromLogInfoCreator
{

    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Override
    public void createChannelsForMessage(DltLogInfoType message)
    {
        for (DltLogInfoType.ChannelInfo channelInfo : message.getChannels())
        {
            String channelName = "trace.dlt.LOG." + channelInfo.applicationID.trim() + "."
                    + channelInfo.contextID.trim();

            RuntimeEventChannel<String> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( channelName,
                                                     Unit.TEXT,
                                                     channelInfo.description,
                                                     Arrays.asList( "appId",
                                                                    "contextId",
                                                                    "numArgs",
                                                                    "logLevel",
                                                                    "Value" ) );
            runtimeEventAcceptor.setParameter( channel,
                                               RuntimeEventChannel.CommonParameterNames.LOG_LEVEL.getName(),
                                               DltExtendedHeader.logInfo.get( channelInfo.logLevel ) );
            // TODO handle trace status
            // runtimeEventAcceptor.setParameter( channel,
            // RuntimeEventChannel.CommonParameterNames.TRACE_STATUS.getName(),
            // message.getTraceStatus() );
        }
    }

    @Reference
    public void bindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

}
