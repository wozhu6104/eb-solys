/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.bundlemanager;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebrace.genivi.targetadapter.dbus.impl.DBusDataEventDecoder;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessInfoChangedListenerIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.google.protobuf.InvalidProtocolBufferException;

public class DBusTargetAdapterController implements TargetAdapter, ProcessInfoChangedListenerIF
{

    private final StructureAcceptor structureAcceptor;
    private final ComRelationAcceptor comRelationAcceptor;
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private DBusDataEventDecoder eventDecoder;
    private final ReadProcessRegistryIF processRegistry;

    private final static Logger LOG = Logger.getLogger( DBusTargetAdapterController.class );
    private Tree dbusCommunicationTree;
    private final DataSourceContext sourceContext;

    public DBusTargetAdapterController(StructureAcceptor structureAcceptor, ComRelationAcceptor comRelationAcceptor,
            RuntimeEventAcceptor runtimeEventAcceptor, ReadProcessRegistryIF processRegistry, DataSourceContext context)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.structureAcceptor = structureAcceptor;
        this.comRelationAcceptor = comRelationAcceptor;
        this.processRegistry = processRegistry;
        this.sourceContext = context;
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {
        initIfNot();
        newAppMessageReceived( timestamp, payload, type );
    }

    private void initIfNot()
    {
        if (isDbusAnalysisTreeNotAvailable())
        {
            createDbusTree();

            eventDecoder = new DBusDataEventDecoder( structureAcceptor,
                                                     comRelationAcceptor,
                                                     runtimeEventAcceptor,
                                                     processRegistry,
                                                     dbusCommunicationTree,
                                                     sourceContext );
            eventDecoder.init();
        }

    }

    private boolean isDbusAnalysisTreeNotAvailable()
    {
        return dbusCommunicationTree == null;
    }

    private void createDbusTree()
    {
        TreeLevelDef treeLevel1 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_1,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_1,
                                                                     null );
        TreeLevelDef treeLevel2 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_2,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_2,
                                                                     null );
        TreeLevelDef treeLevel3 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_3,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_3,
                                                                     null );
        TreeLevelDef treeLevel4 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_4,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_4,
                                                                     null );
        TreeLevelDef treeLevel5 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_5,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_5,
                                                                     null );
        TreeLevelDef treeLevel6 = structureAcceptor.createTreeLevel( DBusPluginConstants.DBUS_TREE_LEVEL_6,
                                                                     DBusPluginConstants.DBUS_TREE_LEVEL_6,
                                                                     null );

        dbusCommunicationTree = structureAcceptor
                .addNewTreeInstance( DBusPluginConstants.DBUS_TREE_NAME,
                                     DBusPluginConstants.DBUS_TREE_DESCRIPTION,
                                     sourceContext.getSourceName() + DBusPluginConstants.DBUS_TREE_ROOT_NODE_NAME,
                                     Arrays.asList( treeLevel1,
                                                    treeLevel2,
                                                    treeLevel3,
                                                    treeLevel4,
                                                    treeLevel5,
                                                    treeLevel6 ) );

    }

    private void newAppMessageReceived(Timestamp ts, byte[] msg, MessageType type)
    {
        if (type == MessageType.MSG_TYPE_DBUS)
        {
            try
            {
                DBusApplicationMessage appMsg = DBusApplicationMessage.parseFrom( msg );

                if (eventDecoder != null)
                {
                    eventDecoder.newDBusApplicationMessageReceived( ts, appMsg );
                }

            }
            catch (InvalidProtocolBufferException e)
            {
                LOG.warn( "DBu's-Message is corrupted!" );
            }
        }
    }

    @Override
    public void processInfoChanged(DataSourceContext dataSourceContext)
    {
        if (eventDecoder != null)
        {
            eventDecoder.processInfoChanged( dataSourceContext );
        }
    }

    @Override
    public void dispose()
    {
        invalidateEventDecoder();
        dbusCommunicationTree = null;
    }

    private void invalidateEventDecoder()
    {
        if (eventDecoder != null)
        {
            eventDecoder.invalidate();
            eventDecoder = null;
        }
    }
}
