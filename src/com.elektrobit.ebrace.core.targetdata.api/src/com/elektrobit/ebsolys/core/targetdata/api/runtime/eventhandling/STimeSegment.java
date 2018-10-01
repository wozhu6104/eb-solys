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

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public interface STimeSegment
{
    public RuntimeEvent<?> getStartEvent();

    public RuntimeEvent<?> getEndEvent();

    public String getLabel();

    public long getStartTime();

    public long getEndTime();

    public long getDuration();

    public void setClickAction(STimeSegmentClickAction action);

    public STimeSegmentClickAction getClickAction();

    public SColor getColor();

    public RuntimeEventChannel<STimeSegment> getChannel();
}
