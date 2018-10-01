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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.viewer.common.view.IResourcesModelView;

public class CreateResourcesModelBaseHandler extends AbstractHandler implements CreateResourceInteractionCallback
{
    protected CreateResourceInteractionUseCase createResourceUseCase;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        return null;
    }

    public CreateResourcesModelBaseHandler()
    {
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
    }

    protected Object getSelection(ExecutionEvent event)
    {
        Object o = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart( event );
        if (activePart instanceof IResourcesModelView)
        {
            o = ((IResourcesModelView)activePart).getModel();
        }
        else
        {
            StructuredSelection selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );
            if (selection != null && !selection.isEmpty())
            {
                o = selection.getFirstElement();
            }
        }
        return o;
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
        MessageDialog.open( SWT.ERROR,
                            new Shell(),
                            "Cannot create resource",
                            "Cannot create the requested resource. A resource with the same name and type already exists.",
                            SWT.NONE );
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
