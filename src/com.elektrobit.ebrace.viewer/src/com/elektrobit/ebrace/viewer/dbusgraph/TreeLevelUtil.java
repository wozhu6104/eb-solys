/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;

public class TreeLevelUtil
{
    private List<Tree> allTrees;

    public void setTreeList(List<Tree> allTrees)
    {
        this.allTrees = allTrees;
    }

    List<TreeLevelDef> getUpperTreeLevelsOfTreeLevel(String treeLevelName)
    {
        Tree tree = getTreeByLevelName( treeLevelName );
        if (tree == null)
        {
            return Collections.emptyList();
        }
        return getTreeLevelsHigherThan( treeLevelName, tree );
    }

    private Tree getTreeByLevelName(String baseTreeLevelName)
    {
        for (Tree tree : allTrees)
        {
            for (TreeLevelDef levelDef : tree.getTreeDef().getTreeLevelDefs())
            {
                if (levelDef.getName().equals( baseTreeLevelName ))
                {
                    return tree;
                }
            }
        }
        return null;
    }

    private List<TreeLevelDef> getTreeLevelsHigherThan(String treeLevelName, Tree tree)
    {
        List<TreeLevelDef> upperTreeLevels = new ArrayList<TreeLevelDef>();
        for (TreeLevelDef levelDef : tree.getTreeDef().getTreeLevelDefs())
        {
            if (levelDef.getName().equals( treeLevelName ))
            {
                return upperTreeLevels;
            }
            else
            {
                upperTreeLevels.add( levelDef );
            }
        }
        return upperTreeLevels;
    }

    List<TreeLevelDef> extractTreeLevels()
    {
        List<TreeLevelDef> newTreeLevelDefs = new ArrayList<TreeLevelDef>();
        for (Tree tree : allTrees)
        {
            List<TreeLevelDef> levelDefs = tree.getTreeDef().getTreeLevelDefs();
            newTreeLevelDefs.addAll( levelDefs );
        }
        return newTreeLevelDefs;
    }

}
