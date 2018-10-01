/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

import junit.framework.Assert;

public class RuntimeEventAcceptorGetRuntimeEventsForModelElementsCTest extends RuntimeEventAcceptorAbstractCTest
{
    private final StructureAcceptor structureAcceptor;

    public RuntimeEventAcceptorGetRuntimeEventsForModelElementsCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
        structureAcceptor = getStructureAcceptor();
    }

    @Test
    public void getRuntimeEventsOfModelElements()
    {
        RuntimeEventChannel<Long> channelInteger = runtimeEventAcceptor.createRuntimeEventChannel( "Channel",
                                                                                                   Unit.COUNT,
                                                                                                   "" );
        Tree testTree = createTree();

        TreeNode node1 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node1" );
        TreeNode node2 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node2" );
        TreeNode node3 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node3" );

        runtimeEventAcceptor.acceptEvent( 999L, channelInteger, node1, 1L );
        runtimeEventAcceptor.acceptEvent( 1000L, channelInteger, node2, 2L );
        runtimeEventAcceptor.acceptEvent( 1001L, channelInteger, node1, 3L );
        runtimeEventAcceptor.acceptEvent( 1002L, channelInteger, node2, 4L );
        runtimeEventAcceptor.acceptEvent( 1003L, channelInteger, node3, 5L );
        runtimeEventAcceptor.acceptEvent( 1004L, channelInteger, node3, 6L );

        List<RuntimeEvent<?>> results = runtimeEventAcceptor
                .getRuntimeEventsOfModelElements( Arrays.asList( new ModelElement[]{node1, node3} ) );

        waitForCommit();
        Assert.assertEquals( 4, results.size() );

        for (RuntimeEvent<?> runtimeEvent : results)
        {
            ModelElement modelElement = runtimeEvent.getModelElement();
            Assert.assertTrue( modelElement.equals( node1 ) || modelElement.equals( node3 ) );
        }
    }

    private Tree createTree()
    {
        return structureAcceptor.addNewTreeInstance( "TestTreeName",
                                                     "TestTreeDescription",
                                                     "TestRootNodeName",
                                                     getTreeLevelDefinition() );
    }

    private List<TreeLevelDef> getTreeLevelDefinition()
    {
        List<TreeLevelDef> treeLevels = new ArrayList<TreeLevelDef>();
        treeLevels.add( structureAcceptor.createTreeLevel( "Level1Name", "", null ) );
        treeLevels.add( structureAcceptor.createTreeLevel( "Level2Name", "", null ) );
        return treeLevels;
    }

    private StructureAcceptor getStructureAcceptor()
    {
        return new GenericOSGIServiceTracker<StructureAcceptor>( StructureAcceptor.class ).getService();
    }
}
