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

import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public interface RuntimeEventTableDataNotifyCallback
{
    public void onFilteringStarted();

    public void onTableInputCollected(TableData filterResultData, boolean jumpToTableEnd);

    public void onTimeMarkerRenamed(TimeMarker timeMarker);

    public void onJumpToTimeMarker(TimeMarker timeMarker);
}
