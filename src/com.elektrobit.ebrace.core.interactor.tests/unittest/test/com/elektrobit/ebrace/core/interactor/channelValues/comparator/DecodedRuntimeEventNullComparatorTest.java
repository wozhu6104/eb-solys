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

import com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventNullComparator;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;

public class DecodedRuntimeEventNullComparatorTest
{

    private final DecodedRuntimeEventNullComparator underTest = new DecodedRuntimeEventNullComparator();

    @Test
    public void callCompareWithNulls()
    {
        int result = underTest.compare( null, null );
        assertEquals( 0, result );
    }

    @Test
    public void callCompareWithFirstNull()
    {
        DecodedRuntimeEvent event = new DecodedRuntimeEventMock();
        int result = underTest.compare( null, event );
        assertEquals( -1, result );
    }

    @Test
    public void callCompareWithSecondNull()
    {
        DecodedRuntimeEvent event = new DecodedRuntimeEventMock();
        int result = underTest.compare( event, null );
        assertEquals( 1, result );
    }

}
