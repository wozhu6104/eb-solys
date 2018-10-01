/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.datamanager.timemarker.checker.Checker;
import com.elektrobit.ebrace.core.datamanager.timemarker.checker.TimeMarkerChecker;
import com.elektrobit.ebrace.core.datamanager.timemarker.checker.TimeMarkerInRangeChecker;
import com.elektrobit.ebrace.core.datamanager.timemarker.checker.TimeMarkerNameChecker;
import com.elektrobit.ebrace.core.datamanager.timemarker.checker.TimeMarkerPrefixChecker;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimespanUtil;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

/**
 * This class represents the time marker manager. It is responsible for setting, removing and changing of time markers.
 */
@Component(service = {TimeMarkerManager.class, ResetListener.class}, immediate = true)
public class TimeMarkerManagerImpl implements TimeMarkerManager, TimeMarkerChangedNotifier, ResetListener
{
    private final static String TIMEMARKER_NAME_PREFIX = "TIMEMARKER_";

    private volatile Set<TimeMarker> timeMarkers = new CopyOnWriteArraySet<TimeMarker>();
    private volatile TimeMarker currentSelectedTimeMarker;
    private volatile int timeMarkerCount = 0;
    private volatile boolean visibilityState = true;
    private volatile Set<TimeMarkersChangedListener> listeners = new HashSet<TimeMarkersChangedListener>();

