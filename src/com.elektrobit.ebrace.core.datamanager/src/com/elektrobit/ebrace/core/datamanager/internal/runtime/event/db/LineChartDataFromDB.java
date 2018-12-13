/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class LineChartDataFromDB implements LineChartData
{

    private final List<List<ChartData>> allEvents;
    private final List<RuntimeEventChannel<?>> channels;
    private List<Long> allTimeStamps;
    private final Map<RuntimeEventChannel<?>, List<Number>> seriesData = new HashMap<>();

    public LineChartDataFromDB(List<List<ChartData>> allEvents, List<RuntimeEventChannel<?>> channels)
    {
        this.allEvents = allEvents;
        this.channels = channels;
    }

    public void build()
    {

        seriesData.clear();
        Set<Long> timeStamps = new LinkedHashSet<>();

        for (List<ChartData> channelEvents : allEvents)
        {
            timeStamps.addAll( channelEvents.stream().map( e -> e.getTimestamp() * 1000 )
                    .collect( Collectors.toList() ) );
        }

        allTimeStamps = new ArrayList<Long>( timeStamps );
        int cIdx = 0;

        for (List<ChartData> channelEvents : allEvents)
        {
            List<Number> series = new ArrayList<>();
            int evtIdx = 0;

            for (int t = 0; t < allTimeStamps.size(); t++)
            {
                if (evtIdx < channelEvents.size())
                {
                    ChartData evt = channelEvents.get( evtIdx );
                    if (allTimeStamps.get( t ) == evt.getTimestamp() * 1000)
                    {
                        series.add( evt.getValue() );
                        evtIdx += 1;
                    }
                    else
                    {
                        series.add( null );
                    }
                }
            }
            seriesData.put( channels.get( cIdx++ ), series );
        }

    }

    @Override
    public Map<RuntimeEventChannel<?>, List<Number>> getSeriesData()
    {
        return seriesData;
    }

    @Override
    public List<Long> getTimestamps()
    {
        return allTimeStamps;
    }

    @Override
    public double getMaxValue()
    {
        // TODO Auto-generated method stub
        return 50;
    }

    @Override
    public double getMinValue()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getMaxValueStacked()
    {
        // TODO Auto-generated method stub
        return 50;
    }

}
