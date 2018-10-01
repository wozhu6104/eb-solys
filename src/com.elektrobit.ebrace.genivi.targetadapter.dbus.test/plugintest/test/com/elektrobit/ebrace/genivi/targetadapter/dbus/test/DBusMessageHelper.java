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

import java.util.Arrays;

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader.Builder;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;

public class DBusMessageHelper
{
    public static DBusApplicationMessage createDBusMessage(DBusTraceMessageType messageType, String senderName,
            int senderPid, String[] senderAliases, String receiverName, Integer receiverPid, String[] receiverAliases,
            Integer serial, Integer replySerial, String path, String interf, String member)
    {
        DBusMessageHeader dbusMessageHeader = createDBusHeaderMsg( messageType,
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

        com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage.Builder traceMsgBuilder = DBusEvtTraceMessage
                .newBuilder();

        traceMsgBuilder.setHeader( dbusMessageHeader );
        traceMsgBuilder.setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS );

        com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage.Builder dbusApplicationMsgBuilder = DBusApplicationMessage
                .newBuilder();
        DBusEvtTraceMessage dbusTraceMsg = traceMsgBuilder.build();
        dbusApplicationMsgBuilder.setTraceMessage( dbusTraceMsg );

        return dbusApplicationMsgBuilder.build();
    }

    private static DBusMessageHeader createDBusHeaderMsg(DBusTraceMessageType messageType, String senderName,
            int senderPid, String[] senderAliases, String receiverName, Integer receiverPid, String[] receiverAliases,
            Integer serial, Integer replySerial, String path, String interf, String member)
    {
        Builder dbusMessageHeaderBuilder = DBusMessageHeader.newBuilder();
        dbusMessageHeaderBuilder.setType( messageType );

        dbusMessageHeaderBuilder.setSender( senderName );
        dbusMessageHeaderBuilder.setSenderPid( senderPid );
        dbusMessageHeaderBuilder.addAllSenderAliasNames( Arrays.asList( senderAliases ) );

        dbusMessageHeaderBuilder.setReceiver( receiverName );

        if (receiverPid != null)
            dbusMessageHeaderBuilder.setReceiverPid( receiverPid );
        if (receiverAliases != null)
            dbusMessageHeaderBuilder.addAllReceiverAliasNames( Arrays.asList( receiverAliases ) );

        if (serial != null)
            dbusMessageHeaderBuilder.setSerial( serial );
        if (replySerial != null)
            dbusMessageHeaderBuilder.setReplySerial( replySerial );
        if (path != null)
            dbusMessageHeaderBuilder.setPath( path );
        if (interf != null)
            dbusMessageHeaderBuilder.setInterface( interf );
        if (member != null)
            dbusMessageHeaderBuilder.setMember( member );

        DBusMessageHeader dbusMessageHeader = dbusMessageHeaderBuilder.build();
        return dbusMessageHeader;
    }
}
