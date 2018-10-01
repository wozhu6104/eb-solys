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

public class NameAndContents
{
    private final String name;
    private final String contents;

    public NameAndContents(String name, String contents)
    {
        this.name = name;
        this.contents = contents;
    }

    public String getName()
    {
        return name;
    }

    public String getContents()
    {
        return contents;
    }

    @Override
    public String toString()
    {
        return name + "[" + contents + "]";
    }
}
