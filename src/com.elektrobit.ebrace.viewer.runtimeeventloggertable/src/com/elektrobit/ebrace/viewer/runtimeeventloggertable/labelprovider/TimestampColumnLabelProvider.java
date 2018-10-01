/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimestampColumnLabelProvider extends ColumnLabelProvider implements RowFormatter, PreferencesNotifyCallback
{
    private final TableModel model;
    private final TimeMarkerManager timeMarkerManager;

    private final TableCellBackgroundColorCreator backgroundColorCreator;
    private final Font timeMarkerFont = new Font( null, new FontData( "Arial", 9, SWT.BOLD | SWT.ITALIC ) );

    private final PreferencesNotifyUseCase preferencesNotifyUseCase;
    private TimeFormatter formatter;

    public TimestampColumnLabelProvider(TableModel model, TimeMarkerManager timeMarkerManager,
            TableCellBackgroundColorCreator backgroundColorCreator)
    {
        this.model = model;
        this.timeMarkerManager = timeMarkerManager;
        this.backgroundColorCreator = backgroundColorCreator;

        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormatPreferences)
    {
        this.formatter = new TimeFormatter( timestampFormatPreferences );
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getText(Object element)
    {
        String result = null;

        long timestamp = -1;
        if (element instanceof TimeMarker)
        {
            result = ((TimeMarker)element).getName();
        }
        else if (element instanceof RuntimeEvent)
        {
            timestamp = ((RuntimeEvent)element).getTimestamp();
            if (timestamp != -1)
            {
                result = formatter.formatMicros( timestamp );
            }
        }

        return result;
    }

    @Override
    public Color getBackground(Object element)
    {
        return backgroundColorCreator.getBackground( model, element );
    }

    @Override
    public Font getFont(Object element)
    {
        Font result = null;
        if (element instanceof TimeMarker)
        {
            if (element.equals( timeMarkerManager.getCurrentSelectedTimeMarker() ))
            {
                result = timeMarkerFont;
            }
        }

        return result;
    }

    @Override
    public void dispose()
    {
        preferencesNotifyUseCase.unregister();
        backgroundColorCreator.dispose();
        super.dispose();
        timeMarkerFont.dispose();
    }
}
