/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusServiceTreeBuilder
{
    public final static int UNKNOWN_PROCESS_PID = 0;
    public final static int UNKNOWN_PROCESS_UID = 0;

    private StructureAcceptor structureAcceptor;
    private String targetAgentOwnSessionBusName;
    private String targetAgentOwnSystemBusName;
    private String targetAgentEventSourceSessionBusName;
    private String targetAgentEventSourceSystemBusName;
    private final ReadProcessRegistryIF processRegistry;
    private final CyclicProcessInfoUpdater cyclicProcessInfoUpdater;
    private final Map<Integer, DBusServiceTreeProcessNode> processNodes = new HashMap<Integer, DBusServiceTreeProcessNode>();
    private final Tree dbusCommunicationTree;
    private final DataSourceContext sourceContext;

    DBusServiceTreeBuilder(StructureAcceptor structureAcceptor, ReadProcessRegistryIF processRegistry,
            Tree dbusCommunicationTree, DataSourceContext sourceContext)
    {
        this.structureAcceptor = structureAcceptor;
        this.processRegistry = processRegistry;
        this.dbusCommunicationTree = dbusCommunicationTree;
        this.sourceContext = sourceContext;
        cyclicProcessInfoUpdater = new CyclicProcessInfoUpdater( structureAcceptor,
                                                                 processRegistry,
                                                                 dbusCommunicationTree,
                                                                 sourceContext );
        targetAgentOwnSessionBusName = "";
        targetAgentOwnSystemBusName = "";
        targetAgentEventSourceSessionBusName = "";
        targetAgentEventSourceSystemBusName = "";
        cyclicProcessInfoUpdater.startProcessInfoUpdating();
    }

    public void addNodesForUnknownProcess()
    {
        DBusServiceTreeProcessNode unknownProcess = this.addDBusProcessNode( UNKNOWN_PROCESS_PID );
        unknownProcess.setProcessName( "unknown" );
        unknownProcess.setUserId( UNKNOWN_PROCESS_UID );
        unknownProcess.changeServiceTreeNodeName( "unknown" );

        unknownProcess.getDBusServiceByName( "broadcast",
                                             Arrays.asList( "session bus broadcast" ),
                                             DBusInstanceType.DBUS_INSTANCE_SESSION_BUS );

        unknownProcess.getDBusServiceByName( "broadcast",
                                             Arrays.asList( "system bus broadcast" ),
                                             DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS );
    }

    public void invalidate()
    {
        cyclicProcessInfoUpdater.stopProcessInfoUpdating();
        structureAcceptor = null;
    }

    public void setOwnBusNameByInstance(DBusInstanceType instance, String busName)
    {
        if (instance == DBusInstanceType.DBUS_INSTANCE_SESSION_BUS)
        {
            targetAgentOwnSessionBusName = busName;
        }
        else if (instance == DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS)
        {
            targetAgentOwnSystemBusName = busName;
        }
    }

    public void setEventSourceBusNameByInstance(DBusInstanceType instance, String busName)
    {
        if (instance == DBusInstanceType.DBUS_INSTANCE_SESSION_BUS)
        {
            targetAgentEventSourceSessionBusName = busName;
        }
        else if (instance == DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS)
        {
            targetAgentEventSourceSystemBusName = busName;
        }
    }

    public String getOwnBusNameByInstance(DBusInstanceType instance)
    {
        if (instance == DBusInstanceType.DBUS_INSTANCE_SESSION_BUS)
        {
            return targetAgentOwnSessionBusName;
        }
        else if (instance == DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS)
        {
            return targetAgentOwnSystemBusName;
        }
        return "";
    }

    public String getEventSourceBusNameByInstance(DBusInstanceType instance)
    {
        if (instance == DBusInstanceType.DBUS_INSTANCE_SESSION_BUS)
        {
            return targetAgentEventSourceSessionBusName;
        }
        else if (instance == DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS)
        {
            return targetAgentEventSourceSystemBusName;
        }
        return "";
    }

    public DBusServiceTreeProcessNode addDBusProcessNode(int pid)
    {
        if (!dbusProcessExists( pid ))
        {
            String processName = "";
            if (processRegistry.isInfoForProcessAvailable( pid, sourceContext ))
            {
                processName = processRegistry.getProcessInfo( pid, sourceContext ).getName();
            }

            String newNodeName = processName.isEmpty() ? Integer.toString( pid ) : processName;
            TreeNode newNode = structureAcceptor.addTreeNode( dbusCommunicationTree.getRootNode(), newNodeName );

            DBusServiceTreeProcessNode processNode = new DBusServiceTreeProcessNode( newNode, structureAcceptor );
            processNode.setPId( pid );
            processNodes.put( pid, processNode );
            return processNode;
        }
        return null;
    }

    public DBusServiceTreeProcessNode getDBusProcessNode(int pid)
    {
        if (processNodes.containsKey( pid ))
            return processNodes.get( pid );
        else
            return addDBusProcessNode( pid );
    }

    public boolean dbusProcessExists(int pid)
    {
        return processNodes.containsKey( pid );
    }
}
