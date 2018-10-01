/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionStatusListener;
import com.elektrobit.ebrace.targetdata.dlt.api.DltControlMessageService;

@Component(immediate = true)
public class GetAllDltChannelsMessageSenderService implements ConnectionStatusListener
{
    private DltControlMessageService dltControlMessageService;
    private ConnectionService connectionService;

    @Activate
    public void activate()
    {
        connectionService.addConnectionStatusListener( this );
    }

    @Override
    public void onTargetConnected(ConnectionModel connected, Set<ConnectionModel> activeConnections)
    {
        String extension = connected.getConnectionType().getExtension();
        if (extension.equals( DltConnectionType.EXTENSION ))
        {
            sendGetAllChannelsDLTmessage();
        }
    }

    private void sendGetAllChannelsDLTmessage()
    {
        byte optionsByte = 0x07;
        byte[] nulls = new byte[]{0x00, 0x00, 0x00, 0x00};
        int options = Byte.toUnsignedInt( optionsByte );
        dltControlMessageService
                .getLogInfo( "ECU", "APP", "CON", 0, options, new String( nulls ), new String( nulls ) );
    }

    @Reference
    public void bindDltControlMessageService(DltControlMessageService dltControlMessageService)
    {
        this.dltControlMessageService = dltControlMessageService;

    }

    public void unbindDltControlMessageService(DltControlMessageService dltControlMessageService)
    {
        this.dltControlMessageService = null;
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

    @Override
    public void onTargetDisconnected(ConnectionModel disconnected, Set<ConnectionModel> activeConnections)
    {
    }

    @Override
    public void onTargetConnecting(ConnectionModel connecting, Set<ConnectionModel> activeConnections)
    {
    }

}
