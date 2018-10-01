/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.script;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class RaceScriptMethod
{
    private final String methodName;
    private final String description;
    private final String scriptName;
    private final Method method;

    public String getLabelText()
    {
        if (description == null || description.isEmpty())
            return scriptName + "." + methodName;
        return description;
    }
}
