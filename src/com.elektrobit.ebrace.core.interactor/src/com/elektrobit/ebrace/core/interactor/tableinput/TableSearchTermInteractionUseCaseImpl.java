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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

public class TableSearchTermInteractionUseCaseImpl implements TableSearchTermInteractionUseCase
{
    private final PreferencesService preferencesService;
    private final String viewID;

    public TableSearchTermInteractionUseCaseImpl(PreferencesService preferencesService, String viewID)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "preferencesService", preferencesService );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "viewID", viewID );
        this.viewID = viewID;
        this.preferencesService = preferencesService;
    }

    @Override
    public void unregister()
    {
    }

    @Override
    public void addSearchTerm(String newTerm)
    {
        if (newTerm == null || newTerm.isEmpty())
        {
            return;
        }

        List<String> allTerms = preferencesService.getTableSearchTermsHistory( viewID );
        allTerms = new ArrayList<String>( allTerms );
        if (allTerms.contains( newTerm ) && !allTerms.get( 0 ).equals( newTerm ))
        {
            allTerms.remove( newTerm );
        }

        if (!allTerms.contains( newTerm ))
        {
            allTerms.add( 0, newTerm );
        }
        preferencesService.setTableSearchTermsHistory( allTerms, viewID );
    }

    @Override
    public void deleteAllTerms()
    {
        preferencesService.setTableSearchTermsHistory( Collections.<String> emptyList(), viewID );
    }
}
