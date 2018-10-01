/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

public class EventMock
{
    private final String value_1;
    private final String value_2;

    public EventMock(String _value_1, String _value_2)
    {
        value_1 = _value_1;
        value_2 = _value_2;
    }

    public String getValue_1()
    {
        return value_1;
    }

    public String getValue_2()
    {
        return value_2;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value_1 == null) ? 0 : value_1.hashCode());
        result = prime * result + ((value_2 == null) ? 0 : value_2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventMock other = (EventMock)obj;
        if (value_1 == null)
        {
            if (other.value_1 != null)
                return false;
        }
        else if (!value_1.equals( other.value_1 ))
            return false;
        if (value_2 == null)
        {
            if (other.value_2 != null)
                return false;
        }
        else if (!value_2.equals( other.value_2 ))
            return false;
        return true;
    }
}
