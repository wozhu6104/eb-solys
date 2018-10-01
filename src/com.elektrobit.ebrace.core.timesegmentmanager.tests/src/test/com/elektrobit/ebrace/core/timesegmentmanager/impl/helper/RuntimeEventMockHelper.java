/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuntimeEventMockHelper
{
    public static RuntimeEvent<?> createRuntimeEvent(long timestamp)
    {
        RuntimeEvent<?> event = mock( RuntimeEvent.class );
        when( event.getTimestamp() ).thenReturn( timestamp );
        return event;
    }
}
