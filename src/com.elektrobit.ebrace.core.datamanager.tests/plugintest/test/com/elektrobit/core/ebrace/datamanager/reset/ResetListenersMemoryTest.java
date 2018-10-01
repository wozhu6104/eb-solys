/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.reset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.dev.test.util.memory.CyclicMemoryChecker;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ResetListenersMemoryTest
{
    private ResetNotifier resetNotfier;

    @Before
    public void setup()
    {
        resetNotfier = new GenericOSGIServiceTracker<ResetNotifier>( ResetNotifier.class ).getService();
    }

    @Test
    public void isHeapSizeofRuntimeEventAcceptorNearlyConstantAfterReset() throws Exception
    {
        final RuntimeEventAcceptor runtimeEventProvider = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();

        final RuntimeEventChannel<String> runtimeEventChannel = runtimeEventProvider
                .createOrGetRuntimeEventChannel( "test.channel", Unit.TEXT, "" );

        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {
                for (int i = 0; i < 100000; i++)
                {
                    runtimeEventProvider.acceptEvent( System.currentTimeMillis(),
                                                      runtimeEventChannel,
                                                      null,
                                                      "Data" + i );
                }

                resetNotfier.performReset();
            }
        };

        assertThat( new CyclicMemoryChecker( true ).heapSizeStdDevInPercent( testCode ), lessThan( 0.01 ) );
    }

    @Test
    public void isHeapSizeofStructureAcceptorNearlyConstantAfterReset() throws Exception
    {
        final StructureAcceptor structureAcceptor = new GenericOSGIServiceTracker<StructureAcceptor>( StructureAcceptor.class )
                .getService();

        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {
                TreeLevelDef level1 = structureAcceptor.createTreeLevel( "L1", "", null );
                TreeLevelDef level2 = structureAcceptor.createTreeLevel( "L2", "", null );
                final Tree testTree = structureAcceptor.addNewTreeInstance( "TestTree",
                                                                            "",
                                                                            "RN",
                                                                            Arrays.asList( level1, level2 ) );

                for (int i = 0; i < 1000; i++)
                {
                    TreeNode treeNode = structureAcceptor.addTreeNode( testTree.getRootNode(), "N" + i );
                    structureAcceptor.addStructureProperty( treeNode, "ID", "" + i, "DummyNode" );
                }

                resetNotfier.performReset();
            }
        };

        Assert.assertTrue( new CyclicMemoryChecker( true ).isHeapSizeStable( testCode ) );
    }

    @Test
    public void isHeapSizeofComRelationAcceptorNearlyConstantAfterReset() throws Exception
    {
        final StructureAcceptor structureAcceptor = new GenericOSGIServiceTracker<StructureAcceptor>( StructureAcceptor.class )
                .getService();
        final ComRelationAcceptor comRelationAcceptor = new GenericOSGIServiceTracker<ComRelationAcceptor>( ComRelationAcceptor.class )
                .getService();

        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {
                TreeLevelDef level1 = structureAcceptor.createTreeLevel( "L1", "", null );
                TreeLevelDef level2 = structureAcceptor.createTreeLevel( "L2", "", null );
                final Tree testTree = structureAcceptor.addNewTreeInstance( "TestTree",
                                                                            "",
                                                                            "RN",
                                                                            Arrays.asList( level1, level2 ) );

                for (int i = 1; i < 1000; i = i + 2)
                {
                    TreeNode treeNodeFirst = structureAcceptor.addTreeNode( testTree.getRootNode(), "N" + (i - 1) );
                    TreeNode treeNodeSecond = structureAcceptor.addTreeNode( testTree.getRootNode(), "N" + i );
                    comRelationAcceptor.addComRelation( treeNodeFirst,
                                                        treeNodeSecond,
                                                        "N" + (i - 1) + "-->" + "N" + i );
                }

                resetNotfier.performReset();
            }
        };

        Assert.assertTrue( new CyclicMemoryChecker( true ).isHeapSizeStable( testCode ) );
    }
}
