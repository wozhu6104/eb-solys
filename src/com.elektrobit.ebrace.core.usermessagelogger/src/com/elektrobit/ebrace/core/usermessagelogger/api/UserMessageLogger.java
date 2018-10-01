/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.usermessagelogger.api;

import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;

public interface UserMessageLogger
{
    public void logUserMessage(UserMessageLoggerTypes type, String message);

    public void register(UserMessageLoggerListener userMessageLoggerListener);

    public void unregister(UserMessageLoggerListener userMessageLoggerListener);
}
