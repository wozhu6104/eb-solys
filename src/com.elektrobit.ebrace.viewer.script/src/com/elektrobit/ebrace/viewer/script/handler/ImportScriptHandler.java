/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionUseCase;
import com.elektrobit.ebrace.viewer.common.UserMessageDialog;

public class ImportScriptHandler extends AbstractHandler implements ImportScriptInteractionCallback
{
    private static final String SCRIPT_EXISTS_IMPORT_TITLE = "Script already exists";
    private static final String SCRIPT_EXISTS_IMPORT_MESSAGE = "Cannot import one or more scripts because a script with the same name already exists.";
    private static final String SCRIPT_IMPORT_FAILED_TITLE = "Script import failed";
    private static final String SCRIPT_IMPORT_FAILED_MESSAGE = "Import of script(s) failed for unkown reason.";

    private final ImportScriptInteractionUseCase importScriptInteractionUseCase;

    public ImportScriptHandler()
    {
        importScriptInteractionUseCase = UseCaseFactoryInstance.get().makeImportScriptInteractionUseCase( this );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {

        if (ProVersion.getInstance().isActive())
        {
            List<String> paths = openFileDialog();

            if (paths.isEmpty())
            {
                return null;
            }

            for (String path : paths)
            {
                File source = new File( path );
                importScriptInteractionUseCase.importUserScript( source );
            }
        }
        else
        {
            UserMessageDialog.UserProMessageDialog();

        }
        return null;
    }

    private List<String> openFileDialog()
    {
        FileDialog dialog = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                            SWT.MULTI );
        dialog.setFilterExtensions( new String[]{"*" + ScriptConstants.SCRIPT_EXTENSION} );
        dialog.open();

        String[] fileNames = dialog.getFileNames();
        String dialogPath = dialog.getFilterPath();
        List<String> filePaths = new ArrayList<String>();

        for (String fileName : fileNames)
        {
            filePaths.add( dialogPath + File.separator + fileName );
        }

        return filePaths;
    }

    @Override
    public void onScriptImportSuccessful()
    {
    }

    @Override
    public void onScriptAlreadyExists()
    {
        MessageDialog.open( MessageDialog.WARNING,
                            new Shell(),
                            SCRIPT_EXISTS_IMPORT_TITLE,
                            SCRIPT_EXISTS_IMPORT_MESSAGE,
                            SWT.NONE );
    }

    @Override
    public void onScriptImportFailed()
    {
        MessageDialog.open( MessageDialog.ERROR,
                            new Shell(),
                            SCRIPT_IMPORT_FAILED_TITLE,
                            SCRIPT_IMPORT_FAILED_MESSAGE,
                            SWT.NONE );
    }

    @Override
    public void dispose()
    {
        importScriptInteractionUseCase.unregister();
    }

}
