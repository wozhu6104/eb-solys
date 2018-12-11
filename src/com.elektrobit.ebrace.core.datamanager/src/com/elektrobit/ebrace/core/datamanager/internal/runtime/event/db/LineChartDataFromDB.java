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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.Channel;

public class LineChartDataFromDB implements LineChartData
{

    private final List<List<BaseEvent<Number>>> allEvents;
    private final List<Channel> channels;

    public LineChartDataFromDB(List<List<BaseEvent<Number>>> allEvents, List<Channel> channels)
    {
        this.allEvents = allEvents;
        this.channels = channels;
    }

    public void build()
    {

    }

    @Override
    public Map<RuntimeEventChannel<?>, List<Number>> getSeriesData()
    {
        return null;
    }

    @Override
    public List<Long> getTimestamps()
    {

        Set<Long> timeStamps = new LinkedHashSet<>();

        for (List<BaseEvent<Number>> channelEvents : allEvents)
        {
            timeStamps.addAll( channelEvents.stream().map( e -> e.getTimestamp() * 1000 )
                    .collect( Collectors.toList() ) );
        }

        return new ArrayList<Long>( timeStamps );
    }

    @Override
    public double getMaxValue()
    {
        // TODO Auto-generated method stub
        return 0;
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
        return 0;
    }

}
