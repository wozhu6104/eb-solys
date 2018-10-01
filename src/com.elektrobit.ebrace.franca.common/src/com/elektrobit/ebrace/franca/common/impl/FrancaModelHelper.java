/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.impl;

import java.util.List;

import org.franca.core.franca.FBroadcast;
import org.franca.core.franca.FInterface;
import org.franca.core.franca.FMethod;
import org.franca.core.franca.FModel;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class FrancaModelHelper
{
    public static FModel getFrancaModelOfFrancaInterface(List<FModel> fModels, String interfaceFullScopeName)
    {
        RangeCheckUtils.assertListIsNotEmpty( "fModels", fModels );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "interfaceFullScopeName", interfaceFullScopeName );

        for (FModel nextFModel : fModels)
        {
            if (getFrancaInterface( nextFModel, interfaceFullScopeName ) != null)
            {
                return nextFModel;
            }
        }

        return null;
    }

    public static FInterface getFrancaInterface(FModel fModel, String interfaceFullScopeName)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "fModel", fModel );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "interfaceFullScopeName", interfaceFullScopeName );

        int index = interfaceFullScopeName.lastIndexOf( "." );
        if (index > -1)
        {
            String packagePrefix = interfaceFullScopeName.substring( 0, index );
            String interfaceName = interfaceFullScopeName.substring( index + 1 );
            String packageName = fModel.getName().toLowerCase();
            for (FInterface fInterface : fModel.getInterfaces())
            {
                if (packagePrefix.toLowerCase().equals( packageName )
                        && interfaceName.toLowerCase().equals( fInterface.getName().toLowerCase() ))
                {
                    return fInterface;
                }
            }
        }
        return null;
    }

    public static FMethod getFmethodFromInterface(FInterface fInterface, String francaMethodName)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "fInterface", fInterface );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "francaMethodName", francaMethodName );

        for (FMethod fMethod : fInterface.getMethods())
        {
            if (fMethod.getName().toLowerCase().equals( francaMethodName.toLowerCase() ))
            {
                return fMethod;
            }
        }
        return null;
    }

    public static FBroadcast getFBroadcastFromInterface(FInterface fInterface, String broadcastName)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "fInterface", fInterface );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "broadcastName", broadcastName );

        for (FBroadcast fMethod : fInterface.getBroadcasts())
        {
            if (fMethod.getName().toLowerCase().equals( broadcastName.toLowerCase() ))
            {
                return fMethod;
            }
        }
        return null;
    }
}
