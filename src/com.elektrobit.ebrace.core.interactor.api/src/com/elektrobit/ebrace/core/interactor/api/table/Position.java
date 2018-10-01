/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.table;

public class Position
{
    public static Position FULL_SELECTION = new Position( 0, Integer.MAX_VALUE );

    private final int start;
    private final int length;

    public Position(int _start, int _length)
    {
        start = _start;
        length = _length;
    }

    public int getStart()
    {
        return start;
    }

    public int getLength()
    {
        return length;
    }
}
