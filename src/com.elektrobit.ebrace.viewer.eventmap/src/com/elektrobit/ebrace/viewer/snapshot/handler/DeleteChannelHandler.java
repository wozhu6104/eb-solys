/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DeleteChannelHandler extends AbstractHandler
{
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        removeChannels( currentSelection.toList() );
        return null;
    }

    private void removeChannels(List<ChannelValueProvider> channelListToBeDeleted)
    {

        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        List<RuntimeEventChannel<?>> channelsOfModel = ((ResourcesModelEditorInput)activeEditor.getEditorInput())
                .getModel().getChannels();
        for (ChannelValueProvider channelToBeDeleted : channelListToBeDeleted)
        {
            String channelNameToBeDeleted;
            if (channelToBeDeleted.getRuntimeEventChannel() != null)
            {
                channelNameToBeDeleted = channelToBeDeleted.getRuntimeEventChannel().getName();
            }
            else
            {
                channelNameToBeDeleted = channelToBeDeleted.getNodes().get( 0 ).getParentTree().getRootNode().getName();
            }
            for (RuntimeEventChannel<?> channel : channelsOfModel)
            {
                if (channel.getName().equals( channelNameToBeDeleted ))
                {
                    channelsOfModel.remove( channel );
                    break;
                }
            }
        }
        ((ResourcesModelEditorInput)activeEditor.getEditorInput()).getModel().setChannels( channelsOfModel );
    }
}
