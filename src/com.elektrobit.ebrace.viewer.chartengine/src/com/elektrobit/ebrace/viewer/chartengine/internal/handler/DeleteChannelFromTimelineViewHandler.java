/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.timeline.SolysTimeGraphEntry;
import com.elektrobit.ebrace.viewer.chartengine.internal.timeline.TimelineViewEditor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DeleteChannelFromTimelineViewHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchPart activePart = HandlerUtil.getActivePart( event );

        if (activePart instanceof TimelineViewEditor)
        {
            TimelineViewEditor timelineViewEditor = (TimelineViewEditor)activePart;
            ResourceModel model = timelineViewEditor.getModel();
            SolysTimeGraphEntry selectedEntry = timelineViewEditor.getSelection();
            removeChannel( model, selectedEntry.getChannel() );
        }
        return null;
    }

    private void removeChannel(ResourceModel model, RuntimeEventChannel<?> channelToBeDeleted)
    {
        List<RuntimeEventChannel<?>> channelsOfModel = model.getChannels();
        channelsOfModel.remove( channelToBeDeleted );
        model.setChannels( channelsOfModel );
    }
}
