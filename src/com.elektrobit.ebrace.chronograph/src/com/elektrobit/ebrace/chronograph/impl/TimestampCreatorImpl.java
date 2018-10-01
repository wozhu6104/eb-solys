/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.chronograph.impl;

import com.elektrobit.ebrace.chronograph.api.TimestampMode;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

public class TimestampCreatorImpl implements TimestampCreator
{

    private final TimestampMode timestampMode;
    private final TargetTimebase targetTimebase;

    public TimestampCreatorImpl(TimestampMode timestampMode, TargetTimebase targetTimebase)
    {
        this.timestampMode = timestampMode;
        this.targetTimebase = targetTimebase;
    }

    @Override
    public Timestamp create(long time)
    {
        return new TimestampImpl( timestampMode, targetTimebase, time );
    }
}
