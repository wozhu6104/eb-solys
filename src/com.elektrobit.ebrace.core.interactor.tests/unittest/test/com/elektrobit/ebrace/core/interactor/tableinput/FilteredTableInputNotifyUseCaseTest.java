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
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.DataCollector;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableNotifyCallback;
import com.elektrobit.ebrace.core.interactor.tableinput.FilteredTableInputNotifyUseCaseImpl;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class FilteredTableInputNotifyUseCaseTest extends UseCaseBaseTest
{

    private final List<RowFormatter> rowFormatters = new ArrayList<RowFormatter>();
    private FilteredTableInputNotifyUseCaseImpl filteredTableInputNotifyUseCase;

    private DataCollector dataCollector;
    private final List<Object> rawInputData = new ArrayList<Object>();
    private FilterResultCallBack filterResultCallback;

    @Before
    public void setUp()
    {
        rawInputData.add( new EventMock( "ABC", "123" ) );
        rawInputData.add( new EventMock( "123", "DEF" ) );
        rawInputData.add( new EventMock( "DEF", "456" ) );

        dataCollector = Mockito.mock( DataCollector.class );
        Mockito.when( dataCollector.collectData() ).thenReturn( rawInputData );
        rowFormatters.add( new ValueOneRowFormatterMock() );
        rowFormatters.add( new ValueTwoRowFormatterMock() );

        filterResultCallback = new FilterResultCallBack();

        filteredTableInputNotifyUseCase = new FilteredTableInputNotifyUseCaseImpl( filterResultCallback,
                                                                                   dataCollector,
                                                                                   rowFormatters );

    }

    @Test
    public void testSetNoFiler() throws Exception
    {
        filteredTableInputNotifyUseCase.collectAndPostNewData();
        Assert.assertEquals( rawInputData, filterResultCallback.inputList );
    }

    @Test
    public void testSetSimpleFilter() throws Exception
    {
        List<EventMock> expectedResult = Arrays.asList( new EventMock( "ABC", "123" ) );

        filteredTableInputNotifyUseCase.setFilterText( "A" );
        filteredTableInputNotifyUseCase.collectAndPostNewData();

        Assert.assertEquals( expectedResult, filterResultCallback.inputList );
    }

    @Test
    public void testSetAndLinkedFilter() throws Exception
    {
        List<EventMock> expectedResult = Arrays.asList( new EventMock( "ABC", "123" ) );

        filteredTableInputNotifyUseCase.setFilterText( "A 1" );
        filteredTableInputNotifyUseCase.collectAndPostNewData();

        Assert.assertEquals( expectedResult, filterResultCallback.inputList );
    }

    @Test
    public void testNotFoundFilter() throws Exception
    {
        filteredTableInputNotifyUseCase.setFilterText( "A1" );
        filteredTableInputNotifyUseCase.collectAndPostNewData();

        Assert.assertEquals( Collections.EMPTY_LIST, filterResultCallback.inputList );
    }

    private class FilterResultCallBack implements FilteredTableNotifyCallback
    {
        public List<Object> inputList;

        @Override
        public void onInputChanged(List<Object> inputList)
        {
            this.inputList = inputList;
        }
    }
}
