/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.PropertyChangedListener;

/**
 * Properties implementation of the data manager.
 * 
 * @author rage2903
 * @version 11.06
 */
public class PropertiesImpl implements Properties, Serializable
{
    private static final long serialVersionUID = -4713457541175384574L;

    private volatile int m_hashCode;

    private final Object structureObject;

    private final List<PropertyChangedListener> m_propertyChangedListeners = new CopyOnWriteArrayList<PropertyChangedListener>();

    private final HashMap<Object, Object> m_properties = new HashMap<Object, Object>();

    private final HashMap<Object, String> m_propertiesDescription = new HashMap<Object, String>();

    /**
     * Standard constructor.
     */
    public PropertiesImpl(Object structureObject)
    {
        this.structureObject = structureObject;
    }

    @Override
    public Object getValue(Object key)
    {
        return m_properties.get( key );
    }

    @Override
    public String getDescription(Object key)
    {
        return m_propertiesDescription.get( key );
    }

    /**
     * Inserts or updates the given key with the given value.
     * 
     * @param key
     *            - The key, that should be insert or be updated.
     * @param value
     *            - The value, that should be associated with the given key.
     * @param description
     *            - The description of this key-value-pair
     */
    public void put(Object key, Object value, String description)
    {
        m_propertiesDescription.put( key, description );
        Object oldValue = m_properties.put( key, value );

        // key was already there?
        if (oldValue == null)
        {
            notifyListenerAboutAdd( key );
        }
        else
        {
            // System.out.println("notifyListenerAboutChange");
            notifyListenerAboutChange( key );
        }
    }

    public void remove(Object key)
    {
        m_propertiesDescription.remove( key );
        m_properties.remove( key );
        notifyListenerAboutRemove( key );
    }

    @Override
    public Set<Object> getKeys()
    {
        return m_properties.keySet();
    }

    @Override
    public boolean addPropertyChangedListener(PropertyChangedListener listener)
    {
        // System.out.println("add listener (" + listener + ")");
        return m_propertyChangedListeners.add( listener );
    }

    @Override
    public void removePropertyChangedListener(PropertyChangedListener listener)
    {
        // System.out.println("remove listener (" + listener + ")");
        m_propertyChangedListeners.remove( listener );
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof PropertiesImpl))
        {
            return false;
        }

        PropertiesImpl props = (PropertiesImpl)o;

        return m_properties.equals( props.m_properties );
    }

    @Override
    public int hashCode()
    {
        int result = m_hashCode;

        if (result == 0)
        {
            result = 17;
            result = 31 * result + m_properties.hashCode();
            m_hashCode = result;
        }

        return result;
    }

    @Override
    public String toString()
    {
        String result = "{";
        for (Entry<Object, Object> item : m_properties.entrySet())
        {
            result += "\n  [" + item.getKey() + ": " + item.getValue() + "]";
        }
        result += "\n}";

        return result;
    }

    /**
     * Informs all listeners about an add.
     * 
     * @param key
     *            - Key of the added property.
     */
    private void notifyListenerAboutAdd(Object key)
    {
        // System.out.println("notifyListenerAboutAdd size =" + m_propertyChangedListeners.size());
        for (PropertyChangedListener l : m_propertyChangedListeners)
        {
            l.added( structureObject, key );
        }
    }

    /**
     * Informs all listeners about a remove.
     * 
     * @param key
     *            - Key of the removed property.
     */
    private void notifyListenerAboutRemove(Object key)
    {
        // System.out.println("notifyListenerAboutRemove size =" + m_propertyChangedListeners.size());
        for (PropertyChangedListener l : m_propertyChangedListeners)
        {
            l.removed( structureObject, key );
        }
    }

    /**
     * Informs all listeners about a change.
     * 
     * @param key
     *            - Key of the changed property.
     */
    private void notifyListenerAboutChange(Object key)
    {
        for (int i = 0; i < m_propertyChangedListeners.size(); i++)
        // for (PropertyChangedListener l : m_propertyChangedListeners)
        {
            PropertyChangedListener l = m_propertyChangedListeners.get( i );
            l.changed( structureObject, key );
        }
    }
}
