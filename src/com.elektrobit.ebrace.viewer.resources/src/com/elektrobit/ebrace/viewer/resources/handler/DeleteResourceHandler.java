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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class DeleteResourceHandler extends AbstractHandler
{
    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        StructuredSelection selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );
        List<?> selectedobjects = selection.toList();
        List<ResourceModel> toDelete = new ArrayList<ResourceModel>();
        for (Object o : selectedobjects)
        {
            if (o instanceof ResourceModel)
            {
                toDelete.add( (ResourceModel)o );
            }
        }
        if (!toDelete.isEmpty())
        {
            resourceManagerTracker.getService().deleteResourcesModels( toDelete );
        }
        return null;
    }
}
