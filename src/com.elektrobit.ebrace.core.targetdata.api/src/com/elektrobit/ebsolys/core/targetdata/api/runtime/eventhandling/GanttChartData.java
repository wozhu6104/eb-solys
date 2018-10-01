/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import java.util.List;

public interface GanttChartData
{
    public List<RuntimeEventChannel<?>> getChannels();

    public List<GanttChartEntry[]> getData();
}
