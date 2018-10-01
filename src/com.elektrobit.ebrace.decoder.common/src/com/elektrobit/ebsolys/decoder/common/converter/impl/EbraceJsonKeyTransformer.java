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

// Json discourages duplicate keys on the same level. As a consequence,
// addProperty replaces existing values for the same key.
// Therefore a unique id is added to the key. This will be removed when reading a json string.
public class EbraceJsonKeyTransformer implements UniqueKeyTransformer
{

    private int uniqueDigit = 0;

    @Override
    public String transform(String key)
    {
        return addUniqueDigit( key );
    }

    @Override
    public String retransform(String key)
    {
        return key.substring( 0, key.lastIndexOf( '_' ) );
    }

    private String addUniqueDigit(String key)
    {
        return key + "_" + uniqueDigit++;
    }

}
