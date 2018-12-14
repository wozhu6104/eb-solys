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

public class DltMessageParseException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public DltMessageParseException()
    {
    }

    public DltMessageParseException(String message)
    {
        super( message );
    }
}
