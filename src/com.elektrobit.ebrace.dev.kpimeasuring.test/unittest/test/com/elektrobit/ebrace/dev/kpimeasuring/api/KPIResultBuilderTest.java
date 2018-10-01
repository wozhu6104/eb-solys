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

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultBuilder;

import junit.framework.Assert;

public class KPIResultBuilderTest
{
    private KPIResultBuilder kpiResultBuilder;

    @Before
    public void setup()
    {
        kpiResultBuilder = new KPIResultBuilder();
    }

    @Test
    public void checkMetaData() throws Exception
    {

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY, 16 );
        calendar.set( Calendar.MINUTE, 04 );
        calendar.set( Calendar.SECOND, 48 );
        calendar.set( Calendar.YEAR, 2015 );
        calendar.set( Calendar.MONTH, 9 );
        calendar.set( Calendar.DAY_OF_MONTH, 21 );

        kpiResultBuilder.addDate( calendar.getTime() ).addMetaData( "test_person", "Hans im Gl�ck" )
                .addMetaData( "test_case", "TEST1" ).addMetaData( "sw_version", "1.00" );

        KPIResult kpiResult = kpiResultBuilder.build();

        Assert.assertEquals( "2015-10-21 16:04:48", kpiResult.getMetaDataValue( "date" ) );
        Assert.assertEquals( "Hans im Gl�ck", kpiResult.getMetaDataValue( "test_person" ) );
        Assert.assertEquals( "TEST1", kpiResult.getMetaDataValue( "test_case" ) );
        Assert.assertEquals( "1.00", kpiResult.getMetaDataValue( "sw_version" ) );
    }

    @Test
    public void checkErrorLogs() throws Exception
    {
        kpiResultBuilder.addErrorMessage( "Failure message." );

        KPIResult kpiResult = kpiResultBuilder.build();

        Assert.assertEquals( "Failure message.", kpiResult.getErrorMessages().get( 0 ) );
    }

    @Test
    public void checkMeasuredItems() throws Exception
    {
        kpiResultBuilder.addMeasuredItem( "time_to_nav_fully_operable", "30000", "ms" );

        KPIResult kpiResult = kpiResultBuilder.build();

        Assert.assertEquals( "time_to_nav_fully_operable", kpiResult.getMeasuredItems().get( 0 ).getName() );
        Assert.assertEquals( "30000", kpiResult.getMeasuredItems().get( 0 ).getValue() );
        Assert.assertEquals( "ms", kpiResult.getMeasuredItems().get( 0 ).getUnit() );
    }

    @Test
    public void resetTest() throws Exception
    {
        kpiResultBuilder.addMeasuredItem( "time_to_nav_fully_operable", "30000", "ms" );
        kpiResultBuilder.reset();

        KPIResult result = kpiResultBuilder.addMetaData( "Key", "Val" ).build();

        Assert.assertTrue( "KPIResult shouldn't contain any measured item after reset.",
                           result.getMeasuredItems().isEmpty() );
        Assert.assertTrue( "KPIResult shouldn't contain error message item after reset.",
                           result.getErrorMessages().isEmpty() );
        Assert.assertTrue( "KPIResult should contain one meta-data item after reset.",
                           result.getMetaDataItems().size() == 1 );
    }
}
