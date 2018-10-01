/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.channelValues.comparator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventBooleanComparator;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;

public class DecodedRuntimeEventBooleanComparatorTest
{

    private final DecodedRuntimeEventBooleanComparator underTest = new DecodedRuntimeEventBooleanComparator();

    @Test
    public void callCompareWithSameEventForFalse()
    {
        DecodedRuntimeEvent event = new DecodedRuntimeEventMock( false );
        int result = underTest.compare( event, event );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithFalseFalse()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( false );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( false );
        int result = underTest.compare( event1, event2 );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithSameEventForTrue()
    {
        DecodedRuntimeEvent event = new DecodedRuntimeEventMock( true );
        int result = underTest.compare( event, event );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithTrueTrue()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( true );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( true );
        int result = underTest.compare( event1, event2 );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithFalseTrue()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( false );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( true );
        int result = underTest.compare( event1, event2 );
        assertEquals( -1, result );
    }

    @Test
    public void callCompareWithTrueFalse()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( true );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( false );
        int result = underTest.compare( event1, event2 );
        assertEquals( 1, result );
    }

}
