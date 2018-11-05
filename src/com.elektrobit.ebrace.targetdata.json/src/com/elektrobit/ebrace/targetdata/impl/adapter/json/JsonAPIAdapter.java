/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.adapter.json;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.protobuf.messagedefinitions.JsonAPI.JsonAPIMsg;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class JsonAPIAdapter implements TargetAdapter
{
    private final JsonEventHandler jsonEventHandler;

    public JsonAPIAdapter(RuntimeEventAcceptor runtimeEventAcceptor, StructureAcceptor structureAcceptor,
            ComRelationAcceptor comRelationAcceptor, JsonEventHandler jsonEventHandler,
            DataSourceContext dataSourceContext)
    {
        this.jsonEventHandler = jsonEventHandler;
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_JSONAPI_PLUGIN ))
        {
            try
            {
                JsonAPIMsg msg = JsonAPIMsg.parseFrom( payload );
                String content = msg.getContent();
                try
                {
                    jsonEventHandler.handle( content );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (InvalidProtocolBufferException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
