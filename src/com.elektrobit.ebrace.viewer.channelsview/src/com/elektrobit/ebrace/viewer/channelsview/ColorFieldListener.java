/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ColorFieldListener implements Listener
{

    private final ChannelColorUseCase channelColorUseCase;
    public Map<RuntimeEventChannel<?>, Rectangle> channelColors;

    public ColorFieldListener(ChannelColorUseCase channelColorUseCase,
            Map<RuntimeEventChannel<?>, Rectangle> channelColors)
    {
        this.channelColorUseCase = channelColorUseCase;
        this.channelColors = channelColors;
    }

    @Override
    public void handleEvent(Event event)
    {
        TreeItem item = (TreeItem)event.item;
        if (item.getData() instanceof ChannelTreeNode)
        {
            ChannelTreeNode node = (ChannelTreeNode)item.getData();
            if (node.isLeaf())
            {
                handleEventForNode( event, node );
            }
        }
    }

    private void handleEventForNode(Event event, ChannelTreeNode node)
    {
        RuntimeEventChannel<?> channel = node.getRuntimeEventChannel();
        if (channelColorUseCase.channelHasColor( channel ))
        {
            channelColors.put( channel, new Rectangle( event.x, event.y, event.width, event.height ) );
        }
    }
}
