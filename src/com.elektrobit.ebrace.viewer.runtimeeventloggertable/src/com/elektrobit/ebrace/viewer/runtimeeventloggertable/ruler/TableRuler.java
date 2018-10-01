/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.swt.SWTHelper;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener.TableRulerAnalysisTimespanPainter;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener.TableRulerSearchResultsPainter;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener.TableRulerTaggedEventPainter;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener.TableRulerTimemarkerPainter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TableRuler extends Composite implements TimeMarkersNotifyCallback, AnalysisTimespanNotifyCallback
{
    private final TimeMarkersNotifyUseCase timeMarkersNotifyUseCase;
    private final AnalysisTimespanNotifyUseCase analysisTimespanNotifyUseCase;
    private final CommonFilteredTable table;
    private TableRulerSearchResultsPainter searchResultsPainter;
    private TableRulerTimemarkerPainter timemarkerPainter;
    private TableData filterData;
    private TableRulerAnalysisTimespanPainter analysisTimespanPainter;

    public TableRuler(Composite parent, int style, CommonFilteredTable table)
    {
        super( parent, style );
        this.table = table;

        timeMarkersNotifyUseCase = UseCaseFactoryInstance.get().makeTimeMarkersNotifyUseCase( this );
        registerPaintListeners();
        analysisTimespanNotifyUseCase = UseCaseFactoryInstance.get().makeAnalysisTimespanNotifyUseCase( this );

        setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
    }

    private void registerPaintListeners()
    {
        searchResultsPainter = new TableRulerSearchResultsPainter( this, table );
        timemarkerPainter = new TableRulerTimemarkerPainter( this );

        addPaintListener( searchResultsPainter );
        addPaintListener( timemarkerPainter );
        analysisTimespanPainter = new TableRulerAnalysisTimespanPainter( this );
        addPaintListener( analysisTimespanPainter );
        addPaintListener( new TableRulerTaggedEventPainter( this, table ) );
    }

    private void unregisterListeners()
    {
    }

    public void onNewData(TableData filterData)
    {
        this.filterData = filterData;
        searchResultsPainter.onNewData( filterData );
        redraw();
    }

    @Override
    public void dispose()
    {
        unregisterListeners();
        timeMarkersNotifyUseCase.unregister();
        analysisTimespanNotifyUseCase.unregister();
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    public List<TimebasedObject> getTableData()
    {
        if (filterData != null)
        {
            return (List<TimebasedObject>)filterData.getItemsToBeDisplayed();
        }
        else
        {
            return Collections.EMPTY_LIST;
        }
    }

    @SuppressWarnings("unchecked")
    public List<RuntimeEvent<?>> getTaggedEvents()
    {
        if (filterData != null)
        {
            return filterData.getTaggedEvents();
        }
        else
        {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void onTimeMarkersChanged(List<TimeMarker> timeMarkers)
    {
        timemarkerPainter.setTimeMarkers( timeMarkers );
        redraw();
    }

    @Override
    public void onTimeMarkerSelected(TimeMarker selectedTimeMarker)
    {
    }

    @Override
    public void onAnalysisTimespanChanged(long analysisTimespanStart, long analysisTimespanEnd)
    {
        analysisTimespanPainter.setAnalysisTimespanStart( analysisTimespanStart );
        analysisTimespanPainter.setAnalysisTimespanEnd( analysisTimespanEnd );
        SWTHelper.asyncRedraw( this );
    }

    @Override
    public void onAnalysisTimespanLengthChanged(long timespanMicros)
    {
    }

    @Override
    public void onFullTimespanChanged(long start, long end)
    {
    }
}
