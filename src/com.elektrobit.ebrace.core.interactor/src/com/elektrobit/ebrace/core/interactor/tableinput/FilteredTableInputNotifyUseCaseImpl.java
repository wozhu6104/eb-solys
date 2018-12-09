/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.DataCollector;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableInputNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.interactor.tableinput.filter.FilterUtil;

public class FilteredTableInputNotifyUseCaseImpl implements FilteredTableInputNotifyUseCase
{
    private FilteredTableNotifyCallback callback;
    private final DataCollector dataCollector;
    private String filterText = "";

    private final List<RowFormatter> rowFormatters;

    public FilteredTableInputNotifyUseCaseImpl(FilteredTableNotifyCallback _callback, DataCollector _dataCollector,
            List<RowFormatter> _columnProviderList)
    {
        callback = _callback;
        dataCollector = _dataCollector;
        rowFormatters = _columnProviderList;
    }

    @Override
    public void unregister()
    {
        callback = null;
    }

    @Override
    public void collectAndPostNewData()
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "FilteredTableInputNotifyUseCase.collectAndPostNewData", () -> {
            TableData filterResultData = FilterUtil.filter( dataCollector.collectData(), filterText, rowFormatters );
            postCollectedTableInputToCallBack( filterResultData );
        } ) );

    }

    private void postCollectedTableInputToCallBack(final TableData filterResultData)
    {
        UIExecutor.post( new Runnable()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onInputChanged( (List<Object>)filterResultData.getItemsToBeDisplayed() );
                }
            }
        } );
    }

    @Override
    public void setFilterText(String filterText)
    {
        this.filterText = filterText;
        collectAndPostNewData();
    }
}
