/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.createResource;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class CreateConnectionInteractionUseCaseImpl implements CreateConnectionInteractionUseCase
{
    private final ResourcesModelManager resourcesModelManager;
    private CreateConnectionInteractionCallback callback;
    private final ConnectionService connectionService;

    public CreateConnectionInteractionUseCaseImpl(ResourcesModelManager resourcesModelManager,
            ConnectionService connectionService, CreateConnectionInteractionCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        RangeCheckUtils.assertReferenceParameterNotNull( "connectionService", connectionService );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        this.resourcesModelManager = resourcesModelManager;
        this.connectionService = connectionService;
        this.callback = callback;
    }

    @Override
    public ConnectionModel createConnection(String name, String host, int port, boolean saveToFile,
            ConnectionType connectionType)
    {
        if (isPortValid( port ))
        {
            return resourcesModelManager.createConnection( name, host, port, saveToFile, connectionType );
        }
        else
        {
            callback.onPortInvalid( port );
            return null;
        }
    }

    private boolean isPortValid(int port)
    {
        if (port < 0 || port > 65535)
        {
            return false;
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<ConnectionType> getAllConnectionTypes()
    {
        return new ArrayList( connectionService.getAllConnectionTypes() );// TODO use Set instead of List in callback
    }

    @Override
    public void unregister()
    {
        callback = null;
    }

    @Override
    public DataInputResourceModel createDataInput(String name)
    {
        DataInputResourceModel returnModel = resourcesModelManager.getDataInputs().stream()
                .map( model -> (DataInputResourceModel)model ).filter( model -> model.getName().equals( name ) )
                .findFirst().orElse( null );
        if (returnModel == null)
        {
            returnModel = resourcesModelManager.createDataInput( name );
        }
        return returnModel;
    }
}
