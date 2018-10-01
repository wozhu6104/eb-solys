/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.usermessagelogger.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLoggerListener;
import com.elektrobit.ebrace.core.usermessagelogger.internal.UserMessageLoggerImpl;

public class UserMessageLoggerTest
{
    private UserMessageLogger userMessageLogger;
    private UserMessageLoggerListener userMessageLoggerListenerMock;
    private static final String ERROR_TEST_MESSAGE = "Error test message";
    private static final String WARNING_TEST_MESSAGE = "Warning test message";
    private static final String INFO_TEST_MESSAGE = "Info test message";

    @Before
    public void setUp()
    {
        userMessageLoggerListenerMock = Mockito.mock( UserMessageLoggerListener.class );
        userMessageLogger = new UserMessageLoggerImpl();
        userMessageLogger.register( userMessageLoggerListenerMock );
    }

    @After
    public void tearDown()
    {
        userMessageLogger.unregister( userMessageLoggerListenerMock );
    }

    @Test
    public void logErrorTest()
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR, ERROR_TEST_MESSAGE );
        Mockito.verify( userMessageLoggerListenerMock ).newUserMessageReceived( UserMessageLoggerTypes.ERROR,
                                                                                ERROR_TEST_MESSAGE );
    }

    @Test
    public void logWarningTest()
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.WARNING, WARNING_TEST_MESSAGE );
        Mockito.verify( userMessageLoggerListenerMock ).newUserMessageReceived( UserMessageLoggerTypes.WARNING,
                                                                                WARNING_TEST_MESSAGE );
    }

    @Test
    public void logInfoTest()
    {
        userMessageLogger.logUserMessage( UserMessageLoggerTypes.INFO, INFO_TEST_MESSAGE );
        Mockito.verify( userMessageLoggerListenerMock ).newUserMessageReceived( UserMessageLoggerTypes.INFO,
                                                                                INFO_TEST_MESSAGE );
    }
}
