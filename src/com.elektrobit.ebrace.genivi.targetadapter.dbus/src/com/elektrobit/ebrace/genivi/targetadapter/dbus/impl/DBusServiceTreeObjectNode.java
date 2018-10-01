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

import java.util.HashMap;
import java.util.Map;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusServiceTreeObjectNode
{

    private static final String NODE_PROPERTY_DBUS_OBJECT_PATH = "ObjectPath";

    private static final String EXPECTED_TREE_LEVEL = DBusPluginConstants.DBUS_TREE_LEVEL_4;

    private final TreeNode myNode;
    private final StructureAcceptor structureAcceptor;
    private final Map<String, DBusServiceTreeInterfaceNode> interfacesNodes = new HashMap<String, DBusServiceTreeInterfaceNode>();

    public DBusServiceTreeObjectNode(TreeNode node, StructureAcceptor structureAcceptor)
    {
        if (!node.getTreeLevel().getName().equals( EXPECTED_TREE_LEVEL ))
            throw new RuntimeException( "Expected tree level: " + EXPECTED_TREE_LEVEL + " , was "
                    + node.getTreeLevel().getName() );

        myNode = node;
        this.structureAcceptor = structureAcceptor;
    }

    public void setPath(String path)
    {
        GenericTreeHelper
                .setNodeProperty( structureAcceptor,
                                  myNode,
                                  NODE_PROPERTY_DBUS_OBJECT_PATH,
                                  path,
                                  "Unix-like path for this DBus-Object" );
    }

    public String getPath()
    {
        return (String)GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( myNode,
                                                                                  NODE_PROPERTY_DBUS_OBJECT_PATH );
    }

    private DBusServiceTreeInterfaceNode addDBusInterfaceNodeWithDedicatedNodeName(String nodeName,
            String interfaceName)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, nodeName );
        DBusServiceTreeInterfaceNode interfaceNode = new DBusServiceTreeInterfaceNode( node, structureAcceptor );
        interfaceNode.setName( interfaceName );
        interfacesNodes.put( nodeName, interfaceNode );
        return interfaceNode;
    }

    public DBusServiceTreeInterfaceNode getDBusInterfaceByName(String nodeName, String interfaceName)
    {
        if (interfacesNodes.containsKey( nodeName ))
            return interfacesNodes.get( nodeName );
        else
            return addDBusInterfaceNodeWithDedicatedNodeName( nodeName, interfaceName );
    }
}
