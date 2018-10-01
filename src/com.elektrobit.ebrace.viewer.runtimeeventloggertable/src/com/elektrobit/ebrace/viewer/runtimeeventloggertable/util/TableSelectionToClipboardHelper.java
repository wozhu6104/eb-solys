/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.util;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TableSelectionToClipboardHelper implements PreferencesNotifyCallback
{
    private final Clipboard clipboard;
    private TimeFormatter formatter;
    private final PreferencesNotifyUseCase preferencesNotifyUseCase;

    public TableSelectionToClipboardHelper(Display display)
    {
        clipboard = new Clipboard( display );
        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );
    }

    public void copy(IStructuredSelection selection)
    {
        String data = convertTableSelectionToString( selection );
        copyTextToClipboard( data );
    }

    private String convertTableSelectionToString(IStructuredSelection selection)
    {
        Iterator<?> iterator = selection.iterator();
        String data = "";
        while (iterator.hasNext())
        {
            Object nextElement = iterator.next();
            if (nextElement instanceof TimeMarker)
            {
                TimeMarker timeMarker = (TimeMarker)nextElement;
                data += formatter.formatMicros( timeMarker.getTimestamp() ) + " | TIMEMARKER | " + timeMarker.getName()
                        + "\n";
            }
            else if (nextElement instanceof RuntimeEvent<?>)
            {
                RuntimeEvent<?> event = (RuntimeEvent<?>)nextElement;
                data += formatter.formatMicros( event.getTimestamp() ) + " | "
                        + event.getRuntimeEventChannel().getName() + " | "
                        + event.getSummary().toString().replaceAll( "\\n", "" ) + "\n";
            }

        }
        return data;
    }

    private void copyTextToClipboard(String data)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "text", data );

        if (data.length() > 0)
        {
            clipboard.setContents( new Object[]{data}, new Transfer[]{TextTransfer.getInstance()} );
        }
    }

    public void dispose()
    {
        preferencesNotifyUseCase.unregister();
        clipboard.dispose();
    }
    
    @Override
    public void onTimestampFormatChanged(String newTimestampFormat)
    {
        this.formatter = new TimeFormatter( newTimestampFormat );
    }

}
