/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class DBusTargetAdapterControllerTest
{
    private static final String SIGNAL_SENDER_SERVICE = "signalSenderService";
    private static final String PATH_FOR_BROADCAST = "pathForBroadcast";
    private static final String INTERFACE_FOR_BROADCAST = "interfaceForBroadcast";
    private static final String MEMBER_FOR_BROADCAST = "memberForBroadcast";
    private static final int SIGNAL_SENDER_PID = 3;
    private static final int SERVICE_2_PID = 2;
    private static final int SERVICE_1_PID = 1;
    private static final String SERVICE_2_NAME = "service2";
    private static final String SERVICE_1_NAME = "service1";
    private static final String CALLED_PATH_NAME = "path";
    private static final String CALLED_INTERFACE_NAME = "interface";
    private static final String CALLED_MEMBER_NAME = "member";
    private ComRelationProvider comrelationProvider = null;
    private StructureProvider structureProvider;
    private TestDBusMessageSender dBusMessageSender;

    @Before
    public void setServices()
    {
        comrelationProvider = CoreServiceHelper.getComRelationProvider();
        structureProvider = CoreServiceHelper.getStructureProvider();
        dBusMessageSender = new TestDBusMessageSender();
    }

    @Test
    public void testRequestResponse()
    {
        dBusMessageSender.sendDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL,
                                           SERVICE_1_NAME,
                                           SERVICE_1_PID,
                                           new String[]{},
                                           SERVICE_2_NAME,
                                           SERVICE_2_PID,
                                           new String[]{},
                                           10,
                                           null,
                                           CALLED_PATH_NAME,
                                           CALLED_INTERFACE_NAME,
                                           CALLED_MEMBER_NAME );

        dBusMessageSender.sendDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN,
                                           "service2 new name",
                                           SERVICE_2_PID,
                                           new String[]{SERVICE_2_NAME}, // original name is as alias in the response
                                           SERVICE_1_NAME,
                                           1,
                                           new String[]{},
                                           null,
                                           10,
                                           null,
                                           null,
                                           null );

        ComRelation[] memberNodeComrelations = getMemberTreeNodeOfSenderPID( SERVICE_1_PID );
        ComRelation deepestComRelation = memberNodeComrelations[0];

        TreeNode caller = deepestComRelation.getSender();
        TreeNode callee = deepestComRelation.getReceiver();

        Assert.assertEquals( 1, memberNodeComrelations.length );
        assertCallerAndParents( caller );
        assertCalleeAndParent( callee );
    }

    private void assertCallerAndParents(TreeNode caller)
    {
        Assert.assertTrue( caller.getName().contains( CALLED_MEMBER_NAME ) );
        Assert.assertTrue( caller.getParent().getName().contains( CALLED_INTERFACE_NAME ) );
        Assert.assertTrue( caller.getParent().getParent().getName().contains( SERVICE_1_NAME ) );
        Assert.assertEquals( SERVICE_1_NAME, caller.getParent().getParent().getParent().getName() );
        Assert.assertEquals( String.valueOf( SERVICE_1_PID ),
                             caller.getParent().getParent().getParent().getParent().getName() );
    }

    private void assertCalleeAndParent(TreeNode callee)
    {
        Assert.assertEquals( CALLED_MEMBER_NAME, callee.getName() );
        Assert.assertEquals( CALLED_INTERFACE_NAME, callee.getParent().getName() );
        Assert.assertEquals( CALLED_PATH_NAME, callee.getParent().getParent().getName() );
        Assert.assertEquals( SERVICE_2_NAME, callee.getParent().getParent().getParent().getName() );
        Assert.assertEquals( String.valueOf( SERVICE_2_PID ),
                             callee.getParent().getParent().getParent().getParent().getName() );

    }

    @Test
    public void testSignal()
    {
        dBusMessageSender
                .sendDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_SIGNAL,
                                  SIGNAL_SENDER_SERVICE,
                                  SIGNAL_SENDER_PID,
                                  new String[]{},
                                  "broadcast",
                                  null,
                                  null,
                                  99,
                                  null,
                                  PATH_FOR_BROADCAST,
                                  INTERFACE_FOR_BROADCAST,
                                  MEMBER_FOR_BROADCAST );

        ComRelation[] memberNodeComrelations = getMemberTreeNodeOfSenderPID( SIGNAL_SENDER_PID );
        ComRelation deepestComRelation = memberNodeComrelations[0];

        Assert.assertEquals( 1, memberNodeComrelations.length );
        assertSignalSender( deepestComRelation.getSender() );
    }

    public ComRelation[] getMemberTreeNodeOfSenderPID(int pid)
    {
        TreeNode processTreeNode = getProcessNodeForPID( pid );
        TreeNode serviceTreeNode = processTreeNode.getChildren().get( 0 );
        TreeNode objectTreeNode = serviceTreeNode.getChildren().get( 0 );
        TreeNode interfaceTreeNode = objectTreeNode.getChildren().get( 0 );
        TreeNode memberTreeNode = interfaceTreeNode.getChildren().get( 0 );

        ComRelation[] memberNodeComrelations = comrelationProvider.getComRelations( memberTreeNode );
        return memberNodeComrelations;
    }

    private void assertSignalSender(TreeNode sender)
    {
        Assert.assertEquals( MEMBER_FOR_BROADCAST, sender.getName() );
        Assert.assertEquals( INTERFACE_FOR_BROADCAST, sender.getParent().getName() );
        Assert.assertEquals( PATH_FOR_BROADCAST, sender.getParent().getParent().getName() );
        Assert.assertEquals( SIGNAL_SENDER_SERVICE, sender.getParent().getParent().getParent().getName() );
        Assert.assertEquals( String.valueOf( SIGNAL_SENDER_PID ),
                             sender.getParent().getParent().getParent().getParent().getName() );
    }

    @After
    public void after()
    {
        CoreServiceHelper.getResetNotifier().performReset();
    }

    private TreeNode getProcessNodeForPID(int pid)
    {
        Tree dBusTree = structureProvider.getTrees().get( 0 );
        List<TreeNode> processNodes = dBusTree.getRootNode().getChildren();
        for (TreeNode processNode : processNodes)
        {
            if (processNode.getName().equals( String.valueOf( pid ) ))
                return processNode;
        }
        return null;
    }

}
