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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChartEditor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ToggleChannelVisibillityHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        List<RuntimeEventChannel<?>> selectedChannelsList = new ArrayList<RuntimeEventChannel<?>>();

        ResourceModel model = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart( event );

        if (activePart instanceof ChartEditor)
        {
            ChartEditor chartEditor = (ChartEditor)activePart;
            model = chartEditor.getModel();
            List<?> list = ChartHandlerUtil.getTreeSelection( chartEditor );
            selectedChannelsList = ChartHandlerUtil.createSelectedChannelsList( list );
        }

        toggleSelectedChannelsVisibillity( model, selectedChannelsList );
        return null;
    }

    private void toggleSelectedChannelsVisibillity(ResourceModel model,
            List<RuntimeEventChannel<?>> selectedChannelsList)
    {
        List<RuntimeEventChannel<?>> disabledChannels = model.getDisabledChannels();

        if (disabledChannels.containsAll( selectedChannelsList ))
        {
            disabledChannels.removeAll( selectedChannelsList );
        }
        else
        {
            disabledChannels.addAll( selectedChannelsList );
        }

        model.setDisabledChannels( disabledChannels );
    }
}
