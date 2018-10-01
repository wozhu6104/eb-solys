/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.datamanager;

import org.mockito.Mockito;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

@SuppressWarnings("unchecked")
public class RuntimeEventMocker
{
    public static <T> RuntimeEvent<T> mock(long timestamp, T value)
    {
        return mock( timestamp, value, (ModelElement)null );
    }

    public static <T> RuntimeEvent<T> mock(long timestamp, T value, ModelElement modelElement)
    {
        RuntimeEvent<T> mockedRuntimeEvent = Mockito.mock( RuntimeEvent.class );

        Mockito.when( mockedRuntimeEvent.getTimestamp() ).thenReturn( timestamp );
        Mockito.when( mockedRuntimeEvent.getValue() ).thenReturn( value );
        Mockito.when( mockedRuntimeEvent.getModelElement() ).thenReturn( modelElement );

        return mockedRuntimeEvent;
    }

    public static <T> RuntimeEvent<T> mock(long timestamp, T value, RuntimeEventChannel<T> channel)
    {
        RuntimeEvent<T> mockedRuntimeEvent = Mockito.mock( RuntimeEvent.class );

        Mockito.when( mockedRuntimeEvent.getTimestamp() ).thenReturn( timestamp );
        Mockito.when( mockedRuntimeEvent.getValue() ).thenReturn( value );
        Mockito.when( mockedRuntimeEvent.getRuntimeEventChannel() ).thenReturn( channel );

        return mockedRuntimeEvent;
    }

}
