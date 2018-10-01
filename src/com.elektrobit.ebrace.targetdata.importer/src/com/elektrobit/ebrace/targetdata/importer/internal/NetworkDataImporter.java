/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

@Component(service = Importer.class)
public class NetworkDataImporter extends AbstractImporter
{
    private static final Logger LOG = Logger.getLogger( NetworkDataImporter.class );

    private ComRelationAcceptor comRelationAcceptor;

    private StructureAcceptor structureAcceptor;

    private PDMLParser parser = null;

    private PDMLStructureManager structureManager;

    private RuntimeEventChannel<String> createdRuntimeEventChannel;

    private RuntimeEventAcceptor runtimeEventAcceptor;

    public static final String TRACE_CHANNEL_PREFIX = "trace.giop.";

    public NetworkDataImporter()
    {
    }

    protected void createChannelBasedOnFileName(File file)
    {
        String channelName = TRACE_CHANNEL_PREFIX + file.getName() + ":" + System.currentTimeMillis();
        createdRuntimeEventChannel = runtimeEventAcceptor
                .createRuntimeEventChannel( channelName, Unit.TEXT, "Channel created through file import" );
    }

    @Override
    public void processFileContent(File file) throws IOException
    {
        structureManager.initialize();
        createChannelBasedOnFileName( file );
        parser = new PDMLParser( file );
        List<NetworkPacket> pdml = parser.parse();
        postProgress( 5, 10 );
        for (NetworkPacket packet : pdml)
        {
            if (isImportCanceled())
            {
                break;
            }
            runtimeEventAcceptor.acceptEvent( packet.getTimestamp(),
                                              createdRuntimeEventChannel,
                                              packet.getComRelation( structureManager ),
                                              packet.getPayload() );
            LOG.info( packet.getTimestamp() + ", " + packet.getPayload() );
        }
        postProgress( 10, 10 );
    }

    @Reference
    public void bindStructureAcceptor(StructureAcceptor acceptor)
    {
        this.structureAcceptor = acceptor;
    }

    public void unbindStructureAcceptor(StructureAcceptor acceptor)
    {
        this.structureAcceptor = null;
    }

    @Reference
    public void bindComRelationAcceptor(ComRelationAcceptor acceptor)
    {
        this.comRelationAcceptor = acceptor;
    }

    public void unbindComRelationAcceptor(ComRelationAcceptor acceptor)
    {
        this.comRelationAcceptor = null;
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

    @Activate
    public void activate()
    {
        structureManager = new PDMLStructureManager( structureAcceptor, comRelationAcceptor );
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "pdml";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "Network Data File";
    }
}
