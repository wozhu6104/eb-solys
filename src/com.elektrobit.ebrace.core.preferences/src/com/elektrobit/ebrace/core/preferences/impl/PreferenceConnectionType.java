/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

import lombok.Data;

@Data
public class PreferenceConnectionType implements ConnectionType
{
    private final String name;
    private final String extension;
    private final int defaultPort;

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof ConnectionType))
        {
            return false;
        }
        ConnectionType other = (ConnectionType)obj;
        if (extension == null)
        {
            if (other.getExtension() != null)
            {
                return false;
            }
        }
        else if (!extension.equals( other.getExtension() ))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return extension.hashCode();
    }

}
