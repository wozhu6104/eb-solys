/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.usermessagelogger;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageNotifyUseCase;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLoggerListener;

public class UserMessageNotifyUseCaseImpl implements UserMessageNotifyUseCase, UserMessageLoggerListener
{
    private UserMessageLoggerNotifyCallback callback;
    private final UserMessageLogger userMessageLogger;

    public UserMessageNotifyUseCaseImpl(UserMessageLoggerNotifyCallback callback, UserMessageLogger userMessageLogger)
    {
        this.callback = callback;
        this.userMessageLogger = userMessageLogger;
        this.userMessageLogger.register( this );
    }

    @Override
    public void unregister()
    {
        userMessageLogger.unregister( this );
        callback = null;
    }

    @Override
    public void newUserMessageReceived(final UserMessageLoggerTypes type, final String message)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onLogUserMessage( type, message );
                }
            }
        } );
    }
}
