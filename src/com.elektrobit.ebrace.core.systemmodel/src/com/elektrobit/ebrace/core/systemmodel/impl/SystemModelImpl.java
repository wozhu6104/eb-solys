/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.core.systemmodel.api.SystemModel;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelEdge;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelNode;

import lombok.Data;

@Data
public class SystemModelImpl implements SystemModel
{
    private final List<SystemModelNode> nodes = new ArrayList<>();
    private final List<SystemModelEdge> edges = new ArrayList<>();

    @Override
    public SystemModelNode addNode(SystemModelNode node)
    {
        nodes.add( node );
        return node;
    }

    @Override
    public SystemModelEdge addEdge(SystemModelEdge edge)
    {
        edges.add( edge );
        return edge;
    }

}
