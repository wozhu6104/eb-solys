/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.stfevents;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverterFactory;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;

@Component(property = {ServiceConstants.CHANNEL_NAME + "=trace.CTracer-Messages"})
public class STFEventDecoder implements DecoderService
{
    private final DecodedRuntimeEventStringConverter converter = DecodedRuntimeEventStringConverterFactory
            .createDefaultDecodedRuntimeEventStringConverter();

    private final DecoderService stringDefaultDecoder = DecoderServiceManagerImpl.getInstance()
            .getDecoderForClassName( String.class.getName() );

    @Override
    public DecodedRuntimeEvent decode(final RuntimeEvent<?> event)
    {
        return new DecodedRuntimeEvent()
        {
            private final String STF_EVENT_MARKER_PREFIX = "STF_MARKER:";
            private final DecodedTree decodedTree;
            private final boolean isStfEventMarker;

            {
                final String eventStr = event.getValue().toString();
                isStfEventMarker = eventStr.startsWith( STF_EVENT_MARKER_PREFIX );
                if (isStfEventMarker)
                {
                    decodedTree = converter.convertFromString( eventStr.substring( STF_EVENT_MARKER_PREFIX.length() ) );
                }
                else
                {
                    decodedTree = stringDefaultDecoder.decode( event ).getDecodedTree();
                }
            }

            @Override
            public String getSummary()
            {
                if (isStfEventMarker)
                {
                    final List<DecodedNode> decodedNodeList = decodedTree.getRootNode().getChildren();
                    return decodedNodeList.get( 0 ).getValue() + " -> " + decodedNodeList.get( 1 ).getValue();
                }
                else
                {
                    return "Summary: " + event.getValue().toString();
                }
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
            public DecodedTree getDecodedTree()
            {
                return decodedTree;
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
        };
    }

}
