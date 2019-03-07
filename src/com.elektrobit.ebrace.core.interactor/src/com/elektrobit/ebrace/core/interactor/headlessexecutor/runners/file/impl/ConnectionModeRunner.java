/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.api.AutomationModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.callback.CallbackScriptRunPart;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutionListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionStatusListener;
import com.google.common.annotations.VisibleForTesting;

public class ConnectionModeRunner implements AutomationModeRunner, ScriptExecutionListener, ConnectionStatusListener
{
    private final CountDownLatch waitForScriptExecutionLatch;
    private final GenericScriptRunner genericScriptRunner;
    private final ResourcesModelManager resourcesModelManager;
    private final CountDownLatch waitForTargetDisconnectLatch;
    private final ScriptExecutorService scriptExecutorService;
    private final ConnectionService connectionService;

    public ConnectionModeRunner(ResourcesModelManager resourcesModelManager, ConnectionService connectionService,
            ScriptExecutorService scriptExecutorService)
    {
        this( resourcesModelManager,
                connectionService,
                new GenericScriptRunner( new CallbackScriptRunPart( scriptExecutorService ) ),
                new CountDownLatch( 1 ),
                new CountDownLatch( 1 ),
                scriptExecutorService );
    }

    @VisibleForTesting
    public ConnectionModeRunner(ResourcesModelManager resourcesModelManager, ConnectionService connectionService,
            GenericScriptRunner genericScriptRunner, CountDownLatch waitForTargetDisconnectLatch,
            CountDownLatch waitForScriptExecutionLatch, ScriptExecutorService scriptExecutorService)
    {
        this.resourcesModelManager = resourcesModelManager;
        this.connectionService = connectionService;
        this.genericScriptRunner = genericScriptRunner;
        this.waitForTargetDisconnectLatch = waitForTargetDisconnectLatch;
        this.waitForScriptExecutionLatch = waitForScriptExecutionLatch;
        this.scriptExecutorService = scriptExecutorService;
        scriptExecutorService.addScriptExecutionListener( this );
    }

    @Override
    public boolean paramsOk(String connectionData, RaceScriptInfo script, String methodName)
    {
        final String[] connectionDataSplitted = connectionData.trim().split( ":" );
        boolean result = connectionDataSplitted.length == 2;
        if (result == true)
        {
            final String port = connectionDataSplitted[1];
            final Integer portAsNumber = transformPortStringToInt( port );
            result = portAsNumber != null && isPortValid( portAsNumber )
                    && genericScriptRunner.paramsOk( script, methodName );
        }

        return result;
    }

    private boolean isPortValid(int port)
    {
        if (port < 0 || port > 65535)
        {
            return false;
        }
        return true;
    }

    private Integer transformPortStringToInt(final String port)
    {
        Integer portAsInteger = null;
        try
        {
            portAsInteger = Integer.parseInt( port );
        }
        catch (NumberFormatException e)
        {
        }

        return portAsInteger;
    }

    @Override
    public boolean run(String connectionData, RaceScriptInfo script, String methodName)
    {
        connectionService.addConnectionStatusListener( this );

        final String[] connectionDataSplitted = connectionData.trim().split( ":" );

        final String hostname = connectionDataSplitted[0];
        final int port = Integer.parseInt( connectionDataSplitted[1] );

        // TODO provide connection type selection in CLI interface
        List<ConnectionType> allConnectionTypes = resourcesModelManager.getAllConnectionTypes();
        ConnectionType firstConnectionType = allConnectionTypes.get( 0 );
        ConnectionType secondConnectionType = allConnectionTypes.get( 1 );
        ConnectionType connectionType = firstConnectionType.getExtension().equals( "bin" )
                ? firstConnectionType
                : secondConnectionType;
        // TODO hack ends here :)

        final ConnectionModel connection = resourcesModelManager
                .createConnection( hostname + "@" + port, hostname, port, true, connectionType );
        boolean scriptRunningAndConnectionEstablished = genericScriptRunner.run( script, methodName )
                && connectionService.connect( connection );

        if (scriptRunningAndConnectionEstablished)
        {
            waitForTargetDisconnecting();
            genericScriptRunner.stop( script );
            waitForScriptExecution();
        }
        scriptExecutorService.removeScriptExecutionListener( this );
        return scriptRunningAndConnectionEstablished;
    }

    private void waitForTargetDisconnecting()
    {
        try
        {
            waitForTargetDisconnectLatch.await();
        }
        catch (InterruptedException e)
        {
        }
    }

    private void waitForScriptExecution()
    {
        try
        {
            waitForScriptExecutionLatch.await();
        }
        catch (InterruptedException e)
        {
        }
    }

    @Override
    public void onScriptStarted(RaceScriptInfo script)
    {
    }

    @Override
    public void onScriptStopped(RaceScriptInfo script)
    {
        waitForScriptExecutionLatch.countDown();
    }

    @Override
    public void onTargetDisconnected(ConnectionModel disconnected, Set<ConnectionModel> activeConnections)
    {
        waitForTargetDisconnectLatch.countDown();

    }

    @Override
    public void onTargetConnecting(ConnectionModel connecting, Set<ConnectionModel> activeConnections)
    {
    }

    @Override
    public void onTargetConnected(ConnectionModel connected, Set<ConnectionModel> activeConnections)
    {
    }

    @Override
    public void onNewDataRateInKB(ConnectionModel connectionInfo, float datarate)
    {
    }
}
