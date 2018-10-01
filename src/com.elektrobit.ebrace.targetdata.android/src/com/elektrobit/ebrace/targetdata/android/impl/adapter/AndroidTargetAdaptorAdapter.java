/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.adapter;

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

public class AndroidTargetAdaptorAdapter implements TargetAdapter
{
    @SuppressWarnings("unused")
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final AndroidTargetAdaptorDataProcessor processor;

    public AndroidTargetAdaptorAdapter(RuntimeEventAcceptor runtimeEventAcceptor, StructureAcceptor structureAcceptor,
            ComRelationAcceptor comRelationAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.processor = new AndroidTargetAdaptorDataProcessor( runtimeEventAcceptor );
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_ANDROID_PLUGIN ))
        {
            // @ebrace:start - Edit class only with these @ebrace comments, do not remove the comments.

            try
            {
                processor.processMessage( payload, timestamp );
            }
            catch (InvalidProtocolBufferException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // @ebrace:end
        }
    }

    @Override
    public void dispose()
    {
        // @ebrace:start - Edit class only with these @ebrace comments, do not remove the comments.

        /* Cleanup here. */

        // @ebrace:end
    }
}
