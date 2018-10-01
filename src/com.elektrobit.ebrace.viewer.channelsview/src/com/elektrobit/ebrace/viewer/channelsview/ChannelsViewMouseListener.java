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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.viewer.channelsview.handler.ChannelsViewHandlerUtil;
import com.elektrobit.ebrace.viewer.common.swt.ColorDialogHandler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelsViewMouseListener implements MouseListener
{

    private final CreateResourceInteractionUseCase createResourceUseCase;
    private final ChannelColorUseCase channelColorUseCase;
    private final Map<RuntimeEventChannel<?>, Rectangle> channelColorFields;
    private final ResourceManager resourceManager;
    private final ChannelsView parent;

    public ChannelsViewMouseListener(CreateResourceInteractionUseCase createResourceUseCase,
            ChannelColorUseCase channelColorUseCase, Map<RuntimeEventChannel<?>, Rectangle> channelColors,
            ResourceManager resourceManager, ChannelsView parent)
    {
        this.createResourceUseCase = createResourceUseCase;
        this.channelColorUseCase = channelColorUseCase;
        this.channelColorFields = channelColors;
        this.resourceManager = resourceManager;
        this.parent = parent;
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
        List<RuntimeEventChannel<?>> channels = ChannelsViewHandlerUtil
                .getChannelListFromSelection( parent.getSelection() );
        channels = ChannelsViewHandlerUtil.filterChannels( channels );
        createResourceUseCase.createOrGetAndOpenResourceAccordingToType( channels );
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        if (e.button == 1)
        {
            RuntimeEventChannel<?> selectedChannel = hitColorField( new Point( e.x, e.y ) );
            if (selectedChannel != null)
            {
                ColorDialogHandler colorDialogHandler = new ColorDialogHandler( resourceManager );
                Color newColor = colorDialogHandler.getSelectedColor();

                if (newColor != null)
                {
                    channelColorUseCase.setColorForChannel( selectedChannel,
                                                            newColor.getRed(),
                                                            newColor.getGreen(),
                                                            newColor.getBlue() );
                }
            }
        }
    }

    private RuntimeEventChannel<?> hitColorField(Point eventPoint)
    {
        Stream<RuntimeEventChannel<?>> channels = channelColorFields.keySet().stream();
        return channels.filter( channel -> channelColorFields.get( channel ).contains( eventPoint ) ).findFirst()
                .orElse( null );
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
    }

}
