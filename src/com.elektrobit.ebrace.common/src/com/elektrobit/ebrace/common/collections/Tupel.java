/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.collections;

public class Tupel<T>
{
    private final T _value1;
    private final T _value2;

    public Tupel(final T value1, final T value2)
    {
        _value1 = value1;
        _value2 = value2;
    }

    public T getValue1()
    {
        return _value1;
    }

    public T getValue2()
    {
        return _value2;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_value1 == null) ? 0 : _value1.hashCode());
        result = prime * result + ((_value2 == null) ? 0 : _value2.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Tupel other = (Tupel)obj;
        if (_value1 == null)
        {
            if (other._value1 != null)
            {
                return false;
            }
        }
        else if (!_value1.equals( other._value1 ))
        {
            return false;
        }
        if (_value2 == null)
        {
            if (other._value2 != null)
            {
                return false;
            }
        }
        else if (!_value2.equals( other._value2 ))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Tupel [_value1=" + _value1 + ", _value2=" + _value2 + "]";
    }

}
