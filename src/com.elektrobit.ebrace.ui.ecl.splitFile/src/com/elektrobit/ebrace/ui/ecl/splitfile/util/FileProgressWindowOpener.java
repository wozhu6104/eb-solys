/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.splitfile.util;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;

public class FileProgressWindowOpener implements OpenFileInteractionCallback
{
    public FileProgressWindowOpener()
    {
        UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        LoadingFileProgressModalWindow dialog = new LoadingFileProgressModalWindow( shell, pathToFile );
        dialog.runMethod();
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
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
