/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.api;

import com.elektrobit.ebsolys.decoder.common.converter.impl.DefaultDecodedRuntimeEventStringConverterImpl;
import com.elektrobit.ebsolys.decoder.common.converter.impl.UniqueKeyDecodedRuntimeEventStringConverterImpl;

public class DecodedRuntimeEventStringConverterFactory
{
    public static DecodedRuntimeEventStringConverter createUniqueKeyDecodedRuntimeEventStringConverter()
    {
        return new UniqueKeyDecodedRuntimeEventStringConverterImpl();
    }

    public static DecodedRuntimeEventStringConverter createDefaultDecodedRuntimeEventStringConverter()
    {
        return new DefaultDecodedRuntimeEventStringConverterImpl();
    }
}
