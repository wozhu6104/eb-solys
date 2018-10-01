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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProviderListener;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;

public class TargetHeaderCPUValueDataProvider implements IDataProvider
{
    private final List<IDataProviderListener> listeners = new CopyOnWriteArrayList<IDataProviderListener>();
    private final List<ISample> samples = new ArrayList<ISample>();
    private Range xDataMinMax = null;
    private Range yDataMinMax = null;

    public TargetHeaderCPUValueDataProvider(List<RTargetHeaderCPUValue> values)
    {
        for (RTargetHeaderCPUValue rTargetHeaderCPUValue : values)
        {
            addSample( new Sample( rTargetHeaderCPUValue.getTimestamp(), rTargetHeaderCPUValue.getValue() ) );
        }
    }

    private void addSample(ISample sample)
    {
        updateXDataMinMax( sample );
        updateYDataMinMax( sample );
        samples.add( sample );
    }

    private void updateXDataMinMax(ISample sample)
    {
        if (xDataMinMax == null)
            xDataMinMax = new Range( sample.getXValue(), sample.getXValue() );
        else if (sample.getXValue() < xDataMinMax.getLower())
            xDataMinMax = new Range( sample.getXValue(), xDataMinMax.getUpper() );
        else if (sample.getXValue() > xDataMinMax.getUpper())
            xDataMinMax = new Range( xDataMinMax.getLower(), sample.getXValue() );
    }

    private void updateYDataMinMax(ISample sample)
    {
        if (yDataMinMax == null)
            yDataMinMax = new Range( sample.getYValue(), sample.getYValue() );
        else if (sample.getYValue() < yDataMinMax.getLower())
            yDataMinMax = new Range( sample.getYValue(), yDataMinMax.getUpper() );
        else if (sample.getYValue() > yDataMinMax.getUpper())
            yDataMinMax = new Range( xDataMinMax.getLower(), sample.getYValue() );
    }

    @Override
    public int getSize()
    {
        return samples.size();
    }

    @Override
    public ISample getSample(int index)
    {
        return samples.get( index );
    }

    @Override
    public Range getXDataMinMax()
    {
        return xDataMinMax;
    }

    @Override
    public Range getYDataMinMax()
    {
        return yDataMinMax;
    }

    @Override
    public boolean isChronological()
    {
        return true;
    }

    @Override
    public void addDataProviderListener(IDataProviderListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public boolean removeDataProviderListener(IDataProviderListener listener)
    {
        return listeners.remove( listener );
    }

}
