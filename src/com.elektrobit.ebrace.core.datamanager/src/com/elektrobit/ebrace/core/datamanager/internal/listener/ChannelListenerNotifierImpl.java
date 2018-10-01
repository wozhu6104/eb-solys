/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

@Component
public class ChannelListenerNotifierImpl implements ChannelListenerNotifier
{
    private enum ACTION {
        ONE_CHANGED, ALL_CHANGED, REMOVED
    };

    private final static int NOTIFY_INTERVAL_MS = 200;

    private final Timer timer = new Timer( "ChannelListenerNotifier" );
    private final TimerTask timerTask = new EventListenerNotifierTimerTask();
    private final int updateIntervalMs;
    private boolean timerIsRunning = false;

    private final GenericListenerCaller<ChannelsContentChangedListener> caller1 = new GenericListenerCaller<ChannelsContentChangedListener>();
    private final GenericListenerCaller<ChannelsContentChangedListener> caller2 = new GenericListenerCaller<ChannelsContentChangedListener>();
    private GenericListenerCaller<ChannelsContentChangedListener> currentCaller;

    private final Map<RuntimeEventChannel<?>, Set<ChannelsContentChangedListener>> listeners = new HashMap<RuntimeEventChannel<?>, Set<ChannelsContentChangedListener>>();

    public ChannelListenerNotifierImpl(int updateIntervalMs)
    {
        this.updateIntervalMs = updateIntervalMs;
        currentCaller = caller1;
    }

    public ChannelListenerNotifierImpl()
    {
        this( NOTIFY_INTERVAL_MS );
    }

    @Override
    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channels)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "listener", listener );
        RangeCheckUtils.assertReferenceParameterNotNull( "channels", channels );

        for (RuntimeEventChannel<?> channel : channels)
        {
            Set<ChannelsContentChangedListener> set = listeners.get( channel );
            if (set == null)
            {
                set = new HashSet<ChannelsContentChangedListener>();
                listeners.put( channel, set );
            }
            set.add( listener );
        }
    }

    @Override
    public void unregisterListener(ChannelsContentChangedListener listener)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "listener", listener );

        for (Set<ChannelsContentChangedListener> set : listeners.values())
        {
            set.remove( listener );
        }
    }

    @Override
    public void notifyChannelChanged(RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "channel", channel );

        Set<ChannelsContentChangedListener> set = listeners.get( channel );
        if (set != null)
        {
            currentCaller.addAll( set );
        }

        ((EventListenerNotifierTimerTask)timerTask).setAction( ACTION.ONE_CHANGED );
        ((EventListenerNotifierTimerTask)timerTask).setChannel( channel );

        // // direct mode for tests
        if (updateIntervalMs == 0)
        {
            timerTask.run();
            return;
        }

        startTimerIfNotRunning();
    }

    @Override
    public void notifyAllChannelsChanged()
    {
        for (Set<ChannelsContentChangedListener> setOfListener : listeners.values())
        {
            currentCaller.addAll( setOfListener );
        }
        ((EventListenerNotifierTimerTask)timerTask).setAction( ACTION.ALL_CHANGED );
        ((EventListenerNotifierTimerTask)timerTask).setChannel( null );
        startTimerIfNotRunning();
    }

    @Override
    public void notifyChannelRemoved(RuntimeEventChannel<?> channel)
    {
        for (Set<ChannelsContentChangedListener> setOfListener : listeners.values())
        {
            currentCaller.addAll( setOfListener );
        }

        ((EventListenerNotifierTimerTask)timerTask).setAction( ACTION.REMOVED );
        ((EventListenerNotifierTimerTask)timerTask).setChannel( channel );
        startTimerIfNotRunning();
    }

    private void startTimerIfNotRunning()
    {
        if (!timerIsRunning)
        {
            timer.scheduleAtFixedRate( timerTask, 0, updateIntervalMs );
            timerIsRunning = true;
        }
    }

    private class EventListenerNotifierTimerTask extends TimerTask
    {
        private ACTION action;
        private RuntimeEventChannel<?> channel;

        public void setAction(ACTION action)
        {
            this.action = action;
        }

        public void setChannel(RuntimeEventChannel<?> channel)
        {
            this.channel = channel;
        }

        @Override
        public void run()
        {
            boolean notifyRequired = currentCaller.size() > 0;
            if (notifyRequired)
            {
                GenericListenerCaller<ChannelsContentChangedListener> localCaller = currentCaller;

                switchCaller();
                localCaller.notifyListeners( new Notifier<ChannelsContentChangedListener>()
                {

                    @Override
                    public void notify(ChannelsContentChangedListener listener)
                    {
                        switch (action)
                        {
                            case ALL_CHANGED :
                                listener.onChannelsContentChanged();
                                break;
                            case ONE_CHANGED :
                                listener.onChannelsContentChanged();
                                break;
                            case REMOVED :
                                listener.onChannelRemoved( channel );
                                break;
                            default :
                                break;
                        }
                    }
                } );
                localCaller.clear();
            }
        }

    }

    private void switchCaller()
    {
        synchronized (currentCaller)
        {
            if (currentCaller == caller1)
            {
                currentCaller = caller2;
            }
            else if (currentCaller == caller2)
            {
                currentCaller = caller1;
            }
            else
            {
                throw new RuntimeException( "currentCaller not assigned correctly" );
            }
        }
    }

}
