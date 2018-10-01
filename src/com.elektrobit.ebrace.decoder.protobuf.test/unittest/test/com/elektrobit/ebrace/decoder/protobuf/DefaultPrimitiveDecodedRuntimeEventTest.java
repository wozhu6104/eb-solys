/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.decoder.protobuf;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.elektrobit.ebrace.decoder.protobuf.model.DefaultPrimitiveDecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class DefaultPrimitiveDecodedRuntimeEventTest
{
    @Test
    public void doubleEventCorrectDecoded() throws Exception
    {
        RuntimeEventChannel<Double> channel = createMockedRuntimeEventChannel( "test.channel1", Unit.PERCENT );
        RuntimeEvent<Double> event = createMockedRuntimeEvent( 1000, 0.0, channel );

        DefaultPrimitiveDecodedRuntimeEvent decodedRuntimeEvent = new DefaultPrimitiveDecodedRuntimeEvent( event,
                                                                                                           "HH:mm:ss.SSS" );

        final DecodedNode rootNode = decodedRuntimeEvent.getDecodedTree().getRootNode();

        final DecodedNode valueNode = rootNode.getChildren().get( 0 );
        final DecodedNode dataTypeNode = rootNode.getChildren().get( 1 );
        final DecodedNode timestampNode = rootNode.getChildren().get( 2 );

        assertEquals( "Value", valueNode.getName() );
        assertEquals( "DataType", dataTypeNode.getName() );
        assertEquals( "Timestamp", timestampNode.getName() );

        assertEquals( "0.0", valueNode.getValue() );
        assertEquals( "Double", dataTypeNode.getValue() );
        assertEquals( "00:00:00.001", timestampNode.getValue() );
    }

    private RuntimeEventChannel<Double> createMockedRuntimeEventChannel(String name, Unit<Double> unit)
    {
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<Double> channel = mock( RuntimeEventChannel.class );
        when( channel.getName() ).thenReturn( name );
        when( channel.getUnit() ).thenReturn( unit );
        return channel;
    }

    private RuntimeEvent<Double> createMockedRuntimeEvent(long timestamp, Double value,
            RuntimeEventChannel<Double> channel)
    {
        @SuppressWarnings("unchecked")
        RuntimeEvent<Double> event = mock( RuntimeEvent.class );

        when( event.getTimestamp() ).thenReturn( timestamp );
        when( event.getRuntimeEventChannel() ).thenReturn( channel );

        when( event.getValue() ).thenReturn( value );

        return event;
    }
}
