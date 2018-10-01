/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.file;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.ui.ecl.splitfile.view.SplitFileDialog;

public class OpenRecentRaceFileHandler extends AbstractHandler implements OpenFileInteractionCallback
{
    private static final String ERROR_DIALOG_MESSAGE = "File not found. It may have been deleted, renamed or moved";

    private final OpenFileInteractionUseCase openFileInteractionUseCase;

    public OpenRecentRaceFileHandler()
    {
        openFileInteractionUseCase = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        String pathToPrevioulyLoadedFile = event.getParameter( RecentFilesDynamicMenu.getCommandParamterId() );
        if (pathToPrevioulyLoadedFile != null)
        {
            File file = new File( pathToPrevioulyLoadedFile );
            if (file.exists())
            {
                openFileInteractionUseCase.openFile( pathToPrevioulyLoadedFile );
            }
            else
            {
                MessageDialog.open( SWT.ERROR, null, null, ERROR_DIALOG_MESSAGE, SWT.NONE );
            }
        }
        return null;
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
        SplitFileDialog dialog = new SplitFileDialog( new Shell(), pathToFile );
        dialog.create();
        dialog.open();
    }

    @Override
    public void dispose()
    {
        openFileInteractionUseCase.unregister();
        super.dispose();
    }

    @Override
    public void onFileLoadedSucessfully()
    {
    }

    @Override
    public void onFileLoadingFailed()
    {
    }

    @Override
    public void onFileAlreadyLoaded(String pathToFile)
    {
    }

    @Override
    public void onFileEmpty(String pathToFile)
    {
    }

    @Override
    public void onFileNotFound(String pathToFile)
    {
    }
}
