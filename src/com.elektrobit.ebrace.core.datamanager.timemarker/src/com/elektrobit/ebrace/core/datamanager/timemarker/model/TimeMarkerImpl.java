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

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerImpl implements TimeMarker
{
    private long timestamp;
    private String name;
    private boolean enabled = true;
    private final TimeMarkerChangedNotifier notifier;

    public TimeMarkerImpl(long timestamp, String name, TimeMarkerChangedNotifier notifier)
    {
        this.timestamp = timestamp;
        this.name = name;
        this.notifier = notifier;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
        notifier.notifyTimeMarkerTimestampChanged( this );
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
        notifier.notifyTimeMarkerNameChanged( this );
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public SColor getColor()
    {
        return new SColor( 0, 235, 0 );
    }

    @Override
    public int compareTo(TimebasedObject timestamp)
    {
        if (timestamp.getTimestamp() == this.getTimestamp())
        {
            return 0;
        }
        if (timestamp.getTimestamp() < this.getTimestamp())
        {
            return 1;
        }
        return -1;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int)(timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        TimeMarkerImpl other = (TimeMarkerImpl)obj;
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals( other.name ))
        {
            return false;
        }
        if (timestamp != other.timestamp)
        {
            return false;
        }
        return true;
    }
}
