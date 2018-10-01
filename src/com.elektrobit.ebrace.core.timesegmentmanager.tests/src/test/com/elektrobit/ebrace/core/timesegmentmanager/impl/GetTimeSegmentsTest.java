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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.RuntimeEventMockHelper;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class GetTimeSegmentsTest
{
    private TimeSegmentAcceptorService timeSegmentManager;

    @Before
    public void setup()
    {
        timeSegmentManager = new TimeSegmentAcceptorTestHelper().createTimeSegmentAcceptorWithMockedChannelManager();
    }

    @Test
    public void testGetSegments() throws Exception
    {
        RuntimeEventChannel<STimeSegment> channel1 = timeSegmentManager.createTimeSegmentChannel( "Channel1", "" );
        RuntimeEventChannel<STimeSegment> channel2 = timeSegmentManager.createTimeSegmentChannel( "Channel2", "" );
        RuntimeEventChannel<STimeSegment> channel3 = timeSegmentManager.createTimeSegmentChannel( "Channel3", "" );
        RuntimeEventChannel<STimeSegment> emptyChannel = timeSegmentManager.createTimeSegmentChannel( "emptyChannel",
                                                                                                      "" );

        timeSegmentManager.add( channel1,
                                RuntimeEventMockHelper.createRuntimeEvent( 1001L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2001L ) );
        timeSegmentManager.add( channel2,
                                RuntimeEventMockHelper.createRuntimeEvent( 1002L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2002L ) );
        timeSegmentManager.add( channel3,
                                RuntimeEventMockHelper.createRuntimeEvent( 1003L ),
                                RuntimeEventMockHelper.createRuntimeEvent( 2003L ) );

        Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result = timeSegmentManager
                .getTimeSegmentsForChannel( Arrays.asList( emptyChannel, channel1, channel2 ) );

        Iterator<Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>>> iterator = result.entrySet().iterator();
        Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> firstEntry = iterator.next();
        Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> secondEntry = iterator.next();
        Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> thirdEntry = iterator.next();

        STimeSegment segment1 = secondEntry.getValue().get( 0 );
        STimeSegment segment2 = thirdEntry.getValue().get( 0 );

        Assert.assertFalse( iterator.hasNext() );

        Assert.assertEquals( emptyChannel, firstEntry.getKey() );
        Assert.assertEquals( 0, firstEntry.getValue().size() );

        Assert.assertEquals( channel1, secondEntry.getKey() );
        Assert.assertEquals( 1, secondEntry.getValue().size() );

        Assert.assertEquals( channel2, thirdEntry.getKey() );
        Assert.assertEquals( 1, thirdEntry.getValue().size() );

        Assert.assertEquals( 1001L, segment1.getStartTime() );
        Assert.assertEquals( 2001L, segment1.getEndTime() );

        Assert.assertEquals( 1002L, segment2.getStartTime() );
        Assert.assertEquals( 2002L, segment2.getEndTime() );
    }

    @Test
    public void testGetSegmentsEmptyChannel() throws Exception
    {
        RuntimeEventChannel<STimeSegment> emptyChannel = timeSegmentManager.createTimeSegmentChannel( "emptyChannel",
                                                                                                      "" );

        Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result = timeSegmentManager
                .getTimeSegmentsForChannel( Arrays.asList( emptyChannel ) );

        Iterator<Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>>> iterator = result.entrySet().iterator();
        Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> entry = iterator.next();
        Assert.assertFalse( iterator.hasNext() );

        Assert.assertEquals( 0, entry.getValue().size() );
    }
}
