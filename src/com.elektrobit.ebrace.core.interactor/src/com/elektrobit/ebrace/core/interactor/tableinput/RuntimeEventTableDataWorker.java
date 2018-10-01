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

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;

public class RuntimeEventTableDataWorker implements Runnable
{
    private final String searchText;
    private final SEARCH_MODE searchMode;
    final RuntimeEventTableDataNotifyUseCaseImpl useCase;
    private final RaceScriptMethod filterMethod;

    public RuntimeEventTableDataWorker(String searchText, RaceScriptMethod filterMethod, SEARCH_MODE searchMode,
            RuntimeEventTableDataNotifyUseCaseImpl useCase)
    {
        this.searchText = new String( searchText );
        this.filterMethod = filterMethod;
        this.searchMode = searchMode;
        this.useCase = useCase;
    }

    @Override
    public void run()
    {
        try
        {
            work();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            useCase.onWorkerRunnableDone();
        }
    }

    private void work()
    {
        useCase.collectAndPostData( searchText, filterMethod, searchMode );
    }
}
