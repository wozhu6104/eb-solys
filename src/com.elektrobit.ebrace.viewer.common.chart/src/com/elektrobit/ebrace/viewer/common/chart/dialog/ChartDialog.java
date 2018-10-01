/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.chart.dialog;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.viewer.resources.dialog.EditResourceModelChannelDialog;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChartDialog extends EditResourceModelChannelDialog
{
    private final ChartTypes chartType;

    public ChartDialog(Shell parentShell, ChartModel chartModel)
    {
        super( parentShell, chartModel );
        this.chartType = chartModel.getType();
    }

    @Override
    protected String getResourceModelSimpleName()
    {
        return chartType.getName();
    }

    /**
     * Returns just channels that can be shown in the chart
     * 
     * @return
     */
    @Override
    protected List<RuntimeEventChannel<?>> getAllValidChannelsForResourceModel()
    {
        RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
        return runtimeEventAcceptor.getRuntimeEventChannelsForType( chartType.getAssignableType() );
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