    /**
     * Creates a new time marker and notifies the platform.
     * 
     * @param timestamp
     *            the time stamp which was selected for the time marker.
     * @return the created time marker.
     */
    @Override
    public TimeMarker createNewTimeMarker(long timestamp)
    {
        final TimeMarker newTimeMarker = new TimeMarkerImpl( timestamp,
                                                             TIMEMARKER_NAME_PREFIX + timeMarkerCount,
                                                             this );
        if (timeMarkers.add( newTimeMarker ))
        {
            timeMarkerCount++;
        }

        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.newTimeMarkerCreated( newTimeMarker );
            }
        } );
        return newTimeMarker;
    }

    @Override
    public TimeMarker getTimeMarkerForTimestamp(long timestamp)
    {
        Iterator<TimeMarker> iterator = timeMarkers.iterator();
        while (iterator.hasNext())
        {
            TimeMarker timeMarker = iterator.next();
            if (timeMarker.getTimestamp() == timestamp)
            {
                return timeMarker;
            }
        }
        return null;
    }

    @Override
    public TimeMarker getTimeMarkerForClickedPosition(double d, double e, long minTimeAxisInAnalysisMode,
            long maxTimeAxisInAnalysisMode, int xPosOfMouse)
    {
        Iterator<TimeMarker> iterator = timeMarkers.iterator();
        while (iterator.hasNext())
        {
            TimeMarker marker = iterator.next();
            int xPositionForTimeMarker = (int)TimestampPositionInChartConverter
                    .calculatePositionForTimestamp( d,
                                                    e,
                                                    marker.getTimestamp(),
                                                    minTimeAxisInAnalysisMode,
                                                    maxTimeAxisInAnalysisMode - minTimeAxisInAnalysisMode );
            if (xPositionForTimeMarker > (xPosOfMouse - 5) && xPositionForTimeMarker < (xPosOfMouse + 5))
            {
                if (marker.isEnabled())
                {
                    return marker;
                }
            }
        }
        return null;
    }

    @Override
    public SortedSet<TimeMarker> getAllTimeMarkers()
    {
        SortedSet<TimeMarker> copy = new TreeSet<TimeMarker>( timeMarkers );
        return copy;
    }

    @Override
    public TimeMarker getCurrentSelectedTimeMarker()
    {
        return currentSelectedTimeMarker;
    }

    @Override
    public void setCurrentSelectedTimeMarker(TimeMarker newSelectedTimeMarker)
    {
        setCurrentSelectedTimeMarker( newSelectedTimeMarker, true );
    }

    @Override
    public void setCurrentSelectedTimeMarker(final TimeMarker newSelectedTimeMarker, boolean updateAnalysisTimespan)
    {
        if (newSelectedTimeMarker == null && currentSelectedTimeMarker == null)
        {
            return;
        }
        currentSelectedTimeMarker = newSelectedTimeMarker;
        if (currentSelectedTimeMarker != null && updateAnalysisTimespan)
        {
            TimespanUtil.changeTimespanIfNeeded( currentSelectedTimeMarker.getTimestamp(),
                                                 ANALYSIS_TIMESPAN_CHANGE_REASON.TIME_MARKER_SELECTED );
        }

        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.timeMarkerSelected( newSelectedTimeMarker );
            }
        } );
    }

    @Override
    public void toggleVisibility()
    {
        visibilityState = !visibilityState;
        Iterator<TimeMarker> iterator = timeMarkers.iterator();
        while (iterator.hasNext())
        {
            TimeMarkerImpl timeMarker = (TimeMarkerImpl)iterator.next();
            timeMarker.setEnabled( visibilityState );
        }
        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.allTimeMarkersVisibilityToggled();
            }
        } );
    }

    @Override
    public void toggleVisibility(final TimeMarker timemarker)
    {
        TimeMarkerImpl timeMarkerImpl = (TimeMarkerImpl)timemarker;
        timeMarkerImpl.setEnabled( !timemarker.isEnabled() );
        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.timeMarkerVisibilityChanged( timemarker );
            }
        } );
    }

    @Override
    public boolean getVisibility()
    {
        return visibilityState;
    }

    @Override
    public boolean removeTimeMarkerForClickedPosition(double d, double e, long minTimeAxisInAnalysisMode,
            long maxTimeAxisInAnalysisMode, int xPosOfMouse)
    {
        TimeMarker timeMarker = getTimeMarkerForClickedPosition( d,
                                                                 e,
                                                                 minTimeAxisInAnalysisMode,
                                                                 maxTimeAxisInAnalysisMode,
                                                                 xPosOfMouse );
        if (timeMarker != null)
        {
            removeTimeMarker( timeMarker );
            return true;
        }
        return false;
    }

    @Override
    public void removeAllTimeMarkers()
    {
        this.currentSelectedTimeMarker = null;
        this.timeMarkers.clear();

        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.allTimeMarkersRemoved();
            }
        } );
    }

    @Override
    public void removeTimeMarker(TimeMarker timeMarker)
    {
        removeTimeMarkersBasedOnChecker( new TimeMarkerChecker( timeMarker ) );
    }

    @Override
    public void removeAllTimeMarkersWithPrefix(String prefix)
    {
        removeTimeMarkersBasedOnChecker( new TimeMarkerPrefixChecker( prefix ) );
    }

    @Override
    public void removeAllTimeMarkersWithName(String name)
    {
        removeTimeMarkersBasedOnChecker( new TimeMarkerNameChecker( name ) );
    }

    private void removeTimeMarkersBasedOnChecker(Checker<TimeMarker> checker)
    {
        for (final TimeMarker timeMarker : timeMarkers)
        {
            if (checker.validate( timeMarker ))
            {
                timeMarkers.remove( timeMarker );
                notifyListeners( new Notifier()
                {
                    @Override
                    public void notifyListener(TimeMarkersChangedListener listener)
                    {
                        listener.timeMarkerRemoved( timeMarker );
                    }
                } );
            }
        }
    }

    @Override
    public SortedSet<TimeMarker> getAllTimeMarkersGreaterThanTimestamp(long timestamp)
    {
        return getTimeMarkersBasedOnChecker( new TimeMarkerInRangeChecker( timestamp, false ) );
    }

    @Override
    public SortedSet<TimeMarker> getAllVisibleTimeMarkersGreaterThanTimestamp(long timestamp)
    {
        return getTimeMarkersBasedOnChecker( new TimeMarkerInRangeChecker( timestamp, true ) );
    }

    @Override
    public SortedSet<TimeMarker> getAllTimeMarkersBetweenTimestamp(long start, long end)
    {
        return getTimeMarkersBasedOnChecker( new TimeMarkerInRangeChecker( start, end, false ) );
    }

    @Override
    public SortedSet<TimeMarker> getAllVisibleTimeMarkersBetweenTimestamp(long start, long end)
    {
        return getTimeMarkersBasedOnChecker( new TimeMarkerInRangeChecker( start, end, true ) );
    }

    private SortedSet<TimeMarker> getTimeMarkersBasedOnChecker(Checker<TimeMarker> checker)
    {
        SortedSet<TimeMarker> result = new TreeSet<TimeMarker>();
        Iterator<TimeMarker> iterator = timeMarkers.iterator();
        while (iterator.hasNext())
        {
            TimeMarker timeMarker = iterator.next();
            if (checker.validate( timeMarker ))
            {
                result.add( timeMarker );
            }
        }
        return result;
    }

    @Override
    public SortedSet<TimeMarker> getAllVisibleTimemarkers()
    {
        SortedSet<TimeMarker> visibleTM = new TreeSet<TimeMarker>();
        SortedSet<TimeMarker> copy = new TreeSet<TimeMarker>( timeMarkers );
        Iterator<TimeMarker> iterator = copy.iterator();
        while (iterator.hasNext())
        {
            TimeMarker timeMarker = iterator.next();
            if (timeMarker.isEnabled())
            {
                visibleTM.add( timeMarker );
            }
        }
        return visibleTM;
    }

    private void notifyListeners(Notifier notifier)
    {
        Set<TimeMarkersChangedListener> listenersCopy = new HashSet<TimeMarkersChangedListener>( listeners );
        for (TimeMarkersChangedListener listener : listenersCopy)
        {
            notifier.notifyListener( listener );
        }
    }

    private interface Notifier
    {
        public void notifyListener(TimeMarkersChangedListener listener);
    }

    @Override
    public void registerListener(TimeMarkersChangedListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void unregisterListener(TimeMarkersChangedListener listener)
    {
        listeners.remove( listener );
    }

    @Override
    public void onReset()
    {
        timeMarkerCount = 0;
        removeAllTimeMarkers();
        setCurrentSelectedTimeMarker( null );
    }

    @Override
    public void notifyTimeMarkerNameChanged(final TimeMarker timeMarker)
    {
        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.timeMarkerNameChanged( timeMarker );
            }
        } );
    }

    @Override
    public void notifyTimeMarkerTimestampChanged(final TimeMarker timeMarker)
    {
        notifyListeners( new Notifier()
        {
            @Override
            public void notifyListener(TimeMarkersChangedListener listener)
            {
                listener.timeMarkerTimestampChanged( timeMarker );
            }
        } );
    }
}
