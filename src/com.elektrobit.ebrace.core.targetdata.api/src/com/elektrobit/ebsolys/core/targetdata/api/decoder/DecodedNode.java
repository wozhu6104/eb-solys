/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.decoder;

import java.util.List;

/**
 * Represents a decoded node in a decoded tree. Classes which have to represent a decoded node have to implement this
 * interface.
 */
public interface DecodedNode
{
    List<DecodedNode> getChildren();

    String getName();

    void setName(String name);

    void setValue(String name);

    String getValue();

    DecodedNode createChildNode(String name);

    DecodedNode createChildNode(String name, String value);

    DecodedTree getParentTree();

    String getSummaryValue();

    DecodedNode getParentNode();

    void traverse(DecodedNode root, DecodedNodeVisitor visitHandler);

    void addToListOfChildTreeNodes(DecodedNode child);

}
