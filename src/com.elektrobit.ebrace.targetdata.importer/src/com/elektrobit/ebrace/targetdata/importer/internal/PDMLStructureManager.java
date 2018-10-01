/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal;

import java.util.Arrays;
import java.util.HashMap;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class PDMLStructureManager
{
    private final ComRelationAcceptor comRelationAcceptor;

    private final StructureAcceptor structureAcceptor;

    private HashMap<String, TreeNode> ecuLevelTreeNodes;
    private HashMap<String, TreeNode> serviceLevelTreeNodes;

    private Tree tree;

    public PDMLStructureManager(StructureAcceptor structureAcceptor, ComRelationAcceptor comRelationAcceptor)
    {
        this.structureAcceptor = structureAcceptor;
        this.comRelationAcceptor = comRelationAcceptor;

    }

    void initialize()
    {
        TreeLevelDef giopLevel = structureAcceptor.createTreeLevel( "GIOP", "", null );
        TreeLevelDef ecuLevel = structureAcceptor.createTreeLevel( "ECU", "", null );
        TreeLevelDef serviceLevel = structureAcceptor.createTreeLevel( "GIOP-Service", "", null );

        tree = structureAcceptor.addNewTreeInstance( "GIOP-Tree",
                                                     "",
                                                     "GIOP",
                                                     Arrays.asList( giopLevel, ecuLevel, serviceLevel ) );
        ecuLevelTreeNodes = new HashMap<String, TreeNode>();
        serviceLevelTreeNodes = new HashMap<String, TreeNode>();
    }

    public ComRelation createComRelation(String ecuLevelSender, String ecuLevelReceiver, String serviceLevelSender,
            String serviceLevelReceiver)
    {
        TreeNode nodeEcuSender = getOrCreateECUTreeNode( ecuLevelSender );
        TreeNode nodeEcuReceiver = getOrCreateECUTreeNode( ecuLevelReceiver );
        TreeNode nodeServiceSender = getOrCreateServiceTreeNode( nodeEcuSender, serviceLevelSender );
        TreeNode nodeServiceReceiver = getOrCreateServiceTreeNode( nodeEcuReceiver, serviceLevelReceiver );

        return getComRelation( nodeServiceSender, nodeServiceReceiver );
    }

    private ComRelation getComRelation(TreeNode sourceNode, TreeNode destinationNode)
    {
        ComRelation comRelation = null;
        comRelation = getExistingComRelationBetweenNodes( sourceNode, destinationNode );
        if (comRelation == null)
            comRelation = comRelationAcceptor.addComRelation( sourceNode, destinationNode, "" );
        return comRelation;
    }

    private ComRelation getExistingComRelationBetweenNodes(TreeNode sourceNode, TreeNode destinationNode)
    {
        ComRelation[] existingComRelations = comRelationAcceptor.getComRelations( sourceNode );
        for (ComRelation cr : existingComRelations)
        {
            if (cr.getReceiver().equals( destinationNode ))
            {
                return cr;
            }
        }
        return null;
    }

    private TreeNode getOrCreateECUTreeNode(String name)
    {
        TreeNode ecuTreeNode = null;
        if (!ecuLevelTreeNodes.containsKey( name ))
        {
            ecuTreeNode = structureAcceptor.addTreeNode( tree.getRootNode(), name );
            ecuLevelTreeNodes.put( name, ecuTreeNode );
        }
        else
        {
            ecuTreeNode = ecuLevelTreeNodes.get( name );
        }
        return ecuTreeNode;
    }

    private TreeNode getOrCreateServiceTreeNode(TreeNode parent, String name)
    {
        TreeNode serviceTreeNode = null;
        if (!serviceLevelTreeNodes.containsKey( name ))
        {
            serviceTreeNode = structureAcceptor.addTreeNode( parent, name );
            serviceLevelTreeNodes.put( name, serviceTreeNode );
        }
        else
        {
            serviceTreeNode = serviceLevelTreeNodes.get( name );
        }
        return serviceTreeNode;
    }
}
