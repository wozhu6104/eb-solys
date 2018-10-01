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
import com.elektrobit.ebrace.decoder.protobuf.model.DefaultProtobufDecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

@Component(property = {ServiceConstants.CLAZZ_TYPE + "="
        + "com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue"})
public class ProtobufDecoderService implements DecoderService
{
    @Override
    public DecodedRuntimeEvent decode(RuntimeEvent<?> event)
    {
        return new DefaultProtobufDecodedRuntimeEvent( event );
    }
}
