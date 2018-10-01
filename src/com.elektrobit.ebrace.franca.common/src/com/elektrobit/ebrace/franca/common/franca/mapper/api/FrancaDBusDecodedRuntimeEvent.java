/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.api;

import java.util.List;

import org.franca.core.franca.FModel;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecoderConstants;
import com.elektrobit.ebrace.dbus.decoder.api.SignatureSummaryBuilder;
import com.elektrobit.ebrace.franca.common.franca.mapper.impl.DecodedDBusTreeMapCleaner;
import com.elektrobit.ebrace.franca.common.franca.mapper.impl.FrancaDecodedTreeMapperImpl;
import com.elektrobit.ebrace.franca.common.franca.mapper.impl.SignatureSplitHelper;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class FrancaDBusDecodedRuntimeEvent implements DecodedRuntimeEvent
{

    private final FrancaDecodedTreeMapperImpl francaDecodedTreeMapper;
    private final DecodedRuntimeEvent decodedRuntimeEvent;
    private DecodedTree decodedTree;

    public FrancaDBusDecodedRuntimeEvent(List<FModel> francaModels, DecodedRuntimeEvent decodedRuntimeEvent)
    {
        this.decodedRuntimeEvent = decodedRuntimeEvent;
        francaDecodedTreeMapper = new FrancaDecodedTreeMapperImpl( francaModels );
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        if (decodedTree == null)
        {
            decodeTree();
        }
        return decodedTree;
    }

    private void decodeTree()
    {
        DecodedTree correctedTree = DecodedDBusTreeMapCleaner
                .copyTreeAndCorrectMaps( decodedRuntimeEvent.getDecodedTree() );

        DecodedNode signatureNode = correctedTree.getRootNode().getChildren().get( 0 );
        String interfaceName = SignatureSplitHelper.getInterfaceName( signatureNode.getName() );
        String methodName = SignatureSplitHelper.getMethodName( signatureNode.getName() );

        if (isRequest())
        {
            francaDecodedTreeMapper.mapInParameters( signatureNode, interfaceName, methodName );
        }
        else if (isResponse())
        {
            francaDecodedTreeMapper.mapOutParameters( signatureNode, interfaceName, methodName );
        }
        else if (isBroadcast())
        {
            francaDecodedTreeMapper.mapBroadcastParameters( signatureNode, interfaceName, methodName );
        }
        else
        {
            throw new IllegalArgumentException( "DBus message is not Broadcast, not Request and not Response. Couldn't map to Franca." );
        }
        decodedTree = correctedTree;
    }

    private boolean isRequest()
    {
        String messageType = getMessageType();

        if (messageType != null)
        {
            return messageType.equals( "Request" );
        }

        return false;
    }

    private String getMessageType()
    {
        String messageType = null;
        try
        {

            DecodedNode metaDataNode = decodedRuntimeEvent.getDecodedTree().getRootNode().getChildren().get( 1 );
            DecodedNode messageTypeNode = metaDataNode.getChildren().get( 0 );

            if (messageTypeNode.getName().equals( DBusDecoderConstants.MESSAGE_TYPE ))
            {
                messageType = messageTypeNode.getValue();
            }

        }
        catch (Exception e)
        {
            throw new IllegalArgumentException( "Couldn't map to Franca. No valid message type." );
        }
        return messageType;
    }

    private boolean isResponse()
    {
        String messageType = getMessageType();

        if (messageType != null)
        {
            return messageType.equals( "Response" );
        }

        return false;
    }

    private boolean isBroadcast()
    {
        String messageType = getMessageType();

        if (messageType != null)
        {
            return messageType.equals( "Broadcast" );
        }

        return false;
    }

    @Override
    public String getSummary()
    {
        return SignatureSummaryBuilder.createSummary( getDecodedTree() );
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return decodedRuntimeEvent.getRuntimeEventType();
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return decodedRuntimeEvent.getRuntimeEventChannel();
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return decodedRuntimeEvent.getRuntimeEventValue();
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return decodedRuntimeEvent.getRuntimeEvent();
    }

}
