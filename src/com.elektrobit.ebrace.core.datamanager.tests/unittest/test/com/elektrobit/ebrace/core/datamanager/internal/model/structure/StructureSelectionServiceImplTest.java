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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.internal.model.structure.StructureSelectionServiceImpl;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

import junit.framework.Assert;

public class StructureSelectionServiceImplTest
{
    @Test
    public void testSetNodes() throws Exception
    {
        StructureSelectionServiceImpl sut = new StructureSelectionServiceImpl();
        TreeNode node = Mockito.mock( TreeNode.class );

        List<TreeNode> testList = new ArrayList<TreeNode>();
        testList.add( node );

        StructureSelectionListener mockedListener = Mockito.mock( StructureSelectionListener.class );
        sut.registerListener( mockedListener );

        sut.setNodesSelected( testList );
        Mockito.verify( mockedListener ).onNodesSelected( testList );

        sut.clearSelection();
        Mockito.verify( mockedListener ).onSelectionCleared();
        Mockito.verifyNoMoreInteractions( mockedListener );

        sut.unregisterListener( mockedListener );
        testList.add( node );
        sut.setNodesSelected( testList );
        Mockito.verifyNoMoreInteractions( mockedListener );
    }

    @Test
    public void testSetComrelations() throws Exception
    {
        StructureSelectionServiceImpl sut = new StructureSelectionServiceImpl();
        ComRelation comRelation = Mockito.mock( ComRelation.class );

        List<ComRelation> testList = new ArrayList<ComRelation>();
        testList.add( comRelation );

        StructureSelectionListener mockedListener = Mockito.mock( StructureSelectionListener.class );
        sut.registerListener( mockedListener );

        sut.setComRelationsSelected( testList );
        Mockito.verify( mockedListener ).onComRelationsSelected( testList );

        sut.clearSelection();
        Mockito.verify( mockedListener ).onSelectionCleared();
        Mockito.verifyNoMoreInteractions( mockedListener );

        sut.unregisterListener( mockedListener );
        testList.add( comRelation );
        sut.setComRelationsSelected( testList );
        Mockito.verifyNoMoreInteractions( mockedListener );
    }

    @Test
    public void testGetSelected() throws Exception
    {
        StructureSelectionServiceImpl sut = new StructureSelectionServiceImpl();

        TreeNode node = Mockito.mock( TreeNode.class );
        List<TreeNode> nodeList = new ArrayList<TreeNode>();
        nodeList.add( node );

        ComRelation comRelation = Mockito.mock( ComRelation.class );
        List<ComRelation> comRelationList = new ArrayList<ComRelation>();
        comRelationList.add( comRelation );

        sut.setNodesSelected( nodeList );
        Assert.assertEquals( nodeList, sut.getSelectedNodes() );
        Assert.assertTrue( sut.getSelectedComRelations().isEmpty() );

        sut.setComRelationsSelected( comRelationList );
        Assert.assertEquals( comRelationList, sut.getSelectedComRelations() );
        Assert.assertTrue( sut.getSelectedNodes().isEmpty() );
    }
}
