/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.adapter;

public interface Timestamp
{

    @Deprecated
    public long getRelativeRaceTimeInMillies();

    @Deprecated
    public long getAbsoluteRaceTimeInMillis();

    @Deprecated
    public long getRelativeTargetTimeInMillies();

    @Deprecated
    public long getAbsoluteTargetTimeInMillies();

    public long getTimeInMillis();
}
