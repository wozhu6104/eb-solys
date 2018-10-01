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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChartEditor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChartLegendChannelSelectionHandler implements ISelectionChangedListener
{
    private final ChartEditor chartEditor;
    private final ResourceModel model;

    public ChartLegendChannelSelectionHandler(ChartEditor chartEditor, ResourceModel model)
    {
        this.chartEditor = chartEditor;
        this.model = model;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event)
    {
        List<?> list = ChartHandlerUtil.getTreeSelection( chartEditor );
        List<RuntimeEventChannel<?>> selectedChannels = ChartHandlerUtil.createSelectedChannelsList( list );
        model.setSelectedChannels( selectedChannels );
    }
}
