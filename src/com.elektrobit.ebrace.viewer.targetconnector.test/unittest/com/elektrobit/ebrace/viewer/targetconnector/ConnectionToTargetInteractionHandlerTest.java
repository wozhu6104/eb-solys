/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.targetconnector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class ConnectionToTargetInteractionHandlerTest
{

    private ConnectToTargetModalWindow underTest;

    @Before
    public void setup()
    {
        underTest = mock( ConnectToTargetModalWindow.class );
    }

    @Test
    public void verifyOnConnectedClosesDialog()
    {
        ConnectionToTargetInteractionHandler handler = new ConnectionToTargetInteractionHandler( underTest );
        handler.onConnected();

        verify( underTest, times( 1 ) ).close();
    }

}
