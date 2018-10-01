/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDelegator extends AbstractList<Object>
{

    private final Map<Integer, List<?>> listContainer = new HashMap<Integer, List<?>>();
    private final List<Integer> accumulatedListSizes = new ArrayList<Integer>();
    private int completeSize = 0;

    public ListDelegator(List<?>... lists)
    {
        int accumulatedSize = 0;
        for (int i = 0; i < lists.length; i++)
        {
            listContainer.put( i, lists[i] );
            accumulatedSize += lists[i].size();
            accumulatedListSizes.add( accumulatedSize );
        }
        completeSize = accumulatedSize;
    }

    @Override
    public Object get(int index)
    {
        List<?> list = foundListForIndex( index );
        int correctedIndex = correctIndex( index );
        return list.get( correctedIndex );
    }

    private List<?> foundListForIndex(int index)
    {
        List<Integer> listKeys = new ArrayList<Integer>( listContainer.keySet() );
        Collections.sort( listKeys );
        int accumulatedSize = 0;
        for (Integer nextKey : listKeys)
        {
            accumulatedSize += listContainer.get( nextKey ).size();
            if (accumulatedSize > index)
                return listContainer.get( nextKey );
        }

        return null;
    }

    private Integer correctIndex(int index)
    {
        List<Integer> listKeys = new ArrayList<Integer>( listContainer.keySet() );
        Collections.sort( listKeys );
        for (Integer nextKey : listKeys)
        {
            int newIndex = index - listContainer.get( nextKey ).size();
            if (newIndex < 0)
                return index;
            else
                index = newIndex;

        }

        return null;
    }

    @Override
    public int size()
    {
        return completeSize;
    }

}
