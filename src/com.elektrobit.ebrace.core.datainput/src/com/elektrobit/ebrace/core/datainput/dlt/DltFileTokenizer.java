/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.dlt;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;

@Component
public class DltFileTokenizer extends DataStreamTokenizer
{
    public DltFileTokenizer()
    {
    }

    @Override
    public byte[] readNextMessage() throws IOException
    {
        byte[] buffer = null;
        DltMessage nextMessage = new DltMessageHelper().getNextMessageAfterStorageHeader( stream );
        if (nextMessage != null)
        {
            buffer = nextMessage.serialize();
        }
        return buffer;
    }

    @Override
    public String getId()
    {
        return "dlt-file-header";
    }

}
