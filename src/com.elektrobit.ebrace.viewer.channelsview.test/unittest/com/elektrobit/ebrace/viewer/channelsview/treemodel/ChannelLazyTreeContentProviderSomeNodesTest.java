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

import static com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelLazyTreeContentProviderTestSuite.createFixture;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ChannelLazyTreeContentProviderSomeNodesTest
{
    private ChannelLazyTreeContentProvider underTest;
    private TreeViewer viewer;

    private ChannelTreeNode rootNode;

    private ChannelTreeNode cpuNode;
    private ChannelTreeNode cpuSystemNode;

    private ChannelTreeNode memNode;
    private ChannelTreeNode mem1Node;
    private ChannelTreeNode mem2Node;
    private ChannelTreeNode mem21Node;

    private ChannelTreeNode traceNode;

    @Before
    public void setUp()
    {
        viewer = mock( TreeViewer.class );
        underTest = new ChannelLazyTreeContentProvider( viewer );

        rootNode = createFixture();

        cpuNode = rootNode.getChildAt( 0 );
        cpuSystemNode = cpuNode.getChildAt( 0 );

        memNode = rootNode.getChildAt( 1 );
        mem1Node = memNode.getChildAt( 0 );
        mem2Node = memNode.getChildAt( 1 );
        mem21Node = mem2Node.getChildAt( 0 );

        traceNode = rootNode.getChildAt( 2 );
    }

    @Test
    public void verifyUpdateChildCountForRootNode()
    {
        underTest.updateChildCount( rootNode, -1 );
        verify( viewer ).setChildCount( rootNode, 3 );
    }

    @Test
    public void callUpdateElementWithRootNodeAndIndex0()
    {
        underTest.updateElement( rootNode, 0 );

        verify( viewer ).replace( rootNode, 0, cpuNode );
        verify( viewer ).setChildCount( cpuNode, 1 );
    }

    @Test
    public void callUpdateElementWithRootNodeAndIndex1()
    {
        underTest.updateElement( rootNode, 1 );

        verify( viewer ).replace( rootNode, 1, memNode );
        verify( viewer ).setChildCount( memNode, 2 );
    }

    @Test
    public void callUpdateElementWithRootNodeAndIndex2()
    {
        underTest.updateElement( rootNode, 2 );

        verify( viewer ).replace( rootNode, 2, traceNode );
        verify( viewer ).setChildCount( traceNode, 0 );
    }

    @Test
    public void verifyUpdateChildCountForCpuNode()
    {
        underTest.updateChildCount( cpuNode, -1 );
        verify( viewer ).setChildCount( cpuNode, 1 );
    }

    @Test
    public void callUpdateElementWithCpuNodeNodeAndIndex0()
    {
        underTest.updateElement( cpuNode, 0 );

        verify( viewer ).replace( cpuNode, 0, cpuSystemNode );
        verify( viewer ).setChildCount( cpuSystemNode, 0 );
    }

    @Test
    public void verifyUpdateChildCountForCpuSystemNode()
    {
        underTest.updateChildCount( cpuSystemNode, -1 );
        verify( viewer ).setChildCount( cpuSystemNode, 0 );
    }

    @Test
    public void verifyUpdateChildCountForMemNode()
    {
        underTest.updateChildCount( memNode, -1 );
        verify( viewer ).setChildCount( memNode, 2 );
    }

    @Test
    public void callUpdateElementWithMemNodeNodeAndIndex0()
    {
        underTest.updateElement( memNode, 0 );

        verify( viewer ).replace( memNode, 0, mem1Node );
        verify( viewer ).setChildCount( mem1Node, 0 );
    }

    @Test
    public void callUpdateElementWithMemNodeNodeAndIndex1()
    {
        underTest.updateElement( memNode, 1 );

        verify( viewer ).replace( memNode, 1, mem2Node );
        verify( viewer ).setChildCount( mem2Node, 1 );
    }

    @Test
    public void verifyUpdateChildCountForMem1Node()
    {
        underTest.updateChildCount( mem1Node, -1 );
        verify( viewer ).setChildCount( mem1Node, 0 );
    }

    @Test
    public void verifyUpdateChildCountForMem2Node()
    {
        underTest.updateChildCount( mem2Node, -1 );
        verify( viewer ).setChildCount( mem2Node, 1 );
    }

    @Test
    public void callUpdateElementWithMem2NodeNodeAndIndex0()
    {
        underTest.updateElement( mem2Node, 0 );

        verify( viewer ).replace( mem2Node, 0, mem21Node );
        verify( viewer ).setChildCount( mem21Node, 0 );
    }

    @Test
    public void verifyUpdateChildCountForTraceNode()
    {
        underTest.updateChildCount( traceNode, -1 );
        verify( viewer ).setChildCount( traceNode, 0 );
    }

}
