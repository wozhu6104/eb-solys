/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class CreateNewEventMapFromSelectedChannelsHandler extends AbstractHandler
        implements
            CreateResourceInteractionCallback
{
    private final CreateResourceInteractionUseCase createResourceUseCase;

    public CreateNewEventMapFromSelectedChannelsHandler()
    {
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        IStructuredSelection selection = ((IStructuredSelection)HandlerUtil.getCurrentSelection( event ));

        channels = ChannelsViewHandlerUtil.filterChannelsInNodes( selection.toList() );
        createResourceUseCase.createOrGetAndOpenSnapshot( channels );
        return null;
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
