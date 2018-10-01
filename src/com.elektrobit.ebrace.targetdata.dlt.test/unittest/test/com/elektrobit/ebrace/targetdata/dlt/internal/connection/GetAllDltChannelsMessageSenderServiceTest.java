/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetdata.dlt.api.DltControlMessageService;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltConnectionType;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.GetAllDltChannelsMessageSenderService;

public class GetAllDltChannelsMessageSenderServiceTest
{
    private GetAllDltChannelsMessageSenderService sutMessageSenderService;
    private ConnectionService mockedConnectionService;
    private DltControlMessageService mockedDltControlMessageService;

    @Before
    public void setup()
    {
        sutMessageSenderService = new GetAllDltChannelsMessageSenderService();

        mockedConnectionService = Mockito.mock( ConnectionService.class );
        sutMessageSenderService.bindConnectionService( mockedConnectionService );

        mockedDltControlMessageService = Mockito.mock( DltControlMessageService.class );
        sutMessageSenderService.bindDltControlMessageService( mockedDltControlMessageService );

        sutMessageSenderService.activate();
    }

    @Test
    public void testRegistration() throws Exception
    {
        Mockito.verify( mockedConnectionService ).addConnectionStatusListener( sutMessageSenderService );
    }

    @Test
    public void testNoMessageBeforeNotification() throws Exception
    {
        Mockito.verifyNoMoreInteractions( mockedDltControlMessageService );
    }

    @Test
    public void testMessageSendWhenConnected() throws Exception
    {
        ConnectionModel mockedConnection = Mockito.mock( ConnectionModel.class );
        Mockito.when( mockedConnection.getConnectionType() ).thenReturn( new DltConnectionType() );
        sutMessageSenderService.onTargetConnected( mockedConnection, null );
        byte[] nulls = new byte[]{0x00, 0x00, 0x00, 0x00};
        String stringNulls = new String( nulls );

        Mockito.verify( mockedDltControlMessageService ).getLogInfo( "ECU",
                                                                     "APP",
                                                                     "CON",
                                                                     0,
                                                                     7,
                                                                     stringNulls,
                                                                     stringNulls );
    }
}
