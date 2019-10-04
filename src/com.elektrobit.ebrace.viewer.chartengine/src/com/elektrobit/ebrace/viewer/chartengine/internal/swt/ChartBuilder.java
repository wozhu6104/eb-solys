/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.JavaDateFormatSpecifier;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaNumberFormatSpecifierImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.impl.DateTimeDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.DateTimeDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.impl.AreaSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.util.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator;
import com.elektrobit.ebrace.core.interactor.api.chartData.ChartAxisMinMaxGenerator.MinMax;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChannelColorCreator;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChartStyleProcessor;
import com.elektrobit.ebrace.viewer.chartengine.yAxis.YAxisLegendWidthService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.ibm.icu.util.Calendar;

public class ChartBuilder<T> implements ChannelColorCallback
{
    private static final Logger LOG = Logger.getLogger( ChartBuilder.class );

    private static final int LINE_SERIES_THICKNESS = 2;
    private static final int LINE_SERIES_THICKNESS_SELECTED = 4;
    private static final int LINE_SERIES_THICKNESS_SELECTED_BACKGROUND = 8;
    private final int STACKED_CHART_Y_AXIS_MIN = 0;

    protected final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    protected final YAxisLegendWidthService yAxisLegendLengthService = new GenericOSGIServiceTracker<YAxisLegendWidthService>( YAxisLegendWidthService.class )
            .getService();

    protected ChartWithAxes chart;
    protected SeriesDefinition ySeriesDefinition;
    protected SeriesDefinition xSeriesDefinition;
    protected Axis yAxisPrimary;
    protected Axis xAxisPrimary;
    protected RunMode runMode;

    protected long startTimestamp = -1;
    protected long endTimestamp = -1;

    private boolean isAreaChartType = true;
    private boolean isStackedChart = false;

    private boolean chartDataSet = false;
    private boolean timestampFormatSet = false;

    protected ChartModel modelToDisplay;
    protected final ChannelColorCreator channelColorDefintionsCreator;
    private final ChannelColorUseCase channelColorUseCase;

    private final JavaDateFormatSpecifier chartTimeFormatter = new ChartTimeFormatter();

    public ChartBuilder(ChartModel model, RunMode mode)
    {
        this.modelToDisplay = model;
        this.runMode = mode;
        setChartBuilderProperties();
        chart = createChart();
        createSeriesDefinitions();
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
        channelColorDefintionsCreator = new ChannelColorCreator( channelColorUseCase );
    }

    private void setChartBuilderProperties()
    {
        isAreaChartType = modelToDisplay.isAreaChartType();
        isStackedChart = modelToDisplay.isStackedChart();
    }

    protected boolean channelCanBeAddedToChart(RuntimeEventChannel<?> channel)
    {
        return Number.class.isAssignableFrom( channel.getUnit().getDataType() );
    }

    private void clearYaxis()
    {
        ySeriesDefinition.getSeries().clear();
        ySeriesDefinition.getSeriesPalette().getEntries().clear();
    }

    private Series createDataSeries(int lineThickness)
    {
        Series newSeries = null;
        if (isAreaChartType)
        {
            newSeries = AreaSeriesImpl.create();
        }
        else
        {
            if (isStackedChart)
            {
                newSeries = AreaSeriesImpl.create();
                newSeries.setStacked( true );
            }
            else
            {
                newSeries = LineSeriesImpl.create();
                LineAttributes la = ((LineSeries)newSeries).getLineAttributes();
                la.setThickness( lineThickness );
            }
        }

        return newSeries;
    }

    public ChartBuilder<T> addChartTitle(String chartTitle)
    {
        chart.getTitle().getLabel().getCaption().setValue( chartTitle );
        return this;
    }

    public Chart build()
    {
        if (isDataSet())
        {
            return chart;
        }
        else
        {
            throw new IllegalStateException( "Data is not set for chart " + modelToDisplay.getName() );
        }
    }

    public boolean isDataSet()
    {
        return chartDataSet && timestampFormatSet;
    }

