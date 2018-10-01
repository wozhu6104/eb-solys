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

public class DBusServiceTreeInterfaceNode
{
    private static final String EXPECTED_TREE_LEVEL = DBusPluginConstants.DBUS_TREE_LEVEL_5;

    public enum MemberType {
        METHOD, SIGNAL, PROPERTY
    }

    private static final String NODE_PROPERTY_DBUS_INTERFACE_NAME = "InterfaceName";

    private final TreeNode myNode;
    private final StructureAcceptor structureAcceptor;
    private final Map<String, DBusServiceTreeMemberNode> members = new HashMap<String, DBusServiceTreeMemberNode>();

    public DBusServiceTreeInterfaceNode(TreeNode node, StructureAcceptor structureAcceptor)
    {
        if (!node.getTreeLevel().getName().equals( EXPECTED_TREE_LEVEL ))
            throw new RuntimeException( "Expected tree level: " + EXPECTED_TREE_LEVEL + " , was "
                    + node.getTreeLevel().getName() );

        myNode = node;
        this.structureAcceptor = structureAcceptor;
    }

    public String getName()
    {
        return (String)GenericTreeHelper.getNodePropertyValueObjectByPropertyKey( myNode,
                                                                                  NODE_PROPERTY_DBUS_INTERFACE_NAME );
    }

    public void setName(String name)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_INTERFACE_NAME,
                                           name,
                                           "Name of exposed DBus Interface" );
    }

    private DBusServiceTreeMemberNode addDBusMethodNodeWithDedicatedNodeName(String nodeName, String methodName)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, nodeName );
        DBusServiceTreeMemberNode memberNode = new DBusServiceTreeMemberNode( node, structureAcceptor );
        memberNode.setName( methodName );
        memberNode.setType( "Method" );
        members.put( nodeName, memberNode );
        return memberNode;
    }

    private DBusServiceTreeMemberNode addDBusSignalNodeWithDedicatedNodeName(String nodeName, String signalName)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, nodeName );
        DBusServiceTreeMemberNode memberNode = new DBusServiceTreeMemberNode( node, structureAcceptor );
        memberNode.setName( signalName );
        memberNode.setType( "Signal" );
        members.put( nodeName, memberNode );
        return memberNode;
    }

    private DBusServiceTreeMemberNode addDBusPropertyNode(String name)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, name );
        DBusServiceTreeMemberNode memberNode = new DBusServiceTreeMemberNode( node, structureAcceptor );
        memberNode.setName( name );
        memberNode.setType( "Property" );
        members.put( name, memberNode );
        return memberNode;
    }

    public DBusServiceTreeMemberNode getDBusMemberByName(String nodeName, String memberName, MemberType memberType)
    {
        if (members.containsKey( nodeName ))
            return members.get( nodeName );
        else
            switch (memberType)
            {
                case METHOD :
                    return addDBusMethodNodeWithDedicatedNodeName( nodeName, memberName );
                case SIGNAL :
                    return addDBusSignalNodeWithDedicatedNodeName( nodeName, memberName );
                case PROPERTY :
                    return addDBusPropertyNode( nodeName );
                default :
                    throw new RuntimeException( "Type " + memberName + "not considered" );
            }
    }
}
