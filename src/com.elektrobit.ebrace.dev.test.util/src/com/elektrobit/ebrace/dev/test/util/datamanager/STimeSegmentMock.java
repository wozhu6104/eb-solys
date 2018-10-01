/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.datamanager;

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegmentClickAction;

import lombok.Data;

@Data
public class STimeSegmentMock implements STimeSegment
{
    private final long startTime;
    private final long endTime;
    private final String label;
    private final SColor color;
    private final RuntimeEventChannel<STimeSegment> channel;

    @Override
    public RuntimeEvent<?> getStartEvent()
    {
        throw new UnsupportedOperationException( "Function not implemented" );
    }

    @Override
    public RuntimeEvent<?> getEndEvent()
    {
        throw new UnsupportedOperationException( "Function not implemented" );
    }

    @Override
    public long getDuration()
    {
        throw new UnsupportedOperationException( "Function not implemented" );
    }

    @Override
    public void setClickAction(STimeSegmentClickAction action)
    {
    }

    @Override
    public STimeSegmentClickAction getClickAction()
    {
        return null;
    }
}
