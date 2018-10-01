/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.propertySupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Stores all property change listener and notifies them for changes. */
@Deprecated
public class EBRacePropertyChangeSupport
{
    /** The property change support which we use for all properties and all events to notify the views/editors. */
    public static final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( "" );

    /**
     * Adds a new property change listener to the list of listeners which is managed by the property change support.
     * 
     * @param listenerToAdd
     *            the listener which has to be added to the list of listeners of the property change support.
     */
    public static void addPropertyChangeListener(PropertyChangeListener listenerToAdd)
    {
        propertyChangeSupport.addPropertyChangeListener( listenerToAdd );

    }

    /**
     * Removes a new property change listener to the list of listeners which is managed by the property change support.
     * 
     * @param listenerToRemove
     *            the listener which has to be removed from list of listeners of the property change support.
     */
    public static void removePropertyChangeListener(PropertyChangeListener listenerToRemove)
    {
        propertyChangeSupport.removePropertyChangeListener( listenerToRemove );
    }

    /**
     * Fires a new property change event with the given parameters.
     * 
     * @param source
     *            the object for which we are firing the event. The object may be null.
     * @param property
     *            the property we have changed.
     * @param oldValue
     *            the old value of the property.
     * @param newValue
     *            the new value of the property.
     */
    public static void firePropertyChangedEvent(Object source, String property, String oldValue, String newValue)
    {
        PropertyChangeEvent newEvent = new PropertyChangeEvent( source, property, oldValue, newValue );
        propertyChangeSupport.firePropertyChange( newEvent );
    }

}
