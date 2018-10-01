/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.datamanager;

import java.util.Collections;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventChannelMock<T> implements RuntimeEventChannel<T>
{
    private final String name;
    private Unit<T> unit;

    public RuntimeEventChannelMock(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name.toString();
    }

    @Override
    public String getDescription()
    {
        return name.toString();
    }

    @Override
    public Unit<T> getUnit()
    {
        return unit;
    }

    @SuppressWarnings("unchecked")
    public void setUnit(Unit<?> unit)
    {
        this.unit = (Unit<T>)unit;
    }

    @Override
    public List<String> getValueColumnNames()
    {
        return Collections.emptyList();
    }

    @Override
    public Object getParameter(String key)
    {
        return null;
    }

    @Override
    public List<String> getParameterNames()
    {
        return Collections.emptyList();
    }

}
