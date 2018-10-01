/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

/** Drop target adapter for the implementation fo the drag and drop functionality for runtime event channels. */
public class RuntimeeventChannelDropTargetAdapter extends DropTargetAdapter
{
    Class<?> assignableDataType;
    final RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
            .getService();
    private List<RuntimeEventChannel<?>> draggedChannels;
    private final ResourceModel model;

    public RuntimeeventChannelDropTargetAdapter(ResourceModel model, Class<?> assignableDataType)
    {
        this.model = model;
        this.assignableDataType = assignableDataType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dragEnter(DropTargetEvent event)
    {
        TransferData tData = event.currentDataType;
        Object transferredData = RuntimeEventChannelTransfer.getInstance().nativeToJava( tData );
        draggedChannels = new ArrayList<RuntimeEventChannel<?>>();
        if (transferredData instanceof List<?>)
        {
            draggedChannels = (List<RuntimeEventChannel<?>>)transferredData;
            if (draggedChannels == null || draggedChannels.isEmpty())
            {
                event.detail = DND.DROP_NONE;
            }
            for (RuntimeEventChannel<?> channel : draggedChannels)
            {
                if (!this.assignableDataType.isAssignableFrom( channel.getUnit().getDataType() ))
                {
                    event.detail = DND.DROP_NONE;
                }
            }
        }
    }

    @Override
    public void drop(DropTargetEvent event)
    {
        if (event.getSource() instanceof DropTarget)
        {
            List<RuntimeEventChannel<?>> newChannels = new ArrayList<RuntimeEventChannel<?>>();
            newChannels.addAll( model.getChannels() );
            newChannels.addAll( draggedChannels );
            model.setChannels( newChannels );
        }
    }
}
