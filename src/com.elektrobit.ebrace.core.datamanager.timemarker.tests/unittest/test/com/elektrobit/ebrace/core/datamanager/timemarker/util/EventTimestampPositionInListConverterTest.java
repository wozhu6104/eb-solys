/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.timemarker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.EventTimestampPositionInListConverter;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMock;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMocker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class EventTimestampPositionInListConverterTest
{
    @Test
    public void testNoEvents()
    {
        List<TimebasedObject> timestamps = timestampsToListOfEvents( new Long[]{1000L, 1002L} );
        EventTimestampPositionInListConverter sut = new EventTimestampPositionInListConverter( new ArrayList<TimebasedObject>(),
                                                                                               timestamps,
                                                                                               100 );
        Map<TimebasedObject, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 0 ) ) );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 1 ) ) );
    }

    @Test
    public void testNoTimestamps()
    {
        List<TimebasedObject> timestamps = Collections.emptyList();
        List<TimebasedObject> allEvents = new ArrayList<TimebasedObject>();
        allEvents.add( RuntimeEventMocker.mock( 0, 0 ) );
        allEvents.add( RuntimeEventMocker.mock( 10, 0 ) );

        EventTimestampPositionInListConverter sut = new EventTimestampPositionInListConverter( allEvents,
                                                                                               timestamps,
                                                                                               100 );
        Map<TimebasedObject, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 0, result.entrySet().size() );
    }

    @Test
    public void testEventsNull() throws Exception
    {
        List<TimebasedObject> timestamps = timestampsToListOfEvents( new Long[]{1000L, 1002L} );
        EventTimestampPositionInListConverter sut = new EventTimestampPositionInListConverter( null, timestamps, 100 );
        Map<TimebasedObject, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 0 ) ) );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 1 ) ) );
    }

    @Test
    public void testWithData() throws Exception
    {
        List<TimebasedObject> timestamps = timestampsToListOfEvents( new Long[]{40L, 45L, 10L, 100L, 1L, 3L} );

        List<TimebasedObject> allEvents = new ArrayList<TimebasedObject>();
        allEvents.add( RuntimeEventMocker.mock( 5L, 0 ) ); // 0
        allEvents.add( RuntimeEventMocker.mock( 10L, 0 ) );// 1
        allEvents.add( RuntimeEventMocker.mock( 10L, 0 ) );// 2
        allEvents.add( RuntimeEventMocker.mock( 20L, 0 ) );// 3
        allEvents.add( RuntimeEventMocker.mock( 30L, 0 ) );// 4
        allEvents.add( RuntimeEventMocker.mock( 40L, 0 ) );// 5
        allEvents.add( RuntimeEventMocker.mock( 50L, 0 ) );// 6
        allEvents.add( RuntimeEventMocker.mock( 60L, 0 ) );// 7
        allEvents.add( RuntimeEventMocker.mock( 70L, 0 ) );// 8
        allEvents.add( RuntimeEventMocker.mock( 80L, 0 ) );// 9

        EventTimestampPositionInListConverter sut = new EventTimestampPositionInListConverter( allEvents,
                                                                                               timestamps,
                                                                                               100 );
        Map<TimebasedObject, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 6, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 4 ) ) );// 1L
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 5 ) ) );// 3L
        Assert.assertEquals( (Integer)30, result.get( timestamps.get( 2 ) ) );// 10L
        Assert.assertEquals( (Integer)60, result.get( timestamps.get( 0 ) ) );// 40L
        Assert.assertEquals( (Integer)60, result.get( timestamps.get( 1 ) ) );// 45L
        Assert.assertEquals( (Integer)100, result.get( timestamps.get( 3 ) ) );// 100L
    }

    @Test
    public void testAllTimestampsBeforeEvents() throws Exception
    {
        List<TimebasedObject> timestamps = timestampsToListOfEvents( new Long[]{1L, 3L} );

        List<TimebasedObject> allEvents = new ArrayList<TimebasedObject>();
        allEvents.add( RuntimeEventMocker.mock( 5, 0 ) ); // 0
        allEvents.add( RuntimeEventMocker.mock( 10, 0 ) );// 1
        allEvents.add( RuntimeEventMocker.mock( 10, 0 ) );// 2
        allEvents.add( RuntimeEventMocker.mock( 20, 0 ) );// 3
        allEvents.add( RuntimeEventMocker.mock( 30, 0 ) );// 4
        allEvents.add( RuntimeEventMocker.mock( 40, 0 ) );// 5
        allEvents.add( RuntimeEventMocker.mock( 50, 0 ) );// 6
        allEvents.add( RuntimeEventMocker.mock( 60, 0 ) );// 7
        allEvents.add( RuntimeEventMocker.mock( 70, 0 ) );// 8
        allEvents.add( RuntimeEventMocker.mock( 80, 0 ) );// 9

        EventTimestampPositionInListConverter sut = new EventTimestampPositionInListConverter( allEvents,
                                                                                               timestamps,
                                                                                               100 );
        Map<TimebasedObject, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 0 ) ) );
        Assert.assertEquals( (Integer)0, result.get( timestamps.get( 1 ) ) );
    }

    private List<TimebasedObject> timestampsToListOfEvents(Long[] timestamps)
    {
        List<TimebasedObject> result = new ArrayList<TimebasedObject>();
        for (Long timestamp : timestamps)
        {
            result.add( new RuntimeEventMock<Void>( null, timestamp ) );
        }
        return result;
    }
}
