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

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverterFactory;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(property = {ServiceConstants.CHANNEL_NAME + "=ipc.websockets",
        ServiceConstants.CHANNEL_NAME + "=ipc.json"}, immediate = true)
public class JsonDecoder implements DecoderService
{
    private final DecodedRuntimeEventStringConverter converter = DecodedRuntimeEventStringConverterFactory
            .createDefaultDecodedRuntimeEventStringConverter();

    @Override
    public DecodedRuntimeEvent decode(final RuntimeEvent<?> event)
    {
        String jsonInputString = (String)event.getValue();
        DecodedTree tree = null;
        try
        {
            tree = converter.convertFromString( jsonInputString );
        }
        catch (Exception e)
        {
            log.info( "Input string is not a valid json format " + jsonInputString );
            return null;
        }
        return new DecodedJsonEvent( tree, "JavaScript Object Notation format decoder", event );
    }

}
