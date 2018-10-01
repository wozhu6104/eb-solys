/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.linuxappstats.service;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.LinuxAppStatsTAProto.AppStatistics;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage.Builder;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;

import lombok.extern.log4j.Log4j;

@Log4j
public class LinuxAppStatsAdapter implements TargetAdapter
{
    private static final Logger LOG = Logger.getLogger( LinuxAppStatsAdapter.class );
    private final AppStatsMessageAcceptor messageAcceptor;
    private final Builder builder;

    public LinuxAppStatsAdapter(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        builder = LinuxAppStatsContentTAProto.MeasureMessage.newBuilder();
        messageAcceptor = new AppStatsMessageAcceptor( runtimeEventAcceptor, dataSourceContext );
    }

    @Override
    public void onProtocolMessageReceived(final Timestamp timestamp, final MessageType type, final byte[] payload,
            TimestampCreator timestampCreator)
    {
        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_LINUX_APP_STATS_PLUGIN ))
        {
            try
            {
                AppStatistics appStatistics = AppStatistics.parseFrom( payload );

                MeasureMessage measureMessage = createMessageFromString( appStatistics.getTrace() );

                messageAcceptor.acceptMessage( measureMessage, timestampCreator );

            }
            catch (InvalidProtocolBufferException | IllegalArgumentException e)
            {
                LOG.warn( "LinuxAppStats's-Message is corrupted!" );
            }
        }
    }

    private MeasureMessage createMessageFromString(String messageAsString)
    {
        builder.clear();
        try
        {
            TextFormat.merge( messageAsString, builder );
        }
        catch (ParseException e)
        {
            log.warn( "Could not parse message, it may be corrupted, content was " + messageAsString );
        }

        return builder.build();
    }

    @Override
    public void dispose()
    {
    }
}
