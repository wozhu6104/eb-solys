/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.line;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.viewer.chartengine.internal.ChartEditor;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseAnalysisChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseLiveChartComposite;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

/** Represents the composite which is the control of the line chart tab item. */
public class LineChartComposite extends BaseChartComposite
{
    public LineChartComposite(ChartEditor editor, Composite parent, int style)
    {
        super( editor, parent, style );
    }

    @Override
    public Class<?> getAssignableChannelDataType()
    {
        return Number.class;
    }

    @Override
    protected BaseLiveChartComposite<?> setupLiveChartComposite()
    {
        return new LiveLineChartComposite<Number>( this, SWT.NONE, Number.class );
    }

    @Override
    protected BaseAnalysisChartComposite<?> setupAnalysisChartComposite()
    {
        return new AnalysisLineChartComposite<Number>( this, SWT.NONE, modelToDisplay );
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {

    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        Iterator<RuntimeEventChannel<?>> iterator = modelToDisplay.getChannels().iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().equals( channel ))
            {
                iterator.remove();
            }
        }
        modelToDisplay.setChannels( modelToDisplay.getChannels() );
    }
}
