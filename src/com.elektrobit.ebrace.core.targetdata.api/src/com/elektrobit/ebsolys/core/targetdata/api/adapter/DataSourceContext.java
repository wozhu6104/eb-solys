/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.adapter;

import lombok.Data;

@Data
public class DataSourceContext
{
    public enum SOURCE_TYPE {
        FILE, CONNECTION
    };

    private final SOURCE_TYPE sourceType;// TODO rename to channel prefix?
    private final String sourceName;
}
