/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.reset;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.reset.ResetNotifierImpl;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;

public class ResetTest
{
    private ListenerNotifier listenerNotifier;
    private ConnectionService targetConnection;
    private ResetNotifierImpl resetNotifierImpl;
    private ClearChunkDataNotifier clearChunkNotifier;

    @Before
    public void setup()
    {
        listenerNotifier = Mockito.mock( ListenerNotifier.class );
        clearChunkNotifier = Mockito.mock( ClearChunkDataNotifier.class );
        targetConnection = Mockito.mock( ConnectionService.class );

        resetNotifierImpl = new ResetNotifierImpl( listenerNotifier );
        resetNotifierImpl.bindTargetAdapterCommunicatorControlService( targetConnection );
        resetNotifierImpl.bindClearChunkDataNotifier( clearChunkNotifier );
    }

    @Test
    public void isResetDone() throws Exception
    {
        resetNotifierImpl.performReset();

        Mockito.verify( targetConnection ).disconnectFromAllTargets();
        Mockito.verify( listenerNotifier, Mockito.times( 1 ) ).notifyListeners();
        Mockito.verify( clearChunkNotifier, Mockito.times( 1 ) ).notifyClearChunkData();

    }
}
