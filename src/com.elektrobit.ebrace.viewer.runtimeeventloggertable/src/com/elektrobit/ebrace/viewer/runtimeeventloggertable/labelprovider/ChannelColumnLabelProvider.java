/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class ChannelColumnLabelProvider extends ColumnLabelProvider implements RowFormatter
{
    private final TableModel model;
    private final TableCellBackgroundColorCreator backgroundColorCreator;

    public ChannelColumnLabelProvider(TableModel model, TableCellBackgroundColorCreator backgroundColorCreator)
    {
        this.model = model;
        this.backgroundColorCreator = backgroundColorCreator;
    }

    @Override
    public String getText(Object element)
    {
        String result = null;
        if (element instanceof RuntimeEvent<?>)
        {
            result = ((RuntimeEvent<?>)element).getRuntimeEventChannel().getName();
        }

        return result;
    }

    @Override
    public Color getBackground(Object element)
    {
        return backgroundColorCreator.getBackground( model, element );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        backgroundColorCreator.dispose();
    }

}
