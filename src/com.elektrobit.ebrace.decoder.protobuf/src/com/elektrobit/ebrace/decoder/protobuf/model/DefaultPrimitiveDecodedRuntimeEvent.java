/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.model;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = {"formatter"})
public class DefaultPrimitiveDecodedRuntimeEvent implements DecodedRuntimeEvent
{
    private final RuntimeEvent<?> selectedRuntimeEvent;
    private TimeFormatter formatter;

    public DefaultPrimitiveDecodedRuntimeEvent(RuntimeEvent<?> selectedEvent, String timestampFormat)
    {
        this.selectedRuntimeEvent = selectedEvent;
        changeTimestampFormat( timestampFormat );
    }

    private void changeTimestampFormat(String timestampFormat)
    {
        this.formatter = new TimeFormatter( timestampFormat );
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        return buildDecodedTree();
    }

    private DecodedTree buildDecodedTree()
    {
        DecodedTree decodedTree = new DefaultMessageDecodedTree( selectedRuntimeEvent.getRuntimeEventChannel()
                .getName() );
        new DefaultMessageDecodedNode( decodedTree,
                                       decodedTree.getRootNode(),
                                       "Value",
                                       String.valueOf( selectedRuntimeEvent.getValue() ) );
        new DefaultMessageDecodedNode( decodedTree,
                                       decodedTree.getRootNode(),
                                       "DataType",
                                       selectedRuntimeEvent.getRuntimeEventChannel().getUnit().getDataType()
                                               .getSimpleName() );
        new DefaultMessageDecodedNode( decodedTree,
                                       decodedTree.getRootNode(),
                                       "Timestamp",
                                       formatter.formatMicros( selectedRuntimeEvent.getTimestamp() ) );
        return decodedTree;
    }

    @Override
    public String getSummary()
    {
        return selectedRuntimeEvent.getRuntimeEventChannel().getName();
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return RuntimeEventType.UNDEFINED;
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return selectedRuntimeEvent.getRuntimeEventChannel();
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return selectedRuntimeEvent.getValue();
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return selectedRuntimeEvent;
    }

}
