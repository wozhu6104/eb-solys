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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.channels.NodeFilter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class NodeFilterTestWith1AsSearchTerm
{

    private NodeFilter underTest;
    private final ChannelTreeNode rootNode = NodeFilterTestSuite.createFixture();

    @Before
    public void setup()
    {
        underTest = new NodeFilter( "1" );
        underTest.filter( rootNode );
    }

    @Test
    public void verifyRootNodeChildren()
    {
        assertEquals( 3, rootNode.getChildCount() );
    }

    @Test
    public void verifyChild0()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 );
        assertEquals( "A.B.C", child.getFullName() );
        assertEquals( "A.B.C", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild00()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "A.B.C.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild1()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 );
        assertEquals( "C", child.getFullName() );
        assertEquals( "C", child.getNodeName() );
        assertEquals( 2, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild10()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 );
        assertEquals( "C.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 3, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ), child.getParent() );
    }

    @Test
    public void verifyChild100()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "C.1.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild101()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 1 );
        assertEquals( "C.1.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild102()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 2 );
        assertEquals( "C.1.3", child.getFullName() );
        assertEquals( "3", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild11()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 1 );
        assertEquals( "C.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ), child.getParent() );
    }

    @Test
    public void verifyChild110()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 1 ).getChildAt( 0 );
        assertEquals( "C.2.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 1 ), child.getParent() );
    }

}
