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

public class DBusRequestResponseMessages
{
    private static final int SERVICE_2_PID = 2;
    private static final int SERVICE_1_PID = 1;
    private static final String SERVICE_2_NAME = "service2";
    private static final String SERVICE_1_NAME = "service1";
    private static final String CALLED_PATH_NAME = "path";
    private static final String CALLED_INTERFACE_NAME = "interface";
    private static final String CALLED_MEMBER_NAME = "member";

    public static DBusApplicationMessage dbusRequestDummy()
    {
        DBusApplicationMessage dBusApplicationMessage = DBusMessageHelper
                .createDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL,
                                    SERVICE_1_NAME,
                                    SERVICE_1_PID,
                                    new String[]{},
                                    SERVICE_2_NAME,
                                    SERVICE_2_PID,
                                    new String[]{},
                                    10,
                                    null,
                                    CALLED_PATH_NAME,
                                    CALLED_INTERFACE_NAME,
                                    CALLED_MEMBER_NAME );
        return dBusApplicationMessage;
    }

    public static DBusApplicationMessage dbusResponseDummy()
    {
        DBusApplicationMessage dBusApplicationMessage = DBusMessageHelper
                .createDBusMessage( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN,
                                    "service2 new name",
                                    SERVICE_2_PID,
                                    new String[]{SERVICE_2_NAME}, // original name is as alias in the response
                                    SERVICE_1_NAME,
                                    1,
                                    new String[]{},
                                    null,
                                    10,
                                    null,
                                    null,
                                    null );
        return dBusApplicationMessage;
    }

}
