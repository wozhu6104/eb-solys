/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class ModelElementColumnLabelProvider extends ColumnLabelProvider implements RowFormatter
{
    private final Logger LOG = Logger.getLogger( ModelElementColumnLabelProvider.class );

    @Override
    public String getText(Object element)
    {
        if (element instanceof RuntimeEvent<?>)
        {
            if (((RuntimeEvent<?>)element).getModelElement() != null)
            {
                return ((RuntimeEvent<?>)element).getModelElement().toString();
            }
            else
            {
                LOG.info( "ModelElement is null!" );
            }
        }
        return null;
    }

    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof TimeMarker)
            return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        return super.getBackground( element );
    }
}
