/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.validation.ValidationPlugin;
import com.elektrobit.ebrace.validation.dialog.DANAValidationDialog;
import com.elektrobit.ebrace.validation.preferences.PreferencesConstants;

public class RunValidationHandler extends AbstractHandler
{
    private static final String NO_SPLASH_ARG = "-noSplash";
    private static final String MODEL_FOR_VALIDATION = "-modelForValidation";
    private static final String PATH_TO_FILE_WITH_EVENTS_ARG = "-fileList";
    private static final String VALIDATE = "-validate";
    private boolean isHeadless;
    String[] resultString;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        DANAValidationDialog dialog = new DANAValidationDialog( HandlerUtil.getActiveShell( event ) );
        int dialogStatus = dialog.open();

        if (dialogStatus == Dialog.OK)
        {
            resultString = dialog.getResultString();
            isHeadless = ValidationPlugin.getDefault().getPreferenceStore()
                    .getBoolean( PreferencesConstants.VALIDATION_HEADLESS_RUN_ID );
            ProcessBuilder validationToolProcessBuilder = createProcesssBuilder();
            validationToolProcessBuilder.redirectErrorStream();
            try
            {
                validationToolProcessBuilder.start();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    private ProcessBuilder createProcesssBuilder()
    {
        String pathToExe = ValidationPlugin.getDefault().getPreferenceStore()
                .getString( PreferencesConstants.VALIDATION_TOOL_PATH_TO_EXE_ID );
        ProcessBuilder validationToolProcessBuilder = null;
        if (isHeadless)
        {
            validationToolProcessBuilder = new ProcessBuilder( pathToExe,
                                                               MODEL_FOR_VALIDATION,
                                                               resultString[0],
                                                               PATH_TO_FILE_WITH_EVENTS_ARG,
                                                               resultString[1],
                                                               "-application",
                                                               "application.headless",
                                                               NO_SPLASH_ARG,
                                                               "-consoleLog",
                                                               VALIDATE,
                                                               "-data",
                                                               resultString[2],
                                                               "-consoleLog",
                                                               "-noExit" );
        }
        else
        {
            validationToolProcessBuilder = new ProcessBuilder( pathToExe,
                                                               MODEL_FOR_VALIDATION,
                                                               resultString[0],
                                                               PATH_TO_FILE_WITH_EVENTS_ARG,
                                                               resultString[1],
                                                               "-consoleLog",
                                                               VALIDATE,
                                                               "-data",
                                                               resultString[2] );
        }

        return validationToolProcessBuilder;
    }
}
