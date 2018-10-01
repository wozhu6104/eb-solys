/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructuredNodeNameToNodeTree<T>
{
    private final Map<String, T> uniqueNodes = new HashMap<>();
    private final NodeAgent<T> agent;
    private final T root;

    public StructuredNodeNameToNodeTree(NodeAgent<T> agent, T root)
    {
        this.agent = agent;
        this.root = root;
    }

    public T getOrCreate(List<String> list)
    {
        if (list.size() == 0)
        {
            return root;
        }
        else
        {
            String join = String.join( ".", list );
            if (uniqueNodes.containsKey( join ))
            {
                return uniqueNodes.get( join );
            }
            else
            {
                T parent = getOrCreate( list.subList( 0, list.size() - 1 ) );
                T newNode = agent.createNodeObject( join, parent );
                uniqueNodes.put( join, newNode );
                return newNode;
            }
        }
    }

    public int getNrOfUniqueNodes()
    {
        return uniqueNodes.size();
    }
}
