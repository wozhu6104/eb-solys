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
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class ExportDBusMessagesHandler extends AbstractHandler
{
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try
        {
            FileDialog fileToSaveDialog = new FileDialog( HandlerUtil.getActiveShell( event ), SWT.SAVE );
            fileToSaveDialog.setFilterExtensions( new String[]{"*.log"} );
            fileToSaveDialog.setOverwrite( true );
            String pathToFile = fileToSaveDialog.open();
            if (pathToFile == null || pathToFile.isEmpty())
            {
                return null;
            }
            FileHandler exporteEventsForValidationHandler = new FileHandler( pathToFile );
            exporteEventsForValidationHandler.setFormatter( new EBRaceTextFormatter() );
            IWorkbenchPart part = HandlerUtil.getActivePart( event );
            if (part instanceof ITableViewerView)
            {
                List<RuntimeEvent<?>> events = (List<RuntimeEvent<?>>)((ITableViewerView)part).getContent();
                for (RuntimeEvent<?> e : events)
                {
                    if (e.getModelElement() instanceof ComRelation)
                    {
                        LogRecord logRecordForEvent = new RuntimeEventLogRecord( Level.ALL,
                                                                                 String.valueOf( e.getValue() ),
                                                                                 e );
                        logRecordForEvent.setMillis( e.getTimestamp() );
                        exporteEventsForValidationHandler.publish( logRecordForEvent );
                    }
                }
            }

            exporteEventsForValidationHandler.flush();
            exporteEventsForValidationHandler.close();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
