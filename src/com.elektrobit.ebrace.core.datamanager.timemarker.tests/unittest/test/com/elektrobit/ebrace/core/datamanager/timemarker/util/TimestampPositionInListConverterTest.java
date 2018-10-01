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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInListConverter;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMocker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TimestampPositionInListConverterTest
{
    @Test
    public void testNoEvents()
    {
        List<Long> timestamps = Arrays.asList( new Long[]{1000L, 1002L} );
        TimestampPositionInListConverter sut = new TimestampPositionInListConverter( new ArrayList<TimebasedObject>(),
                                                                                     timestamps,
                                                                                     100 );
        Map<Long, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( 1000L ) );
        Assert.assertEquals( (Integer)0, result.get( 1002L ) );
    }

    @Test
    public void testNoTimestamps()
    {
        List<Long> timestamps = Arrays.asList( new Long[]{} );
        List<TimebasedObject> events = new ArrayList<TimebasedObject>();
        events.add( RuntimeEventMocker.mock( 0, 0 ) );
        events.add( RuntimeEventMocker.mock( 10, 0 ) );

        TimestampPositionInListConverter sut = new TimestampPositionInListConverter( events, timestamps, 100 );
        Map<Long, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 0, result.entrySet().size() );
    }

    @Test
    public void testEventsNull() throws Exception
    {
        List<Long> timestamps = Arrays.asList( new Long[]{1000L, 1002L} );
        TimestampPositionInListConverter sut = new TimestampPositionInListConverter( null, timestamps, 100 );
        Map<Long, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( 1000L ) );
        Assert.assertEquals( (Integer)0, result.get( 1002L ) );
    }

    @Test
    public void testWithData() throws Exception
    {
        List<Long> timestamps = new ArrayList<Long>( Arrays.asList( new Long[]{40L, 45L, 10L, 100L, 1L, 3L} ) );

        List<TimebasedObject> events = new ArrayList<TimebasedObject>();
        events.add( RuntimeEventMocker.mock( 5L, 0 ) ); // 0
        events.add( RuntimeEventMocker.mock( 10L, 0 ) );// 1
        events.add( RuntimeEventMocker.mock( 10L, 0 ) );// 2
        events.add( RuntimeEventMocker.mock( 20L, 0 ) );// 3
        events.add( RuntimeEventMocker.mock( 30L, 0 ) );// 4
        events.add( RuntimeEventMocker.mock( 40L, 0 ) );// 5
        events.add( RuntimeEventMocker.mock( 50L, 0 ) );// 6
        events.add( RuntimeEventMocker.mock( 60L, 0 ) );// 7
        events.add( RuntimeEventMocker.mock( 70L, 0 ) );// 8
        events.add( RuntimeEventMocker.mock( 80L, 0 ) );// 9

        TimestampPositionInListConverter sut = new TimestampPositionInListConverter( events, timestamps, 100 );
        Map<Long, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 6, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( 1L ) );
        Assert.assertEquals( (Integer)0, result.get( 3L ) );
        Assert.assertEquals( (Integer)30, result.get( 10L ) );
        Assert.assertEquals( (Integer)60, result.get( 40L ) );
        Assert.assertEquals( (Integer)60, result.get( 45L ) );
        Assert.assertEquals( (Integer)100, result.get( 100L ) );
    }

    @Test
    public void testAllTimestampsBeforeEvents() throws Exception
    {
        List<Long> timestamps = new ArrayList<Long>( Arrays.asList( new Long[]{1L, 3L} ) );

        List<TimebasedObject> events = new ArrayList<TimebasedObject>();
        events.add( RuntimeEventMocker.mock( 5, 0 ) ); // 0
        events.add( RuntimeEventMocker.mock( 10, 0 ) );// 1
        events.add( RuntimeEventMocker.mock( 10, 0 ) );// 2
        events.add( RuntimeEventMocker.mock( 20, 0 ) );// 3
        events.add( RuntimeEventMocker.mock( 30, 0 ) );// 4
        events.add( RuntimeEventMocker.mock( 40, 0 ) );// 5
        events.add( RuntimeEventMocker.mock( 50, 0 ) );// 6
        events.add( RuntimeEventMocker.mock( 60, 0 ) );// 7
        events.add( RuntimeEventMocker.mock( 70, 0 ) );// 8
        events.add( RuntimeEventMocker.mock( 80, 0 ) );// 9

        TimestampPositionInListConverter sut = new TimestampPositionInListConverter( events, timestamps, 100 );
        Map<Long, Integer> result = sut.getTimestampPositions();

        Assert.assertEquals( 2, result.entrySet().size() );
        Assert.assertEquals( (Integer)0, result.get( 1L ) );
        Assert.assertEquals( (Integer)0, result.get( 3L ) );
    }
}
