/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService;

public class CheckBoxColumnEditingSupport extends EditingSupport
{
    private static final Logger LOG = Logger.getLogger( CheckBoxColumnEditingSupport.class );
    private final TreeNodeCheckStateService treeNodeCheckStateService = new GenericOSGIServiceTracker<TreeNodeCheckStateService>( TreeNodeCheckStateService.class )
            .getService();
    private final TreeViewer treeViewer;

    public CheckBoxColumnEditingSupport(TreeViewer viewer)
    {
        super( viewer );
        treeViewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element)
    {
        CellEditor ce = new CellEditor()
        {
            TreeNode treeNode; // TODO remove

            @Override
            protected void doSetValue(Object value)
            {
                Assert.isTrue( value instanceof TreeNode );
                this.treeNode = (TreeNode)value;
            }

            @Override
            protected void doSetFocus()
            {
            }

            @Override
            protected Object doGetValue()
            {
                return treeNode;
            }

            @Override
            protected Control createControl(Composite parent)
            {
                return treeViewer.getControl();
            }

            @Override
            public void activate()
            {
                fireApplyEditorValue();
            }
        };
        return ce;
    }

    @Override
    protected boolean canEdit(Object element)
    {
        return true;
    }

    @Override
    protected Object getValue(Object element)
    {
        if (element instanceof TreeNode)
        {
            TreeNode treeNode = (TreeNode)element;
            return treeNode;
        }
        else if (element instanceof Tree)
        {
            Tree tree = (Tree)element;
            return tree.getRootNode();
        }
        LOG.warn( "getValue: Unexpected type of element " + element.getClass() );

        return null;
    }

    @Override
    protected void setValue(Object element, Object value)
    {
        if (element instanceof TreeNode)
        {
            TreeNode treeNode = (TreeNode)element;
            treeNodeCheckStateService.toggleCheckState( treeNode );
        }
        else if (element instanceof Tree)
        {
            Tree tree = (Tree)element;
            setValue( tree.getRootNode(), value );
        }
        else
            LOG.warn( "setValue: Unexpected type of element " + element.getClass() );
    }
}
