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

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class RuntimeEventAcceptorGetRuntimeEventChannelsForModelElementCTest extends RuntimeEventAcceptorAbstractCTest
{
    private final StructureAcceptor structureAcceptor;

    public RuntimeEventAcceptorGetRuntimeEventChannelsForModelElementCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
        structureAcceptor = getStructureAcceptor();
    }

    @Test
    public void getRuntimeEventChannelsOfModelElement()
    {
        RuntimeEventChannel<Long> channel1Integer = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                                    Unit.COUNT,
                                                                                                    "" );

        RuntimeEventChannel<Long> channel2Integer = runtimeEventAcceptor.createRuntimeEventChannel( "Channel2",
                                                                                                    Unit.COUNT,
                                                                                                    "" );
        Tree testTree = createTree();

        TreeNode modelElement1 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node1" );
        TreeNode modelElement2 = structureAcceptor.addTreeNode( testTree.getRootNode(), "Node1" );

        runtimeEventAcceptor.acceptEvent( 999L, channel1Integer, modelElement1, 1L );
        runtimeEventAcceptor.acceptEvent( 1000L, channel2Integer, modelElement2, 2L );
        runtimeEventAcceptor.acceptEvent( 1001L, channel2Integer, modelElement2, 2L );
        runtimeEventAcceptor.acceptEvent( 1001L, channel2Integer, modelElement1, 3L );
        runtimeEventAcceptor.acceptEvent( 1002L, channel2Integer, ModelElement.NULL_MODEL_ELEMENT, 4L );

        waitForCommit();

        List<RuntimeEventChannel<?>> results1 = runtimeEventAcceptor
                .getRuntimeEventChannelsForModelElement( modelElement1 );

        Assert.assertTrue( results1.contains( channel1Integer ) );
        Assert.assertTrue( results1.contains( channel2Integer ) );
        Assert.assertEquals( results1.size(), 2 );

        List<RuntimeEventChannel<?>> results2 = runtimeEventAcceptor
                .getRuntimeEventChannelsForModelElement( modelElement2 );
        Assert.assertTrue( results2.contains( channel2Integer ) );
        Assert.assertEquals( results2.size(), 1 );

        List<RuntimeEventChannel<?>> results3 = runtimeEventAcceptor
                .getRuntimeEventChannelsForModelElement( ModelElement.NULL_MODEL_ELEMENT );
        Assert.assertTrue( results3.contains( channel2Integer ) );
        Assert.assertEquals( results3.size(), 1 );
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
