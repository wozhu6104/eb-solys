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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.allChannels.NodeBuilder;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class NodeBuilderTest1
{
    private static final String[] INPUT = {"A", "A.B", "A.B.C", "A.B.C.1", "A.B.C.2", "A.B.C.3", "B", "B.C", "B.C.D",
            "B.C.D.E"};
    private static final List<String> INPUT_LIST = Arrays.asList( INPUT );

    private NodeBuilder underTest;
    private ChannelTreeNode rootNode;

    @Before
    public void setup()
    {
        underTest = new NodeBuilder();
        rootNode = underTest.build( INPUT_LIST );
    }

    @Test
    public void checkThatResultIsNotNull()
    {
        assertNotNull( rootNode );
    }

    @Test
    public void verifyRootNodeHasNoParent()
    {
        assertNull( rootNode.getParent() );
    }

    @Test
    public void verifyRootNodeChildren()
    {
        assertEquals( 2, rootNode.getChildCount() );
    }

    @Test
    public void verifyChild0()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 );
        assertEquals( "A", child.getFullName() );
        assertEquals( "A", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild00()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "A.B", child.getFullName() );
        assertEquals( "B", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild000()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "A.B.C", child.getFullName() );
        assertEquals( "C", child.getNodeName() );
        assertEquals( 3, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild0000()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "A.B.C.1", child.getFullName() );
        assertEquals( "1", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild0001()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ).getChildAt( 1 );
        assertEquals( "A.B.C.2", child.getFullName() );
        assertEquals( "2", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild0002()
    {
        ChannelTreeNode child = rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ).getChildAt( 2 );
        assertEquals( "A.B.C.3", child.getFullName() );
        assertEquals( "3", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild1()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 );
        assertEquals( "B", child.getFullName() );
        assertEquals( "B", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode, child.getParent() );
    }

    @Test
    public void verifyChild10()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 );
        assertEquals( "B.C", child.getFullName() );
        assertEquals( "C", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ), child.getParent() );
    }

    @Test
    public void verifyChild100()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "B.C.D", child.getFullName() );
        assertEquals( "D", child.getNodeName() );
        assertEquals( 1, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 0 ), child.getParent() );
    }

    @Test
    public void verifyChild1000()
    {
        ChannelTreeNode child = rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 0 ).getChildAt( 0 );
        assertEquals( "B.C.D.E", child.getFullName() );
        assertEquals( "E", child.getNodeName() );
        assertEquals( 0, child.getChildCount() );
        assertEquals( rootNode.getChildAt( 1 ).getChildAt( 0 ).getChildAt( 0 ), child.getParent() );
    }
}
