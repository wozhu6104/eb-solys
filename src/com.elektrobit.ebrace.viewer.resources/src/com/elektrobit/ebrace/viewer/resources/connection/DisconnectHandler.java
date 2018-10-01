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

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.viewer.resources.handler.BaseResourcesModelHandler;

public class DisconnectHandler extends BaseResourcesModelHandler implements ConnectionToTargetInteractionCallback
{
    private final ConnectionToTargetInteractionUseCase connectUseCase;

    public DisconnectHandler()
    {
        connectUseCase = UseCaseFactoryInstance.get().makeConnectionToTargetInteractionUseCase( this );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object selectedObject = getSelection( event );
        ConnectionModel connection = (ConnectionModel)selectedObject;
        connectUseCase.disconnect( connection );
        return null;
    }

    @Override
    public void onConnected()
    {
    }

    @Override
    public void onTargetNotReachable()
    {
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
}
