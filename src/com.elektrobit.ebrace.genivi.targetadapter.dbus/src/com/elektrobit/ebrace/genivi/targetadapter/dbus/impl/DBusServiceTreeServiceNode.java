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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusServiceTreeServiceNode
{

    private static final String NODE_PROPERTY_DBUS_SERVICE_ALIAS_NAMES = "AliasNames";
    private static final String NODE_PROPERTY_DBUS_INSTANCE = "Bus Instance";
    private static final String EXPECTED_TREE_LEVEL = DBusPluginConstants.DBUS_TREE_LEVEL_3;

    private final TreeNode myNode;
    private final StructureAcceptor structureAcceptor;
    private final Map<String, DBusServiceTreeObjectNode> objectNodes = new HashMap<String, DBusServiceTreeObjectNode>();

    public DBusServiceTreeServiceNode(TreeNode node, StructureAcceptor structureAcceptor)
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

    public String getServiceNiceName()
    {
        return getAliasNames().get( 0 );
    }

    public boolean hasName(List<String> names)
    {
        for (String name : names)
        {
            if (hasName( name ))
                return true;
        }
        return false;
    }

    public boolean hasName(String name)
    {
        List<String> aliasNames = getAliasNames();
        if (aliasNames == null)
            return false;
        for (int i = 0; i < aliasNames.size(); i++)
        {
            Object alias = aliasNames.get( i );
            if (name.equals( alias ))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<String> getAliasNames()
    {
        Object property = GenericTreeHelper
                .getNodePropertyValueObjectByPropertyKey( myNode, NODE_PROPERTY_DBUS_SERVICE_ALIAS_NAMES );

        return (List<String>)property;
    }

    public void setAliasNames(List<String> inAliases)
    {
        List<String> aliasNames = new ArrayList<String>();
        for (String name : inAliases)
        {
            aliasNames.add( name );
        }
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_SERVICE_ALIAS_NAMES,
                                           aliasNames,
                                           "Alias-Names of DBusService" );
    }

    public void setBusInstance(DBusInstanceType instance)
    {
        GenericTreeHelper.setNodeProperty( structureAcceptor,
                                           myNode,
                                           NODE_PROPERTY_DBUS_INSTANCE,
                                           instance,
                                           "DBus Instance (session- or system-bus)" );
    }

    public DBusInstanceType getBusInstance()
    {
        return (DBusInstanceType)GenericTreeHelper
                .getNodePropertyValueObjectByPropertyKey( myNode, NODE_PROPERTY_DBUS_INSTANCE );
    }

    private DBusServiceTreeObjectNode addDBusObjectNodeWithDedicatedNodeName(String nodeName, String path)
    {
        TreeNode node = structureAcceptor.addTreeNode( myNode, nodeName );
        DBusServiceTreeObjectNode objectNode = new DBusServiceTreeObjectNode( node, structureAcceptor );
        objectNode.setPath( path );
        objectNodes.put( path, objectNode );
        return objectNode;
    }

    public DBusServiceTreeObjectNode getDBusObjectByPath(String nodeName, String path)
    {
        if (objectNodes.containsKey( path ))
            return objectNodes.get( path );
        else
            return addDBusObjectNodeWithDedicatedNodeName( nodeName, path );
    }

    public void addNewAlias(String alias)
    {
        addNewAliases( Arrays.asList( alias ) );
    }

    public void addNewAliases(List<String> newAliases)
    {
        LinkedHashSet<String> allAliases = new LinkedHashSet<String>();
        allAliases.addAll( newAliases );

        List<String> currentAliases = getAliasNames();
        if (currentAliases != null)
            allAliases.addAll( currentAliases );

        ArrayList<String> allAliasesList = new ArrayList<String>( allAliases );
        setAliasNames( allAliasesList );
        setTreeNodeName( allAliasesList.get( 0 ) );
    }

    private void setTreeNodeName(String newName)
    {
        structureAcceptor.changeNameOfTreeNode( myNode, newName );
    }
}
