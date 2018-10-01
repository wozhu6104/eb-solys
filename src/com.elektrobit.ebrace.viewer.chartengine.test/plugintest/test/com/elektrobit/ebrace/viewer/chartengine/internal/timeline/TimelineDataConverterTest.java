/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;
import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebrace.dev.test.util.datamanager.STimeSegmentMock;
import com.elektrobit.ebrace.viewer.chartengine.internal.timeline.SolysTimeEvent;
import com.elektrobit.ebrace.viewer.chartengine.internal.timeline.SolysTimeGraphEntry;
import com.elektrobit.ebrace.viewer.chartengine.internal.timeline.TimelineDataConverter;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public class TimelineDataConverterTest
{
    private SColor colorGreen;
    private SColor colorBlue;

    @Test
    public void testEmpty() throws Exception
    {
        List<TimeGraphEntry> result = new TimelineDataConverter( Collections.emptyMap() ).buildInput();
        Assert.assertTrue( result.isEmpty() );
    }

    @Test
    public void testData() throws Exception
    {
        RuntimeEventChannel<STimeSegment> oneSegmentChannel = new RuntimeEventChannelMock<>( "timesegment channel 1" );
        RuntimeEventChannel<STimeSegment> twoSegmentsChannel = new RuntimeEventChannelMock<>( "timesegment channel 1" );

        colorGreen = new SColor( 0, 255, 0 );
        colorBlue = new SColor( 0, 0, 255 );
        STimeSegment channel1Segment1 = new STimeSegmentMock( 1000, 2000, "c1 seg1", colorBlue, oneSegmentChannel );
        STimeSegment channel2Segment1 = new STimeSegmentMock( 1001, 2001, "c2 seg1", colorBlue, twoSegmentsChannel );
        STimeSegment channel2Segment2 = new STimeSegmentMock( 2002, 3002, "c2 seg2", colorGreen, twoSegmentsChannel );

        Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> input = new LinkedHashMap<>();
        input.put( oneSegmentChannel, Arrays.asList( channel1Segment1 ) );
        input.put( twoSegmentsChannel, Arrays.asList( channel2Segment1, channel2Segment2 ) );

        TimelineDataConverter sutTimelineDataConverter = new TimelineDataConverter( input );
        List<TimeGraphEntry> result = sutTimelineDataConverter.buildInput();
        Set<SColor> allColors = sutTimelineDataConverter.getAllColors();

        Assert.assertEquals( 2, result.size() );

        assertEvents( channel1Segment1, channel2Segment1, channel2Segment2, result );
        assertEntriesHaveChannelReference( oneSegmentChannel, twoSegmentsChannel, result );
        assertColors( allColors );
    }

    private void assertEntriesHaveChannelReference(RuntimeEventChannel<STimeSegment> oneSegmentChannel,
            RuntimeEventChannel<STimeSegment> twoSegmentsChannel, List<TimeGraphEntry> result)
    {
        SolysTimeGraphEntry entryForChannel1 = (SolysTimeGraphEntry)result.get( 0 );
        SolysTimeGraphEntry entryForChannel2 = (SolysTimeGraphEntry)result.get( 1 );

        Assert.assertEquals( oneSegmentChannel, entryForChannel1.getChannel() );
        Assert.assertEquals( twoSegmentsChannel, entryForChannel2.getChannel() );
    }

    private void assertEvents(STimeSegment channel1Segment1, STimeSegment channel2Segment1,
            STimeSegment channel2Segment2, List<TimeGraphEntry> result)
    {
        TimeGraphEntry channel1Entry = result.get( 0 );
        TimeGraphEntry channel2Entry = result.get( 1 );

        Assert.assertEquals( 1000000, channel1Entry.getStartTime() );
        Assert.assertEquals( 2000000, channel1Entry.getEndTime() );

        Assert.assertEquals( 1001000, channel2Entry.getStartTime() );
        Assert.assertEquals( 3002000, channel2Entry.getEndTime() );

        Iterator<ITimeEvent> channel1Iterator = channel1Entry.getTimeEventsIterator();
        SolysTimeEvent channel1Event1 = (SolysTimeEvent)channel1Iterator.next();
        Assert.assertFalse( channel1Iterator.hasNext() );

        Assert.assertEquals( channel1Segment1, channel1Event1.getTimeSegment() );

        Iterator<ITimeEvent> channel2Iterator = channel2Entry.getTimeEventsIterator();
        SolysTimeEvent channel2Event1 = (SolysTimeEvent)channel2Iterator.next();
        SolysTimeEvent channel2Event2 = (SolysTimeEvent)channel2Iterator.next();
        Assert.assertFalse( channel2Iterator.hasNext() );

        Assert.assertEquals( channel2Segment1, channel2Event1.getTimeSegment() );
        Assert.assertEquals( channel2Segment2, channel2Event2.getTimeSegment() );

        Assert.assertEquals( 1000000, channel1Event1.getTime() );
        Assert.assertEquals( 1000000, channel1Event1.getDuration() );

        Assert.assertEquals( 1001000, channel2Event1.getTime() );
        Assert.assertEquals( 1000000, channel2Event1.getDuration() );

        Assert.assertEquals( 2002000, channel2Event2.getTime() );
        Assert.assertEquals( 1000000, channel2Event2.getDuration() );
    }

    private void assertColors(Set<SColor> allColors)
    {
        Assert.assertEquals( 2, allColors.size() );
        Assert.assertTrue( allColors.contains( colorBlue ) );
        Assert.assertTrue( allColors.contains( colorGreen ) );
    }

    @Test
    public void microsToNanos() throws Exception
    {
        Assert.assertEquals( 1000000, TimelineDataConverter.microsToNanos( 1000 ) );
    }

    @Test
    public void nanosToMicros() throws Exception
    {
        Assert.assertEquals( 11, TimelineDataConverter.nanosToMicros( 10800 ) );
    }
}
