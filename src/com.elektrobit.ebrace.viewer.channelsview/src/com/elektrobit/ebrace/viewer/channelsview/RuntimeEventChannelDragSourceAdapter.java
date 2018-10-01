/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import com.elektrobit.ebrace.viewer.channelsview.handler.ChannelsViewHandlerUtil;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

/** Drag source adapter for the implementation for the drag and drop functionality for runtime event channels. */
public class RuntimeEventChannelDragSourceAdapter extends DragSourceAdapter
{
    private TableViewer tableViewer;
    private StringBuilder resultString;
    private TreeViewer treeViewer;

    public RuntimeEventChannelDragSourceAdapter(TableViewer tableViewer)
    {
        this.tableViewer = tableViewer;
    }

    public RuntimeEventChannelDragSourceAdapter(TreeViewer treeViewer)
    {
        this.treeViewer = treeViewer;
    }

    @Override
    public void dragSetData(org.eclipse.swt.dnd.DragSourceEvent event)
    {
        event.data = channels;
    }

    private List<RuntimeEventChannel<?>> channels;

    @Override
    public void dragStart(DragSourceEvent event)
    {
        ISelection selectionTable = null;
        ISelection selectionTree = null;
        if (tableViewer != null)
        {
            selectionTable = tableViewer.getSelection();
        }
        if (treeViewer != null)
        {
            selectionTree = treeViewer.getSelection();
        }

        if (selectionTable != null)
        {
            if (selectionTable instanceof StructuredSelection)
            {
                StructuredSelection tableSelection = (StructuredSelection)selectionTable;
                resultString = new StringBuilder();
                channels = new ArrayList<RuntimeEventChannel<?>>();
                Iterator<?> iterator = tableSelection.iterator();
                while (iterator.hasNext())
                {
                    Object o = iterator.next();
                    if (o instanceof RuntimeEventChannel<?>)
                    {
                        channels.add( (RuntimeEventChannel<?>)o );
                        resultString.append( "\n" );
                    }
                }
                event.doit = true;
            }
            else
            {
                event.doit = false;
            }
        }
        else
        {
            if (selectionTree instanceof TreeSelection)
            {
                TreeSelection treeSelection = (TreeSelection)selectionTree;
                resultString = new StringBuilder();
                channels = new ArrayList<RuntimeEventChannel<?>>();
                Iterator<?> iterator = treeSelection.iterator();

                List<Object> allNodes = new ArrayList<>();
                while (iterator.hasNext())
                {
                    allNodes.add( iterator.next() );
                }
                channels = ChannelsViewHandlerUtil.filterChannelsInNodes( allNodes );
                event.doit = true;
            }
            else
            {
                event.doit = false;
            }
        }
    }
}
