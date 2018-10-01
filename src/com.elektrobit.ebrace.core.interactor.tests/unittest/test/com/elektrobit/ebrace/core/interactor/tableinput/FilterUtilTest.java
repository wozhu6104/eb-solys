/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.tableinput.filter.FilterUtil;

public class FilterUtilTest
{
    private final List<RowFormatter> rowFormatters = new ArrayList<RowFormatter>();
    private final List<Object> rawInputData = new ArrayList<Object>();

    @Before
    public void setUp()
    {
        rawInputData.add( new EventMock( "ABC", "123" ) );
        rawInputData.add( new EventMock( "123", "DEF" ) );
        rawInputData.add( new EventMock( "DEF", "456" ) );

        rawInputData.add( new EventMock( "BL", "345" ) );
        rawInputData.add( new EventMock( "GHI", "345 789" ) );

        rowFormatters.add( new ValueOneRowFormatterMock() );
        rowFormatters.add( new ValueTwoRowFormatterMock() );
    }

    @Test
    public void testFilterUtilWithNullFilter()
    {
        String noFilter = null;
        TableData filteredResult = FilterUtil.filter( rawInputData, noFilter, rowFormatters );

        Assert.assertEquals( rawInputData, filteredResult.getItemsToBeDisplayed() );
    }

    @Test
    public void testFilterUtilWithEmptyStringFilter()
    {
        String noFilter = "";
        TableData filteredResult = FilterUtil.filter( rawInputData, noFilter, rowFormatters );

        Assert.assertEquals( rawInputData, filteredResult.getItemsToBeDisplayed() );
    }

    @Test
    public void testFilterSimple()
    {
        String filter = "A";

        TableData filteredResult = FilterUtil.filter( rawInputData, filter, rowFormatters );

        List<EventMock> expectedResult = Arrays.asList( new EventMock( "ABC", "123" ) );
        Assert.assertEquals( expectedResult, filteredResult.getItemsToBeDisplayed() );

    }

    @Test
    public void testFilterWithExcludedWord()
    {
        String filter = "345     -         789";
        TableData filteredResult = FilterUtil.filter( rawInputData, filter, rowFormatters );

        List<EventMock> expectedResult = Arrays.asList( new EventMock( "BL", "345" ) );
        Assert.assertEquals( expectedResult, filteredResult.getItemsToBeDisplayed() );
    }
}
