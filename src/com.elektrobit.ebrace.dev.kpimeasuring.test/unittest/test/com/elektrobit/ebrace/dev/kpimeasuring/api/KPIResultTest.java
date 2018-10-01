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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultMeasuredItem;

public class KPIResultTest
{
    private KPIResult kpiResult;

    @Before
    public void setup()
    {
        kpiResult = new KPIResult();
    }

    @Test
    public void addMetaDataTest() throws Exception
    {
        kpiResult.addMetaData( "MyKey", "MyValue" );

        List<String> metaDataItems = kpiResult.getMetaDataItems();

        Assert.assertEquals( 1, metaDataItems.size() );
    }

    @Test
    public void addErrorLogTest() throws Exception
    {
        kpiResult.addErrorMessage( "Failed" );

        List<String> errorMessages = kpiResult.getErrorMessages();

        Assert.assertEquals( 1, errorMessages.size() );
    }

    @Test
    public void addMeasuredItemsTest() throws Exception
    {
        kpiResult.addMeasuredItem( new KPIResultMeasuredItem( "MyName", "MyValue", "MyUnit" ) );

        List<KPIResultMeasuredItem> errorMessages = kpiResult.getMeasuredItems();

        Assert.assertEquals( 1, errorMessages.size() );
    }

}
