/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetRuntimeEventsOfChannelsForTimestampCTest
        extends
            RuntimeEventAcceptorAbstractCTest
{
    private RuntimeEventChannel<Integer> numericChannel1;
    private RuntimeEventChannel<Integer> numericChannel2;
    private RuntimeEventChannel<Integer> numericChannel3;
    private RuntimeEventChannel<Integer> numericChannel4;
    private RuntimeEventChannel<Boolean> booleanChannel;

    public RuntimeEventAcceptorGetRuntimeEventsOfChannelsForTimestampCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Before
    public void createData()
    {
        Unit<Integer> integerUnit = Unit.createCustomUnit( "Integer Test Unit", Integer.class );
        numericChannel1 = runtimeEventAcceptor.createRuntimeEventChannel( "numericChannel1", integerUnit, "" );
        numericChannel2 = runtimeEventAcceptor.createRuntimeEventChannel( "numericChannel2", integerUnit, "" );
        numericChannel3 = runtimeEventAcceptor.createRuntimeEventChannel( "numericChannel3", integerUnit, "" );
        numericChannel4 = runtimeEventAcceptor.createRuntimeEventChannel( "numericChannel4", integerUnit, "" );
        booleanChannel = runtimeEventAcceptor.createRuntimeEventChannel( "booleanChannel", Unit.BOOLEAN, "" );

        runtimeEventAcceptor.acceptEventMicros( 999L, numericChannel1, ModelElement.NULL_MODEL_ELEMENT, 999 );
        runtimeEventAcceptor.acceptEventMicros( 1001L, numericChannel1, ModelElement.NULL_MODEL_ELEMENT, 1001 );
        runtimeEventAcceptor.acceptEventMicros( 1002L, numericChannel1, ModelElement.NULL_MODEL_ELEMENT, 1002 );
        runtimeEventAcceptor.acceptEventMicros( 1003L, numericChannel1, ModelElement.NULL_MODEL_ELEMENT, 1003 );

        runtimeEventAcceptor.acceptEventMicros( 500, numericChannel2, ModelElement.NULL_MODEL_ELEMENT, 500 );
        runtimeEventAcceptor.acceptEventMicros( 2000L, numericChannel2, ModelElement.NULL_MODEL_ELEMENT, 2000 );

        runtimeEventAcceptor.acceptEventMicros( 600, numericChannel3, ModelElement.NULL_MODEL_ELEMENT, 600 );
        runtimeEventAcceptor.acceptEventMicros( 3000L, numericChannel3, ModelElement.NULL_MODEL_ELEMENT, 3000 );

        runtimeEventAcceptor.acceptEventMicros( 1000, booleanChannel, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1003, booleanChannel, ModelElement.NULL_MODEL_ELEMENT, false );

        waitForCommit();
    }

    @Test
    public void testTimestampBeforeFirstValueNumeric() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( numericChannel1 );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 998L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( numericChannel1 );

        Assert.assertEquals( null, resultValue );
    }

    @Test
    public void testTimestampsInsideIntervalNumeric() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( numericChannel1, numericChannel2 );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1000 );

        Assert.assertEquals( 2, result.entrySet().size() );

        RuntimeEvent<?> resultChannel1 = result.get( numericChannel1 );
        RuntimeEvent<?> resultChannel2 = result.get( numericChannel2 );

        Assert.assertEquals( 1001, resultChannel1.getValue() );
        Assert.assertEquals( 2000, resultChannel2.getValue() );
    }

    @Test
    public void testTimestampAfterIntervalNumeric() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( numericChannel1 );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1004L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( numericChannel1 );

        Assert.assertEquals( null, resultValue );
    }

    @Test
    public void testChannelWithoutEvents() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( numericChannel4 );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1003L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( numericChannel1 );

        Assert.assertEquals( null, resultValue );
    }

    @Test
    public void testTimestampBeforeIntervalBoolean() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( booleanChannel );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 999L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( numericChannel1 );

        Assert.assertEquals( null, resultValue );
    }

    @Test
    public void testTimestampInsideIntervalBoolean() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( booleanChannel );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1002L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( booleanChannel );

        Assert.assertEquals( true, resultValue.getValue() );
    }

    @Test
    public void testTimestampAfterIntervalBoolean() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( booleanChannel );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1005L );

        Assert.assertEquals( 1, result.entrySet().size() );

        RuntimeEvent<?> resultValue = result.get( booleanChannel );

        Assert.assertEquals( false, resultValue.getValue() );
    }

    @Test
    public void testMixedChannels() throws Exception
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( booleanChannel, numericChannel1 );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventsOfChannelsForTimestamp( channels, 1002L );

        Assert.assertEquals( 2, result.entrySet().size() );

        RuntimeEvent<?> numericValue = result.get( numericChannel1 );
        RuntimeEvent<?> booleanValue = result.get( booleanChannel );

        Assert.assertEquals( 1002, numericValue.getValue() );
        Assert.assertEquals( true, booleanValue.getValue() );
    }

}
