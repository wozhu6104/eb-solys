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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;

public class LoadingFileProgressModalWindow extends ProgressMonitorDialog implements OpenFileInteractionCallback
{
    private Shell shell = null;
    private final String path;

    public LoadingFileProgressModalWindow(Shell parent, String path)
    {
        super( parent );
        this.shell = parent;
        this.path = path;
    }

    @Override
    protected void cancelPressed()
    {
        super.cancelPressed();
        OpenFileInteractionUseCase loadFileInteractionUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileInteractionUseCase( this );
        loadFileInteractionUseCase.cancelLoadingFile();
        loadFileInteractionUseCase.unregister();
    }

    public void runMethod()
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    LoadingFileProgressModalWindow.this.run( true,
                                                             true,
                                                             new LoadingFileProgressRunningOperation( path ) );
                }
                catch (InvocationTargetException e)
                {
                    MessageDialog.openError( shell, "Error", e.getMessage() );
                }
                catch (InterruptedException e)
                {
                    MessageDialog.openInformation( shell, "Canceled", e.getMessage() );
                }
            }
        } );
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
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
