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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;

import com.elektrobit.ebrace.viewer.chartengine.internal.ChartEditor;
import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebrace.viewer.snapshot.editor.ChannelsSnapshotDecoderComposite;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChartHandlerUtil
{

    public static List<?> getTreeSelection(ChartEditor chartEditor)
    {
        ChannelsSnapshotDecoderComposite chartLegendComposite = chartEditor.getChartLegend();
        ISelection selection = chartLegendComposite.getTreeViewerOfSnapshot().getSelection();
        TreeSelection treeSelection = (TreeSelection)selection;
        List<?> list = treeSelection.toList();
        return list;
    }

    public static List<RuntimeEventChannel<?>> createSelectedChannelsList(List<?> list)
    {
        List<RuntimeEventChannel<?>> selectedChannelsList = new ArrayList<RuntimeEventChannel<?>>();
        for (Object object : list)
        {
            if (object instanceof ChannelValueProvider)
            {
                ChannelValueProvider channelValueProvider = (ChannelValueProvider)object;
                RuntimeEventChannel<?> runtimeEventChannel = channelValueProvider.getRuntimeEventChannel();
                selectedChannelsList.add( runtimeEventChannel );
            }
        }
        return selectedChannelsList;
    }

}
