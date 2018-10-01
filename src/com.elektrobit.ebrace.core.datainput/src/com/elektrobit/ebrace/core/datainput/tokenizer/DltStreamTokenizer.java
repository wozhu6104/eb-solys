/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.tokenizer;

import java.io.IOException;

import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;
import com.elektrobit.ebrace.targetdata.dlt.newapi.DltStreamMessageService;

public class DltStreamTokenizer extends DataStreamTokenizer
{
    private DltStreamMessageService dltStreamMessageService;

    public DltStreamTokenizer()
    {
    }

    @Override
    public byte[] readNextMessage() throws IOException
    {
        return dltStreamMessageService.tokenizeNextMessage( stream );
    }

    @Override
    public String getId()
    {
        return "Dlt Stream Tokenizer";
    }

    @Reference
    public void bindDltStreamMessageService(DltStreamMessageService dltStreamMessageService)
    {
        this.dltStreamMessageService = dltStreamMessageService;
    }

    public void unbindDltStreamMessageService(DltStreamMessageService dltStreamMessageService)
    {
        this.dltStreamMessageService = null;
    }
}
