/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorCreateOrGetRuntimeEventChannelCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorCreateOrGetRuntimeEventChannelCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrGetRuntimeEventChannel()
    {
        RuntimeEventChannel<?> channel1 = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( "Channel 1", Unit.KILOBYTE, "My second channel" );
        Assert.assertEquals( 1, runtimeEventAcceptor.getRuntimeEventChannels().size() );
        RuntimeEventChannel<?> channel2 = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( "Channel 1", Unit.KILOBYTE, "My first channel" );
        Assert.assertEquals( channel1, channel2 );
        runtimeEventAcceptor.createOrGetRuntimeEventChannel( "Channel 1", Unit.TEXT, "My first channel" );
    }

    @Test
    public void testCreateOrGetRuntimeEventChannelWithContext() throws Exception
    {
        DataSourceContext dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "file.bin." );

        RuntimeEventChannel<Double> percentChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, "percent.channel", Unit.PERCENT, "" );
        List<RuntimeEventChannel<?>> allChannels = runtimeEventAcceptor.getRuntimeEventChannels();
        Assert.assertEquals( 1, allChannels.size() );
        Assert.assertEquals( percentChannel, allChannels.get( 0 ) );
    }

    @Test
    public void testCustomUnit() throws Exception
    {
        Unit<Long> customUnit = Unit.createCustomUnit( "Length", Long.class );

        DataSourceContext dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "file.bin" );
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, "list channel", customUnit, "" );

        runtimeEventAcceptor.acceptEventMicros( 1000L, channel, null, 1L );

        Assert.assertEquals( 1, runtimeEventAcceptor.getRuntimeEventChannels().size() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFloatNotAllowed()
    {
        Unit<Float> floatUnit = Unit.createCustomUnit( "float unit", Float.class );
        RuntimeEventChannel<Float> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( "Channel 1", floatUnit, "My second channel" );
        runtimeEventAcceptor.acceptEventMicros( 0, channel, null, 0.0f );
    }
}
