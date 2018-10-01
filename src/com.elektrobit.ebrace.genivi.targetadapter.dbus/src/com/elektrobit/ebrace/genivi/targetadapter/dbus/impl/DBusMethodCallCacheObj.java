/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.impl;

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;

class DBusMethodCallCacheObj
{
    private final int senderPid, receiverPid, serial;
    private final String path, interfaceName, member;

    public DBusMethodCallCacheObj(DBusMessageHeader header)
    {
        if (header.getType() != DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL)
            throw new IllegalArgumentException( "Message has to be a method call" );
        senderPid = header.getSenderPid();
        receiverPid = header.getReceiverPid();
        serial = header.getSerial();

        path = header.getPath();
        interfaceName = header.getInterface();
        member = header.getMember();
    }

    public boolean matchesResponse(DBusMessageHeader header)
    {
        if (header.getType() != DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN)
            throw new IllegalArgumentException( "Message has to be a method call" );
        if (header.getReplySerial() == serial && header.getSenderPid() == receiverPid
                && header.getReceiverPid() == senderPid)
            return true;
        return false;
    }

    public String getPath()
    {
        return path;
    }

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public String getMember()
    {
        return member;
    }
}
