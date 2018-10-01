/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.model;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNodeVisitor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;

public class DefaultMessageDecodedTree implements DecodedTree
{
    DefaultMessageDecodedNode rootNode;

    public DefaultMessageDecodedTree(String name)
    {
        this.rootNode = new DefaultMessageDecodedNode( this, null, name, name );
    }

    @Override
    public DefaultMessageDecodedNode getRootNode()
    {
        return rootNode;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rootNode == null) ? 0 : rootNode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultMessageDecodedTree other = (DefaultMessageDecodedTree)obj;
        if (rootNode == null)
        {
            if (other.rootNode != null)
                return false;
        }
        else if (!rootNode.equals( other.rootNode ))
            return false;
        return true;
    }

    @Override
    public void traverse(DecodedNodeVisitor callbackObj)
    {
        // TODO Auto-generated method stub

    }
}
