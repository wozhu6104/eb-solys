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

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;

public class DBusMethodCallCache
{
    private final List<DBusMethodCallCacheObj> methodCalls = new ArrayList<DBusMethodCallCacheObj>();

    public void addCallToCache(DBusMessageHeader header)
    {
        methodCalls.add( new DBusMethodCallCacheObj( header ) );
    }

    public DBusMethodCallCacheObj findCallForReturn(DBusMessageHeader header)
    {
        for (DBusMethodCallCacheObj call : methodCalls)
        {
            if (call.matchesResponse( header ))
                return call;
        }
        return null;
    }

    public boolean removeCall(DBusMethodCallCacheObj call)
    {
        return methodCalls.remove( call );
    }

    public boolean removeCallByReturnMsg(DBusMessageHeader header)
    {
        DBusMethodCallCacheObj obj = findCallForReturn( header );
        if (obj != null)
            return removeCall( obj );
        return false;
    }
}
