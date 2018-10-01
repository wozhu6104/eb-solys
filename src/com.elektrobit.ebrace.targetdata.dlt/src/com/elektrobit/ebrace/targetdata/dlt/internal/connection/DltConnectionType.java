/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

@Component
public class DltConnectionType implements ConnectionType
{
    public static final String EXTENSION = "dlts";

    @Override
    public String getName()
    {
        return "DLT daemon";
    }

    @Override
    public String getExtension()
    {
        return EXTENSION;
    }

    @Override
    public int getDefaultPort()
    {
        return 3490;
    }

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
        if (getExtension() == null)
        {
            if (other.getExtension() != null)
            {
                return false;
            }
        }
        else if (!getExtension().equals( other.getExtension() ))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return getExtension().hashCode();
    }
}
