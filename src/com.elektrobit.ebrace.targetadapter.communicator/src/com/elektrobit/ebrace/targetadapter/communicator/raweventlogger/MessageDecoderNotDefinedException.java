/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.raweventlogger;

public class MessageDecoderNotDefinedException extends Exception
{
    private static final long serialVersionUID = 7792278709772238337L;

    public MessageDecoderNotDefinedException(String string)
    {
        super( string );
    }
}
