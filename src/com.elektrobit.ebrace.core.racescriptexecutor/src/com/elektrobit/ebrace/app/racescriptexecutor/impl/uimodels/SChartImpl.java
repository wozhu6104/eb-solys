/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.SChart;
import com.elektrobit.ebsolys.script.external.UIResourcesContext.CHART_TYPE;

public class SChartImpl extends SBaseResourceImpl<ChartModel, SChart> implements SChart
{
    private static final String ADD_CHANNELS_MISMATCH_MESSAGE = "ERROR: All channels in chart must have numerical type only (line chart) or boolean type only (gantt chart)";
    private final ChartModel chartModel;
    private final Console scriptConsole;

    public SChartImpl(ChartModel chartModel, Console scriptConsole, ResourcesModelManager resourcesModelManager)
    {
        super( chartModel, resourcesModelManager );
        this.chartModel = chartModel;
        this.scriptConsole = scriptConsole;
    }

    @Override
    protected SChart getThis()
    {
        return this;
    }

    @Override
    public CHART_TYPE getType()
    {
        CHART_TYPE type = chartModel.getType() == ChartTypes.LINE_CHART
                ? CHART_TYPE.LINE_CHART
                : CHART_TYPE.GANTT_CHART;
        return type;
    }

    @Override
    protected boolean canChannelsBeAddedToView(List<RuntimeEventChannel<?>> channels)
    {
        if (chartModel.channelsMatchChartType( channels ))
        {
            return true;
        }
        else
        {
            scriptConsole.println( ADD_CHANNELS_MISMATCH_MESSAGE );
            return false;
        }
    }
}
