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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateConnectionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.createresource.DefaultResourceNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.viewer.resources.handler.BaseResourcesModelHandler;

public class EditHandler extends BaseResourcesModelHandler implements CreateConnectionInteractionCallback
{
    private static final String CANNOT_EDIT_CONNECTION_MESSAGE = "Connection cannot be edited while active.";
    private static final String CANNOT_EDIT_CONNECTION_TITLE = "Cannot Edit Connection";

    private final DefaultResourceNameNotifyUseCase defaultResourceNameUseCase;
    private final CreateConnectionInteractionUseCase createConnectionCase;

    public EditHandler()
    {
        defaultResourceNameUseCase = UseCaseFactoryInstance.get().makeDefaultResourceNameNotifyUseCase();
        createConnectionCase = UseCaseFactoryInstance.get().makeCreateConnectionInteractionUseCase( this );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object selectedObject = getSelection( event );
        if (selectedObject instanceof ConnectionModel)
        {
            editConnection( (ConnectionModel)selectedObject );
        }
        return null;
    }

    public void editConnection(ConnectionModel model)
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        if (model.isConnected())
        {
            MessageDialog.openInformation( shell, CANNOT_EDIT_CONNECTION_TITLE, CANNOT_EDIT_CONNECTION_MESSAGE );
            return;
        }

        Set<String> usedNames = defaultResourceNameUseCase.getUsedConnectionNames();
        String name = model.getName();
        usedNames.remove( name );

        List<ConnectionType> allConnectionTypes = createConnectionCase.getAllConnectionTypes();
        EditConnectionDialog dialog = new EditConnectionDialog( name,
                                                                model.getHost(),
                                                                model.getPort(),
                                                                model.isSaveToFile(),
                                                                shell,
                                                                usedNames,
                                                                allConnectionTypes,
                                                                model.getConnectionType() );
        dialog.setBlockOnOpen( true );
        int returnCode = dialog.open();
        if (returnCode == IDialogConstants.OK_ID)
        {
            model.setName( dialog.getName() );
            model.setHost( dialog.getHost() );
            model.setPort( dialog.getPort() );
            model.setConnectionType( dialog.getConnectionType() );
            model.setSaveToFile( dialog.isSaveToFile() );
        }
    }

    @Override
    public void dispose()
    {
        createConnectionCase.unregister();
        super.dispose();
    }

    @Override
    public void onPortInvalid(int port)
    {
    }
}
