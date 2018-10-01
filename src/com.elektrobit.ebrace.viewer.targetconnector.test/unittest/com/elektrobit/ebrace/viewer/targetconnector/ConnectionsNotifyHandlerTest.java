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

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public class ConnectionsNotifyHandlerTest
{
    private ConnectToTargetModalWindow underTest;
    private ConnectionsNotifyHandler handler;

    private List<ResourceModel> list;

    @Before
    public void setup()
    {
        underTest = mock( ConnectToTargetModalWindow.class );
        list = new ArrayList<>();
        handler = new ConnectionsNotifyHandler( underTest );
    }

    @Test
    public void verifyConnectionsChangedReloadsConnections()
    {
        handler.onConnectionsChanged( list );

        verify( underTest, times( 1 ) ).reloadConnections( anyListOf( ResourceModel.class ) );
    }

}
