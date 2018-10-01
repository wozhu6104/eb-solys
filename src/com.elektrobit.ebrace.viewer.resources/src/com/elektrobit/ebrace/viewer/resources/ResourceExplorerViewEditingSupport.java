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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

class ResourceExplorerViewEditingSupport extends EditingSupport
{
    private final TextCellEditor cellEditor;
    private final ResourcesModelManager resourcesModelManager;

    public ResourceExplorerViewEditingSupport(TreeViewer treeViewer, ResourcesModelManager resourcesModelManager)
    {
        super( treeViewer );
        this.resourcesModelManager = resourcesModelManager;
        cellEditor = new TextCellEditor( treeViewer.getTree() );
    }

    @Override
    protected CellEditor getCellEditor(Object element)
    {
        return cellEditor;
    }

    @Override
    protected boolean canEdit(Object element)
    {
        boolean isChart = element instanceof ChartModel;
        boolean isTable = element instanceof TableModel;
        boolean isSnapshot = element instanceof SnapshotModel;
        boolean isTimelineView = element instanceof TimelineViewModel;
        return isChart || isTable || isSnapshot || isTimelineView;
    }

    @Override
    protected Object getValue(Object element)
    {
        BaseResourceModel model = (BaseResourceModel)element;
        return model.getName();
    }

    @Override
    protected void setValue(Object element, Object value)
    {
        String newName = ((String)value).trim();
        BaseResourceModel model = (BaseResourceModel)element;
        if (isNewNameValid( newName ))
        {
            model.setName( newName );
        }
    }

    private boolean isNewNameValid(String newName)
    {
        boolean newNameNotEmpty = newName.length() > 0;
        boolean nameAlreadyUsed = false;
        if (newNameNotEmpty)
        {
            nameAlreadyUsed = isNameAlreadyUsed( newName );
        }

        return newNameNotEmpty && !nameAlreadyUsed;
    }

    private boolean isNameAlreadyUsed(String newName)
    {
        for (ResourceModel model : resourcesModelManager.getResources())
        {
            if (model.getName().equals( newName ))
            {
                return true;
            }
        }
        return false;
    }

}
