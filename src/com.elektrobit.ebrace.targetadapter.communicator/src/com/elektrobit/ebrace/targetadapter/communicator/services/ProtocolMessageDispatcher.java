/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.services;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.google.common.annotations.VisibleForTesting;

public interface ProtocolMessageDispatcher
{
    @VisibleForTesting
    void newProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator, DataSourceContext sourceContext);

    /**
     * Sets if received messages should be forwarded to registered services or ignored.
     * 
     * @param forward
     *            When set to false, incoming message are not forwarded (default true)
     */
    void setForwardMessages(boolean forward);
}
