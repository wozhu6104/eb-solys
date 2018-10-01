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

import com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventNumberComparator;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;

public class DecodedRuntimeEventNumberComparatorTest
{

    private final DecodedRuntimeEventNumberComparator underTest = new DecodedRuntimeEventNumberComparator();

    @Test
    public void callCompareWithSameEventForSomeNumber()
    {
        DecodedRuntimeEvent event = new DecodedRuntimeEventMock( 1234 );
        int result = underTest.compare( event, event );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithSomeNumber()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( 1234 );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( 1234 );
        int result = underTest.compare( event1, event2 );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithFirstSmallerNumber()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( 1234 );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( 1235 );
        int result = underTest.compare( event1, event2 );
        assertEquals( -1, result );
    }

    @Test
    public void callCompareWithFirstGreaterNumber()
    {
        DecodedRuntimeEvent event1 = new DecodedRuntimeEventMock( 1235 );
        DecodedRuntimeEvent event2 = new DecodedRuntimeEventMock( 1234 );
        int result = underTest.compare( event1, event2 );
        assertEquals( 1, result );
    }

}
