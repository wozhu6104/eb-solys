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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TableDataImpl implements TableData
{
    private List<?> itemsToBeDisplayed = Collections.emptyList(); // always all items that will be shown
    private Map<?, Map<RowFormatter, List<Position>>> searchPositionList = Collections.emptyMap(); // keys - items that
                                                                                                   // match search and
                                                                                                   // should be
                                                                                                   // highlighted
    private List<RuntimeEvent<?>> taggedEvents = Collections.emptyList();

    public TableDataImpl(List<?> itemsToBeDisplayed, Map<?, Map<RowFormatter, List<Position>>> searchPositionList)
    {
        this.itemsToBeDisplayed = itemsToBeDisplayed;
        this.searchPositionList = searchPositionList;
    }

    public TableDataImpl(List<?> itemsToBeDisplayed, Map<?, Map<RowFormatter, List<Position>>> searchPositionList,
            List<RuntimeEvent<?>> taggedEvents)
    {
        this.itemsToBeDisplayed = itemsToBeDisplayed;
        this.searchPositionList = searchPositionList;
        this.taggedEvents = taggedEvents;
    }

    @Override
    public List<?> getItemsToBeDisplayed()
    {
        return itemsToBeDisplayed;
    }

    @Override
    public Map<?, Map<RowFormatter, List<Position>>> getSearchPositionMap()
    {
        return searchPositionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<TimebasedObject> getSearchMatchingItems()
    {
        return (Set<TimebasedObject>)searchPositionList.keySet();
    }

    @Override
    public List<RuntimeEvent<?>> getTaggedEvents()
    {
        return taggedEvents;
    }

    @Override
    public void setTaggedEvents(List<RuntimeEvent<?>> taggedEvents)
    {
        this.taggedEvents = taggedEvents;
    }

}
