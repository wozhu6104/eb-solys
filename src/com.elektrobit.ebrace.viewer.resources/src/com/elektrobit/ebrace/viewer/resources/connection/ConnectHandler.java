/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.connection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.viewer.resources.handler.BaseResourcesModelHandler;

public class ConnectHandler extends BaseResourcesModelHandler implements ConnectionToTargetInteractionCallback
{
    private final ConnectionToTargetInteractionUseCase connectUseCase;

    public ConnectHandler()
    {
        connectUseCase = UseCaseFactoryInstance.get().makeConnectionToTargetInteractionUseCase( this );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object selectedObject = getSelection( event );
        ConnectionModel connection = (ConnectionModel)selectedObject;
        connectUseCase.connect( connection );
        return null;
    }

    @Override
    public void onConnected()
    {
    }

    @Override
    public void onTargetNotReachable()
    {
        MessageDialog.open( SWT.ERROR,
                            null,
                            LABEL_CONNECTION_NOT_POSSIBLE_TITLE,
                            LABEL_HOST_NOT_REACHABLE_TEXT,
                            SWT.NONE );
    }

    @Override
    public void onDisconnected()
    {
    }

    @Override
    public void dispose()
    {
        connectUseCase.unregister();
    }

    public void connect(ConnectionModel model)
    {
        connectUseCase.connect( model );
    }

    public void disconnect(ConnectionModel model)
    {
        connectUseCase.disconnect( model );
    }

}
