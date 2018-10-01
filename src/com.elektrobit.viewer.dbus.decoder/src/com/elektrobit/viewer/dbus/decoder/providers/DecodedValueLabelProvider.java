/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.viewer.dbus.decoder.providers;

import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;

public class DecodedValueLabelProvider extends DecodedNodeLabelProvider
{
    @Override
    public String getText(Object element)
    {
        if (element instanceof DecodedNode)
        {
            return ((DecodedNode)element).getValue();
        }
        else
        {
            if (element instanceof ChannelValueProvider)
            {
                ChannelValueProvider channelValueProvider = (ChannelValueProvider)element;
                if (!channelValueProvider.getNodes().isEmpty())
                    return channelValueProvider.getNodes().get( 0 ).getValue();
                if (channelValueProvider.getValue() != null)
                {
                    return ((DecodedRuntimeEvent)channelValueProvider.getValue()).getRuntimeEventValue().toString();
                }
                else
                    return "N/A";
            }
        }
        return null;
    }
}
