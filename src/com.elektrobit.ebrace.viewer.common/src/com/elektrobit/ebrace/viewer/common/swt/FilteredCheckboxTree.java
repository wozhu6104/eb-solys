/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class FilteredCheckboxTree extends FilteredTree
{
    public FilteredCheckboxTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook)
    {
        super( parent, treeStyle, filter, useNewLook );

    }

    @Override
    protected TreeViewer doCreateTreeViewer(Composite parent, int style)
    {
        return new CheckboxTreeViewer( parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION );
    }
}
