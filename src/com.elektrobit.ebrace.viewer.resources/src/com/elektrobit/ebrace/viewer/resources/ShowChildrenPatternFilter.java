/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

public class ShowChildrenPatternFilter extends PatternFilter
{
    private boolean isChildMatch(Viewer viewer, Object element)
    {
        Object parent = ((ITreeContentProvider)((AbstractTreeViewer)viewer).getContentProvider()).getParent( element );

        if (parent != null)
        {
            return (isLeafMatch( viewer, parent ) ? true : isChildMatch( viewer, parent ));
        }
        return false;
    }

    @Override
    protected boolean isLeafMatch(Viewer viewer, Object element)
    {
        String labelText = ((ILabelProvider)((StructuredViewer)viewer).getLabelProvider()).getText( element );

        if (labelText == null)
        {
            return false;
        }

        return (wordMatches( labelText ) ? true : isChildMatch( viewer, element ));
    }
}
