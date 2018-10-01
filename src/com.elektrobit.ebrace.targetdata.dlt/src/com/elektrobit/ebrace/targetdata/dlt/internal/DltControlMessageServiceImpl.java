/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.elektrobit.ebrace.targetadapter.communicator.api.SendMessageToTargetService;
import com.elektrobit.ebrace.targetdata.dlt.api.DltControlMessageService;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltConnectionType;

@Component(immediate = true, enabled = true)
public class DltControlMessageServiceImpl implements DltControlMessageService
{
    private SendMessageToTargetService service;
    private ConnectionService connectionService;

    @Reference
    public void bindTargetMessageBroadcastService(SendMessageToTargetService service)
    {
        this.service = service;
    }

    public void unbindTargetMessageBroadcastService(SendMessageToTargetService service)
    {
        this.service = null;
    }

    @Reference
    public void bindConnectionService(ConnectionService connectionService)
    {
        this.connectionService = connectionService;
    }

    public void unbindConnectionService(ConnectionService connectionService)
    {
        this.connectionService = null;
    }

    @Activate
    public void start()
    {

    }

    @Override
    public void setLogLevel(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, String appId, String ctxId,
            int logLevel)
    {
        OutgoingMessage message = DltControlMessageHelper
                .createControlMessageSetLogLevel( ecuId, appIdSrc, ctxIdSrc, timestamp, appId, ctxId, logLevel );
        ConnectionModel activeDLTConnection = getActiveDLTConnection();
        service.sendMessage( activeDLTConnection, message );
    }

    @Override
    public void setTraceStatus(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, String appId,
            String ctxId, int traceStatus)
    {
        OutgoingMessage message = DltControlMessageHelper
                .createControlMessageSetTraceStatus( ecuId, appIdSrc, ctxIdSrc, timestamp, appId, ctxId, traceStatus );
        ConnectionModel activeDLTConnection = getActiveDLTConnection();
        service.sendMessage( activeDLTConnection, message );
    }

    private ConnectionModel getActiveDLTConnection()
    {
        Set<ConnectionModel> activeConnections = connectionService.getAllActiveConnections();
        for (ConnectionModel activeConnection : activeConnections)
        {
            ConnectionType connectionType = activeConnection.getConnectionType();
            if (connectionType.getExtension().equals( DltConnectionType.EXTENSION ))
            {
                return activeConnection;
            }
        }
        return null;
    }

    @Override
    public void getLogInfo(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, int options, String appId,
            String ctxId)
    {
        ConnectionModel activeDLTConnection = getActiveDLTConnection();
        OutgoingMessage message = DltControlMessageHelper
                .createControlMessageGetLogInfo( ecuId, appIdSrc, ctxIdSrc, timestamp, options, appId, ctxId );
        service.sendMessage( activeDLTConnection, message );

    }

}
