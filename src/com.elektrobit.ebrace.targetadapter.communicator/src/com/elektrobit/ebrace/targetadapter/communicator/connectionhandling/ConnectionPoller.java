/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.connectionhandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionStatusListener;
import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.elektrobit.ebrace.targetadapter.communicator.api.SendMessageToTargetService;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.ctrl.TargetAgentProtocolCtrl.ProtHandlerCtrlMessage;
import com.elektrobit.ebrace.targetagent.protocol.ctrl.TargetAgentProtocolCtrl.ProtHandlerCtrlMessageId;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

@Component(immediate = true)
public class ConnectionPoller implements ConnectionStatusListener
{
    private static final long HEART_BEAT_DELAY_MS = 2000;

    private Timer timer = null;

    private SendMessageToTargetService sendMessageService;
    private ConnectionService connectionService;

    private Set<ConnectionModel> activeConnections = Collections.emptySet();

    @Reference
    public void setSendMessageToTargetService(SendMessageToTargetService sendMessageService)
    {
        this.sendMessageService = sendMessageService;
    }

    public void unsetSendMessageToTargetService(SendMessageToTargetService sendMessageService)
    {
        this.sendMessageService = null;
    }

    @Reference
    public void setConnectionService(ConnectionService connectionService)
    {
        this.connectionService = connectionService;
    }

    public void unsetConnectionService(ConnectionService connectionService)
    {
        this.connectionService = null;
    }

    @Activate
    public void activate()
    {
        connectionService.addConnectionStatusListener( this );
        startPollerThread();
    }

    @Deactivate
    public void deactivate()
    {
        stopPollerThread();
    }

    @Override
    public void onTargetDisconnected(ConnectionModel disconnected, Set<ConnectionModel> activeConnections)
    {
        this.activeConnections = activeConnections;
    }

    @Override
    public void onTargetConnecting(ConnectionModel connecting, Set<ConnectionModel> activeConnections)
    {
    }

    @Override
    public void onTargetConnected(ConnectionModel connected, Set<ConnectionModel> activeConnections)
    {
        this.activeConnections = activeConnections;
    }

    @Override
    public void onNewDataRateInKB(ConnectionModel connectionInfo, float datarate)
    {
    }

    private void startPollerThread()
    {
        TimerTask sendTask = new TimerTask()
        {
            @Override
            public void run()
            {
                sendHeartbeatMessages();
            }
        };

        timer = new Timer( "ConnectionPoller Timer" );
        timer.schedule( sendTask, 0, HEART_BEAT_DELAY_MS );
    }

    private void stopPollerThread()
    {
        if (timer != null)
        {
            timer.cancel();
        }
    }

    private void sendHeartbeatMessages()
    {
        for (ConnectionModel connectionModel : activeConnections)
        {
            OutgoingMessage message = getMessageForConnection( connectionModel );
            sendMessageService.sendMessage( connectionModel, message );
        }
    }

    private OutgoingMessage getMessageForConnection(ConnectionModel connectionModel)
    {
        String extension = connectionModel.getConnectionType().getExtension();

        if (extension.equals( "bin" ))
        {
            return getTAMessage();
        }
        else if (extension.equals( "dlts" ))
        {
            return getDLTMessage();
        }
        else
        {
            throw new IllegalArgumentException( "unknown poll message for connection " + extension );
        }

    }

    private OutgoingMessage getDLTMessage()
    {
        byte[] dltGetSoftwareVersionMessage = HexStringHelper
                .hexStringToByteArray( "3500001A4543550001CA8F54160141505000434F4E0013000000" );
        return () -> dltGetSoftwareVersionMessage;
    }

    private OutgoingMessage getTAMessage()
    {
        ProtHandlerCtrlMessage.Builder heartbeatRequestMessageBuilder = ProtHandlerCtrlMessage.newBuilder();
        heartbeatRequestMessageBuilder.setId( ProtHandlerCtrlMessageId.PROT_HNDLR_CTRL_CMD_HEARTBEAT_REQUEST_MSG_ID );

        ProtHandlerCtrlMessage heartbeatRequestMessage = heartbeatRequestMessageBuilder.build();
        byte[] heartBeatMsgBodyBytes = heartbeatRequestMessage.toByteArray();

        Header header = createTAProtocolMessageHeader( heartBeatMsgBodyBytes.length );
        byte headerLength = (byte)header.getSerializedSize();
        ByteArrayOutputStream wholeMessage = new ByteArrayOutputStream();

        try
        {
            wholeMessage.write( headerLength );
            header.writeTo( wholeMessage );
            wholeMessage.write( heartBeatMsgBodyBytes );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        byte[] wholeMessageBytes = wholeMessage.toByteArray();
        return () -> wholeMessageBytes;
    }

    private Header createTAProtocolMessageHeader(int payloadLength)
    {
        Header.Builder builder = Header.newBuilder();
        builder.setType( MessageType.MSG_TYPE_PROT_HNDLR_CONTROL );
        builder.setLength( payloadLength );
        builder.setTimestamp( System.currentTimeMillis() );
        int version = VersionHandler.getVersionToken();
        builder.setVersionToken( version );
        return builder.build();
    }

}
