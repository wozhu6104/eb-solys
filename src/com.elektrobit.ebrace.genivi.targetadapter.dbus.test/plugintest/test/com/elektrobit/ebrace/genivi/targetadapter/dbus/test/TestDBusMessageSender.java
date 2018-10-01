/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;

public class TestDBusMessageSender
{
    private final GenericOSGIServiceTracker<ProtocolMessageDispatcher> dispatcherTracker = new GenericOSGIServiceTracker<ProtocolMessageDispatcher>( ProtocolMessageDispatcher.class );
    private final DataSourceContext context = new DataSourceContext( SOURCE_TYPE.FILE, "test." );

    public void sendDBusMessage(DBusTraceMessageType messageType, String senderName, int senderPid,
            String[] senderAliases, String receiverName, Integer receiverPid, String[] receiverAliases, Integer serial,
            Integer replySerial, String path, String interf, String member)
    {
        DBusApplicationMessage dBusApplicationMessage = DBusMessageHelper
                .createDBusMessage( messageType,
                                    senderName,
                                    senderPid,
                                    senderAliases,
                                    receiverName,
                                    receiverPid,
                                    receiverAliases,
                                    serial,
                                    replySerial,
                                    path,
                                    interf,
                                    member );

        sendDBusMessage( dBusApplicationMessage );
    }

    public void sendDBusMessage(DBusApplicationMessage dBusApplicationMessage)
    {

        ProtocolMessageDispatcher service = dispatcherTracker.getService();
        service.newProtocolMessageReceived( TimestampMocker.mock( 1000 ),
                                            MessageType.MSG_TYPE_DBUS,
                                            dBusApplicationMessage.toByteArray(),
                                            new MockedTimestampCreator(),
                                            context );
    }

}
