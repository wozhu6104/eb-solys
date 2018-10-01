/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;

public class ComRelationAcceptorTest
{
    private ComRelationAcceptor comrelationAcceptor;
    private StructureAcceptor structureAcceptor;

    private Tree testTree;

    @Before
    public void setup()
    {
        structureAcceptor = getStructureAcceptor();
        comrelationAcceptor = getComRelationAcceptor();
    }

    @Test
    public void testComrelations()
    {
        createTree();
        createTestComRelations();

        Assert.assertEquals( 101, comrelationAcceptor.getComRelations().size() );

        iterateAndRemoveComrelationsFromAnotherThread();

    }

    private void createTree()
    {
        testTree = structureAcceptor.addNewTreeInstance( "TestTree",
                                                         "TestDescription",
                                                         "TestRootNode",
                                                         getTreeLevelDefinition() );
    }

    private List<TreeLevelDef> getTreeLevelDefinition()
    {
        List<TreeLevelDef> treeLevels = new ArrayList<TreeLevelDef>();
        treeLevels.add( structureAcceptor.createTreeLevel( "TestTreeLevel", "", null ) );
        treeLevels.add( structureAcceptor.createTreeLevel( "TestTreeLevel1", "", null ) );
        return treeLevels;
    }

    private void createTestComRelations()
    {
        int i = 100;

        while (i > 0)
        {
            comrelationAcceptor
                    .addComRelation( structureAcceptor.addTreeNode( testTree.getRootNode(), "testSender" + i ),
                                     structureAcceptor.addTreeNode( testTree.getRootNode(), "testReceiver" + i ),
                                     "" );
            i--;
        }
    }

    private void iterateAndRemoveComrelationsFromAnotherThread()
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                Set<ComRelation> comrelations = comrelationAcceptor.getComRelations();

                Iterator<ComRelation> iterator = comrelations.iterator();

                while (iterator.hasNext())
                {
                    ComRelation relation = iterator.next();
                    iterator.remove();
                    comrelationAcceptor.removeComRelation( relation );
                }
                Assert.assertEquals( 0, comrelationAcceptor.getComRelations().size() );
            }
        };
        thread.start();
    }

    private StructureAcceptor getStructureAcceptor()
    {
        return new GenericOSGIServiceTracker<StructureAcceptor>( StructureAcceptor.class ).getService();
    }

    private ComRelationAcceptor getComRelationAcceptor()
    {
        return new GenericOSGIServiceTracker<ComRelationAcceptor>( ComRelationAcceptor.class ).getService();
    }

}
