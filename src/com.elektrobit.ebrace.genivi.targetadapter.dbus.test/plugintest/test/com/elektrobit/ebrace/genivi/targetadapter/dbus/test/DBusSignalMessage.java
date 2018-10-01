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

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;

public class DBusSignalMessage
{
    private static final String SIGNAL_SENDER_SERVICE = "signalSenderService";
    private static final String PATH_FOR_BROADCAST = "pathForBroadcast";
    private static final String INTERFACE_FOR_BROADCAST = "interfaceForBroadcast";
    private static final String MEMBER_FOR_BROADCAST = "memberForBroadcast";
    private static final int SIGNAL_SENDER_PID = 3;

    public static DBusApplicationMessage dbusSignalDummy()
    {
        DBusApplicationMessage dBusApplicationMessage = DBusMessageHelper
                .createDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_SIGNAL,
                                    SIGNAL_SENDER_SERVICE,
                                    SIGNAL_SENDER_PID,
                                    new String[]{},
                                    "broadcast",
                                    null,
                                    null,
                                    99,
                                    null,
                                    PATH_FOR_BROADCAST,
                                    INTERFACE_FOR_BROADCAST,
                                    MEMBER_FOR_BROADCAST );
        return dBusApplicationMessage;
    }
}
