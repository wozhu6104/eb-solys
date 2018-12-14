/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.dlt;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;

@Component
public class DltStreamParser implements DataStreamParser
{
    public DltStreamParser()
    {
    }

    @Override
    public String parse(byte[] message)
    {
        return new DltMessageHelper().createEvent( message );
    }

    @Override
    public String getId()
    {
        return "dlt";
    }

}
