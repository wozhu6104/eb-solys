/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.impl;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedNode;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

public class DecodedDBusTreeMapCleaner
{

    public static DecodedTree copyTree(DecodedTree srcTree)
    {
        GenericDecodedTree tree = new GenericDecodedTree( srcTree.getRootNode().getName() );
        copyNode( srcTree.getRootNode(), tree.getRootNode(), false );
        return tree;
    }

    private static void copyNode(DecodedNode srcNode, GenericDecodedNode destNode, boolean correctMapsValues)
    {
        for (DecodedNode nextNodeToCopy : srcNode.getChildren())
        {
            GenericDecodedNode copiedNode = (GenericDecodedNode)destNode.createChildNode( nextNodeToCopy.getName(),
                                                                                          nextNodeToCopy.getValue() );
            if (!correctMapsValues)
            {
                copyNode( nextNodeToCopy, copiedNode, correctMapsValues );
            }
            else
            {
                if (nextNodeToCopy.getName().equals( "DBUS_MSG_PARAM_TYPE_DICT_ENTRY" ))
                {
                    DecodedNode dictEntryKeyNode = getDictEntryKeyNode( nextNodeToCopy );
                    DecodedNode variantNode = getVariantNode( nextNodeToCopy );

                    // Unknown Franca
                    if (dictEntryKeyNode == null || variantNode == null)
                    {
                        copyNode( nextNodeToCopy, copiedNode, correctMapsValues );
                    }
                    // Franca 0.9 version
                    // Expecting maps as INT comparable key and STRUCT(BYTE, VARIANT).
                    else
                    {
                        // copy key
                        copiedNode.createChildNode( dictEntryKeyNode.getName(), dictEntryKeyNode.getValue() );

                        // copy value
                        GenericDecodedNode copiedVariantNode = (GenericDecodedNode)copiedNode
                                .createChildNode( variantNode.getName(), variantNode.getValue() );

                        copyNode( variantNode, copiedVariantNode, correctMapsValues );
                    }
                }
                else
                {
                    copyNode( nextNodeToCopy, copiedNode, correctMapsValues );
                }
            }
        }
    }

    private static DecodedNode getVariantNode(DecodedNode nextNodeToCopy)
    {
        if ((nextNodeToCopy.getChildren().size() >= 2)
                && (nextNodeToCopy.getChildren().get( 1 ).getChildren().size() >= 2))
        {
            return nextNodeToCopy.getChildren().get( 1 ).getChildren().get( 1 );
        }
        else
        {
            return null;
        }
    }

    private static DecodedNode getDictEntryKeyNode(DecodedNode nextNodeToCopy)
    {
        if (!nextNodeToCopy.getChildren().isEmpty())
        {
            return nextNodeToCopy.getChildren().get( 0 );
        }
        else
        {
            return null;
        }
    }

    /**
     * This method is required to change the Values of Map to decode it with franca.
     */
    public static DecodedTree copyTreeAndCorrectMaps(DecodedTree srcTree)
    {
        GenericDecodedTree tree = new GenericDecodedTree( srcTree.getRootNode().getName() );
        copyNode( srcTree.getRootNode(), tree.getRootNode(), true );
        return tree;
    }
}
