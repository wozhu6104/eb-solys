/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.dlt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.targetdata.dlt.api.DltControlMessageService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ConfigureDltChannelPropertiesHandler extends AbstractHandler implements IHandler
{
    private final DltControlMessageService controlService = new GenericOSGIServiceTracker<DltControlMessageService>( DltControlMessageService.class )
            .getService();

    private String applicationId;
    private String contextId;

    private final Map<String, Integer> logLevels = initLogLevels();

    private HashMap<String, Integer> initLogLevels()
    {
        final HashMap<String, Integer> logLevels = new HashMap<>();
        logLevels.put( "default", -1 );
        logLevels.put( "OFF", 0 );
        logLevels.put( "FATAL", 1 );
        logLevels.put( "ERROR", 2 );
        logLevels.put( "WARN", 3 );
        logLevels.put( "INFO", 4 );
        logLevels.put( "DEBUG", 5 );
        logLevels.put( "VERBOSE", 6 );
        return logLevels;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        String channelName = getChannelName( event );
        initFields( channelName );
        String commandName = "";
        try
        {
            commandName = event.getCommand().getName();
        }
        catch (NotDefinedException e)
        {
            e.printStackTrace();
        }
        Integer logLevelInt = logLevels.get( commandName );
        handleConfigurationData( applicationId, contextId, (logLevelInt != null) ? logLevelInt.intValue() : -1 );

        return null;
    }

    private void initFields(String channelName)
    {
        if (channelName != null)
        {
            String[] parts = channelName.split( "\\." );
            if (parts != null)
            {
                if (parts[0].equals( "trace" ) && parts.length > 3)
                {
                    if (parts[1].equals( "dlt" ))
                    {
                        this.applicationId = parts[3];
                        if (parts.length > 4)
                        {
                            this.contextId = parts[4];
                        }
                    }
                }
            }
        }
    }

    private String getChannelName(ExecutionEvent event)
    {
        String channelName = "";
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow( event ).getActivePage().getSelection();
        if (selection != null & selection instanceof IStructuredSelection)
        {
            IStructuredSelection strucSelection = (IStructuredSelection)selection;
            for (Object item : strucSelection.toArray())
            {
                if (item instanceof ChannelTreeNode)
                {
                    channelName = ((ChannelTreeNode)item).getFullName();
                }
            }
        }
        return channelName;
    }

    private void handleConfigurationData(String appId, String ctxId, int logLevel)
    {
        controlService.setLogLevel( "ECU", "APP", "CON", 0, appId, ctxId, logLevel );

        int traceStatus = logLevel > 1 ? 1 : logLevel;
        controlService.setTraceStatus( "ECU", "APP", "CON", 0, appId, ctxId, traceStatus );
    }
}
