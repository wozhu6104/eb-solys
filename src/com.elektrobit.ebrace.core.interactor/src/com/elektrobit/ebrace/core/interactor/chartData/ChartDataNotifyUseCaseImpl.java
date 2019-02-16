/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.chartData;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator.MinMax;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class ChartDataNotifyUseCaseImpl
        implements
            ChartDataNotifyUseCase,
            ResourceChangedListener,
            AnalysisTimespanChangedListener,
            TimeMarkersChangedListener,
            ChannelsContentChangedListener
{
    private static final int LINE_CHART_PIXELS_PER_AGGREGATED_VALUE = 5;
    private static final int GANTT_CHART_AGGREGATION_FACTOR = 2;

    private static final Logger LOG = Logger.getLogger( ChartDataNotifyUseCaseImpl.class );

    private final RuntimeEventProvider runtimeEventProvider;
    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private final ResourcesModelManager resourcesModelManager;
    private ChartDataCallback callback;
    private ChartModel chartModel;
    private RunMode runMode;

    private long startTimestamp = 0;
    private long endTimestamp = 0;
    private final TimeMarkerManager timeMarkerManager;

    public ChartDataNotifyUseCaseImpl(ChartDataCallback chartDataCallback, RuntimeEventProvider runtimeEventProvider,
            AnalysisTimespanPreferences analysisTimespanPreferences, ResourcesModelManager resourcesModelManager,
            TimeMarkerManager timeMarkerManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", chartDataCallback );
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventProvider", runtimeEventProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "analysisTimespanPreferences", analysisTimespanPreferences );
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        RangeCheckUtils.assertReferenceParameterNotNull( "timeMarkerManager", timeMarkerManager );
        this.runtimeEventProvider = runtimeEventProvider;
        this.callback = chartDataCallback;
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.resourcesModelManager = resourcesModelManager;
        this.timeMarkerManager = timeMarkerManager;
    }

    @Override
    public void register(ChartModel chartModel, RunMode runMode)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "ChartModel", chartModel );
        RangeCheckUtils.assertReferenceParameterNotNull( "RunMode", runMode );
        this.chartModel = chartModel;
        this.runMode = runMode;
        resourcesModelManager.registerResourceListener( this );
        analysisTimespanPreferences.addTimespanPreferencesChangedListener( this );
        timeMarkerManager.registerListener( this );

        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.register( this, chartModel.getEnabledChannels() );

        if (runMode != RunMode.FULL) // full chart will report its width and trigger collection later
        {
            collectAndPostNewData();
        }
    }

    private void collectAndPostNewData()
    {
        LOG.info( "Starting chart data collection " + runMode );
        UseCaseExecutor.schedule( new UseCaseRunnable( "ChartDataNotifyUseCase.collectAndPostNewData", () -> {
            Object result = collectData();
            if (result != null)
            {
                LOG.info( "Chart data collection done " + runMode );
                postDataToCallBack( result );
            }
        } ) );

    }

    private void postDataToCallBack(final Object result)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback == null)
                {
                    return;
                }

                if (result instanceof LineChartData)
                {
                    callback.onNewLineChartData( (LineChartData)result );
                }
                else if (result instanceof GanttChartData)
                {
                    callback.onNewGanttChartData( (GanttChartData)result );
                }
                else
                {
                    LOG.error( "Exepected result type " + result.getClass() );
                }
            }
        } );
    }

    private Object collectData()
    {
        MinMax<Long> timespan = ChartAxisMinMaxGenerator.computeXaxisMinMax( runMode, analysisTimespanPreferences );
        startTimestamp = timespan.getMin();
        endTimestamp = timespan.getMax();
        boolean dataAsBars = chartModel.isLineChartPresAsBar();
        List<RuntimeEventChannel<?>> channels = chartModel.getEnabledChannels();

        ChartTypes chartType = chartModel.getType();
        Long aggregationTimeAsLong = null;
        if (callback != null && runMode == RunMode.FULL)
        {
            double aggregationTime = computeAggregationTimeDependingOnChartViewWidth( chartType );
            aggregationTimeAsLong = new Double( aggregationTime ).longValue();
        }

        if (chartType == ChartTypes.LINE_CHART)
        {
            return runtimeEventProvider.getLineChartData( channels,
                                                          startTimestamp,
                                                          endTimestamp,
                                                          dataAsBars,
                                                          aggregationTimeAsLong,
                                                          chartModel.isStackedChart() );
        }
        else if (chartType == ChartTypes.GANTT_CHART)
        {
            return runtimeEventProvider
                    .getGanttChartData( channels, startTimestamp, endTimestamp, aggregationTimeAsLong );
        }
        throw new RuntimeException( "Unexpected ChartType: " + chartType );
    }

    private double computeAggregationTimeDependingOnChartViewWidth(ChartTypes chartType)
    {
        int widthPx = callback.getWidth();
        long shownTimeInterval = endTimestamp - startTimestamp;
        if (chartType == ChartTypes.GANTT_CHART)
        {
            return (GANTT_CHART_AGGREGATION_FACTOR * shownTimeInterval) / widthPx;
        }
        else
        {
            long desiredIntervalcount = widthPx / LINE_CHART_PIXELS_PER_AGGREGATED_VALUE;
            long singleIntervalTimespan = shownTimeInterval / desiredIntervalcount;
            return singleIntervalTimespan;
        }
    }

    @Override
    public void onResourceModelChannelsChanged(ResourceModel resourceModel)
    {
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.register( this, chartModel.getEnabledChannels() );
        if (resourceModel.equals( chartModel ))
        {
            collectAndPostNewData();
        }
    }

    @Override
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel)
    {
        if (resourceModel.equals( chartModel ))
        {
            UIExecutor.post( () -> {
                if (callback != null)
                {
                    callback.onSelectedChannelsChanged();
                }
            } );
        }
    }

    @Override
    public void analysisTimespanLengthChanged(long newAnalysisTimespanInMillis)
    {
        if (runMode != RunMode.FULL)
        {
            collectAndPostNewData();
        }
    }

    @Override
    public void fullTimespanEndTimeChanged(long newAnalysisTimespanEndTimeInMillis)
    {
        collectAndPostNewData();
    }

    @Override
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        if (runMode == RunMode.ANALYSIS)
        {
            collectAndPostNewData();
        }
    }

    private volatile boolean isExecutionScheduled = false;

    @Override
    public void notifyChartWidthChanged()
    {
        if (!isExecutionScheduled)
        {
            isExecutionScheduled = true;
            UseCaseExecutor.scheduleDelayed( new TimerTask()
            {

                @Override
                public void run()
                {
                    if (runMode == RunMode.FULL)
                    {
                        collectAndPostNewData();
                    }
                    isExecutionScheduled = false;
                }
            }, 2000 );
        }
    }

    @Override
    public void unregister()
    {
        resourcesModelManager.unregisterResourceListener( this );
        analysisTimespanPreferences.removeTimespanPreferencesChangedListener( this );
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.unregister( this );
        timeMarkerManager.unregisterListener( this );
        callback = null;
    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerSelected(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerNameChanged(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        postTimeMarkerChanged();
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        postTimeMarkerChanged();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
        postTimeMarkerChanged();
    }

    private void postTimeMarkerChanged()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onTimeMarkerChanged();
                }
            }
        } );
    }

    @Override
    public void onChannelsContentChanged()
    {
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // collectAndPostNewData();
    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        // TODO Auto-generated method stub

    }
}
