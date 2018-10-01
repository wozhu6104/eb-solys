/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.parser;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;
import com.elektrobit.ebrace.targetdata.dlt.newapi.DltStreamMessageService;

@Component
public class DltStreamParser implements DataStreamParser
{
    private DltStreamMessageService dltStreamMessageService;

    public DltStreamParser()
    {
    }

    @Override
    public String parse(byte[] message)
    {
        return dltStreamMessageService.createEvent( message );
    }

    @Override
    public String getId()
    {
        return "Dlt Stream Parser";
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
