/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.decoder.json;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverterFactory;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

@Component(property = ServiceConstants.CLAZZ_TYPE
        + "=com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent", immediate = true)
public class JsonEventDecoderImpl implements DecoderService
{
    private final DecodedRuntimeEventStringConverter converter = DecodedRuntimeEventStringConverterFactory
            .createDefaultDecodedRuntimeEventStringConverter();
    private DecodedTree tree;

    @Override
    public DecodedRuntimeEvent decode(RuntimeEvent<?> event)
    {
        if (event.getValue() instanceof JsonEvent)
        {
            JsonEvent jsonEvent = (JsonEvent)event.getValue();
            tree = converter.convertFromString( jsonEvent.toString() );
        }
        return new DecodedRuntimeEvent()
        {

            @Override
            public String getSummary()
            {
                if (event.getSummary().length() <= 100)
                {
                    return event.getSummary();
                }
                else
                {
                    return event.getSummary().substring( 0, 100 ).concat( "..." );
                }

            }

            @Override
            public Object getRuntimeEventValue()
            {
                return RuntimeEventType.UNDEFINED;
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
            public RuntimeEvent<?> getRuntimeEvent()
            {
                return event;
            }

            @Override
            public DecodedTree getDecodedTree()
            {
                return tree;
            }
        };
    }

}
