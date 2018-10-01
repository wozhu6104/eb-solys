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

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusServiceTreeMemberNode
{

    private static final String NODE_PROPERTY_DBUS_INTERFACE_MEMBER_TYPE = "MemberType";
    private static final String NODE_PROPERTY_DBUS_INTERFACE_MEMBER_NAME = "MethodName";

    private final TreeNode myNode;
    private final StructureAcceptor structureAcceptor;

    public DBusServiceTreeMemberNode(TreeNode node, StructureAcceptor structureAcceptor)
    {
        if (!node.getTreeLevel().getName().equals( DBusPluginConstants.DBUS_TREE_LEVEL_6 ))
            throw new RuntimeException( "Expected tree level: " + DBusPluginConstants.DBUS_TREE_LEVEL_6 + " , was "
                    + node.getTreeLevel().getName() );

        myNode = node;
        this.structureAcceptor = structureAcceptor;
    }

    public void setName(String name)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_INTERFACE_MEMBER_NAME,
                                           name,
                                           "Name of DBus InterfaceMember-Function" );
    }

    public void setType(String type)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_INTERFACE_MEMBER_TYPE,
                                           type,
                                           "Type of DBus InterfaceMember-Function" );
    }

    public String getName()
    {
        return (String)GenericTreeHelper
                .getNodePropertyValueObjectByPropertyKey( myNode, NODE_PROPERTY_DBUS_INTERFACE_MEMBER_NAME );
    }

    public TreeNode getTreeNode()
    {
        return myNode;
    }
}
