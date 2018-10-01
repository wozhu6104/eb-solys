/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.listeners;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.elektrobit.ebrace.core.interactor.api.tableinput.FilterChangedListener;

public class FilteredTextFieldModifyListener implements ModifyListener
{
    private final Timer searchTimer = new Timer();
    private TimerTask searchTimerTask;

    private final List<FilterChangedListener> listeners = new CopyOnWriteArrayList<FilterChangedListener>();

    @Override
    public void modifyText(ModifyEvent e)
    {
        final CCombo combo = (CCombo)e.widget;
        final String newFilter = combo.getText();

        if (searchTimerTask != null)
        {
            searchTimerTask.cancel();
        }
        searchTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                notifyInputChangedListeners( newFilter );
            }
        };
        searchTimer.schedule( searchTimerTask, 1000 );
    }

    public void addFilterChangedListener(FilterChangedListener listener)
    {
        listeners.add( listener );
    }

    public void removeFilterChangedListener(FilterChangedListener listener)
    {
        listeners.remove( listener );
    }

    private void notifyInputChangedListeners(String newFilter)
    {
        for (FilterChangedListener listener : listeners)
        {
            listener.onFilterTextChanged( newFilter );
        }
    }
}
