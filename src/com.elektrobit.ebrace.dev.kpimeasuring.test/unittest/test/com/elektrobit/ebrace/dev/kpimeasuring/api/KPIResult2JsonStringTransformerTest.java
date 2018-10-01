/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.dev.kpimeasuring.api;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult2JsonStringTransformer;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultMeasuredItem;

public class KPIResult2JsonStringTransformerTest
{
    // @formatter:off
    private static final String expectedResult = "" 
        + "{"
            + "\"metaDataItems\":{"
                + "\"MetaKey\":\"MetaValue\""
            + "},"
            + "\"errorMessages\":["
                + "\"Failure\""
            + "],"
            + "\"measuredItems\":["
                + "{\"name\":\"MeasureName\","
                + "\"value\":\"MeasureValue\","
                + "\"unit\":\"MeasureUnit\""
                + "}"
            + "]"
        + "}";
    // @formatter:on

    @Test
    public void transformationTest() throws Exception
    {
        KPIResult kpiResult = new KPIResult();
        kpiResult.addMetaData( "MetaKey", "MetaValue" );
        kpiResult.addErrorMessage( "Failure" );
        kpiResult.addMeasuredItem( new KPIResultMeasuredItem( "MeasureName", "MeasureValue", "MeasureUnit" ) );

        String result = KPIResult2JsonStringTransformer.transform( kpiResult );

        Assert.assertEquals( removeWhiteSpaces( expectedResult ), removeWhiteSpaces( result ) );
    }

    private String removeWhiteSpaces(String input)
    {
        return input.replaceAll( "\\s+", "" );
    }
}
