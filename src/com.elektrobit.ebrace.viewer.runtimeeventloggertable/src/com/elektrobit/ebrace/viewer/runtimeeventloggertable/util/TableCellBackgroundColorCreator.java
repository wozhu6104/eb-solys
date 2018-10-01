/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.util;

import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TableCellBackgroundColorCreator
{
    private final TableBackgroundColorCreator colorCreator;

    public TableCellBackgroundColorCreator()
    {
        this.colorCreator = new TableBackgroundColorCreator();
    }

    public Color getBackground(TableModel model, Object element)
    {
        if (element instanceof TimeMarker)
        {
            return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        }
        else
        {
            if (element instanceof RuntimeEvent<?> && model.isBackgroundEnabled())
            {
                return colorCreator.createBackgroundColorForChannel( (RuntimeEvent<?>)element );
            }
        }
        return null;
    }

    public void dispose()
    {
        colorCreator.dispose();
    }

}
