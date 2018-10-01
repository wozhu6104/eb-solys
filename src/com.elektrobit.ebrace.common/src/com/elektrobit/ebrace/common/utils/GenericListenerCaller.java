/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 
 * @author mamo8543
 *
 *         Can be use by classes that need to implement listener mechanism, T is type of a class specific listener.
 *
 * @param <T>
 */
public class GenericListenerCaller<T>
{
    private final Set<T> listeners = new CopyOnWriteArraySet<T>();

    public interface Notifier<T>
    {
        public void notify(T listener);
    }

    public void add(T listener)
    {
        listeners.add( listener );
    }

    public void addAll(Set<T> set)
    {
        listeners.addAll( set );
    }

    public void remove(T listener)
    {
        listeners.remove( listener );
    }

    /**
     * Calls desired method on all registered listeners (with Java 8 can be called as lambda expression (e.g.
     * notifyListeners( l -> l.methodName() )
     * 
     * @param notifier
     */
    public void notifyListeners(Notifier<T> notifier)
    {
        for (T listener : listeners)
        {
            try
            {
                notifier.notify( listener );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public int size()
    {
        return listeners.size();
    }

    public void clear()
    {
        listeners.clear();
    }

}
