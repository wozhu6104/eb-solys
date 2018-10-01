/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.gantt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseLiveChartComposite;

public class LiveGanttChartComposite<T> extends BaseLiveChartComposite<T>
{

    public LiveGanttChartComposite(BaseChartComposite parent, int style, Class<?> assignableDataType)
    {
        super( parent, style, assignableDataType );
    }

    @Override
    protected void createNewLiveChartCanvas(Composite parent)
    {
        if (assignableDataType.isAssignableFrom( Boolean.class ))
        {
            liveChartCanvas = new LiveGanttChartCanvas<T>( chartDataSeriesSuiteForLiveChart,
                                                           parent,
                                                           SWT.DOUBLE_BUFFERED,
                                                           modelToDisplay );
        }
    }
}
