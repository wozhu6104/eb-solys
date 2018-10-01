/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimestampColumnLabelProvider extends ColumnLabelProvider implements RowFormatter, PreferencesNotifyCallback
{
    private TimeFormatter formatter;

    protected final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    private final PreferencesNotifyUseCase makePreferencesNotifyUseCase;

    private final Viewer viewer;

    public TimestampColumnLabelProvider(Viewer viewer)
    {
        this.viewer = viewer;
        makePreferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
    }

    @Override
    public String getText(Object element)
    {
        long timestamp = -1;
        if (element instanceof RuntimeEvent<?>)
        {
            timestamp = ((RuntimeEvent<?>)element).getTimestamp();
        }
        else if (element instanceof TimebasedObject)
        {
            timestamp = ((TimebasedObject)element).getTimestamp();
        }
        if (timestamp != -1)
        {
            return formatter.formatMicros( timestamp );
        }
        return null;
    }

    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof TimeMarker && element.equals( timeMarkerManager.getCurrentSelectedTimeMarker() ))
        {
            return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        }
        return super.getBackground( element );
    }

    private void setTimestampFormat(String format)
    {
        this.formatter = new TimeFormatter( format );
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormatPreferences)
    {
        setTimestampFormat( timestampFormatPreferences );
        viewer.refresh();
    }

    @Override
    public void dispose()
    {
        makePreferencesNotifyUseCase.unregister();
        super.dispose();
    }
}
