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
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

import com.elektrobit.ebrace.common.thread.StoppableRunnable;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.ChartDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering.CenteredTextRenderer;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering.ChartTimeMarkerRenderer;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering.ChartToImageRenderer;
import com.elektrobit.ebrace.viewer.chartengine.internal.thread.ChartRenderingThreadPool;
import com.elektrobit.ebrace.viewer.common.timemarker.listener.ChartShiftKeyListener;
import com.elektrobit.ebrace.viewer.common.timemarker.listener.TimeMarkerMouseListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class BaseChartCanvas<T> extends Canvas
        implements
            PreferencesNotifyCallback,
            ChartDataCallback,
            PaintListener,
            TimeMarkersNotifyCallback,
            AnalysisTimespanNotifyCallback,
            ChannelColorCallback
{
    protected enum TimeMarkerLabelStyle {
        HORIZONTAL, TILTED
    };

    private final String INFO_BOX_MESSAGE = "Calculating Available Data. Please Wait.";

    private final Display display;
    private final TimeMarkerMouseListener handleTimelineMouseListener;
    protected final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    private ResourceManager resManager = null;

    protected GeneratedChartState chartState;
    protected final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    protected ChartModel modelToDisplay;
    protected int width;
    protected ChartDataNotifyUseCase chartDataUseCase;

    private final PreferencesNotifyUseCase preferencesNotifyUseCase;
    private final TimeMarkersNotifyUseCase timeMarkersNotifyUseCase;
    private final AnalysisTimespanNotifyUseCase analysisTimespanNotifyUseCase;

    private LineChartData lineChartData;
    private ImageData imageData;

    private final String chartTitle;
    private GanttChartData ganttChartData;
    private String timestampFormat;

    private StoppableRunnable currentRederingRunnable;

    private final ChannelColorUseCase channelColorUseCase;

    public BaseChartCanvas(Set<RuntimeEventChannel<T>> channels, String chartTitle, Composite parent, int style,
            ChartModel model)
    {
        super( parent, style );
        this.chartTitle = chartTitle;
        this.modelToDisplay = model;
        display = parent.getDisplay();
        addPaintListener( this );
        addShiftTimespanKeyListener();
        createContextMenu();
        setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
        handleTimelineMouseListener = new TimeMarkerMouseListener( this, SWT.HORIZONTAL );
        addMouseListener( this.handleTimelineMouseListener );
        addMouseTrackListener( this.handleTimelineMouseListener );
        resManager = new LocalResourceManager( JFaceResources.getResources(), parent );
        width = getShell().getBounds().width;
        chartDataUseCase = UseCaseFactoryInstance.get().makeChartDataNotifyUseCase( this );
        chartDataUseCase.register( modelToDisplay, getRunMode() );
        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
        timeMarkersNotifyUseCase = UseCaseFactoryInstance.get().makeTimeMarkersNotifyUseCase( this );
        analysisTimespanNotifyUseCase = UseCaseFactoryInstance.get().makeAnalysisTimespanNotifyUseCase( this );
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );

        addControlResizedListener();
    }

    private void addShiftTimespanKeyListener()
    {
        ChartShiftKeyListener handleTimelineKeyListener = new ChartShiftKeyListener();
        addKeyListener( handleTimelineKeyListener );
    }

    protected void createContextMenu()
    {
        MenuManager menuMgr = new MenuManager();
        // Create menu.
        Menu menu = menuMgr.createContextMenu( this );
        BaseChartComposite p = null;
        this.setMenu( menu );
        if (getParent() instanceof BaseChartComposite)
        {
            p = (BaseChartComposite)getParent();
        }
        else if (getParent() instanceof Composite)
        {
            Composite pr = getParent();
            while (pr != null)
            {
                if (pr instanceof BaseChartComposite)
                {
                    p = (BaseChartComposite)pr;
                    break;
                }
                pr = pr.getParent();
            }
        }
        if (p != null)
        {
            p.getChartEditor().getSite().registerContextMenu( "canvasContextMenu", menuMgr, null );
        }
    }

    private void addControlResizedListener()
    {
        addControlListener( new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                onResize();
            }

        } );
    }

    private void onResize()
    {
        if (getRunMode() == RunMode.FULL)
        {
            imageData = null;
            redraw();
        }
        else
        {
            updateChartImage();
        }
    };

    /**
     * Creates the chart builder for this canvas.
     * 
     * @param chartTitle
     *            the chart title.
     */
    protected abstract ChartBuilder<T> createChartBuilder(String chartTitle);

    @Override
    public void paintControl(PaintEvent pe)
    {
        doPaint( pe );
    }

    private void doPaint(PaintEvent pe)
    {
        if (imageData == null)
        {
            drawInfoBox( pe, INFO_BOX_MESSAGE );
            return;
        }

        Image chartImage = new Image( Display.getCurrent(), imageData );
        pe.gc.drawImage( chartImage, 0, 0 );
        chartImage.dispose();

        paintControlFinished( pe );
    }

    private void updateChartImage()
    {
        Rectangle clientArea = getClientArea();
        ChartBuilder<T> chartBuilder = createChartbuilderWithData();

        if (currentRederingRunnable != null)
        {
            currentRederingRunnable.setStopFlag();
            imageData = null;
        }

        currentRederingRunnable = new StoppableRunnable()
        {
            @Override
            public void run()
            {
                if (shouldStop())
                {
                    chartBuilder.dispose();
                    return;
                }

                log.info( "START updating chart image, chart: " + getRunMode() );
                ChartToImageRenderer chartToImageRenderer = new ChartToImageRenderer();
                ImageData newImageData = chartToImageRenderer.createChartImage( clientArea, chartBuilder );

                if (!shouldStop())
                {
                    log.info( "setting image data " + getRunMode() );
                    imageData = newImageData;
                    chartState = chartToImageRenderer.getChartState();
                }
                chartBuilder.dispose();
                log.info( "DONE updating chart image, chart: " + getRunMode() );
                Display.getDefault().asyncExec( () -> redrawIfNotDisposed() );
            }

            private void redrawIfNotDisposed()
            {
                if (!isDisposed())
                {
                    redraw();
                }
            }
        };
        ChartRenderingThreadPool.run( currentRederingRunnable );
    }

    private ChartBuilder<T> createChartbuilderWithData()
    {
        ChartBuilder<T> chartBuilder = createChartBuilder( chartTitle );
        if (lineChartData != null)
        {
            chartBuilder.setLineChartData( lineChartData );
        }
        if (ganttChartData != null)
        {
            chartBuilder.setGanttChartData( ganttChartData );
        }
        chartBuilder.setTimestampFormat( timestampFormat );

        return chartBuilder;
    }

    private void drawInfoBox(PaintEvent pe, String text)
    {
        Composite composite = (Composite)pe.getSource();
        Rectangle clientArea = composite.getClientArea();
        composite.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
        pe.gc.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
        CenteredTextRenderer.drawTextAndTrim( pe.gc,
                                              INFO_BOX_MESSAGE,
                                              0,
                                              0,
                                              clientArea.width,
                                              clientArea.height,
                                              true,
                                              true,
                                              false );
    }

    @Override
    public void onNewLineChartData(LineChartData lineChartData)
    {
        this.lineChartData = lineChartData;
        updateChartImage();
    }

    @Override
    public void onSelectedChannelsChanged()
    {
        updateChartImage();
    }

    @Override
    public void onNewGanttChartData(GanttChartData ganttChartData)
    {
        this.ganttChartData = ganttChartData;
        updateChartImage();
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormat)
    {
        this.timestampFormat = timestampFormat;
        updateChartImage();
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public void dispose()
    {
        chartDataUseCase.unregister();
        preferencesNotifyUseCase.unregister();
        timeMarkersNotifyUseCase.unregister();
        analysisTimespanNotifyUseCase.unregister();
        channelColorUseCase.unregister();
        super.dispose();

        if (resManager != null)
        {
            resManager.dispose();
        }
    }

    protected void paintControlFinished(PaintEvent pe)
    {
        pe.gc.setAntialias( SWT.ON );
        updateTimemarkerMouseListenerValues();
        getAndDrawTimemarkers( pe );
    }

    private void updateTimemarkerMouseListenerValues()
    {
        Bounds plotBounds = chartState.getComputations().getPlotBounds();
        handleTimelineMouseListener.updateValues( plotBounds.getLeft(),
                                                  plotBounds.getTop(),
                                                  plotBounds.getWidth(),
                                                  plotBounds.getHeight(),
                                                  getChartStartTime(),
                                                  getChartEndTime() );
    }

    private void getAndDrawTimemarkers(PaintEvent pe)
    {
        SortedSet<TimeMarker> timeMarkersToDraw = getTimeMarkersToDraw();
        List<TimeMarker> sortedTimeMarkers = moveSelectedTimeMarkerToEndToRenderItAsLast( timeMarkersToDraw );
        for (TimeMarker line : sortedTimeMarkers)
        {
            if (line.isEnabled())
            {
                double xPositionForTimeLine = getPositionForTimestamp( line );
                if (xPositionForTimeLine != TimestampPositionInChartConverter.INVALID_VALUE)
                {
                    drawTimemarker( pe, line, xPositionForTimeLine );
                }
            }
        }
    }

    protected abstract SortedSet<TimeMarker> getTimeMarkersToDraw();

    private List<TimeMarker> moveSelectedTimeMarkerToEndToRenderItAsLast(SortedSet<TimeMarker> timeMarkers)
    {
        TimeMarker selectedTimeMarker = timeMarkerManager.getCurrentSelectedTimeMarker();
        List<TimeMarker> resultList = new ArrayList<TimeMarker>( timeMarkers );

        if (selectedTimeMarker == null)
        {
            return resultList;
        }

        if (timeMarkers.contains( selectedTimeMarker ))
        {
            resultList.remove( selectedTimeMarker );
            resultList.add( selectedTimeMarker );
        }
        return resultList;
    }

    private double getPositionForTimestamp(TimeMarker line)
    {
        return TimestampPositionInChartConverter
                .calculatePositionForTimestamp( chartState.getComputations().getPlotBounds().getLeft(),
                                                chartState.getComputations().getPlotBounds().getWidth(),
                                                line.getTimestamp(),
                                                getChartStartTime(),
                                                getChartEndTime() - getChartStartTime() );
    }

    protected long getChartEndTime()
    {
        return analysisTimespanPreferences.getAnalysisTimespanEnd();
    }

    protected long getChartStartTime()
    {
        return analysisTimespanPreferences.getAnalysisTimespanStart();
    }

    private void drawTimemarker(PaintEvent pe, TimeMarker timeMarker, double xPositionForTimeLine)
    {
        TimeMarker selected = timeMarkerManager.getCurrentSelectedTimeMarker();
        boolean timeMarkerSelected = false;
        if (selected != null && selected.equals( timeMarker ))
        {
            timeMarkerSelected = true;
        }

        int top = (int)chartState.getComputations().getPlotBounds().getTop();
        int height = (int)chartState.getComputations().getPlotBounds().getHeight();
        int bottom = top + height;

        Point topPoint = new Point( (int)xPositionForTimeLine, top );
        Point bottomPoint = new Point( (int)xPositionForTimeLine, bottom );

        TimeMarkerLabelStyle style = getTimeMarkerLabelStyle();
        if (style == TimeMarkerLabelStyle.HORIZONTAL)
        {
            ChartTimeMarkerRenderer
                    .drawTimeMarkerHorizontalText( pe.gc,
                                                   topPoint,
                                                   bottomPoint,
                                                   timeMarker,
                                                   timeMarkerSelected,
                                                   resManager );
        }
        if (style == TimeMarkerLabelStyle.TILTED)
        {
            ChartTimeMarkerRenderer.drawTimeMarkerTiltedText( pe.gc,
                                                              topPoint,
                                                              bottomPoint,
                                                              timeMarker,
                                                              timeMarkerSelected,
                                                              resManager );
        }

    }

    protected abstract TimeMarkerLabelStyle getTimeMarkerLabelStyle();

    @Override
    public void onTimeMarkerChanged()
    {
        redraw();
    }

    @Override
    public void onTimeMarkersChanged(List<TimeMarker> timeMarkers)
    {
        redraw();
    }

    @Override
    public void onAnalysisTimespanChanged(long analysisTimespanStart, long analysisTimespanEnd)
    {
        if (getRunMode() == RunMode.ANALYSIS)
        {
            imageData = null;
        }
        redraw();
    }

    @Override
    public void onAnalysisTimespanLengthChanged(long timespanMicros)
    {
        redraw();
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> changedChannels)
    {
        List<RuntimeEventChannel<?>> channelsInChart = modelToDisplay.getChannels();
        for (RuntimeEventChannel<?> channel : changedChannels)
        {
            if (channelsInChart.contains( channel ))
            {
                updateChartImage();
                break;
            }
        }
    }

    protected abstract RunMode getRunMode();

    @Override
    public void onFullTimespanChanged(long start, long end)
    {
    }

    @Override
    public void onTimeMarkerSelected(TimeMarker selectedTimeMarker)
    {
    }
}
