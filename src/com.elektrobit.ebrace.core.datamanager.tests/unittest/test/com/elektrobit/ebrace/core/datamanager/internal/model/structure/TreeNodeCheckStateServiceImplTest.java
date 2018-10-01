/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.model.structure;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeDefImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeLevelDefImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeNodeCheckStateServiceImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.TreeNodeImpl;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckStateListener;

import junit.framework.Assert;

public class TreeNodeCheckStateServiceImplTest
{
    private Tree testTree;

    private TreeNodeImpl rootNodeA;
    private TreeNodeImpl nodeB;
    private TreeNodeImpl nodeC;
    private TreeNodeImpl nodeD;
    private TreeNodeImpl nodeE;

    private TreeNodeCheckStateServiceImpl sutTreeNodeCheckStateService;

    //@formatter:off
    /**
     * Creates following test tree
     * 
     * rootNodeA
     *     |
     *     |_nodeB
     *     |
     *     |_nodeC
     *          |
     *          |_nodeD
     *          |
     *          |_nodeE
     *          |
     *          |_(nodeF)
     *     |
     *     |_(nodeG)
     */
    //@formatter:on
    @Before
    public void createTestTree()
    {
        createTree();
        createNodes();
        setupSut();
    }

    private void createTree()
    {
        TreeLevelDef treeLevel1 = new TreeLevelDefImpl( "level 1", "desc 1", "path" );
        TreeLevelDef treeLevel2 = new TreeLevelDefImpl( "level 2", "desc 2", "path" );
        TreeLevelDef treeLevel3 = new TreeLevelDefImpl( "level 3", "desc 3", "path" );

        TreeDefImpl treeDef = new TreeDefImpl( Arrays
                .asList( new TreeLevelDef[]{treeLevel1, treeLevel2, treeLevel3} ) );
        testTree = new TreeImpl( "test tree", "desc", "rootNode", treeDef );
    }

    private void createNodes()
    {
        rootNodeA = (TreeNodeImpl)testTree.getRootNode();
        nodeB = (TreeNodeImpl)rootNodeA.addTreeNode( new TreeNodeImpl( "b", testTree ) );
        nodeC = (TreeNodeImpl)rootNodeA.addTreeNode( new TreeNodeImpl( "c", testTree ) );

        nodeD = (TreeNodeImpl)nodeC.addTreeNode( new TreeNodeImpl( "d", testTree ) );
        nodeE = (TreeNodeImpl)nodeC.addTreeNode( new TreeNodeImpl( "e", testTree ) );
    }

    private void setupSut()
    {
        sutTreeNodeCheckStateService = new TreeNodeCheckStateServiceImpl();
        StructureProvider mockedStructureProvider = Mockito.mock( StructureProvider.class );
        sutTreeNodeCheckStateService.bind( mockedStructureProvider );
        sutTreeNodeCheckStateService.activate();
        Mockito.verify( mockedStructureProvider ).addStructureModificationListener( sutTreeNodeCheckStateService );
    }

    @Test
    public void testCheckUpdate() throws Exception
    {
        TreeNodesCheckStateListener mockedListener = Mockito.mock( TreeNodesCheckStateListener.class );
        sutTreeNodeCheckStateService.registerListener( mockedListener );

        sutTreeNodeCheckStateService.toggleCheckState( nodeC );
        Mockito.verify( mockedListener, Mockito.times( 1 ) ).onCheckStateChanged();
        TreeNodesCheckState checkStates = sutTreeNodeCheckStateService.getCheckStates();

        Assert.assertEquals( CHECKED_STATE.PARTIALLY_CHECKED, checkStates.getNodeCheckState( rootNodeA ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeB ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeC ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeD ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeE ) );
    }

    @Test
    public void testUncheckUpdate() throws Exception
    {
        TreeNodesCheckStateListener mockedListener = Mockito.mock( TreeNodesCheckStateListener.class );
        sutTreeNodeCheckStateService.registerListener( mockedListener );

        sutTreeNodeCheckStateService.toggleCheckState( nodeC );
        sutTreeNodeCheckStateService.toggleCheckState( nodeD );

        Mockito.verify( mockedListener, Mockito.times( 2 ) ).onCheckStateChanged();
        TreeNodesCheckState checkStates = sutTreeNodeCheckStateService.getCheckStates();

        Assert.assertEquals( CHECKED_STATE.PARTIALLY_CHECKED, checkStates.getNodeCheckState( rootNodeA ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeB ) );
        Assert.assertEquals( CHECKED_STATE.PARTIALLY_CHECKED, checkStates.getNodeCheckState( nodeC ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeD ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeE ) );
    }

    @Test
    public void testNoUpdateAfterUnregister() throws Exception
    {
        TreeNodesCheckStateListener mockedListener = Mockito.mock( TreeNodesCheckStateListener.class );
        sutTreeNodeCheckStateService.registerListener( mockedListener );

        sutTreeNodeCheckStateService.unregisterListener( mockedListener );
        sutTreeNodeCheckStateService.toggleCheckState( nodeD );

        Mockito.verifyNoMoreInteractions( mockedListener );
    }

    @Test
    public void testNodeAdded() throws Exception
    {
        sutTreeNodeCheckStateService.toggleCheckState( nodeC );

        TreeNodeImpl nodeF = (TreeNodeImpl)nodeC.addTreeNode( new TreeNodeImpl( "f", testTree ) );
        sutTreeNodeCheckStateService.added( nodeF );

        TreeNodeImpl nodeG = (TreeNodeImpl)rootNodeA.addTreeNode( new TreeNodeImpl( "g", testTree ) );
        sutTreeNodeCheckStateService.added( nodeG );

        TreeNodesCheckState checkStates = sutTreeNodeCheckStateService.getCheckStates();
        Assert.assertEquals( CHECKED_STATE.PARTIALLY_CHECKED, checkStates.getNodeCheckState( rootNodeA ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeB ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeC ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeD ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeE ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeF ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeG ) );
    }

    @Test
    public void testCheckListOfNodes() throws Exception
    {
        TreeNodesCheckStateListener mockedListener = Mockito.mock( TreeNodesCheckStateListener.class );
        sutTreeNodeCheckStateService.registerListener( mockedListener );

        sutTreeNodeCheckStateService.checkTreeNodes( Arrays.asList( new TreeNode[]{nodeD, nodeC} ) );
        Mockito.verify( mockedListener, Mockito.times( 1 ) ).onCheckStateChanged();
        TreeNodesCheckState checkStates = sutTreeNodeCheckStateService.getCheckStates();

        Assert.assertEquals( CHECKED_STATE.PARTIALLY_CHECKED, checkStates.getNodeCheckState( rootNodeA ) );
        Assert.assertEquals( CHECKED_STATE.UNCHECKED, checkStates.getNodeCheckState( nodeB ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeC ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeD ) );
        Assert.assertEquals( CHECKED_STATE.CHECKED, checkStates.getNodeCheckState( nodeE ) );
    }
}
