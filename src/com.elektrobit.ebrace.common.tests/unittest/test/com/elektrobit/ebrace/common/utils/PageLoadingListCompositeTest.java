/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.ListDelegator;

public class PageLoadingListCompositeTest
{
    @Test
    public void simpleConcatenationTest()
    {
        ListDelegator pageLoadingListComposite = new ListDelegator( Arrays.asList( 0, 1, 2, 3 ),
                                                                    Arrays.asList( 4, 5 ),
                                                                    Arrays.asList( 6, 7 ) );
        for (int i = 0; i < pageLoadingListComposite.size(); i++)
        {
            Assert.assertEquals( i, pageLoadingListComposite.get( i ) );
        }
    }
}
