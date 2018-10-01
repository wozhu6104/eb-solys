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

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.core.timesegmentmanager.impl.TimeSegmentAcceptorServiceImpl;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import lombok.Getter;

public class TimeSegmentAcceptorTestHelper
{
    @Getter
    private ChannelListenerNotifier mockedChannelNotifier;
    @Getter
    private ChannelColorProviderService mockedChannelColorProviderService;

    public TimeSegmentAcceptorService createTimeSegmentAcceptorWithMockedChannelManager()
    {
        TimeSegmentAcceptorServiceImpl timeSegmentAcceptorImpl = new TimeSegmentAcceptorServiceImpl();
        timeSegmentAcceptorImpl.bindRuntimeEventChannelManager( getMockedRuntimeEventChannelManager() );
        mockedChannelNotifier = Mockito.mock( ChannelListenerNotifier.class );
        mockedChannelColorProviderService = Mockito.mock( ChannelColorProviderService.class );
        timeSegmentAcceptorImpl.bindChannelListenerNotifier( mockedChannelNotifier );
        timeSegmentAcceptorImpl.bindChannelColorProviderService( mockedChannelColorProviderService );

        return timeSegmentAcceptorImpl;
    }

    private RuntimeEventChannelManager getMockedRuntimeEventChannelManager()
    {
        RuntimeEventChannelManager mock = Mockito.mock( RuntimeEventChannelManager.class );
        Answer<RuntimeEventChannel<?>> createChannelAnswer = new Answer<RuntimeEventChannel<?>>()
        {
            @Override
            public RuntimeEventChannel<?> answer(InvocationOnMock invocation) throws Throwable
            {
                String name = (String)invocation.getArguments()[0];
                Unit<?> unit = (Unit<?>)invocation.getArguments()[1];
                RuntimeEventChannelMock<?> channel = new RuntimeEventChannelMock<String>( name );
                channel.setUnit( unit );
                return channel;
            }
        };
        Mockito.when( mock.createRuntimeEventChannel( Mockito.any(), Mockito.any(), Mockito.any() ) )
                .thenAnswer( createChannelAnswer );
        Mockito.when( mock.createOrGetRuntimeEventChannel( Mockito.any(), Mockito.any(), Mockito.any() ) )
                .thenAnswer( createChannelAnswer );

        return mock;
    }
}
