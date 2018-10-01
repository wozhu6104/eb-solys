/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;

public class CreateTableFromChartHandler extends CreateResourcesModelBaseHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object o = getSelection( event );
        if (o instanceof ChartModel)
        {
            createResourceUseCase.createTableFromResource( (ResourceModel)o );
        }
        return null;
    }
}
