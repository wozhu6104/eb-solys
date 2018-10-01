/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.targetagent;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnection;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnectionFactory;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.TargetAgentConnectionType;

@Component
public class TargetAgentConnectionFactory implements TargetConnectionFactory
{
    private final TargetAgentConnectionType connectionType = new TargetAgentConnectionType();
    private UserMessageLogger userMessageLogger;

    @Override
    public ConnectionType getConnectionType()
    {
        return connectionType;
    }

    @Override
    public TargetConnection createNewConnection(ConnectionModel connectionModel)
    {
        return new TargetAgentConnectionImpl( connectionType, connectionModel, userMessageLogger );
    }

    @Reference
    public void bindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

}
