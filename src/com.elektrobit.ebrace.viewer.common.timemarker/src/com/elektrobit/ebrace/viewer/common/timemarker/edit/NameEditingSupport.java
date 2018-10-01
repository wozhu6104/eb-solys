/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class NameEditingSupport extends EditingSupport
{
    private final TableViewer viewer;

    public NameEditingSupport(TableViewer viewer)
    {
        super( viewer );
        this.viewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element)
    {
        // TODO Auto-generated method stub
        return new TextCellEditor( this.viewer.getTable() );
    }

    @Override
    protected boolean canEdit(Object element)
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected Object getValue(Object element)
    {
        if (element instanceof TimeMarker)
        {
            return ((TimeMarker)element).getName();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value)
    {
        if (element instanceof TimeMarker)
        {
            ((TimeMarker)element).setName( (String)value );
        }

    }

}
