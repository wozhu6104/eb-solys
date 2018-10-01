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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elektrobit.ebrace.decoder.protobuf.services.JsonDecoder;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMocker;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class JsonDecoderTest
{
    @Test
    public void testValidJson()
    {
        RuntimeEvent<String> mock = RuntimeEventMocker
                .mock( 1494242567,
                       "{\"id\": 700,\"jsonrpc\": \"2.0\", \"method\": \"MB.registerComponent\",\"params\": { \"componentName\": \"VehicleInfo\"}}" );
        JsonDecoder jsonDecoder = new JsonDecoder();
        DecodedRuntimeEvent decoded = jsonDecoder.decode( mock );
        assertTrue( decoded.getDecodedTree().toString()
                .equals( "{\"FAKE_ROOT\":{\"id\":\"700\",\"jsonrpc\":\"2.0\",\"method\":\"MB.registerComponent\",\"params\":{\"componentName\":\"VehicleInfo\"}}}" ) );
    }

    @Test
    public void testInValidJson()
    {
        RuntimeEvent<String> mock = RuntimeEventMocker
                .mock( 1494242567,
                       "\"id\": 700,\"jsonrpc\": \"2.0\", \"method\": \"MB.registerComponent\",\"params\": { \"componentName\": \"VehicleInfo\"}}" );
        JsonDecoder jsonDecoder = new JsonDecoder();
        DecodedRuntimeEvent event = jsonDecoder.decode( mock );
        assertNull( event );
    }

}
