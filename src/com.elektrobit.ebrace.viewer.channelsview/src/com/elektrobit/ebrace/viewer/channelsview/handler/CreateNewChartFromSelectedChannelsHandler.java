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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class CreateNewChartFromSelectedChannelsHandler extends AbstractHandler
        implements
            CreateResourceInteractionCallback
{
    private final CreateResourceInteractionUseCase createResourceUseCase;

    public CreateNewChartFromSelectedChannelsHandler()
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
        createResourceUseCase.createOrGetAndOpenChart( channels );
        return null;
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
        MessageBox box = new MessageBox( new Shell(), SWT.ICON_ERROR | SWT.OK );
        box.setMessage( "Could not create chart. Chart can only display channels of numerical type." );
        box.setText( "Wrong channel type" );
        box.open();
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
