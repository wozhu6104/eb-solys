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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.UnitConverter;

public class UnitConverterTest
{
    @Test
    public void convertBytesToKBTest()
    {
        assertEquals( 2, UnitConverter.convertBytesToKB( 2047 ) );
    }

    @Test
    public void convertBytesToMBTest()
    {
        assertEquals( 2, UnitConverter.convertBytesToMB( 2097151 ) );
    }

    @Test
    public void convertBytesToGBTest()
    {
        assertEquals( 1, UnitConverter.convertBytesToGB( 1073741823 ) );
    }

    @Test
    public void convertMBtoBytes()
    {
        assertEquals( 52428800, UnitConverter.convertMBToBytes( 50 ) );
    }

}
