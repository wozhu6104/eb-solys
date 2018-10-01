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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.extension.datafeed.GanttEntry;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.HorizontalAlignment;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.JavaDateFormatSpecifier;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.attribute.TextAlignment;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.VerticalAlignment;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.impl.DateTimeDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.GanttDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.GanttSeries;
import org.eclipse.birt.chart.model.type.impl.GanttSeriesImpl;
import org.eclipse.birt.chart.util.CDateTime;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator.MinMax;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.ChartBuilder;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class GanttChartBuilder<T> extends ChartBuilder<T> implements ChannelColorCallback
{
    private static final Logger LOG = Logger.getLogger( GanttChartBuilder.class );

    long startTimeOfYAxis = -1;
    long endTimeOfYAxis = -1;

    public GanttChartBuilder(ChartModel model, RunMode runMode)
    {
        super( model, runMode );
    }

    @Override
    protected void createChartType()
    {
        chart = ChartWithAxesImpl.create();
        chart.setType( "Gantt Chart" );
        chart.setSubType( "Standard Gantt Chart" );
        chart.setOrientation( Orientation.HORIZONTAL_LITERAL );
        chart.getBlock().setBackground( ColorDefinitionImpl.WHITE() );
        chart.getBlock().getOutline().setVisible( false );
        Plot p = chart.getPlot();
        p.getClientArea().setBackground( ColorDefinitionImpl.WHITE() );

        // Title
        chart.getTitle().getLabel().setVisible( false );

        // Legend
        Legend lg = chart.getLegend();
        lg.setItemType( LegendItemType.CATEGORIES_LITERAL );
        lg.setVisible( false );

    }

    @Override
    protected void createPrimaryAxes()
    {
        xAxisPrimary = chart.getPrimaryBaseAxes()[0];
        xAxisPrimary.setCategoryAxis( true );
        xAxisPrimary.getMajorGrid().setTickStyle( TickStyle.BELOW_LITERAL );
        xAxisPrimary.getOrigin().setType( IntersectionType.MIN_LITERAL );

        TextAlignment ta = TextAlignmentImpl.create();
        ta.setHorizontalAlignment( HorizontalAlignment.CENTER_LITERAL );
        ta.setVerticalAlignment( VerticalAlignment.BOTTOM_LITERAL );

        yAxisPrimary = chart.getPrimaryOrthogonalAxis( xAxisPrimary );
        yAxisPrimary.setType( AxisType.DATE_TIME_LITERAL );
        yAxisPrimary.getMajorGrid().setTickStyle( TickStyle.LEFT_LITERAL );
    }

    @Override
    protected void setFormatSpecifier(JavaDateFormatSpecifier chartTimeFormatter)
    {
        yAxisPrimary.setFormatSpecifier( chartTimeFormatter );
    }

    @Override
    protected void createSeriesDefinitions()
    {
        ySeriesDefinition = SeriesDefinitionImpl.create();
        yAxisPrimary.getSeriesDefinitions().add( ySeriesDefinition );
    }

    @Override
    public Chart build()
    {
        return super.build();
    }

    @Override
    public void setGanttChartData(GanttChartData ganttChartData)
    {
        super.setGanttChartData( ganttChartData );
        LOG.info( "Gantt chart - Reloading for mode " + runMode );
        setupXAxis( new ArrayList<Long>() );
        setXAxisMinMax();
        List<DataSet> birtGanttChartData = convertDataToBirtFormat( ganttChartData );
        addNewGanttChartPhaseToSeriesDefinition( birtGanttChartData );
        List<RuntimeEventChannel<?>> channels = ganttChartData.getChannels();
        DataSet ganttTaskLabels = createGanttTaskLabels( channels );
        addGanttTaskLabelsToXSeriesDefintion( ganttTaskLabels );
        addColorDefinitionForNewSeries( channels );
    }

    private void setXAxisMinMax()
    {
        MinMax<Long> timespan = ChartAxisMinMaxGenerator.computeXaxisMinMax( runMode, analysisTimespanPreferences );
        startTimestamp = timespan.getMin();
        endTimestamp = timespan.getMax();
        yAxisPrimary.getScale().setMin( DateTimeDataElementImpl.create( new CDateTime( startTimestamp ) ) );
        yAxisPrimary.getScale().setMax( DateTimeDataElementImpl.create( new CDateTime( endTimestamp ) ) );
    }

    private List<DataSet> convertDataToBirtFormat(GanttChartData ganttChartData)
    {
        List<DataSet> ganttChartDataSet = new ArrayList<DataSet>();
        for (GanttChartEntry[] channelEntries : ganttChartData.getData())
        {
            GanttEntry[] birtGantEntries = new GanttEntry[channelEntries.length];
            for (int i = 0; i < channelEntries.length; i++)
            {
                GanttChartEntry ganttChartEntry = channelEntries[i];
                if (ganttChartEntry != null)
                    birtGantEntries[i] = new GanttEntry( new CDateTime( ganttChartEntry.getStartTimeStamp() ),
                                                         new CDateTime( ganttChartEntry.getEndTimeStamp() ),
                                                         "" );
            }
            ganttChartDataSet.add( GanttDataSetImpl.create( birtGantEntries ) );
        }
        return ganttChartDataSet;
    }

    private void addNewGanttChartPhaseToSeriesDefinition(List<DataSet> dataSets)
    {
        ySeriesDefinition.getSeries().clear();
        for (DataSet nextDataSet : dataSets)
        {
            GanttSeries ganttBlock = (GanttSeries)GanttSeriesImpl.create();
            ySeriesDefinition.getSeries().add( ganttBlock );
            ganttBlock.getLabel().setVisible( false );
            ganttBlock.setDataSet( nextDataSet );

        }
    }

    private DataSet createGanttTaskLabels(List<RuntimeEventChannel<?>> channels)
    {
        String longestName = "";
        String[] runtimeEventChannelNames = new String[channels.size()];
        for (int i = 0; i < channels.size(); i++)
        {
            String channelName = channels.get( i ).getName();
            runtimeEventChannelNames[i] = channelName;
            if (channelName.length() > longestName.length())
                longestName = channelName;
        }
        alignChartWithOthers( longestName );
        return TextDataSetImpl.create( runtimeEventChannelNames );
    }

    private void addGanttTaskLabelsToXSeriesDefintion(DataSet ganttBlockLabels)
    {
        if (this.xSeriesDefinition.getSeries().size() > 0)
        {
            xSeriesDefinition.getSeries().get( 0 ).setDataSet( ganttBlockLabels );
        }
        else
        {
            Series seCategory = SeriesImpl.create();
            seCategory.setDataSet( ganttBlockLabels );
            xSeriesDefinition.getSeries().add( seCategory );
        }
    }

    private void addColorDefinitionForNewSeries(List<RuntimeEventChannel<?>> channels)
    {
        for (RuntimeEventChannel<?> channel : channels)
        {
            xSeriesDefinition.getSeriesPalette().getEntries()
                    .add( channelColorDefintionsCreator.getColorDefinitionForSeries( channel.getName() ) );
        }
    }

}
