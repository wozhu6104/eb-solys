/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.tester;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;

public class SelectedConnectionsDisconnected extends PropertyTester
{
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        Boolean allDisconnected = null;
        if (receiver instanceof List<?>)
        {
            List<?> selected = (List<?>)receiver;
            for (Object object : selected)
            {
                if (object instanceof ConnectionModel)
                {
                    boolean disconnected = !((ConnectionModel)object).isConnected();
                    if (allDisconnected == null)
                        allDisconnected = disconnected;
                    else
                        allDisconnected = allDisconnected && disconnected;
                }
            }
        }
        return allDisconnected == null ? false : allDisconnected;
    }
}
