/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

@Component(service = Importer.class)
public class DltStreamImporterService extends DltAbstractImporter
{

    private DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator;

    @Override
    protected MessageReader<DltMessage> getMessageParser()
    {
        return new DltStreamMessageServiceImpl( dltChannelFromLogInfoCreator );
    }

    @Reference
    public void bindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = dltChannelFromLogInfoCreator;
    }

    public void unbindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = null;
    }

    @Reference
    public void bindTimeMarkerManager(TimeMarkerManager timeMarker)
    {
        this.timeMarkerManager = timeMarker;
    }

    public void unbindTimeMarkerManager(TimeMarkerManager timeMarker)
    {
        this.timeMarkerManager = null;
    }

    @Reference
    public void bindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = timestampProvider;
    }

    public void unbindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = null;
    }

    @Reference
    public void bindProtocolMessageDispatcher(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = protocolMessageDispatcher;
    }

    public void unbindProtocolMessageDispatcher(ProtocolMessageDispatcher tsProvider)
    {
        this.protocolMessageDispatcher = null;
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
    protected long getMaximumTraceFileSizeInMB()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "dlts";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "AUTOSAR DLT 4.0 Stream Files";
    }

}
