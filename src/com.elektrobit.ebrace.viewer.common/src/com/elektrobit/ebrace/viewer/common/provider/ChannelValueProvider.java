/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelValueProvider
{
    private final RuntimeEventChannel<?> channel;
    private Object value;
    private List<DecodedNode> decodedNodes = new ArrayList<DecodedNode>();

    public ChannelValueProvider(RuntimeEventChannel<?> channel, Object value)
    {
        this.channel = channel;
        this.value = value;
    }

    public ChannelValueProvider(RuntimeEventChannel<?> channel, List<DecodedNode> nodes)
    {
        this.channel = channel;
        this.decodedNodes = nodes;
    }

    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return channel;
    }

    public Object getValue()
    {
        return value;
    }

    public List<DecodedNode> getNodes()
    {
        return decodedNodes;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
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
        ChannelValueProvider other = (ChannelValueProvider)obj;
        if (channel == null)
        {
            if (other.channel != null)
            {
                return false;
            }
        }
        else if (!channel.equals( other.channel ))
        {
            return false;
        }
        return true;
    }
}
