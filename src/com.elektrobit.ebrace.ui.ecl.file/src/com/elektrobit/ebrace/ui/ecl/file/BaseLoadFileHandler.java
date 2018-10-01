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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.ui.ecl.splitfile.view.SplitFileDialog;

public abstract class BaseLoadFileHandler extends AbstractHandler implements OpenFileInteractionCallback
{
    private final OpenFileInteractionUseCase loadFileInteractionUseCase;

    public BaseLoadFileHandler()
    {
        loadFileInteractionUseCase = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
        init( loadFileInteractionUseCase );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        FileDialog fileDialog = new FileDialog( new Shell(), SWT.OPEN );
        fileDialog.setFilterExtensions( getFileExtentions() );
        fileDialog.setFilterNames( getFileExtentionNames() );
        fileDialog.setText( getFileDialogTitle() );
        String selectedFilePath = fileDialog.open();

        if (selectedFilePath != null)
        {
            loadFileInteractionUseCase.openFile( selectedFilePath );
        }
        return null;
    }

    abstract protected void init(OpenFileInteractionUseCase useCase);

    abstract protected String[] getFileExtentions();

    abstract protected String[] getFileExtentionNames();

    abstract protected String getFileDialogTitle();

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
        loadFileInteractionUseCase.unregister();
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
