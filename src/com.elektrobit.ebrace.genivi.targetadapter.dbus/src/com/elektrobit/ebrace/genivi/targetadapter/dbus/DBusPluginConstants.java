/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;

public interface DBusPluginConstants
{
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.genivi.targetadapter.dbus";

    public static final boolean DBUS_SYSTEM_BUS_ENABLE_DEFAULT_VALUE = true;

    public static final boolean DBUS_SESSION_BUS_ENABLE_DEFAULT_VALUE = true;

    public final MessageType messageType = MessageType.MSG_TYPE_DBUS;

    public static final String DBUS_TREE_DESCRIPTION = "Show DBus related communication nodes";
    public static final String DBUS_TREE_NAME = "DBusCommunicationTree";
    public static final String DBUS_TREE_LEVEL_6 = "DBusMembers";
    public static final String DBUS_TREE_LEVEL_5 = "DBusInterfaces";
    public static final String DBUS_TREE_LEVEL_4 = "DBusObjects";
    public static final String DBUS_TREE_LEVEL_3 = "DBusServices";
    public static final String DBUS_TREE_LEVEL_2 = "DBusProcesses";
    public static final String DBUS_TREE_LEVEL_1 = "DBusCommunication";

    public static final String DBUS_TREE_ROOT_NODE_NAME = "DBusCommunication";
}
