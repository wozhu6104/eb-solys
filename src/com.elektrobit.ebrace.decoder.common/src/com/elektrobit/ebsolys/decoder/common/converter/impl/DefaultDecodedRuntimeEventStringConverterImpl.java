/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.converter.impl;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;

public class DefaultDecodedRuntimeEventStringConverterImpl implements DecodedRuntimeEventStringConverter
{
    private final DecodedTreeJsonConverter jsonConverter = new DecodedTreeJsonConverter();

    @Override
    public DecodedTree convertFromString(String decodedTreeAsString)
    {
        return jsonConverter.getDecodedTreeFromString( decodedTreeAsString );
    }

    @Override
    public String convertToString(DecodedTree decodedTree)
    {
        return jsonConverter.getStringRepresentation( decodedTree );
    }
}
