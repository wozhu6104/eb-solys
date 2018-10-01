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

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class CreateTimeSegmentChannelTest
{
    private TimeSegmentAcceptorService timeSegmentManager;

    @Before
    public void setup()
    {
        timeSegmentManager = new TimeSegmentAcceptorTestHelper().createTimeSegmentAcceptorWithMockedChannelManager();
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnChannelNameNull() throws Exception
    {
        timeSegmentManager.createTimeSegmentChannel( null, "My description" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnCreateWithChannelNameEmpty() throws Exception
    {
        timeSegmentManager.createTimeSegmentChannel( "", "My description" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnCreateWithChannelDescriptionNull() throws Exception
    {
        timeSegmentManager.createTimeSegmentChannel( "MyTimeSegments", null );
    }

    @Test
    public void createOrGetChannelNotNull() throws Exception
    {
        RuntimeEventChannel<STimeSegment> timeSegmentChannel1 = timeSegmentManager
                .createOrGetTimeSegmentChannel( "MyTimeSegments", "" );
        RuntimeEventChannel<STimeSegment> timeSegmentChannel2 = timeSegmentManager
                .createOrGetTimeSegmentChannel( "MyTimeSegments", "" );
        assertNotNull( timeSegmentChannel1 );
        assertNotNull( timeSegmentChannel2 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnCreateOrGetWithChannelNameEmpty() throws Exception
    {
        timeSegmentManager.createOrGetTimeSegmentChannel( "", "My description" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionOnCreateOrGetWithChannelDescriptionNull() throws Exception
    {
        timeSegmentManager.createOrGetTimeSegmentChannel( "MyTimeSegments", null );
    }
}
