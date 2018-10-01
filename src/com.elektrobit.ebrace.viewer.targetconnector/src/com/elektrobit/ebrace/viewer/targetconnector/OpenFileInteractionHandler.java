/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.targetconnector;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.ui.ecl.splitfile.view.SplitFileDialog;

public class OpenFileInteractionHandler implements OpenFileInteractionCallback
{

    @Override
    public void onFileTooBig(String pathToFile)
    {
        Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        SplitFileDialog dialog = new SplitFileDialog( parentShell, pathToFile );
        dialog.create();
        dialog.open();
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
