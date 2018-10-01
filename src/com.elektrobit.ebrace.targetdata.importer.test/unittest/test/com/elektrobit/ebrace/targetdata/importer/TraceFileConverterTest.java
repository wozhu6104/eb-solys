/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrameOld.OldHeader;
import com.elektrobit.ebrace.targetdata.importer.internal.TraceFileConverter;

public class TraceFileConverterTest
{
    @Test
    public void converterTest() throws Exception
    {
        int expectedVersion = VersionHandler.getVersionToken();
        long expectedTimestamp = 1000;
        MessageType expectedMessageType = MessageType.MSG_TYPE_DBUS;
        int expectLength = 0;

        TraceFileConverter converter = new TraceFileConverter( VersionHandler.getVersionToken() );
        OldHeader inputHeader = createOldHeader( expectedTimestamp, expectedMessageType, expectLength );

        Header output = converter.convert( inputHeader );

        Assert.assertEquals( expectedVersion, output.getVersionToken() );
        Assert.assertEquals( expectedTimestamp, output.getTimestamp() );
        Assert.assertEquals( expectedMessageType, output.getType() );
        Assert.assertEquals( expectLength, output.getLength() );
    }

    private OldHeader createOldHeader(long expectedTimestamp, MessageType expectedMessageType, int expectLength)
    {
        OldHeader.Builder oldHeaderBuilder = OldHeader.newBuilder();

        oldHeaderBuilder.setTimestamp( expectedTimestamp );
        oldHeaderBuilder.setType( expectedMessageType );
        oldHeaderBuilder.setLength( expectLength );

        OldHeader inputHeader = oldHeaderBuilder.build();
        return inputHeader;
    }
}
