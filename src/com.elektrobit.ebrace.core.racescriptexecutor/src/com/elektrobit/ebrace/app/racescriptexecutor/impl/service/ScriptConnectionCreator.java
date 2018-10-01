/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SConnectionImpl;
import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.SConnection;

public class ScriptConnectionCreator implements CreateConnectionInteractionCallback
{

    private final Console scriptConsole;

    public ScriptConnectionCreator(Console scriptConsole)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptConsole", scriptConsole );
        this.scriptConsole = scriptConsole;
    }

    public SConnection createConnection(String name, String address, int port, boolean saveDataToFile)
    {
        CreateConnectionInteractionUseCase createConnectionUseCase = UseCaseFactoryInstance.get()
                .makeCreateConnectionInteractionUseCase( this );

        // TODO provide connection type selection in Script API
        List<ConnectionType> allConnectionTypes = createConnectionUseCase.getAllConnectionTypes();
        ConnectionType firstConnectionType = allConnectionTypes.get( 0 );
        ConnectionType secondConnectionType = allConnectionTypes.get( 1 );
        ConnectionType connectionType = firstConnectionType.getExtension().equals( "bin" )
                ? firstConnectionType
                : secondConnectionType;
        // TODO hack ends here :)

        ConnectionModel connection = createConnectionUseCase
                .createConnection( name, address, port, saveDataToFile, connectionType );
        SConnection sConnection = new SConnectionImpl( connection );
        return sConnection;
    }

    @Override
    public void onPortInvalid(int port)
    {
        scriptConsole.println( "ERROR: Invalid port " + port );
    }

}
