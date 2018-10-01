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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.RuntimeEventMockHelper;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class AddTimeSegmentToChannelTest
{
    private TimeSegmentAcceptorService timeSegmentManager;
    private ChannelListenerNotifier mockedChannelNotifier;
    private ChannelColorProviderService mockedChannelColorProviderService;

    @Before
    public void setup()
    {
        TimeSegmentAcceptorTestHelper timeSegmentAcceptorTestHelper = new TimeSegmentAcceptorTestHelper();
        timeSegmentManager = timeSegmentAcceptorTestHelper.createTimeSegmentAcceptorWithMockedChannelManager();
        mockedChannelNotifier = timeSegmentAcceptorTestHelper.getMockedChannelNotifier();
        mockedChannelColorProviderService = timeSegmentAcceptorTestHelper.getMockedChannelColorProviderService();
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfChannelNull() throws Exception
    {
        timeSegmentManager.add( null,
                                RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2000L ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfSegmentNull() throws Exception
    {
        RuntimeEventChannel<STimeSegment> timeSegmentChannel = timeSegmentManager.createTimeSegmentChannel( "Channel",
                                                                                                            "" );
        timeSegmentManager.add( timeSegmentChannel, null, null );
    }

    @Test
    public void addSegmentToChannel() throws Exception
    {
        RuntimeEventChannel<STimeSegment> timeSegmentChannel = timeSegmentManager.createTimeSegmentChannel( "Channel",
                                                                                                            "" );

        timeSegmentManager.add( timeSegmentChannel,
                                RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2000L ) );

        assertEquals( 1, timeSegmentManager.getTimeSegments( timeSegmentChannel ).size() );
        Mockito.verify( mockedChannelNotifier, Mockito.times( 1 ) ).notifyChannelChanged( timeSegmentChannel );
    }

    @Test
    public void addSegmentWithLabelToChannel() throws Exception
    {
        RuntimeEventChannel<STimeSegment> timeSegmentChannel = timeSegmentManager.createTimeSegmentChannel( "Channel",
                                                                                                            "" );

        timeSegmentManager.add( timeSegmentChannel,
                                RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2000L ),
                                "MyLabel" );

        assertEquals( "MyLabel", timeSegmentManager.getTimeSegments( timeSegmentChannel ).get( 0 ).getLabel() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSegmentIncorrectBoundaries()
    {
        RuntimeEventChannel<STimeSegment> timeSegmentChannel = timeSegmentManager.createTimeSegmentChannel( "Channel",
                                                                                                            "" );
        timeSegmentManager.add( timeSegmentChannel,
                                RuntimeEventMockHelper.createRuntimeEvent( 2000L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                "MyLabel" );
    }

    @Test
    public void defaultSegmentColorIsChannelColor()
    {
        RuntimeEventChannel<STimeSegment> channel = timeSegmentManager.createTimeSegmentChannel( "Channel", "" );
        SColor desiredColor = new SColor( 123, 123, 123 );
        Mockito.when( mockedChannelColorProviderService.createAndGetColorForChannel( channel ) )
                .thenReturn( desiredColor );

        STimeSegment newSegment = timeSegmentManager.add( channel,
                                                          RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                                          RuntimeEventMockHelper.createRuntimeEvent( 2000L ),
                                                          "MyLabel" );

        Assert.assertEquals( desiredColor, newSegment.getColor() );
    }

    @Test
    public void testSorting() throws Exception
    {
        RuntimeEventChannel<STimeSegment> channel = timeSegmentManager.createTimeSegmentChannel( "Channel", "" );

        STimeSegment thirdSegment = timeSegmentManager.add( channel,
                                                            RuntimeEventMockHelper.createRuntimeEvent( 1000L ),
                                                            RuntimeEventMockHelper.createRuntimeEvent( 2000L ) );

        STimeSegment secondSegment = timeSegmentManager.add( channel,
                                                             RuntimeEventMockHelper.createRuntimeEvent( 900L ),
                                                             RuntimeEventMockHelper.createRuntimeEvent( 1100L ) );

        STimeSegment firstSegment = timeSegmentManager.add( channel,
                                                            RuntimeEventMockHelper.createRuntimeEvent( 100L ),
                                                            RuntimeEventMockHelper.createRuntimeEvent( 200L ) );

        List<STimeSegment> result = timeSegmentManager.getTimeSegments( channel );
        assertEquals( 3, result.size() );
        assertEquals( firstSegment, result.get( 0 ) );
        assertEquals( secondSegment, result.get( 1 ) );
        assertEquals( thirdSegment, result.get( 2 ) );

    }
}
