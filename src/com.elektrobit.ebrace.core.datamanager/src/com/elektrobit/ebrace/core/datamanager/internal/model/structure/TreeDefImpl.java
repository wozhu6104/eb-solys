/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.model.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;

/**
 * Data container for tree level definition.
 * 
 * @author rage2903
 * @version 11.07
 */
public class TreeDefImpl implements TreeDef, Serializable
{
    private static final long serialVersionUID = -7484377311654314701L;

    private final List<TreeLevelDef> m_treeLevelDefinition;

    public TreeDefImpl(List<TreeLevelDef> treeLevelDefinition)
    {
        m_treeLevelDefinition = new ArrayList<TreeLevelDef>( treeLevelDefinition );
    }

    @Override
    public List<TreeLevelDef> getTreeLevelDefs()
    {
        return m_treeLevelDefinition;
    }

    /**
     * Appends a new {@link TreeLevelDef} to the {@link TreeDef}.
     * 
     * @param treeLevelDef
     *            The new {@link TreeLevelDef} which should be appended.
     * @return true if appending was successful, else false.
     */
    public boolean appendTreeLevelDef(TreeLevelDef treeLevelDef)
    {
        return m_treeLevelDefinition.add( treeLevelDef );
    }

    @Override
    public String toString()
    {
        return m_treeLevelDefinition.toString();
    }
}
