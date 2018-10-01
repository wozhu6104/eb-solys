/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.handler;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.elektrobit.ebrace.viewer.chartengine.ChartEnginePluginConstants;
import com.elektrobit.ebrace.viewer.chartengine.internal.Activator;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;

public class SelectionHandler extends AbstractHandler implements EventHandler
{

    private static final String GOTOSELECTION_PARAMETER = "gotoselection";
    private static final String CLEAR_PARAMETER = "clear";

    public SelectionHandler()
    {
        setBaseEnabled( false );
        registerThisAsEventHandler();
    }

    private void registerThisAsEventHandler()
    {
        String[] topics = new String[]{EventConstants.EVENT_TOPIC, "com/elektrobit/ebrace/events/clickedtimestamp"};
        Dictionary<String, Object> eventHandlerProperties = new Hashtable<String, Object>();
        eventHandlerProperties.put( EventConstants.EVENT_TOPIC, topics );
        Activator.getDefault().getBundle().getBundleContext().registerService( EventHandler.class.getName(),
                                                                               this,
                                                                               eventHandlerProperties );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ITableViewerView tableViewer = getTableViewerToUpdate( event );
        if (tableViewer != null)
        {
            String parameter = event.getParameter( ChartEnginePluginConstants.SELECTION_HANDLER_PARAMETER );
            if (parameter == null)
                return null;
            if (parameter.equals( CLEAR_PARAMETER ))
            {
                tableViewer.getTreeViewer().setSelection( new StructuredSelection( new Object[]{} ) );
                // runtimeEventLoggerView.clearSelection();
                disableClearSelectionButton();
            }
            else if (parameter.equals( GOTOSELECTION_PARAMETER ))
            {
                IStructuredSelection s = (IStructuredSelection)tableViewer.getTreeViewer().getSelection();
                tableViewer.getTreeViewer().reveal( s.getFirstElement() );
            }

        }

        return null;
    }

    private ITableViewerView getTableViewerToUpdate(ExecutionEvent event)
    {
        IWorkbenchPart activePart;
        try
        {
            activePart = HandlerUtil.getActivePartChecked( event );
            if (activePart instanceof ITableViewerView)
            {
                return (ITableViewerView)activePart;
            }
        }
        catch (ExecutionException e)
        {
            return null;
        }

        return null;
    }

    private void disableClearSelectionButton()
    {
        setBaseEnabled( false );
    }

    @Override
    public void handleEvent(Event event)
    {
        enableClearSelectionButton();
    }

    private void enableClearSelectionButton()
    {
        if (!super.isEnabled())
        {
            setBaseEnabled( true );
        }
    }

}
