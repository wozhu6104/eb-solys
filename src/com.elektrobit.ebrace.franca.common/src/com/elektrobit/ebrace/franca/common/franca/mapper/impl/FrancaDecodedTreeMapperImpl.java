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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.franca.core.franca.FArgument;
import org.franca.core.franca.FBroadcast;
import org.franca.core.franca.FEnumerationType;
import org.franca.core.franca.FInterface;
import org.franca.core.franca.FMethod;
import org.franca.core.franca.FModel;
import org.franca.core.franca.FTypeRef;

import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDecodedTreeMapper;
import com.elektrobit.ebrace.franca.common.impl.FrancaModelHelper;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;

public class FrancaDecodedTreeMapperImpl implements FrancaDecodedTreeMapper
{
    private final FrancaFTypeHandler fTypeHandler = new FrancaFTypeHandler();
    private final List<FModel> francaModels;
    private FBroadcast broadCast;
    private FMethod fMethod;
    private FInterface fInterface;

    public FrancaDecodedTreeMapperImpl(List<FModel> francaModels)
    {
        this.francaModels = francaModels;
    }

    @Override
    public void mapOutParameters(DecodedNode node, String interfaceName, String methodName)
    {
        initializeInterfaceAndMethod( interfaceName, methodName );
        if (fMethod != null)
        {
            Iterator<DecodedNode> childIterator = node.getChildren().iterator();

            handleGeniviErrorParam( childIterator );

            mapParametersForDecodedNode( fMethod.getOutArgs(), childIterator );
        }
    }

    private void initializeInterfaceAndMethod(String interfaceName, String methodName)
    {
        fInterface = findInterface( interfaceName );
        if (fInterface != null)
        {
            fMethod = findFMethod( methodName );
        }
    }

    private FInterface findInterface(String fInterface)
    {
        if (isInterfaceValid( fInterface ))
        {
            for (FModel nextModel : francaModels)
            {
                FInterface foundFrancaInterface = FrancaModelHelper.getFrancaInterface( nextModel, fInterface );
                if (foundFrancaInterface != null)
                {
                    return foundFrancaInterface;
                }
            }
        }

        return null;
    }

    private boolean isInterfaceValid(String fInterface)
    {
        return fInterface != null && !fInterface.isEmpty();
    }

    private FMethod findFMethod(String methodName)
    {
        return FrancaModelHelper.getFmethodFromInterface( fInterface, methodName );
    }

    private void handleGeniviErrorParam(Iterator<DecodedNode> childrenIterator)
    {
        FEnumerationType errorEnum = fMethod != null ? fMethod.getErrors() : null;
        if (!(errorEnum == null))
        {
            DecodedNode child = childrenIterator.next();
            child.setName( "result" );
            int errorNameIndex = Integer.valueOf( child.getValue() );
            child.setValue( errorEnum.getEnumerators().get( errorNameIndex ).getName() );
        }
    }

    private void mapParametersForDecodedNode(EList<FArgument> methodArgs, Iterator<DecodedNode> childIterator)
    {
        int methodArgIndex = 0;

        while (childIterator.hasNext())
        {
            DecodedNode child = childIterator.next();

            if (methodArgIndex < methodArgs.size())
            {
                FArgument currentFArgument = methodArgs.get( methodArgIndex );
                String fArgumentName = currentFArgument.getName();
                child.setName( fArgumentName );

                FTypeRef fArgumentType = currentFArgument.getType();
                if (child.getChildren().isEmpty())
                {
                    fTypeHandler.handlePrimitiveType( fArgumentType, child, fArgumentName, currentFArgument );
                }
                else
                {
                    if (isDerived( currentFArgument.getType() ))
                    {
                        fTypeHandler.handleArgssForDerivedFType( child,
                                                                 fArgumentType.getDerived(),
                                                                 fArgumentName,
                                                                 currentFArgument );
                    }
                }
            }
            methodArgIndex++;
        }

    }

    private boolean isDerived(FTypeRef fTypeRef)
    {
        return fTypeRef.getDerived() != null;
    }

    @Override
    public void mapBroadcastParameters(DecodedNode node, String interfaceName, String broadcastName)
    {
        initializeInterfaceAndBroadcast( interfaceName, broadcastName );
        if (broadCast != null)
        {
            Iterator<DecodedNode> childIterator = node.getChildren().iterator();

            mapParametersForDecodedNode( broadCast.getOutArgs(), childIterator );
        }
    }

    private void initializeInterfaceAndBroadcast(String interfaceName, String methodName)
    {
        fInterface = findInterface( interfaceName );
        if (fInterface != null)
        {
            broadCast = findFBroadcast( methodName );
        }
    }

    private FBroadcast findFBroadcast(String braodcastName)
    {
        return FrancaModelHelper.getFBroadcastFromInterface( fInterface, braodcastName );
    }

    @Override
    public void mapInParameters(DecodedNode node, String interfaceName, String methodName)
    {
        initializeInterfaceAndMethod( interfaceName, methodName );
        if (fMethod != null)
        {
            Iterator<DecodedNode> childIterator = node.getChildren().iterator();

            mapParametersForDecodedNode( fMethod.getInArgs(), childIterator );
        }
    }

}
