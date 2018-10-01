/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.datamanager;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class RuntimeEventMock<T> implements RuntimeEvent<T>
{
    private final T value;
    private long timestamp = 0;

    public RuntimeEventMock(T value)
    {
        this.value = value;
    }

    public RuntimeEventMock(T value, long timestamp)
    {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public ModelElement getModelElement()
    {
        if (value instanceof ComRelation)
        {
            return (ModelElement)value;
        }
        return ModelElement.NULL_MODEL_ELEMENT;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public String getSummary()
    {
        return value.toString();
    }

    @Override
    public RuntimeEventChannel<T> getRuntimeEventChannel()
    {
        return new RuntimeEventChannelMock<T>( value.toString() );
    }

    @Override
    public RuntimeEventTag getTag()
    {
        return null;
    }

    @Override
    public String getTagDescription()
    {
        return "";
    }

    @Override
    public int compareTo(TimebasedObject o)
    {
        if (o.getTimestamp() == this.getTimestamp())
        {
            return 0;
        }
        else if (this.getTimestamp() < o.getTimestamp())
        {
            return -1;
        }

        return 1;
    }

    @Override
    public boolean isTagged()
    {
        return false;
    }
}
