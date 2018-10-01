/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.services;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DecodedJsonEvent implements DecodedRuntimeEvent
{
    private final String summary;
    private final DecodedTree tree;
    private final RuntimeEvent<?> event;

    public DecodedJsonEvent(DecodedTree tree, String summary, final RuntimeEvent<?> event)
    {
        this.tree = tree;
        this.summary = summary;
        this.event = event;
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        return tree;
    }

    @Override
    public String getSummary()
    {
        return summary;
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return RuntimeEventType.UNDEFINED;
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return event.getRuntimeEventChannel();
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return event.getValue();
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return event;
    }
}
