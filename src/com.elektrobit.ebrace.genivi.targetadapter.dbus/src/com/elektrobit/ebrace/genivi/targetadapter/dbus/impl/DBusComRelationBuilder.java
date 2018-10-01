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

import java.util.List;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.impl.DBusServiceTreeInterfaceNode.MemberType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusComRelationBuilder
{
    private ComRelationAcceptor comRelationAcceptor;
    private DBusServiceTreeBuilder treeBuilder;
    private final DBusMethodCallCache dbusMethodCallCache;

    public DBusComRelationBuilder(ComRelationAcceptor comRelationAcceptor, DBusServiceTreeBuilder treeBuilder,
            DBusMethodCallCache dBusMethodCallCache)
    {
        this.comRelationAcceptor = comRelationAcceptor;
        this.treeBuilder = treeBuilder;
        this.dbusMethodCallCache = dBusMethodCallCache;
    }

    public void invalidate()
    {
        comRelationAcceptor = null;
        treeBuilder = null;
    }

    public ComRelation createComRelationByDBusTraceMessage(DBusEvtTraceMessage msg)
    {
        ComRelation comRelation = null;
        TreeNode sourceEndpoint = null;
        TreeNode destinationEndpoint = null;

        DBusMessageHeader header = msg.getHeader();
        String senderName = header.getSender();
        List<String> senderAliasNamesList = header.getSenderAliasNamesList();
        int senderPid = header.getSenderPid();
        if (senderPid < 0)
            senderPid = DBusServiceTreeBuilder.UNKNOWN_PROCESS_PID;

        String receiverName = header.getReceiver();
        List<String> receiverAliasNamesList = header.getReceiverAliasNamesList();
        int receiverPid = header.getReceiverPid();
        if (receiverPid < 0)
            receiverPid = DBusServiceTreeBuilder.UNKNOWN_PROCESS_PID;

        DBusServiceTreeServiceNode messageSourceService = getServiceNodeByName( senderName,
                                                                                senderAliasNamesList,
                                                                                senderPid,
                                                                                msg.getInstance() );

        DBusServiceTreeServiceNode messageDestinationService = getServiceNodeByName( receiverName,
                                                                                     receiverAliasNamesList,
                                                                                     receiverPid,
                                                                                     msg.getInstance() );

        switch (header.getType())
        {
            case DBUS_MSG_TYPE_METHOD_CALL :
                sourceEndpoint = getDBusMethodCallComRelationSourceNode( messageSourceService,
                                                                         header.getInterface(),
                                                                         header.getMember() );
                destinationEndpoint = getDBusMethodCallComRelationDestinationNode( messageDestinationService,
                                                                                   header.getPath(),
                                                                                   header.getInterface(),
                                                                                   header.getMember() );
                comRelation = getComRelation( sourceEndpoint, destinationEndpoint );
                break;

            case DBUS_MSG_TYPE_SIGNAL :
                sourceEndpoint = getDBusSignalComRelationSourceNode( messageSourceService, header );
                destinationEndpoint = getDBusSignalComRelationDestinationNode( messageDestinationService, header );
                comRelation = getComRelation( sourceEndpoint, destinationEndpoint );
                break;

            case DBUS_MSG_TYPE_METHOD_RETURN :
                DBusMethodCallCacheObj call = dbusMethodCallCache.findCallForReturn( header );
                if (call != null)
                {
                    sourceEndpoint = getDBusMethodCallComRelationSourceNode( messageDestinationService,
                                                                             call.getInterfaceName(),
                                                                             call.getMember() );
                    destinationEndpoint = getDBusMethodCallComRelationDestinationNode( messageSourceService,
                                                                                       call.getPath(),
                                                                                       call.getInterfaceName(),
                                                                                       call.getMember() );
                }
                else
                {
                    sourceEndpoint = getDBusMethodCallComRelationSourceNode( messageDestinationService, "", "" );
                    destinationEndpoint = getDBusMethodCallComRelationDestinationNode( messageSourceService,
                                                                                       "",
                                                                                       "",
                                                                                       "" );
                }

                comRelation = getComRelation( sourceEndpoint, destinationEndpoint );
                break;

            case DBUS_MSG_TYPE_ERROR :
                break;

            default :
                break;
        }

        return comRelation;
    }

    private ComRelation getComRelation(TreeNode sourceNode, TreeNode destinationNode)
    {
        ComRelation comRelation = null;
        comRelation = getExistingComRelationBetweenNodes( sourceNode, destinationNode );
        if (comRelation == null)
            comRelation = comRelationAcceptor.addComRelation( sourceNode, destinationNode, "" );
        return comRelation;
    }

    private ComRelation getExistingComRelationBetweenNodes(TreeNode sourceNode, TreeNode destinationNode)
    {
        ComRelation[] existingComRelations = comRelationAcceptor.getComRelations( sourceNode );
        for (ComRelation cr : existingComRelations)
        {
            if (cr.getReceiver().equals( destinationNode ))
            {
                return cr;
            }
        }
        return null;
    }

    private DBusServiceTreeServiceNode getServiceNodeByName(String serviceName, List<String> aliases, int processID,
            DBusInstanceType instance)
    {
        DBusServiceTreeProcessNode processNode = treeBuilder.getDBusProcessNode( processID );
        return processNode.getDBusServiceByName( serviceName, aliases, instance );
    }

    private TreeNode getDBusMethodCallComRelationSourceNode(DBusServiceTreeServiceNode sourceServiceNode,
            String interfaceName, String member)
    {
        final String NEW_OBJECT_NODE_PROPERTY_NAME = "/CallerObject";
        final String NEW_INTERFACE_NODE_PROPERTY_NAME = interfaceName + "Caller";
        final String NEW_MEMBER_NODE_PROPERTY_NAME = interfaceName + "Caller";

        final String NEW_OBJECT_NODE_NAME = sourceServiceNode.getServiceNiceName() + "/CallerObject";
        final String NEW_INTERFACE_NODE_NAME = sourceServiceNode.getServiceNiceName() + interfaceName + "Caller";
        final String NEW_MEMBER_NODE_NAME = sourceServiceNode.getServiceNiceName() + member + "Caller";

        DBusServiceTreeObjectNode objectNode = sourceServiceNode.getDBusObjectByPath( NEW_OBJECT_NODE_NAME,
                                                                                      NEW_OBJECT_NODE_PROPERTY_NAME );

        DBusServiceTreeInterfaceNode interfaceNode = objectNode
                .getDBusInterfaceByName( NEW_INTERFACE_NODE_NAME, NEW_INTERFACE_NODE_PROPERTY_NAME );
        DBusServiceTreeMemberNode memberNode = interfaceNode
                .getDBusMemberByName( NEW_MEMBER_NODE_NAME, NEW_MEMBER_NODE_PROPERTY_NAME, MemberType.METHOD );
        return memberNode.getTreeNode();
    }

    private TreeNode getDBusMethodCallComRelationDestinationNode(DBusServiceTreeServiceNode destinationServiceNode,
            String destinationPath, String destinationInterfaceName, String destinationMember)
    {
        DBusServiceTreeObjectNode objectNode = destinationServiceNode.getDBusObjectByPath( destinationPath,
                                                                                           destinationPath );

        DBusServiceTreeInterfaceNode interfaceNode = objectNode.getDBusInterfaceByName( destinationInterfaceName,
                                                                                        destinationInterfaceName );

        DBusServiceTreeMemberNode memberNode = interfaceNode
                .getDBusMemberByName( destinationMember, destinationMember, MemberType.METHOD );
        return memberNode.getTreeNode();
    }

    private TreeNode getDBusSignalComRelationSourceNode(DBusServiceTreeServiceNode targetServiceNode,
            DBusMessageHeader header)
    {
        DBusServiceTreeObjectNode objectNode = targetServiceNode.getDBusObjectByPath( header.getPath(),
                                                                                      header.getPath() );
        DBusServiceTreeInterfaceNode interfaceNode = objectNode.getDBusInterfaceByName( header.getInterface(),
                                                                                        header.getInterface() );
        DBusServiceTreeMemberNode memberNode = interfaceNode
                .getDBusMemberByName( header.getMember(), header.getMember(), MemberType.SIGNAL );
        return memberNode.getTreeNode();
    }

    private TreeNode getDBusSignalComRelationDestinationNode(DBusServiceTreeServiceNode targetServiceNode,
            DBusMessageHeader header)
    {
        final String NEW_OBJECT_NODE_PROPERTY_NAME = "/SignalReceiverObject";
        final String NEW_INTERFACE_NODE_PROPERTY_NAME = "SignalReceiverInterface";
        final String NEW_MEMBER_NODE_PROPERTY_NAME = "SignalReceiverMember";

        final String NEW_OBJECT_NODE_NAME = targetServiceNode.getServiceNiceName() + "/SignalReceiverObject";
        final String NEW_INTERFACE_NODE_NAME = targetServiceNode.getServiceNiceName() + "SignalReceiverInterface";
        final String NEW_MEMBER_NODE_NAME = targetServiceNode.getServiceNiceName() + "SignalReceiverMember";

        DBusServiceTreeObjectNode objectNode = targetServiceNode.getDBusObjectByPath( NEW_OBJECT_NODE_NAME,
                                                                                      NEW_OBJECT_NODE_PROPERTY_NAME );
        DBusServiceTreeInterfaceNode interfaceNode = objectNode
                .getDBusInterfaceByName( NEW_INTERFACE_NODE_NAME, NEW_INTERFACE_NODE_PROPERTY_NAME );

        DBusServiceTreeMemberNode memberNode = interfaceNode
                .getDBusMemberByName( NEW_MEMBER_NODE_NAME, NEW_MEMBER_NODE_PROPERTY_NAME, MemberType.SIGNAL );
        return memberNode.getTreeNode();
    }
}
