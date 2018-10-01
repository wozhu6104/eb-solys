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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class TargetConnectionState extends AbstractSourceProvider implements ConnectionStateNotifyCallback
{
    public final static String TARGET_CONNECTION_STATE_ID = "com.elektrobit.ebrace.viewer.targetconnector.sourceprovider.active";

    public final static String CONNECTED = "CONNECTED";
    public final static String DISCONNECTED = "DISCONNECTED";
    public final static String DISABLED = "DISABLED";

    private boolean connected = false;
    private static boolean enabled = false;

    private final ConnectionService connectionService = new GenericOSGIServiceTracker<ConnectionService>( ConnectionService.class )
            .getService();
    private final ConnectionStateNotifyUseCase targetConnectionNotifyUseCase;

    public TargetConnectionState()
    {
        targetConnectionState();
        targetConnectionNotifyUseCase = UseCaseFactoryInstance.get().makeConnectionStateNotifyUseCase( this );
    }

    private void targetConnectionState()
    {
        connected = !connectionService.getAllActiveConnections().isEmpty();
        if (connected)
        {
            enabled = true;
            setConnected();
        }
    }

    enum State {
        CONNECTED, DISCONNECTED, DISABLED
    };

    @Override
    public String[] getProvidedSourceNames()
    {
        return new String[]{TARGET_CONNECTION_STATE_ID};
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map getCurrentState()
    {
        Map map = new HashMap( 1 );
        String value;
        if (enabled)
        {
            value = connected ? CONNECTED : DISCONNECTED;
        }
        else
        {
            value = DISABLED;
        }

        map.put( TARGET_CONNECTION_STATE_ID, value );
        return map;
    }

    private void setConnected()
    {

        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                connected = true;
                fireSourceChanged( ISources.WORKBENCH, TARGET_CONNECTION_STATE_ID, CONNECTED );

            }
        } );
    }

    private void setDisconnected()
    {

        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                connected = false;
                fireSourceChanged( ISources.WORKBENCH, TARGET_CONNECTION_STATE_ID, DISCONNECTED );
            }
        } );
    }

    private void setDisabled()
    {
        enabled = false;
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                fireSourceChanged( ISources.WORKBENCH, TARGET_CONNECTION_STATE_ID, DISABLED );
            }
        } );
    }

    @Override
    public void dispose()
    {
        if (targetConnectionNotifyUseCase != null)
        {
            targetConnectionNotifyUseCase.unregister();
        }
    }

    @Override
    public void onTargetDisconnected()
    {
        setDisconnected();
    }

    @Override
    public void onTargetConnecting()
    {
        setDisabled();
    }

    @Override
    public void onTargetConnected()
    {
        setConnected();
    }
}
