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

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;

@Component(immediate = true)
public class ClearChunkDataNotifierImpl implements ClearChunkDataNotifier
{
    public ClearChunkDataNotifierImpl()
    {
    }

    @Override
    public void notifyClearChunkData()
    {
        notifyListeners();
    }

    private void notifyListeners()
    {
        new OSGIWhiteBoardPatternCaller<ClearChunkDataListener>( ClearChunkDataListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<ClearChunkDataListener>()
                {
                    @Override
                    public void callOSGIService(ClearChunkDataListener listener)
                    {
                        listener.onClearChunkData();
                    }
                } );
    }

}
