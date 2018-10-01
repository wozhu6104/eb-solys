/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DraggedTimeMarker implements TimeMarker
{
    private long timestamp;
    private String name;
    private SColor color;

    @Override
    public int compareTo(TimebasedObject o)
    {
        throw new UnsupportedOperationException( "function not implemented" );
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
