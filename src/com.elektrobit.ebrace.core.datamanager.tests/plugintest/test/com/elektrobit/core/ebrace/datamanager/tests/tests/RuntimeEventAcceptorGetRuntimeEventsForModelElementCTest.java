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
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

import junit.framework.Assert;

public class RuntimeEventAcceptorGetRuntimeEventsForModelElementCTest extends RuntimeEventAcceptorAbstractCTest
{
    private final StructureAcceptor structureAcceptor;

    public RuntimeEventAcceptorGetRuntimeEventsForModelElementCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
        structureAcceptor = getStructureAcceptor();
    }

    @Test
    public void getRuntimeEventsOfModelElement()
    {
        RuntimeEventChannel<Long> channelCount = runtimeEventAcceptor.createRuntimeEventChannel( "Channel",
                                                                                                 Unit.COUNT,
                                                                                                 "" );
        Tree testTree = createTree();

        TreeNode node1 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node1" );
        TreeNode node2 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node1" );

        runtimeEventAcceptor.acceptEvent( 999L, channelCount, node1, 1L );
        runtimeEventAcceptor.acceptEvent( 1000L, channelCount, node2, 2L );
        runtimeEventAcceptor.acceptEvent( 1001L, channelCount, node1, 3L );
        runtimeEventAcceptor.acceptEvent( 1002L, channelCount, node2, 4L );

        List<RuntimeEvent<?>> results = runtimeEventAcceptor.getRuntimeEventsOfModelElement( node1 );

        waitForCommit();
        Assert.assertEquals( 2, results.size() );

        for (RuntimeEvent<?> runtimeEvent : results)
        {
            Assert.assertTrue( runtimeEvent.getModelElement().equals( node1 ) );
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
