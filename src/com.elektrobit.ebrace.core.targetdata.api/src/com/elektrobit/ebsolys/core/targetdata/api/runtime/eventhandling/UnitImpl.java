/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
class UnitImpl<T> implements Unit<T>
{
    private final String name;
    private final Class<T> dataType;

    UnitImpl(String name, Class<T> dataType)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "name", name );
        RangeCheckUtils.assertReferenceParameterNotNull( "dataType", dataType );
        this.name = name;
        this.dataType = dataType;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Class<T> getDataType()
    {
        return dataType;
    }
}
