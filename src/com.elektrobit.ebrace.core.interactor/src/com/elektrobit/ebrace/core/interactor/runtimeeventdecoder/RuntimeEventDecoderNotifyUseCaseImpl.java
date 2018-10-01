/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.runtimeeventdecoder;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;

import lombok.extern.log4j.Log4j;

@Log4j
public class RuntimeEventDecoderNotifyUseCaseImpl implements RuntimeEventDecoderNotifyUseCase
{
    private RuntimeEventDecoderNotifyCallback callback;

    public RuntimeEventDecoderNotifyUseCaseImpl(RuntimeEventDecoderNotifyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public void unregister()
    {
        callback = null;
    }

    @Override
    public void decodeRuntimeEvent(final RuntimeEvent<?> event)
    {
        DecoderService decoderService = DecoderServiceManagerImpl.getInstance().getDecoderServiceForEvent( event );
        if (decoderService != null)
        {
            DecodedRuntimeEvent decodedRuntimeEvent = decoderService.decode( event );
            postDecodedEventToCallBack( decodedRuntimeEvent );
        }
        else
        {
            log.warn( "Couldn't decode event, because no decoder was registered for this event." );
            log.warn( "Event was " + event );
        }
    }

    private void postDecodedEventToCallBack(final DecodedRuntimeEvent decodedRuntimeEvent)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onRuntimeEventDecoded( decodedRuntimeEvent );
                }
            }
        } );
    }
}
