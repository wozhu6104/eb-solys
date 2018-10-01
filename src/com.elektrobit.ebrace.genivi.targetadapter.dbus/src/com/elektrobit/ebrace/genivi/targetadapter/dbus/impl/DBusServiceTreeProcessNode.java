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

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusServiceTreeProcessNode
{

    private static final String NODE_PROPERTY_DBUS_PROCESS_ID = "ProcessID";
    private static final String NODE_PROPERTY_DBUS_USER_ID = "UserID";
    private static final String NODE_PROPERTY_DBUS_PROCESS_NAME = "ProcessName";
    private static final String NODE_PROPERTY_DBUS_PROCESS_STATE = "ProcessState";
    private static final String EXPECTED_TREE_LEVEL = DBusPluginConstants.DBUS_TREE_LEVEL_2;

    private final TreeNode myNode;
    private final StructureAcceptor structureAcceptor;
    private final List<DBusServiceTreeServiceNode> serviceNodes = new ArrayList<DBusServiceTreeServiceNode>();

    public DBusServiceTreeProcessNode(TreeNode node, StructureAcceptor structureAcceptor)
    {

        if (!node.getTreeLevel().getName().equals( EXPECTED_TREE_LEVEL ))
            throw new IllegalArgumentException( "Expected tree level: " + EXPECTED_TREE_LEVEL + " , was "
                    + node.getTreeLevel().getName() );

        myNode = node;
        this.structureAcceptor = structureAcceptor;
    }

    public void changeServiceTreeNodeName(String newName)
    {
        structureAcceptor.changeNameOfTreeNode( myNode, newName );
    }

    public void setPId(int pid)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor, myNode, NODE_PROPERTY_DBUS_PROCESS_ID, pid, "ProcessID" );
    }

    public int getPId()
    {
        return (Integer)GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( myNode,
                                                                                   NODE_PROPERTY_DBUS_PROCESS_ID );
    }

    public void setProcessState(String state)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_PROCESS_STATE,
                                           state,
                                           "ProcessState" );
    }

    public void setUserId(int userId)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor, myNode, NODE_PROPERTY_DBUS_USER_ID, userId, "UserID" );
    }

    public int getUserId()
    {
        return (Integer)GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( myNode, NODE_PROPERTY_DBUS_USER_ID );
    }

    public void setProcessName(String processName)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_PROCESS_NAME,
                                           processName,
                                           "ProcessName" );
    }

    public String getProcessName()
    {
        return (String)GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( myNode,
                                                                                  NODE_PROPERTY_DBUS_PROCESS_NAME );
    }

    public DBusServiceTreeServiceNode addDBusServiceNode(String uniqueName, List<String> aliasNames,
            DBusInstanceType instance)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, uniqueName );
        DBusServiceTreeServiceNode serviceNode = new DBusServiceTreeServiceNode( node, structureAcceptor );
        serviceNode.addNewAlias( uniqueName );
        serviceNode.addNewAliases( aliasNames );
        serviceNode.setBusInstance( instance );
        serviceNodes.add( serviceNode );
        return serviceNode;
    }

    public DBusServiceTreeServiceNode getDBusServiceByName(String name, List<String> aliases, DBusInstanceType instance)
    {
        for (DBusServiceTreeServiceNode serviceNode : serviceNodes)
        {
            boolean hasName = serviceNode.hasName( name );
            boolean hasAliasName = serviceNode.hasName( aliases );
            if ((hasName || hasAliasName) && serviceNode.getBusInstance() == instance)
            {
                serviceNode.addNewAliases( aliases );
                return serviceNode;
            }
        }
        return addDBusServiceNode( name, aliases, instance );
    }

    public ModelElement getTreeNodeModelElement()
    {
        return myNode;
    }
}
