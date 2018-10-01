/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import java.util.List;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartRepresentation;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartType;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartYaxisScaleMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import lombok.Getter;
import lombok.Setter;

public class ChartModelImpl extends BaseResourceModel implements ChartModel
{
    private ChartTypes chartType;

    private final PreferencesService preferencesService = new GenericOSGIServiceTracker<PreferencesService>( PreferencesService.class )
            .getService();

    @Setter
    @Getter
    private LineChartModelSettings lineChartModelSettings;

    @Override
    public ChartTypes getType()
    {
        return chartType;
    }

    @Override
    public void setType(ChartTypes type)
    {
        this.chartType = type;
    }

    public ChartModelImpl(String name, ChartTypes type, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( name, parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
        this.chartType = type;
        loadGlobalSettings();
    }

    @Override
    public void loadGlobalSettings()
    {
        lineChartModelSettings = preferencesService.getGlobalLineChartSettings();
    }

    @Override
    public void setChannels(List<RuntimeEventChannel<?>> channels)
    {
        if (channelsMatchChartType( channels ))
        {
            super.setChannels( channels );
        }
        else
        {
            throw new RuntimeException( "Channels do not match type of chart,check them with method channelsMatchChartType() first" );
        }
    }

    @Override
    public boolean channelsMatchChartType(List<RuntimeEventChannel<?>> channels)
    {
        for (RuntimeEventChannel<?> channel : channels)
        {
            Class<?> channelTypeClass = channel.getUnit().getDataType();
            Class<?> chartTypeClass = chartType.getAssignableType();
            boolean isAssignableFrom = chartTypeClass.isAssignableFrom( channelTypeClass );
            if (!isAssignableFrom)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chartType == null) ? 0 : chartType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals( obj ))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ChartModelImpl other = (ChartModelImpl)obj;
        if (chartType != other.chartType)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAreaChartType()
    {
        return lineChartModelSettings.getLineChartRepresentation().equals( LineChartRepresentation.FILLED );
    }

    @Override
    public boolean isLineChartPresAsBar()
    {
        return lineChartModelSettings.getLineChartType().equals( LineChartType.BAR_CHART );
    }

    @Override
    public boolean isStackedChart()
    {
        return lineChartModelSettings.getLineChartRepresentation().equals( LineChartRepresentation.STACKED );
    }

    @Override
    public int getMinYAxis()
    {
        return lineChartModelSettings.getYAxisMinValue();
    }

    @Override
    public int getMaxYAxis()
    {
        return lineChartModelSettings.getYAxisMaxValue();
    }

    @Override
    public boolean isFix()
    {
        return lineChartModelSettings.getLineChartYaxisScaleMode().equals( LineChartYaxisScaleMode.FIXED );
    }

    @Override
    public boolean isSemiDynamic()
    {
        return lineChartModelSettings.getLineChartYaxisScaleMode().equals( LineChartYaxisScaleMode.SEMI_DYNAMIC );
    }

    @Override
    public <T> T getAdapter(Class<T> adapter)
    {
        return null;
    }
}
