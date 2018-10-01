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
import java.io.IOException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.viewer.resources.handler.BaseResourcesModelHandler;
import com.google.common.io.Files;

public class ExportScriptHandler extends BaseResourcesModelHandler
{

    @Override
    public Object execute(ExecutionEvent event)
    {
        StructuredSelection selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );

        String path = openDirectoryDialog();
        if (path == null)
        {
            return null;
        }

        List<?> selectedobjects = selection.toList();
        for (Object o : selectedobjects)
        {
            if (o instanceof RaceScriptResourceModel)
            {
                exportScript( o, path );
            }
        }
        return null;
    }

    private void exportScript(Object o, String path)
    {
        RaceScriptResourceModel raceScriptResourceModel = (RaceScriptResourceModel)o;
        File raceScriptXtendFile = raceScriptResourceModel.getSourceFile();
        File destination = new File( path + File.separator + raceScriptResourceModel.getName()
                + ScriptConstants.SCRIPT_EXTENSION );
        try
        {
            if (!destination.createNewFile())
            {
                if (!shouldReplaceFile( raceScriptResourceModel.getName() + ScriptConstants.SCRIPT_EXTENSION ))
                {
                    return;
                }
            }
            copyContent( raceScriptXtendFile, destination );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void copyContent(File inputFile, File outputFile) throws IOException
    {
        Files.copy( inputFile, outputFile );
    }

    private boolean shouldReplaceFile(String fileName)
    {
        return MessageDialog.openQuestion( new Shell(),
                                           "Question",
                                           "In the selected folder is allready a file with the name " + fileName
                                                   + ". Do you want to replace it? " );
    }

    private String openDirectoryDialog()
    {
        DirectoryDialog dialog = new DirectoryDialog( new Shell(), SWT.SINGLE );
        return dialog.open();
    }
}
