/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.selectelement;

import java.util.List;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebsolys.core.targetdata.api.listener.SelectedElementsChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.SelectedElementsService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class StatusLineTextNotifyUseCaseImpl implements StatusLineTextNotifyUseCase, SelectedElementsChangedListener
{

    private StatusLineTextNotifyCallback statusCallback;
    private final SelectedElementsService selectedElmntService;
    private final PreferencesService preferenceService;
    private final static String DELTA_TIME_INFO = "  Delta Time: ";

    public StatusLineTextNotifyUseCaseImpl(StatusLineTextNotifyCallback callback, SelectedElementsService service,
            PreferencesService pService)
    {
        this.selectedElmntService = service;
        this.selectedElmntService.register( this );
        this.statusCallback = callback;
        this.preferenceService = pService;
    }

    @Override
    public void unregister()
    {
        this.selectedElmntService.unregister( this );
        this.statusCallback = null;

    }

    private String calculateTimeStamp(List<TimebasedObject> timeStamps)
    {
        if (timeStamps.isEmpty() || timeStamps.size() == 1)
        {
            return "";
        }

        long diff = timeStamps.get( timeStamps.size() - 1 ).getTimestamp() - timeStamps.get( 0 ).getTimestamp();

        String format = preferenceService.getTimestampFormatPreferences();

        if (format != null)
        {
            TimeFormatter timeFormatter = new TimeFormatter( format );

            return DELTA_TIME_INFO + timeFormatter.formatMicros( diff );
        }
        else
        {
            return "";
        }

    }

    @Override
    public void onNewTimeStamps(final List<TimebasedObject> timeStamps)
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "StatusLineTextNotifyUseCase.onNewTimeStamps",
                                                       () -> postNewTime( calculateTimeStamp( timeStamps ) ) ) );
    }

    private void postNewTime(final String newStatus)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (statusCallback != null)
                {
                    statusCallback.onNewStatus( newStatus );
                }
            }
        } );
    }

}
