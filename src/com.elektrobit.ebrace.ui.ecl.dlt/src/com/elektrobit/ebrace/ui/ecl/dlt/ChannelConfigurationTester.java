/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.dlt;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ChannelConfigurationTester extends PropertyTester implements ConnectionStateNotifyCallback
{

    private final ConnectionStateNotifyUseCase connectionStateNotifyUseCase;

    public ChannelConfigurationTester()
    {
        connectionStateNotifyUseCase = UseCaseFactoryInstance.get().makeConnectionStateNotifyUseCase( this );
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (receiver instanceof List)
        {
            @SuppressWarnings("unchecked")
            List<ChannelTreeNode> items = (List<ChannelTreeNode>)receiver;
            long filter = items.stream().filter( node -> node.getFullName().startsWith( "trace.dlt" ) ).count();
            boolean connected = connectionStateNotifyUseCase.isConnected();
            if (filter == items.size() && items.size() > 0 && connected)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onTargetDisconnected()
    {
    }

    @Override
    public void onTargetConnecting()
    {
    }

    @Override
    public void onTargetConnected()
    {
    }

}
