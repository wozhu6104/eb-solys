/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.usermessagelogger.internal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLoggerListener;

@Component
public class UserMessageLoggerImpl implements UserMessageLogger
{
    private final List<UserMessageLoggerListener> listeners = new ArrayList<UserMessageLoggerListener>();

    @Override
    public synchronized void logUserMessage(UserMessageLoggerTypes type, String message)
    {
        for (UserMessageLoggerListener listener : listeners)
        {
            listener.newUserMessageReceived( type, message );
        }
    }

    @Override
    public void register(UserMessageLoggerListener userMessageLoggerListener)
    {
        listeners.add( userMessageLoggerListener );
    }

    @Override
    public void unregister(UserMessageLoggerListener userMessageLoggerListener)
    {
        listeners.remove( userMessageLoggerListener );
    }
}
