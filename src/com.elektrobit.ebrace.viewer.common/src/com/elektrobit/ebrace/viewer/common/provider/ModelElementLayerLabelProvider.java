/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ModelElementLayerLabelProvider extends ColumnLabelProvider
{
    @Override
    public String getText(Object element)
    {
        if (element instanceof Tree)
        {
            return ((Tree)element).getRootNode().getTreeLevel().getName();
        }
        else if (element instanceof TreeNode)
        {
            return ((TreeNode)element).getTreeLevel().getName();
        }
        return null;
    }

}
