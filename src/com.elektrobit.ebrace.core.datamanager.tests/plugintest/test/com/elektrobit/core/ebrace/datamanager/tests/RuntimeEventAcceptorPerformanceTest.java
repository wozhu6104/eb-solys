/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.profiling.PerformanceUtils;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import junit.framework.Assert;

public class RuntimeEventAcceptorPerformanceTest
{
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN_OF_CHANNEL = "RuntimeEventAcceptor get events in timespan of channel";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN = "RuntimeEventAcceptor get events in timespan";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNEL = "RuntimeEventAcceptor get events of channel";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_ALL_EVENTS = "RuntimeEventAcceptor get all events";
    private static final String RUNTIME_EVENT_ACCEPTOR_CREATE_EVENTS = "RuntimeEventAcceptor CreateEvents";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_LAST_EVENT_IN_TIMESPAN_OF_CHANNEL = "RuntimeEventAcceptor get last event in timespan of channel";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_FIRST_EVENT_IN_TIMESPAN_OF_CHANNEL = "RuntimeEventAcceptor get first event in timespan of channel";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNELS = "RuntimeEventAcceptor get events of channels";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_FIRST_RUNTIME_EVENT = "RuntimeEventAcceptor get first runtime event";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_LAST_RUNTIME_EVENT = "RuntimeEventAcceptor get last runtime event";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENT = "RuntimeEventAcceptor get events of model element";
    private static final String RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENTS = "RuntimeEventAcceptor get events of model elements";

    private static final int BUILDSERVER_SPEED_FACTOR = 10;

    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Before
    public void setup()
    {
        runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
    }

    @Test
    public void performanceMeasureTest() throws Exception
    {
        generateDataAndMeasure();
        evaluateMeasurements();
    }

