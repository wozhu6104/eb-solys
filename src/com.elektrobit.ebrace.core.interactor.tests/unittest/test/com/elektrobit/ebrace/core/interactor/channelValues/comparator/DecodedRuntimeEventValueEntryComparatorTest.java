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

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventValueEntryComparator;

public class DecodedRuntimeEventValueEntryComparatorTest
{

    private final DecodedRuntimeEventValueEntryComparator underTest = new DecodedRuntimeEventValueEntryComparator();

    @Test(expected = IllegalArgumentException.class)
    public void callCompareWithNulls()
    {
        underTest.compare( null, null );
    }

}
