/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces;

public class ValueOfArgumentMissingException extends Exception
{
    private static final long serialVersionUID = -5838264886236404531L;

    public ValueOfArgumentMissingException(String string)
    {
        super( string );
    }
}