    private void generateDataAndMeasure()
    {
        RuntimeEventChannel<Long> runtimeEventChannel = runtimeEventAcceptor
                .createRuntimeEventChannel( "MyChannel", Unit.COUNT, "" );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_CREATE_EVENTS );
        for (long i = 0; i < 3000000; i++)
        {
            runtimeEventAcceptor.acceptEvent( i, runtimeEventChannel, ModelElement.NULL_MODEL_ELEMENT, i );
            if (i % 200000 == 0)
            {
                logMemory();
            }
        }
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_CREATE_EVENTS );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_CREATE_EVENTS );

        // start GC now so that it will not run during measurements
        System.gc();

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_ALL_EVENTS );
        runtimeEventAcceptor.getAllRuntimeEvents().get( 2999999 );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_ALL_EVENTS );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_ALL_EVENTS );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNEL );
        runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( runtimeEventChannel );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNEL );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNEL );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN );
        runtimeEventAcceptor.getRuntimeEventsOfTimespan( 2000000, 2100000 );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN );

        List<RuntimeEventChannel<?>> channelList = new ArrayList<RuntimeEventChannel<?>>();
        channelList.add( runtimeEventChannel );
        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN_OF_CHANNEL );
        runtimeEventAcceptor.getRuntimeEventForTimeStampIntervalForChannels( 2000000, 2100000, channelList );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN_OF_CHANNEL );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN_OF_CHANNEL );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_LAST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        runtimeEventAcceptor.getLastRuntimeEventForTimeStampInterval( 2000000, 2500000, runtimeEventChannel );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_LAST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_LAST_EVENT_IN_TIMESPAN_OF_CHANNEL );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        runtimeEventAcceptor.getFirstRuntimeEventForTimeStampInterval( 2000000, 2500000, runtimeEventChannel );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_EVENT_IN_TIMESPAN_OF_CHANNEL );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNELS );
        runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannels( channelList );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNELS );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNELS );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_RUNTIME_EVENT );
        runtimeEventAcceptor.getFirstRuntimeEvent();
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_RUNTIME_EVENT );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_RUNTIME_EVENT );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_LAST_RUNTIME_EVENT );
        runtimeEventAcceptor.getLatestRuntimeEvent();
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_LAST_RUNTIME_EVENT );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_LAST_RUNTIME_EVENT );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENT );
        runtimeEventAcceptor.getRuntimeEventsOfModelElement( ModelElement.NULL_MODEL_ELEMENT );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENT );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENT );

        PerformanceUtils.startMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENTS );
        runtimeEventAcceptor.getRuntimeEventsOfModelElements( Arrays.asList( ModelElement.NULL_MODEL_ELEMENT ) );
        PerformanceUtils.stopMeasure( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENTS );
        PerformanceUtils.printTimingResult( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENTS );
    }

    private void evaluateMeasurements()
    {
        long createEventsTime = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_CREATE_EVENTS );
        Assert.assertTrue( "Create events too slow " + createEventsTime + " ms",
                           createEventsTime < 3000 * BUILDSERVER_SPEED_FACTOR );

        long getAllEventsTime = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_ALL_EVENTS );
        Assert.assertTrue( "Get all events too slow " + getAllEventsTime + " ms",
                           getAllEventsTime < 50 * BUILDSERVER_SPEED_FACTOR );

        long getEventsOfChannel = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNEL );
        Assert.assertTrue( "Get events of channel too slow " + getEventsOfChannel + " ms",
                           getEventsOfChannel < 120 * BUILDSERVER_SPEED_FACTOR );

        long getEventsInTimespan = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN );
        Assert.assertTrue( "Get events in timespan too slow " + getEventsInTimespan + " ms",
                           getEventsInTimespan < 120 * BUILDSERVER_SPEED_FACTOR );

        long getEventsInTimespanOfChannel = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_IN_TIMESPAN_OF_CHANNEL );
        Assert.assertTrue( "Get events in timespan of channel too slow " + getEventsInTimespanOfChannel + " ms",
                           getEventsInTimespanOfChannel < 120 * BUILDSERVER_SPEED_FACTOR );

        long getLastEventInTimespanOfChannel = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_LAST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        Assert.assertTrue( "Get last event in timespan of channel too slow " + getLastEventInTimespanOfChannel + " ms",
                           getLastEventInTimespanOfChannel < 100 * BUILDSERVER_SPEED_FACTOR );

        long getFirstEventInTimespanOfChannel = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_EVENT_IN_TIMESPAN_OF_CHANNEL );
        Assert.assertTrue( "Get events first event in timespan of channel too slow " + getFirstEventInTimespanOfChannel
                + " ms", getFirstEventInTimespanOfChannel < 100 * BUILDSERVER_SPEED_FACTOR );

        long getEventsOfChannels = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_CHANNELS );
        Assert.assertTrue( "Get events of channels too slow " + getEventsOfChannels + " ms",
                           getEventsOfChannels < 200 * BUILDSERVER_SPEED_FACTOR );

        long getFirstRuntimeEvent = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_FIRST_RUNTIME_EVENT );
        Assert.assertTrue( "Get first event too slow " + getFirstRuntimeEvent + " ms",
                           getFirstRuntimeEvent < 40 * BUILDSERVER_SPEED_FACTOR );

        long getLastRuntimeEvent = PerformanceUtils.getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_LAST_RUNTIME_EVENT );
        Assert.assertTrue( "Get last event too slow " + getLastRuntimeEvent + " ms",
                           getLastRuntimeEvent < 40 * BUILDSERVER_SPEED_FACTOR );

        long getEventOfModelElement = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENT );
        Assert.assertTrue( "Get last event too slow " + getEventOfModelElement + " ms",
                           getEventOfModelElement < 300 * BUILDSERVER_SPEED_FACTOR );

        long getEventOfModelElements = PerformanceUtils
                .getMeasuredTimeMs( RUNTIME_EVENT_ACCEPTOR_GET_EVENTS_OF_MODEL_ELEMENTS );
        Assert.assertTrue( "Get last event too slow " + getEventOfModelElements + " ms",
                           getEventOfModelElements < 300 * BUILDSERVER_SPEED_FACTOR );
    }

    private void logMemory()
    {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        double percent = ((double)freeMemory) / totalMemory;
        percent *= 100.0;
        System.out.println( "Memory " + String.format( "%.2f", percent ) + "% Total " + totalMemory + " Free "
                + freeMemory );
    }

    @After
    public void cleanup()
    {
        runtimeEventAcceptor.dispose();
    }
}
