/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.franca.core.franca.FModel;
import org.junit.Test;

import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDBusDecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class FrancaDBusDecodedRuntimeEventTest
{
    @Test
    public void isRuntimeEventReturned() throws Exception
    {
        DecodedRuntimeEvent decodedRuntimeEvent = mock( DecodedRuntimeEvent.class );
        RuntimeEvent<?> runtimeEvent = mock( RuntimeEvent.class );
        doReturn( runtimeEvent ).when( decodedRuntimeEvent ).getRuntimeEvent();

        FrancaDBusDecodedRuntimeEvent francaDBusDecodedRuntimeEvent = new FrancaDBusDecodedRuntimeEvent( Collections
                .<FModel> emptyList(), decodedRuntimeEvent );

        assertEquals( runtimeEvent, francaDBusDecodedRuntimeEvent.getRuntimeEvent() );
    }
}
