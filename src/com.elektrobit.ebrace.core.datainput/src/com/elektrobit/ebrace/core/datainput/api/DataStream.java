/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.api;

import java.io.BufferedInputStream;

public interface DataStream
{

    public String getType();

    void open() throws Exception;

    public BufferedInputStream getInputStream() throws Exception;

    public void close() throws Exception;

    public String getImplementationDetails();
}
