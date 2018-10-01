/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.allChannels;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

@RunWith(Suite.class)
@Suite.SuiteClasses({NodeFilterTestWithNullAsSearchTerm.class, NodeFilterTestWithAAsSearchTerm.class,
        NodeFilterTestWithBAsSearchTerm.class, NodeFilterTestWith1AsSearchTerm.class,
        NodeFilterTestWithUnitAsSearchTerm.class})
public class NodeFilterTestSuite
{

    static ChannelTreeNode createFixture()
    {
        ChannelTreeNode root = new ChannelTreeNode( "root", null, null );

        ChannelTreeNode node = createChildNode( root, "A.B.C", "A.B.C" );
        createChildNode( node, "A.B.C.1", "1", Unit.TEXT );
        createChildNode( node, "A.B.C.2", "2" );
        createChildNode( node, "A.B.C.3", "3" );

        node = createChildNode( root, "B.C.D.E", "B.C.D.E", Unit.TEXT );

        node = createChildNode( root, "C", "C" );
        ChannelTreeNode node1 = createChildNode( node, "C.1", "1" );
        createChildNode( node1, "C.1.1", "1" );
        createChildNode( node1, "C.1.2", "2" );
        createChildNode( node1, "C.1.3", "3" );
        ChannelTreeNode node2 = createChildNode( node, "C.2", "2" );
        createChildNode( node2, "C.2.1", "1" );
        createChildNode( node2, "C.2.2", "2" );

        node = createChildNode( root, "D.E.F", "D.E.F" );
        createChildNode( node, "D.E.F.1", "1" );
        createChildNode( node, "D.E.F.2", "2" );
        createChildNode( node, "D.E.F.3.1", "3.1" );

        return root;
    }

    private static ChannelTreeNode createChildNode(ChannelTreeNode parent, String fullName, String nodeName)
    {
        return createChildNode( parent, fullName, nodeName, null );
    }

    private static ChannelTreeNode createChildNode(ChannelTreeNode parent, String fullName, String nodeName,
            Unit<String> unit)
    {
        ChannelTreeNode childNode = new ChannelTreeNode( nodeName, parent, fullName );
        parent.addChild( childNode );

        RuntimeEventChannelMock<String> channel = new RuntimeEventChannelMock<String>( fullName );
        channel.setUnit( unit );
        childNode.setRuntimeEventChannel( channel );

        return childNode;
    }

}
