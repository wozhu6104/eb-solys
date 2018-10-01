/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class TypeColumnLabelProvider extends ColumnLabelProvider
{

    @Override
    public String getText(Object element)
    {
        if (element instanceof TreeNode)
        {
            return ((TreeNode)element).getTreeLevel().getName();
        }
        else if (element instanceof Tree)
        {
            TreeNode rootNode = ((Tree)element).getRootNode();
            return getText( rootNode );
        }

        return "";
    }
}
