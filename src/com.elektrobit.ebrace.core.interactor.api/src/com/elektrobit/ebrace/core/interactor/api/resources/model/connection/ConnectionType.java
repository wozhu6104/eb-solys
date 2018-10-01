/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model.connection;

public interface ConnectionType
{
    /**
     * 
     * @return name, e.g. "DLT Daemon" or "EB solys agent"
     */
    public String getName();

    /**
     * 
     * @return extention, e.g. "bin" or "dlt"
     */
    public String getExtension();

    public int getDefaultPort();

}
