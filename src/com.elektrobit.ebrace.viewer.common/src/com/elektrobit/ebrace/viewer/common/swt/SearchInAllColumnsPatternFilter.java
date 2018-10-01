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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

public class SearchInAllColumnsPatternFilter extends PatternFilter
{
    @Override
    protected boolean isLeafMatch(final Viewer viewer, final Object element)
    {
        TreeViewer treeViewer = (TreeViewer)viewer;
        int numberOfColumns = treeViewer.getTree().getColumnCount();
        boolean isMatch = false;
        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++)
        {
            if (treeViewer.getLabelProvider( columnIndex ) instanceof ColumnLabelProvider)
            {
                ColumnLabelProvider labelProvider = (ColumnLabelProvider)treeViewer.getLabelProvider( columnIndex );
                String labelText = labelProvider.getText( element );
                isMatch |= wordMatches( labelText );
            }
        }
        return isMatch;
    }
}
