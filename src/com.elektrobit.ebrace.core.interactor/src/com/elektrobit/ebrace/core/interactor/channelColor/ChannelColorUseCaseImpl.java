/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.channelColor;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorChannelListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.ColorSettingsPreferenceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelColorUseCaseImpl implements ChannelColorUseCase, ColorChannelListener
{

    private final ChannelColorProviderService channelColorProviderService;
    private ChannelColorCallback callback;
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private Set<RuntimeEventChannel<?>> channelsSet;
    private final Set<RuntimeEventChannel<?>> channelsSet1 = new HashSet<RuntimeEventChannel<?>>();
    private final Set<RuntimeEventChannel<?>> channelsSet2 = new HashSet<RuntimeEventChannel<?>>();

    private volatile TimerTask refreshTask = null;
    private static final int REFRESH_TIMEOUT_MS = 500;

    public ChannelColorUseCaseImpl(ChannelColorCallback callback,
            ChannelColorProviderService channelColorProviderService, RuntimeEventAcceptor runtimeEventAcceptor)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        this.channelColorProviderService = channelColorProviderService;
        this.callback = callback;
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        channelColorProviderService.registerColorChannelListener( this );
        channelsSet = channelsSet1;
    }

    @Override
    public void setColorForChannel(final String channelName, final int r, final int g, final int b)
    {
        RuntimeEventChannel<?> channel = runtimeEventAcceptor.getRuntimeEventChannel( channelName, null );
        channelColorProviderService.setColorForChannel( channel, r, g, b );
    }

    @Override
    public void setColorForChannel(RuntimeEventChannel<?> channel, int r, int g, int b)
    {
        channelColorProviderService.setColorForChannel( channel, r, g, b );
    }

    @Override
    public SColor getColorOfChannel(String channelName)
    {
        SColor result = null;
        RuntimeEventChannel<?> channel = runtimeEventAcceptor.getRuntimeEventChannel( channelName, null );
        if (channel == null)
        {
            result = ColorSettingsPreferenceConstants.defaultChannelColors.get( 0 );
        }
        else
        {
            result = getColorOfChannel( channel );
        }
        return result;
    }

    @Override
    public SColor getColorOfChannel(RuntimeEventChannel<?> channel)
    {
        SColor color = channelColorProviderService.getColorForChannel( channel );
        if (color == null)
        {
            color = channelColorProviderService.createAndGetColorForChannel( channel );
        }

        return color;
    }

    @Override
    public boolean channelHasColor(RuntimeEventChannel<?> channel)
    {
        return channelColorProviderService.hasColor( channel );
    }

    @Override
    public void onColorAssignedToChannel(RuntimeEventChannel<?> channel)
    {
        channelsSet.add( channel );
        startRefreshTimerIfNot();
    }

    private void startRefreshTimerIfNot()
    {
        if (refreshTask == null)
        {
            refreshTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    postColorAssignedToCallback();
                    refreshTask = null;
                }
            };
            UseCaseExecutor.scheduleDelayed( refreshTask, REFRESH_TIMEOUT_MS );
        }
    }

    private void postColorAssignedToCallback()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback == null)
                    return;

                Set<RuntimeEventChannel<?>> channelsSetLocal = channelsSet;
                if (channelsSet == channelsSet1)
                {
                    channelsSet = channelsSet2;
                }
                else if (channelsSet == channelsSet2)
                {
                    channelsSet = channelsSet1;
                }

                callback.onColorAssigned( channelsSetLocal );
                channelsSetLocal.clear();
            }
        } );
    }

    @Override
    public void unregister()
    {
        channelColorProviderService.unregisterColorChannelListener( this );
        if (refreshTask != null)
            refreshTask.cancel();
        callback = null;
    }

}
