/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInListConverter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyUseCase;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

import lombok.Setter;

public class TableRulerAnalysisTimespanPainter implements PaintListener, DisposeListener, LiveModeNotifyCallback
{
    private static final int PADDING = 5;

    private final TableRuler ruler;

    @Setter
    private long analysisTimespanEnd = 1;
    @Setter
    private long analysisTimespanStart = 0;

    private final LiveModeNotifyUseCase liveModeNotifyUseCase;

    private boolean isLiveMode;

    public TableRulerAnalysisTimespanPainter(TableRuler ruler)
    {
        this.ruler = ruler;
        ruler.addDisposeListener( this );
        liveModeNotifyUseCase = UseCaseFactoryInstance.get().makeLiveModeNotifyUseCase( this );
    }

    @Override
    public void paintControl(PaintEvent e)
    {
        if (!isLiveMode)
        {
            paintTimespanArea( e.gc );
        }
    }

    private void paintTimespanArea(GC gc)
    {
        gc.setForeground( Display.getCurrent().getSystemColor( SWT.COLOR_BLACK ) );
        gc.setLineWidth( 1 );

        gc.setBackground( ColorPreferences.ANALYSIS_TIMESPAN_COLOR_RULER );

        Collection<Integer> positionsValues = calculateTimestampPositions().values();
        Integer[] startEndPositions = positionsValues.toArray( new Integer[2] );

        if (startEndPositions[0] < startEndPositions[1])
        {
            paintTimespanAreaVertical( gc, startEndPositions[0], startEndPositions[1] );
        }
        else
        {
            paintTimespanAreaVertical( gc, startEndPositions[1], startEndPositions[0] );
        }

    }

    private Map<Long, Integer> calculateTimestampPositions()
    {
        List<Long> allTimestamps = new ArrayList<Long>();
        allTimestamps.add( analysisTimespanStart );
        allTimestamps.add( analysisTimespanEnd );
        TimestampPositionInListConverter timestampInListToPositionConverter = new TimestampPositionInListConverter( getInputEvents(),
                                                                                                                    allTimestamps,
                                                                                                                    ruler.getBounds().height
                                                                                                                            - PADDING );
        Map<Long, Integer> timestampPositions = timestampInListToPositionConverter.getTimestampPositions();
        return timestampPositions;
    }

    private List<TimebasedObject> getInputEvents()
    {
        return ruler.getTableData();
    }

    private void paintTimespanAreaVertical(GC gc, int timespanStartPixelPos, int timespanEndPixelPos)
    {
        int height = timespanEndPixelPos - timespanStartPixelPos;
        gc.setAlpha( ColorPreferences.ANALYSIS_TIMESPAN_FILL_ALPHA );
        Rectangle analysisTimespanRectangle = new Rectangle( 0,
                                                             timespanStartPixelPos,
                                                             ruler.getBounds().width - 17,
                                                             height );
        gc.fillRectangle( analysisTimespanRectangle );
        gc.setAlpha( 255 );
        gc.drawRectangle( analysisTimespanRectangle );
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        liveModeNotifyUseCase.unregister();
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        this.isLiveMode = isLiveMode;
    }
}
