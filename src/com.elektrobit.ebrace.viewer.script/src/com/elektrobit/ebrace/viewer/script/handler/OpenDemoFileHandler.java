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

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebrace.viewer.common.constants.DemoConstants;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;

public class OpenDemoFileHandler extends AbstractHandler
        implements
            ClearAllDataInteractionCallback,
            OpenFileInteractionCallback

{
    private static final String TITLE = "Open Demo File";
    private static final String MESSAGE = "Do you want to load demo file? All other data will be deleted.";
    private static final int INDEX_YES = 0;
    private static final int INDEX_NO = 1;
    private final String[] buttonTexts = new String[]{"Yes", "No"};

    private final ClearAllDataInteractionUseCase clearAllDataInteractionUseCase = UseCaseFactoryInstance.get()
            .makeClearAllDataInteractionUseCase( this );
    private final OpenFileInteractionUseCase loadFileInteractionUseCase = UseCaseFactoryInstance.get()
            .makeLoadFileInteractionUseCase( this );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        MessageDialog dialog = new MessageDialog( null,
                                                  TITLE,
                                                  null,
                                                  MESSAGE,
                                                  MessageDialog.QUESTION,
                                                  buttonTexts,
                                                  INDEX_NO );
        int result = dialog.open();
        if (result == INDEX_YES)
        {
            clearAllDataInteractionUseCase.reset();
        }
        return null;
    }

    @Override
    public void onResetDone()
    {
        URI pathURI = FileHelper.locateFileInBundle( ViewerScriptPlugin.PLUGIN_ID, DemoConstants.DEMO_RACE_FILE_PATH );
        loadFileInteractionUseCase.openFile( pathURI.getPath() );
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
    public void dispose()
    {
        clearAllDataInteractionUseCase.unregister();
        loadFileInteractionUseCase.unregister();
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
