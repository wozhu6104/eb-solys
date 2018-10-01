/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.common;

public class UseCaseFactoryInstance
{
    private static UseCaseFactoryService INSTANCE = null;

    public static UseCaseFactoryService get()
    {
        return INSTANCE;
    }

    public static void register(UseCaseFactoryService service)
    {
        INSTANCE = service;
    }

    public static void unregister()
    {
        INSTANCE = null;
    }

}
