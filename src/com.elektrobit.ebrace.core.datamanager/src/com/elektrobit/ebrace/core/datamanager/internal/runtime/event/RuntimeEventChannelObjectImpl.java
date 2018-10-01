/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.IdNumberGenerator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public final class RuntimeEventChannelObjectImpl<T> implements RuntimeEventChannel<T>
{
    private String runtimeEventChannelName;
    private final String runtimeEventChannelDescription;
    private Unit<T> unit;
    private List<String> valueColumns;
    private Map<String, Object> parameters = new HashMap<>();

    public RuntimeEventChannelObjectImpl(final String runtimeEventChannelName,
            final String runtimeEventChannelDescription, Unit<T> unit)
    {
        this( IdNumberGenerator.getNextId( "RuntimeEventChannelId" ),
                runtimeEventChannelName,
                runtimeEventChannelDescription,
                unit,
                Collections.emptyList() );
    }

    public RuntimeEventChannelObjectImpl(final String runtimeEventChannelName,
            final String runtimeEventChannelDescription, Unit<T> unit, List<String> valueColumns)
    {
        this( IdNumberGenerator.getNextId( "RuntimeEventChannelId" ),
                runtimeEventChannelName,
                runtimeEventChannelDescription,
                unit,
                valueColumns );
    }

    public RuntimeEventChannelObjectImpl(final long runtimeEventChannelId, final String runtimeEventChannelName,
            final String runtimeEventChannelDescription, Unit<T> unit)
    {
        this( runtimeEventChannelId,
                runtimeEventChannelName,
                runtimeEventChannelDescription,
                unit,
                Collections.emptyList() );
    }

    public RuntimeEventChannelObjectImpl(final long runtimeEventChannelId, final String runtimeEventChannelName,
            final String runtimeEventChannelDescription, Unit<T> unit, List<String> valueColumns)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventChannelName", runtimeEventChannelName );
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventChannelDescription",
                                                         runtimeEventChannelDescription );
        RangeCheckUtils.assertReferenceParameterNotNull( "unit", unit );
        RangeCheckUtils.assertReferenceParameterNotNull( "valueColumns", valueColumns );

        this.runtimeEventChannelName = runtimeEventChannelName;
        this.runtimeEventChannelDescription = runtimeEventChannelDescription;
        this.unit = unit;
        this.valueColumns = valueColumns;
    }

    @Override
    public String getName()
    {
        return runtimeEventChannelName;
    }

    public void setName(String newName)
    {
        runtimeEventChannelName = newName;
    }

    @Override
    public String getDescription()
    {
        return runtimeEventChannelDescription;
    }

    @Override
    public Unit<T> getUnit()
    {
        return unit;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((runtimeEventChannelName == null) ? 0 : runtimeEventChannelName.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
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
        if (getClass() != obj.getClass())
        {
            return false;
        }
        RuntimeEventChannelObjectImpl other = (RuntimeEventChannelObjectImpl)obj;
        if (runtimeEventChannelName == null)
        {
            if (other.runtimeEventChannelName != null)
            {
                return false;
            }
        }
        else if (!runtimeEventChannelName.equals( other.runtimeEventChannelName ))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "RuntimeEventChannelObjectImpl [ runtimeEventChannelName=" + runtimeEventChannelName
                + ", runtimeEventChannelDescription=" + runtimeEventChannelDescription
                + ", runtimeEventChannelClassType=" + "]\n";
    }

    @Override
    public List<String> getValueColumnNames()
    {
        return valueColumns;
    }

    public void setParameter(String key, Object value)
    {
        parameters.put( key, value );
    }

    @Override
    public Object getParameter(String key)
    {
        return parameters.get( key );
    }

    public void setParameters(Map<String, Object> parameters)
    {
        this.parameters = parameters;
    }

    @Override
    public List<String> getParameterNames()
    {
        return new ArrayList<String>( parameters.keySet() );
    }

}
