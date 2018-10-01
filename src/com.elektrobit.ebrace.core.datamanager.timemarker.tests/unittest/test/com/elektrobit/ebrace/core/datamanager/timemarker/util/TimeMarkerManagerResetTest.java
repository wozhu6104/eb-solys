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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.datamanager.timemarker.model.TimeMarkerManagerImpl;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class TimeMarkerManagerResetTest
{
    private TimeMarkerManagerImpl timeMarkerManagerImpl;

    @Before
    public void setup() throws Exception
    {
        timeMarkerManagerImpl = new TimeMarkerManagerImpl();
        timeMarkerManagerImpl.createNewTimeMarker( 1000 );

    }

    @Test
    public void timeMarkersRemovedAfterReset() throws Exception
    {
        timeMarkerManagerImpl.onReset();

        Assert.assertTrue( "Expecting no timemarkers after reset.",
                           timeMarkerManagerImpl.getAllTimeMarkers().isEmpty() );
    }

    @Test
    public void listenerNotifiedAfterReset() throws Exception
    {
        TimeMarkersChangedListener timeMarkersChangedListener = Mockito.mock( TimeMarkersChangedListener.class );
        timeMarkerManagerImpl.registerListener( timeMarkersChangedListener );

        timeMarkerManagerImpl.onReset();

        Mockito.verify( timeMarkersChangedListener ).allTimeMarkersRemoved();
    }

    @Test
    public void listenerNotNotifiedAfterResetAfterUnregistration() throws Exception
    {
        TimeMarkersChangedListener timeMarkersChangedListener = Mockito.mock( TimeMarkersChangedListener.class );
        timeMarkerManagerImpl.registerListener( timeMarkersChangedListener );
        timeMarkerManagerImpl.unregisterListener( timeMarkersChangedListener );

        timeMarkerManagerImpl.onReset();

        Mockito.verifyNoMoreInteractions( timeMarkersChangedListener );
    }
}
