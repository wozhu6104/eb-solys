/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.reset;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

@Component
public class ResetNotifierImpl implements ResetNotifier
{
    private final ListenerNotifier listenerNotifier;
    private ConnectionService connectionService;
    private ClearChunkDataNotifier clearChunkDataNotifier;

    public ResetNotifierImpl()
    {
        this.listenerNotifier = new ResetListenerOSGIServiceNotifier();
    }

    public ResetNotifierImpl(ListenerNotifier listenerNotifier)
    {
        this.listenerNotifier = listenerNotifier;
    }

    @Reference
    public void bindTargetAdapterCommunicatorControlService(ConnectionService connectionService)
    {
        this.connectionService = connectionService;
    }

    public void unbindTargetAdapterCommunicatorControlService(ConnectionService connectionService)
    {
        this.connectionService = null;
    }

    @Reference
    public void bindClearChunkDataNotifier(ClearChunkDataNotifier clearChunkDataNotifier)
    {
        this.clearChunkDataNotifier = clearChunkDataNotifier;
    }

    public void unbindClearChunkDataNotifier(ClearChunkDataNotifier clearChunkDataNotifier)
    {
        this.clearChunkDataNotifier = null;
    }

    @Override
    public synchronized void performReset()
    {
        connectionService.disconnectFromAllTargets();
        clearChunkDataNotifier.notifyClearChunkData();
        informResetListenerAboutReset();
    }

    public void informResetListenerAboutReset()
    {
        listenerNotifier.notifyListeners();
    }

    private class ResetListenerOSGIServiceNotifier implements ListenerNotifier
    {

        @Override
        public void notifyListeners()
        {
            new OSGIWhiteBoardPatternCaller<ResetListener>( ResetListener.class )
                    .callOSGIService( new OSGIWhiteBoardPatternCommand<ResetListener>()
                    {
                        @Override
                        public void callOSGIService(ResetListener listener)
                        {
                            listener.onReset();
                        }
                    } );
        }
    }
}