    protected void createChartType()
    {
        chart = ChartWithAxesImpl.create();
        chart.setType( "Area Chart" );
        chart.setSubType( "Overlay" );
        chart.getBlock().setBackground( ColorDefinitionImpl.WHITE() );
        chart.getBlock().getOutline().setVisible( false );
        chart.setDimension( ChartDimension.TWO_DIMENSIONAL_LITERAL );
        // Title
        chart.getTitle().getLabel().setVisible( false );
        Plot plot = chart.getPlot();
        plot.getClientArea().setBackground( ColorDefinitionImpl.WHITE() );
        plot.getOutline().setVisible( false );
        plot.setBackground( ColorDefinitionImpl.WHITE() );
        plot.getClientArea().setBackground( ColorDefinitionImpl.WHITE() );
        chart.getLegend().setVisible( false );
    }

    protected void createSeriesDefinitions()
    {
        ySeriesDefinition = SeriesDefinitionImpl.create();
        yAxisPrimary.getSeriesDefinitions().add( ySeriesDefinition );
    }

    protected void createPrimaryAxes()
    {
        xAxisPrimary = chart.getBaseAxes()[0];
        xAxisPrimary.getMajorGrid().setTickStyle( TickStyle.BELOW_LITERAL );

        xAxisPrimary.getOrigin().setType( IntersectionType.MIN_LITERAL );
        xAxisPrimary.setCategoryAxis( false );
        xAxisPrimary.getScale().setAutoExpand( false );
        xAxisPrimary.getScale().setTickBetweenCategories( false );
        xAxisPrimary.getScale().setShowOutside( false );
        setDataFormatXAxis();

        xAxisPrimary.setInterval( 2 );
        yAxisPrimary = chart.getPrimaryOrthogonalAxis( xAxisPrimary );
        yAxisPrimary.setType( AxisType.LINEAR_LITERAL );
        yAxisPrimary.getMajorGrid().setTickStyle( TickStyle.LEFT_LITERAL );
        yAxisPrimary.setFormatSpecifier( JavaNumberFormatSpecifierImpl.create( "##.##" ) );
        yAxisPrimary.setPercent( false );
        yAxisPrimary.getScale().setTickBetweenCategories( false );

        setYAxis();
    }

    private void setDataFormatXAxis()
    {
        xAxisPrimary.setType( AxisType.DATE_TIME_LITERAL );
    }

    private void setYAxis()
    {
        int maxYAxis = modelToDisplay.getMaxYAxis();
        int minYAxis = modelToDisplay.getMinYAxis();
        boolean isFix = modelToDisplay.isFix();
        boolean isSemiDynamic = modelToDisplay.isSemiDynamic();
        if (isFix || isSemiDynamic)
        {
            yAxisPrimary.getScale().setMin( NumberDataElementImpl.create( minYAxis ) );
            yAxisPrimary.getScale().setMax( NumberDataElementImpl.create( maxYAxis ) );
            yAxisPrimary.getScale().setAutoExpand( false );
            yAxisPrimary.getScale().setShowOutside( false );
        }
        yAxisPrimary.getTitle().setVisible( true );
        yAxisPrimary.getTitle().getCaption().setValue( modelToDisplay.getChannels().get( 0 ).getUnit().getName() );
        yAxisPrimary.getTitle().getCaption()
                .setFont( FontDefinitionImpl.create( ChartStyleProcessor.CHART_FONT,
                                                     ChartStyleProcessor.FONT_SIZE + 2,
                                                     true,
                                                     false,
                                                     false,
                                                     false,
                                                     false,
                                                     90,
                                                     null ) );
    }

    private ChartWithAxes createChart()
    {
        createChartType();
        createPrimaryAxes();
        setFormatSpecifier( chartTimeFormatter );
        return chart;
    }

    protected void setFormatSpecifier(JavaDateFormatSpecifier chartTimeFormatter)
    {
        xAxisPrimary.setFormatSpecifier( chartTimeFormatter );
    }

    private void addColorDefinitionForSeries(ColorDefinition colorDefinition)
    {
        ySeriesDefinition.getSeriesPalette().getEntries().add( colorDefinition );
    }

