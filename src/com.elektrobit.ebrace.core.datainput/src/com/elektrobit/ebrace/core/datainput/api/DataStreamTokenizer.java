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
import java.io.IOException;

public abstract class DataStreamTokenizer
{

    protected boolean hasNextMessage = true;
    protected BufferedInputStream stream;

    public void setDataStream(DataStream stream) throws Exception
    {
        this.stream = stream.getInputStream();
    }

    public abstract byte[] readNextMessage() throws IOException;

    public boolean hasNextMessage()
    {
        return hasNextMessage;
    }

    public abstract String getId();
}
