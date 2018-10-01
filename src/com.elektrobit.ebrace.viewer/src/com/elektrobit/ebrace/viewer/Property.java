/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

/**
 * Data container for a key-value pair.
 */
public class Property<K, V>
{
    public K m_key;

    public V m_value;

    public Property(K key, V value)
    {
        m_key = key;
        m_value = value;
    }

    public K getKey()
    {
        return m_key;
    }

    public void setKey(K key)
    {
        m_key = key;
    }

    public V getValue()
    {
        return m_value;
    }

    public void setValue(V value)
    {
        m_value = value;
    }
}
