/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoType;

public interface DltChannelFromLogInfoCreator
{
    public void createChannelsForMessage(DltLogInfoType message);
}
