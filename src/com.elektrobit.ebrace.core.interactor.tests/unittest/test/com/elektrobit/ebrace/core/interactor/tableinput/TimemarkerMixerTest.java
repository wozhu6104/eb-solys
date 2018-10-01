/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.tableinput.TimeMarkerMixer;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMocker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimemarkerMixerTest
{
    private static long SAME_TIMESTAMP = 1004;

    List<TimebasedObject> items;
    SortedSet<TimeMarker> timemarkerList;
    TimeMarkerManager timemarkerManager;

    @Before
    public void setup()
    {
        items = new ArrayList<TimebasedObject>();
        items.add( RuntimeEventMocker.mock( 1000, 0 ) );
        items.add( RuntimeEventMocker.mock( SAME_TIMESTAMP, 0 ) );

        timemarkerList = new TreeSet<TimeMarker>();

        timemarkerManager = Mockito.mock( TimeMarkerManager.class );
        Mockito.when( timemarkerManager.getAllVisibleTimemarkers() ).thenReturn( timemarkerList );

    }

    @Test
    public void mixNoMarkersTest()
    {
        List<TimebasedObject> resultList = TimeMarkerMixer
                .mixAndSortTimemarkers( items,
                                        new ArrayList<TimeMarker>( timemarkerManager.getAllVisibleTimemarkers() ) );
        Assert.assertEquals( items, resultList );
    }

    @Test
    public void mixOneMarkersTest()
    {
        TimeMarker tm1 = Mockito.mock( TimeMarker.class );
        Mockito.when( tm1.getTimestamp() ).thenReturn( (long)999 );

        timemarkerList.add( tm1 );

        List<TimebasedObject> resultList = TimeMarkerMixer
                .mixAndSortTimemarkers( items,
                                        new ArrayList<TimeMarker>( timemarkerManager.getAllVisibleTimemarkers() ) );

        Assert.assertEquals( tm1, resultList.get( 0 ) );
        Assert.assertEquals( items.get( 0 ), resultList.get( 1 ) );
        Assert.assertEquals( items.get( 1 ), resultList.get( 2 ) );
    }

    @Test
    public void mixTimemarkersAndRuntimeEventsWithSameTimestamp() throws Exception
    {

        TimeMarker tm1 = Mockito.mock( TimeMarker.class );
        Mockito.when( tm1.getTimestamp() ).thenReturn( SAME_TIMESTAMP );

        timemarkerList.add( tm1 );

        List<TimebasedObject> resultList = TimeMarkerMixer
                .mixAndSortTimemarkers( items,
                                        new ArrayList<TimeMarker>( timemarkerManager.getAllVisibleTimemarkers() ) );

        Assert.assertEquals( items.get( 0 ), resultList.get( 0 ) );
        Assert.assertEquals( tm1, resultList.get( 1 ) );
        Assert.assertEquals( items.get( 1 ), resultList.get( 2 ) );

    }
}
