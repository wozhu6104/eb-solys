/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api;

import java.util.List;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;

public interface WriteProcessRegistryIF extends ReadProcessRegistryIF
{
    void setProcessInfoOfActiveProcesses(List<ProcessInfo> processList, DataSourceContext dataSourceContext);
}
