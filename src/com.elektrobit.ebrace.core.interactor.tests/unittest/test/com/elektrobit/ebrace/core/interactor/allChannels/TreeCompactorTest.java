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

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.allChannels.TreeCompactor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class TreeCompactorTest
{
    private ChannelTreeNode rootNode;

    @Test
    public void testCompactOneChannel() throws Exception
    {
        rootNode = createTreeWithOneChannel();
        TreeCompactor sut = new TreeCompactor( rootNode );
        ChannelTreeNode compactedTreeRootNode = sut.compact();

        Assert.assertEquals( 1, compactedTreeRootNode.getChildCount() );
        Assert.assertEquals( "a.b.c", compactedTreeRootNode.getChildAt( 0 ).getFullName() );
        Assert.assertEquals( true, compactedTreeRootNode.getChildAt( 0 ).isLeaf() );
    }

    private ChannelTreeNode createTreeWithOneChannel()
    {
        ChannelTreeNode rootNode = new ChannelTreeNode( "root", null, null );
        ChannelTreeNode aNode = new ChannelTreeNode( "a", rootNode, "a" );
        rootNode.addChild( aNode );

        ChannelTreeNode abNode = new ChannelTreeNode( "b", aNode, "a.b" );
        aNode.addChild( abNode );

        ChannelTreeNode abcNode = new ChannelTreeNode( "c", abNode, "a.b.c" );
        abNode.addChild( abcNode );

        return rootNode;

    }

    @Test
    public void testCompactTwoChannels() throws Exception
    {
        rootNode = createTreeWithTwoChannels();
        TreeCompactor sut = new TreeCompactor( rootNode );
        ChannelTreeNode compactedTreeRootNode = sut.compact();

        Assert.assertEquals( 2, compactedTreeRootNode.getChildCount() );
        Assert.assertEquals( "a.b.c", compactedTreeRootNode.getChildAt( 0 ).getFullName() );
        Assert.assertEquals( true, compactedTreeRootNode.getChildAt( 0 ).isLeaf() );

        Assert.assertEquals( "x.y", compactedTreeRootNode.getChildAt( 1 ).getFullName() );
        Assert.assertEquals( false, compactedTreeRootNode.getChildAt( 1 ).isLeaf() );

        Assert.assertEquals( "x.y.1", compactedTreeRootNode.getChildAt( 1 ).getChildAt( 0 ).getFullName() );
        Assert.assertEquals( true, compactedTreeRootNode.getChildAt( 1 ).getChildAt( 0 ).isLeaf() );

        Assert.assertEquals( "x.y.2", compactedTreeRootNode.getChildAt( 1 ).getChildAt( 1 ).getFullName() );
        Assert.assertEquals( true, compactedTreeRootNode.getChildAt( 1 ).getChildAt( 1 ).isLeaf() );
    }

    private ChannelTreeNode createTreeWithTwoChannels()
    {
        ChannelTreeNode rootNode = new ChannelTreeNode( "root", null, null );
        ChannelTreeNode aNode = new ChannelTreeNode( "a", rootNode, "a" );
        rootNode.addChild( aNode );

        ChannelTreeNode abNode = new ChannelTreeNode( "b", aNode, "a.b" );
        aNode.addChild( abNode );

        ChannelTreeNode abcNode = new ChannelTreeNode( "c", abNode, "a.b.c" );
        abNode.addChild( abcNode );

        ChannelTreeNode xNode = new ChannelTreeNode( "x", rootNode, "x" );
        rootNode.addChild( xNode );

        ChannelTreeNode xyNode = new ChannelTreeNode( "y", aNode, "x.y" );
        xNode.addChild( xyNode );

        ChannelTreeNode xy1Node = new ChannelTreeNode( "1", abNode, "x.y.1" );
        xyNode.addChild( xy1Node );

        ChannelTreeNode xy2Node = new ChannelTreeNode( "2", abNode, "x.y.2" );
        xyNode.addChild( xy2Node );

        return rootNode;

    }

}
