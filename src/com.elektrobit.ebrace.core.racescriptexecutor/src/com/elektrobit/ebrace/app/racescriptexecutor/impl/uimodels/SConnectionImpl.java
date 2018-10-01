/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebsolys.script.external.SConnection;

import lombok.Getter;

public class SConnectionImpl implements SConnection
{
    @Getter
    private final ConnectionModel connectionModel;

    public SConnectionImpl(ConnectionModel connectionModel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "connectionModel", connectionModel );
        this.connectionModel = connectionModel;
    }

    @Override
    public String getName()
    {
        return connectionModel.getName();
    }

    @Override
    public String getHost()
    {
        return connectionModel.getHost();
    }

    @Override
    public int getPort()
    {
        return connectionModel.getPort();
    }

}
