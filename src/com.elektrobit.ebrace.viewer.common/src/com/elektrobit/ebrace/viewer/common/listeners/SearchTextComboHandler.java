/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.listeners;

import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilterChangedListener;
import com.elektrobit.ebrace.viewer.common.swt.SearchComboListener;

public class SearchTextComboHandler implements FilterChangedListener, SelectionListener
{
    private final CCombo searchCombo;
    private static String CLEAR_ALL_FILTERS_LABEL = "Clear filters...";
    private final GenericListenerCaller<SearchComboListener> comboListeners = new GenericListenerCaller<SearchComboListener>();

    public SearchTextComboHandler(CCombo dropdown)
    {
        this.searchCombo = dropdown;
        dropdown.addSelectionListener( this );
    }

    @Override
    public void onFilterTextChanged(final String filterText)
    {
        comboListeners.notifyListeners( new Notifier<SearchComboListener>()
        {
            @Override
            public void notify(SearchComboListener listener)
            {
                listener.onTextEntered( filterText );

            }
        } );
    }

    @Override
    public void onSearchModeChanged(SEARCH_MODE searchMode)
    {
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        final int selectionIndex = searchCombo.getSelectionIndex();

        if (lastItemSelected( selectionIndex ))
        {
            searchCombo.setText( "" );
            notifyClearSelected();
        }
        else
        {
            notifyComboItemSelected( selectionIndex );
        }
    }

    private boolean lastItemSelected(int selectionIndex)
    {
        return searchCombo.getItemCount() - 1 == selectionIndex;
    }

    private void notifyClearSelected()
    {
        comboListeners.notifyListeners( new Notifier<SearchComboListener>()
        {

            @Override
            public void notify(SearchComboListener listener)
            {
                listener.onClearComboSelected();
            }
        } );
    }

    private void notifyComboItemSelected(final int selectionIndex)
    {
        comboListeners.notifyListeners( new Notifier<SearchComboListener>()
        {

            @Override
            public void notify(SearchComboListener listener)
            {
                listener.onComboItemSelected( selectionIndex );
            }
        } );
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void setComboContent(List<String> contents)
    {
        contents.add( CLEAR_ALL_FILTERS_LABEL );
        searchCombo.setItems( contents.toArray( new String[contents.size()] ) );
    }

    public void registerComboListener(SearchComboListener listener)
    {
        comboListeners.add( listener );
    }

    public void unregisterComboListener(SearchComboListener listener)
    {
        comboListeners.remove( listener );
    }
}
