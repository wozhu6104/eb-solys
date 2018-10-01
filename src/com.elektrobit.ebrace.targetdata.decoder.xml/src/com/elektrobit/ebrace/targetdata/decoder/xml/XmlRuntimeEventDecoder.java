/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.decoder.xml;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

@Component(property = {ServiceConstants.CHANNEL_NAME + "=trace.giop",
        ServiceConstants.CHANNEL_NAME + "=trace.targetagent.config"})
public class XmlRuntimeEventDecoder implements DecoderService
{
    @Override
    public DecodedRuntimeEvent decode(final RuntimeEvent<?> event)
    {

        return new DecodedRuntimeEvent()
        {
            private static final String DECODED_TREE_NAME = "GIOP";

            GenericDecodedTree theTree = null;
            XmlEventToDecodedTreeParser parser = null;

            {
                parser = new XmlEventToDecodedTreeParser();
                theTree = parser.parseXML( (String)event.getValue(), DECODED_TREE_NAME );
            }

            @Override
            public String getSummary()
            {
                return "XML Structured Event";
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
                return theTree;
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
