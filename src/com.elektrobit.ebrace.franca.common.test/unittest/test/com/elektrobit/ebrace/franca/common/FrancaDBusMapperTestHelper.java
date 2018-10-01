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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class FrancaDBusMapperTestHelper
{

    public static RuntimeEvent<ProtoMessageValue> mockValueInRuntimeEvent(DBusApplicationMessage dBusApplicationMessage)
    {
        return mockValueInRuntimeEvent( "DefaultSummary", dBusApplicationMessage );
    }

    public static RuntimeEvent<ProtoMessageValue> mockValueInRuntimeEvent(String summary,
            DBusApplicationMessage dBusApplicationMessage)
    {
        ProtoMessageValue valueObject = new ProtoMessageValue( summary, dBusApplicationMessage.getTraceMessage() );
        @SuppressWarnings("unchecked")
        RuntimeEvent<ProtoMessageValue> runtimeEvent = mock( RuntimeEvent.class );

        @SuppressWarnings("unchecked")
        RuntimeEventChannel<ProtoMessageValue> runtimeEventChannel = mock( RuntimeEventChannel.class );

        when( runtimeEventChannel.getName() ).thenReturn( "trace.dbus.sessionbus" );
        when( runtimeEvent.getValue() ).thenReturn( valueObject );
        when( runtimeEvent.getRuntimeEventChannel() ).thenReturn( runtimeEventChannel );

        DecodedRuntimeEvent decodedRuntimeEvent = mock( DecodedRuntimeEvent.class );

        when( decodedRuntimeEvent.getRuntimeEventValue() ).thenReturn( valueObject );
        return runtimeEvent;
    }

}
