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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimeSegmentImpl;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class SetTimeSegmentColorTest
{
    private TimeSegmentAcceptorService timeSegmentManager;
    private ChannelListenerNotifier mockedChannelNotifier;

    @Before
    public void setup()
    {
        TimeSegmentAcceptorTestHelper timeSegmentAcceptorTestHelper = new TimeSegmentAcceptorTestHelper();
        timeSegmentManager = timeSegmentAcceptorTestHelper.createTimeSegmentAcceptorWithMockedChannelManager();
        mockedChannelNotifier = timeSegmentAcceptorTestHelper.getMockedChannelNotifier();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setColor() throws Exception
    {
        TimeSegmentImpl mockedTimeSegment = Mockito.mock( TimeSegmentImpl.class );
        RuntimeEventChannel<STimeSegment> mockedChannel = Mockito.mock( RuntimeEventChannel.class );
        Mockito.when( mockedTimeSegment.getChannel() ).thenReturn( mockedChannel );

        SColor color = new SColor( 100, 100, 100 );
        timeSegmentManager.setColor( mockedTimeSegment, color );

        Mockito.verify( mockedTimeSegment ).setColor( color );
        Mockito.verify( mockedChannelNotifier ).notifyChannelChanged( mockedChannel );
    }
}
