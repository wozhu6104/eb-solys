/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputUseCase;

public class LoadTargetConfigHandler extends AbstractHandler
{

    DataInputUseCase uc = UseCaseFactoryInstance.get().makeDataInputUseCase();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dialog = new FileDialog( parentShell );
        dialog.setFilterExtensions( new String[]{"*.json"} );
        try
        {
            String configPath = dialog.open();
            uc.loadDataInputsFromFile( configPath );
        }
        catch (IOException e)
        {
            MessageBox box = new MessageBox( parentShell );
            box.setMessage( e.getMessage() );
            box.open();
        }
        return null;
    }

}
