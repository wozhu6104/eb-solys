/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.timesegmentmanager.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.timesegmentmanager.impl.TimeSegmentAcceptorServiceImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.RuntimeEventMockHelper;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class ClearTimeSegmentChannelTest
{
    private TimeSegmentAcceptorServiceImpl timeSegmentManager;
    private ChannelListenerNotifier mockedChannelNotifier;
    private RuntimeEventChannel<STimeSegment> timeSegmentChannel;

    @Before
    public void setup()
    {
        TimeSegmentAcceptorTestHelper timeSegmentAcceptorTestHelper = new TimeSegmentAcceptorTestHelper();
        timeSegmentManager = (TimeSegmentAcceptorServiceImpl)timeSegmentAcceptorTestHelper
                .createTimeSegmentAcceptorWithMockedChannelManager();
        mockedChannelNotifier = timeSegmentAcceptorTestHelper.getMockedChannelNotifier();
    }

    @SuppressWarnings("unchecked")
    private void createData()
    {
        timeSegmentChannel = Mockito.mock( RuntimeEventChannel.class );
        RuntimeEvent<?> startEvent = RuntimeEventMockHelper.createRuntimeEvent( 1000L );
        RuntimeEvent<?> endEvent = RuntimeEventMockHelper.createRuntimeEvent( 2000L );
        timeSegmentManager.add( timeSegmentChannel, startEvent, endEvent );
    }

    @Test
    public void timesegmentCleared() throws Exception
    {
        createData();
        timeSegmentManager.clear();

        assertEquals( 0, timeSegmentManager.getTimeSegments( timeSegmentChannel ).size() );
        Mockito.verify( mockedChannelNotifier, Mockito.times( 1 ) ).notifyAllChannelsChanged();
    }

    @Test
    public void timesegmentClearedonClearChunkData() throws Exception
    {
        createData();
        timeSegmentManager.onClearChunkData();

        assertEquals( 0, timeSegmentManager.getTimeSegments( timeSegmentChannel ).size() );
        Mockito.verify( mockedChannelNotifier, Mockito.times( 1 ) ).notifyAllChannelsChanged();
    }
}
