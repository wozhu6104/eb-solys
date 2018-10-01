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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.timemarker.model.TimeMarkerManagerImpl;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerManagerImplTest
{
    public TimeMarkerManagerImpl timeMarkerManager;
    public long testTime;

    @Before
    public void setup()
    {
        timeMarkerManager = new TimeMarkerManagerImpl();
        testTime = System.currentTimeMillis();
    }

    @Test
    public void testCanNotAddMoreTimemarkerWithSameTimestamp()
    {
        timeMarkerManager.createNewTimeMarker( testTime );
        timeMarkerManager.createNewTimeMarker( testTime );

        assertEquals( 1, timeMarkerManager.getAllTimeMarkers().size() );
    }

    @Test
    public void testTimemarkerSortSet()
    {
        TimeMarker secondMarker = timeMarkerManager.createNewTimeMarker( testTime );
        TimeMarker firstMarker = timeMarkerManager.createNewTimeMarker( testTime - 1000 );
        TimeMarker forthMarker = timeMarkerManager.createNewTimeMarker( testTime + 2000 );
        TimeMarker thirdMarker = timeMarkerManager.createNewTimeMarker( testTime + 1000 );

        Iterator<TimeMarker> iterator = timeMarkerManager.getAllTimeMarkers().iterator();

        assertEquals( firstMarker, iterator.next() );
        assertEquals( secondMarker, iterator.next() );
        assertEquals( thirdMarker, iterator.next() );
        assertEquals( forthMarker, iterator.next() );

    }

    @Test
    public void testGetAllMarkersBetweenTimestamp()
    {
        TimeMarker firstMarker = timeMarkerManager.createNewTimeMarker( testTime );
        timeMarkerManager.createNewTimeMarker( testTime - 1000 );
        timeMarkerManager.createNewTimeMarker( testTime + 2000 );
        TimeMarker secondMarker = timeMarkerManager.createNewTimeMarker( testTime + 1000 );

        SortedSet<TimeMarker> allMarkersBetweenTimestamp = timeMarkerManager
                .getAllTimeMarkersBetweenTimestamp( testTime, testTime + 1000 );
        Iterator<TimeMarker> iterator = allMarkersBetweenTimestamp.iterator();

        assertEquals( firstMarker, iterator.next() );
        assertEquals( secondMarker, iterator.next() );
    }

    @SuppressWarnings("unused")
    @Test
    public void testRemoveTimemarker() throws Exception
    {
        TimeMarker timeMarker1 = timeMarkerManager.createNewTimeMarker( testTime + 1000 );
        TimeMarker timeMarker2 = timeMarkerManager.createNewTimeMarker( testTime );

        timeMarkerManager.removeTimeMarker( timeMarker1 );

        SortedSet<TimeMarker> allTimeMarkers = timeMarkerManager.getAllTimeMarkers();
        assertEquals( 1, allTimeMarkers.size() );
        assertEquals( testTime, allTimeMarkers.iterator().next().getTimestamp() );
    }
}
