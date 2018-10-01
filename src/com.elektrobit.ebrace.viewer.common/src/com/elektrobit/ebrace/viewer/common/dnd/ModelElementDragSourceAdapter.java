/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;

/** Drag source adapter for the implementation fo the drag and drop functionality for runtime event channels. */
public class ModelElementDragSourceAdapter extends DragSourceAdapter
{
    ColumnViewer treeViewer;
    List<ModelElement> modeElements;

    public ModelElementDragSourceAdapter(ColumnViewer treeViewer)
    {
        this.treeViewer = treeViewer;
    }

    @Override
    public void dragSetData(org.eclipse.swt.dnd.DragSourceEvent event)
    {
        event.data = modeElements;
    }

    @Override
    public void dragStart(DragSourceEvent event)
    {
        ISelection selection = treeViewer.getSelection();
        if (selection instanceof TreeSelection)
        {
            TreeSelection treeSelection = (TreeSelection)selection;
            modeElements = new ArrayList<ModelElement>();
            Iterator<?> iterator = treeSelection.iterator();
            while (iterator.hasNext())
            {
                Object o = iterator.next();
                if (o instanceof ModelElement)
                {
                    modeElements.add( (ModelElement)o );
                }
            }
            event.doit = true;
        }
        else
            event.doit = false;
    }
}
