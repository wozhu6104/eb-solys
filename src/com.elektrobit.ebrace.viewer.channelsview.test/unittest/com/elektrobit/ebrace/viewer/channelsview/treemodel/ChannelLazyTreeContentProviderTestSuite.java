/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.treemodel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChannelLazyTreeContentProviderNullTest.class, ChannelLazyTreeContentProviderOnlyRootNodeTest.class,
        ChannelLazyTreeContentProviderSomeNodesTest.class})

public class ChannelLazyTreeContentProviderTestSuite
{
    public static ChannelTreeNode createFixture()
    {
        ChannelTreeNode rootNode = new ChannelTreeNode( "root" );

        ChannelTreeNode cpuNode = new ChannelTreeNode( "cpu", rootNode, "cpu" );
        rootNode.addChild( cpuNode );

        ChannelTreeNode cpuSystemNode = new ChannelTreeNode( "system", cpuNode, "cpu.system" );
        cpuNode.addChild( cpuSystemNode );

        ChannelTreeNode memNode = new ChannelTreeNode( "mem", rootNode, "mem" );
        rootNode.addChild( memNode );

        ChannelTreeNode mem1Node = new ChannelTreeNode( "mem", memNode, "mem.1" );
        memNode.addChild( mem1Node );

        ChannelTreeNode mem2Node = new ChannelTreeNode( "2", memNode, "mem.2" );
        memNode.addChild( mem2Node );

        ChannelTreeNode mem21Node = new ChannelTreeNode( "1", memNode, "mem.2.1" );
        mem2Node.addChild( mem21Node );

        ChannelTreeNode traceNode = new ChannelTreeNode( "trace", rootNode, "trace" );
        rootNode.addChild( traceNode );

        return rootNode;
    }

}
