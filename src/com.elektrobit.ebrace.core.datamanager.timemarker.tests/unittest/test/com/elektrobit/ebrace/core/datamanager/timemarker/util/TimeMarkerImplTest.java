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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.timemarker.model.TimeMarkerChangedNotifier;
import com.elektrobit.ebrace.core.datamanager.timemarker.model.TimeMarkerImpl;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerImplTest
{
    long testTime = System.currentTimeMillis();
    String testName = "TIMEMARKER_NAME";
    private TimeMarkerImpl timeMarker;
    private final String otherName = "OTHER_NAME";
    private TimeMarkerChangedNotifier notifier;;

    @Before
    public void setup()
    {
        notifier = Mockito.mock( TimeMarkerChangedNotifier.class );
        timeMarker = new TimeMarkerImpl( testTime, testName, notifier );
    }

    @Test
    public void testTimeMarkerSetName()
    {
        timeMarker.setName( otherName );
        assertEquals( otherName, timeMarker.getName() );
        assertEquals( otherName, timeMarker.toString() );
    }

    @Test
    public void testTimeMarkerSetTimestamp()
    {
        final long newTime = testTime - 1000;
        timeMarker.setTimestamp( newTime );
        assertEquals( newTime, timeMarker.getTimestamp() );

    }

    @Test
    public void testTimeMarkerVisible()
    {
        assertTrue( timeMarker.isEnabled() );
        timeMarker.setEnabled( false );
        assertFalse( timeMarker.isEnabled() );
        timeMarker.setEnabled( true );
        assertTrue( timeMarker.isEnabled() );
    }

    @Test
    public void testTimeMarkerEquals()
    {
        TimeMarker testMarker = new TimeMarkerImpl( testTime, testName, notifier );
        assertTrue( timeMarker.equals( testMarker ) );

        testMarker = new TimeMarkerImpl( testTime, otherName, notifier );
        assertFalse( timeMarker.equals( testMarker ) );

        testMarker = new TimeMarkerImpl( testTime + 1, testName, notifier );
        assertFalse( timeMarker.equals( testMarker ) );

        testMarker = new TimeMarkerImpl( testTime + 1, otherName, notifier );
        assertFalse( timeMarker.equals( testMarker ) );
    }

    @Test
    public void testTimeMarkerCompareTo()
    {
        TimeMarkerImpl testMarker = new TimeMarkerImpl( testTime, testName, notifier );
        assertEquals( 0, testMarker.compareTo( timeMarker ) );

        testMarker.setTimestamp( testTime - 1 );
        assertEquals( -1, testMarker.compareTo( timeMarker ) );

        testMarker.setTimestamp( testTime + 1 );
        assertEquals( 1, testMarker.compareTo( timeMarker ) );
    }
}
