/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.yAxis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.common.propertySupport.EBRacePropertyChangeSupport;
import com.elektrobit.ebrace.viewer.common.propertySupport.PropertyChangeConstants;

@Component
public class YAxisLegendWidthServiceImpl implements YAxisLegendWidthService
{
    private final Map<ChartModel, Integer> maxYAxisCharts = new HashMap<ChartModel, Integer>();
    private final Set<ChartModel> hiddenChartModels = new HashSet<ChartModel>();
    private ResourcesModelManager resourcesModelManager;

    @Override
    public void notifyLegendWidthChanged(ChartModel model, int value)
    {
        int max = getYAxisLegendMaxWidth();
        maxYAxisCharts.put( model, value );
        checkIfMaxYAxisWidthChanged( max, model );
    }

    @Override
    public void notifyChartClosed(ChartModel model)
    {
        int max = getYAxisLegendMaxWidth();
        maxYAxisCharts.remove( model );
        hiddenChartModels.remove( model );
        checkIfMaxYAxisWidthChanged( max, model );
    }

    @Override
    public int getYAxisLegendMaxWidth()
    {
        int max = -1;
        for (ChartModel chartModel : maxYAxisCharts.keySet())
        {
            if (!hiddenChartModels.contains( chartModel ))
            {
                if (maxYAxisCharts.get( chartModel ) != null)
                {
                    if (maxYAxisCharts.get( chartModel ) > max)
                    {
                        max = maxYAxisCharts.get( chartModel );
                    }
                }
            }
        }
        return max;
    }

    @Override
    public void addToHidden(ChartModel chartModel)
    {
        hiddenChartModels.add( chartModel );
        triggerPropertyChangeSupport( chartModel );
    }

    @Override
    public void removeFromHidden(final ChartModel chartModel)
    {
        int max = getYAxisLegendMaxWidth();
        hiddenChartModels.remove( chartModel );
        checkIfMaxYAxisWidthChanged( max, chartModel );
    }

    private void checkIfMaxYAxisWidthChanged(int max, ChartModel chartModel)
    {
        if (max != getYAxisLegendMaxWidth())
        {
            triggerPropertyChangeSupport( chartModel );
        }
    }

    private void triggerPropertyChangeSupport(ChartModel chartModel)
    {
        for (ResourceModel chart : resourcesModelManager.getCharts())
        {
            if (chart.getName() != chartModel.getName())
            {
                EBRacePropertyChangeSupport.firePropertyChangedEvent( chart,
                                                                      PropertyChangeConstants.MODEL_PROPERTIES_CHANGED,
                                                                      "old",
                                                                      "new" );
            }
        }
    }

    @Reference
    public void bind(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = resourcesModelManager;
    }

    public void unbind(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = null;
    }
}
