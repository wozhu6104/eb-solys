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

import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.DefaultResourceNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

public class NewConnectionHandler extends AbstractHandler implements CreateConnectionInteractionCallback
{
    private final CreateConnectionInteractionUseCase createConnectionCase;
    private final DefaultResourceNameNotifyUseCase defaultResourceNameUseCase;

    public NewConnectionHandler()
    {
        createConnectionCase = UseCaseFactoryInstance.get().makeCreateConnectionInteractionUseCase( this );
        defaultResourceNameUseCase = UseCaseFactoryInstance.get().makeDefaultResourceNameNotifyUseCase();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String nextPossibleConnectionName = defaultResourceNameUseCase.getNextPossibleConnectionName();
        Set<String> usedNames = defaultResourceNameUseCase.getUsedConnectionNames();

        List<ConnectionType> allConnectionTypes = createConnectionCase.getAllConnectionTypes();
        EditConnectionDialog editConnectionDialog = new EditConnectionDialog( nextPossibleConnectionName,
                                                                              null,
                                                                              null,
                                                                              null,
                                                                              parentShell,
                                                                              usedNames,
                                                                              allConnectionTypes,
                                                                              null );
        editConnectionDialog.setBlockOnOpen( true );
        int returnCode = editConnectionDialog.open();
        if (returnCode == IDialogConstants.OK_ID)
        {
            createNewConnection( editConnectionDialog.getName(),
                                 editConnectionDialog.getHost(),
                                 editConnectionDialog.getPort(),
                                 editConnectionDialog.isSaveToFile(),
                                 editConnectionDialog.getConnectionType() );
        }
        return null;
    }

    private void createNewConnection(String name, String host, int port, boolean saveToFile,
            ConnectionType connectionType)
    {
        createConnectionCase.createConnection( name, host, port, saveToFile, connectionType );
    }

    @Override
    public void dispose()
    {
        createConnectionCase.unregister();
    }

    @Override
    public void onPortInvalid(int port)
    {
    }
}