    protected void setupXAxis(List<Long> timestamps)
    {
        xSeriesDefinition = SeriesDefinitionImpl.create();
        Series xSeries = SeriesImpl.create();
        DataSet xData = createDataSetForXAxis( timestamps );
        xSeries.setDataSet( xData );

        xSeriesDefinition.getSeries().add( xSeries );

        xAxisPrimary.getSeriesDefinitions().clear();
        xAxisPrimary.getSeriesDefinitions().add( xSeriesDefinition );
    }

    private DataSet createDataSetForXAxis(List<Long> timestamps)
    {
        List<Calendar> timestampsAsTime = new ArrayList<Calendar>();
        for (Long timestamp : timestamps)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis( timestamp );
            timestampsAsTime.add( calendar );
        }
        return DateTimeDataSetImpl.create( timestampsAsTime );
    }

    private void calculateStartAndEndTimestamp()
    {
        MinMax<Long> timespan = ChartAxisMinMaxGenerator.computeXaxisMinMax( runMode, analysisTimespanPreferences );
        startTimestamp = timespan.getMin();
        endTimestamp = timespan.getMax();
    }

    private void setYAxisMinMax(LineChartData chartData)
    {
        MinMax<Double> displayedMinMax;

        if (!isStackedChart)
        {
            displayedMinMax = ChartAxisMinMaxGenerator
                    .computeYaxisMinMax( chartData.getMinValue(), chartData.getMaxValue(), modelToDisplay );
        }
        else
        {
            displayedMinMax = ChartAxisMinMaxGenerator
                    .computeYaxisMinMax( STACKED_CHART_Y_AXIS_MIN, chartData.getMaxValueStacked(), modelToDisplay );
        }

        yAxisPrimary.getScale().setMin( NumberDataElementImpl.create( displayedMinMax.getMin() ) );
        yAxisPrimary.getScale().setMax( NumberDataElementImpl.create( displayedMinMax.getMax() ) );

        String labelText = String.valueOf( displayedMinMax.getMax().longValue() );
        alignChartWithOthers( labelText );
    }

    protected void alignChartWithOthers(String longestYAxisLabel)
    {
        int labelWidth = computeStringWidth( longestYAxisLabel );
        notifyWidthServiceIfNeeded( labelWidth );
        int maxWidthAllCharts = yAxisLegendLengthService.getYAxisLegendMaxWidth();
        int difference = maxWidthAllCharts - labelWidth;
        if (difference < labelWidth)
        {
            chart.getPlot().getInsets().setLeft( 0 );
        }
        else
        {
            chart.getPlot().getInsets().setLeft( (difference) * ChartScaleRatio.get() );
        }
    }

    private void notifyWidthServiceIfNeeded(int labelWidth)
    {
        if (runMode != RunMode.ANALYSIS)
        {
            yAxisLegendLengthService.notifyLegendWidthChanged( modelToDisplay, labelWidth );
        }
    }

    private int computeStringWidth(String longestName)
    {
        Shell shell = new Shell();
        Label label = new Label( shell, SWT.BORDER );
        label.setSize( 50, 30 );
        label.setText( longestName );

        GC gc = new GC( label );
        FontData fontData = new FontData();
        fontData.setName( ChartStyleProcessor.CHART_FONT );

        fontData.setHeight( ChartStyleProcessor.FONT_SIZE );
        fontData.setStyle( SWT.NORMAL );
        Font font = new Font( Display.getDefault(), fontData );
        gc.setFont( font );
        int stringWidth = gc.stringExtent( longestName ).x;
        gc.dispose();
        label.dispose();
        shell.dispose();
        font.dispose();
        return stringWidth;
    }

    private void setXAxisMinMax(long startTimestamp, long endTimestamp)
    {
        xAxisPrimary.getScale().setMin( DateTimeDataElementImpl.create( new CDateTime( startTimestamp ) ) );
        xAxisPrimary.getScale().setMax( DateTimeDataElementImpl.create( new CDateTime( endTimestamp ) ) );
    }

    /**
     * For logging purposes only
     */
    @Deprecated
    public RunMode getRunMode()
    {
        return runMode;
    }

    public void setLineChartData(LineChartData lineChartData)
    {
        chartDataSet = true;
        LOG.info( "Line chart - Reloading for mode " + runMode );
        calculateStartAndEndTimestamp();

        setXAxisMinMax( startTimestamp, endTimestamp );
        setYAxisMinMax( lineChartData );

        clearYaxis();

        Map<RuntimeEventChannel<?>, List<Number>> channelsToValuesMap = lineChartData.getSeriesData();
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>( channelsToValuesMap.keySet() );
        moveSelectedChannelToEndToRenderThemAsLast( channels );
        for (RuntimeEventChannel<?> channel : channels)
        {
            boolean isChannelSelected = modelToDisplay.getSelectedChannels().contains( channel );
            ColorDefinition colorDefinition = channelColorDefintionsCreator
                    .getColorDefinitionForSeries( channel.getName() );
            List<Number> series = channelsToValuesMap.get( channel );

            if (isChannelSelected)
            {
                addChannelToChartWithBackgroundLineAndHighlighted( channel, colorDefinition, series );
            }
            else
            {
                addChannelToChart( channel, colorDefinition, series );
            }
        }

        setupXAxis( lineChartData.getTimestamps() );
    }

    private void moveSelectedChannelToEndToRenderThemAsLast(List<RuntimeEventChannel<?>> channels)
    {
        List<RuntimeEventChannel<?>> originalChannels = new ArrayList<RuntimeEventChannel<?>>( channels );
        List<RuntimeEventChannel<?>> selectedChannels = modelToDisplay.getSelectedChannels();
        channels.removeAll( selectedChannels );

        for (RuntimeEventChannel<?> selectedChannel : selectedChannels)
        {
            if (originalChannels.contains( selectedChannel ))
            {
                channels.add( selectedChannel );
            }
        }
    }

    private void addChannelToChartWithBackgroundLineAndHighlighted(RuntimeEventChannel<?> channel,
            ColorDefinition colorDefinition, List<Number> series)
    {
        ColorDefinition colorDefinitionWhite = ColorDefinitionImpl.create( 255, 255, 255 );

        if (!isStackedChart)
        {
            addNewSeriesToYaxis( channel, series, LINE_SERIES_THICKNESS_SELECTED_BACKGROUND, colorDefinitionWhite );
        }

        addNewSeriesToYaxis( channel, series, LINE_SERIES_THICKNESS_SELECTED, colorDefinition );
    }

    private void addChannelToChart(RuntimeEventChannel<?> channel, ColorDefinition colorDefinition, List<Number> series)
    {
        addNewSeriesToYaxis( channel, series, LINE_SERIES_THICKNESS, colorDefinition );
    }

    protected void addNewSeriesToYaxis(RuntimeEventChannel<?> channel, List<Number> valuesOfSeries, int lineThickness,
            ColorDefinition color)
    {
        String channelName = channel.getName();
        Series newSeries = createDataSeries( lineThickness );
        boolean transtlucentLines = isAreaChartType;
        newSeries.setTranslucent( transtlucentLines );
        newSeries.setSeriesIdentifier( channelName );
        NumberDataSet newSeriesData = NumberDataSetImpl.create( valuesOfSeries );
        newSeries.setDataSet( newSeriesData );
        ySeriesDefinition.getSeries().add( newSeries );
        addColorDefinitionForSeries( color );
    }

    public void setGanttChartData(GanttChartData ganttChartData)
    {
        chartDataSet = true;
    }

    public void setTimestampFormat(String timestampFormat)
    {
        if (timestampFormat == null)
        {
            return;
        }

        timestampFormatSet = true;
        chartTimeFormatter.setPattern( timestampFormat );
        if (xAxisPrimary != null)
        {
            setFormatSpecifier( chartTimeFormatter );
        }
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
    }

    public void dispose()
    {
        channelColorUseCase.unregister();
    }
}
