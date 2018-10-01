/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.tableinput;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public interface TableData
{
    List<?> getItemsToBeDisplayed();

    Map<?, Map<RowFormatter, List<Position>>> getSearchPositionMap();

    Set<TimebasedObject> getSearchMatchingItems();

    List<RuntimeEvent<?>> getTaggedEvents();

    void setTaggedEvents(List<RuntimeEvent<?>> taggedEvents);
}
