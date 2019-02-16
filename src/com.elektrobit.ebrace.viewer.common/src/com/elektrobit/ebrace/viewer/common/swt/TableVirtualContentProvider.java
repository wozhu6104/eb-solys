/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import java.util.List;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;

public class TableVirtualContentProvider implements ILazyContentProvider
{

    private List<?> data;
    private final TableViewer viewer;

    public TableVirtualContentProvider(TableViewer viewer)
    {
        this.viewer = viewer;
    }

    public void onNewInput(List<?> filterResultList)
    {
        this.data = filterResultList;
    }

    @Override
    public void updateElement(int index)
    {
        if (data != null && data.size() > index)
        {
            Object x = data.get( index );
            viewer.replace( x, index );
        }
    }

    public List<?> getInput()
    {
        return data;
    }

}
