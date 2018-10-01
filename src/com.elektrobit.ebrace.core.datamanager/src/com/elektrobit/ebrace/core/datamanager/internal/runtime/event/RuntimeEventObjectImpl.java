/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ComplexEventValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public final class RuntimeEventObjectImpl<T> implements RuntimeEvent<T>
{
    private final RuntimeEventChannel<T> runtimeEventChannel;
    private final T value;
    private final long modelElementId;
    private final long timeStamp;
    private final ModelElementPool modelElementPool;
    private RuntimeEventTag tag;
    private String tagDescription;
    private final String summary;

    public RuntimeEventObjectImpl(final long timeStamp, final RuntimeEventChannel<T> runtimeEventChannel,
            final long modelElementId, final T value, String summary, final ModelElementPool modelElementPool)
    {
        this.runtimeEventChannel = runtimeEventChannel;
        this.value = value;
        this.modelElementId = modelElementId;
        this.timeStamp = timeStamp;
        this.modelElementPool = modelElementPool;
        this.tag = null;
        this.tagDescription = "";
        this.summary = summary;
    }

    @Override
    public RuntimeEventChannel<T> getRuntimeEventChannel()
    {
        return runtimeEventChannel;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public String getSummary()
    {
        if (summary != null && summary != "")
        {
            return summary;
        }
        else if (value instanceof ProtoMessageValue)
        {
            return ((ProtoMessageValue)value).getSummary();
        }
        else if (value instanceof ComplexEventValue)
        {
            return ((ComplexEventValue)value).getSummary();
        }
        else
        {
            return value.toString();
        }
    }

    @Override
    public long getTimestamp()
    {
        return timeStamp;
    }

    @Override
    public ModelElement getModelElement()
    {
        return this.modelElementPool.getModelElementWithID( modelElementId );
    }

    @Override
    public RuntimeEventTag getTag()
    {
        return tag;
    }

    public void setTag(RuntimeEventTag tag)
    {
        setTagWithDescription( tag, "" );
    }

    @Override
    public boolean isTagged()
    {
        return getTag() != null;
    }

    public void clearTag()
    {
        this.tag = null;
        this.tagDescription = "";
    }

    @Override
    public String getTagDescription()
    {
        return tagDescription;
    }

    public void setTagWithDescription(RuntimeEventTag tag, String tagDescription)
    {
        this.tag = tag;
        this.tagDescription = tagDescription;
    }

    @Override
    public String toString()
    {
        return "RuntimeEventObjectImpl [runtimeEventChannelName=" + runtimeEventChannel.getName()
                + ", runtimeEventChannel=" + runtimeEventChannel + ", value=" + value + ", modelElementId="
                + modelElementId + ", timeStamp=" + timeStamp + ", modelElementPool=" + modelElementPool + "]";
    }

    @Override
    public int compareTo(TimebasedObject o)
    {
        if (o.getTimestamp() == this.getTimestamp())
        {
            return 0;
        }
        if (o.getTimestamp() < this.getTimestamp())
        {
            return 1;
        }
        return -1;
    }
}
