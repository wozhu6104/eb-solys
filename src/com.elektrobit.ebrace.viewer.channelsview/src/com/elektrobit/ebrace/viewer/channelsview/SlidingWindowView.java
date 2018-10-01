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

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

import lombok.extern.log4j.Log4j;

@Log4j
public class SlidingWindowView extends ViewPart
        implements
            PreferencesNotifyCallback,
            TimeMarkersNotifyCallback,
            OpenFileInteractionCallback,
            LoadDataChunkInteractionCallback,
            LoadDataChunkNotifyCallback,
            SystemCPUValuesNotifyCallback
{
    private static final int REDRAW_EVERY_N_MOUSE_MOVE_PIXELS = 3;
    private static final double SELECTION_PADDING_LEFT_PORTION = 0.06;
    private static final int TIMEMARKER_LINE_WIDTH = 2;
    private static final RGB WHITE_COLOR = new RGB( 255, 255, 255 );
    private static final RGB BLACK_COLOR = new RGB( 0, 0, 0 );
    private static final RGB LINE_COLOR = new RGB( 100, 100, 140 );

    private Long selectionLength = 0L;

    private Long startTime = 0L;
    private Long endTime = 0L;
    private Long loadedStartTime = 0L;
    private Long loadedEndTime = 0L;
    private Long currentMousePositionTime;

    private Canvas canvas;
    private ResourceManager resManager;
    private PreferencesNotifyUseCase preferencesNotifyUseCase;
    private TimeFormatter timeFormatter;

    private final Color loadedIntervalColor = Display.getDefault().getSystemColor( SWT.COLOR_CYAN );
    private Color whiteColor;
    private TimeMarkersNotifyUseCase timeMarkersNotifyUseCase;
    private List<TimeMarker> timeMarkers = Collections.emptyList();
    private OpenFileInteractionUseCase openFileInteractionUseCase;
    private LoadDataChunkInteractionUseCase loadDataChunkInteractionUseCase;
    private LoadDataChunkNotifyUseCase loadDataChunkNotifyUseCase;
    private Color blackColor;
    private SystemCPUValuesNotifyUseCase systemCPUValuesNotifyUseCase;
    private SlidingWindowChart slidingWindowChart;
    private Color lineColor;

    @Override
    public void createPartControl(Composite parent)
    {
        canvas = new Canvas( parent, SWT.BORDER | SWT.NO_BACKGROUND );
        resManager = new LocalResourceManager( JFaceResources.getResources(), canvas );

        whiteColor = resManager.createColor( WHITE_COLOR );
        blackColor = resManager.createColor( BLACK_COLOR );
        lineColor = resManager.createColor( LINE_COLOR );

        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
        timeMarkersNotifyUseCase = UseCaseFactoryInstance.get().makeTimeMarkersNotifyUseCase( this );
        openFileInteractionUseCase = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
        loadDataChunkInteractionUseCase = UseCaseFactoryInstance.get().makeLoadDataChunkInteractionUseCase( this );
        loadDataChunkNotifyUseCase = UseCaseFactoryInstance.get().makeLoadDataChunkNotifyUseCase( this );
        systemCPUValuesNotifyUseCase = UseCaseFactoryInstance.get().makeSystemCPUValuesNotifyUseCase( this );

        registerMouseListeners();
    }

    private void registerMouseListeners()
    {
        canvas.addPaintListener( new PaintListener()
        {
            @Override
            public void paintControl(PaintEvent e)
            {
                onPaint( e );
            }
        } );

        canvas.addMouseListener( new MouseListener()
        {
            @Override
            public void mouseUp(MouseEvent e)
            {
            }

            @Override
            public void mouseDown(MouseEvent e)
            {
                long clickedTime = computeTimeUnderMouseCursor( e );
                onTimeSelected( clickedTime );
            }

            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
            }
        } );

        canvas.addMouseTrackListener( new MouseTrackListener()
        {
            @Override
            public void mouseHover(MouseEvent e)
            {
                computerMousePositionAndRedraw( e );
                setTooltip( e );
            }

            @Override
            public void mouseExit(MouseEvent e)
            {
                currentMousePositionTime = null;
                canvas.redraw();
            }

            @Override
            public void mouseEnter(MouseEvent e)
            {
            }
        } );

        canvas.addMouseMoveListener( new MouseMoveListener()
        {
            int counter = 0;

            @Override
            public void mouseMove(MouseEvent e)
            {
                counter++;
                if (counter % REDRAW_EVERY_N_MOUSE_MOVE_PIXELS == 0)
                {
                    computerMousePositionAndRedraw( e );
                }
            }

        } );
    }

    private void computerMousePositionAndRedraw(MouseEvent e)
    {
        currentMousePositionTime = computeTimeUnderMouseCursor( e );
        currentMousePositionTime = shiftTimeAwayFromLeftOrRightEdge( currentMousePositionTime );
        canvas.redraw();
    }

    private void setTooltip(MouseEvent e)
    {
        String timeString = timeFormatter.formatMicros( currentMousePositionTime );
        canvas.setToolTipText( "Start loading from " + timeString );
    }

    private long shiftTimeAwayFromLeftOrRightEdge(long time)
    {
        long lastPossibleStartTime = endTime - selectionLength;
        if (time > lastPossibleStartTime)
        {
            return lastPossibleStartTime;
        }
        else
        {
            time -= selectionLength * SELECTION_PADDING_LEFT_PORTION;
            if (time < startTime)
            {
                return startTime;
            }
            else
            {
                return time;
            }
        }
    }

    private long computeTimeUnderMouseCursor(MouseEvent e)
    {
        Rectangle clientArea = canvas.getClientArea();
        int width = clientArea.width;
        int leftEdge = clientArea.x;
        double clickedTime = TimestampPositionInChartConverter
                .calculateTimestampForClick( leftEdge, width, e.x, startTime, endTime - startTime );
        return Math.round( clickedTime );
    }

    private void onPaint(PaintEvent e)
    {
        paintWithDoubleBuffering( e );
    }

    private void paintWithDoubleBuffering(PaintEvent e)
    {
        Image buffer = new Image( Display.getDefault(), canvas.getBounds() );
        GC bufferGc = new GC( buffer );
        paintOnContext( bufferGc );
        Rectangle b = canvas.getBounds();
        e.gc.drawImage( buffer, 0, 0, b.width, b.height, 0, 0, b.width, b.height );
        buffer.dispose();
        bufferGc.dispose();
    }

    private void paintOnContext(GC gc)
    {
        paintBackground( gc );
        if (loadedStartTime != null && loadedEndTime != null)
        {
            paintLoadedInterval( gc );
        }

        paintMousePositionSelectionRectangle( gc );
        paintSlidingWindowChart( gc );
        paintTimeMarkers( gc );
    }

    private void paintBackground(GC gc)
    {
        Rectangle clientArea = canvas.getClientArea();
        int width = clientArea.width;
        int height = clientArea.height;

        gc.setBackground( whiteColor );
        gc.fillRectangle( 0, 0, width, height );
    }

    private void paintLoadedInterval(GC gc)
    {
        Rectangle clientArea = canvas.getClientArea();
        int width = clientArea.width;
        int height = clientArea.height;
        int leftEdge = clientArea.x;
        int startPosition = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( leftEdge, width, loadedStartTime, startTime, endTime - startTime );
        int endPosition = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( leftEdge, width, loadedEndTime, startTime, endTime - startTime );

        gc.setBackground( loadedIntervalColor );
        gc.setForeground( blackColor );
        gc.setAlpha( 30 );
        gc.fillRectangle( startPosition, 0, endPosition - startPosition, height );
        gc.setAlpha( 255 );
    }

    private void paintMousePositionSelectionRectangle(GC gc)
    {
        if (currentMousePositionTime == null)
        {
            return;
        }

        Rectangle clientArea = canvas.getClientArea();
        int width = clientArea.width;
        int height = clientArea.height;
        int leftEdge = clientArea.x;
        int startPosition = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( leftEdge,
                                                width,
                                                currentMousePositionTime,
                                                startTime,
                                                endTime - startTime );
        long filledRectangleTimeLength = loadedEndTime - loadedStartTime;
        int filledRectangleWidth = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( leftEdge,
                                                width,
                                                startTime + filledRectangleTimeLength,
                                                startTime,
                                                endTime - startTime );

        gc.setBackground( blackColor );
        gc.setForeground( blackColor );
        gc.setAlpha( 20 );
        gc.fillRectangle( startPosition, 0, filledRectangleWidth + 1, height );
        gc.setAlpha( 255 );
    }

    private void paintSlidingWindowChart(GC gc)
    {
        if (slidingWindowChart != null)
        {
            slidingWindowChart.resize( canvas.getClientArea().width, canvas.getClientArea().height );

            Image chart = slidingWindowChart.renderChartToImage();
            if (chart != null)
            {
                gc.drawImage( chart, 0, 0 );
                chart.dispose();
            }
        }
    }

    private void paintTimeMarkers(GC gc)
    {
        Rectangle clientArea = canvas.getClientArea();
        int width = clientArea.width;
        int height = clientArea.height;
        int leftEdge = clientArea.x;
        gc.setLineWidth( TIMEMARKER_LINE_WIDTH );
        for (TimeMarker timeMarker : timeMarkers)
        {
            SColor raceColor = timeMarker.getColor();
            Color swtColor = resManager
                    .createColor( new RGB( raceColor.getRed(), raceColor.getGreen(), raceColor.getBlue() ) );
            gc.setForeground( swtColor );
            int position = (int)TimestampPositionInChartConverter
                    .calculatePositionForTimestamp( leftEdge,
                                                    width,
                                                    timeMarker.getTimestamp(),
                                                    startTime,
                                                    endTime - startTime );

            gc.drawLine( position, 0, position, height );
        }
    }

    private void onDataChanged(Long startTime, Long endTime, Long loadedStartTime, Long loadedEndTime,
            Long selectionLength)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.loadedStartTime = loadedStartTime;
        this.loadedEndTime = loadedEndTime;
        this.selectionLength = selectionLength;

        if (selectionLength == null || selectionLength == 0)
        {
            selectionLength = loadedEndTime - loadedStartTime;
        }

        canvas.redraw();
    }

    private void onTimeSelected(long time)
    {
        time = shiftTimeAwayFromLeftOrRightEdge( time );
        loadDataChunkInteractionUseCase.loadDataChunk( time, selectionLength );
    }

    @Override
    public void setFocus()
    {
        canvas.setFocus();
    }

    @Override
    public void dispose()
    {
        preferencesNotifyUseCase.unregister();
        timeMarkersNotifyUseCase.unregister();
        openFileInteractionUseCase.unregister();
        loadDataChunkInteractionUseCase.unregister();
        loadDataChunkNotifyUseCase.unregister();
        systemCPUValuesNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    public void onTimestampFormatChanged(String newTimestampFormat)
    {
        timeFormatter = new TimeFormatter( newTimestampFormat );
    }

    @Override
    public void onTimeMarkersChanged(List<TimeMarker> timeMarkers)
    {
        this.timeMarkers = timeMarkers;
        canvas.redraw();
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
    }

    @Override
    public void onLoadDataFromSuccessful(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
    }

    @Override
    public void onDataChunkChanged(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime,
            Long selectionLength)
    {
        onDataChanged( fileStartTime, fileEndTime, chunkStartTime, chunkEndTime, selectionLength );
    }

    @Override
    public void onLoadDataFromFailed()
    {
        resetData();
    }

    @Override
    public void onLoadDataAlreadyActive()
    {
    }

    private void resetData()
    {
        slidingWindowChart = null;
        onDataChanged( 0L, 0L, 0L, 0L, 0L );
    }

    @Override
    public void onSystemCPUValuesUpdated(List<RTargetHeaderCPUValue> systemCPUValues)
    {
        if (systemCPUValues == null)
        {
            resetData();
        }
        else if (systemCPUValues.isEmpty())
        {
        }
        else
        {
            slidingWindowChart = new SlidingWindowChart( canvas.getClientArea().width, canvas.getClientArea().height );

            TargetHeaderCPUValueDataProvider dataProvider = new TargetHeaderCPUValueDataProvider( systemCPUValues );
            Trace trace = new Trace( "CPU-Values",
                                     slidingWindowChart.getPrimaryXAxis(),
                                     slidingWindowChart.getPrimaryYAxis(),
                                     dataProvider );

            trace.setAntiAliasing( true );
            trace.setTraceType( TraceType.STEP_VERTICALLY );
            trace.setPointStyle( PointStyle.NONE );
            trace.setTraceColor( lineColor );
            trace.setLineWidth( 1 );

            slidingWindowChart.addTrace( trace );

            canvas.redraw();

            openSlidingWindowChartIfDebugLogActive( dataProvider );
        }

    }

    private void openSlidingWindowChartIfDebugLogActive(TargetHeaderCPUValueDataProvider dataProvider)
    {
        if (log.isDebugEnabled())
        {
            final Shell shell = new Shell();
            shell.setSize( canvas.getClientArea().width, canvas.getClientArea().height );
            shell.open();

            final LightweightSystem lws = new LightweightSystem( shell );

            XYGraph xyGraph = new XYGraph();
            xyGraph.setTitle( "CPU-Values Debug Chart" );

            lws.setContents( xyGraph );

            Trace trace = new Trace( "CPU-Values Debug",
                                     xyGraph.getPrimaryXAxis(),
                                     xyGraph.getPrimaryYAxis(),
                                     dataProvider );

            trace.setAntiAliasing( true );
            trace.setTraceType( TraceType.STEP_VERTICALLY );
            trace.setPointStyle( PointStyle.NONE );
            trace.setTraceColor( lineColor );
            trace.setLineWidth( 1 );

            xyGraph.addTrace( trace );
            xyGraph.getPrimaryXAxis().setDateEnabled( true );
            xyGraph.performAutoScale();
        }
    }

    @Override
    public void onTimeMarkerSelected(TimeMarker selectedTimeMarker)
    {
    }

    @Override
    public void onFileLoadedSucessfully()
    {
    }

    @Override
    public void onFileLoadingFailed()
    {
    }

    @Override
    public void onFileAlreadyLoaded(String pathToFile)
    {
    }

    @Override
    public void onFileEmpty(String pathToFile)
    {
    }

    @Override
    public void onFileNotFound(String pathToFile)
    {
    }

}
