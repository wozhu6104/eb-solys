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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.timesegmentmanager.impl.TimeSegmentAcceptorServiceImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.RuntimeEventMockHelper;
import test.com.elektrobit.ebrace.core.timesegmentmanager.impl.helper.TimeSegmentAcceptorTestHelper;

public class RemoveTimeSegmentChannelTest
{

    private TimeSegmentAcceptorServiceImpl timeSegmentManager;
    private RuntimeEventChannel<STimeSegment> timeSegmentChannel;
    private RuntimeEventChannel<STimeSegment> timeSegmentChannel2;

    @Before
    public void setup()
    {
        TimeSegmentAcceptorTestHelper timeSegmentAcceptorTestHelper = new TimeSegmentAcceptorTestHelper();
        timeSegmentManager = (TimeSegmentAcceptorServiceImpl)timeSegmentAcceptorTestHelper
                .createTimeSegmentAcceptorWithMockedChannelManager();
    }

    @SuppressWarnings("unchecked")
    private void createData()
    {
        timeSegmentChannel = Mockito.mock( RuntimeEventChannel.class );
        timeSegmentChannel2 = Mockito.mock( RuntimeEventChannel.class );
        RuntimeEvent<?> startEvent = RuntimeEventMockHelper.createRuntimeEvent( 1000L );
        RuntimeEvent<?> endEvent = RuntimeEventMockHelper.createRuntimeEvent( 2000L );
        timeSegmentManager.add( timeSegmentChannel, startEvent, endEvent );
        timeSegmentManager.add( timeSegmentChannel2, startEvent, endEvent );
    }

    @Test
    public void timesegmentChannelRemoved() throws Exception
    {
        createData();
        timeSegmentManager.removeTimeSegmentsWithChannel( timeSegmentChannel );

        assertEquals( 0, timeSegmentManager.getTimeSegments( timeSegmentChannel ).size() );
        assertEquals( 1, timeSegmentManager.getTimeSegments( timeSegmentChannel2 ).size() );
    }

}
