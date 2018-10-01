/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.impl;

public class SignatureSplitHelper
{
    public static String getMethodName(String signature)
    {
        String interfaceName = null;

        int endIndex = signature.lastIndexOf( "." );
        if (endIndex != -1)
        {
            interfaceName = signature.substring( endIndex + 1, signature.length() );
        }
        return interfaceName;
    }

    public static String getInterfaceName(String signature)
    {
        String interfaceName = null;

        int endIndex = signature.lastIndexOf( "." );
        if (endIndex != -1)
        {
            interfaceName = signature.substring( 0, endIndex );
        }
        return interfaceName;
    }

}
