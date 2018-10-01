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

/**
 * A decoded tree is a tree which represents the value of a decoded runtime event as tree. Classes which represents the
 * tree structure of a decoded runtime event have to implement this interface.
 */
public interface DecodedTree
{
    DecodedNode getRootNode();

    void traverse(DecodedNodeVisitor callbackObj);

}
