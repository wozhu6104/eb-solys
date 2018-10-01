/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.channels.impl;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannelsChangedListener;

public class RuntimeEventChannelLifecycleChangedNotifierImpl implements ListenerNotifier
{

    private final OSGIWhiteBoardPatternCaller<RuntimeEventChannelsChangedListener> whiteBoardPatternCaller;
    private final OSGIWhiteBoardPatternCommand<RuntimeEventChannelsChangedListener> command;

    public RuntimeEventChannelLifecycleChangedNotifierImpl()
    {
        whiteBoardPatternCaller = new OSGIWhiteBoardPatternCaller<RuntimeEventChannelsChangedListener>( RuntimeEventChannelsChangedListener.class );
        command = new OSGIWhiteBoardPatternCommand<RuntimeEventChannelsChangedListener>()
        {
            @Override
            public void callOSGIService(RuntimeEventChannelsChangedListener listener)
            {
                listener.onRuntimeEventChannelsChanged();
            }
        };
    }

    @Override
    public void notifyListeners()
    {
        whiteBoardPatternCaller.callOSGIService( command );
    }

}
