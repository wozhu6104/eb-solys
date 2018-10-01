/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor.protocoldefinitions.TargetAgentProtocolSystemDStartupMonitor.SystemdBootupMonApplicationMessage;
import com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor.protocoldefinitions.TargetAgentProtocolSystemDStartupMonitor.SystemdBootupMonMessageId;
import com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor.protocoldefinitions.TargetAgentProtocolSystemDStartupMonitor.SystemdServicesStartupTimes;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.protobuf.InvalidProtocolBufferException;

public class SystemdMessageAdapter implements TargetAdapter
{
    private static final Logger LOG = Logger.getLogger( SystemdMessageAdapter.class );

    private final Map<String, RuntimeEventChannel<Boolean>> systemdActiveUnitChannels;

    private final RuntimeEventAcceptor runtimeEventAcceptorService;

    private final DataSourceContext dataSourceContext;

    public SystemdMessageAdapter(RuntimeEventAcceptor runtimeEventAcceptorService, DataSourceContext dataSourceContext)
    {
        this.dataSourceContext = dataSourceContext;
        this.runtimeEventAcceptorService = runtimeEventAcceptorService;
        systemdActiveUnitChannels = new HashMap<String, RuntimeEventChannel<Boolean>>();
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {
        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_SYSTEMD_START_UP_MONITOR ))
        {
            try
            {
                SystemdBootupMonApplicationMessage applicationMsg = SystemdBootupMonApplicationMessage
                        .parseFrom( payload );

                SystemdBootupMonMessageId id = applicationMsg.getId();

                switch (id)
                {
                    case SYSTEMD_START_UP_MONITOR_MSG_ID : {
                        if (applicationMsg.hasBootupmessage())
                        {
                            applicationMsg.getBootupmessage();
                        }
                    }
                        break;

                    case SYSTEMD_START_UP_MONITOR_SERVICES_STARTUP_TIMES_ID : {
                        if (applicationMsg.hasStartuptimes())
                        {
                            SystemdServicesStartupTimes startupTimesMsg = applicationMsg.getStartuptimes();

                            for (int serviceIndex = 0; serviceIndex < startupTimesMsg
                                    .getServicenameCount(); serviceIndex++)
                            {
                                String name = startupTimesMsg.getServicename( serviceIndex );

                                long inactiveExitTimestampMonotonic = startupTimesMsg
                                        .getStatusTimingInfo( serviceIndex ).getInactiveExitTimestampMonotonic();
                                long activeEnterTimestampMonotonic = startupTimesMsg.getStatusTimingInfo( serviceIndex )
                                        .getActiveEnterTimestampMonotonic();
                                long activeExitTimestampMonotonic = startupTimesMsg.getStatusTimingInfo( serviceIndex )
                                        .getActiveExitTimestampMonotonic();
                                long inactiveEnterTimestampMonotonic = startupTimesMsg
                                        .getStatusTimingInfo( serviceIndex ).getInactiveEnterTimestampMonotonic();

                                if (inactiveExitTimestampMonotonic > 0 && activeEnterTimestampMonotonic > 0)
                                {
                                    RuntimeEventChannel<Boolean> channel = null;
                                    if (!systemdActiveUnitChannels.containsKey( name ))
                                    {
                                        channel = runtimeEventAcceptorService
                                                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                                                 "systemd." + name,
                                                                                 Unit.BOOLEAN,
                                                                                 "unit active" );
                                        systemdActiveUnitChannels.put( name, channel );
                                    }
                                    else
                                    {
                                        channel = systemdActiveUnitChannels.get( name );
                                    }

                                    runtimeEventAcceptorService.acceptEventMicros( inactiveExitTimestampMonotonic,
                                                                                   channel,
                                                                                   null,
                                                                                   true );
                                    runtimeEventAcceptorService.acceptEventMicros( activeEnterTimestampMonotonic,
                                                                                   channel,
                                                                                   null,
                                                                                   false );
                                }
                                else if (activeExitTimestampMonotonic > 0 && inactiveEnterTimestampMonotonic > 0)
                                {
                                    RuntimeEventChannel<Boolean> channel = null;
                                    if (!systemdActiveUnitChannels.containsKey( name ))
                                    {
                                        channel = runtimeEventAcceptorService
                                                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                                                 "systemd." + name,
                                                                                 Unit.BOOLEAN,
                                                                                 "unit active" );
                                        systemdActiveUnitChannels.put( name, channel );
                                    }
                                    else
                                    {
                                        channel = systemdActiveUnitChannels.get( name );
                                    }

                                    runtimeEventAcceptorService.acceptEventMicros( activeExitTimestampMonotonic,
                                                                                   channel,
                                                                                   null,
                                                                                   true );
                                    runtimeEventAcceptorService.acceptEventMicros( inactiveEnterTimestampMonotonic,
                                                                                   channel,
                                                                                   null,
                                                                                   false );
                                }

                            }
                        }
                    }
                        break;

                    default :
                        System.out.println( applicationMsg );
                        break;
                }
            }
            catch (InvalidProtocolBufferException e)
            {
                LOG.warn( "SystemMessageAdapter's-Message is corrupted!" );

            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
